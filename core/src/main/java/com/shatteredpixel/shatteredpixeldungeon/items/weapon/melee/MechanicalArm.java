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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class MechanicalArm extends MeleeWeapon {

    {
        image = ItemSpriteSheet.MECH_ARM;
        hitSound = Assets.Sounds.HIT_STAB;
        hitSoundPitch = 1f;

        tier = 1;
        DLY = 1.5f; //0.67x speed
        RCH = 2;    //extra reach
    }

    @Override
    public int max(int lvl) {
        return  Math.round(7f*(tier+1)) +    //14 base, up from 10
                lvl*Math.round(1.5f*(tier+1)); //+3 per level, up from +2
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        //+(7+2*lvl) damage, roughly +100% base damage, +100% scaling
        int dmgBoost = augment.damageFactor(8 + Math.round(2f*buffedLvl()));
        Spear.spikeAbility(hero, target, 1, dmgBoost, this);
    }

    public String upgradeAbilityStat(int level){
        int dmgBoost = 8 + Math.round(2f*level);
        return augment.damageFactor(min(level)+dmgBoost) + "-" + augment.damageFactor(max(level)+dmgBoost);
    }

    @Override
    public String abilityInfo() {
        int dmgBoost = levelKnown ? 8 + Math.round(2f*buffedLvl()) : 8;
        if (levelKnown){
            return Messages.get(this, "ability_desc", augment.damageFactor(min()+dmgBoost), augment.damageFactor(max()+dmgBoost));
        } else {
            return Messages.get(this, "typical_ability_desc", min(0)+dmgBoost, max(0)+dmgBoost);
        }
    }

}
