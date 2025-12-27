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

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.BlazingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.BurningTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ChillingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ConfusionTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.CorrosionTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisintegrationTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ExplosiveTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FlashingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FlockTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FrostTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GatewayTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GeyserTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrippingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.OozeTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PoisonDartTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.RockfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ShockingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.StormTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.TeleportationTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ToxicTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WornDartTrap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.TerrainFeaturesTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBuildTrap;
import com.watabou.noosa.Visual;
import com.watabou.utils.Reflection;

import java.util.LinkedHashMap;

public class TrapChoose extends Buff implements ActionIndicator.Action {

    public Class<? extends Trap> trapClass = WornDartTrap.class;

    {
        revivePersists = true;
    }

    @Override
    public boolean attachTo( Char target ) {
        if (super.attachTo( target )) {
            ActionIndicator.setAction(this);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String actionName() {
        return Messages.get(this, "action");
    }

    @Override
    public int indicatorColor() {
        return 0x333333;
    }

    @Override
    public Visual primaryVisual(){
        return TerrainFeaturesTilemap.getTrapVisual(Reflection.newInstance(trapClass));
    }

    public LinkedHashMap<Class<? extends Trap>, Integer> TrapClasses(){
        LinkedHashMap<Class<? extends Trap>, Integer> traps = new LinkedHashMap<>();
        traps.put(OozeTrap.class, 5);
        traps.put(TeleportationTrap.class, 4);
        traps.put(FlockTrap.class, 4);

        int point = ((Hero)target).pointsInTalent(Talent.TRAP_MASTER);
        if (point >= 1){
            traps.put(GatewayTrap.class, 5);
            traps.put(GeyserTrap.class, 6);

            traps.put(PoisonDartTrap.class, 7);
            traps.put(FrostTrap.class, 7);
        } else {
            traps.put(WornDartTrap.class, 2);
            traps.put(ChillingTrap.class, 4);
        }
        if (point >= 2){
            traps.put(ConfusionTrap.class, 5);
            traps.put(RockfallTrap.class, 10);

            traps.put(CorrosionTrap.class, 8);
            traps.put(BlazingTrap.class, 8);
        } else {
            traps.put(ToxicTrap.class, 5);
            traps.put(BurningTrap.class, 6);
        }
        if (point >= 3){
            traps.put(ExplosiveTrap.class, 12);
            traps.put(DisintegrationTrap.class, 14);

            traps.put(StormTrap.class, 10);
            traps.put(FlashingTrap.class, 8);
        } else {
            traps.put(ShockingTrap.class, 7);
            traps.put(GrippingTrap.class, 5);
        }
        return traps;
    }

    @Override
    public void doAction() {
        GameScene.show(new WndBuildTrap(this));
    }
}