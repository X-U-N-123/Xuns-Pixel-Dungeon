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

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Exhaustion extends FlavourBuff {

	private int level = 0;

	@Override
	public int icon() {
		return BuffIndicator.EXHAUSTION_START + level;
	}

	public static void stack(Hero hero) {
		Exhaustion e = hero.buff(Exhaustion.class);
		if (e == null) Buff.prolong(hero, Exhaustion.class, 3).level = 1;
		else {
			Buff.prolong(hero, Exhaustion.class, e.level + 3);
			e.level++;
			if (e.level >= 4){
				e.level = 2;
				int chance = Random.Int(20);
				if (chance < 6)       Buff.prolong(hero, Hex.class,        5);
				else if (chance < 11) Buff.prolong(hero, Weakness.class,   5);
				else if (chance < 15) Buff.prolong(hero, Vulnerable.class, 5);
				else if (chance < 18) Buff.prolong(hero, Slow.class,       5);
				else                  Buff.prolong(hero, Degrade.class,    5);
			}
		}
		BuffIndicator.refreshHero();
	}

	@Override
	public void detach() {
		level --;
		super.detach();
		if (level > 0) Buff.prolong(target, Exhaustion.class, (int)Math.pow(2, level-1) + 1).level = level;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", level, dispTurns());
	}

	private static final String LEVEL = "level";

	@Override
	public void storeInBundle(Bundle bundle){
		super.storeInBundle(bundle);
		bundle.put(LEVEL, level);
	}

	@Override
	public void restoreFromBundle(Bundle bundle){
		super.restoreFromBundle(bundle);
		level = bundle.getInt(LEVEL);
	}
}