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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class BladeOfMimic extends MeleeWeapon {
    
    public static final String AC_MIMIC = "mimic";

    private int baseMin = 1;
    private int scaleMin = 1;
    private int baseMax = 10;
    private int scaleMax = 10;
    private int strReq = 10;
    private int baseBlock = 0;
    private int scaleBlock = 0;

    public boolean canSneak = true;
    private boolean useDefaultSTR = true;

    {
        image = ItemSpriteSheet.BLADE_OF_MIMIC;
        tier = 1;
    }

    @Override
    public int min(int lvl) {
        return baseMin + lvl * scaleMin;
    }

    @Override
    public int max(int lvl) {
        return baseMax + lvl * scaleMax;
    }

    @Override
    public int STRReq(int lvl) {
        if (useDefaultSTR) return super.STRReq(lvl);
        else                    return strReq;
    }

    @Override
    public int defenseFactor( Char owner ) {
        return DRMax();
    }

    public int DRMax(){
        return DRMax(buffedLvl());
    }

    //6 extra defence, plus 2 per level
    public int DRMax(int lvl){
        return 6 + 2*lvl;
    }

    //改变攻击距离、力量要求、基础最小值、成长最小值、基础最大值、成长最大值、精准、基础格挡、成长格挡、延迟
    
    @Override
    public ArrayList<String> actions(Hero hero ) {
        ArrayList<String> actions = super.actions( hero );
        actions.add(AC_MIMIC);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        if (action.equals(AC_MIMIC)){

        }
    }

    public String statsInfo(){
        if (isIdentified()){
            return Messages.get(this, "stats_desc", 6+2*buffedLvl());
        } else {
            return Messages.get(this, "typical_stats_desc", 6);
        }
    }

    private static final String BASE_MIN    = "base_min";
    private static final String SCALE_MIN   = "scale_min";
    private static final String BASE_MAX    = "base_max";
    private static final String SCALE_MAX   = "scale_min";
    private static final String REACH       = "reach";
    private static final String STR_REQ     = "str_req";
    private static final String ACCURACY    = "accuracy";
    private static final String BASE_BLOCK  = "base_block";
    private static final String SCALE_BLOCK = "scale_block";
    private static final String DELAY       = "dly";
    private static final String MIMIC_WEP   = "mimic_WEP";


    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put( BASE_MIN, baseMin );
        bundle.put( SCALE_MIN, scaleMin );
        bundle.put( BASE_MAX, baseMax );
        bundle.put( SCALE_MAX, scaleMax );
        bundle.put( REACH, RCH );
        bundle.put( STR_REQ, strReq );
        bundle.put( ACCURACY, ACC );
        bundle.put( BASE_BLOCK, baseBlock );
        bundle.put( SCALE_BLOCK, scaleBlock );
        bundle.put( DELAY, DLY );
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        baseMin = bundle.getInt(BASE_MIN);
        scaleMin = bundle.getInt(SCALE_MIN);
        baseMax = bundle.getInt(BASE_MAX);
        scaleMax = bundle.getInt(SCALE_MAX);
        RCH = bundle.getInt(REACH);
        strReq = bundle.getInt(STR_REQ);
        ACC = bundle.getFloat(ACCURACY);
        baseBlock = bundle.getInt(BASE_BLOCK);
        scaleBlock = bundle.getInt(SCALE_BLOCK);
        DLY = bundle.getFloat(DELAY);
    }
}