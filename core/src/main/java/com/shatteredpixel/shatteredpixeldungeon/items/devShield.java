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

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;

import java.util.ArrayList;

public class devShield extends Item {

    String AC_SHIELD = "shield";

    {
        defaultAction = AC_SHIELD;
        image = ItemSpriteSheet.SHIELD;
        cursedKnown = levelKnown = true;
        unique = true;
        bones = false;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_SHIELD);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);
        if (action.equals(AC_SHIELD)) {
            if (hero.buff(devShieldBuff.class) == null) Buff.affect(hero, devShieldBuff.class);
            else                                    hero.buff(devShieldBuff.class).detach();
        }
    }

    public static class devShieldBuff extends Buff{

        {
            type = buffType.POSITIVE;
        }

        @Override
        public int icon() {
            return BuffIndicator.ARMOR;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0.5f, 1f, 2.5f);
        }

        @Override
        public void fx(boolean on) {
            if (!target.buffs(ChampionEnemy.class).isEmpty()) return;
            if (on) target.sprite.aura( 0x1111FF, 6 );
            else target.sprite.clearAura();
        }
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public float weight(){
        return 0;
    }

}