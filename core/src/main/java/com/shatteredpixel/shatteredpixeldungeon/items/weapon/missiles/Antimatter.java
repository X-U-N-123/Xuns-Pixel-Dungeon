/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles;

import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.BARRICADE;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.BOOKSHELF;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.DOOR;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.EMBERS;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.FURROWED_GRASS;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.GRASS;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.HIGH_GRASS;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.MINE_BOULDER;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.MINE_CRYSTAL;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.OPEN_DOOR;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.WATER;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.TenguDartTrap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

public class Antimatter extends MissileWeapon{

    {
        image = ItemSpriteSheet.Anti_matter;

        tier = 6;
        baseUses = 1;

        sticky = false;
    }

    @Override
    public int min() {
        return Math.max(0 , min( buffedLvl() ));
    }

    @Override
    public int max() {
        return Math.max(0 , max( buffedLvl() ));
    }

    @Override
    public int max(int lvl) {
        return  30 * tier;                   //180 base, up from 30
                              //no scaling
    }

    @Override
    public void hitSound(float pitch) {
        //no hitsound as it never hits enemies directly
    }

    @Override
    protected void onThrow(int cell) {
        if (Dungeon.level.pit[cell]){
            super.onThrow(cell);
            return;
        }

        rangedHit( null, cell );
        Dungeon.level.pressCell(cell);

        ArrayList<Char> targets = new ArrayList<>();
        if (Actor.findChar(cell) != null) targets.add(Actor.findChar(cell));

        for (int i : PathFinder.NEIGHBOURS25){
            if (!(Dungeon.level.traps.get(cell+i) instanceof TenguDartTrap)) Dungeon.level.pressCell(cell+i);
            if ((Actor.findChar(cell + i) != null) && i != 0) targets.add(Actor.findChar(cell + i));
            boolean HeroposTerr = Dungeon.level.map[cell + i] == HIGH_GRASS
                    || Dungeon.level.map[cell + i] == GRASS
                    || Dungeon.level.map[cell + i] == FURROWED_GRASS
                    || Dungeon.level.map[cell + i] == WATER
                    || Dungeon.level.map[cell + i] == BOOKSHELF
                    || Dungeon.level.map[cell + i] == BARRICADE
                    || Dungeon.level.map[cell + i] == MINE_CRYSTAL
                    || Dungeon.level.map[cell + i] == MINE_BOULDER
                    || Dungeon.level.map[cell + i] == DOOR
                    || Dungeon.level.map[cell + i] == OPEN_DOOR;
            if (HeroposTerr){
                Level.set(cell + i, EMBERS);
                GameScene.updateMap(cell + i);
            }
        }

        for (Char target : targets){
            curUser.shoot(target, this);
            if (target == Dungeon.hero && !target.isAlive()){
                Badges.validateDeathFromFriendlyMagic();
                Dungeon.fail(this);
                GLog.n(Messages.get(this, "ondeath"));
            }
        }

        WandOfBlastWave.BlastWave.blast(cell);
        Sample.INSTANCE.play( Assets.Sounds.BLAST );
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public float durabilityPerUse( boolean rounded){
        return MAX_DURABILITY;
    }
}