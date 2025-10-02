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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;

public class Peaceful extends Weapon.Enchantment {

    private static ItemSprite.Glowing PINK = new ItemSprite.Glowing( 0xFFCCFF );

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage ) {

        if (defender.isImmune(Peaceful.class)) {
            return damage;
        }

        int level = Math.max( 0, weapon.buffedLvl() );

        float maxChance = (2f / (level + 3f));
        maxChance = (float)Math.pow(maxChance, procChanceMultiplier(attacker));

        Buff.affect(defender, Peaceful.PeaccefulTracker.class).chance = maxChance;

        return damage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return PINK;
    }

    public static class PeaccefulTracker extends Buff {

        {
            actPriority = Actor.VFX_PRIO;
        }

        public float chance;
    }

}