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

public class Twobroadaxe extends MeleeWeapon {

    {
        image = ItemSpriteSheet.Twobroadaxe;
        hitSound = Assets.Sounds.HIT_SLASH;
        hitSoundPitch = 1f;

        tier = 4;
        DLY = 0.5f; //2x speed
    }

    @Override
    public int max(int lvl) {
        return  tier+9 +     //13 base, down from 25
                lvl*(tier-1);  //+3 per level, down from +5
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        //+(3+lvl) damage, roughly +40% base damage, +50% scaling
        int dmgBoost = augment.damageFactor(3 + Math.round(1f*buffedLvl()));
        Sai.comboStrikeAbility(hero, target, 0, dmgBoost, this);
    }

    @Override
    public String abilityInfo() {
        int dmgBoost = levelKnown ? 3 + Math.round(1f*buffedLvl()) : 3;
        if (levelKnown){
            return Messages.get(this, "ability_desc", augment.damageFactor(dmgBoost));
        } else {
            return Messages.get(this, "typical_ability_desc", augment.damageFactor(dmgBoost));
        }
    }

    public String upgradeAbilityStat(int level){
        return "+" + augment.damageFactor(3 + Math.round(1f*level));
    }

}
