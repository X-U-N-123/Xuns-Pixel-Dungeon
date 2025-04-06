/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * Xun's Pixel Dungeon
 * Copyright (C) 2025-2025 Jiarun Chen
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
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class Darkgoldsword extends MeleeWeapon{

    {
        image = ItemSpriteSheet.Darkgoldsword;
        hitSound = Assets.Sounds.HIT_SLASH;
        hitSoundPitch = 1f;

        tier = 4;
    }

    @Override
    public int max(int lvl) {
        return  4*(tier+1) +        //20 base, down from 25
                lvl*(tier+1);  //scaling unchanged
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (defender.HT <= damage) {
            defender.die(defender);//在最大生命值小于伤害值时使敌人死亡
        } else {defender.HT -= damage;}
        return super.proc(attacker, defender, damage);
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");}

    protected void duelistAbility(Hero hero, Integer target, float dmgMulti, int dmgBoost) {
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
                    if (hero.attack(enemy, dmgMulti, dmgBoost, Char.INFINITE_ACCURACY)){
                        Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
                    }

                    Invisibility.dispel();
                    hero.spendAndNext(hero.attackDelay());
                    if (!enemy.isAlive()){
                        onAbilityKill(hero, enemy);
                    } else {
                        enemy.HP -= max(buffedLvl());
                        enemy.HT -= max(buffedLvl());
                        if(enemy.HT <=0){enemy.die(enemy);}
                    }
                    afterAbilityUsed(hero);
                }
            });
        }
    }


