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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM300;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Crabclaw;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.DMdrill;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Darkgoldsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dinnerknife;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Erlangknife;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Fetter;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Fork;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greatknife;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.HandAxe;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Havoc;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Jieniu;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Jingubang;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Knife;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Nunchaku;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Ripperclaw;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Shangfang;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Windblade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Wolftailgrassspear;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class Annoying extends Weapon.Enchantment {

	private static ItemSprite.Glowing BLACK = new ItemSprite.Glowing( 0x000000 );

	@Override
	public int proc( Weapon weapon, Char attacker, Char defender, int damage ) {

		float procChance = 1/20f * procChanceMultiplier(attacker);
		if (Random.Float() < procChance) {
			for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
				mob.beckon(attacker.pos);
			}
			attacker.sprite.centerEmitter().start(Speck.factory(Speck.SCREAM), 0.3f, 3);
			Sample.INSTANCE.play(Assets.Sounds.MIMIC);
			Invisibility.dispel();
			//rare line, ~9% for each common line
			if (Random.Int(20) != 0) {
				GLog.n(Messages.get(this, "msg_" + Random.IntRange(1, 12)));
			} else {
				if (weapon instanceof HandAxe){
					GLog.n(Messages.get(this, "handaxe"));
					return damage;
				}
				if (weapon instanceof DMdrill){
					GLog.n(Messages.get(DM300.class, "supercharged"));
					return damage;
				}
				if (weapon instanceof Havoc){
					GLog.n(Messages.get(this, "havoc"));
					return damage;
				}
				if (weapon instanceof Jieniu){
					GLog.n(Messages.get(this, "jieniu"));
					return damage;
				}
				if (weapon instanceof Ripperclaw){
					GLog.n(Messages.get(this, "ripper"));
					return damage;
				}
				if (weapon instanceof Knife || weapon instanceof Greatknife){
					GLog.n(Messages.get(this, "knife"));
					return damage;
				}
				if (weapon instanceof Erlangknife){
					GLog.n(Messages.get(this, "erlang"));
					return damage;
				}
				if (weapon instanceof Jingubang){
					GLog.n(Messages.get(this, "jigubang"));
					return damage;
				}
				if (weapon instanceof Windblade){
					GLog.n(Messages.get(this, "wind"));
					return damage;
				}
				if (weapon instanceof Crabclaw){
					GLog.n(Messages.get(this, "crab"));
					return damage;
				}
				if (weapon instanceof Dinnerknife || weapon instanceof Fork){
					GLog.n(Messages.get(this, "dinner"));
					return damage;
				}
				if (weapon instanceof Fetter){
					GLog.n(Messages.get(this, "slave"));
					return damage;
				}
				if (weapon instanceof Nunchaku){
					GLog.n(Messages.get(this, "nunchaku"));
					return damage;
				}
				if (weapon instanceof Shangfang){
					GLog.n(Messages.get(this, "emperor"));
					return damage;
				}
				if (weapon instanceof Wolftailgrassspear){
					GLog.n(Messages.get(this, "qi"));
					return damage;
				}
				GLog.n(Messages.get(this, "msg_" + Random.IntRange(1, 12)));
			}
		}

		return damage;
	}

	@Override
	public boolean curse() {
		return true;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return BLACK;
	}

}