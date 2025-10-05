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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class Phantom extends Buff implements ActionIndicator.Action {

    {
        type = buffType.POSITIVE;
        revivePersists = true;
    }

    private float CD = 0;

    @Override
    public int icon() {
        return BuffIndicator.IMBUE;
    }

    @Override
    public void tintIcon(Image icon) {
        if (CD <= 0) icon.hardlight(1.6f, 0.4f, 2f);
        else         icon.hardlight(1.2f, 0.3f, 1.5f);
    }

    @Override
    public String iconTextDisplay() {
        return Integer.toString((int)CD);
    }

    @Override
    public String desc() {
        if (CD > 0) return Messages.get(this, "desc", CD);
        else return Messages.get(this, "desc_ready");
    }

    public float getCD(){
        return CD;
    }

    @Override
    public boolean act() {
        CD -= TICK;
        spend(TICK);
        if (CD <= 0f) {
            ActionIndicator.setAction(this);
            CD = 0f;
        }
        return true;
    }

    public void reduceCD(float turn) {
        CD -= turn;
        if (CD <= 0f) {
            ActionIndicator.setAction(this);
            CD = 0f;
        }
    }

    private static final String COOLDOWN = "cd";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(COOLDOWN, CD);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        CD = bundle.getFloat(COOLDOWN);
        if (CD <= 0) ActionIndicator.setAction(this);
    }

    @Override
    public String actionName() {
        return Messages.get(this, "action_name");
    }

    @Override
    public int actionIcon() {
        return HeroIcon.MIRROR_IMAGE;
    }

    @Override
    public int indicatorColor() {
        return 0x5A00B2;
    }

    @Override
    public void doAction() {
        Sample.INSTANCE.play(Assets.Sounds.MISS);
        summon();
    }

    public int summon(){
        CD += 50f;
        ActionIndicator.clearAction();
        BuffIndicator.refreshHero();
        return ScrollOfMirrorImage.spawnImages((Hero)target, 1);
    }

}