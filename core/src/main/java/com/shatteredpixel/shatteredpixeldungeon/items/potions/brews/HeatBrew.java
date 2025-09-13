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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfDragonsBreath;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.BlazingTrap;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class HeatBrew extends Brew {

	{
		image = ItemSpriteSheet.BREW_HEAT;

		talentChance = 1/(float) Recipe.OUT_QUANTITY;
	}

	@Override
	public void shatter(int cell) {

		Sample.INSTANCE.play(Assets.Sounds.BURNING);

		for (int i : PathFinder.NEIGHBOURS25) {
			if ((Terrain.flags[Dungeon.level.map[i + cell]] & Terrain.FLAMABLE) != 0){
				if (Dungeon.level.distance(i+cell, cell) == 2 && Random.Int(3) > 0){
					Level.set(i + cell, Terrain.EMBERS);
				} else if (Dungeon.level.distance(i+cell, cell) < 2){
					Level.set(i + cell, Terrain.EMBERS);
				}
			}
			if (Dungeon.level.map[i + cell] == Terrain.WATER){
				if (Dungeon.level.distance(i + cell, cell) == 2 && Random.Int(3) > 0){
					Level.set(i + cell, Terrain.EMPTY);
					CellEmitter.get(i + cell).burst( Speck.factory( Speck.STEAM ), 5 );
				} else if (Dungeon.level.distance(i+cell, cell) < 2){
					Level.set(i + cell, Terrain.EMPTY);
					CellEmitter.get(i + cell).burst( Speck.factory( Speck.STEAM ), 5 );
				}
			}
			GameScene.updateMap(cell + i);
		}

		for (int i : PathFinder.NEIGHBOURS9){
			Char ch = Actor.findChar(cell + i);
			if (ch != null){

				Buff buff = ch.buff(Chill.class);
				if (buff != null) buff.detach();
				buff = ch.buff(Frost.class);
				if (buff != null) buff.detach();

				//does the equivalent of a bomb's damage against icy enemies.
				if (Char.hasProp(ch, Char.Property.ICY)){
					int dmg = Random.NormalIntRange(5 + scalingDepth(), 10 + scalingDepth()*2);
					dmg *= 0.67f;
					if (!ch.isImmune(BlazingTrap.class)){
						ch.damage(dmg, this);
					}
				}

				if (ch.isAlive()) {
					Buff.prolong(ch, Blindness.class, 4f);
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
