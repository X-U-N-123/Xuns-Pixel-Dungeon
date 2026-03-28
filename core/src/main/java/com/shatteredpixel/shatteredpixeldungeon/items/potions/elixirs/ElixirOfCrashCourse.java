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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.GooBlob;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class ElixirOfCrashCourse extends Elixir {

    {
        image = ItemSpriteSheet.ELIXIR_TEMPERING;

        talentChance = 1f;//special logic for this one
        talentFactor = 1f;
    }

	@Override
	public void drink( Hero hero ){
		if (hero.exp < hero.maxExp() * 0.8f){
			GLog.w(Messages.get(this, "no_enough_exp"));
		} else super.drink(hero);
	}

    @Override
    public void apply(Hero hero) {
        hero.exp -= hero.maxExp() * 0.8f;
        Buff.affect(hero, Weakness.class, 80f);
        Sample.INSTANCE.playDelayed(Assets.Sounds.MISS, 0.75f);
        GLog.h(Messages.get(this, "stronger"));

        hero.fakeLvl ++;
        hero.updateHT(false);
        hero.incAttackSkill(1);
        hero.incDefenseSkill(1);
    }

	@Override
	public String desc() {
		return Messages.get(this, "desc", Dungeon.hero != null ? (int)(Dungeon.hero.maxExp() * 0.8f) : 8);
	}

    @Override
    public int value() {
        return (int)(100 * (quantity/(float) Recipe.OUT_QUANTITY));
    }

    @Override
    public int energyVal() {
        return (int)(20 * (quantity/(float) Recipe.OUT_QUANTITY));
    }

    public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

        private static final int OUT_QUANTITY = 2;

        {
            inputs =  new Class[]{PotionOfStrength.class, GooBlob.class};
            inQuantity = new int[]{1, 1};

            cost = 10;

            output = ElixirOfCrashCourse.class;
            outQuantity = OUT_QUANTITY;
        }

        @Override
        public Item brew(ArrayList<Item> ingredients) {
            Catalog.countUse(GooBlob.class);
            return super.brew(ingredients);
        }

    }

    @Override
    public float weight(){
        return 0.1f * quantity() / Recipe.OUT_QUANTITY;
    }
}