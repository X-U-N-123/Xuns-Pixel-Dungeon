/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
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

public class Hoe extends MeleeWeapon {

    {
        image = ItemSpriteSheet.Hoe;
        hitSound = Assets.Sounds.HIT_STAB;
        hitSoundPitch = 1.1f;

        tier = 4;
        ACC = 0.72f; //28% penalty to accuracy
    }

    @Override
    public int max(int lvl) {
        return  Math.round(11f*(tier-1)) +    //33 base, up from 25
                lvl*(tier+1);                //scaling unchanged
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        //replaces damage with 25+4*lvl bleed, roughly 133% avg base dmg, 133% avg scaling
        int bleedAmt = augment.damageFactor(Math.round(25f + 4f*buffedLvl()));
        Sickle.harvestAbility(hero, target, 0f, bleedAmt, this);
    }

    @Override
    public String abilityInfo() {
        int bleedAmt = levelKnown ? Math.round(25f + 4f * buffedLvl()) : 25;
        if (levelKnown) {
            return Messages.get(this, "ability_desc", augment.damageFactor(bleedAmt));
        } else {
            return Messages.get(this, "typical_ability_desc", bleedAmt);
        }
    }
}