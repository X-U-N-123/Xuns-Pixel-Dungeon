package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSight;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class Lightsword extends MeleeWeapon{

    {
        image = ItemSpriteSheet.Lightsword;
        hitSound = Assets.Sounds.HIT_SLASH;
        hitSoundPitch = 1.1f;

        tier = 5;
    }

    @Override
    public int max(int lvl) {
        return  5*(tier) +    //25 base, down from 30
                lvl*(tier+1); //scaling unchanged
    }

    @Override
    public String statsInfo(){
        if (isIdentified()){
            return Messages.get(this, "stats_desc", 3+buffedLvl());
        } else {
            return Messages.get(this, "typical_stats_desc", 3);
        }
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        Buff.affect(attacker, Light.class, 3 + buffedLvl());
        if ((defender.properties().contains(Char.Property.DEMONIC) || defender.properties().contains(Char.Property.UNDEAD))){
            defender.sprite.emitter().start( ShadowParticle.UP, 0.05f, 10 );
            Sample.INSTANCE.play( Assets.Sounds.BURNING );
        }
        return super.proc( attacker, defender, damage );
    }

    @Override
    public float accuracyFactor(Char owner, Char target) {
        if ((owner != Dungeon.hero || Dungeon.hero.STR >= STRReq())
        && (target.properties().contains(Char.Property.UNDEAD) || target.properties().contains(Char.Property.DEMONIC))) {
            return Float.POSITIVE_INFINITY;
        } else {
            return super.accuracyFactor(owner, target);
        }
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
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
                if (hero.attack(enemy, (enemy.properties().contains(Char.Property.DEMONIC) || enemy.properties().contains(Char.Property.UNDEAD)) ? 1.5f : 1f,
                        0, Char.INFINITE_ACCURACY)){
                    Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
                    Buff.affect(hero, Light.class, 2f+buffedLvl());
                    Buff.affect(hero, MagicalSight.class, 2f);
                }

                Invisibility.dispel();

                if (!enemy.isAlive()){
                    hero.next();
                    onAbilityKill(hero, enemy);
                }
                hero.spendAndNext(hero.attackDelay());
                afterAbilityUsed(hero);
            }
        });
    }

    @Override
    public String abilityInfo() {
        float Multi = 1.5f;
        if (levelKnown){
            return Messages.get(this, "ability_desc", augment.damageFactor(Math.round(min()*Multi)), augment.damageFactor(Math.round(max()*Multi)), 5+2*buffedLvl());
        } else {
            return Messages.get(this, "typical_ability_desc", Math.round(min(0)*Multi), Math.round(max(0)*Multi), 5);
        }
    }

    @Override
    public String upgradeAbilityStat(int level) {
        return Integer.toString(5+level);
    }

}