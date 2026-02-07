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
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class Phantom extends Buff implements ActionIndicator.Action {

    {
        revivePersists = true;
    }

    private int CD = 0;

    public float getCD(){
        return CD;
    }

    @Override
    public boolean act() {
        if (CD > 0) CD --;
        spend(1f);
        ActionIndicator.refresh();
        return true;
    }

    public void reduceCD(int turn) {
        CD = Math.max(CD - turn, 0);
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
        return HeroIcon.MIRROR_IMAGE;
    }

    @Override
    public Visual secondaryVisual() {
        if (CD <= 0) return null;

        BitmapText txt = new BitmapText(PixelScene.pixelFont);
        txt.text( Integer.toString(CD) );
        txt.hardlight(0x5A00B2);
        txt.measure();
        return txt;
    }

    @Override
    public int indicatorColor() {
        if (CD > 0) return 0x48008E;
        return 0x5A00B2;
    }

    @Override
    public void doAction() {//spawn a mirror image beside Phantom
        if (CD > 0){
            GLog.w(Messages.get(this, "cd"));
            return;
        }
        Sample.INSTANCE.play(Assets.Sounds.MISS);
        summon();
    }

    public void summon(){
        if (ScrollOfMirrorImage.spawnImages((Hero)target, 1) > 0){
            CD += 50;
            ActionIndicator.refresh();
        } else GLog.w(Messages.get(this, "no_space"));
    }
}