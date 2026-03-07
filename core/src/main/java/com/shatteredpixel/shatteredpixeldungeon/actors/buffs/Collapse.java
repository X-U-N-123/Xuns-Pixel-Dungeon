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
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class Collapse extends Buff {

    {
        revivePersists = true;
    }

    private int totalTime = 0;
    private final int timePerLevel = 500;
    private final int[] hintTime = new int[]{10, 50, 100, 200, 300};

    @Override
    public String iconTextDisplay() {
        return Integer.toString(timeRemain());
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", timePerLevel, totalTime / timePerLevel, timeRemain());
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0, visualcooldown() / timePerLevel);
    }

    @Override
    public boolean act() {
        int magnitudeBefore = 0;
        for (int i : hintTime) {
            if (timeRemain() <= i) magnitudeBefore ++;
        }
        if (Regeneration.regenOn()) totalTime += TICK;
        spend(TICK);
        if (timeRemain() < timePerLevel){
            int magnitude = 0;
            for (int i : hintTime) {
                if (timeRemain() <= i) magnitude ++;
            }
            if (magnitude > magnitudeBefore){
                GLog.w(Messages.get(this, "danger"+ (magnitude-1) ));
                Sample.INSTANCE.play(Assets.Sounds.ROCKS, magnitude * 0.2f);
                GameScene.shake(magnitude, magnitude * 0.2f);
                ((Hero)target).interrupt();
            }
        }
        if (totalTime % timePerLevel == 0){
            GLog.n(Messages.get(this, "collapse", totalTime / timePerLevel));
            if (timeRemain() == 0) Sample.INSTANCE.play(Assets.Sounds.ROCKS);
            else                   Sample.INSTANCE.play(Assets.Sounds.ROCKS, 0.4f, 0.7f);
        }
        if (!canReturnTo(Dungeon.depth)) {
            if (Dungeon.bossLevel() || Dungeon.branch == 1) {
                target.damage(target.HT / 10, this);
                if (!target.isAlive()){
                    Badges.validateDeathFromFalling();

                    Dungeon.fail( CollapseDieTracker.class );
                    GLog.n( Messages.get(CollapseDieTracker.class, "ondeath") );
                }
            }
            else Chasm.heroFall(target.pos);
        }
        return true;
    }

    public boolean canReturnTo(int depth){
        return timeRemain(depth) > 0 || depth >= 25;
    }

    public int timeRemain(){
        return timeRemain(Dungeon.depth);
    }

    public int timeRemain(int depth){
        return depth * timePerLevel - totalTime;
    }

    public void incTotalTime(int time){
        totalTime += time;
    }

    private static final String TOTAL_TIME = "total_time";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(TOTAL_TIME, totalTime);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        totalTime = bundle.getInt(TOTAL_TIME);
    }

    @Override
    public int icon() {
        return BuffIndicator.COLLAPSE;
    }

    public static class CollapseDieTracker{}//only used to track the damage and death

}