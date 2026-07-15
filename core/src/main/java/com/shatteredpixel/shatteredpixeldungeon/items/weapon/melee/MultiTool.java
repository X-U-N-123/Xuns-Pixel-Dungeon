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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.PhysicalEmpower;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.engineer.ForceField;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Transmuting;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.MetalPart;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class MultiTool extends MeleeWeapon {

    public static final String AC_DISARM = "disarm";
    public static final String AC_SMELTT =  "smeltt";

    public Armor.Modification armorModify = null;
    private int armorModDura = 0;
    public ConductiveLoot loot = null;
    public Wand.Modification  wandModify  = null;
    private int wandModDura = 0;

    {
        image = ItemSpriteSheet.TOOL;
        hitSound = Assets.Sounds.HIT_STAB;
        hitSoundPitch = 1.1f;

        tier = 1;

        unique = true;
        bones = false;
    }

    @Override
    public int max(int lvl) {
        return  Math.round(2.5f*(tier+1)) +    //5 base, down from 10
                lvl*(tier);                  //+1 scaling, down from +2
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (defender.buff(Knife.Cutabilitytracker.class) == null)
            Buff.affect(defender, Bleeding.class).set( augment.damageFactor((min() + 1) * Random.NormalFloat(1, 1.5f)) );

        return super.proc( attacker, defender, damage );
    }

    @Override
    public String statsInfo(){
        if (isIdentified()){
            return Messages.get(this, "stats_desc",
                    Math.round(augment.damageFactor(min() + 1)),
                    Math.round(augment.damageFactor((min() + 1) * 1.5f)));
        } else {
            return Messages.get(this, "typical_stats_desc",
                    Math.round(augment.damageFactor(min(0) + 1)),
                    Math.round(augment.damageFactor((min(0) + 1) * 1.5f)));
        }
    }

    @Override
    public String upgradeStat(int level){
        return Math.round(augment.damageFactor(min(level) + 1)) + "-" +
                Math.round(augment.damageFactor((min(level) + 1) * 1.5f));
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions( hero );
        actions.add(AC_DISARM);
        if (hero.pointsInTalent(Talent.APART_ANYTHING) >= 2) actions.add(AC_SMELTT);
        return actions;
    }

    @Override
    public String defaultAction() {
        return AC_DISARM;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_DISARM)){
            if (Dungeon.level.map[hero.pos] == Terrain.INACTIVE_TRAP ||
                    (Dungeon.level.map[hero.pos] == Terrain.PEDESTAL && hero.hasTalent(Talent.APART_ANYTHING))){

                //if a custom tilemap is over that cell, then no trap here (mostly in DM-300 level)
                for (CustomTilemap cust : Dungeon.level.customTiles){
                    Point custPoint = new Point(Dungeon.level.cellToPoint(hero.pos));
                    custPoint.x -= cust.tileX;
                    custPoint.y -= cust.tileY;
                    if (custPoint.x >= 0 && custPoint.y >= 0
                            && custPoint.x < cust.tileW && custPoint.y < cust.tileH){
                        if (cust.image(custPoint.x, custPoint.y) != null){
                            GLog.w(Messages.get(this, "no_trap"));
                            return;
                        }
                    }
                }
                Level.set(hero.pos, Terrain.EMPTY);
                Dungeon.level.traps.remove( hero.pos );
                GameScene.updateMap( hero.pos );

                MetalPart part = new MetalPart();
                if (hero.subClass == HeroSubClass.CRAFTSMAN) part.quantity(part.quantity() + 1);

                if (!part.collect()) Dungeon.level.drop(part, hero.pos).sprite.drop();

                hero.sprite.operate( hero.pos );
                Sample.INSTANCE.play(Assets.Sounds.UNLOCK);
                hero.spendAndNext(2f);

                if (hero.hasTalent(Talent.GENERAL_DISARM))
                    Buff.affect(hero, PhysicalEmpower.class).set(1 + hero.pointsInTalent(Talent.GENERAL_DISARM), 3);

            } else GLog.w(Messages.get(this, "no_trap"));
        }
        if (action.equals(AC_SMELTT)){
            GameScene.selectItem(new WndBag.ItemSelector() {

                @Override
                public String textPrompt() {
                    return Messages.get(this, "tosmelt");
                }

                @Override
                public boolean itemSelectable(Item item) {
                    return (item instanceof MeleeWeapon || (item instanceof Armor))
                            && !item.unique && !item.cursed && item.cursedKnown && !(item instanceof ClassArmor)
                            && !item.isEquipped(hero);
                }

                @Override
                public void onSelect( Item item ) {
                    if (item == null) return;

                    item.detach(hero.belongings.backpack);
                    hero.sprite.operate(hero.pos);
                    MetalPart part = new MetalPart();
                    part.quantity(item.level() + 1);
                    if (!part.collect()) Dungeon.level.drop(part, hero.pos).sprite.drop();
                    Sample.INSTANCE.play(Assets.Sounds.EVOKE);
                }
            });
        }
    }

    @Override
    public String info() {
        String info = super.info();

        if (armorModify != null){
            info += "\n\n" + Messages.get(Armor.class, "has_modify", armorModify.title(), armorModDura) + armorModify.desc();
        }
        if (wandModify != null){
            info += "\n\n" + Messages.get(Wand.class, "has_modify", wandModify.title(), wandModDura) + wandModify.desc();
        }

        return info;
    }

    public void decreaseArmorModDura(){
        if (Dungeon.hero.buff(ForceField.Field.class) == null
                || Random.Int(4) >= Dungeon.hero.pointsInTalent(Talent.REPAIR_ABILITY))
            armorModDura = Math.max(armorModDura - 1, 0);
        if (armorModDura <= 0) modify((Armor.Modification) null);
    }

    public void modify(Armor.Modification mod){
        boolean activeRepair = armorModify == mod && Dungeon.hero.hasTalent(Talent.ACTIVE_REPAIR);
        armorModify = mod;
        if (mod == null){
            armorModDura = 0;
            GLog.n(Messages.get(this, "armor_break"));
            float chance = Dungeon.hero.pointsInTalent(Talent.PART_RECYCLING) / 2f;
            while (Random.Float() < chance){
                MetalPart part = new MetalPart();
                if (!part.collect()) Dungeon.level.drop(part, Dungeon.hero.pos).sprite.drop();
                chance --;
            }
            if (Dungeon.hero.pointsInTalent(Talent.KINETIC_FRAGMENT) >= 2){
                Buff.affect(Dungeon.hero, Barrier.class).setShield(Dungeon.hero.lvl);
                Dungeon.hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(Dungeon.hero.lvl), FloatingText.SHIELDING);
            }
        } else {
            if (!activeRepair)
                armorModDura = 0;

            float duraToInc = mod.maxDurability() * (1 + 0.15f * Dungeon.hero.pointsInTalent(Talent.DURABLE_MODIFIES));
            if (Dungeon.hero.pointsInTalent(Talent.ACTIVE_REPAIR) >= 2 && activeRepair) duraToInc *= 1.2f;
            armorModDura += Math.round(duraToInc);

            Sample.INSTANCE.play(Assets.Sounds.UNLOCK);
            Transmuting.show(curUser, this, this);
            curUser.sprite.operate(curUser.pos);

            if (Dungeon.hero.pointsInTalent(Talent.ACTIVE_REPAIR) >= 3 && activeRepair){
                MetalPart part = new MetalPart();
                if (!part.collect()) Dungeon.level.drop(part, Dungeon.hero.pos).sprite.drop();
            }
        }
    }

    public void decreaseWandModDura(){
        if (Dungeon.hero.buff(ForceField.Field.class) == null
                || Random.Int(4) >= Dungeon.hero.pointsInTalent(Talent.REPAIR_ABILITY))
            wandModDura = Math.max(wandModDura - 1, 0);
        if (wandModDura <= 0) modify((Wand.Modification) null);
    }

    public void modify(Wand.Modification mod){
        boolean activeRepair = wandModify == mod && Dungeon.hero.hasTalent(Talent.ACTIVE_REPAIR);
        wandModify = mod;
        if (mod == null){
            wandModDura = 0;
            GLog.n(Messages.get(this, "wand_break"));
            float chance = Dungeon.hero.pointsInTalent(Talent.PART_RECYCLING) / 2f;
            while (Random.Float() < chance){
                MetalPart part = new MetalPart();
                if (!part.collect()) Dungeon.level.drop(part, Dungeon.hero.pos).sprite.drop();
                chance --;
            }
            if (Dungeon.hero.pointsInTalent(Talent.KINETIC_FRAGMENT) >= 3){
                Buff.affect(Dungeon.hero, Recharging.class, 5);
                ScrollOfRecharging.charge(Dungeon.hero);
            }
        } else {
            if (!activeRepair)
                wandModDura = 0;

            float duraToInc = mod.maxDurability() * (1 + 0.15f * Dungeon.hero.pointsInTalent(Talent.DURABLE_MODIFIES));
            if (Dungeon.hero.pointsInTalent(Talent.ACTIVE_REPAIR) >= 2 && activeRepair) duraToInc *= 1.2f;
            wandModDura += Math.round(duraToInc);

            Sample.INSTANCE.play(Assets.Sounds.UNLOCK);
            Transmuting.show(curUser, this, this);
            curUser.sprite.operate(curUser.pos);

            if (Dungeon.hero.pointsInTalent(Talent.ACTIVE_REPAIR) >= 3 && activeRepair){
                MetalPart part = new MetalPart();
                if (!part.collect()) Dungeon.level.drop(part, Dungeon.hero.pos).sprite.drop();
            }
        }
    }

    @Override
    public boolean collect(Bag container){
        if (super.collect(container)){

            if (container.owner != null){
                if (loot != null) {
                    if (loot.target != null) loot.detach();
                    loot = null;
                }
                loot = new ConductiveLoot();
                loot.attachTo(container.owner);
            }
            return true;
        } else return false;
    }

    @Override
    public void onDetach( ) {
        if (loot != null) {
            loot.detach();
            loot = null;
        }
    }

    public class ConductiveLoot extends Buff {

        {
            revivePersists = true;
            actPriority = HERO_PRIO + 1;
        }

        @Override
        public boolean attachTo( Char target ) {
            if (super.attachTo( target )) {
                //if we're loading in and the hero has partially spent a turn, delay for 1 turn
                if (target instanceof Hero && Dungeon.hero == null && cooldown() == 0 && target.cooldown() > 0) {
                    spend(TICK);
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean act() {
            if (Dungeon.hero.belongings.contains(MultiTool.this)
                    && MultiTool.this.armorModify == Armor.Modification.CONDUCTIVE)
                MultiTool.this.decreaseArmorModDura();

            spendConstant(1);
            return true;
        }

    }

    private static final String ARMORMOD = "armor_mod";
    private static final String ARMORDURA = "armor_dura";
    private static final String WANDMOD = "wand_mod";
    private static final String WANDDURA = "wand_dura";

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle(bundle);
        if (armorModify != null) bundle.put(ARMORMOD, armorModify);
        bundle.put(ARMORDURA, armorModDura);
        if (wandModify != null) bundle.put(WANDMOD, wandModify);
        bundle.put(WANDDURA, wandModDura);
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle(bundle);
        if (bundle.contains(ARMORMOD)) armorModify = bundle.getEnum(ARMORMOD, Armor.Modification.class);
        armorModDura = bundle.getInt(ARMORDURA);
        if (bundle.contains(WANDMOD)) wandModify = bundle.getEnum(WANDMOD, Wand.Modification.class);
        wandModDura = bundle.getInt(WANDDURA);
    }
}