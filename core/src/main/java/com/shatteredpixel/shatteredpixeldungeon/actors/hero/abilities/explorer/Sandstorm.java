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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Adrenaline;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barkskin;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Levitation;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSight;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Barricade;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfAquaticRejuvenation;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Sandstorm extends ArmorAbility {

	{
		baseChargeUse = 35f;
	}

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {
		Ballistica aim;
		//The direction of the aim only matters if it goes outside the map
		//So we try to aim in the cardinal direction that has the most space
		int x = hero.pos % Dungeon.level.width();
		int y = hero.pos / Dungeon.level.width();

		if (Math.max(x, Dungeon.level.width()-x) >= Math.max(y, Dungeon.level.height()-y)){
			if (x > Dungeon.level.width()/2){
				aim = new Ballistica(hero.pos, hero.pos - 1, Ballistica.WONT_STOP);
			} else {
				aim = new Ballistica(hero.pos, hero.pos + 1, Ballistica.WONT_STOP);
			}
		} else {
			if (y > Dungeon.level.height()/2){
				aim = new Ballistica(hero.pos, hero.pos - Dungeon.level.width(), Ballistica.WONT_STOP);
			} else {
				aim = new Ballistica(hero.pos, hero.pos + Dungeon.level.width(), Ballistica.WONT_STOP);
			}
		}

		int terrain = Dungeon.level.map[hero.pos];

		int aoeSize = 4 + hero.pointsInTalent(Talent.GLOOM_ABOVE);

		int projectileProps = Ballistica.STOP_SOLID | Ballistica.STOP_TARGET;

		ConeAOE aoe = new ConeAOE(aim, aoeSize, 360, projectileProps);

		for (Ballistica ray : aoe.outerRays){
			((MagicMissile)hero.sprite.parent.recycle( MagicMissile.class )).reset(
			MagicMissile.FORCE_CONE,
			hero.sprite,
			ray.path.get(ray.dist),
			null
			);
		}

		Sample.INSTANCE.play( Assets.Sounds.MISS, 1.5f, 0.6f);

		final float effectMulti = 1f + 0.25f*hero.pointsInTalent(Talent.SAND_FLOW);

		//cast a ray 2/3 the way, and do effects
		((MagicMissile)hero.sprite.parent.recycle( MagicMissile.class )).reset(
				MagicMissile.FORCE_CONE,
				hero.sprite,
				aim.path.get(Math.min(aoeSize / 2, aim.path.size()-1)),
				new Callback() {
					@Override
					public void call() {

						int charsHit = 0;
						for (int cell : aoe.cells) {

							//### Deal damage ###
							Char mob = Actor.findChar(cell);
							int damage = Math.round(Hero.heroDamageIntRange(15, 25));

							if (mob != null && mob.alignment != Char.Alignment.ALLY){
								if (terrain != Terrain.CHASM) mob.damage(damage, hero.armorAbility);
								charsHit++;

								//### Other Char Effects ###
								// empty effect
								if (terrain == Terrain.EMPTY || terrain == Terrain.EMPTY_DECO){
									Buff.affect(mob, Slow.class, 5 * effectMulti);
								}
								// chasm effect
								if (terrain == Terrain.CHASM){
									Buff.affect(mob, Levitation.class, 5 - effectMulti).fromSandstorm = true;
								}
								// pedestal and door effect
								if (terrain == Terrain.PEDESTAL || terrain == Terrain.OPEN_DOOR){
									Buff.affect(mob, Blindness.class, 4 * effectMulti);
									if (((Mob)mob).state == ((Mob) mob).HUNTING) ((Mob)mob).state = ((Mob) mob).WANDERING;
								}
								// ember effect
								if (terrain == Terrain.EMBERS){
									Burning fire = mob.buff(Burning.class);
									if (fire != null){
										for (int i = 0; i < 5 * effectMulti; i++) {
											fire.act();
											fire.fireSpend(-1f);
										}
									}
								}
								// sp effect
								if (terrain == Terrain.EMPTY_SP){
									//barricade position is the closest adjacent cell to the defender
									int pos = -1;
									for (int i : PathFinder.NEIGHBOURS8){
										if (!Dungeon.level.solid[mob.pos+i] && (pos == -1 ||
										Dungeon.level.trueDistance(hero.pos, mob.pos+i) < Dungeon.level.trueDistance(hero.pos, pos))){
											pos = mob.pos+i;
										}
									}
									if (pos == -1) {
										pos = mob.pos;
									}

									//build a barricade that's weak but can stop the enemy for a while
									if (Actor.findChar(pos) == null && Dungeon.level.passable[pos]){
										Barricade.buildBarricade(pos, 10, Char.Alignment.ALLY, 4f * effectMulti);
									}
								}
							}

						}

						//### Self-Effects ###
						if (charsHit > 0){
							int point = hero.pointsInTalent(Talent.HEART_OF_STORM);
							if (point > 0){
								int time = point * Math.min(charsHit, 4 + point);

								Buff.affect(hero, MindVision.class, time);
								Buff.affect(hero, MagicalSight.class, time);
								Dungeon.observe();
								Dungeon.hero.checkVisibleMobs();
							}

							// water effect
							if (terrain == Terrain.WATER){
								Buff.affect(hero, ElixirOfAquaticRejuvenation.AquaHealing.class)
									.set(Math.round(3 * effectMulti * Math.min(charsHit, 4)) );
							}
							// grass effect
							else if (terrain == Terrain.GRASS || terrain == Terrain.FURROWED_GRASS || terrain == Terrain.HIGH_GRASS){
								Barkskin.conditionallyAppend( hero, Math.round(4 * effectMulti * Math.min(charsHit, 4)), 3 );
							}
							// general effect
							else if (terrain != Terrain.EMPTY && terrain != Terrain.EMPTY_DECO
							&& terrain != Terrain.CHASM
							&& terrain != Terrain.PEDESTAL && terrain != Terrain.OPEN_DOOR
							&& terrain != Terrain.EMBERS
							&& terrain != Terrain.EMPTY_SP) {
								Buff.affect(hero, Adrenaline.class, 3 * effectMulti * Math.min(charsHit, 4));
							}
						}

						PathFinder.buildDistanceMap( hero.pos, BArray.not( Dungeon.level.solid, null ), 2 );

						int driftTerrain = Dungeon.level.map[hero.pos];
						if (canDrift(driftTerrain)){
							for (int i : PathFinder.NEIGHBOURS8) {
								if (canDrift(Dungeon.level.map[hero.pos + i])
								&& Random.Float() <= hero.pointsInTalent(Talent.DRIFT_SAND) / 4f){
									Level.set(hero.pos + i, driftTerrain);
									GameScene.updateMap(hero.pos + i);
								}
							}
						}

						hero.spendAndNext(Actor.TICK);
					}
				}
		);

		hero.sprite.operate( hero.pos );
		Invisibility.dispel();
		hero.busy();

		armor.charge -= chargeUse(hero);
		armor.updateQuickslot();

	}

	@Override
	public String desc() {
		String desc = Messages.get(this, "desc");
		if (Game.scene() instanceof GameScene){
			desc += "\n\n" + terrainDesc(Dungeon.hero);
		}
		desc += "\n\n" + Messages.get(this, "cost", (int)baseChargeUse);
		return desc;
	}

	public static String terrainDesc(Hero hero){
		int terr = Dungeon.level.map[hero.pos];
		switch (terr){
			case Terrain.EMPTY: case Terrain.EMPTY_DECO:
				return Messages.get(Sandstorm.class, "empty_desc");
			case Terrain.WATER:
				return Messages.get(Sandstorm.class, "water_desc");
			case Terrain.GRASS: case Terrain.FURROWED_GRASS: case Terrain.HIGH_GRASS:
				return Messages.get(Sandstorm.class, "grass_desc");
			case Terrain.CHASM:
				return Messages.get(Sandstorm.class, "chasm_desc");
			case Terrain.OPEN_DOOR: case Terrain.PEDESTAL:
				return Messages.get(Sandstorm.class, "door_desc");
			case Terrain.EMBERS:
				return Messages.get(Sandstorm.class, "ember_desc");
			case Terrain.EMPTY_SP:
				return Messages.get(Sandstorm.class, "sp_desc");
			default:
				return Messages.get(Sandstorm.class, "generic_desc");
		}
	}

	@Override
	public int icon() {
		return HeroIcon.SANDSTORM;
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.GLOOM_ABOVE, Talent.SAND_FLOW, Talent.HEART_OF_STORM, Talent.DRIFT_SAND, Talent.HEROIC_ENERGY};
	}

	public static boolean canDrift(int terrain){
		return
			terrain == Terrain.EMPTY || terrain == Terrain.EMPTY_DECO
			|| terrain == Terrain.EMPTY_SP
			|| terrain == Terrain.WATER
			|| terrain == Terrain.GRASS || terrain == Terrain.FURROWED_GRASS
			|| terrain == Terrain.EMBERS;
	}
}