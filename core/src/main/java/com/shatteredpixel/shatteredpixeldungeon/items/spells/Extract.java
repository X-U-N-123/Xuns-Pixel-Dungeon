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
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.Transmuting;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.MetalShard;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.ArrayList;

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
	protected void onCast(Hero hero) {
		if (Math.pow(1.2f, Statistics.extractUsed)-1f >= Random.Float()){
			Statistics.extractUsed++;
			GLog.n(Messages.get(this, "shattered"));
			Sample.INSTANCE.play( Assets.Sounds.SHATTER);
			detach(hero.belongings.backpack);
			return;
		}
		GameScene.selectItem( itemSelector );
	}

	@Override
	protected void onItemSelected(Item item) {
		Item result;
		int toget = item.trueLevel();
		if (item instanceof Armor && ((Armor)item).checkSeal() != null && ((Armor)item).checkSeal().level() > 0){
			toget --;
		}
		if (toget > 2) toget = 2;
		if (item.unique) item.degrade(toget);
		else {
			if (item instanceof Armor && ((Armor) item).checkSeal() != null){
				item.execute(Dungeon.hero, Armor.AC_DETACH);
			}
			item.detach(Dungeon.hero.belongings.backpack);
			if (Dungeon.hero.belongings.weapon == item || Dungeon.hero.belongings.secondWep == item){
				((KindOfWeapon) item).doUnequip(Dungeon.hero, false);
			} else if (Dungeon.hero.belongings.armor == item) {
				((Armor) item).doUnequip(Dungeon.hero, false);
			} else if (Dungeon.hero.belongings.ring == item || Dungeon.hero.belongings.misc == item) {
				((Ring) item).doUnequip(Dungeon.hero, false);
			}
		}
		Statistics.extractUsed++;
		result = new ScrollOfUpgrade().quantity(toget + 1);

		GLog.p(Messages.get(this, "extracted", toget + 1));
		if (!result.collect()){
			Dungeon.level.drop(result, curUser.pos).sprite.drop();
		}
		Transmuting.show(curUser, item, result);
		curUser.sprite.emitter().start(Speck.factory(Speck.CHANGE), 0.2f, 10);
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", Statistics.extractUsed);
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

		@Override
		public Item brew(ArrayList<Item> ingredients) {
			Catalog.countUse(MetalShard.class);
			return super.brew(ingredients);
		}
		
	}

	@Override
	public float weight(){
		return 0.1f * quantity() / Recipe.OUT_QUANTITY;
	}
}
