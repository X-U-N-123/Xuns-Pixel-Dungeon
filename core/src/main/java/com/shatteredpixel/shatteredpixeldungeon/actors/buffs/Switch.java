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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.utils.Bundle;

public class Switch extends FlavourBuff {

    public static float DURATION = 4f;
    public boolean used = false;

    {
        type = buffType.POSITIVE;
        announced = true;
    }

    public int staffLevel = 0;
    private Wand wandJustApplied; //we don't bundle this as it's only used right as the buff is applied

    public void setup(Wand wand){
        this.wandJustApplied = wand;
    }

    @Override
    public void detach() {
        super.detach();
        if (!used) ArtifactRecharge.chargeArtifacts(Dungeon.hero, Dungeon.hero.pointsInTalent(Talent.MYSTICAL_SWITCH)*2f/3f);
        Item.updateQuickslot();
    }

    //this is used briefly so that a wand of magic missile can't clear the buff it just applied
    public Wand wandJustApplied(){
        Wand result = this.wandJustApplied;
        this.wandJustApplied = null;
        return result;
    }

    @Override
    public int icon() {
        return BuffIndicator.SWITCH;
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0, (DURATION - visualcooldown()) / DURATION);
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", 2 + Dungeon.hero.pointsInTalent(Talent.SWITCH_MASTER), staffLevel);
    }

    private static final String LEVEL = "level";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEVEL, staffLevel);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        staffLevel = bundle.getInt(LEVEL);
    }

}