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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.MetalShard;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfPsionicBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorruption;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class CorruptionSpell extends Spell {

    {
        image = ItemSpriteSheet.CORRUPTION;

        talentChance = 1/(float) Recipe.OUT_QUANTITY;
    }

    @Override
    protected void onCast(final Hero hero){

        int corruptAmount = 0;

        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])){
            if (mob.alignment == Char.Alignment.ENEMY && hero.fieldOfView[mob.pos]){
                WandOfCorruption.corruptEnemy(mob);
                corruptAmount ++;//corrupt the enemy
                CellEmitter.get(mob.pos).burst(ShadowParticle.CURSE, 5);
            }
        }

        hero.damage( Math.round(10f * hero.lvl / (corruptAmount + 2f) ), this);
        Buff.affect(hero, Vulnerable.class, 100f);
        Buff.affect(hero, Weakness.class,   100f);

        CellEmitter.get(hero.pos).burst(ShadowParticle.CURSE, 8);
        Sample.INSTANCE.play(Assets.Sounds.CURSED);

        detach(hero.belongings.backpack);
        hero.spendAndNext(1f);
        Catalog.countUse(getClass());
        if (Random.Float() < talentChance){
            Talent.onScrollUsed(curUser, curUser.pos, talentFactor, getClass());
        }
    }

    @Override
    public int value() {
        return (int)(100 * (quantity/(float) Recipe.OUT_QUANTITY));
    }

    @Override
    public int energyVal() {
        return (int)(25 * (quantity/(float) Recipe.OUT_QUANTITY));
    }

    public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

        private static final int OUT_QUANTITY = 1;

        {
            inputs =  new Class[]{ScrollOfPsionicBlast.class, MetalShard.class};
            inQuantity = new int[]{1, 1};

            cost = 10;

            output = CorruptionSpell.class;
            outQuantity = OUT_QUANTITY;
        }

    }
}