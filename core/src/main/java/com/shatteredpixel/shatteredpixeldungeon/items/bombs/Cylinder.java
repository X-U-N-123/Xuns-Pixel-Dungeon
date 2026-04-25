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

package com.shatteredpixel.shatteredpixeldungeon.items.bombs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;

public class Cylinder extends Bomb {
	
	{
		image = ItemSpriteSheet.CYLINDER;
	}

	@Override
	public void explode(int cell) {

		int centerVolume = 500; //50*9+50
		PathFinder.buildDistanceMap( cell, BArray.not( Dungeon.level.solid, null ), explosionRange() );
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] < Integer.MAX_VALUE) {
				GameScene.add( Blob.seed( i, 50, Hydrogen.class ) );
				centerVolume -= 50;
			}
		}

		//excess volume if some cells were blocked
		if (centerVolume > 0){
			GameScene.add( Blob.seed( cell, centerVolume, Hydrogen.class ) );
		}

		Sample.INSTANCE.play(Assets.Sounds.GAS);
	}

	@Override
	public int value() {
		//prices of ingredients
		return quantity * (20 + 40);
	}

	public static class Hydrogen extends Blob {

		@Override
		protected void evolve() {
			super.evolve();

			int cell;

			for (int i = area.left; i < area.right; i++){
				for (int j = area.top; j < area.bottom; j++){
					cell = i + j*Dungeon.level.width();
					
					Char ch = Actor.findChar(cell);
					Blob fire = Dungeon.level.blobs.get(Fire.class);
					
					if (volume > 0 && cur[cell] > 0
							&& (Char.hasProp(ch, Char.Property.FIERY) || (ch != null && ch.buff(Burning.class) != null)
							|| (fire != null && fire.volume > 0 && fire.cur[cell] > 0)))
						explode(cell);
				}
			}
		}

		public void explode(int pos){
			if (volume == 0 || cur[pos] <= 0) return;
			clear(pos);
			new Bomb().explode(pos);
		}

		@Override
		public void use( BlobEmitter emitter ) {
			super.use( emitter );

			emitter.pour( Speck.factory( Speck.HYDROGEN ), 0.4f );
		}

		@Override
		public String tileDesc() {
			return Messages.get(this, "desc");
		}
	}
}