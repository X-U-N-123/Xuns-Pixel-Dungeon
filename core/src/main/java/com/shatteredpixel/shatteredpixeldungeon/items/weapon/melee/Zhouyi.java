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
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class Zhouyi extends MeleeWeapon{

    {
        image = ItemSpriteSheet.ZHOUYI;
        hitSound = Assets.Sounds.HIT;
        hitSoundPitch = 0.9f;

        tier = 4;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (!defender.properties().contains(Char.Property.BOSS) && !defender.properties().contains(Char.Property.MINIBOSS)){
            float exchangechance = 0.2f;
            float atkhp = (float)attacker.HP / attacker.HT;
            float defhp = (float)defender.HP / defender.HT;
            exchangechance += (defhp - atkhp)/5f;//0% ~ 40%
            int atkaftHP = Math.round(attacker.HT * defhp);
            int defaftHP = Math.round(defender.HT * atkhp);

            if (Random.Float() < exchangechance && atkaftHP > 0 && defaftHP > 0 && attacker.alignment != defender.alignment){
                attacker.HP = atkaftHP;
                defender.HP = defaftHP;
                if (atkhp <= defhp){
                    attacker.sprite.emitter().start(Speck.factory(Speck.UP),   0.2f, 3);
                    defender.sprite.emitter().start(Speck.factory(Speck.DOWN), 0.2f, 3);
                } else {
                    attacker.sprite.emitter().start(Speck.factory(Speck.DOWN), 0.2f, 3);
                    defender.sprite.emitter().start(Speck.factory(Speck.UP),   0.2f, 3);
                }
            }
        }
        return super.proc( attacker, defender, damage );
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
                if (hero.attack(enemy, 1f, 0, Char.INFINITE_ACCURACY)){
                    Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
                }
                int heropos = hero.pos;
                int enemypos = enemy.pos;

                //can't swap into a space without room
                if ((!enemy.properties().contains(Char.Property.LARGE) || Dungeon.level.openSpace[heropos])
                && (Dungeon.level.passable[enemypos] || hero.flying)
                && !enemy.properties().contains(Char.Property.STATIC) && !enemy.properties().contains(Char.Property.IMMOVABLE)
                && !hero.rooted && !enemy.rooted){
                    enemy.pos = heropos;
                    hero.pos = enemypos;
                    ScrollOfTeleportation.appear(enemy, heropos);
                    ScrollOfTeleportation.appear(hero, enemypos);
                    enemy.move( heropos );
                    hero.move( enemypos );
                }

                Invisibility.dispel();

                if (!enemy.isAlive()){
                    hero.next();
                    onAbilityKill(hero, enemy);
                }
                float timeMulti = Random.NormalFloat(0, 1f/(level()+1));
                hero.spendAndNext(hero.attackDelay()*timeMulti);

                afterAbilityUsed(hero);
            }
        });
    }

    @Override
    public String abilityInfo() {
        float maxMulti = 1f/(buffedLvl()+1);
        if (levelKnown){
            return Messages.get(this, "ability_desc", maxMulti);
        } else {
            return Messages.get(this, "typical_ability_desc", maxMulti);
        }
    }

    public String upgradeAbilityStat(int level){
        return String.valueOf(1f/(level+1));
    }

}