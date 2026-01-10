package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.explorer;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class OpticalCamou extends ArmorAbility {

    {
        baseChargeUse = 35;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target){

        Buff.prolong(hero, Camouflage.class, 20 + 5 * Dungeon.hero.pointsInTalent(Talent.LASTING_DISGUISE));
        hero.sprite.operate(hero.pos);

        Sample.INSTANCE.play(Assets.Sounds.MELD, 0.5f);
        armor.charge -= chargeUse(hero);
        Item.updateQuickslot();
        hero.spendAndNext(Actor.TICK);
    }

    @Override
    public int icon() {
        return HeroIcon.OPTIMAL_CAMOU;
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.LASTING_DISGUISE, Talent.STRAIN_CAPACITY, Talent.PAINTED_BLADE, Talent.QUICK_BUILD, Talent.HEROIC_ENERGY};
    }
    
    public static class Camouflage extends FlavourBuff {

        {
            type = buffType.POSITIVE;
        }

        @Override
        public boolean attachTo( Char target ) {
            if (super.attachTo( target )) {
                target.invisible++;
                if (target instanceof Hero && ((Hero) target).hasTalent(Talent.PROTECTIVE_SHADOWS)){
                    Buff.affect(target, Talent.ProtectiveShadowsTracker.class);
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void detach() {
            if (target.invisible > 0)
                target.invisible--;
            super.detach();
        }

        @Override
        public void fx(boolean on) {
            if (on) target.sprite.add( CharSprite.State.INVISIBLE );
            else if (target.invisible == 0) target.sprite.remove( CharSprite.State.INVISIBLE );
        }

        private int terrain = 0;


        @Override
        public int icon() {
            return BuffIndicator.IMBUE;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0xcc7f0d);
        }

        public void move(int newTerrain){
            if (newTerrain != terrain){ //decrease time
                terrain = newTerrain;
                if (Random.Float() >= Dungeon.hero.pointsInTalent(Talent.STRAIN_CAPACITY) * 0.3f) spend(-1f);
                if (Random.Float() + 1 <= Dungeon.hero.pointsInTalent(Talent.STRAIN_CAPACITY) * 0.3f) spend(1f);
                /*if (camouTime <= 0f) {
                    detach();
                }*/
            }
        }

        private static final String TERRAIN   = "terrain";

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(TERRAIN, terrain);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            terrain   = bundle.getInt(TERRAIN);
        }
        
    }

}