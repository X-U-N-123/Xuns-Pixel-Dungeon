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
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BrokenArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Daze;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.MetalShard;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRetribution;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class RockFall extends TargetedSpell {

    {
        image = ItemSpriteSheet.ROCKFALL;

        usesTargeting = false;

        talentChance = 1/(float) Recipe.OUT_QUANTITY;
    }

    @Override
    protected void affectTarget(Ballistica bolt, Hero hero) {

        Sample.INSTANCE.play(Assets.Sounds.ROCKS);
        PixelScene.shake(5, 1.5f);

        for (int i : PathFinder.NEIGHBOURS9){
            CellEmitter.get( i + bolt.collisionPos ).burst( Speck.factory( Speck.ROCK ), 3 );
            Char ch = Actor.findChar(i + bolt.collisionPos);
            if (ch != null){
                int dmg = Random.NormalIntRange(5 + Dungeon.scalingDepth(), 10 + Dungeon.scalingDepth()*2);
                dmg *= 0.67f; //a little lower to a bomb's damage
                if (!ch.isImmune(RockFall.class)){
                    ch.damage(dmg, this);
                    if (ch.isAlive()){
                        Buff.affect(ch, Cripple.class, 8f);
                        Buff.affect(ch, Daze.class, 8f);
                        Buff.affect(ch, BrokenArmor.class, 8f);
                    } else if (ch == hero) {
                        Badges.validateDeathFromFriendlyMagic();
                        Dungeon.fail(this);
                        GLog.n(Messages.get(this, "ondeath"));
                    }
                }
            }
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

        private static final int OUT_QUANTITY = 4;

        {
            inputs =  new Class[]{ScrollOfRetribution.class, MetalShard.class};
            inQuantity = new int[]{1, 1};

            cost = 3;

            output = RockFall.class;
            outQuantity = OUT_QUANTITY;
        }

    }

    @Override
    public float weight(){
        return 0.1f * quantity() / Recipe.OUT_QUANTITY;
    }

}