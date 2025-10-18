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

public class Greatrunicblade extends MeleeWeapon {

    {
        image = ItemSpriteSheet.Greatrunicblade;
        hitSound = Assets.Sounds.HIT_SLASH;
        hitSoundPitch = 1f;

        tier = 6;
    }

    @Override
    public int max(int lvl) {
        return  4*(tier+1) +                	//28 base, down from 35
                Math.round(lvl*(tier+2));	//+8 per level, up from +7
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        RunicBlade.runicSlashAbility(hero, target, this, 2f);
    }

    @Override
    public String abilityInfo() {
        if (levelKnown){
            return Messages.get(this, "ability_desc", 200+50*buffedLvl());
        } else {
            return Messages.get(this, "typical_ability_desc", 200);
        }
    }

    @Override
    public String upgradeAbilityStat(int level) {
        return "+" + (200+50*level) + "%";
    }

}