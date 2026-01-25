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
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Whirlpool;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class WhirlpoolBuff extends Buff implements ActionIndicator.Action {

    {
        revivePersists = true;
    }

    private float CD = 0;

    @Override
    public int icon() {
        if (CD <= 0) return BuffIndicator.NONE;
        else         return BuffIndicator.TIME;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(0x2364bc);
    }

    @Override
    public String iconTextDisplay() {
        return Integer.toString((int)CD);
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", CD);
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

    public void decreaseCD(int amount) {
        CD -= amount;
        if (CD <= 0) {
            CD = 0;
            ActionIndicator.setAction(this);
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
        return HeroIcon.WHIRLPOOL;
    }

    @Override
    public int indicatorColor() {
        return 0x2364bc;
    }

    @Override
    public void doAction(){
        GameScene.selectCell(new CellSelector.Listener() {
            @Override
            public String prompt() {
                return Messages.get(this, "prompt");
            }

            @Override
            public void onSelect(Integer cell) {
                if (cell == null) return;

                if (!Dungeon.level.heroFOV[cell] || Dungeon.level.map[cell] != Terrain.WATER){
                    GLog.w(Messages.get(this, "invalid_pos"));
                    return;
                }

                GameScene.add(Blob.seed(cell, 10, Whirlpool.class));
                CD += 101;
                ActionIndicator.clearAction();
                Dungeon.hero.sprite.attack(cell);
                Dungeon.hero.spendAndNext(1f);
            }
        });
    }
}