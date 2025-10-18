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

public class Longrunicblade extends MeleeWeapon {

    {
        image = ItemSpriteSheet.Longrunicblade;
        hitSound = Assets.Sounds.HIT_SLASH;
        hitSoundPitch = 1f;

        tier = 5;
    }

    @Override
    public int max(int lvl) {
        return  5*(tier) +                	//24 base, down from 30
                Math.round(lvl*(tier+2));	//+7 per level, up from +6
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        RunicBlade.runicSlashAbility(hero, target, this, 2.5f);
    }

    @Override
    public String abilityInfo() {
        if (levelKnown){
            return Messages.get(this, "ability_desc", 250+50*buffedLvl());
        } else {
            return Messages.get(this, "typical_ability_desc", 250);
        }
    }

    @Override
    public String upgradeAbilityStat(int level) {
        return "+" + (250+50*level) + "%";
    }

}
