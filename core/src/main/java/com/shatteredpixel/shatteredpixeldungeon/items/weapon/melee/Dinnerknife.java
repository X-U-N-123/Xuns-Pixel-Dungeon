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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class Dinnerknife extends MeleeWeapon {

    {
        image = ItemSpriteSheet.Dinnerknife;
        hitSound = Assets.Sounds.HIT_SLASH;
        hitSoundPitch = 1.2f;

        tier = 1;
    }

    @Override
    public int max(int lvl) {
        return  Math.round(2.5f*(tier+1)) +    //5 base, up from 10
                lvl*(tier);                    //+1 scaling, down from +2
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (defender.buff(Cutabilitytracker.class) == null){
            Buff.affect(defender, Bleeding.class).set(0.9f*damage);
        }
        return super.proc( attacker, defender, damage );
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        cutAbility(hero, target, this, 5+buffedLvl());
    }

    public static void cutAbility(Hero hero, Integer target, MeleeWeapon wep, int debuffDuration){
        if (target == null) {
            return;
        }

        Char enemy = Actor.findChar(target);
        if (enemy == null || enemy == hero || hero.isCharmedBy(enemy) || !Dungeon.level.heroFOV[target]) {
            GLog.w(Messages.get(wep, "ability_no_target"));
            return;
        }

        hero.belongings.abilityWeapon = wep;
        if (!hero.canAttack(enemy)){
            GLog.w(Messages.get(wep, "ability_bad_position"));
            hero.belongings.abilityWeapon = null;
            return;
        }
        hero.belongings.abilityWeapon = null;

        hero.sprite.attack(enemy.pos, new Callback() {
            @Override
            public void call() {
                wep.beforeAbilityUsed(hero, enemy);
                AttackIndicator.target(enemy);
                Buff.affect(enemy, Cutabilitytracker.class, 0f);
                if (hero.attack(enemy, 2, 0, Char.INFINITE_ACCURACY)){
                    Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
                }

                Invisibility.dispel();
                hero.spendAndNext(hero.attackDelay());
                if (!enemy.isAlive()){
                    wep.onAbilityKill(hero, enemy);
                } else {
                    Buff.prolong(enemy, Vulnerable.class, debuffDuration);
                    Buff.prolong(enemy, Cripple.class, debuffDuration);
                }
                wep.afterAbilityUsed(hero);
            }
        });
    }

    @Override
    public String abilityInfo() {
        int debuffDuration = levelKnown ? Math.round(5f + buffedLvl()) : 5;
        if (levelKnown){
            return Messages.get(this, "ability_desc", augment.damageFactor(Math.round(min()*2f)), augment.damageFactor(Math.round(max()*2f)), debuffDuration);
        } else {
            return Messages.get(this, "typical_ability_desc", Math.round(min(0)*2f), Math.round(max(0)*2f), debuffDuration);
        }
    }

    @Override
    public String upgradeAbilityStat(int level) {
        return Integer.toString(5+level);
    }

    public static class Cutabilitytracker extends FlavourBuff{}

}
