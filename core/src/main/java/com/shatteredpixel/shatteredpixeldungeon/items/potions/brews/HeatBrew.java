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

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.scalingDepth;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfDragonsBreath;
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

public class HeatBrew extends Brew {

	{
		image = ItemSpriteSheet.BREW_HEAT;

		talentChance = 1/(float) Recipe.OUT_QUANTITY;
	}

	@Override
	public void shatter(int cell) {
		Splash.at( DungeonTilemap.tileCenterToWorld( cell ), -PointF.PI/2, PointF.PI/2, 0xf38b24, 10, 0.01f);
		Sample.INSTANCE.play(Assets.Sounds.BURNING);

		Freezing freezing = (Freezing) Dungeon.level.blobs.get(Freezing.class);
		PathFinder.buildDistanceMap( cell, BArray.not( Dungeon.level.solid, null ), 2 );
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] == 2 && Random.Int(3) > 0){
				HeatTile(i);
				if (freezing != null){
					freezing.clear(i);
				}
			} else if (PathFinder.distance[i] < 2){
				HeatTile(i);
				if (freezing != null){
					freezing.clear(i);
				}
			}
		}

		for (int i : PathFinder.NEIGHBOURS8){
			Char ch = Actor.findChar(cell + i);
			if (ch != null){

				//does the equivalent of a bomb's damage against icy enemies.
				if (Char.hasProp(ch, Char.Property.ICY)){
					int dmg = Random.NormalIntRange(5 + scalingDepth(), 10 + scalingDepth()*2);
					dmg *= 0.67f;
					if (!ch.isImmune(HeatBrew.class)){
						ch.damage(dmg, this);
					}
				}

				if (ch.isAlive()) {
					if (ch.buff(Chill.class) != null){
						ch.buff(Chill.class).detach();
					}
					if (ch.buff(Frost.class) != null){
						ch.buff(Frost.class).detach();
					}
					Buff.prolong(ch, Blindness.class, 4f);
				}
			}
		}

		Char ch = Actor.findChar(cell);
		if (ch != null){

			//does the equivalent of a bomb's damage against icy enemies.
			if (Char.hasProp(ch, Char.Property.ICY)){
				int dmg = Random.NormalIntRange(5 + scalingDepth(), 10 + scalingDepth()*2);
				if (!ch.isImmune(HeatBrew.class)){
					ch.damage(dmg, this);
				}
			}

			if (ch.isAlive()) {
				if (ch.buff(Chill.class) != null){
					ch.buff(Chill.class).detach();
				}
				if (ch.buff(Frost.class) != null){
					ch.buff(Frost.class).detach();
				}
				Buff.prolong(ch, Blindness.class, 6f);
			}
		}

	}

	public boolean HeatTile(int cell) {
		Point p = Dungeon.level.cellToPoint(cell);

		//if a custom tilemap is over that cell, don't heat there
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

		if ((Terrain.flags[Dungeon.level.map[cell]] & Terrain.FLAMABLE) != 0){
			Level.set(cell, Terrain.EMBERS);
			GameScene.updateMap(cell);
			return true;
		}
		if (Dungeon.level.map[cell] == Terrain.WATER){
			Level.set(cell, Terrain.EMPTY);
			CellEmitter.get(cell).burst( Speck.factory( Speck.STEAM ), 5 );
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
		return (int)(12 * (quantity/(float) Recipe.OUT_QUANTITY));
	}

	public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

		private static final int OUT_QUANTITY = 8;

		{
			inputs =  new Class[]{PotionOfDragonsBreath.class};
			inQuantity = new int[]{1};

			cost = 8;

			output = HeatBrew.class;
			outQuantity = OUT_QUANTITY;
		}

	}

	@Override
	public float weight(){
		return 0.1f * quantity() / Recipe.OUT_QUANTITY;
	}

}
