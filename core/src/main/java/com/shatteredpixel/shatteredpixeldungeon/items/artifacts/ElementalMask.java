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

package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corrosion;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.AntiMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.PotionBandolier;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHaste;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfInvisibility;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLevitation;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMindVision;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfParalyticGas;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfPurity;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.UnstableBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfDivineInspiration;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfDragonsBreath;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.HashSet;

public class ElementalMask extends Artifact {//will replace Ring of Elements

    {
        image = ItemSpriteSheet.ARTIFACT_MASK;

        levelCap = 10;

        charge = (int)(level()*0.6f)+2;
        partialCharge = 0;
        chargeCap = (int)(level()*0.6f)+2;

        defaultAction = AC_RELEASE;
    }

    public static final String AC_RELEASE = "RELEASE";
    public static final String AC_ADD = "ADD";

    private final ArrayList<Class> potions = new ArrayList<>();

    private int rarePotionsRemain = 2;//prevent player from surviving forever by drinking PoH from this

    public ElementalMask() {
        super();
        setupPotions();
    }

    private void setupPotions(){
        potions.clear();

        Class<?>[] PotionClasses = new Class<?>[]{
            PotionOfLiquidFlame.class,
            PotionOfFrost.class,
            PotionOfToxicGas.class,
            PotionOfParalyticGas.class,
            PotionOfPurity.class,
            PotionOfInvisibility.class,
            PotionOfHaste.class,
            PotionOfHealing.class,
            PotionOfLevitation.class,
            PotionOfMindVision.class};
        float[] probs = new float[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1}; //Every potion has equal chance to appear
        int i = Random.chances(probs);

        while (i != -1){
            potions.add(PotionClasses[i]);
            probs[i] = 0;

            i = Random.chances(probs);
        }
    }

    public float resist(Class effect){
        return resist(effect, this.level());
    }

    public static final HashSet<Class> RESISTS = new HashSet<>();
    static {
        RESISTS.add( Burning.class );
        RESISTS.add( Chill.class );
        RESISTS.add( Frost.class );
        RESISTS.add( Ooze.class );
        RESISTS.add( Paralysis.class );
        RESISTS.add( Poison.class );
        RESISTS.add( Corrosion.class );

        RESISTS.add( ToxicGas.class );
        RESISTS.add( Electricity.class );

        RESISTS.addAll( AntiMagic.RESISTS );
    }

    public float resist(Class effect, int level){
        if (this.cursed) return 1.3f;

        for (Class c : RESISTS){
            if (c.isAssignableFrom(effect)){
                return 1-(2 + level)*0.07f;
            }
        }

        return 1f;
    }

    @Override
    public ArrayList<String> actions( Hero hero ) {
        ArrayList<String> actions = super.actions( hero );
        if (isEquipped( hero ) && charge > 0 && !cursed && hero.buff(MagicImmune.class) == null) {
            actions.add(AC_RELEASE);
        }
        if (isEquipped( hero ) && level() < levelCap && !cursed && hero.buff(MagicImmune.class) == null) {
            actions.add(AC_ADD);
        }
        return actions;
    }

    @Override
    public void execute( Hero hero, String action ) {

        super.execute( hero, action );

        if (hero.buff(MagicImmune.class) != null) return;

        if (action.equals( AC_RELEASE )) {

            if (!isEquipped( hero ))             GLog.i( Messages.get(Artifact.class, "need_to_equip") );
            else if (charge <= 0)                     GLog.i( Messages.get(this, "no_charge") );
            else if (cursed)                          GLog.i( Messages.get(this, "cursed") );
            else {
                doReleaseEffect(hero);
            }

        } else if (action.equals( AC_ADD )) {
            GameScene.selectItem(itemSelector);
        }
    }

    Potion fpotion;

