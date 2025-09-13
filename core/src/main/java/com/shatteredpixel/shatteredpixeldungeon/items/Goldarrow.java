/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * Xun's Pixel Dungeon
 * Copyright (C) 2025-2025 Jiarun Chen
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

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Awareness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Foresight;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSight;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Wandmaker;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfFeatherFall;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Game;

import java.util.ArrayList;

public class Goldarrow extends Item {

    String AC_TELEPORT = "teleport";
    String AC_RETURN = "return";
    String AC_AWARE = "aware";
    String AC_GOTO = "goto";
    String AC_RESET = "reset";

    public static int questDepth;

    {
        defaultAction = AC_GOTO;
        image = ItemSpriteSheet.GOLDARROW;
        cursedKnown = levelKnown = true;
        unique = true;
        bones = false;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_TELEPORT);
        actions.add(AC_RETURN);
        actions.add(AC_AWARE);
        actions.add(AC_GOTO);
        actions.add(AC_RESET);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);
        if (action.equals(AC_TELEPORT)) {
            Buff.affect(hero, ElixirOfFeatherFall.FeatherBuff.class, 10f);
            Chasm.heroFall(hero.pos);
            defaultAction = AC_TELEPORT;
        }
        if (action.equals(AC_RETURN)) {
            InterlevelScene.mode = InterlevelScene.Mode.RETURN;
            InterlevelScene.returnDepth = Math.max(1, (Dungeon.depth - 1));
            InterlevelScene.returnBranch = 0;
            InterlevelScene.returnPos = -2;
            Game.switchScene( InterlevelScene.class );
            defaultAction = AC_RETURN;
        }
        if (action.equals(AC_AWARE)) {
            int length = Dungeon.level.length();
            int[] map = Dungeon.level.map;
            boolean[] mapped = Dungeon.level.mapped;
            boolean[] discoverable = Dungeon.level.discoverable;
            for (int i=0; i < length; i++) {

                int terr = map[i];

                if (discoverable[i]) {

                    mapped[i] = true;
                    if ((Terrain.flags[terr] & Terrain.SECRET) != 0) {

                        Dungeon.level.discover( i );
                    }
                }
            }

            Buff.prolong(curUser, Awareness.class, 100);
            Buff.prolong(curUser, MindVision.class, 100);
            Buff.prolong(curUser, MagicalSight.class, 100);
            Buff.prolong(curUser, Foresight.class, 100);
            Dungeon.observe();
            Dungeon.hero.checkVisibleMobs();
            defaultAction = AC_AWARE;
        }
        if (action.equals(AC_GOTO)){
            GameScene.selectCell(new CellSelector.Listener() {
                @Override public String prompt() {
                    return Messages.get(Goldarrow.class, "where");
                }
                @Override public void onSelect(Integer cell) {
                    if (cell == null)return;
                    ScrollOfTeleportation.teleportToLocation(curUser, cell);

                    hero.next();
                }
            });
            defaultAction = AC_GOTO;
        }
        if (action.contains(AC_RESET)){
            switch (Dungeon.depth){
                case 2: case 3: case 4:
                    for (Mob m: Dungeon.level.mobs){
                        if (m instanceof Ghost) {
                            questDepth = Dungeon.depth;
                            Ghost.Quest.reset();
                        }
                    }
                    break;
                case 7: case 8: case 9:
                    for (Mob m: Dungeon.level.mobs){
                        if (m instanceof Wandmaker) {
                            questDepth = Dungeon.depth;
                            Wandmaker.Quest.reset();
                        }
                    }
                    break;
                case 12: case 13: case 14:
                    for (Mob m: Dungeon.level.mobs){
                        if (m instanceof Blacksmith) {
                            questDepth = Dungeon.depth;
                            Blacksmith.Quest.reset();
                        }
                    }
                    break;
                case 17: case 18: case 19:
                    for (Mob m: Dungeon.level.mobs){
                        if (m instanceof Imp) {
                            questDepth = Dungeon.depth;
                            Imp.Quest.reset();
                        }
                    }
                    break;
            }
            InterlevelScene.mode = InterlevelScene.Mode.RESET;
            Game.switchScene(InterlevelScene.class);
            defaultAction = AC_RESET;
        }
        GameScene.updateFog();
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public float weight(){
        return 0;
    }

}