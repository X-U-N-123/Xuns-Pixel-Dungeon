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

package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Awareness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Identification;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Waterskin;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Shovel;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes.Landmark;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PointF;

public class WaterOfAwareness extends WellWater {

	@Override
	protected boolean affectHero( Hero hero ) {

		if (cur[hero.pos] == CUR_EMPTY) return false;
		
		Sample.INSTANCE.play( Assets.Sounds.DRINK );
		emitter.parent.add( new Identification( hero.sprite.center() ) );
		
		hero.belongings.observe();
		
		for (int i=0; i < Dungeon.level.length(); i++) {
			
			int terr = Dungeon.level.map[i];
			if ((Terrain.flags[terr] & Terrain.SECRET) != 0) {
				
				Dungeon.level.discover( i );

				Talent.secretForesightID(hero);
				
				if (Dungeon.level.heroFOV[i]) {
					GameScene.discoverTile( i, terr );
				}
			}
		}
		
		Buff.affect( hero, Awareness.class, Awareness.DURATION );
		Dungeon.observe();

		Dungeon.hero.interrupt();
	
		GLog.p( Messages.get(this, "procced") );
		
		return true;
	}
	
	@Override
	protected Item affectItem( Item item, int pos ) {

		if (item.isIdentified() || cur[pos] == CUR_EMPTY) {
			if (item instanceof Waterskin && cur[pos] == CUR_EMPTY){
				cur[pos] = 1;
				Shovel.ExplorerCooldown.affectCD(100, Dungeon.hero);
				Level.set(pos, Terrain.WELL);

				Splash.at( DungeonTilemap.tileCenterToWorld( pos ), -PointF.PI/2, PointF.PI/2, 0x5bc1e3, 10, 0.01f);
				Sample.INSTANCE.play(Assets.Sounds.GAS, 1f, 0.8f);
				GameScene.updateMap(pos);
				Dungeon.observe();
				Statistics.wellWaterDug ++;
			}
			return null;
		} else {
			ScrollOfIdentify.IDItem(item);
			
			Sample.INSTANCE.play( Assets.Sounds.DRINK );
			emitter.parent.add( new Identification( DungeonTilemap.tileCenterToWorld( pos ) ) );
			
			return item;
		}
	}
	
	@Override
	public Landmark landmark() {
		return Landmark.WELL_OF_AWARENESS;
	}
	
	@Override
	public void use( BlobEmitter emitter ) {
		super.use( emitter );
		emitter.pour( Speck.factory( Speck.QUESTION ), 0.3f );
	}
	
	@Override
	public String tileDesc() {
		return Messages.get(this, "desc");
	}
}
