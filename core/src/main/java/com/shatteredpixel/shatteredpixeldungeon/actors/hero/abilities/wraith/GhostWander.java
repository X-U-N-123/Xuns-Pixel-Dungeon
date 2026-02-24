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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.wraith;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dread;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class GhostWander extends ArmorAbility {

    {
        baseChargeUse = 35f;
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt");
    }

    @Override
    public float chargeUse( Hero hero ) {
        float chargeUse = super.chargeUse(hero);
        if (hero.buff(SoulfreeGhostTracker.class) != null)
            chargeUse *= 1 - hero.pointsInTalent(Talent.SOULFREE_GHOST) / 8f;
        return chargeUse;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        if (target != null && target != hero.pos) {

            if (hero.rooted){
                PixelScene.shake( 1, 1f );
                return;
            }

            Ballistica route = new Ballistica(hero.pos, target, Ballistica.STOP_TARGET);

			PathFinder.buildDistanceMap(hero.pos, BArray.or(Dungeon.level.passable, Dungeon.level.avoid, null));

            //can't occupy the same cell as another char, so move back one.
            int backTrace = route.dist-1;
            while ((Actor.findChar( target ) != null || PathFinder.distance[target] == Integer.MAX_VALUE
					|| (Dungeon.level.solid[target]) && Dungeon.level.map[target] != Terrain.DOOR) //the door is passable actually
					&& target != hero.pos) {
                target = route.path.get(backTrace);
                backTrace--;
            }

			int solidCells = 0;
            ArrayList<Integer> routeCells = new ArrayList<>();
            for (int i : route.path) {
                routeCells.add(i);
				if (Dungeon.level.solid[i]) solidCells ++;
                if (i == route.collisionPos) break;
            }
			if (solidCells > 2 * hero.pointsInTalent(Talent.SOUL_VANISHING)){
				GLog.w(Messages.get(this, "invalid_pos"));
				return;
			}
            armor.charge -= chargeUse( hero );
            armor.updateQuickslot();

            hero.busy();
            int finalCell = target;
            hero.sprite.jump(hero.pos, target, 0, Dungeon.level.trueDistance(hero.pos, target) * 0.07f, ()->{
                hero.move(finalCell);
                Dungeon.level.occupyCell(hero);
                Dungeon.observe();
                GameScene.updateFog();
                Sample.INSTANCE.play(Assets.Sounds.BADGE);

                for (Mob m : Dungeon.level.mobs.toArray(new Mob[0])) {
                    if (Dungeon.level.adjacent(m.pos, finalCell) && !m.isImmune(Dread.class)
                            && Random.Float() <= hero.pointsInTalent(Talent.FACE_TO_FACE_FRIGHT) / 4f){
                        Buff.affect(m, Dread.class).object = hero.id();
                        new Flare( 5, 32 ).color( 0xFF0000, true ).show( m.sprite, 2f );
                        continue;
                    }
                    for (int i : routeCells) {
                        if (Dungeon.level.distance(m.pos, i) <= 1 + hero.pointsInTalent(Talent.FEAR_SPREADING)
								&& !m.isImmune(Terror.class)){
							Buff.prolong(m, Terror.class, 15f).object = hero.id();
							break;
                        }
                    }
                }

                Invisibility.dispel();
                hero.spendAndNext(Actor.TICK);

                if (hero.buff(SoulfreeGhostTracker.class) != null){
                    hero.buff(SoulfreeGhostTracker.class).detach();
                } else if (hero.hasTalent(Talent.SOULFREE_GHOST)) {
                    Buff.affect(hero, SoulfreeGhostTracker.class, 3);
                }
            });
        }
    }

    public static class SoulfreeGhostTracker extends FlavourBuff {}

    @Override
    public int icon() {
        return HeroIcon.GHOSTWANDER;
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.FACE_TO_FACE_FRIGHT, Talent.SOUL_VANISHING, Talent.FEAR_SPREADING, Talent.SOULFREE_GHOST, Talent.HEROIC_ENERGY};
    }
}