    public void doReleaseEffect(Hero hero){

        Potion potion;

        do {
            potion = Reflection.newInstance(Random.chances(UnstableBrew.potionChances));
        } while ((potion instanceof PotionOfExperience && charge <= 1)
        || ((potion instanceof PotionOfHealing || potion instanceof PotionOfExperience) && rarePotionsRemain <= 0));

        potion.anonymize();
        curItem = potion;
        curUser = hero;

        fpotion = potion;
        charge --;
        if (potion instanceof PotionOfExperience) charge --;
        if (potion instanceof PotionOfHealing || potion instanceof PotionOfExperience) rarePotionsRemain--;

        //if there are charges left and the potion has been given to the mask
        if ( charge > 0 && !potions.contains(potion.getClass())
        && (!(fpotion instanceof PotionOfExperience) || level() >= 10) ) {

            final ExploitHandler handler = Buff.affect(hero, ExploitHandler.class);
            handler.potion = potion;

            GameScene.show(new WndOptions(new ItemSprite(this),
            Messages.get(this, "prompt"),
            Messages.get(this, "read_empowered"),
            potion.trueName(),
            Messages.get(ExoticPotion.regToExo.get(potion.getClass()), "name")){
                @Override
                protected void onSelect(int index) {
                    handler.detach();
                    if (index == 1){
                        fpotion = Reflection.newInstance(ExoticPotion.regToExo.get(fpotion.getClass()));
                        charge--;
                        if (fpotion instanceof PotionOfDragonsBreath || fpotion instanceof PotionOfDivineInspiration){
                            ((PotionOfDragonsBreath)fpotion).drink(Dungeon.hero);
                            updateQuickslot();
                            return;
                        }
                    }
                    GameScene.selectCell(cellSelector);
                    updateQuickslot();
                }

                @Override
                public void onBackPressed() {
                    //do nothing
                }
            });
        } else {
            GLog.h(Messages.get(this, "will_release") + potion.trueName());
            GameScene.selectCell(cellSelector);
        }

        updateQuickslot();
    }

    //forces the release of a regular potion if the player tried to exploit by quitting the game when the menu was up
    public static class ExploitHandler extends Buff {
        { actPriority = VFX_PRIO; }

        public Potion potion;

        @Override
        public boolean act() {
            curUser = Dungeon.hero;
            curItem = potion;
            potion.anonymize();
            Game.runOnRenderThread(new Callback() {
                @Override
                public void call() {
                    potion.shatter(target.pos);
                    Item.updateQuickslot();
                }
            });
            detach();
            return true;
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put( "potion", potion);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            potion = (Potion)bundle.get("potion");
        }
    }

    @Override
    public void resetForTrinity(int visibleLevel) {
        super.resetForTrinity(visibleLevel);
        setupPotions();
        while (!potions.isEmpty() && potions.size() > (levelCap-1-level())) {
            potions.remove(0);
        }
    }

    @Override
    protected ArtifactBuff passiveBuff() {
        return new maskRecharge();
    }

    @Override
    public void charge(Hero target, float amount) {
        if (charge < chargeCap && !cursed && target.buff(MagicImmune.class) == null){
            partialCharge += 0.1f*amount;
            while (partialCharge >= 1){
                partialCharge--;
                charge++;
            }
            if (charge >= chargeCap){
                partialCharge = 0;
            }
            updateQuickslot();
        }
    }

    @Override
    public Item upgrade() {
        chargeCap = (int)((level()+1)*0.6f)+2;
        if (level() % 2 == 0)rarePotionsRemain ++;

        //for artifact transmutation.
        while (!potions.isEmpty() && potions.size() > (levelCap-1-level())) {
            potions.remove(0);
        }

        return super.upgrade();
    }

    private void checkForArtifactProc(int cell){
        //trigger illuminate in 3x3 area
        for (int i : PathFinder.NEIGHBOURS9) {
            Mob mob = null;
            if (Actor.findChar(i+cell) instanceof Mob) mob = (Mob)Actor.findChar(i+cell);
            if (mob != null) artifactProc(mob, visiblyUpgraded(), 1);
        }
    }

    @Override
    public String desc() {
        String desc = super.desc();

        if (isEquipped(Dungeon.hero)) {
            if (cursed) {
                desc += "\n\n" + Messages.get(this, "desc_cursed");
            }

            if (level() < levelCap){
                if (potions.size() > 1) {
                    desc += "\n\n" + Messages.get(this, "desc_index_2",
                        Messages.get(potions.get(0), "name"), Messages.get(potions.get(1), "name"));
                } else if (potions.size() > 0) {
                    desc += "\n\n" + Messages.get(this, "desc_index_1",
                        Messages.get(potions.get(0), "name"));
                }
            }
        }

        if (level() > 0) {
            desc += "\n\n" + Messages.get(this, "desc_empowered");
        }

        if (rarePotionsRemain <= 0) {
            desc += "\n\n" + Messages.get(this, "desc_exhausted");
        }

        return desc;
    }

