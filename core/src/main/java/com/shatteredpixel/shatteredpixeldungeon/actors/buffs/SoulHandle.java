/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Barricade;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Ghoul;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RipperDemon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wraith;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.YogDzewa;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;

public class SoulHandle extends Buff implements ActionIndicator.Action {

	{
		type = buffType.POSITIVE;
		revivePersists = true;
	}

	public float soulAmount = 0;
	public int cooldown;

	private static final int MAX_COOLDOWN = 5;

	@Override
	public int icon() {
		return BuffIndicator.POISON;
	}

	@Override
	public float iconFadePercent() {
		return GameMath.gate(0, (float)cooldown/MAX_COOLDOWN, 1);
	}

	@Override
	public String iconTextDisplay() {
		if (cooldown > 0){
			return Integer.toString(cooldown);
		} else {
			return "";
		}
	}

	@Override
	public boolean act() {
		if (cooldown > 0){
			cooldown--;
			if (cooldown <= 0 && soulAmount >= 3){
				ActionIndicator.setAction(this);
				cooldown = 0;
			}
			BuffIndicator.refreshHero();
		}

		spend(TICK);
		return true;
	}

	@Override
	public String desc() {
		String desc = Messages.get(this, "desc", (int) soulAmount);
		if (cooldown > 0){
			desc += "\n\n" + Messages.get(this, "desc_cooldown", cooldown);
		}
		return desc;
	}

	public static String SOUL = "soulamount";
	public static String COOLDOWN = "cooldown";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(SOUL, soulAmount);
		bundle.put(COOLDOWN, cooldown);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		soulAmount = bundle.getFloat(SOUL);
		cooldown = bundle.getInt(COOLDOWN);

		if (soulAmount >= 3 && cooldown == 0){
			ActionIndicator.setAction(this);
		}
	}

	public void gainEnergy(Mob enemy){
		if (target == null) return;

		if (!Regeneration.regenOn()){
			return; //to prevent farming boss minions
		}

		float energyGain = 1;

		//bosses and minibosses give extra energy, certain enemies give half, otherwise give 1
		if (Char.hasProp(enemy, Char.Property.BOSS))            energyGain = 5;
		else if (Char.hasProp(enemy, Char.Property.MINIBOSS))   energyGain = 3;
		else if (enemy instanceof Ghoul)                        energyGain = 0.5f;
		else if (enemy instanceof RipperDemon)                  energyGain = 0.5f;
		else if (enemy instanceof YogDzewa.Larva)               energyGain = 0.5f;
		else if (enemy instanceof Wraith)                       energyGain = 0.5f;
		else if (enemy instanceof Barricade)                    energyGain = 0;

		soulAmount += energyGain;
		soulAmount = Math.min(soulAmount, soulCap());
		if (((Hero)target).hasTalent(Talent.IMMEDIATE_USE)){
			Buff.prolong(target, ImmediateUseTracker.class, 1f);
		}

		if (soulAmount >= 3 && cooldown == 0){
			ActionIndicator.setAction(this);
		}
		BuffIndicator.refreshHero();
	}

	//10 at base, 15/20/25 at +1/+2/+3
	public int soulCap(){
		return 10 + 5 * ((Hero)target).pointsInTalent(Talent.OVERFLOW);
	}

	public int extraSoul(){
		return (int) soulAmount - 10;
	}

	public void abilityUsed(){
		soulAmount -= 3;
		soulAmount = Math.min(soulAmount, soulCap());

		ImmediateUseTracker tracker = target.buff(ImmediateUseTracker.class);
		if (tracker != null){
			cooldown -= 2 * ((Hero) target).pointsInTalent(Talent.IMMEDIATE_USE);
			if (((Hero) target).pointsInTalent(Talent.IMMEDIATE_USE) >= 3) {
				target.next();
			} else ((Hero) target).spendAndNext(1f);
			tracker.detach();
		} else ((Hero) target).spendAndNext(1f);
		cooldown += MAX_COOLDOWN + 1;

		if (cooldown > 0 || soulAmount < 3){
			ActionIndicator.clearAction(this);
		} else {
			ActionIndicator.refresh();
		}
		BuffIndicator.refreshHero();
	}

	@Override
	public String actionName() {
		return Messages.get(this, "action");
	}

	@Override
	public int actionIcon() {
		return HeroIcon.SOUL;
	}

	@Override
	public Visual secondaryVisual() {
		BitmapText txt = new BitmapText(PixelScene.pixelFont);
		txt.text( Integer.toString((int) soulAmount) );
		txt.hardlight(CharSprite.POSITIVE);
		txt.measure();
		return txt;
	}

	@Override
	public int indicatorColor() {
		return cooldown > 0 ? 0x222222 : 0x333333;
	}

	@Override
	public void doAction() {
		GameScene.selectCell(new CellSelector.Listener() {
			@Override
			public void onSelect(Integer cell) {
				if (cell == null) return;
				if (!Dungeon.level.heroFOV[cell]){
					GLog.w(Messages.get(this, "invalid_pos"));
					return;
				}
				Char c = Actor.findChar(cell);
				if (c instanceof Hero){ //heal the soulhandler
					int toHeal = (int)Math.min(c.HT - c.HP,
							(0.5f + 0.1f * ((Hero) c).pointsInTalent(Talent.DEVOUR_SURGING)) * ((Hero) c).lvl);
					c.HP += toHeal;
					c.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(toHeal), FloatingText.HEALING);
					c.sprite.emitter().start( Speck.factory( Speck.HEALING ), 0.33f, 4 );
					Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
					c.sprite.operate(cell);
				} else if (c != null) { //doom the enemy
					Buff.affect(c, Doom.class);
					c.sprite.emitter().burst(ShadowParticle.CURSE, 5);
					switch (((Hero)target).pointsInTalent(Talent.SOUL_CAGING)) {
						case 3: //proc in Mob.die()
						case 2: c.damage((int)(((Hero)target).lvl * 0.3f), new SoulDamage());
						case 1: Buff.affect(c, Vertigo.class, 5f);
						default: break;
					}
					Sample.INSTANCE.play(Assets.Sounds.BURNING);
					c.sprite.attack(cell);
				} else { //summon a wraith ally
					c = Wraith.spawnAt(cell, Wraith.class, false, false);
					if (c == null) {
						GLog.w(Messages.get(this, "invalid_pos"));
						return;
					} else switch (((Hero)target).pointsInTalent(Talent.VENGEFUL_SPIRIT)) {
						case 3: Buff.affect(c, Barrier.class).setShield((int)(((Hero)target).lvl * 0.6f));
						case 2: Buff.affect(c, PhysicalEmpower.class).set((int)(((Hero)target).lvl * 0.2f), 3);
						case 1: Buff.affect(c, Adrenaline.class, 5.5f); //effectively 5 turn
						default: break;
					}
					c.sprite.attack(cell);
					Buff.affect(c, Corruption.class);
					Sample.INSTANCE.play(Assets.Sounds.CURSED);
				}
				abilityUsed();
			}

			@Override
			public String prompt() {
				return Messages.get(this, "prompt");
			}
		});
	}
	public static class SoulDamage{} //only used to track doom damage
	public static class ImmediateUseTracker extends FlavourBuff{}
}
