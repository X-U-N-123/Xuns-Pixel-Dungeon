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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class LightFlail extends MeleeWeapon{

    {
        image = ItemSpriteSheet.LightFlail;
        hitSound = Assets.Sounds.HIT_CRUSH;
        hitSoundPitch = 0.9f;

        tier = 3;
        ACC = 0.84f; //0.84x accuracy
        //also cannot surprise attack, see Hero.canSurpriseAttack
    }

    @Override
    public int max(int lvl) {
        return  Math.round(7*(tier+1)) +        //28 base, up from 20
                lvl*Math.round(1.75f*(tier+1));  //+7 per level, up from +4
    }

    private static int spinBoost = 0;

    @Override
    public int damageRoll(Char owner) {
        int dmg = super.damageRoll(owner) + spinBoost;
        if (spinBoost > 0) Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
        spinBoost = 0;
        return dmg;
    }

    @Override
    public float accuracyFactor(Char owner, Char target) {
        Flail.SpinAbilityTracker spin = owner.buff(Flail.SpinAbilityTracker.class);
        if (spin != null) {
            Actor.add(new Actor() {
                { actPriority = VFX_PRIO; }
                @Override
                protected boolean act() {
                    if (owner instanceof Hero && !target.isAlive()){
                        onAbilityKill((Hero)owner, target);
                    }
                    Actor.remove(this);
                    return true;
                }
            });
            //we detach and calculate bonus here in case the attack misses (e.g. vs. monks)
            spin.detach();
            //+(7+2*lvl) damage per spin, roughly +45.2% base damage, +57.1% scaling
            // so +135.5% base dmg, +171.4% scaling at 3 spins
            spinBoost = spin.spins * augment.damageFactor(7 + 2*buffedLvl());
            return Float.POSITIVE_INFINITY;
        } else {
            spinBoost = 0;
            return super.accuracyFactor(owner, target);
        }
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {

        Flail.SpinAbilityTracker spin = hero.buff(Flail.SpinAbilityTracker.class);
        if (spin != null && spin.spins >= 2){
            GLog.w(Messages.get(this, "spin_warn"));
            return;
        }

        beforeAbilityUsed(hero, null);
        if (spin == null){
            spin = Buff.affect(hero, Flail.SpinAbilityTracker.class, 3f);
        }

        spin.spins++;
        Buff.prolong(hero, Flail.SpinAbilityTracker.class, 3f);
        Sample.INSTANCE.play(Assets.Sounds.CHAINS, 1, 1, 1f + 0.1f*spin.spins);
        hero.sprite.operate(hero.pos);
        hero.spendAndNext(Actor.TICK);
        BuffIndicator.refreshHero();

        afterAbilityUsed(hero);
    }

    @Override
    public String abilityInfo() {
        int dmgBoost = levelKnown ? 7 + 2*buffedLvl() : 7;
        if (levelKnown){
            return Messages.get(this, "ability_desc", augment.damageFactor(dmgBoost));
        } else {
            return Messages.get(this, "typical_ability_desc", augment.damageFactor(dmgBoost));
        }
    }

    public String upgradeAbilityStat(int level){
        return "+" + augment.damageFactor(7 + 2*level);
    }

}