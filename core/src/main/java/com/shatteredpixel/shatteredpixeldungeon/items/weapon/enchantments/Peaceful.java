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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Sleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.utils.Random;

public class Peaceful extends Weapon.Enchantment {

    @Override
    public int proc(Weapon weapon, Char attacker, Char defender, int damage ) {

        if (!defender.isImmune(Peaceful.class)) {
            int level = Math.max( 0, weapon.buffedLvl() );

            // lvl 0 - 20%
            // lvl 1 ~ 27.2%
            // lvl 2 ~ 33.3%
            float procChance = (level+2f)/(level+10f) * procChanceMultiplier(attacker);

            if (defender instanceof Mob){

                if (((Mob)defender).state == ((Mob)defender).HUNTING && Random.Float() < procChance){
                    ((Mob) defender).clearEnemy();
                    ((Mob) defender).beckon(Dungeon.level.randomDestination(defender));
                    defender.sprite.showLost(); //for unknown reason, it cannot show lost emoicon correctly
                    procChance --;
                }
                if (((Mob)defender).state == ((Mob)defender).WANDERING && Random.Float() < procChance && !defender.isImmune(Sleep.class)){
                    ((Mob) defender).state = ((Mob) defender).SLEEPING;
                    procChance --;
                }
                if (((Mob)defender).state == ((Mob)defender).SLEEPING && Random.Float() < procChance && !defender.isImmune(Sleep.class)){
                    Buff.affect(defender, MagicalSleep.class);
                }
                Buff.affect(defender, PeacefulTracker.class);
            }
        }

        return damage;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return new ItemSprite.Glowing( 0xFF99FF );
    }

    public static class PeacefulTracker extends Buff {
        {actPriority = Actor.VFX_PRIO;}
    }

}