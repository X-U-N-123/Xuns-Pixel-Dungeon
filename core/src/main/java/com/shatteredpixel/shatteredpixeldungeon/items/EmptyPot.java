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

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bee;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

public class EmptyPot extends Item {

    public static final String AC_HOLD = "hold";

    {
        image = ItemSpriteSheet.POT;

        defaultAction = AC_HOLD;
        usesTargeting = false;

        stackable = true;
    }

    @Override
    public ArrayList<String> actions( Hero hero ) {
        ArrayList<String> actions = super.actions( hero );
        actions.add( AC_HOLD );
        return actions;
    }

    @Override
    public void execute(final Hero hero, String action ) {

        super.execute( hero, action );

        if (action.equals( AC_HOLD )) {

            Char bee = null;
            for (int i : PathFinder.NEIGHBOURS8){
                if (Actor.findChar(hero.pos + i) instanceof Bee){
                    bee = Actor.findChar(hero.pos + i);
                    break;
                }
            }
            if (bee != null) {
                bee.die(Hero.class);
                Sample.INSTANCE.play( Assets.Sounds.BEE );
                Honeypot pot = new Honeypot();
                Catalog.countUse(EmptyPot.class);
                if (!pot.collect()){
                    Dungeon.level.drop(pot, hero.pos).sprite.drop();
                }
                this.detach(hero.belongings.backpack);
                hero.spendAndNext(Actor.TICK);
            } else {
                GLog.w(Messages.get(this, "no_bee"));
            }
        }
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public int value() {
        return 20 * quantity;
    }

    @Override
    public int energyVal() {
        return 6 * quantity;
    }

    public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

        private static final int OUT_QUANTITY = 1;

        {
            inputs =  new Class[]{Honeypot.ShatteredPot.class, PotionOfHealing.class};
            inQuantity = new int[]{1, 1};

            cost = 0;

            output = EmptyPot.class;
            outQuantity = OUT_QUANTITY;
        }

        @Override
        public Item brew(ArrayList<Item> ingredients) {
            Catalog.countUse(Honeypot.ShatteredPot.class);
            return super.brew(ingredients);
        }
    }

}