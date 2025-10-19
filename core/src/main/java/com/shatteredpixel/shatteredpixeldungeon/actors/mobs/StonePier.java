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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BrokenArmor;
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
    protected boolean getCloser(int target) {
        return false;
    }

    @Override
    protected boolean getFurther(int target) {
        return false;
    }

    @Override
    protected boolean canAttack(Char enemy) {
        return false;
    }

    @Override
    public void damage( int dmg, Object src ) {
        if (src instanceof Char){
            ArrayList<Class<? extends FlavourBuff>> debuff = new ArrayList<>();
            if (((Char)src).buff(Weakness.class) == null)   debuff.add(Weakness.class);
            if (((Char)src).buff(Vertigo.class) == null)    debuff.add(Vertigo.class);
            if (((Char)src).buff(Daze.class) == null)       debuff.add(Daze.class);
            if (((Char)src).buff(Vulnerable.class) == null) debuff.add(Vulnerable.class);
            if (((Char)src).buff(Slow.class) == null)       debuff.add(Slow.class);
            if (((Char)src).buff(BrokenArmor.class) == null)debuff.add(BrokenArmor.class);
            if (((Char)src).buff(Blindness.class) == null)  debuff.add(Blindness.class);
            if (debuff.isEmpty())                           debuff.add(Paralysis.class);
            Buff.affect((Char)src, debuff.get(Random.Int(debuff.size())), 8f);
        }
        super.damage( dmg, src );
    }

}