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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.Transmuting;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.MetalShard;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class Extract extends InventorySpell {
	
	{
		image = ItemSpriteSheet.EXTRACT;

		talentFactor = 2;
		talentChance = 1;
	}

	@Override
	protected boolean usableOnItem(Item item) {
		return item.isUpgradable() && item.trueLevel() > 0 &&
		!(item instanceof Artifact || item instanceof Trinket || item instanceof SpiritBow);
	}

	@Override
	protected void onItemSelected(Item item) {
		Item result;
		int toget = item.trueLevel();
		if (item instanceof Armor && ((Armor)item).checkSeal() != null && ((Armor)item).checkSeal().level() > 0){
			toget --;
		}
		item.degrade(toget);
		result = new ScrollOfUpgrade().quantity(toget + 1);

		GLog.p(Messages.get(this, "extracted", toget + 1));
		if (!result.collect()){
			Dungeon.level.drop(result, curUser.pos).sprite.drop();
		}
		Transmuting.show(curUser, item, result);
		curUser.sprite.emitter().start(Speck.factory(Speck.CHANGE), 0.2f, 10);
	}
	
	@Override
	public int value() {
		return 125 * quantity;
	}

	@Override
	public int energyVal() {
		return 25 * quantity;
	}
	
	public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

		private static final int OUT_QUANTITY = 1;
		
		{
			inputs =  new Class[]{ScrollOfUpgrade.class, MetalShard.class};
			inQuantity = new int[]{1, 1};
			
			cost = 12;
			
			output = Extract.class;
			outQuantity = OUT_QUANTITY;
		}
		
	}
}
