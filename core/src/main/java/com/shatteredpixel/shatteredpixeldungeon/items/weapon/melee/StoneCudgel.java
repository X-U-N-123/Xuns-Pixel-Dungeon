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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GuardianTrap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.StatueSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class StoneCudgel extends MeleeWeapon {

    {
        image = ItemSpriteSheet.Stonecudgel;
        hitSound = Assets.Sounds.HIT_CRUSH;
        hitSoundPitch = 1f;

        tier = 6;
    }

    @Override
    public int max(int lvl) {
        return 4 * (tier-1) +     //20 base, down from 35
        lvl * (tier-2);  //+4 per level, down from +7
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        int Guardamount = 0;
        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
            if (mob instanceof StoneGuardian && !(attacker instanceof StoneGuardian)) {
                Guardamount ++;
                mob.beckon(defender.pos);
            }
        }

        if (Math.pow(0.7, Guardamount) >= Random.Float()) {
            ArrayList<Integer> spawnPoints = new ArrayList<>();
            for (int i : PathFinder.NEIGHBOURS8) {
                int pos = attacker.pos + i;
                if (Actor.findChar(pos) == null && Dungeon.level.passable[pos]) {
                    spawnPoints.add(pos);
                }
            }

            if (!spawnPoints.isEmpty() && !(attacker instanceof StoneGuardian)){
                StoneGuardian guardian = new StoneGuardian();
                guardian.createWeapon(this);
                guardian.state = guardian.WANDERING;
                guardian.pos = Random.element(spawnPoints);
                GameScene.add(guardian);
                Dungeon.level.occupyCell(guardian);
                guardian.alignment = attacker.alignment;

                ScrollOfTeleportation.appear(guardian, guardian.pos);
                Bestiary.setSeen(StoneGuardian.class);
            }
        }
        return super.proc( attacker, defender, damage );
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {

        beforeAbilityUsed(hero, null);

        int a = 0;
        int range = 0;
        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {

            ArrayList<Integer> telePoints = new ArrayList<>();
            PathFinder.buildDistanceMap( target, BArray.not( Dungeon.level.solid, null ), 2 );
            for (int i = 0; i < PathFinder.distance.length; i++) {
                if (PathFinder.distance[i] <= range && Actor.findChar(i) == null && Dungeon.level.passable[i]){
                    telePoints.add(i);
                }
            }

            if (mob instanceof StoneGuardian && a < 2+buffedLvl() && mob.alignment == Char.Alignment.ALLY) {

                if (!telePoints.isEmpty()) {
                    int point = Random.element(telePoints);
                    mob.pos = point;
                    mob.sprite.place( point );
                    Dungeon.level.occupyCell(mob);
                    CellEmitter.get( point ).burst( Speck.factory( Speck.ROCK ), 3 );
                    mob.beckon(target);
                    a ++;
                } else {
                    range ++;
                }
            }
        }
        hero.sprite.turnTo( Dungeon.hero.pos, target);
        hero.sprite.attack(target);
        Sample.INSTANCE.play( Assets.Sounds.ROCKS );
        Dungeon.observe();
        hero.checkVisibleMobs();

        hero.next();
        afterAbilityUsed(hero);
    }

    @Override
    public String abilityInfo() {
        int stone = levelKnown ? 2 + buffedLvl() : 2;
        if (levelKnown){
            return Messages.get(this, "ability_desc", stone);
        } else {
            return Messages.get(this, "typical_ability_desc", stone);
        }
    }

    public String upgradeAbilityStat(int level){
        return String.valueOf(2 + level);
    }

    public static class StoneGuardian extends GuardianTrap.Guardian {

        {
            spriteClass = StoneGuardianSprite.class;

            alignment = Alignment.ALLY;
        }

        public void createWeapon(Weapon wep) {
            weapon = wep;
        }

        @Override
        public int drRoll() {
            return 0;
        }

        @Override
        public void die( Object cause ) {
            //Jump Guardian super method to stop it from dropping weapon
            if (Dungeon.hero.isAlive() && !Dungeon.level.heroFOV[pos]) {
                GLog.i( Messages.get(this, "died") );
            }//Mob.java super method

            destroy();
            if (cause != Chasm.class) {
                sprite.die();
                if (!flying && Dungeon.level != null && sprite instanceof MobSprite && Dungeon.level.map[pos] == Terrain.CHASM){
                    ((MobSprite) sprite).fall();
                }
            }//Char.java super method
        }

        private static final String ALIGNMENT = "alignment";

        @Override
        public void storeInBundle( Bundle bundle ){
            super.storeInBundle(bundle);
            bundle.put(ALIGNMENT, alignment);
        }

        @Override
        public void restoreFromBundle( Bundle bundle ){
            super.restoreFromBundle(bundle);
            alignment = bundle.getEnum(ALIGNMENT, Char.Alignment.class);
        }
    }

    public static class StoneGuardianSprite extends StatueSprite {

        public StoneGuardianSprite(){
            super();
            tint(0, 0, 0, 0.2f);
        }

        @Override
        public void resetColor() {
            super.resetColor();
            tint(0, 0, 0, 0.2f);
        }
    }
}
