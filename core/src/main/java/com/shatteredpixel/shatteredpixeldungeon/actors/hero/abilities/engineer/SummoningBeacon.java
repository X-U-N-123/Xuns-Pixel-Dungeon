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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.engineer;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Adrenaline;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM100;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM200;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM201;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Golem;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfSirensSong;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.RatSkull;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.Arrays;

public class SummoningBeacon extends ArmorAbility {

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {

		hero.sprite.operate(hero.pos);
		Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
		ArrayList<Integer> nonOpenSpawnPoints = new ArrayList<>();
		ArrayList<Integer> openSpawnPoints = new ArrayList<>();

		ArrayList<Mob> machines = new ArrayList<>(Arrays.asList(
				new DM100(), new DM100(),
				new DM200(), new DM200(),
				new Golem(), new Golem())
		);
		for (int i = 0; i < hero.pointsInTalent(Talent.POWERED_DEVICE); i++) {
			machines.add(new DM200());
			machines.add(new Golem());
			machines.add(new Golem());
		}

		for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
			int p = hero.pos + PathFinder.NEIGHBOURS8[i];

			Char ch = Actor.findChar( p );
			if (ch != null && ch.alignment != Char.Alignment.ALLY){
				if (hero.hasTalent(Talent.VIOLENT_LEAP))
					ch.damage(Random.IntRange(5 + 5 * hero.pointsInTalent(Talent.VIOLENT_LEAP),
							5 + 10 * hero.pointsInTalent(Talent.VIOLENT_LEAP)), this);

			} else if (Dungeon.level.passable[p]) {
				if (Dungeon.level.openSpace[p]) openSpawnPoints.add(p);
				else nonOpenSpawnPoints.add(p);
			}
		}

		int machinesToSapwn = 2;
		float chance = hero.pointsInTalent(Talent.SKYNET) * 0.75f;
		while (Random.Float() < chance){
			machinesToSapwn ++;
			chance --;
		}

		Random.shuffle(machines);
		while (machinesToSapwn > 0 && !machines.isEmpty()
				&& (!nonOpenSpawnPoints.isEmpty() || !openSpawnPoints.isEmpty())) {

			Mob mob = machines.remove(0);
			if (Random.Float() < 1 / 50f * RatSkull.exoticChanceMultiplier() && mob instanceof DM200)
				mob = new DM201();

			int pos;
			Random.shuffle(nonOpenSpawnPoints);
			Random.shuffle(openSpawnPoints);
			if (Char.hasProp(mob, Char.Property.LARGE) && !openSpawnPoints.isEmpty()) {
				pos = openSpawnPoints.remove(0);
			} else if (!nonOpenSpawnPoints.isEmpty()){
				pos = nonOpenSpawnPoints.remove(0);
			} else {
				machinesToSapwn ++;
				continue;
			}

			mob.state = mob.HUNTING;
			Buff.affect(mob, AscensionChallenge.AscensionBuffBlocker.class);
			GameScene.add( mob );
			ScrollOfTeleportation.appear( mob, pos );
			Buff.affect(mob, ScrollOfSirensSong.Enthralled.class);
			if (hero.hasTalent(Talent.ASSAULT))
				Buff.affect(mob, Adrenaline.class, hero.pointsInTalent(Talent.ASSAULT) * 3f + 0.67f);
			//act priority problem
			machinesToSapwn--;
		}

		armor.charge -= chargeUse(hero);
		hero.spendAndNext(Actor.TICK);
		Item.updateQuickslot();

	}

	@Override
	public int icon(){
		return HeroIcon.SUMMON_BEACON;
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.POWERED_DEVICE, Talent.SKYNET, Talent.VIOLENT_LEAP, Talent.ASSAULT, Talent.HEROIC_ENERGY};
	}
}