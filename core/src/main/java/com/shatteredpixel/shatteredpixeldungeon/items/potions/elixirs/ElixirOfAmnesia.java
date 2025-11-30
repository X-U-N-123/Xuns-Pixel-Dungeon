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

package com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfDivineInspiration;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.GooBlob;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.StatusPane;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndHero;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ElixirOfAmnesia extends Elixir {

    {
        image = ItemSpriteSheet.ELIXIR_AMNESIA;

        talentFactor = 2f;
    }

    @Override
    public void apply(Hero hero) {
        boolean reseted = false;

        for (LinkedHashMap<Talent, Integer> tier : hero.talents){
            for (Talent f : tier.keySet()){
                if (tier.get(f) > 0){
                    reseted = true;
                    tier.put(f, 0);
                }
            }
        }

        if (reseted){
            StatusPane.talentBlink = 10f;
            WndHero.lastIdx = 1;
        }
        GameScene.showlevelUpStars();
        Sample.INSTANCE.play(Assets.Sounds.BURNING);
        new Flare( 6, 32 ).color(0xFFFF00, true).show( curUser.sprite, 2f );
    }

    @Override
    public int value() {
        return (int)(100 * (quantity/(float) Recipe.OUT_QUANTITY));
    }

    @Override
    public int energyVal() {
        return (int)(24 * (quantity/(float) Recipe.OUT_QUANTITY));
    }

    public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

        private static final int OUT_QUANTITY = 1;

        {
            inputs =  new Class[]{PotionOfDivineInspiration.class, GooBlob.class};
            inQuantity = new int[]{1, 1};

            cost = 10;

            output = ElixirOfAmnesia.class;
            outQuantity = OUT_QUANTITY;
        }

        @Override
        public Item brew(ArrayList<Item> ingredients) {
            Catalog.countUse(GooBlob.class);
            return super.brew(ingredients);
        }

    }

}