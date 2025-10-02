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

package com.shatteredpixel.shatteredpixeldungeon.items.trinkets;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class MemberCard extends Trinket {

    {
        image = ItemSpriteSheet.MEMBER_CARD;
    }

    @Override
    protected int upgradeEnergyCost() {
        //6 -> 8(14) -> 12(26) -> 16(42)
        return 8+4*level();
    }

    @Override
    public String statsDesc() {
        String off = Messages.get(this, String.valueOf(buffedLvl()));
        if (isIdentified()){
            return Messages.get(this, "stats_desc", Messages.decimalFormat("#.##", 100*betterItemChance(buffedLvl())), off);
        } else {
            return Messages.get(this, "typical_stats_desc", Messages.decimalFormat("#.##", 100*betterItemChance(0)), off);
        }
    }

    public static float off() {
        return off(trinketLevel(MemberCard.class));
    }

    public static float off(int level) {
        if (level == -1){
            return 1f;
        } else {
            return 0.95f - 0.05f*level;
        }
    }

    public static float betterItemChance() {
        return betterItemChance(trinketLevel(MemberCard.class));
    }

    public static float betterItemChance(int level) {
        if (level == -1){
            return 0f;
        } else {
            return 0.15f + 0.15f*level;
        }
    }

}