package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.explorer;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
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

        Buff.prolong(hero, Camouflage.class, 15 + 3 * Dungeon.hero.pointsInTalent(Talent.LASTING_DISGUISE));
		if (hero.hasTalent(Talent.ENERGY_SURPLUS)){
			Buff.affect(hero, Recharging.class, 2 * hero.pointsInTalent(Talent.ENERGY_SURPLUS));
		}
        hero.sprite.operate(hero.pos);

        Sample.INSTANCE.play(Assets.Sounds.MELD, 0.5f);
        armor.charge -= chargeUse(hero);
        Item.updateQuickslot();
        hero.spendAndNext(Actor.TICK);
    }

    @Override
    public float chargeUse( Hero hero ) {
        float chargeUse = super.chargeUse(hero);
        if (hero.buff(Camouflage.class) != null){
            //reduced charge use by 12%/24%/36%/58%
            chargeUse *= 1 - 0.12f * hero.pointsInTalent(Talent.STANDBY);
        }
        return chargeUse;
    }

    @Override
    public int icon() {
        return HeroIcon.OPTIMAL_CAMOU;
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.LASTING_DISGUISE, Talent.STRAIN_CAPACITY, Talent.STANDBY, Talent.ENERGY_SURPLUS, Talent.HEROIC_ENERGY};
    }
    
    public static class Camouflage extends Invisibility {

        {
            announced = false;
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