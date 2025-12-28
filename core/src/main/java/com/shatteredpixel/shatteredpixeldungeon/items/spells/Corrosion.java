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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.MetalShard;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfMetamorphosis;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentsPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class Corrosion extends Spell {

    {
        image = ItemSpriteSheet.CORROSION;

        talentChance = 1/(float) Recipe.OUT_QUANTITY;
        talentFactor = 2f;
    }

    @Override
    protected void onCast(final Hero hero){
        if (Statistics.corrosionUsed || hero.subClass == HeroSubClass.NONE || hero.lvl < 12){
            hero.HP = 0;
            Sample.INSTANCE.play(Assets.Sounds.BURNING);
            GLog.n(Messages.get(this, "ondeath"));
            hero.die(this);
            Dungeon.fail(this);
        } else {
            GameScene.show(new WndTalentForget(this));
        }
    }

    @Override
    public String desc() {
        String desc = super.desc();
        if (Statistics.corrosionUsed)
            desc += "\n\n" + Messages.get(this, "desc_used");
        if (Dungeon.hero == null || Dungeon.hero.subClass == HeroSubClass.NONE || Dungeon.hero.lvl < 12)
            desc += "\n\n" + Messages.get(this, "desc_no_subclass");
        return desc;
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
            inputs =  new Class[]{ScrollOfMetamorphosis.class, MetalShard.class};
            inQuantity = new int[]{1, 1};

            cost = 8;

            output = Corrosion.class;
            outQuantity = OUT_QUANTITY;
        }

        @Override
        public Item brew(ArrayList<Item> ingredients) {
            Catalog.countUse(MetalShard.class);
            return super.brew(ingredients);
        }

    }

    public static class WndTalentForget extends Window {

        public static WndTalentForget INSTANCE;
        public static Corrosion corrosion;

        public WndTalentForget(Corrosion c){
            super();
            INSTANCE = this;
            corrosion = c;
            setup();
        }

        private void setup(){
            float top;

            IconTitle title = new IconTitle( new ItemSprite(corrosion), Messages.titleCase(corrosion.name()) );
            title.color( TITLE_COLOR );
            title.setRect(0, 0, 120, 0);
            add(title);

            top = title.bottom() + 2;

            RenderedTextBlock text = PixelScene.renderTextBlock(Messages.get(Corrosion.class, "choose_forget"), 6);
            text.maxWidth(120);
            text.setPos(0, top);
            add(text);

            top = text.bottom() + 2;

            TalentsPane.TalentTierPane optionsPane = new TalentsPane.TalentTierPane(Dungeon.hero.talents.get(2), 3, TalentButton.Mode.CORROSION_FORGET);
            add(optionsPane);
            optionsPane.title.text("");
            optionsPane.setPos(0, top);
            optionsPane.setSize(120, optionsPane.height());
            resize((int)optionsPane.width(), (int)optionsPane.bottom());

            resize(120, (int)optionsPane.bottom());
        }
    }
}