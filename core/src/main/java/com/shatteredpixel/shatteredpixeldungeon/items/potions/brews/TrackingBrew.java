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

package com.shatteredpixel.shatteredpixeldungeon.items.potions.brews;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMindVision;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class TrackingBrew extends Brew {

    {
        image = ItemSpriteSheet.BREW_TRACKING;

        usesTargeting = true;

        talentChance = 1/(float) Recipe.OUT_QUANTITY;
    }

    @Override
    public void shatter(int cell) {
        splash( cell );
        Char c = Actor.findChar(cell);
        if (c != null){
            Buff.append(Dungeon.hero, TalismanOfForesight.CharAwareness.class, Integer.MAX_VALUE).charID = c.id();
            Sample.INSTANCE.play(Assets.Sounds.READ);
            GLog.h(Messages.get(this, "track", c.name()));
        }
    }

    @Override
    public int value() {
        return (int)(60 * (quantity/(float) Recipe.OUT_QUANTITY));
    }

    @Override
    public int energyVal() {
        return (int)(12 * (quantity/(float) Recipe.OUT_QUANTITY));
    }

    public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

        private static final int OUT_QUANTITY = 8;

        {
            inputs =  new Class[]{PotionOfMindVision.class};
            inQuantity = new int[]{1};

            cost = 6;

            output = TrackingBrew.class;
            outQuantity = OUT_QUANTITY;
        }

    }

    @Override
    public float weight(){
        return 0.1f * quantity() / Recipe.OUT_QUANTITY;
    }



}