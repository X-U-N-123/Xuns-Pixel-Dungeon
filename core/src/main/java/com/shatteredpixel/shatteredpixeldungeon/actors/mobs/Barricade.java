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
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BarricadeSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Barricade extends Mob {

    {
        spriteClass = BarricadeSprite.class;

        EXP = 0;
        maxLvl = -6;
        state = PASSIVE;

        properties.add(Property.INORGANIC);
        properties.add(Property.IMMOVABLE);
        properties.add(Property.STATIC);

        useParry = true;
    }

    public float aggression = 0f;

    //cannot move or attack, only blocks the road
    @Override
    protected boolean getCloser(int target) {
        return true;
    }

    @Override
    protected boolean getFurther(int target) {
        return true;
    }

    @Override
    protected boolean canAttack(Char enemy) {
        return false;
    }

    @Override
    public void damage( int dmg, Object src ) {
        if (src instanceof Char && aggression > 0 && dmg > 0){
            ArrayList<Class<? extends FlavourBuff>> debuff = new ArrayList<>();
            if (((Char)src).buff(Weakness.class) == null)   debuff.add(Weakness.class);
            if (((Char)src).buff(Vertigo.class) == null)    debuff.add(Vertigo.class);
            if (((Char)src).buff(Daze.class) == null)       debuff.add(Daze.class);
            if (((Char)src).buff(Vulnerable.class) == null) debuff.add(Vulnerable.class);
            if (((Char)src).buff(Slow.class) == null)       debuff.add(Slow.class);
            if (((Char)src).buff(BrokenArmor.class) == null)debuff.add(BrokenArmor.class);
            if (((Char)src).buff(Blindness.class) == null)  debuff.add(Blindness.class);
            if (debuff.isEmpty())                           debuff.add(Paralysis.class);
            Buff.affect((Char)src, debuff.get(Random.Int(debuff.size())), aggression);
        }
        sprite.linkVisuals(this);//check sprite
        super.damage( dmg, src );
        sprite.linkVisuals(this);//check sprite
    }

    @Override
    public CharSprite sprite() { // changes the icon in the mob info window
        BarricadeSprite sprite = (BarricadeSprite) super.sprite();

        if (HP < HT /3f)        sprite.broken();
        else if (HP < HT *2/3f) sprite.cracked();
        else                    sprite.idle();

        return sprite;
    }

    @Override
    public String description() {
        String desc;
        switch (alignment){
            case ALLY:
                desc = Messages.get(this, "desc_ally");
                break;
            case ENEMY: default:
                desc = Messages.get(this, "desc_enemy");
                break;
        }
        if (aggression > 0) desc += Messages.get(this, "aggressive");
        return desc;
    }

    private static final String ALIGNMENT = "alignment";
    private static final String AGGRESSION= "aggression";
    //the alignment of this may change, so need to store and restore it
    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle(bundle);
        bundle.put(AGGRESSION, aggression);
        bundle.put(ALIGNMENT, alignment);
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle(bundle);
        aggression = bundle.getFloat(AGGRESSION);
        alignment = bundle.getEnum(ALIGNMENT, Alignment.class);
    }

}