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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShieldBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class BladeOfUnreal extends MeleeWeapon {

    boolean isReal = true;

    public static final String AC_SWITCH = "SWITCH";

    {
        image = ItemSpriteSheet.BLADE_OF_REAL;
        hitSound = Assets.Sounds.HIT_SLASH;
        hitSoundPitch = 1.2f;

        tier = 5;

        unique = true;
        bones = false;

        defaultAction = AC_SWITCH;
    }

    @Override
    public int max(int lvl) {
        return  4*(tier+1) +    //24 base, down from 30
                lvl*(tier);   //+5 scaling, down from +6
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions( hero );
        actions.add(AC_SWITCH);
        return actions;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (isReal){
            if (attacker == Dungeon.hero){
                int exStr = Dungeon.hero.STR() - STRReq();
                if (exStr < 0) {
                    exStr = 0;
                }
                damage = Random.NormalIntRange(min(), max() + exStr);
                int toHeal = Math.round(0.1f * damage);
                if (((Hero) attacker).pointsInTalent(Talent.BLADE_OF_UNREAL) > 2) {
                    toHeal = Math.round(0.15f * damage);
                }
                attacker.HP = Math.min(attacker.HP + toHeal, attacker.HT);
                attacker.sprite.showStatusWithIcon(CharSprite.POSITIVE, String.valueOf(toHeal), FloatingText.HEALING);
                defender.sprite.showStatusWithIcon(CharSprite.NEGATIVE, String.valueOf(damage), FloatingText.REALITY);
                damage = ShieldBuff.processDamage(defender, damage, this);

                if (defender.isAlive()) {
                    defender.die(this);
                } else {
                    defender.HP -= damage;
                }
                damage = 0;
            }
        }
        return super.proc( attacker, defender, damage );
    }

    @Override
    public int reachFactor(Char owner) {
        int reach = super.reachFactor(owner);
        if (!isReal) {
            reach ++;
            if (owner instanceof Hero && ((Hero) owner).pointsInTalent(Talent.BLADE_OF_UNREAL) > 2) reach ++;
        }
        return reach;
    }

    @Override
    public String info() {
        String isRealDesc;
        if (isReal) isRealDesc = Messages.get(this, "real");
        else        isRealDesc = Messages.get(this, "unreal");
        return super.info() + isRealDesc;
    }

    @Override
    public String abilityInfo() {
        return "";
    }

    @Override
    public int damageRoll(Char owner) {
        if (owner instanceof Hero) {
            Hero hero = (Hero)owner;
            Char enemy = hero.attackTarget();
            if (enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)) {
                //deals 80% toward max to max on surprise, instead of min to max.
                int diff = max() - min();
                int damage = augment.damageFactor(Hero.heroDamageIntRange(
                min() + Math.round(diff*0.85f),
                max()));
                int exStr = hero.STR() - STRReq();
                if (exStr > 0) {
                    damage += Hero.heroDamageIntRange(0, exStr);
                }
                return damage;
            }
        }
        return super.damageRoll(owner);
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);

        if (action.equals(AC_SWITCH)){
            isReal = !isReal;
            if (isReal) {
                image = ItemSpriteSheet.BLADE_OF_REAL;
            } else {
                image = ItemSpriteSheet.BLADE_OF_UNREAL;
            }
            Sample.INSTANCE.play(Assets.Sounds.MELD, 0.5f);
            updateQuickslot();
            AttackIndicator.updateState();
        }
    }

    private static final String IS_REAL = "is_real";

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put(IS_REAL, isReal);
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        isReal = bundle.getBoolean(IS_REAL);
        if (isReal) {
            image = ItemSpriteSheet.BLADE_OF_REAL;
        } else {
            image = ItemSpriteSheet.BLADE_OF_UNREAL;
        }
    }
}
