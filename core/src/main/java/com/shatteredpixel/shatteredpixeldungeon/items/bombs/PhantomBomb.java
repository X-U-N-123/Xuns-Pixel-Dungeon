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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class PhantomBomb extends Bomb {

	public int generation = 1;

	{
		image = ItemSpriteSheet.PHANTOM_BOMB;
	}

	@Override
	protected int explosionRange() {
		return 2;
	}

	@Override
	protected Fuse createFuse() {
		return new PhantomFuse();
	}

	@Override
	public void explode(int cell) {
		super.explode(cell);

		if (generation < 3){

			int bombPos = -1;

			for (Mob m : Dungeon.level.mobs.toArray(new Mob[0])){
				if (new Ballistica(cell, m.pos, Ballistica.MAGIC_BOLT | Ballistica.IGNORE_SOFT_SOLID).collisionPos == m.pos
						&& m.alignment == Char.Alignment.ENEMY && bombPos != cell){
					bombPos = m.pos;
					break;
				}
			}

			if (bombPos == -1){
				bombPos = Dungeon.level.randomDestination(new Char(){} );
			}

			if (bombPos != -1) {
				PhantomBomb subBomb = new PhantomBomb();
				subBomb.generation = this.generation + 1;
				lightingFuse = true;
				subBomb.onThrow(bombPos);
			}

			Sample.INSTANCE.playDelayed( Assets.Sounds.MISS, 0.1f);
		}
	}

	@Override
	public boolean doPickUp(Hero hero, int pos) {
		if (generation > 1){
			GLog.w( Messages.get(this, "cant_pickup") );
			return false;
		} else return super.doPickUp(hero, pos);
	}

	@Override
	public String desc() {
		int depth = Dungeon.hero == null ? 1 : Dungeon.scalingDepth();
		String desc = "";
		if (generation > 1) desc += Messages.get(this, "desc_phantom");
		desc += Messages.get(this, "desc", 4+depth, 12+3*depth);
		if (fuse == null) {
			return desc + "\n\n" + Messages.get(this, "desc_fuse");
		} else {
			return desc + "\n\n" + Messages.get(this, "desc_burning");
		}
	}

	@Override
	public int value() {
		//prices of ingredients
		return quantity * (20 + 30);
	}

	public static class PhantomFuse extends Fuse {
		@Override
		public boolean freeze() {
			if (((PhantomBomb)bomb).generation <= 1) {
				return super.freeze();
			} else {
				//sub-phantombombs cannot have their fuse snuffed
				return false;
			}
		}
	}

	private static final String GEN = "generation";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put(GEN, generation );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		generation = bundle.getInt(GEN);
	}
}