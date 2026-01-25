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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.WhirlpoolBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.explorer.OpticalCamou;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Barricade;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.DoorPlank;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Shovel extends MeleeWeapon {

    public static final String AC_BUILD = "build";
    public static final String AC_WATER = "water";
    public static final String AC_BREAK = "break";

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
        if (hero.subClass == HeroSubClass.WAVECHASER) actions.add(AC_WATER);
        if (hero.hasTalent(Talent.DEMOLITION))        actions.add(AC_BREAK);
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

        if (action.equals(AC_BUILD) || action.equals(AC_WATER) || action.equals(AC_BREAK)){
            defaultAction = action;
            switch (defaultAction){
                case AC_BUILD: image = ItemSpriteSheet.SHOVEL;       break;
                case AC_BREAK: image = ItemSpriteSheet.WOOD_SHOVEL;  break;
                case AC_WATER: image = ItemSpriteSheet.WATER_SHOVEL; break;
            }
            updateQuickslot();
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
            float percent = 1 - hero.pointsInTalent(Talent.CONVENIENT_SHOVEL) * 0.1f;
            if (hero.buff(OpticalCamou.Camouflage.class) != null){
                percent -= 0.12f * hero.pointsInTalent(Talent.QUICK_BUILD);
            }
            Buff.affect(hero, ExplorerCooldown.class, turn * percent);
        }
    }

    public CellSelector.Listener changeTerrain = new CellSelector.Listener() {
        @Override public String prompt() {
            return Messages.get(this, defaultAction);
        }

        @Override
        public void onSelect(Integer cell) {
            if (cell == null) return;
            if (Dungeon.level.distance(cell, curUser.pos) > 1 || !Dungeon.level.heroFOV[cell]) {
                GLog.w(Messages.get(this, "reach"));
                return;
            }
            Char ch = Actor.findChar(cell);

            int terrain = Dungeon.level.map[cell];

            switch (defaultAction){
                case AC_BUILD:
                    if (curUser.buff(ExplorerCooldown.class) != null) {
                        GLog.w(Messages.get(Shovel.class, "cd"));
                        return;
                    }

                    if (Dungeon.level.passable[cell]){
                        if (ch != null){
                            for (int i : PathFinder.NEIGHBOURS8) {
                                if (Actor.findChar(ch.pos + i) == curUser && Dungeon.level.pit[ch.pos - i]) return;
                            } // cannot knock back target into chasm

                            //trace a ballistica to our target (which will also extend past them)
                            Ballistica trajectory = new Ballistica(curUser.pos, cell, Ballistica.STOP_TARGET);
                            //trim it to just be the part that goes past them
                            trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size() - 1), Ballistica.PROJECTILE);
                            //knock them back along that ballistica
                            WandOfBlastWave.throwChar(ch, trajectory, 1, true, false, curUser);

                            curUser.next();
                        }
                        curUser.sprite.zap(cell, new Callback() {
                            @Override
                            public void call() {
                                if (Actor.findChar(cell) == null){
                                    //build a barricade that can block the enemy
                                    Barricade.buildBarricade(cell, 2 * curUser.lvl + 5, Char.Alignment.ALLY, 0);

                                    Sample.INSTANCE.play( Assets.Sounds.BUILD );
                                    ExplorerCooldown.affectCD(50, curUser);
                                    if (curUser.pointsInTalent(Talent.AGGRESSIVE_ROADBLOCK) < 2) curUser.spendAndNext(Actor.TICK);
                                }
                            }
                        });
                        return;
                    }
                    break;
                case AC_WATER:
                    Fire fire = (Fire) Dungeon.level.blobs.get(Fire.class);

                    if (curUser.hasTalent(Talent.LAKE_DEVELOPMENT) && cell == curUser.pos
                            && curUser.buff(ExplorerCooldown.class) == null){
                        boolean watered = false;
                        for (int i : PathFinder.NEIGHBOURS9) {
                            if (Dungeon.level.setCellToWater(true, curUser.pos + i)){
                                if (fire != null) fire.clear(curUser.pos + i);
                                GameScene.updateMap(curUser.pos + i);
                                watered = true;
                            }
                        }//put water if there has no water

                        if (watered){
                            Sample.INSTANCE.play(Assets.Sounds.WATER, 2f);
                            Splash.at( DungeonTilemap.tileCenterToWorld( curUser.pos ), -PointF.PI/2, PointF.PI/2, 0x5bc1e3, 10, 0.01f);
                            ExplorerCooldown.affectCD(10 * (7 - curUser.pointsInTalent(Talent.LAKE_DEVELOPMENT)), curUser);
                            curUser.spendAndNext(Actor.TICK);
                            Dungeon.observe();
                            curUser.sprite.zap(cell);
                            return;
                        }

                    } else if (terrain == Terrain.WATER){
                        Level.set(cell, Terrain.EMPTY);

                        //remove water here if here is
                        if (ch != null && ch.alignment == Char.Alignment.ENEMY){
                            switch (curUser.pointsInTalent(Talent.SON_OF_SEA)){
                                case 3:
                                    curUser.buff(WhirlpoolBuff.class).decreaseCD(4);//1 turn less because this takes a turn
                                case 2:
                                    Buff.prolong(curUser, Recharging.class, 4f);
                                case 1:
                                    Buff.prolong(ch, Vertigo.class, 4f);
                            }
                        }
                        Sample.INSTANCE.play(Assets.Sounds.WATER, 2f);
                        curUser.spendAndNext(Actor.TICK);
                        GameScene.updateMap(cell);
                        Dungeon.observe();
                        curUser.sprite.zap(cell);
                        return;

                    } else if (curUser.buff(ExplorerCooldown.class) == null
                            && Dungeon.level.setCellToWater(true, cell)){
                        if (fire != null) fire.clear(cell);

                        //put water if there has no water
                        Sample.INSTANCE.play(Assets.Sounds.WATER, 2f);
                        Splash.at( DungeonTilemap.tileCenterToWorld( cell ), -PointF.PI/2, PointF.PI/2, 0x5bc1e3, 5, 0.01f);
                        ExplorerCooldown.affectCD(10, curUser);
                        curUser.spendAndNext(Actor.TICK);
                        GameScene.updateMap(cell);
                        Dungeon.observe();
                        curUser.sprite.zap(cell);
                        return;

                    }
                    break;
                case AC_BREAK:
                    if (breakTerrain(cell)) return;
                    break;
            }
            GLog.w(Messages.get(this, "hard"));
        }
    };

    public static boolean breakTerrain(int cell) {
        int terrain = Dungeon.level.map[cell];

        if (curUser.buff(ExplorerCooldown.class) != null) {
            GLog.w(Messages.get(Shovel.class, "cd"));
            return true;
        }
        if (terrain == Terrain.BARRICADE
        || terrain == Terrain.DOOR || terrain == Terrain.OPEN_DOOR
        && curUser.hasTalent(Talent.DEMOLITION) ) {
            Level.set(cell, Terrain.EMPTY);

            Sample.INSTANCE.play( Assets.Sounds.BUILD );
            ExplorerCooldown.affectCD(30, curUser);

            if (curUser.pointsInTalent(Talent.DEMOLITION) >= 3 && Random.Float() > 1/3f
            && (terrain == Terrain.DOOR || terrain == Terrain.OPEN_DOOR)){
                new DoorPlank().doPickUp(curUser);
            } else curUser.spendAndNext(Actor.TICK);//picking the door already consumes a turn

            GameScene.updateMap(cell);
            Dungeon.observe();
            curUser.sprite.zap(cell);
            return true;
            //remove barricade and door
        } else if (curUser.pointsInTalent(Talent.DEMOLITION) >= 2) {
            if (terrain != Terrain.STATUE && terrain != Terrain.STATUE_SP) return false;

            if (terrain == Terrain.STATUE)
                Level.set(cell, Terrain.EMPTY);
            if (terrain == Terrain.STATUE_SP)
                Level.set(cell, Terrain.EMPTY_SP);

            Sample.INSTANCE.play( Assets.Sounds.MINE );
            ExplorerCooldown.affectCD(30, curUser);
            curUser.spendAndNext(Actor.TICK);
            GameScene.updateMap(cell);
            Dungeon.observe();
            curUser.sprite.zap(cell);
            return true;
            //break the statues
        }
        return false;
    }
}