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

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Daze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.sprites.StonePierSprite;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class StonePier extends Mob {

    {
        spriteClass = StonePierSprite.class;

        EXP = 0;
        state = PASSIVE;

        properties.add(Property.INORGANIC);
        properties.add(Property.IMMOVABLE);
        properties.add(Property.STATIC);
    }

    @Override
    public int damageRoll() {
        return 0;
    }

    @Override
    protected boolean canAttack(Char enemy) {
        return false;
    }

    @Override
    public void damage( int dmg, Object src ) {
        if (src instanceof Char){
            ArrayList<Class<? extends FlavourBuff>> debuff = new ArrayList<>();
            debuff.add(Weakness.class);
            debuff.add(Vertigo.class);
            debuff.add(Daze.class);
            debuff.add(Vulnerable.class);
            debuff.add(Slow.class);
            debuff.add(Paralysis.class);
            int i;
            do {
                i = Random.Int(debuff.size());
            } while (((Char)src).buff(debuff.get(i)) != null);
            Buff.affect((Char)src, debuff.get(i), 8f);
        }
        super.damage( dmg, src );
    }

}