    private static final String POTIONS =   "potions";
    private static final String POHREMAIN =   "pohremain";

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle(bundle);
        bundle.put( POTIONS, potions.toArray(new Class[potions.size()]) );
        bundle.put(POHREMAIN, rarePotionsRemain);
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle(bundle);
        potions.clear();
        if (bundle.contains(POTIONS) && bundle.getClassArray(POTIONS) != null) {
            for (Class<?> potion : bundle.getClassArray(POTIONS)) {
                if (potion != null) potions.add(potion);
            }
        }
        if (bundle.contains(POHREMAIN)) rarePotionsRemain = bundle.getInt(POHREMAIN);
        else for (int i = 0; i < 5;){
            upgrade();
            i++;
        }
    }

    protected WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {

        @Override
        public String textPrompt() {
            return Messages.get(ElementalMask.class, "prompt");
        }

        @Override
        public Class<?extends Bag> preferredBag(){
            return PotionBandolier.class;
        }

        @Override
        public boolean itemSelectable(Item item) {
            return item instanceof Potion && item.isIdentified() && potions.contains(item.getClass());
        }

        @Override
        public void onSelect(Item item) {
            if (item != null && item instanceof Potion && item.isIdentified()){
                Hero hero = Dungeon.hero;
                for (int i = 0; ( i <= 1 && i < potions.size() ); i++){
                    if (potions.get(i).equals(item.getClass())){
                        hero.sprite.operate( hero.pos );
                        hero.busy();
                        hero.spend( 1f );
                        Sample.INSTANCE.play(Assets.Sounds.DRINK);
                        hero.sprite.emitter().burst( ElmoParticle.FACTORY, 12 );

                        potions.remove(i);
                        item.detach(hero.belongings.backpack);

                        upgrade();
                        Catalog.countUse(ElementalMask.class);
                        GLog.p( Messages.get(ElementalMask.class, "infuse_potion") );
                        return;
                    }
                }
                GLog.w( Messages.get(ElementalMask.class, "unable_potion") );
            } else if (item instanceof Potion && !item.isIdentified()) {
                GLog.w( Messages.get(ElementalMask.class, "unknown_potion") );
            }
        }
    };

    public class maskRecharge extends ArtifactBuff{
        @Override
        public boolean act() {
            if (charge < chargeCap
            && !cursed
            && target.buff(MagicImmune.class) == null
            && Regeneration.regenOn()) {
                //120 turns to charge at full, 80 turns to charge at 0/7
                float chargeGain = 1 / (120f - (chargeCap - charge)*5f);
                chargeGain *= RingOfEnergy.artifactChargeMultiplier(target);
                partialCharge += chargeGain;

                while (partialCharge >= 1) {
                    partialCharge --;
                    charge ++;

                    if (charge == chargeCap){
                        partialCharge = 0;
                    }
                }
            }

            updateQuickslot();

            spend( TICK );

            return true;
        }
    }

    public CellSelector.Listener cellSelector = new CellSelector.Listener(){

        @Override
        public void onSelect(Integer cell) {
            if (cell != null){

                if (!Dungeon.level.heroFOV[cell] || Dungeon.level.solid[cell]){
                    GLog.w(Messages.get(SandalsOfNature.class, "out_of_range"));
                } else if (fpotion != null){

                    if (cell == Dungeon.hero.pos) {
                        fpotion.apply(Dungeon.hero);
                        Sample.INSTANCE.play(Assets.Sounds.DRINK);
                    }
                    else fpotion.shatter(cell);

                    checkForArtifactProc(cell);

                    Talent.onArtifactUsed(Dungeon.hero);
                    updateQuickslot();
                    curUser.spendAndNext(1f);
                } else {
                    GLog.w(Messages.get(ElementalMask.class, "no_potion"));
                }
            }
        }

        @Override
        public String prompt() {
            return Messages.get(SandalsOfNature.class, "prompt_target");
        }
    };

}