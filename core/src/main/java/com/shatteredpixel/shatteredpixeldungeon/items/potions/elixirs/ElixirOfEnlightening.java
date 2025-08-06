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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bless;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfDivineInspiration;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.StatusPane;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndHero;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class ElixirOfEnlightening extends Elixir {

    {
        image = ItemSpriteSheet.ELIXIR_ENLIGHTENING;
    }

    @Override
    public void apply(Hero hero) {
        EnlighteningTracker tracker = hero.buff(EnlighteningTracker.class);

        if (tracker != null && tracker.Boostamount() >= 3){
            hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(hero.maxExp()), FloatingText.EXPERIENCE);
            hero.earnExp( hero.maxExp(), getClass() );
            new Flare( 6, 32 ).color(0xFFFF00, true).show( curUser.sprite, 2f );
            Buff.affect(hero, Bless.class, 100f);
        } else {
            Buff.affect(curUser, EnlighteningTracker.class).Boost();

            curUser.busy();
            curUser.sprite.operate(curUser.pos);
            curUser.spendAndNext(1f);
            boolean unspentTalents = false;

            for (int i = 1; i <= Dungeon.hero.talents.size(); i++){
                if (Dungeon.hero.talentPointsAvailable(i) > 0){
                    unspentTalents = true;
                    break;
                }
            }
            if (unspentTalents){
                StatusPane.talentBlink = 10f;
                WndHero.lastIdx = 1;
            }

            GameScene.showlevelUpStars();

            Sample.INSTANCE.play( Assets.Sounds.DRINK );
            Sample.INSTANCE.playDelayed(Assets.Sounds.LEVELUP, 0.2f, 0.7f, 1.2f);
            Sample.INSTANCE.playDelayed(Assets.Sounds.LEVELUP, 0.4f, 0.7f, 1.2f);
            Sample.INSTANCE.playDelayed(Assets.Sounds.LEVELUP, 0.6f, 0.7f, 1.2f);
            Sample.INSTANCE.playDelayed(Assets.Sounds.LEVELUP, 0.8f, 0.7f, 1.2f);
            new Flare( 6, 32 ).color(0xFFFF00, true).show( curUser.sprite, 2f );
            GLog.p(Messages.get(ElixirOfEnlightening.class, "bonus"));

            if (!anonymous) {
                if (Random.Float() < talentChance) {
                    Talent.onPotionUsed(curUser, curUser.pos, talentFactor);
                }
            }
        }
    }

    public static class EnlighteningTracker extends Buff {

        {
            type = buffType.POSITIVE;
            revivePersists = true;
        }

        private int boost = 0;

        public void Boost(){
            boost += 1;
        }

        public int Boostamount(){
            return boost;
        }

        private static final String BOOST = "boost";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(BOOST, boost);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            boost = bundle.getInt(BOOST);
        }
    }

    public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs =  new Class[]{PotionOfDivineInspiration.class};
            inQuantity = new int[]{1};

            cost = 12;

            output = ElixirOfEnlightening.class;
            outQuantity = 1;
        }

    }
}
