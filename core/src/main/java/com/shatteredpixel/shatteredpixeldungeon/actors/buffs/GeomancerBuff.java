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

import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.CHASM;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.EMPTY;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.EMPTY_DECO;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.EMPTY_SP;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.FURROWED_GRASS;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.GRASS;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.HIGH_GRASS;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.PEDESTAL;
import static com.shatteredpixel.shatteredpixeldungeon.levels.Terrain.WATER;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EarthParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class GeomancerBuff extends Buff implements ActionIndicator.Action {

    {
        revivePersists = true;
    }

    private float CD = 0;

    @Override
    public int icon() {
        if (CD <= 0) return BuffIndicator.NONE;
        else         return BuffIndicator.TIME;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(0xcc7f0d);
    }

    @Override
    public String iconTextDisplay() {
        return Integer.toString((int)CD);
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", CD);
    }

    @Override
    public boolean act() {
        CD -= TICK;
        spend(TICK);
        if (CD <= 0f) {
            ActionIndicator.setAction(this);
            CD = 0f;
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
        if (CD <= 0) ActionIndicator.setAction(this);
    }

    @Override
    public String actionName() {
        return Messages.get(this, "action_name");
    }

    @Override
    public int actionIcon() {
        return HeroIcon.SHOVEL;
    }

    @Override
    public int indicatorColor() {
        switch (Dungeon.level.map[target.pos]){
            case GRASS: case HIGH_GRASS: case FURROWED_GRASS: return 0x009900;
            case EMPTY: case EMPTY_DECO:                      return 0x565656;
            case EMPTY_SP:                                    return 0x523c26;
            case WATER: case PEDESTAL:                        return 0x2364bc;
            case CHASM:                                       return 0x333333;
            default:                                          return 0x000000;
        }
    }

    @Override
    public void doAction(){
        switch (Dungeon.level.map[target.pos]){
            case GRASS: case HIGH_GRASS: case FURROWED_GRASS:
                if (Dungeon.hero.pointsInTalent(Talent.TAPESTRY_OF_VINES) >= 2) {
                    GameScene.selectCell(action);
                    break;
                }
            case EMPTY: case EMPTY_DECO:
                if (Dungeon.hero.pointsInTalent(Talent.STRIKING_STONE) >= 2) {
                    GameScene.selectCell(action);
                    break;
                }
            case WATER:
                if (Dungeon.hero.pointsInTalent(Talent.SON_OF_SEA) >= 2) {
                    GameScene.selectCell(action);
                    break;
                }
            case CHASM:
                if (Dungeon.hero.pointsInTalent(Talent.RISING_WIND) >= 2) {
                    GameScene.selectCell(action);
                    break;
                }
            case EMPTY_SP: case PEDESTAL:
                if (Dungeon.hero.pointsInTalent(Talent.LAYERED_ARCHITECTURE) >= 2){
                    GameScene.selectItem(new WndBag.ItemSelector() {

                        @Override
                        public String textPrompt() {
                            return Messages.get(this, "charge");
                        }

                        @Override
                        public Class<?extends Bag> preferredBag(){
                            return MagicalHolster.class;
                        }

                        @Override
                        public boolean itemSelectable(Item item) {
                            return item instanceof Wand && ((Wand) item).curChargeKnown
                        && ((Wand) item).curCharges < ((Wand) item).maxCharges;
                        }

                        @Override
                        public void onSelect(Item item) {
                            if (item == null) return;

                            Dungeon.hero.damage(5, GeomancerBuff.class);
                            ((Wand) item).curCharges ++;//consume 5 HP to give the wand 1 charge
                            Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
                            ScrollOfRecharging.charge(Dungeon.hero);
                            CD += 60;
                            ActionIndicator.clearAction();
                        }
                    });
                    break;
                }
            default:
                GLog.w(Messages.get(this, "no_ability"));
        }

    }

    public CellSelector.Listener action = new CellSelector.Listener() {
        @Override
        public String prompt() {
            return Messages.get(this, "prompt");
        }

        @Override
        public void onSelect(Integer cell) {
            if (cell == null || !Dungeon.level.heroFOV[cell]) return;
            Char ch = Actor.findChar(cell);
            switch (Dungeon.level.map[target.pos]){
                case GRASS: case HIGH_GRASS: case FURROWED_GRASS:
                    if (ch != null && !ch.isImmune(Roots.class)){
                        Buff.affect(ch, Roots.class, 5);//root the enemy
                        CellEmitter.bottom( cell ).start( EarthParticle.FACTORY, 0.05f, 8 );
                        ActionIndicator.clearAction();
                        CD += 60;
                        break;
                    } else if (ch == null){
                        GLog.w(Messages.get(this, "no_target"));
                    } else {
                        GLog.w(Messages.get(this, "invalid_target"));
                    }
                    break;
                case EMPTY: case EMPTY_DECO:
                    if (ch != null && ch != target && !target.isCharmedBy(ch)){

                        AttackIndicator.target(ch);
                        if (target.attack(ch, 1f, 0, 1f))
                            Sample.INSTANCE.play(Assets.Sounds.ROCKS);
                        Invisibility.dispel();//attack enemy, ignore reach limit
                        ((Hero) target).spendAndNext( ((Hero) target).attackDelay() );
                        ActionIndicator.clearAction();
                        CD += 60;
                    }
                    break;
                case WATER:
                    if (!Dungeon.level.water[cell]){
                        GLog.w(Messages.get(GeomancerBuff.class, "invalid_target"));
                    } else if (ScrollOfTeleportation.teleportToLocation(target, cell) ){
                        ActionIndicator.clearAction();//teleport to another water cell
                        CD += 60;
                    }
                    break;
                case CHASM:
                    if (ch != null && ch != target){
                        //trace a ballistica to our target (which will also extend past them)
                        Ballistica trajectory = new Ballistica(target.pos, ch.pos, Ballistica.STOP_TARGET);
                        //trim it to just be the part that goes past them
                        trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size() - 1), Ballistica.PROJECTILE);
                        //knock them back along that ballistica
                        WandOfBlastWave.throwChar(ch, trajectory, 3, true, false, target);

                        Sample.INSTANCE.play(Assets.Sounds.MISS);
                        ch.sprite.emitter().burst(Speck.factory(Speck.JET), 5);

                        Dungeon.hero.next();
                        ActionIndicator.clearAction();
                        CD += 60;
                    } else {
                        GLog.w(Messages.get(GeomancerBuff.class, "invalid_target"));
                    }
                    break;
            }
            BuffIndicator.refreshHero();
        }
    };
}