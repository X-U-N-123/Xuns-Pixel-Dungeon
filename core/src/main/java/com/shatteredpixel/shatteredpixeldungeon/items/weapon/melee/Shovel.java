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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WellWater;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Barricade;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.MagicWellRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.ColorMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;

import java.util.ArrayList;

public class Shovel extends MeleeWeapon {

    public static final String AC_BUILD = "build";
    public static final String AC_WATER = "water";
    public static final String AC_PLANT = "plant";
    public static final String AC_WELL  = "well";

    {
        image = ItemSpriteSheet.SHOVEL;
        hitSound = Assets.Sounds.HIT_SLASH;
        hitSoundPitch = 1.2f;

        tier = 1;
        DLY = 0.8f; //1.25x speed

        defaultAction = AC_BUILD;
        usesTargeting = false;

        unique = true;
        bones = false;
    }

    @Override
    public ArrayList<String> actions(Hero hero ) {
        ArrayList<String> actions = super.actions( hero );
        actions.add(AC_BUILD);
        if (Statistics.wellWaterDug < hero.pointsInTalent(Talent.DIG_THE_WELL))
            actions.add(AC_WELL);
        if (hero.subClass == HeroSubClass.GEOMANCER) {
            actions.add(AC_WATER);
            actions.add(AC_PLANT);
        }
        return actions;
    }

    @Override
    public int max(int lvl) {
        return  4*(tier+1) +    //8 base, down from 10
                lvl*(tier+1);   //scaling unchanged
    }

    @Override
    public void execute( Hero hero, String action ) {

        super.execute(hero, action);

        if (action.equals(AC_BUILD) || action.equals(AC_WATER) || action.equals(AC_PLANT) || action.equals(AC_WELL)){
            defaultAction = action;
            GameScene.selectCell(changeTerrain);
        }
    }

    @Override
    public String upgradeAbilityStat(int level) {
        return Integer.toString(5+level);
    }

    public static class ExplorerCooldown extends FlavourBuff {
        public int icon() { return BuffIndicator.TIME; }
        public void tintIcon(Image icon) { icon.hardlight(0.5f, 0.5f, 0.5f); }

        public static void affectCD(float turn, Hero hero){
            Buff.affect(hero, ExplorerCooldown.class, turn * (1 - hero.pointsInTalent(Talent.CONVENIENT_SHOVEL) / 8f));
        }
    }

