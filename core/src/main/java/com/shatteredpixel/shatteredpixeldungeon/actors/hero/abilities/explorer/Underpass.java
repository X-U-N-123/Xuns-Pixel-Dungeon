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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.explorer;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Underpass extends ArmorAbility {

    {
        baseChargeUse = 35;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target){
        if (Dungeon.level.map[hero.pos] == Terrain.UNDERPASS){
            GameScene.selectCell(new CellSelector.Listener() {
                @Override
                public String prompt() {
                    return Messages.get(this, "prompt");
                }

                @Override
                public void onSelect(Integer cell) {
                    if (cell == null) {
                        return;
                    }

                    if (Dungeon.level.map[cell] != Terrain.UNDERPASS || cell == hero.pos){
                        GLog.w(Messages.get(this, "no_exit"));
                    } else {

                        boolean needConfirm = false;
                        if (hero.hasTalent(Talent.MONITOR_BENEATH)){
                            for (Mob m : Dungeon.level.mobs.toArray(new Mob[0])) {
                                if (Dungeon.level.distance(m.pos, cell) <= 2 * hero.pointsInTalent(Talent.MONITOR_BENEATH)
                                        && m.alignment == Char.Alignment.ENEMY){
                                    needConfirm = true;
                                    break;
                                }
                            }
                        }

                        if (needConfirm){
                            GameScene.show(new WndOptions(
                            new TalentIcon(Talent.MONITOR_BENEATH),
                            Messages.titleCase(Messages.get(Talent.MONITOR_BENEATH, Talent.MONITOR_BENEATH.name() + ".title")),
                            Messages.get(Underpass.class, "sure"),
                            Messages.get(Chasm.class, "yes"),
                            Messages.get(Chasm.class, "no")
                            ){
                                @Override
                                protected void onSelect(int index) {
                                    if (index == 0){
                                        goThroughUnderpass(hero, cell, armor);
                                    }
                                }
                            });
                        } else {
                            goThroughUnderpass(hero, cell, armor);
                        }
                    }
                }
            });
        } else if (Sandstorm.canDrift(Dungeon.level.map[hero.pos])
                || Dungeon.level.map[hero.pos] == Terrain.HIGH_GRASS){
            int exitAmount = 0;
            for (int i : Dungeon.level.map) {
                if (i == Terrain.UNDERPASS) exitAmount++;
            }

            if (exitAmount < 2 + hero.pointsInTalent(Talent.SLY_RABBIT)){
                armor.charge -= chargeUse(hero);
                Level.set(hero.pos, Terrain.UNDERPASS);
                GameScene.updateMap(hero.pos);

                Item.updateQuickslot();
                hero.spendAndNext(Actor.TICK);
                Sample.INSTANCE.play(Assets.Sounds.MINE);
            } else {
                GLog.w(Messages.get(this, "no_more_exit"));
            }
        }

    }

    @Override
    public float chargeUse( Hero hero ) {
        float chargeUse = super.chargeUse(hero);
        if (Dungeon.level.map[hero.pos] != Terrain.UNDERPASS){
            chargeUse *= 2 - 0.4f * hero.pointsInTalent(Talent.SLY_RABBIT);
        }
        return chargeUse;
    }

    @Override
    public int icon() {
        return HeroIcon.UNDERPASS;
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.EXPRESS_UNDERWAY, Talent.MONITOR_BENEATH, Talent.SLY_RABBIT, Talent.GRENADE_COVER, Talent.HEROIC_ENERGY};
    }

    public void goThroughUnderpass(Hero hero, int pos, ClassArmor armor){

        Char existing = Actor.findChar(pos);
        ArrayList<Integer> candidates = new ArrayList<>();
        Char toPush = Char.hasProp(existing, Char.Property.IMMOVABLE) ? hero : existing;

        if (existing != null){

            for (int n : PathFinder.NEIGHBOURS8) {
                int cell = pos + n;
                if (!Dungeon.level.solid[cell] && Actor.findChar( cell ) == null
                && (!Char.hasProp(toPush, Char.Property.LARGE) || Dungeon.level.openSpace[cell])) {
                    candidates.add( cell );
                }
            }

            if (candidates.isEmpty()){ //need to push char but have no space
                GLog.w( Messages.get(this, "crowded") );
                return;
            }
            Random.shuffle(candidates);
        }

        int oldPos = hero.pos;

        hero.move( pos, false );
        Dungeon.observe();
        GameScene.updateFog();
        armor.charge -= chargeUse(hero);
        armor.updateQuickslot();

        if (hero.pos == pos) {
            hero.sprite.interruptMotion();
            hero.sprite.place(pos);
        }

        if (hero.invisible == 0) {
            hero.sprite.alpha( 0 );
            hero.sprite.parent.add( new AlphaTweener( hero.sprite, 1, 0.4f ) );
        }

         //effectively 2/3/4/5 turns of haste
        if (hero.hasTalent(Talent.EXPRESS_UNDERWAY))
            Buff.prolong(hero, Haste.class, 0.67f + hero.pointsInTalent(Talent.EXPRESS_UNDERWAY));
        BuffIndicator.refreshHero();

        if (Random.Float() <= hero.pointsInTalent(Talent.GRENADE_COVER) / 4f)
            new Bomb().explode(oldPos);

        hero.checkVisibleMobs();

        if (toPush != null){
            Actor.add(new Pushing(toPush, toPush.pos, candidates.get(0)));

            toPush.pos = candidates.get(0);
            Dungeon.level.occupyCell(toPush);
            hero.next();
        }

        Sample.INSTANCE.play(Assets.Sounds.STEP, 1.5f);
        Sample.INSTANCE.playDelayed(Assets.Sounds.STEP, 0.2f, 1.5f);
        Sample.INSTANCE.playDelayed(Assets.Sounds.STEP, 0.4f, 1.5f);
    }
}