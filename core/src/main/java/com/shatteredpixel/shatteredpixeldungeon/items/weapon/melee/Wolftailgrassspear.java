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

public class Wolftailgrassspear extends MeleeWeapon {

    {
        image = ItemSpriteSheet.Wolftailgrassspear;
        hitSound = Assets.Sounds.HIT_STAB;
        hitSoundPitch = 0.9f;

        tier = 4;
        RCH = 3;    //lots of extra reach
    }

    @Override
    public int max(int lvl) {
        return  6*(tier-1) +      //18 base, down from 25
                lvl*(tier);     //+4 per level, down from +5
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        Whip.lashAbility(hero, this);
    }

    @Override
    public String abilityInfo() {
        if (levelKnown){
            return Messages.get(this, "ability_desc", augment.damageFactor(min()), augment.damageFactor(max()));
        } else {
            return Messages.get(this, "typical_ability_desc", min(0), max(0));
        }
    }

    public String upgradeAbilityStat(int level){
        return augment.damageFactor(min(level)) + "-" + augment.damageFactor(max(level));
    }
}
