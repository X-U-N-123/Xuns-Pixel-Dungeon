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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.explorer.Sandstorm;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollGeomancer;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.StoneCudgel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class RockFallBuff extends Buff implements ActionIndicator.Action {

    {
        revivePersists = true;
    }

    private float CD = 0;
    private int rockCell = -1;

    @Override
    public int icon() {
        return BuffIndicator.ROCK;
    }

    @Override
    public String iconTextDisplay() {
        if (CD <= 0) return "";
        return Integer.toString((int)CD);
    }

    @Override
    public String desc() {
        String desc = Messages.get(this, "desc", Dungeon.hero.lvl/3, Dungeon.hero.lvl);
        if (CD > 0){
            desc += "\n\n" + Messages.get(this, "desc_cd", CD);
        }
        return desc;
    }

    @Override
    public void tintIcon(Image icon) {
        if (CD > 0) icon.hardlight(0xaaaaaa);
        else        icon.hardlight(0xffffff);
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
    public boolean act() {
        CD -= TICK;
        spend(TICK);
        if (CD <= 0f) {
            CD = 0f;
            BuffIndicator.refreshHero();
        }
        return true;
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
        CD = bundle.getFloat(COOLDOWN);
    }

    @Override
    public String actionName() {
        return Messages.get(this, "name");
    }

    @Override
    public int indicatorColor() {
        return 0x333333;
    }

    @Override
    public int actionIcon() {
        return HeroIcon.ROCK;
    }

    @Override
    public void doAction() {
        GameScene.selectCell(new CellSelector.Listener() {
            @Override
            public String prompt() {
                if (CD <= 0) return Messages.get(this, "prompt");
                else         return Messages.get(this, "prompt_rock");
            }

            @Override
            public void onSelect(Integer cell) {
                if (cell == null) return;

                if (Dungeon.level.losBlocking[cell] || !Dungeon.level.heroFOV[cell]){
                    GLog.w(Messages.get(this, "invalid_pos"));
                    return;
                }

                PathFinder.buildDistanceMap( cell, BArray.not( Dungeon.level.solid, null ), 7);

                if (Dungeon.level.map[cell] == Terrain.MINE_BOULDER){
                    if (PathFinder.distance[target.pos] <= 1 + 2*((Hero)target).pointsInTalent(Talent.MIND_CONTROL)){
                        rockCell = cell;
                        target.sprite.attack(cell, new Callback() {
                            @Override
                            public void call() {
                                target.sprite.idle();
                                GameScene.selectCell(throwRock);
                            }
                        });
                    } else GLog.w(Messages.get(RockFallBuff.class, "blocked"));

                } else if (CD > 0) {
                    GLog.w(Messages.get(this, "cd"));
                } else {
                    if (Dungeon.level.passable[cell] && Actor.findChar(cell) == null
                        && Random.Float() < Dungeon.hero.pointsInTalent(Talent.METEROIC_IRON) * 0.1f){
                        StoneCudgel.StoneGuardian guardian = new StoneCudgel.StoneGuardian();
                        guardian.createWeapon(false);
                        guardian.state = guardian.WANDERING;
                        guardian.pos = cell;
                        GameScene.add(guardian);
                        Dungeon.level.occupyCell(guardian);
                    }

                    for (int i = 0; i < PathFinder.distance.length; i++) {
                        if (PathFinder.distance[i] <= 2){
                            Char ch = Actor.findChar(i);
                            if (ch != null && ch.alignment == Char.Alignment.ENEMY){
                                ch.damage(Random.NormalIntRange(Dungeon.hero.lvl/3, Dungeon.hero.lvl),
                                    target.buff(RockFallBuff.class));
                                Buff.affect(ch, Paralysis.class, 6f);
                            }
                            if (ch == null && Random.Float() <= 0.15f + Dungeon.hero.pointsInTalent(Talent.METEOR_CRATER) * 0.2f / 3f
                            && (Sandstorm.canDrift(Dungeon.level.map[i]) || Dungeon.level.map[i] == Terrain.HIGH_GRASS)){
                                Level.set(i, Terrain.MINE_BOULDER);
                                GameScene.updateMap(i);

                                //throw items inside the door in random directions
                                if (Dungeon.level.heaps.get(i) != null){
                                    ArrayList<Integer> candidates = new ArrayList<>();
                                    for (int n : PathFinder.NEIGHBOURS8){
                                        if (Dungeon.level.passable[i+n]){
                                            candidates.add(i+n);
                                        }
                                    }
                                    if (!candidates.isEmpty()){
                                        Heap heap = Dungeon.level.heaps.get(i);
                                        while (!heap.isEmpty()) {
                                            Dungeon.level.drop(heap.pickUp(), Random.element(candidates)).sprite.drop(i);
                                        }
                                    }
                                }
                            }
                            CellEmitter.get( i ).burst( Speck.factory( Speck.ROCK ), 3 );
                        }
                    }
                    Sample.INSTANCE.play(Assets.Sounds.ROCKS);
                    PixelScene.shake(2, 0.5f);
                    CD += 51; //1 more turn because this takes a turn
                    Dungeon.hero.sprite.zap(cell);
                    Dungeon.hero.spendAndNext(Actor.TICK);
                }
            }
        });
    }

    CellSelector.Listener throwRock = new CellSelector.Listener() {
        @Override
        public String prompt() {
            return Messages.get(this, "prompt");
        }

        @Override
        public void onSelect(Integer cell) {
            if (cell == null || rockCell == -1 || Dungeon.level.map[rockCell] != Terrain.MINE_BOULDER) return;

            Ballistica rockPath = new Ballistica(rockCell, cell, Ballistica.MAGIC_BOLT);
            if (rockPath.collisionPos == rockCell) {
                GLog.w(Messages.get(RockFallBuff.class, "blocked"));
                return;
            }

            Level.set(rockCell, Terrain.EMPTY);
            GameScene.updateMap(rockCell);

            Sample.INSTANCE.play(Assets.Sounds.MISS);
            ((MissileSprite)target.sprite.parent.recycle( MissileSprite.class )).
            reset( rockCell, rockPath.collisionPos, new GnollGeomancer.Boulder(), new Callback() {
                @Override
                public void call() {
                    Splash.at(rockPath.collisionPos, 0x555555, 15);
                    Sample.INSTANCE.play(Assets.Sounds.ROCKS);

                    Char ch = Actor.findChar(rockPath.collisionPos);
                    if (ch == Dungeon.hero){
                        PixelScene.shake( 3, 0.7f );
                    } else {
                        PixelScene.shake(0.5f, 0.5f);
                    }

                    if (ch != null){
                        if (ch.alignment == Char.Alignment.ALLY && ((Hero)target).hasTalent(Talent.ROCK_PROTECTOR)){
                            int shieldAmount = Math.round(Dungeon.hero.lvl * ((Hero)target).pointsInTalent(Talent.ROCK_PROTECTOR)/3f);
                            Buff.affect(ch, Barrier.class).setShield(shieldAmount);
                            ch.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(shieldAmount), FloatingText.SHIELDING);
                        } else {
                            int debuffTime = ((Hero)target).pointsInTalent(Talent.DESTRUCTIVE_STRIKE) >= 2 ? 6 : 4;
                            if (((Hero)target).hasTalent(Talent.DESTRUCTIVE_STRIKE))
                                Buff.prolong(ch, Blindness.class, debuffTime);

                            Buff.prolong(ch, Cripple.class, debuffTime);

                            if (rockPath.path.size() > rockPath.dist+1) {
                                Ballistica trajectory =
                                    new Ballistica(ch.pos, rockPath.path.get(rockPath.dist + 1), Ballistica.MAGIC_BOLT);
                                int knockDistance = ((Hero)target).pointsInTalent(Talent.DESTRUCTIVE_STRIKE) >= 3 ? 2 : 1;
                                WandOfBlastWave.throwChar(ch, trajectory, knockDistance, false, false, target);
                            }
                        }

                        if (!ch.isAlive() && ch == Dungeon.hero) {
                            Badges.validateDeathFromFriendlyMagic();
                            Dungeon.fail( ch.buffs(RockFallBuff.class) );
                            GLog.n( Messages.get( GnollGeomancer.class, "rock_kill") );
                        }
                    } else Dungeon.level.pressCell(rockPath.collisionPos);

                    CD += 6; //1 more turn because this takes a turn
                    Dungeon.hero.spendAndNext(Actor.TICK);
                }
            });
        }
    };
}