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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Jieniu extends MeleeWeapon {

    {
        image = ItemSpriteSheet.Jieniu;
        hitSound = Assets.Sounds.HIT_SLASH;
        hitSoundPitch = 1.1f;

        tier = 4;
    }

    @Override
    public int max(int lvl) {
        return  Math.round(2f*(tier+2)+1) +    //13 base, down from 25
                lvl*(tier-1);                    //+3 scaling, down from +5
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        Buff.affect(defender, Bleeding.class).set(0.62f*damage);
        return super.proc( attacker, defender, damage );
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        Dinnerknife.cutAbility(hero, target, 0f, this, 3+buffedLvl());
    }

    @Override
    public String abilityInfo() {
        int debuffDuration = levelKnown ? Math.round(3f + buffedLvl()) : 3;
        if (levelKnown){
            return Messages.get(this, "ability_desc", augment.damageFactor(Math.round(min()*1f)), augment.damageFactor(Math.round(max()*1f)), debuffDuration);
        } else {
            return Messages.get(this, "typical_ability_desc", Math.round(min(0)*1f), Math.round(max(0)*1f), debuffDuration);
        }
    }

    @Override
    public String upgradeAbilityStat(int level) {
        return Integer.toString(3+level);
    }

}