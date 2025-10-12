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

package com.shatteredpixel.shatteredpixeldungeon.items.spells;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Awareness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class Scout extends Spell {
	
	{
		image = ItemSpriteSheet.SCOUT;

		talentChance = 1/(float) Recipe.OUT_QUANTITY;
	}

	protected void onCast(final Hero hero){

		Buff.affect(hero, Awareness.class, 0f);
		Dungeon.observe();
		GameScene.updateFog();
		Sample.INSTANCE.play(Assets.Sounds.SCAN);

		detach(hero.belongings.backpack);
		Catalog.countUse(getClass());
		if (Random.Float() < talentChance){
			Talent.onScrollUsed(curUser, curUser.pos, talentFactor, getClass());
		}
	}
	
	@Override
	public int value() {
		return (int)(60 * (quantity/(float) Recipe.OUT_QUANTITY));
	}

	@Override
	public int energyVal() {
		return (int)(15 * (quantity/(float) Recipe.OUT_QUANTITY));
	}
	
	public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

		private static final int OUT_QUANTITY = 5;
		
		{
			inputs =  new Class[]{ScrollOfForesight.class};
			inQuantity = new int[]{1};
			
			cost = 3;
			
			output = Scout.class;
			outQuantity = OUT_QUANTITY;
		}
		
	}

	@Override
	public float weight(){
		return 0.1f * quantity() / Recipe.OUT_QUANTITY;
	}
	
}
