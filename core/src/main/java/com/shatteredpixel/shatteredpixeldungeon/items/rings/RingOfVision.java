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

package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class RingOfVision extends Ring {

    {
        icon = ItemSpriteSheet.Icons.RING_VISION;
        buffClass = Vision.class;
    }

    @Override
    public boolean doEquip(Hero hero) {
        if (super.doEquip(hero)){
            updateView();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        if (super.doUnequip(hero, collect, single)){
            updateView();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Item upgrade() {
        super.upgrade();
        updateView();
        return this;
    }

    @Override
    public void level(int value) {
        super.level(value);
        updateView();
    }

    private void updateView(){
        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
            if (mob.alignment == Char.Alignment.ALLY){
                Dungeon.level.updateFieldOfView(mob, mob.fieldOfView);
            }
        }
    }

    public String statsInfo() {
        if (isIdentified()){
            String info = Messages.get(this, "stats", soloBonus());
            if (isEquipped(Dungeon.hero) && soloBuffedBonus() != combinedBuffedBonus(Dungeon.hero)){
                info += "\n\n" + Messages.get(this, "combined_stats", getBonus(Dungeon.hero, Vision.class));
            }
            return info;
        } else {
            return Messages.get(this, "typical_stats", 1);
        }
    }

    @Override
    public String upgradeStat1(int level) {
        if (cursed && cursedKnown) level = Math.min(-1, level-3);
        return Integer.toString(level+1);
    }

    @Override
    protected RingBuff buff( ) {
        return new Vision();
    }

    public static int visionBonus(){
        return getBonus( Dungeon.hero, Vision.class );
    }

    public class Vision extends RingBuff {
    }
}