    public CellSelector.Listener changeTerrain = new CellSelector.Listener() {
        @Override public String prompt() {
            switch (defaultAction){
                case AC_WATER:
                    return Messages.get(this, "water");
                case AC_PLANT:
                    return Messages.get(this, "plant");
                case AC_WELL:
                    return Messages.get(this, "well");
                case AC_BUILD: default:
                    return Messages.get(this, "barricade");
            }
        }

        @Override
        public void onSelect(Integer cell) {
            if (cell == null) return;
            if (!Dungeon.level.adjacent(cell, Dungeon.hero.pos) || !Dungeon.level.heroFOV[cell]) {
                GLog.w(Messages.get(this, "reach"));
                return;
            }

            switch (defaultAction){
                case AC_WATER:

                    if (curUser.buff(ExplorerCooldown.class) == null
                    && Dungeon.level.setCellToWater(true, cell)){
                        Fire fire = (Fire) Dungeon.level.blobs.get(Fire.class);
                        if (fire != null) fire.clear(cell);

                        //put water if there has no water
                        Sample.INSTANCE.play(Assets.Sounds.WATER, 2f);
                        Splash.at( DungeonTilemap.tileCenterToWorld( cell ), -PointF.PI/2, PointF.PI/2, 0x5bc1e3, 5, 0.01f);
                        ExplorerCooldown.affectCD(10, curUser);
                        Dungeon.hero.spendAndNext(Actor.TICK);
                        GameScene.updateMap(cell);
                        Dungeon.observe();
                        curUser.sprite.zap(cell);
                        return;

                    } else if (Dungeon.level.map[cell] == Terrain.WATER) {
                        //remove water if there is
                        Level.set( cell, Terrain.EMPTY );

                        CellEmitter.get(cell).burst( Speck.factory( Speck.STEAM ), 5 );
                        Dungeon.hero.spendAndNext(Actor.TICK);
                        GameScene.updateMap(cell);
                        Dungeon.observe();
                        curUser.sprite.zap(cell);
                        return;
                    }
                    break;
                case AC_PLANT:

                    if (curUser.buff(ExplorerCooldown.class) == null){
                        boolean grown = false;

                        for (int i : PathFinder.NEIGHBOURS9) {
                            if (Dungeon.level.map[cell + i] == Terrain.EMPTY || Dungeon.level.map[cell + i] == Terrain.EMPTY_DECO
                            || Dungeon.level.map[cell + i] == Terrain.EMBERS || Dungeon.level.map[cell + i] == Terrain.GRASS){

                                Level.set(cell + i, Terrain.GRASS);
                                grown = true;
                                if (curUser.pointsInTalent(Talent.TAPESTRY_OF_VINES) >= 3){
                                    Level.set(cell + i, Terrain.FURROWED_GRASS);
                                }

                                Splash.at( DungeonTilemap.tileCenterToWorld(cell + i), -PointF.PI/2, PointF.PI/2,
                                    ColorMath.random( 0x004400, 0x88CC44 ), 10, 0.02f);
                            }
                        }//plant grass if grass can grow there

                        if (grown){
                            Sample.INSTANCE.play(Assets.Sounds.PLANT);
                            ExplorerCooldown.affectCD(20, curUser);
                            Dungeon.hero.spendAndNext(Actor.TICK);
                            GameScene.updateMap(cell);
                            Dungeon.observe();
                            curUser.sprite.zap(cell);
                            return;
                        }

                    } else if (Dungeon.level.map[cell] == Terrain.HIGH_GRASS || Dungeon.level.map[cell] == Terrain.FURROWED_GRASS) {
                        //cut grass down if there is
                        Level.set( cell, Terrain.GRASS );

                        Splash.at( DungeonTilemap.tileCenterToWorld( cell ), -PointF.PI/2, PointF.PI/2,
                                ColorMath.random( 0x004400, 0x88CC44 ), 10, 0.02f);

                        Sample.INSTANCE.play(Assets.Sounds.HIT_SLASH);
                        Dungeon.hero.spendAndNext(Actor.TICK);
                        GameScene.updateMap(cell);
                        Dungeon.observe();
                        curUser.sprite.zap(cell);
                        return;
                    }

                    break;
                case AC_WELL:
                    if (curUser.buff(ExplorerCooldown.class) == null
                    && Dungeon.level.map[cell] == Terrain.EMPTY_WELL
                    && Statistics.wellWaterDug < curUser.pointsInTalent(Talent.DIG_THE_WELL)){
                        for (Class<?> waterClass : MagicWellRoom.WATERS ) {
                            WellWater water = (WellWater)Dungeon.level.blobs.get( waterClass );
                            if (water != null && water.cur[cell] == WellWater.CUR_EMPTY) {

                                water.cur[cell] = 1;
                                Statistics.wellWaterDug ++;
                                break;
                            }
                        }
                        ExplorerCooldown.affectCD(100, curUser);
                        Level.set(cell, Terrain.WELL);
                        Splash.at( DungeonTilemap.tileCenterToWorld( cell ), -PointF.PI/2, PointF.PI/2, 0x5bc1e3, 10, 0.01f);

                        Sample.INSTANCE.play(Assets.Sounds.GAS, 1f, 0.8f);
                        Dungeon.hero.spendAndNext(Actor.TICK);
                        GameScene.updateMap(cell);
                        Dungeon.observe();
                        curUser.sprite.zap(cell);
                        return;
                    }
                    break;
                case AC_BUILD: default:

                    if (curUser.buff(ExplorerCooldown.class) == null
                    && Dungeon.level.passable[cell] && Actor.findChar(cell) == null){
                        //build a barricade that can block the enemy
                        Barricade barricade = new Barricade();

                        barricade.HT = 2 * Dungeon.hero.lvl + 5;
                        barricade.HP = barricade.HT;
                        barricade.alignment = Char.Alignment.ALLY;
                        barricade.pos = cell;
                        GameScene.add(barricade);
                        Dungeon.level.occupyCell(barricade);
                        if (curUser.pointsInTalent(Talent.AGGRESSIVE_BARRICADE) >= 2) barricade.aggression = 6;

                        Sample.INSTANCE.play( Assets.Sounds.BUILD );
                        ExplorerCooldown.affectCD(50, curUser);
                        Dungeon.hero.spendAndNext(Actor.TICK);
                        GameScene.updateMap(cell);
                        Dungeon.observe();
                        curUser.sprite.zap(cell);
                        Bestiary.setSeen(Barricade.class);
                        return;
                    }
                    break;
            }

            if (curUser.buff(ExplorerCooldown.class) != null){
                GLog.w(Messages.get(this, "cd"));
            } else {
                GLog.w(Messages.get(this, "hard"));
            }
        }
    };
}