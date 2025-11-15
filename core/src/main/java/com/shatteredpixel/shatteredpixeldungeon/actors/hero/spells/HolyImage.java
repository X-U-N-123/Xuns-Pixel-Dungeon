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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

public class HolyImage extends ClericSpell {

    public static final HolyImage INSTANCE = new HolyImage();

    @Override
    public int icon() {
        return HeroIcon.HOLY_IMAGE;
    }

    @Override
    public String desc() {
        int quantity = 2 + Dungeon.hero.pointsInTalent(Talent.HOLY_IMAGE);
        return Messages.get(this, "desc", quantity) +"\n\n"+ Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.hasTalent(Talent.HOLY_IMAGE);
    }

    @Override
    public float chargeUse(Hero hero) {
        return 3;
    }

    @Override
    public void onCast(HolyTome tome, Hero hero) {
        ScrollOfMirrorImage.spawnImages(hero, 2 + Dungeon.hero.pointsInTalent(Talent.HOLY_IMAGE));
        onSpellCast(tome, hero);
    }

}