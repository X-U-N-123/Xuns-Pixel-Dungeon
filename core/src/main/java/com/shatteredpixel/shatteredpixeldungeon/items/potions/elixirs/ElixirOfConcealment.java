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

package com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfInvisibility;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class ElixirOfConcealment extends Elixir {

	{
		image = ItemSpriteSheet.ELIXIR_CONCEAL;
	}

	@Override
	public void apply(Hero hero) {
		Buff.affect(hero, Conceal.class, Conceal.DURATION);

		Sample.INSTANCE.play(Assets.Sounds.MELD, 0.6f);
		GLog.p(Messages.get(this, "conceal"));
	}

	@Override
	public void shatter(int cell) {
		Char ch = Actor.findChar(cell);

		if (ch == null){
			super.shatter(cell);
		} else {
			splash( cell );
			if (Dungeon.level.heroFOV[cell]) {
				Sample.INSTANCE.play(Assets.Sounds.SHATTER);
			}

			if (ch instanceof Hero) apply((Hero) ch);
			else                    Buff.affect(ch, Conceal.class, Conceal.DURATION);
		}
	}

	@Override
	public int value() {
		return (int)(100 * (quantity/(float) Recipe.OUT_QUANTITY));
	}

	@Override
	public int energyVal() {
		return (int)(14 * (quantity/(float) Recipe.OUT_QUANTITY));
	}

	public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

		private static final int OUT_QUANTITY = 1;

		{
			inputs =  new Class[]{PotionOfInvisibility.class};
			inQuantity = new int[]{1};

			cost = 8;

			output = ElixirOfConcealment.class;
			outQuantity = OUT_QUANTITY;
		}

	}

	public static class Conceal extends FlavourBuff {
		{
			type = buffType.POSITIVE;
		}

		public static final float DURATION	= 50f;

		@Override
		public int icon() {
			return BuffIndicator.IMBUE;
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (DURATION - visualcooldown()) / DURATION);
		}
	}
}