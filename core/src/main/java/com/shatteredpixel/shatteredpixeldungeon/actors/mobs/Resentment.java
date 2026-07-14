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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ResentmentSprite;
import com.watabou.utils.Random;

public class Resentment extends Mob {

	{
		spriteClass = ResentmentSprite.class;

		HP = HT = 75;
		defenseSkill = 50;
		viewDistance = Light.DISTANCE;

		EXP = 13;
		maxLvl = 27;

		flying = true;

		loot = Gold.class;
		lootChance = 0.5f;

		properties.add(Property.DEMONIC);
	}

	@Override
	public float spawningWeight() {
		return -1;
	}

	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 5, 45 );
	}

	@Override
	public int attackSkill( Char target ) {
		return 75;
	}//high evasion and accuracy! But players still have a way to fight ahainst it, see wandProc()

	@Override
	public void die( Object cause ){
		flying = false;
		super.die(cause);
		Dungeon.level.spawnMob(12);
	}
}