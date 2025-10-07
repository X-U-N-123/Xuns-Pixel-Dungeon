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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHaste;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.GooBlob;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class ElixirOfOverdraft extends Elixir {
	
	{
		image = ItemSpriteSheet.ELIXIR_OVERDRAFT;
	}
	
	@Override
	public void apply(Hero hero) {
		Buff.affect(hero, OverdraftTracker.class, 0f);
		hero.spendConstant(-8f);
		Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
	}

	@Override
	public void shatter(int cell) {
		Char ch = Actor.findChar(cell);

		if (ch == null){
			super.shatter(cell);
		} else {
			splash( cell );

			Buff.affect(ch, OverdraftTracker.class, 0f);
			ch.spendConstant(-8f);
			Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
		}
	}
	
	public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {
		
		{
			inputs =  new Class[]{PotionOfHaste.class, GooBlob.class};
			inQuantity = new int[]{1, 1};
			
			cost = 16;
			
			output = ElixirOfOverdraft.class;
			outQuantity = 1;
		}

		@Override
		public Item brew(ArrayList<Item> ingredients) {
			Catalog.countUse(GooBlob.class);
			return super.brew(ingredients);
		}
	}

	public static class OverdraftTracker extends FlavourBuff{

		@Override
		public void detach(){
			super.detach();
			Buff.affect(target, Slow.class, 14f);
			Sample.INSTANCE.play(Assets.Sounds.DEGRADE);
		}
	}
}
