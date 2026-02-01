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
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Visual;
import com.watabou.utils.Bundle;

public class WhirlpoolBuff extends Buff implements ActionIndicator.Action {

    {
        revivePersists = true;
    }

    private int CD = 0;

    @Override
    public boolean act(){
        if (CD > 0) CD --;
        spend(1f);
        ActionIndicator.refresh();
        return true;
    }

    public void decreaseCD(int amount) {
        CD = Math.max(CD - amount, 0);
        ActionIndicator.refresh();
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
        CD = bundle.getInt(COOLDOWN);
        ActionIndicator.setAction(this);
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
    public Visual secondaryVisual() {
        if (CD <= 0) return null;

        BitmapText txt = new BitmapText(PixelScene.pixelFont);
        txt.text( Integer.toString(CD) );
        txt.hardlight(0x1e6ad1);
        txt.measure();
        return txt;
    }

    @Override
    public int indicatorColor() {
        if (CD > 0) return 0x1a5098;
        return 0x1e6ad1;
    }

    @Override
    public void doAction(){
        if (CD > 0){
            GLog.w(Messages.get(this, "cd"));
            return;
        }

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
                CD += 76;
                ActionIndicator.refresh();
                Dungeon.hero.sprite.attack(cell);
                Dungeon.hero.spendAndNext(1f);
            }
        });
    }
}