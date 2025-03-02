package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Smallshield extends MeleeWeapon {

    {
        image = ItemSpriteSheet.Smallshield;
        hitSound = Assets.Sounds.HIT;
        hitSoundPitch = 1.1f;

        tier = 1;
    }

    @Override
    public int max(int lvl) {
        return  tier+6 +   //7 base, down from 10
                lvl*tier;  //+1 per level, down from +2
    }

    @Override
    public int defenseFactor( Char owner ) {
        return DRMax();
    }

    public int DRMax(){
        return DRMax(buffedLvl());
    }

    //2 extra defence, plus 1 per level
    public int DRMax(int lvl){
        return 2 + lvl;
    }

    public String statsInfo(){
        if (isIdentified()){
            return Messages.get(this, "stats_desc", 2+buffedLvl());
        } else {
            return Messages.get(this, "typical_stats_desc", 2);
        }
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        RoundShield.guardAbility(hero, 7+buffedLvl(), this);
    }

    @Override
    public String abilityInfo() {
        if (levelKnown){
            return Messages.get(this, "ability_desc", 7+buffedLvl());
        } else {
            return Messages.get(this, "typical_ability_desc", 7);
        }
    }

    @Override
    public String upgradeAbilityStat(int level) {
        return Integer.toString(7 + level);
    }

}
