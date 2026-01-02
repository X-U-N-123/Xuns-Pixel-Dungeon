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

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.Transmuting;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.MetalShard;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Reflection;

import java.util.ArrayList;

public class Uptier extends InventorySpell {

    {
        image = ItemSpriteSheet.UPTIER;

        unique = true;

        talentFactor = 2;
        talentChance = 1;

        preferredBag = Belongings.Backpack.class;
    }

    @Override
    protected boolean usableOnItem(Item item) {
        return ((item instanceof Weapon && ((Weapon)item).tier < 6) || (item instanceof Armor && ((Armor)item).tier < 6))
        && !item.unique;
    }

    @Override
    protected void onItemSelected( Item item ){

        EquipableItem result = null;

        if (item instanceof ClassArmor){//change hero armor
            ((Armor)item).tier ++;
            curUser.sprite.emitter().start(Speck.factory(Speck.CHANGE), 0.2f, 10);
            return;
        } else if (item instanceof Armor){//change armor

            result = (Armor) Reflection.newInstance(Generator.Category.ARMOR.classes[((Armor) item).tier]);

            result.level(0);
            int level = item.trueLevel();
            if (level > 0) {
                result.upgrade( level );
            } else if (level < 0) {
                result.degrade( -level );
            }

            ((Armor) result).glyph = ((Armor) item).glyph;
            ((Armor) result).curseInfusionBonus = ((Armor) item).curseInfusionBonus;
            ((Armor) result).masteryPotionBonus = ((Armor) item).masteryPotionBonus;
            ((Armor) result).augment = ((Armor) item).augment;
            ((Armor) result).glyphHardened = ((Armor) item).glyphHardened;

        } else if (item instanceof Weapon) {//change weapon

            Generator.Category cat = Generator.wepTiers[((Weapon)item).tier];

            do {
                result = (Weapon)Generator.randomUsingDefaults(cat);
            } while (Challenges.isItemBlocked(result));

            result.level(0);
            int level = item.trueLevel();
            if (level > 0) {
                result.upgrade( level );
            } else if (level < 0) {
                result.degrade( -level );
            }

            ((Weapon) result).enchantment = ((Weapon) item).enchantment;
            ((Weapon) result).curseInfusionBonus = ((Weapon) item).curseInfusionBonus;
            ((Weapon) result).masteryPotionBonus = ((Weapon) item).masteryPotionBonus;
            ((Weapon) result).augment = ((Weapon) item).augment;
            ((Weapon) result).enchantHardened = ((Weapon) item).enchantHardened;
        }

        result.levelKnown = item.levelKnown;
        result.cursedKnown = item.cursedKnown;
        result.cursed = item.cursed;

        if (result != item) {
            int slot = Dungeon.quickslot.getSlot(item);
            if (item.isEquipped(Dungeon.hero)) {
                item.cursed = false; //to allow it to be unequipped
                if (item instanceof KindOfWeapon && Dungeon.hero.belongings.secondWep() == item){
                    ((EquipableItem) item).doUnequip(Dungeon.hero, false);
                    ((KindOfWeapon) result).equipSecondary(Dungeon.hero);
                } else {
                    ((EquipableItem) item).doUnequip(Dungeon.hero, false);
                    result.doEquip(Dungeon.hero);
                }
                Dungeon.hero.spend(-Dungeon.hero.cooldown()); //cancel equip/unequip time
            } else {
                item.detach(Dungeon.hero.belongings.backpack);
                if (!result.collect()) {
                    Dungeon.level.drop(result, curUser.pos).sprite.drop();
                }
            }
            if (slot != -1
            && result.defaultAction() != null
            && !Dungeon.quickslot.isNonePlaceholder(slot)
            && Dungeon.hero.belongings.contains(result)){
                Dungeon.quickslot.setSlot(slot, result);
            }
        }
        if (result.isIdentified()){
            Catalog.setSeen(result.getClass());
            Statistics.itemTypesDiscovered.add(result.getClass());
        }
        Transmuting.show(curUser, item, result);
        curUser.sprite.emitter().start(Speck.factory(Speck.CHANGE), 0.2f, 10);
        GLog.p( Messages.get(this, "uptier", result.name()) );
    }

    @Override
    public int value() {
        return 100 * quantity;
    }

    @Override
    public int energyVal() {
        return 28 * quantity;
    }

    public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

        {
            inputs =  new Class[]{ScrollOfUpgrade.class, MetalShard.class};
            inQuantity = new int[]{1, 1};

            cost = 15;

            output = Uptier.class;
            outQuantity = 1;
        }

        @Override
        public Item brew(ArrayList<Item> ingredients) {
            Catalog.countUse(MetalShard.class);
            return super.brew(ingredients);
        }

    }

}