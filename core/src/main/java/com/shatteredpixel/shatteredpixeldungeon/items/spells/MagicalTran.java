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
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.Transmuting;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.MetalShard;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Pickaxe;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.levels.MiningLevel;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;

public class MagicalTran extends InventorySpell{

    {
        image = ItemSpriteSheet.MAGICAL_TRAN;

        talentFactor = 2;
        talentChance = 1;
    }

    @Override
    protected boolean usableOnItem(Item item) {
        //all melee weapons, except pickaxe when in a mining level
        if (item instanceof MeleeWeapon){
            return !(item instanceof Pickaxe && Dungeon.level instanceof MiningLevel);

            //all missile weapons except darts
        } else if (item instanceof MissileWeapon){
            return !(item instanceof Dart);

            //all rings, wands, trinkets
        } else {
            return item instanceof Ring || item instanceof Wand || item instanceof Trinket;
        }
    }

    @Override
    protected void onItemSelected(Item item) {
        boolean isSimilar;
        Item result1 = changeItem(item);
        Item result2;
        do {
            result2 = changeItem(item);
            if (item instanceof MagesStaff){
                isSimilar = ((MagesStaff)result1).wandClass() == ((MagesStaff)result2).wandClass();
            } else {
                isSimilar = result1.getClass() == result2.getClass();
            }
        } while (isSimilar);
        Item result3;
        do {
            result3 = changeItem(item);
            if (item instanceof MagesStaff){
                isSimilar = ((MagesStaff)result3).wandClass() == ((MagesStaff)result2).wandClass()
                || ((MagesStaff)result3).wandClass() == ((MagesStaff)result1).wandClass();
            } else {
                isSimilar = result3.getClass() == result2.getClass() || result3.getClass() == result1.getClass();
            }
        } while (isSimilar);

        Item Result2 = result2;
        Item Result3 = result3;

        GameScene.show(new WndOptions(new ItemSprite(this),
            Messages.get(this, "title"),
            Messages.get(this, "which"),
            result1.name(), result2.name(), result3.name()){
            @Override
            protected void onSelect(int index) {
                Item fItem = null;
                switch (index){
                    case 0:
                        fItem = result1;
                        break;
                    case 1:
                        fItem = Result2;
                        break;
                    case 2:
                        fItem = Result3;
                        break;
                }

                if (fItem != item) {
                    int slot = Dungeon.quickslot.getSlot(item);
                    if (item.isEquipped(Dungeon.hero)) {
                        item.cursed = false; //to allow it to be unequipped
                        if (item instanceof KindOfWeapon && Dungeon.hero.belongings.secondWep() == item){
                            ((EquipableItem) item).doUnequip(Dungeon.hero, false);
                            ((KindOfWeapon) fItem).equipSecondary(Dungeon.hero);
                        } else {
                            ((EquipableItem) item).doUnequip(Dungeon.hero, false);
                            item.detach(Dungeon.hero.belongings.backpack);
                            ((EquipableItem) fItem).doEquip(Dungeon.hero);
                        }
                        Dungeon.hero.spend(-Dungeon.hero.cooldown()); //cancel equip/unequip time
                    } else {
                        item.detach(Dungeon.hero.belongings.backpack);
                        if (!fItem.collect()) {
                            Dungeon.level.drop(fItem, curUser.pos).sprite.drop();
                        } else if (fItem.stackable && Dungeon.hero.belongings.getSimilar(fItem) != null){
                            fItem = Dungeon.hero.belongings.getSimilar(fItem);
                        }
                    }
                    if (slot != -1
                    && fItem.defaultAction() != null
                    && !Dungeon.quickslot.isNonePlaceholder(slot)
                    && Dungeon.hero.belongings.contains(fItem)){
                        Dungeon.quickslot.setSlot(slot, fItem);
                    }
                }
                if (fItem.isIdentified()){
                    Catalog.setSeen(fItem.getClass());
                    Statistics.itemTypesDiscovered.add(fItem.getClass());
                }
                Transmuting.show(curUser, item, fItem);
                curUser.sprite.emitter().start(Speck.factory(Speck.CHANGE), 0.2f, 10);
                GLog.p( Messages.get(this, "morph"), fItem.name());
                updateQuickslot();
            }

            @Override
            public void onBackPressed() {
                //do nothing
            }
        });
    }

    public static Item changeItem( Item item ){
        if (item instanceof MagesStaff) {
            return ScrollOfTransmutation.changeStaff((MagesStaff) item);
        } else if (item instanceof MeleeWeapon || item instanceof MissileWeapon) {
            return ScrollOfTransmutation.changeWeapon( (Weapon)item );
        } else if (item instanceof Ring) {
            return ScrollOfTransmutation.changeRing( (Ring)item );
        } else if (item instanceof Wand) {
            return ScrollOfTransmutation.changeWand( (Wand)item );
        } else if (item instanceof Trinket) {
            return ScrollOfTransmutation.changeTrinket( (Trinket)item );
        } else {
            return null;
        }
    }

    @Override
    public int value() {
        return 105 * quantity;
    }

    @Override
    public int energyVal() {
        return 21 * quantity;
    }

    public static class Recipe extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe.SimpleRecipe {

        private static final int OUT_QUANTITY = 1;

        {
            inputs =  new Class[]{ScrollOfTransmutation.class, MetalShard.class};
            inQuantity = new int[]{1, 1};

            cost = 8;

            output = MagicalTran.class;
            outQuantity = OUT_QUANTITY;
        }

    }
}
