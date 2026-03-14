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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BlobImmunity;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

public class Futsunomitama extends MeleeWeapon {

	private int curCharge = 0;
	private static final int maxCharges = 15;

	public static final String AC_DISCHARGE = "DISCHARGE";

	{
		image = ItemSpriteSheet.FUTSUNOMITAMA;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 1.1f;

		tier = 4;
	}

	@Override
	public int max(int lvl) {
		return  4*(tier+1) +     //18 base, down from 25
				lvl*(tier);      //+4 scaling, down from +5
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		int chargeBefore = curCharge;
		curCharge = Math.min(curCharge + 1, maxCharges);
		if (chargeBefore < maxCharges && curCharge == maxCharges) GLog.p(Messages.get(this, "ready"));
		return super.proc(attacker, defender, damage);
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions( hero );
		if (curCharge >= maxCharges) actions.add(AC_DISCHARGE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {
		super.execute(hero, action);
		if (action.equals(AC_DISCHARGE)){
			if (curCharge < maxCharges){
				GLog.w(Messages.get(this, "no_enough_charge"));
			} else {
				for (int i : PathFinder.NEIGHBOURS9) {
					if (!Dungeon.level.solid[hero.pos + i]) {
						GameScene.add(Blob.seed(hero.pos + i, 8, Electricity.class));
					}
				}
				Buff.affect(hero, BlobImmunity.class, 7);
				curCharge = 0;
				Sample.INSTANCE.play(Assets.Sounds.LIGHTNING);
				hero.sprite.operate(hero.pos);
				hero.next();
			}
		}
	}

	public String statsInfo(){
		return Messages.get(this, "stats_desc", curCharge);
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		if (target == null) {
			return;
		}

		Char enemy = Actor.findChar(target);
		if (enemy == null || enemy == hero || hero.isCharmedBy(enemy) || !Dungeon.level.heroFOV[target]) {
			GLog.w(Messages.get(this, "ability_no_target"));
			return;
		}

		hero.belongings.abilityWeapon = this;
		if (!hero.canAttack(enemy)){
			GLog.w(Messages.get(this, "ability_target_range"));
			hero.belongings.abilityWeapon = null;
			return;
		}
		hero.belongings.abilityWeapon = null;

		hero.sprite.attack(enemy.pos, () -> {
			beforeAbilityUsed(hero, enemy);
			AttackIndicator.target(enemy);
			//no bonus damage, but have electricity effect
			if (hero.attack(enemy, 1, 0, Char.INFINITE_ACCURACY)){
				int chargeBefore = curCharge;
				curCharge = Math.min(curCharge + 1, maxCharges);
				if (chargeBefore < maxCharges && curCharge == maxCharges) GLog.p(Messages.get(Futsunomitama.class, "ready"));

				int electricityDmg = Hero.heroDamageIntRange(2 + buffedLvl(), 9 + 2 * buffedLvl());
				if (enemy.isAlive()) {
					enemy.damage(electricityDmg, new Electricity());
					enemy.sprite.burst(0xFFFFFFFF, 10);
					Sample.INSTANCE.play(Assets.Sounds.LIGHTNING);
				} else Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
			}

			Invisibility.dispel();
			hero.spendAndNext(hero.attackDelay());

			if (!enemy.isAlive()){
				hero.next();
				onAbilityKill(hero, enemy);
			}
			afterAbilityUsed(hero);
		});
	}

	@Override
	public String abilityInfo() {
		int minDmgBoost = levelKnown ? 2 + buffedLvl() : 2;
		int maxDmgBoost = levelKnown ? 9 + 2 * buffedLvl() : 9;
		if (levelKnown){
			return Messages.get(this, "ability_desc", minDmgBoost, maxDmgBoost);
		} else {
			return Messages.get(this, "typical_ability_desc", minDmgBoost, maxDmgBoost);
		}
	}

	public String upgradeAbilityStat(int level){
		int minDmgBoost = levelKnown ? 2 + level : 2;
		int maxDmgBoost = levelKnown ? 9 + 2 * level : 9;
		return minDmgBoost + "-" + maxDmgBoost;
	}

	private static final String CHARGE = "charge";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put(CHARGE, curCharge);
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		curCharge = bundle.getInt(CHARGE);
	}
}