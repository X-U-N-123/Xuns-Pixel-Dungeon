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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class RegrowingBrew extends Brew {

	{
		image = ItemSpriteSheet.BREW_REGROWING;

		talentChance = 1/(float) Recipe.OUT_QUANTITY;
	}

	@Override
	public void shatter(int cell) {

		Sample.INSTANCE.play(Assets.Sounds.PLANT, 1.3f);

		for (int i : PathFinder.NEIGHBOURS25) {
			if (Dungeon.level.map[i + cell] == Terrain.EMPTY
			|| Dungeon.level.map[i + cell] == Terrain.EMBERS
			|| Dungeon.level.map[i + cell] == Terrain.EMPTY_DECO
			|| Dungeon.level.map[i + cell] == Terrain.GRASS
			|| Dungeon.level.map[i + cell] == Terrain.FURROWED_GRASS){
				if (Dungeon.level.map[i + cell] != Terrain.FURROWED_GRASS){
					Level.set(i + cell, Terrain.GRASS);
				}
				if (Dungeon.level.distance(i+cell, cell) == 2 && Random.Int(2) > 0){
					Level.set(i + cell, Terrain.HIGH_GRASS);
					CellEmitter.get( cell + i ).burst( LeafParticle.LEVEL_SPECIFIC, 5 );
				} else if (Dungeon.level.distance(i+cell, cell) < 2){
					Level.set(i + cell, Terrain.HIGH_GRASS);
					CellEmitter.get( cell + i ).burst( LeafParticle.LEVEL_SPECIFIC, 5 );
				}
			}
			GameScene.updateMap(cell + i);
		}

		for (int i : PathFinder.NEIGHBOURS9){
			Char ch = Actor.findChar(cell + i);
			if (ch != null){
				Buff.prolong(ch, Roots.class, 3f);
			}
		}

	}

	@Override
	public int value() {
		return (int)(60 * (quantity/(float) Recipe.OUT_QUANTITY));
	}

	@Override
	public int energyVal() {
		return (int)(10 * (quantity/(float) Recipe.OUT_QUANTITY));
	}

	public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

		private static final int OUT_QUANTITY = 6;

		{
			inputs =  new Class[]{PotionOfHealing.class};
			inQuantity = new int[]{1};

			cost = 10;

			output = RegrowingBrew.class;
			outQuantity = OUT_QUANTITY;
		}

	}

}
