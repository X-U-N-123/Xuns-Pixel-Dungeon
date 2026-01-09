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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Levitation;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.explorer.OpticalCamou;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Barricade;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;

import java.util.ArrayList;

public class Shovel extends MeleeWeapon {

    public static final String AC_BUILD = "build";
    public static final String AC_WATER = "water";
    public static final String AC_PLANT = "plant";
    public static final String AC_CHASM = "chasm";
    public static final String AC_JUMP  = "jump";

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
        actions.add(AC_JUMP);
        if (hero.subClass == HeroSubClass.GEOMANCER) {
            actions.add(AC_WATER);
            actions.add(AC_PLANT);
            actions.add(AC_CHASM);
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

        if (action.equals(AC_BUILD) || action.equals(AC_JUMP)
        || action.equals(AC_WATER) || action.equals(AC_PLANT) || action.equals(AC_CHASM)){//Geomancer ability
            defaultAction = action;
            switch (defaultAction){
                case AC_JUMP:
                case AC_BUILD: image = ItemSpriteSheet.WOOD_SHOVEL;  break;
                case AC_PLANT: image = ItemSpriteSheet.PLANT_SHOVEL; break;
                case AC_WATER: image = ItemSpriteSheet.WATER_SHOVEL; break;
                case AC_CHASM: image = ItemSpriteSheet.CHASM_SHOVEL; break;
            }
            updateQuickslot();
            if (curUser.buff(ExplorerCooldown.class) == null || action.equals(AC_JUMP)) GameScene.selectCell(changeTerrain);
            else GLog.w(Messages.get(this, "cd"));
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

            switch (defaultAction){
                case AC_JUMP:

                    if (ch instanceof Barricade && ch.alignment == Char.Alignment.ALLY) {
                        for (int i : PathFinder.NEIGHBOURS8) {
                            if (curUser.pos == ch.pos + i && curUser.buff(Roots.class) == null
                            && Dungeon.level.passable[ch.pos - i]
                            && Actor.findChar(ch.pos - i ) == null){

                                Sample.INSTANCE.play( Assets.Sounds.MISS, 1.5f);
                                curUser.sprite.jump(curUser.pos, ch.pos - i, new Callback() {
                                    @Override
                                    public void call() {
                                        curUser.pos = ch.pos - i;
                                        Dungeon.level.occupyCell( curUser );
                                        Dungeon.observe();
                                        GameScene.updateFog();
                                        //jump over the barricade if there is
                                        curUser.spendAndNext(1f/curUser.speed());
                                    }
                                });
                                return;
                            } else if (curUser.buff(Roots.class) != null) {
                                PixelScene.shake( 1, 1f );
                                return;
                            }
                        }
                    }

                    break;
                case AC_BUILD:

                    if (Dungeon.level.passable[cell]){
                        if (ch != null){
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

                    if (curUser.pointsInTalent(Talent.SON_OF_SEA) >= 3){
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
                            Splash.at( DungeonTilemap.tileCenterToWorld( curUser.pos ), -PointF.PI/2, PointF.PI/2, 0x5bc1e3, 5, 0.01f);
                            ExplorerCooldown.affectCD(50, curUser);
                            curUser.spendAndNext(Actor.TICK);
                            Dungeon.observe();
                            curUser.sprite.zap(cell);
                            return;
                        }

                    } else if (Dungeon.level.setCellToWater(true, cell)){
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
                case AC_PLANT:
                    boolean grown = false;

                    for (int i : PathFinder.NEIGHBOURS9) {
                        if (Dungeon.level.map[curUser.pos + i] == Terrain.EMPTY || Dungeon.level.map[curUser.pos + i] == Terrain.EMPTY_DECO
                        || Dungeon.level.map[curUser.pos + i] == Terrain.EMBERS || Dungeon.level.map[curUser.pos + i] == Terrain.GRASS){

                            Level.set(curUser.pos + i, Terrain.GRASS);
                            grown = true;
                            if (curUser.pointsInTalent(Talent.TAPESTRY_OF_VINES) >= 3){
                                Level.set(curUser.pos + i, Terrain.FURROWED_GRASS);
                            }
                            GameScene.updateMap(curUser.pos + i);
                        }
                    }//plant grass if grass can grow there

                    if (grown){
                        Sample.INSTANCE.play(Assets.Sounds.PLANT);
                        if (curUser.hasTalent(Talent.TAPESTRY_OF_VINES)) ExplorerCooldown.affectCD(50, curUser);
                        else                                             ExplorerCooldown.affectCD(15, curUser);
                        curUser.spendAndNext(Actor.TICK);
                        Dungeon.observe();
                        curUser.sprite.zap(cell);
                        return;
                    }
                    break;
                case AC_CHASM:
                    if (Dungeon.level.map[cell] == Terrain.CHASM){
                        Level.set(cell, Terrain.EMPTY_SP);

                        Sample.INSTANCE.play( Assets.Sounds.BUILD );
                        ExplorerCooldown.affectCD(60, curUser);
                        curUser.spendAndNext(Actor.TICK);
                        GameScene.updateMap(cell);
                        Dungeon.observe();
                        curUser.sprite.zap(cell);
                        return;
                        //prevent player from escaping boss fight by this
                    } else if (Dungeon.level.passable[cell] && Actor.findChar(cell) == null && Dungeon.depth % 5 != 0) {
                        Level.set(cell, Terrain.CHASM);

                        Sample.INSTANCE.play( Assets.Sounds.ROCKS );
                        GameScene.shake(2, 0.5f);
                        ExplorerCooldown.affectCD(60, curUser);
                        curUser.spendAndNext(Actor.TICK);
                        GameScene.updateMap(cell);
                        Dungeon.observe();
                        curUser.sprite.zap(cell);
                        return;

                        //dig floor
                    } else if ((Actor.findChar(cell) != null || Dungeon.depth % 5 == 0) && curUser.hasTalent(Talent.RISING_WIND)) {
                        Buff.affect(curUser, Levitation.class, 15f);
                        Sample.INSTANCE.play(Assets.Sounds.MISS);

                        ExplorerCooldown.affectCD(60, curUser);
                        curUser.spendAndNext(Actor.TICK);
                        curUser.sprite.zap(cell);
                        return;
                    }
                    if (curUser.pointsInTalent(Talent.STRIKING_STONE) >= 3 && Dungeon.level.map[cell] == Terrain.BARRICADE) {
                        Level.set(cell, Terrain.EMPTY);

                        Sample.INSTANCE.play( Assets.Sounds.BUILD );
                        ExplorerCooldown.affectCD(50, curUser);
                        curUser.spendAndNext(Actor.TICK);
                        GameScene.updateMap(cell);
                        Dungeon.observe();
                        curUser.sprite.zap(cell);
                        return;
                        //remove barricade
                    } else if (curUser.pointsInTalent(Talent.LAYERED_ARCHITECTURE) >= 3) {
                        if (Dungeon.level.map[cell] == Terrain.STATUE){
                            Level.set(cell, Terrain.EMPTY);

                            Sample.INSTANCE.play( Assets.Sounds.MINE );
                            ExplorerCooldown.affectCD(75, curUser);
                            curUser.spendAndNext(Actor.TICK);
                            GameScene.updateMap(cell);
                            Dungeon.observe();
                            curUser.sprite.zap(cell);
                            return;
                        }
                        if (Dungeon.level.map[cell] == Terrain.STATUE_SP){
                            Level.set(cell, Terrain.EMPTY_SP);

                            Sample.INSTANCE.play( Assets.Sounds.MINE );
                            ExplorerCooldown.affectCD(75, curUser);
                            curUser.spendAndNext(Actor.TICK);
                            GameScene.updateMap(cell);
                            Dungeon.observe();
                            curUser.sprite.zap(cell);
                            return;
                        }
                        //break the statues
                    }
            }
            GLog.w(Messages.get(this, "hard"));
        }
    };
}