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
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class RegrowingBrew extends Brew {

	{
		image = ItemSpriteSheet.BREW_REGROWING;

		talentChance = 1/(float) Recipe.OUT_QUANTITY;
	}

	@Override
	public void shatter(int cell) {
		Splash.at( DungeonTilemap.tileCenterToWorld( cell ), -PointF.PI/2, PointF.PI/2, 0x2ee62e, 10, 0.01f);
		Sample.INSTANCE.play(Assets.Sounds.PLANT, 1.4f);

		PathFinder.buildDistanceMap( cell, BArray.not( Dungeon.level.solid, null ), 2 );
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] == 2 && Random.Int(3) > 0){
				GrowGrass(i);
			} else if (PathFinder.distance[i] < 2){
				GrowGrass(i);
			}
		}

		for (int i : PathFinder.NEIGHBOURS8){
			Char ch = Actor.findChar(cell + i);
			if (ch != null && ch.isAlive()){
				Buff.prolong(ch, Roots.class, 4f);
			}
		}

		Char ch = Actor.findChar(cell);
		if (ch != null && ch.isAlive()){
			Buff.prolong(ch, Roots.class, 6f);
		}

	}

	public boolean GrowGrass(int cell) {
		Point p = Dungeon.level.cellToPoint(cell);

		//if a custom tilemap is over that cell, don't grow plants there
		for (CustomTilemap cust : Dungeon.level.customTiles){
			Point custPoint = new Point(p);
			custPoint.x -= cust.tileX;
			custPoint.y -= cust.tileY;
			if (custPoint.x >= 0 && custPoint.y >= 0
			&& custPoint.x < cust.tileW && custPoint.y < cust.tileH){
				if (cust.image(custPoint.x, custPoint.y) != null){
					return false;
				}
			}
		}

		if (Dungeon.level.map[cell] == Terrain.EMPTY
		|| Dungeon.level.map[cell] == Terrain.EMBERS
		|| Dungeon.level.map[cell] == Terrain.EMPTY_DECO
		|| Dungeon.level.map[cell] == Terrain.GRASS
		|| Dungeon.level.map[cell] == Terrain.FURROWED_GRASS){
			Level.set(cell, Terrain.HIGH_GRASS);
			CellEmitter.get(cell).burst( LeafParticle.LEVEL_SPECIFIC, 5 );
			GameScene.updateMap(cell);
			return true;
		}

		return false;

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

	@Override
	public float weight(){
		return 0.1f * quantity() / Recipe.OUT_QUANTITY;
	}

}
