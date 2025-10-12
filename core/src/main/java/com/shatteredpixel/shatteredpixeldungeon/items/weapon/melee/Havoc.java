package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Brute;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Ghoul;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;

public class Havoc extends MeleeWeapon {

    public int Enemieskilled = 0;

    {
        image = ItemSpriteSheet.Havoc;
        hitSound = Assets.Sounds.HIT_SLASH;
        hitSoundPitch = 1.2f;

        tier = 2;
    }

    @Override
    public int min(int lvl) {//every 5 killed enemy increase its min dmg by 1 point, no scaling
        return Math.min(1 + (int)(Enemieskilled / 5f), max());
    }

    @Override
    public int max(int lvl) {
        return  4 * (tier + 1) +    //10 base, down from 15
                lvl*(tier + 1);     //scaling unchanged
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if(defender.HP<=damage && defender.buff(Brute.BruteRage.class)==null && defender.buff(Ghoul.Revivetracker.class)==null){//preventting brute and ghoul
            Enemieskilled++;
        }
        return super.proc( attacker, defender, damage );
    }

    @Override
    public String statsInfo()
        {return Messages.get(this, "stats_desc", Enemieskilled);}

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        //+(2+lvl) damage, roughly +40% base dmg, +100% scaling
        int dmgBoost = augment.damageFactor(2 + buffedLvl());
        if (target == null) {
            return;
        }

        Char enemy = Actor.findChar(target);
        if (enemy == null || enemy == hero || hero.isCharmedBy(enemy) || !Dungeon.level.heroFOV[target]) {
            GLog.w(Messages.get(this, "ability_no_target"));
            return;
        }

        hero.belongings.abilityWeapon = this;
        if (!hero.canAttack(enemy)){
            GLog.w(Messages.get(this, "ability_target_range"));
            hero.belongings.abilityWeapon = null;
            return;
        }
        hero.belongings.abilityWeapon = null;

        hero.sprite.attack(enemy.pos, new Callback() {
            @Override
            public void call() {
                beforeAbilityUsed(hero, enemy);
                AttackIndicator.target(enemy);
                if (hero.attack(enemy, 1, dmgBoost, Char.INFINITE_ACCURACY)){
                    Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
                }

                Invisibility.dispel();

                if (!enemy.isAlive()){
                    hero.next();
                    onAbilityKill(hero, enemy);
                } else {
                    Enemieskilled ++;
                }
                hero.spendAndNext(hero.attackDelay());

                afterAbilityUsed(hero);
            }
        });
    }

    @Override
    public String abilityInfo() {
        int dmgBoost = levelKnown ? 2 + buffedLvl() : 2;
        if (levelKnown){
            return Messages.get(this, "ability_desc", augment.damageFactor(min()+dmgBoost), augment.damageFactor(max()+dmgBoost));
        } else {
            return Messages.get(this, "typical_ability_desc", min(0)+dmgBoost, max(0)+dmgBoost);
        }
    }

    public String upgradeAbilityStat(int level){
        int dmgBoost = 2 + level;
        return augment.damageFactor(min(level)+dmgBoost) + "-" + augment.damageFactor(max(level)+dmgBoost);
    }

    private static final String ENEMIESKILLED = "Enemies_Killed";

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put( ENEMIESKILLED, Enemieskilled );
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        Enemieskilled = bundle.getInt(ENEMIESKILLED);
    }

}