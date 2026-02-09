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

import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Annoying;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.BarricadeCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Dazzling;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Displacing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Explosive;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Friendly;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Polarized;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Sacrificial;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Wayward;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Alienating;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blazing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blocking;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blooming;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Chilling;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Corrupting;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Elastic;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Kinetic;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Lucky;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Peaceful;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Projecting;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Shocking;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Vampiric;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.CanScrollCheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTextInput;
import com.watabou.input.GameAction;
import com.watabou.noosa.Game;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Bundle;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class BladeOfMimic extends MeleeWeapon { //copied from Magic Ling Pixel Dungeon, with some extension

    private static final String AC_MIMIC = "mimic";

    {
        image = ItemSpriteSheet.BLADE_OF_MIMIC;
        resetStatus();
        levelKnown = true;

        unique = true;
        bones = false;

        tier = 1;
    }

    //basic modifiers
    private int s_str_req = 10;
    private boolean use_default_strength = true;
    private boolean use_default_base = true;
    private boolean use_default_scaling = true;
    private int base_min = 1;
    private float scaling_min = 1f;
    private int base_max = 10;
    private float scaling_max = 2f;
    private int base_block = 0;
    private float scaling_block = 0f;
    private float sneak_bonus = 0f;

    public boolean canSneak = true;

    private long enchOn = 0L;
    public static ArrayList<Enchantment> enchList = new ArrayList<>();
    public static LinkedHashMap<Class<? extends Enchantment> , Integer> enchPrio = new LinkedHashMap<>();
    static{
        enchPrio.put(Blazing.class,1);
        enchPrio.put(Blocking.class, 0);
        enchPrio.put(Blooming.class, 0);
        enchPrio.put(Chilling.class, 1);
        enchPrio.put(Corrupting.class, 0);
        enchPrio.put(Elastic.class, 1);
        enchPrio.put(Grim.class, 0);
        enchPrio.put(Kinetic.class, 0);
        enchPrio.put(Lucky.class, 0);
        enchPrio.put(Projecting.class, 2);
        enchPrio.put(Shocking.class, 1);
        enchPrio.put(Vampiric.class, 1);
        enchPrio.put(Alienating.class,2);
        enchPrio.put(Peaceful.class,0);

        enchPrio.put(Annoying.class, 0);
        enchPrio.put(Displacing.class, 1);
        enchPrio.put(Dazzling.class, 0);
        enchPrio.put(Explosive.class, 2);
        enchPrio.put(Friendly.class, 0);
        enchPrio.put(Polarized.class, 2);
        enchPrio.put(Sacrificial.class, 1);
        enchPrio.put(Wayward.class, 2);
        enchPrio.put(BarricadeCurse.class, 0);
    }

    @Override
    public Weapon enchant( Enchantment ench ) {
        if(ench!=null){
            if(!enchList.contains(ench)){
                enchList.add(ench);
                int i = 0;
                for(Class<? extends Enchantment> e: enchPrio.keySet()){
                    if (ench.getClass() == e){
                        enchOn += 1L<<i;
                        break;
                    }
                    ++i;
                }
            }
        }
        return this;
    }

    @Override
    public boolean hasEnchant(Class<?extends Enchantment> type, Char owner) {
        for(Enchantment e: enchList){
            if(e.getClass() == type) return true;
        }
        return false;
    }

    @Override
    public int proc( Char attacker, Char defender, int damage ) {
        if (!enchList.isEmpty() && attacker.buff(MagicImmune.class) == null) {
            //Actually a rearrange is needed, but
            //1. the scale of ench is large, 2. there are only 3 prio values,
            //so scan 3 times instead.
            for(int i=2;i>=0;--i){
                for(Enchantment e: enchList){
                    if(enchPrio.get(e.getClass())==i) damage = e.proc(this, attacker, defender, damage);
                }
            }

        }

        return damage;
    }

    public void dispelAllEnch(){
        enchList.clear();
        enchOn = 0L;
    }

    public void setEnchant(long index){
        dispelAllEnch();
        int id = 0;
        for(Class<? extends Enchantment> ench : enchPrio.keySet()){
            if(((index >> id)&1)!=0){
                enchant(Reflection.newInstance(ench));
            }
            ++id;
        }
    }

    private static final String CAN_SNEAK   = "can_sneak";
    private static final String BASE_BLOCK  = "base_block";
    private static final String SCALE_BLOCK = "scale_block";
    private static final String SNEAK_BONUS = "sneak_bonus";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("tier", tier);
        bundle.put("s_str_req", s_str_req);
        bundle.put("use_default_strength", use_default_strength);
        bundle.put("use_default_base", use_default_base);
        bundle.put("use_default_scaling", use_default_scaling);
        bundle.put("base_min", base_min);
        bundle.put("base_max", base_max);
        bundle.put("scaling_min", scaling_min);
        bundle.put("scaling_max", scaling_max);
        bundle.put("accuracy", ACC);
        bundle.put("delay", DLY);
        bundle.put("reach", RCH);
        bundle.put("ench_on", enchOn);
        bundle.put(CAN_SNEAK, canSneak);
        bundle.put(BASE_BLOCK, base_block);
        bundle.put(SCALE_BLOCK, scaling_block);
        bundle.put(SNEAK_BONUS, sneak_bonus);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        tier = bundle.getInt("tier");
        s_str_req = bundle.getInt("s_str_req");
        use_default_strength = bundle.getBoolean("use_default_strength");
        use_default_base = bundle.getBoolean("use_default_base");
        use_default_scaling = bundle.getBoolean("use_default_scaling");
        base_min = bundle.getInt("base_min");
        base_max = bundle.getInt("base_max");
        scaling_min = bundle.getFloat("scaling_min");
        scaling_max = bundle.getFloat("scaling_max");
        ACC = bundle.getFloat("accuracy");
        DLY = bundle.getFloat("delay");
        RCH = bundle.getInt("reach");
        enchOn = bundle.getLong("ench_on");
        canSneak = bundle.getBoolean(CAN_SNEAK);
        base_block = bundle.getInt(BASE_BLOCK);
        scaling_block = bundle.getFloat(SCALE_BLOCK);
        sneak_bonus = bundle.getFloat(SNEAK_BONUS);
        Item.updateQuickslot();
        setEnchant(enchOn);
    }

    @Override
    public ArrayList<String> actions(Hero hero ) {
        ArrayList<String> actions = super.actions( hero );
        actions.remove(AC_ABILITY);
        actions.add(AC_MIMIC);
        return actions;
    }

    @Override
    public String defaultAction(){
        return AC_MIMIC;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_MIMIC)){
            Item.updateQuickslot();
            GameScene.show(new SettingsWindow());
        }
    }

    @Override
    public int STRReq(int lvl) {
        lvl = Math.max(0, lvl);
        return (use_default_strength?(2*tier+8):(s_str_req)) - (int) (Math.sqrt(8 * lvl + 1) - 1) / 2;
    }

    @Override
    public int min(int lvl) {
        return Math.round(minBase() + level() * minScaling());
    }

    @Override
    public int max(int lvl) {
        return Math.round(maxBase() + level() * maxScaling());
    }

    @Override
    public int defenseFactor( Char owner ) {
        return DRMax();
    }

    public int DRMax(){
        return DRMax(buffedLvl());
    }

    //7 extra defence, plus 3 per level
    public int DRMax(int lvl){
        return Math.round(base_block + lvl * scaling_block);
    }

    @Override
    public int damageRoll(Char owner) {
        if (owner instanceof Hero) {
            Hero hero = (Hero)owner;
            Char enemy = hero.attackTarget();
            if (enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)) {
                //deals sneak_bonus percentage toward max to max on surprise, instead of min to max.
                int diff = max() - min();
                int damage = augment.damageFactor(Hero.heroDamageIntRange(
                min() + Math.round(diff*sneak_bonus),
                max()));
                int exStr = hero.STR() - STRReq();
                if (exStr > 0) {
                    damage += Hero.heroDamageIntRange(0, exStr);
                }
                return damage;
            }
        }
        return super.damageRoll(owner);
    }

    private float defaultDamageModifier(){
        float modifier = 1f;
        if(ACC>=1f){
            modifier *= 0.5f+0.5f/ACC;
        }else if(ACC<1f){
            modifier *= 2f-ACC;
        }

        modifier *= 0.16f +  0.84f*DLY;

        if (RCH <= 8) {
            modifier *= (RCH+3f)/(3f*RCH+1f);
        } else {
            modifier *= 0.44f;
        }

        return modifier;
    }

    private int minBase() {
        return (use_default_base ? Math.round(tier*defaultDamageModifier()) : base_min);
    }

    private int maxBase() {
        return (use_default_base ? Math.round((5 * tier + 5)*defaultDamageModifier()) : base_max);
    }

    private float minScaling() {
        return (use_default_scaling ? defaultDamageModifier() : scaling_min);
    }

    private float maxScaling() {
        return (use_default_scaling ? (tier + 1)*defaultDamageModifier() : scaling_max);
    }

    private void resetStatus() {
        tier = 1;
        s_str_req = 10;
        use_default_strength = true;
        use_default_base = true;
        use_default_scaling = true;
        base_min = 1;
        base_max = 10;
        scaling_min = 1f;
        scaling_max = 2f;
        ACC = 1f;
        DLY = 1f;
        RCH = 1;
        enchOn = 0L;
        canSneak = true;
        base_block = 0;
        scaling_block = 0f;
        sneak_bonus = 0f;
        Item.updateQuickslot();
        setEnchant(enchOn);
    }

    protected class SettingsWindow extends Window {
        private final int WIDTH;
        private static final int GAP = 2;
        private final RedButton tierButton;
        private final RedButton strengthButton;
        private final RedButton baseDamageButton;
        private final RedButton damageScalingButton;
        private final RedButton accuracyButton;
        private final RedButton reachButton;
        private final RedButton delayButton;
        private final RedButton baseBlockButton;
        private final RedButton scalingBlockButton;
        private final RedButton sneakBonusButton;

        public SettingsWindow() {
            WIDTH = PixelScene.landscape() ? 170 : 120;

            tierButton = new RedButton(Messages.get(this, "tier_button", tier)) {
                @Override
                protected void onClick() {
                    Game.runOnRenderThread(() ->GameScene.show(new WndTextInput(
                    Messages.get(SettingsWindow.class, "level_title"), Messages.get(SettingsWindow.class, "level_title_desc"),
                    Integer.toString(tier),
                    Short.MAX_VALUE, false, Messages.get(SettingsWindow.class, "confirm"),
                    Messages.get(SettingsWindow.class, "cancel")) {
                        @Override
                        public void onSelect(boolean check, String text) {
                            if (check && text.matches("\\d+")) {
                                tier = Math.min(Integer.parseInt(text), Short.MAX_VALUE);
                                tierButton.text(Messages.get(SettingsWindow.class, "tier_button", tier));
                                Item.updateQuickslot();
                            }
                        }
                    }));
                    super.onClick();
                }
            };
            tierButton.setRect(0, 0, WIDTH / 2 - 1, 16);
            add(tierButton);

            strengthButton = new RedButton(Messages.get(this, "str_button", (use_default_strength?(2*tier+8):(s_str_req)))) {
                @Override
                protected void onClick() {
                    GameScene.show(new StrengthWindow());
                }
            };
            strengthButton.setRect(WIDTH / 2 + 1, 0, WIDTH / 2 - 1, 16);
            add(strengthButton);

            baseDamageButton = new RedButton(Messages.get(this, "base_damage_button", minBase(), maxBase())) {
                @Override
                protected void onClick() {
                    GameScene.show(new BaseDamageWindow());
                }
            };
            baseDamageButton.setRect(0, tierButton.bottom() + GAP, WIDTH / 2 - 1, 16);
            add(baseDamageButton);

            damageScalingButton = new RedButton(Messages.get(this, "damage_scaling_button", minScaling(), maxScaling())) {
                @Override
                protected void onClick() {
                    GameScene.show(new DamageScalingWindow());
                }
            };
            damageScalingButton.setRect(WIDTH / 2 + 1, tierButton.bottom() + GAP, WIDTH / 2 - 1, 16);
            add(damageScalingButton);

            accuracyButton = new RedButton(Messages.get(this, "accuracy_button", ACC)) {
                @Override
                protected void onClick() {
                    Game.runOnRenderThread(() ->GameScene.show(new WndTextInput(
                    Messages.get(SettingsWindow.class, "level_title"), Messages.get(SettingsWindow.class, "level_title_desc"),
                    Float.toString(ACC),
                    Short.MAX_VALUE, false, Messages.get(SettingsWindow.class, "confirm"),
                    Messages.get(SettingsWindow.class, "cancel")) {
                        @Override
                        public void onSelect(boolean check, String text) {
                            if (check && text.matches("^\\d+\\.?\\d{0,3}")) {
                                ACC = Math.min(Float.parseFloat(text), Short.MAX_VALUE);
                                accuracyButton.text(Messages.get(SettingsWindow.class, "accuracy_button", ACC));
                            }
                        }
                    }));
                    super.onClick();
                }
            };
            accuracyButton.setRect(0, baseDamageButton.bottom() + GAP, WIDTH, 16);
            add(accuracyButton);

            reachButton = new RedButton(Messages.get(this, "reach_button", RCH)) {
                @Override
                protected void onClick() {
                    Game.runOnRenderThread(() ->GameScene.show(new WndTextInput(
                    Messages.get(SettingsWindow.class, "level_title"), Messages.get(SettingsWindow.class, "level_title_desc"),
                    Integer.toString(RCH),
                    Short.MAX_VALUE, false, Messages.get(SettingsWindow.class, "confirm"),
                    Messages.get(SettingsWindow.class, "cancel")) {
                        @Override
                        public void onSelect(boolean check, String text) {
                            if (check && text.matches("\\d+")) {
                                RCH = Math.min(Integer.parseInt(text), Short.MAX_VALUE);
                                reachButton.text(Messages.get(SettingsWindow.class, "reach_button", RCH));
                                AttackIndicator.updateState();
                            }
                        }
                    }));
                    super.onClick();
                }
            };
            reachButton.setRect(0, accuracyButton.bottom() + GAP, WIDTH / 2 - 1, 16);
            add(reachButton);

            delayButton = new RedButton(Messages.get(this, "delay_button", DLY)) {
                @Override
                protected void onClick() {
                    Game.runOnRenderThread(() ->GameScene.show(new WndTextInput(
                    Messages.get(SettingsWindow.class, "level_title"), Messages.get(SettingsWindow.class, "level_title_desc"),
                    Float.toString(DLY),
                    Short.MAX_VALUE, false, Messages.get(SettingsWindow.class, "confirm"),
                    Messages.get(SettingsWindow.class, "cancel")) {
                        @Override
                        public void onSelect(boolean check, String text) {
                            if (check && text.matches("^\\d+\\.?\\d{0,3}")) {
                                DLY = Math.min(Float.parseFloat(text), Short.MAX_VALUE);
                                delayButton.text(Messages.get(SettingsWindow.class, "delay_button", DLY));
                            }
                        }
                    }));
                    super.onClick();
                }
            };
            delayButton.setRect(WIDTH / 2 + 1, accuracyButton.bottom() + GAP, WIDTH / 2 - 1, 16);
            add(delayButton);

            CheckBox canSneakBox = new CheckBox(Messages.titleCase(Messages.get(this, "can_sneak"))) {
                @Override
                protected void onClick() {
                    super.onClick();
                    canSneak = checked();
                }
            };
            canSneakBox.checked(canSneak);
            canSneakBox.setRect(0, delayButton.bottom() + GAP, WIDTH / 2 - 1, 16);
            add(canSneakBox);

            sneakBonusButton = new RedButton(Messages.get(this, "sneak_bonus_button", sneak_bonus)) {
                @Override
                protected void onClick() {
                    Game.runOnRenderThread(() ->GameScene.show(new WndTextInput(
                    Messages.get(SettingsWindow.class, "level_title"), Messages.get(SettingsWindow.class, "level_title_desc"),
                    Float.toString(sneak_bonus),
                    Short.MAX_VALUE, false, Messages.get(SettingsWindow.class, "confirm"),
                    Messages.get(SettingsWindow.class, "cancel")) {
                        @Override
                        public void onSelect(boolean check, String text) {
                            if (check && text.matches("^\\d+\\.?\\d{0,3}")) {
                                sneak_bonus = Math.min(Float.parseFloat(text), 1f);
                                sneakBonusButton.text(Messages.get(SettingsWindow.class, "sneak_bonus_button", sneak_bonus));
                            }
                        }
                    }));
                    super.onClick();
                }
            };
            sneakBonusButton.setRect(WIDTH / 2 + 1, delayButton.bottom() + GAP, WIDTH / 2 - 1, 16);
            add(sneakBonusButton);

            baseBlockButton = new RedButton(Messages.get(this, "base_block_button", base_block)) {
                @Override
                protected void onClick() {
                    Game.runOnRenderThread(() ->GameScene.show(new WndTextInput(
                    Messages.get(SettingsWindow.class, "level_title"), Messages.get(SettingsWindow.class, "level_title_desc"),
                    Integer.toString(base_block),
                    Short.MAX_VALUE, false, Messages.get(SettingsWindow.class, "confirm"),
                    Messages.get(SettingsWindow.class, "cancel")) {
                        @Override
                        public void onSelect(boolean check, String text) {
                            if (check && text.matches("\\d+")) {
                                base_block = Math.min(Integer.parseInt(text), Short.MAX_VALUE);
                                baseBlockButton.text(Messages.get(SettingsWindow.class, "base_block_button", base_block));
                            }
                        }
                    }));
                    super.onClick();
                }
            };
            baseBlockButton.setRect(0, canSneakBox.bottom() + GAP, WIDTH / 2 - 1, 16);
            add(baseBlockButton);

            scalingBlockButton = new RedButton(Messages.get(this, "scaling_block_button", scaling_block)) {
                @Override
                protected void onClick() {
                    Game.runOnRenderThread(() ->GameScene.show(new WndTextInput(
                    Messages.get(SettingsWindow.class, "level_title"), Messages.get(SettingsWindow.class, "level_title_desc"),
                    Float.toString(scaling_block),
                    Short.MAX_VALUE, false, Messages.get(SettingsWindow.class, "confirm"),
                    Messages.get(SettingsWindow.class, "cancel")) {
                        @Override
                        public void onSelect(boolean check, String text) {
                            if (check && text.matches("^\\d+\\.?\\d{0,3}")) {
                                scaling_block = Math.min(Float.parseFloat(text), Short.MAX_VALUE);
                                scalingBlockButton.text(Messages.get(SettingsWindow.class, "scaling_block_button", scaling_block));
                            }
                        }
                    }));
                }
            };
            scalingBlockButton.setRect(WIDTH / 2 + 1, canSneakBox.bottom() + GAP, WIDTH / 2 - 1, 16);
            add(scalingBlockButton);

            IconButton ench = new IconButton(Icons.BUFFS.get()){
                @Override
                protected void onClick() {
                    super.onClick();
                    GameScene.show(new EnchantWindow());
                }

                @Override
                public GameAction keyAction() {
                    return SPDAction.TAG_ACTION;
                }

                @Override
                protected String hoverText() {
                    return Messages.get(SettingsWindow.class, "ench_button");
                }
            };
            add(ench);
            ench.setRect(0, baseBlockButton.bottom() + GAP, 15, 15);

            IconButton strengthen = new IconButton(Icons.STRENGTHEN.get()){
                @Override
                protected void onClick() {
                    super.onClick();
                    s_str_req = 0;
                    use_default_strength = false;
                    use_default_base = false;
                    use_default_scaling = false;
                    base_min = Short.MAX_VALUE;
                    base_max = Short.MAX_VALUE;
                    scaling_min = Short.MAX_VALUE;
                    scaling_max = Short.MAX_VALUE;
                    ACC = Short.MAX_VALUE;
                    DLY = 0.001f;
                    RCH = Short.MAX_VALUE;
                    kindEnchOn(false);
                    canSneak = true;
                    canSneakBox.checked(true);
                    base_block = Short.MAX_VALUE;
                    scaling_block = Short.MAX_VALUE;
                    sneak_bonus = 1f;
                    Item.updateQuickslot();
                    setEnchant(enchOn);
                    updateAllButtonText();
                }

                @Override
                public GameAction keyAction() {
                    return SPDAction.ZOOM_IN;
                }

                @Override
                protected String hoverText() {
                    return Messages.get(SettingsWindow.class, "strengthen");
                }
            };
            add(strengthen);
            strengthen.setRect(ench.right() + 5 * GAP, baseBlockButton.bottom() + GAP, 15, 15);

            IconButton weaken = new IconButton(Icons.WEAKEN.get()){
                @Override
                protected void onClick() {
                    super.onClick();
                    s_str_req = Short.MAX_VALUE;
                    use_default_strength = false;
                    use_default_base = false;
                    use_default_scaling = false;
                    base_min = 0;
                    base_max = 0;
                    scaling_min = 0f;
                    scaling_max = 0f;
                    ACC = 0f;
                    DLY = Short.MAX_VALUE;
                    RCH = 1;
                    kindEnchOn(true);
                    canSneak = false;
                    canSneakBox.checked(false);
                    base_block = 0;
                    scaling_block = 0f;
                    sneak_bonus = 0f;
                    Item.updateQuickslot();
                    setEnchant(enchOn);
                    updateAllButtonText();
                }

                @Override
                public GameAction keyAction() {
                    return SPDAction.ZOOM_OUT;
                }

                @Override
                protected String hoverText() {
                    return Messages.get(SettingsWindow.class, "weaken");
                }
            };
            add(weaken);
            weaken.setRect(strengthen.right() + 5 * GAP, baseBlockButton.bottom() + GAP, 15, 15);

            IconButton reset = new IconButton(Icons.CHANGES.get()){
                @Override
                protected void onClick() {
                    super.onClick();
                    GameScene.show(
                        new WndOptions(Messages.titleCase(Messages.get(SettingsWindow.class, "reset")),
                        Messages.get(SettingsWindow.class, "reset_warn"),
                        Messages.get(SettingsWindow.class, "reset_yes"),
                        Messages.get(SettingsWindow.class, "reset_no")) {
                        @Override
                        protected void onSelect(int index) {
                            if (index == 0) {
                                resetStatus();
                                updateAllButtonText();
                                canSneakBox.checked(true);
                                AttackIndicator.updateState();
                            }
                        }
                    }
                    );
                }

                @Override
                public GameAction keyAction() {
                    return SPDAction.TAG_RESUME;
                }

                @Override
                protected String hoverText() {
                    return Messages.get(SettingsWindow.class, "reset");
                }
            };
            add(reset);
            reset.setRect(WIDTH - 15, baseBlockButton.bottom() + GAP, 15, 15);

            resize(WIDTH, (int) reset.bottom());
        }

        public void updateAllButtonText(){
            tierButton.text(Messages.get(this, "tier_button", tier));
            strengthButton.text(Messages.get(this, "str_button", (use_default_strength?(2*tier+8):(s_str_req))));
            baseDamageButton.text(Messages.get(this, "base_damage_button", minBase(), maxBase()));
            damageScalingButton.text(Messages.get(this, "damage_scaling_button", minScaling(), maxScaling()));
            accuracyButton.text(Messages.get(this, "accuracy_button", ACC));
            reachButton.text(Messages.get(this, "reach_button", RCH));
            delayButton.text(Messages.get(this, "delay_button", DLY));
            sneakBonusButton.text(Messages.get(this, "sneak_bonus_button", sneak_bonus));
            baseBlockButton.text(Messages.get(this, "base_block_button", base_block));
            scalingBlockButton.text(Messages.get(this, "scaling_block_button", scaling_block));
        }

        private class StrengthWindow extends Window {
            CheckBox c_default;
            RedButton strSet1;

            public StrengthWindow() {

                c_default = new CheckBox(Messages.get(SettingsWindow.class, "default")){
                    @Override
                    protected void onClick(){
                        super.onClick();
                        use_default_strength = checked();
                        updateText();
                        strSet1.active = !checked();
                        strSet1.alpha(checked()?0.4f:1.0f);
                    }
                };
                c_default.checked(use_default_strength);
                c_default.setRect(0, 0, WIDTH, 16);
                add(c_default);

                strSet1 = new RedButton(Messages.get(SettingsWindow.class,"str_button",s_str_req)){
                    @Override
                    protected void onClick() {
                        Game.runOnRenderThread(() ->GameScene.show(new WndTextInput(
                                Messages.get(SettingsWindow.class, "level_title"), Messages.get(SettingsWindow.class, "level_title_desc"),
                                Integer.toString(s_str_req),
                                Short.MAX_VALUE, false, Messages.get(SettingsWindow.class, "confirm"),
                                Messages.get(SettingsWindow.class, "cancel")) {
                            @Override
                            public void onSelect(boolean check, String text) {
                                if (check && text.matches("\\d+")) {
                                    s_str_req = Integer.parseInt(text);
                                    updateText();
                                    strengthButton.text(Messages.get(SettingsWindow.class, "str_button", s_str_req));
                                    Item.updateQuickslot();
                                }
                            }
                        }));
                        super.onClick();
                    }
                };
                strSet1.setRect(0, GAP + c_default.bottom(), WIDTH, 16);
                add(strSet1);

                strSet1.active = !use_default_strength;
                strSet1.alpha(use_default_strength?0.4f:1.0f);

                updateText();

                layout();
            }

            private void updateText() {
                strengthButton.text(Messages.get(SettingsWindow.class, "str_button", (use_default_strength?(2*tier+8):(s_str_req))));
                strSet1.text(Messages.get(SettingsWindow.class, "str_button", (use_default_strength?(2*tier+8):(s_str_req))));
            }

            private void layout() {
                c_default.setRect(0, 0, WIDTH, 16);
                strSet1.setRect(0, GAP + c_default.bottom(), WIDTH, 16);
                resize(WIDTH, (int) strSet1.bottom());
            }

            @Override
            public void onBackPressed(){
                Item.updateQuickslot();
                updateAllButtonText();
                super.onBackPressed();
            }
        }

        private class BaseDamageWindow extends Window {
            private final RedButton mindmg;
            private final RedButton maxdmg;

            public BaseDamageWindow() {

                CheckBox useDefault = new CheckBox(Messages.get(SettingsWindow.class, "default")) {
                    @Override
                    protected void onClick() {
                        super.onClick();
                        use_default_base = checked();
                        updateText();
                        mindmg.active = !checked();
                        mindmg.alpha(checked() ? 0.4f : 1.0f);
                        maxdmg.active = !checked();
                        maxdmg.alpha(checked() ? 0.4f : 1.0f);
                    }
                };
                useDefault.checked(use_default_base);
                useDefault.setRect(0, 0, WIDTH, 16);
                add(useDefault);

                mindmg = new RedButton(Messages.get(SettingsWindow.class,"base_dmg_min",base_min)){
                    @Override
                    protected void onClick() {
                        Game.runOnRenderThread(() ->GameScene.show(new WndTextInput(
                                Messages.get(SettingsWindow.class, "level_title"), Messages.get(SettingsWindow.class, "level_title_desc"),
                                Integer.toString(base_min),
                                Short.MAX_VALUE, false, Messages.get(SettingsWindow.class, "confirm"),
                                Messages.get(SettingsWindow.class, "cancel")) {
                            @Override
                            public void onSelect(boolean check, String text) {
                                if (check && text.matches("\\d+")) {
                                    int level = Integer.parseInt(text);
                                    base_min = Math.min(level, Short.MAX_VALUE);
                                    suppressMin();
                                    updateText();
                                    baseDamageButton.text(Messages.get(SettingsWindow.class, "base_damage_button", base_min,base_max));
                                }
                            }
                        }));
                        super.onClick();
                    }
                };
                mindmg.setRect(0, GAP + useDefault.bottom(), WIDTH, 16);
                add(mindmg);

                mindmg.active = !use_default_base;
                mindmg.alpha(use_default_base?0.4f:1.0f);

                maxdmg = new RedButton(Messages.get(SettingsWindow.class,"base_dmg_max",base_max)){
                    @Override
                    protected void onClick() {
                        Game.runOnRenderThread(() ->GameScene.show(new WndTextInput(
                                Messages.get(SettingsWindow.class, "level_title"), Messages.get(SettingsWindow.class, "level_title_desc"),
                                Integer.toString(base_max),
                                Short.MAX_VALUE, false, Messages.get(SettingsWindow.class, "confirm"),
                                Messages.get(SettingsWindow.class, "cancel")) {
                            @Override
                            public void onSelect(boolean check, String text) {
                                if (check && text.matches("\\d+")) {
                                    int level = Integer.parseInt(text);
                                    base_max = Math.min(level, Short.MAX_VALUE);
                                    suppressMin();
                                    updateText();
                                    baseDamageButton.text(Messages.get(SettingsWindow.class, "base_damage_button", base_min,base_max));
                                }
                            }
                        }));
                        super.onClick();
                    }
                };
                maxdmg.setRect(0, GAP + mindmg.bottom(), WIDTH, 16);
                add(maxdmg);

                maxdmg.active = !use_default_base;
                maxdmg.alpha(use_default_base?0.4f:1.0f);

                updateText();

                resize(WIDTH, (int) maxdmg.bottom());
            }

            private void updateText() {
                mindmg.text(Messages.get(SettingsWindow.class,"base_dmg_min",base_min));
                maxdmg.text(Messages.get(SettingsWindow.class,"base_dmg_max",base_max));
            }

            private void suppressMin() {
                if (base_max < base_min) {
                    base_min = base_max;
                }
            }

            @Override
            public void onBackPressed(){
                updateAllButtonText();
                super.onBackPressed();
            }
        }

        private class DamageScalingWindow extends Window {
            private final RedButton minDmgScale;
            private final RedButton maxDmgScale;

            public DamageScalingWindow() {

                CheckBox useDefault = new CheckBox(Messages.get(SettingsWindow.class, "default")) {
                    @Override
                    protected void onClick() {
                        super.onClick();
                        use_default_scaling = checked();
                        updateText();
                        minDmgScale.active = !checked();
                        minDmgScale.alpha(checked() ? 0.4f : 1.0f);
                        maxDmgScale.active = !checked();
                        maxDmgScale.alpha(checked() ? 0.4f : 1.0f);
                    }
                };
                useDefault.checked(use_default_scaling);
                useDefault.setRect(0, 0, WIDTH, 16);
                add(useDefault);

                minDmgScale = new RedButton(Messages.get(SettingsWindow.class,"scale_dmg_min",scaling_min)){
                    @Override
                    protected void onClick() {
                        Game.runOnRenderThread(() ->GameScene.show(new WndTextInput(
                                Messages.get(SettingsWindow.class, "level_title"), Messages.get(SettingsWindow.class, "level_title_desc"),
                                Float.toString(scaling_min),
                                Short.MAX_VALUE, false, Messages.get(SettingsWindow.class, "confirm"),
                                Messages.get(SettingsWindow.class, "cancel")) {
                            @Override
                            public void onSelect(boolean check, String text) {
                                if (check && text.matches("^\\d+\\.?\\d{0,3}")) {
                                    float level = Float.parseFloat(text);
                                    scaling_min = Math.min(level, Short.MAX_VALUE);
                                    suppressMin();
                                    updateText();
                                    damageScalingButton.text(Messages.get(SettingsWindow.class, "damage_scaling_button", scaling_min,scaling_max));
                                }
                            }
                        }));
                        super.onClick();
                    }
                };
                minDmgScale.setRect(0, GAP + useDefault.bottom(), WIDTH, 16);
                add(minDmgScale);

                minDmgScale.active = !use_default_scaling;
                minDmgScale.alpha(use_default_scaling?0.4f:1.0f);

                maxDmgScale = new RedButton(Messages.get(SettingsWindow.class,"scale_dmg_max",scaling_max)){
                    @Override
                    protected void onClick() {
                        Game.runOnRenderThread(() ->GameScene.show(new WndTextInput(
                                Messages.get(SettingsWindow.class, "level_title"), Messages.get(SettingsWindow.class, "level_title_desc"),
                                Float.toString(scaling_max),
                                Short.MAX_VALUE, false, Messages.get(SettingsWindow.class, "confirm"),
                                Messages.get(SettingsWindow.class, "cancel")) {
                            @Override
                            public void onSelect(boolean check, String text) {
                                if (check && text.matches("^\\d+\\.?\\d{0,3}")) {
                                    float level = Float.parseFloat(text);
                                    scaling_max = Math.min(level, Short.MAX_VALUE);
                                    suppressMin();
                                    updateText();
                                    damageScalingButton.text(Messages.get(SettingsWindow.class, "damage_scaling_button", scaling_min,scaling_max));
                                }
                            }
                        }));
                        super.onClick();
                    }
                };
                maxDmgScale.setRect(0, GAP + minDmgScale.bottom(), WIDTH, 16);
                add(maxDmgScale);

                maxDmgScale.active = !use_default_scaling;
                maxDmgScale.alpha(use_default_scaling?0.4f:1.0f);

                updateText();

                resize(WIDTH, (int) maxDmgScale.bottom());
            }

            private void updateText() {
                minDmgScale.text(Messages.get(SettingsWindow.class, "scale_dmg_min", scaling_min));
                maxDmgScale.text(Messages.get(SettingsWindow.class, "scale_dmg_max", scaling_max));
            }

            private void suppressMin() {
                if (scaling_max < scaling_min) {
                    scaling_min = scaling_max;
                }
            }

            @Override
            public void onBackPressed(){
                updateAllButtonText();
                super.onBackPressed();
            }
        }

        private class EnchantWindow extends Window {
            private final ArrayList<CanScrollCheckBox> checkBoxes = new ArrayList<>();
            public EnchantWindow(){

                super();
                resize(WIDTH, 108 + 36);
                int placed = 0;
                ScrollPane list = new ScrollPane(new Component()) {

                    @Override
                    public void onClick(float x, float y) {
                        int max_size = checkBoxes.size();
                        for (int i = 0; i < max_size; ++i) {
                            if (checkBoxes.get(i).onClick(x, y))
                                break;
                        }
                    }

                };
                add(list);
                Component content = list.content();
                for(Class<? extends Enchantment> ench : enchPrio.keySet()){
                    CanScrollCheckBox cb = new CanScrollCheckBox(Reflection.newInstance(ench).name()){
                        public boolean onClick(float x, float y){
                            if(!inside(x,y)) return false;
                            onClick();

                            return true;
                        }

                        @Override
                        protected void onClick(){
                            super.onClick();
                            checked(!checked());
                        }

                        @Override
                        protected void layout(){
                            super.layout();
                            hotArea.width = hotArea.height = 0;
                        }
                    };
                    cb.checked((enchOn&(1L<<placed)) > 0 );
                    PixelScene.align(cb);
                    content.add(cb);
                    checkBoxes.add(cb);
                    cb.setRect(0, (16 + GAP) * placed, WIDTH, 16);
                    placed ++;
                }
                content.setSize(WIDTH, checkBoxes.get(checkBoxes.size()-1).bottom());
                list.setRect(0, 0, WIDTH, 108);
                list.scrollTo(0,0);

                RedButton allOn = new RedButton(Messages.get(BladeOfMimic.class, "all_on")) {
                    @Override
                    protected void onClick() {
                        int len = enchPrio.size();
                        enchOn = (1L<<len) - 1L;
                        updateCheckBox();
                        setEnchant(enchOn);
                    }
                };
                allOn.setRect(0, 110, WIDTH / 2 - 1, 16);
                add(allOn);

                RedButton allOff = new RedButton(Messages.get(BladeOfMimic.class, "all_off")) {
                    @Override
                    protected void onClick() {
                        enchOn = 0L;
                        updateCheckBox();
                        setEnchant(enchOn);
                    }
                };
                allOff.setRect(WIDTH / 2 + 1, 110, WIDTH / 2 - 1, 16);
                add(allOff);

                RedButton posOn = new RedButton(Messages.get(BladeOfMimic.class, "positive_on")) {
                    @Override
                    protected void onClick() {
                        kindEnchOn(false);
                        updateCheckBox();
                    }
                };
                posOn.setRect(0, 128, WIDTH / 2 - 1, 16);
                add(posOn);

                RedButton negOn = new RedButton(Messages.get(BladeOfMimic.class, "negative_on")) {
                    @Override
                    protected void onClick() {
                        kindEnchOn(true);
                        updateCheckBox();
                    }
                };
                negOn.setRect(WIDTH / 2 + 1, 128, WIDTH / 2 - 1, 16);
                add(negOn);
            }

            private void updateCheckBox(){
                int i=0;
                for(CheckBox cb: checkBoxes){
                    cb.checked((enchOn&(1L<<i)) > 0 );
                    ++i;
                }
            }

            @Override
            public void onBackPressed() {
                int max_size = checkBoxes.size();
                long ench = 0L;
                for(int i=0;i<max_size;i++){
                    ench += (checkBoxes.get(i).checked()?1L<<i:0);
                }
                setEnchant(ench);
                super.onBackPressed();
            }
        }

        private void kindEnchOn(boolean isCurse){
            enchOn = 0L;
            int i=0;
            for(Class<? extends Enchantment> ench : enchPrio.keySet()){
                if(Reflection.newInstance(ench).curse() == isCurse){
                    enchOn += 1L<<i;
                }
                ++i;
            }
            setEnchant(enchOn);
        }
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return new ItemSprite.Glowing(0xb57d4f);
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public float weight(){
        return 0;
    }
}