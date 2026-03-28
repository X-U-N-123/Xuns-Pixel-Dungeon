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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BrokenArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class HiddenBlade extends MeleeWeapon {

    private boolean locked = false;

    {
        image = ItemSpriteSheet.HIDDEN_BLADE;
        hitSound = Assets.Sounds.HIT_STAB;
        hitSoundPitch = 1f;

        tier = 3;
    }

    @Override
    public int max(int lvl) {
        return  4*(tier+1) +    //16 base, down from 20
                lvl*(tier+1);   //scaling unchanged
    }

    @Override
    public int damageRoll(Char owner) {
        if (owner instanceof Hero) {
            Hero hero = (Hero)owner;
            Char enemy = hero.attackTarget();
            if (enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero) && locked) {
                //deals 90% toward max to max on surprise, instead of min to max,
                //and gives the target 4 turns of broken armor.
                int diff = max() - min();
                int damage = augment.damageFactor(Hero.heroDamageIntRange(
                min() + Math.round(diff*0.9f), max()));
                int exStr = hero.STR() - STRReq();
                if (exStr > 0) {
                    damage += Hero.heroDamageIntRange(0, exStr);
                }
                Buff.affect(enemy, BrokenArmor.class, 4f);
                lock(false);
                return damage;
            }
        }
        lock(false);
        return super.damageRoll(owner);
    }

    public void lock(boolean lock){
        if (lock && !locked) {
            Sample.INSTANCE.play(Assets.Sounds.UNLOCK);
            Dungeon.hero.sprite.operate(Dungeon.hero.pos);
        }
        if (lock) image = ItemSpriteSheet.HIDDEN_BLADE_LOCKED;
        else      image = ItemSpriteSheet.HIDDEN_BLADE;
        locked = lock;
        Item.updateQuickslot();
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        beforeAbilityUsed(hero, null);
        Buff.affect(hero, Invisibility.class, 3 + buffedLvl()); // 1 less turn as the ability takes no tine
        lock(true);
        hero.next();
        CellEmitter.get( Dungeon.hero.pos ).burst( Speck.factory( Speck.WOOL ), 6 );
        Sample.INSTANCE.play( Assets.Sounds.PUFF );
        afterAbilityUsed(hero);
    }

    @Override
    public String abilityInfo() {
        if (levelKnown){
            return Messages.get(this, "ability_desc", 4+buffedLvl());
        } else {
            return Messages.get(this, "typical_ability_desc", 4);
        }
    }

    @Override
    public String upgradeAbilityStat(int level) {
        return Integer.toString(4+level);
    }

    private static final String LOAD = "loaded";

    @Override
    public void storeInBundle( Bundle bundle ) {
        super.storeInBundle( bundle );
        bundle.put(LOAD, locked );
    }

    @Override
    public void restoreFromBundle( Bundle bundle ) {
        super.restoreFromBundle( bundle );
        locked = bundle.getBoolean(LOAD);
        if (locked) {
            image = ItemSpriteSheet.HIDDEN_BLADE_LOCKED;
            Item.updateQuickslot();
        }
    }

}