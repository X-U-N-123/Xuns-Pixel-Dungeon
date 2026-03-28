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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.wraith;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corrosion;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LostInventory;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Viscosity;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAggression;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class Transfusion extends ArmorAbility {

	{
		baseChargeUse = 50f;
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	public float chargeUse( Hero hero ) {
		float chargeUse = super.chargeUse(hero);
		if (hero.buff(PhilanthropistTracker.class) != null)
			chargeUse *= 1 - hero.pointsInTalent(Talent.HEMATOPOIESIS) / 8f;
		return chargeUse;
	}

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {
		if (target == null) return;
		int loanAmount = 10 + 5 * hero.pointsInTalent(Talent.EMERGENCY_TRANSFUSION);
		if (hero.HP <= loanAmount){
			GLog.w(Messages.get(this, "no_enough_hp"));
			return;
		}
		Char c = Actor.findChar(target);
		if (c == null || c.alignment == Char.Alignment.ALLY || c.buff(TransfusionTracker.class) != null){
			GLog.w(Messages.get(this, "invalid_target"));
			return;
		}
		Buff.affect(c, TransfusionTracker.class).spend(24 - 4 * hero.pointsInTalent(Talent.BIOLOGICAL_CONTROL));
		int toHeal = Math.min(c.HT - c.HP, loanAmount);
		c.HP += toHeal;
		c.sprite.emitter().start( Speck.factory( Speck.HEALING ), 0.33f, 3 + hero.pointsInTalent(Talent.EMERGENCY_TRANSFUSION));
		c.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(toHeal), FloatingText.HEALING);

		hero.HP -= loanAmount;
		hero.damage(0, this); //to proc some on-damage effect
		hero.sprite.showStatusWithIcon(CharSprite.NEGATIVE, Integer.toString(loanAmount), FloatingText.REALITY);
		Buff.affect(hero, PhilanthropistTracker.class, 0f);

		armor.charge -= chargeUse( hero );
		armor.updateQuickslot();

		int giftAmount = 0;
		for (Buff b : hero.buffs()){
			if (giftAmount >= hero.pointsInTalent(Talent.BLOOD_INFECTION)) break;
			if (b.type != Buff.buffType.NEGATIVE
					|| b instanceof AllyBuff || b instanceof LostInventory)
				continue;

			if (b instanceof FlavourBuff)       Buff.affect(c, (Class<?extends FlavourBuff>)b.getClass(), b.cooldown());
			else if (b instanceof Bleeding)     Buff.affect(c, Bleeding.class).set(((Bleeding) b).level());
			else if (b instanceof Burning)      Buff.affect(c, Burning.class).reignite(c, ((Burning) b).left());
			else if (b instanceof Corrosion)    Buff.affect(c, Corrosion.class).set(((Corrosion) b).duration(), ((Corrosion) b).damage());
			else if (b instanceof Ooze)         Buff.affect(c, Ooze.class).set(((Ooze) b).left());
			else if (b instanceof Poison)       Buff.affect(c, Poison.class).set(((Poison) b).left());
			else if (b instanceof Viscosity.DeferedDamage) Buff.affect(c, Viscosity.DeferedDamage.class).set(((Viscosity.DeferedDamage) b).damage());
			else continue;

			giftAmount ++;
			b.detach();
		}
	}

	@Override
	public int icon() {
		return HeroIcon.LIFELOAN;
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.EMERGENCY_TRANSFUSION, Talent.BLOOD_INFECTION, Talent.BIOLOGICAL_CONTROL, Talent.HEMATOPOIESIS, Talent.HEROIC_ENERGY};
	}

	public static class PhilanthropistTracker extends FlavourBuff{}

	public static class TransfusionTracker extends Buff {

		{
			announced = true;
		}

		@Override
		public int icon() {
			return BuffIndicator.LIFELOAN;
		}

		@Override
		public boolean act(){
			if (Dungeon.hero.hasTalent(Talent.BIOLOGICAL_CONTROL))
				Buff.affect(target, StoneOfAggression.Aggression.class, 25);
			diactivate();
			return true;
		}

		@Override
		public void spend( float time ){
			super.spend(time);
		}

		@Override
		public void detach() {
			int toHeal = Math.min(Dungeon.hero.HT - Dungeon.hero.HP,
					20 + Math.round(Dungeon.hero.pointsInTalent(Talent.EMERGENCY_TRANSFUSION) * 7.5f));
			Dungeon.hero.HP += toHeal;
			Dungeon.hero.sprite.emitter().start( Speck.factory( Speck.HEALING ),
					0.33f, 5 + 2 * Dungeon.hero.pointsInTalent(Talent.EMERGENCY_TRANSFUSION));
			Dungeon.hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(toHeal), FloatingText.HEALING);
			super.detach();
		}

	}
}