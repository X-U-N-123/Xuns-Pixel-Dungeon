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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GuardianTrap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.StatueSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class StoneCudgel extends MeleeWeapon{

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

        if (Math.pow(0.8, Guardamount) >= Random.Float()) {
            ArrayList<Integer> spawnPoints = new ArrayList<>();
            for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
                int p = attacker.pos + PathFinder.NEIGHBOURS8[i];
                if (Actor.findChar(p) == null && Dungeon.level.passable[p]) {
                    spawnPoints.add(p);
                }
            }

            if (!spawnPoints.isEmpty() && !(attacker instanceof StoneGuardian)){
                StoneGuardian guardian = new StoneGuardian();
                guardian.createWeapon(false);
                guardian.state = guardian.WANDERING;
                guardian.pos = Random.element(spawnPoints);
                GameScene.add(guardian);
                Dungeon.level.occupyCell(guardian);
                if (!(attacker == Dungeon.hero) && attacker.alignment == Char.Alignment.ENEMY){
                    guardian.alignment = Char.Alignment.ENEMY;
                }

                ScrollOfTeleportation.appear(guardian, guardian.pos);
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

        int a=0;
        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
            ArrayList<Integer> telePoints = new ArrayList<>();
            if (mob instanceof StoneGuardian && a < 2+buffedLvl() ) {
                for (int i = 0; i < PathFinder.NEIGHBOURS25.length; i++) {
                    int p = target + PathFinder.NEIGHBOURS25[i];
                    if (Actor.findChar(p) == null && Dungeon.level.passable[p]) {
                        telePoints.add(p);
                    }
                }
                if (telePoints.size() > 0) {
                    int point = Random.element(telePoints);
                    mob.pos = point;
                    mob.sprite.place( point );
                    Dungeon.level.occupyCell(mob);
                    CellEmitter.get( point ).burst( Speck.factory( Speck.ROCK ), 3 );
                    mob.beckon(target);
                    a += 1;
                } else {
                    a = 2+buffedLvl();
                }
            }
        }
        hero.sprite.turnTo( Dungeon.hero.pos, target);
        hero.sprite.attack(target);
        Sample.INSTANCE.play( Assets.Sounds.ROCKS );
        hero.sprite.operate(hero.pos);
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

        @Override
        public void createWeapon( boolean useDecks ) {
            weapon = (MeleeWeapon) Dungeon.hero.belongings.weapon();
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
