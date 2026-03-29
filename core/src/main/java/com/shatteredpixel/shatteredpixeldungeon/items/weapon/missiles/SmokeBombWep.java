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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SmokeScreen;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class SmokeBombWep extends MissileWeapon {

	{
		image = ItemSpriteSheet.SMOKEBOMB;
		hitSound = Assets.Sounds.BLAST;
		hitSoundPitch = 1f;

		tier = 3;
		baseUses = 5;

		sticky = false;
	}

	@Override
	public int max(int lvl) {
		return  3 * tier +                      //9 base, down from 15
				(tier == 1 ? 2*lvl : tier*lvl); //scaling unchanged
	}

	@Override
	public void hitSound(float pitch) {
		//no hitsound as it never hits enemies directly
	}

	@Override
	protected void onThrow(int cell) {

		if (Dungeon.level.pit[cell]){
			super.onThrow(cell);
			return;
		}

		Char target = Actor.findChar(cell);
		if (target != null && target.alignment == Char.Alignment.ENEMY) {
			rangedHit( target, cell );
			curUser.shoot(target, this);
			if (target instanceof Mob && ((Mob) target).state == ((Mob) target).HUNTING) ((Mob) target).clearEnemy();

		} else rangedHit( null, cell );

		Dungeon.level.pressCell(cell);

		int centerVolume = 20;
		for (int i : PathFinder.NEIGHBOURS8){
			if (!Dungeon.level.solid[cell+i]){
				GameScene.add( Blob.seed( cell+i, 20, SmokeScreen.class ) );
			} else {
				centerVolume += 20;
			}
		}
		GameScene.add( Blob.seed( cell, centerVolume, SmokeScreen.class ) );

		thrownEvilProc(cell);

		WandOfBlastWave.BlastWave.blast(cell);
		Sample.INSTANCE.play( Assets.Sounds.BLAST );
	}
}