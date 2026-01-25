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
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class DoorPlank extends Item {

    {
        defaultAction = AC_THROW;
        image = ItemSpriteSheet.DOOR_PLANK;
        stackable = true;
        bones = false;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    //being a demolisher is a little profitable
    @Override
    public int value() {
        return 15 * quantity;
    }

    @Override
    protected void onThrow( int cell ) {int c = Dungeon.level.map[cell];
        boolean built = false;
        if ( c == Terrain.EMPTY || c == Terrain.EMPTY_DECO
            || c == Terrain.EMBERS || c == Terrain.WATER
            || c == Terrain.GRASS || c == Terrain.HIGH_GRASS || c == Terrain.FURROWED_GRASS){

            for (int i = 0; i < PathFinder.CIRCLE4.length; i++) {
                if (Dungeon.level.solid[cell + PathFinder.CIRCLE4[i]]
                 && Dungeon.level.solid[cell - PathFinder.CIRCLE4[i]]
                 && Dungeon.level.passable[cell + PathFinder.CIRCLE4[(i+1) % 4]]
                 && Dungeon.level.passable[cell + PathFinder.CIRCLE4[(i+3) % 4]]){

                    Level.set(cell, Terrain.DOOR);
                    built = true;
                    GameScene.updateMap(cell);
                    if (Dungeon.level.heroFOV != null && Dungeon.level.heroFOV[cell]) {
                        Sample.INSTANCE.play(Assets.Sounds.BUILD, 1f, 1.1f);
                    }
                }
            }
        }
        if (!built) super.onThrow(cell);
    }
}