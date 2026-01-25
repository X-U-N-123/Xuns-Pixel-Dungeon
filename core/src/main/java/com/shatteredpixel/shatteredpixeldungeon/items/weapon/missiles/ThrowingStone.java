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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ThrowingStone extends MissileWeapon {
	
	{
		image = ItemSpriteSheet.THROWING_STONE;
		hitSound = Assets.Sounds.HIT;
		hitSoundPitch = 1.1f;
		
		bones = false;
		
		tier = 1;
		baseUses = 5;
		sticky = false;
	}

	@Override
	public int damageRoll(Char owner) {
		if (owner instanceof Hero) {
			Hero hero = (Hero)owner;
			Char enemy = hero.attackTarget();
			if (enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero) &&
			((Hero) owner).pointsInTalent(Talent.FLYING_LOCUST_STONE) > 1) {
				//deals 80% toward max on surprise, instead of min to max.
				int diff = max() - min();
				int damage = augment.damageFactor(Hero.heroDamageIntRange(
				min() + Math.round(diff*0.8f), max()));

				int exStr = hero.STR() - STRReq();
				if (exStr > 0) {
					if (((Hero) owner).pointsInTalent(Talent.FLYING_LOCUST_STONE) > 1) damage += exStr;
					else damage += Hero.heroDamageIntRange(0, exStr);
				}
				return damage;
			}
		}
		return super.damageRoll(owner);
	}

	@Override
	public float delayFactor(Char owner) {
		if (owner instanceof Hero && ((Hero) owner).justMoved && ((Hero) owner).pointsInTalent(Talent.FLYING_LOCUST_STONE) > 2) return 0;
		else                                                    return super.delayFactor(owner);
	}
}
