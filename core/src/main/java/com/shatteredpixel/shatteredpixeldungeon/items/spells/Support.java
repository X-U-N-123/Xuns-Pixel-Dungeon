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

package com.shatteredpixel.shatteredpixeldungeon.items.spells;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Collapse;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.EnergyCrystal;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class Support extends Spell {

    {
        image = ItemSpriteSheet.SUPPORT;

        usesTargeting = true;
    }

    @Override
    protected void onCast(Hero hero) {
        if (hero.buff(Collapse.class) != null) {
            hero.buff(Collapse.class).incTotalTime(-201);
            hero.spendAndNext(1f);
            Catalog.countUse(getClass());
            GLog.p(Messages.get(this, "support"));
            Sample.INSTANCE.play(Assets.Sounds.MINE, 0.5f);
        } else {
            Item i = new ScrollOfIdentify();
            if (!i.collect()){
                Dungeon.level.drop(i, hero.pos).sprite.drop();
            }
            i = new PotionOfHealing();
            if (!i.collect()){
                Dungeon.level.drop(i, hero.pos).sprite.drop();
            }
            new EnergyCrystal(3).doPickUp(hero, hero.pos);
        }
        hero.sprite.operate(hero.pos);
        detach(hero.belongings.backpack);
    }

    @Override
    public String desc() {
        String desc = super.desc();
        if (!Dungeon.isChallenged(Challenges.NO_RETURN)){
            desc += "\n\n" + Messages.get(this, "desc_no_challenge");
        }
        return desc;
    }

    @Override
    public int value() {
        return (int)(40 * (quantity/(float) Recipe.OUT_QUANTITY));
    }

    @Override
    public int energyVal() {
        return (int)(8 * (quantity/(float) Recipe.OUT_QUANTITY));
    }

    public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

        private static final int OUT_QUANTITY = 1;

        {
            inputs =  new Class[]{ScrollOfIdentify.class, PotionOfHealing.class};
            inQuantity = new int[]{1, 1};

            cost = 0;

            output = Support.class;
            outQuantity = OUT_QUANTITY;
        }
    }
}