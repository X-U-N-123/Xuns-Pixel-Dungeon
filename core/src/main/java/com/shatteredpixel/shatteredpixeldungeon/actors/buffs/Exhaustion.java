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

public class Exhaustion extends FlavourBuff {

	public static final int LAYER = 12;

	{
		actPriority = HERO_PRIO + 1;
		type = buffType.NEGATIVE;
	}

	@Override
	public int icon() {
		return BuffIndicator.EXHAUSTION_START + Math.min((int)cooldown() / LAYER, 3);
	}

	public static void stack(Hero hero) {
		Exhaustion e = hero.buff(Exhaustion.class);
		if (e == null) Buff.prolong(hero, Exhaustion.class, 4);
		else e.spend(e.cooldown() - 1);
		BuffIndicator.refreshHero();
	}

	@Override
	public String desc() {
		String desc = Messages.get(this, "desc");
		for (int i = 0; i < Math.min(cooldown() / LAYER, 4); i++)
			desc += "\n" + Messages.get(this, "desc" + i);
		return desc + Messages.get(this, "turn", dispTurns(cooldown()));
	}

	@Override
	public String iconTextDisplay() {
		return Integer.toString((int)cooldown());
	}
}