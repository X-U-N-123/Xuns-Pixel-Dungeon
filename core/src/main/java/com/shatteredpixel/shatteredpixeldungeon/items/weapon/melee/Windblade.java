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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Callback;

import java.util.ArrayList;

public class Windblade extends MeleeWeapon {

    {
        image = ItemSpriteSheet.Windblade;
        hitSound = Assets.Sounds.HIT_SLASH;
        hitSoundPitch = 1.3f;

        tier = 3;
    }

    @Override
    public int max(int lvl) {
        return  5*(tier) +    //15 base, down from 20
                lvl*tier;       //+3 per level, down from +4
    }

    @Override
    public int reachFactor(Char owner){
        return super.reachFactor(owner) + level();
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        attacker.sprite.emitter().burst(Speck.factory(Speck.JET), (int)Math.min(15, Math.ceil(damage+1/4f)));
        return super.proc( attacker, defender, damage );
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {

        ArrayList<Char> targets = new ArrayList<>();
        Char closest = null;

        hero.belongings.abilityWeapon = this;
        for (Char ch : Actor.chars()){
            if (ch.alignment == Char.Alignment.ENEMY
                    && !hero.isCharmedBy(ch)
                    && Dungeon.level.heroFOV[ch.pos]
                    && hero.canAttack(ch)){
                targets.add(ch);
                if (closest == null || Dungeon.level.trueDistance(hero.pos, closest.pos) > Dungeon.level.trueDistance(hero.pos, ch.pos)){
                    closest = ch;
                }
            }
        }
        hero.belongings.abilityWeapon = null;

        if (targets.isEmpty()) {
            GLog.w(Messages.get(this, "ability_no_target"));
            return;
        }

        throwSound();
        Char finalClosest = closest;
        hero.sprite.attack(hero.pos, new Callback() {
            @Override
            public void call() {
                beforeAbilityUsed(hero, finalClosest);
                for (Char ch : targets) {
                    //ability does 10% less base damage
                    hero.attack(ch, 1f, -2, Char.INFINITE_ACCURACY);
                    ch.sprite.emitter().burst(Speck.factory(Speck.JET), 15);
                    if (ch.isAlive()) {
                        //trace a ballistica to our target (which will also extend past them
                        Ballistica trajectory = new Ballistica(hero.pos, ch.pos, Ballistica.STOP_TARGET);
                        //trim it to just be the part that goes past them
                        trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size() - 1), Ballistica.PROJECTILE);
                        //knock them back along that ballistica
                        WandOfBlastWave.throwChar(ch, trajectory, 2, true, true, hero);
                    } else {
                        onAbilityKill(hero, ch);
                    }
                }
                Invisibility.dispel();
                hero.spendAndNext(hero.attackDelay());
                afterAbilityUsed(hero);
            }
        });
    }

    @Override
    public String abilityInfo() {
        if (levelKnown){
            return Messages.get(this, "ability_desc", augment.damageFactor(min())-2, augment.damageFactor(max())-2);
        } else {
            return Messages.get(this, "typical_ability_desc", min(0)-2, max(0)-2);
        }
    }

    public String upgradeAbilityStat(int level){
        return augment.damageFactor(min(level)-2) + "-" + augment.damageFactor(max(level)-2);
    }

}