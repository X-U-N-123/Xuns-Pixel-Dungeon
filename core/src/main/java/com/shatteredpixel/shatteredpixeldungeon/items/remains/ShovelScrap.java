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

package com.shatteredpixel.shatteredpixeldungeon.items.remains;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;

public class ShovelScrap extends RemainsItem {

	{
		image = ItemSpriteSheet.SHOVEL_SCRAP;
	}

	@Override
	protected void doEffect(Hero hero) {
		Fire fire = (Fire) Dungeon.level.blobs.get(Fire.class);
		Splash.at( DungeonTilemap.tileCenterToWorld( hero.pos ), -PointF.PI/2, PointF.PI/2, 0x5bc1e3, 15, 0.02f);

		for (int i : PathFinder.NEIGHBOURS9){
			if (fire != null) fire.clear(hero.pos + i);
			Dungeon.level.setCellToWater(true, hero.pos + i);
			GameScene.updateMap(hero.pos + i);

			Char ch = Actor.findChar(hero.pos + i);
			if (ch != null){
				Buff.detach(ch, Burning.class);
				if (ch != hero){
					//trace a ballistica to our target (which will also extend past them)
					Ballistica trajectory = new Ballistica(hero.pos, ch.pos, Ballistica.STOP_TARGET);
					//trim it to just be the part that goes past them
					trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size() - 1), Ballistica.PROJECTILE);
					//knock them back along that ballistica
					WandOfBlastWave.throwChar(ch, trajectory, 2, true, true, hero);
				}
			}
		}
		Dungeon.observe();
		Sample.INSTANCE.play(Assets.Sounds.GAS, 1f, 0.75f);
	}
}
