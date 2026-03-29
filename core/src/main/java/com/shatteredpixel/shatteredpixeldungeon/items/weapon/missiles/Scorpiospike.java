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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Scorpiospike extends MissileWeapon {

    {
        image = ItemSpriteSheet.SCORPIOSPIKE;
        hitSound = Assets.Sounds.HIT_STAB;
        hitSoundPitch = 0.9f;

        tier = 6;
    }

	@Override
	public int max(int lvl) {
		return  3 * tier +     //18 base, down from 30
				2 * lvl;       //scaling unchanged
	}

	@Override
	public int proc(Char attacker, Char defender, int damage ) {
		Buff.prolong( defender, Cripple.class, Cripple.DURATION );
		return super.proc( attacker, defender, damage );
	}

}