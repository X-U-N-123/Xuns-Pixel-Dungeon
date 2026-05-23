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

package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Doom;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RipperSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.TargetHealthIndicator;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class Parasite extends RipperDemon {

	{
		spriteClass = RipperSprite.ParasiteSprite.class;
		maxLvl = 28;

		lootChance = 1;
		loot = Generator.randomMissile(5).random();
	}

	@Override
	public float lootChance() {
		//each drop makes future drops 1/3 as likely
		// so loot chance looks like: 1/33, 1/100, 1/300, 1/900, etc.
		return super.lootChance() * (float)Math.pow(1/3f, Dungeon.LimitedDrops.PARA_MISSILE.count);
	}

	@Override
	public Item createLoot() {
		Dungeon.LimitedDrops.PARA_MISSILE.count++;
		return super.createLoot();
	}

	@Override
	protected void doLeap(Char leapVictim, int endPos){
		if (leapVictim != null && alignment != leapVictim.alignment){
			if (hit(this, leapVictim, Char.INFINITE_ACCURACY, false)
					&& leapVictim.buff(Parasitism.class) == null) {
				Buff.affect(leapVictim, Parasitism.class).parasite = this;

				Doom d = buff(Doom.class);
				Actor.remove(this);
				Dungeon.level.mobs.remove(this);
				TargetHealthIndicator.instance.target(null);
				sprite.kill();
				if (d != null) add(d);

				leapVictim.sprite.flash();
				Sample.INSTANCE.play(Assets.Sounds.HIT);
			} else {
				leapVictim.sprite.showStatus( CharSprite.NEUTRAL, leapVictim.defenseVerb() );
				Sample.INSTANCE.play(Assets.Sounds.MISS);
			}
		}

		if (Dungeon.level.mobs.contains(this)){
			if (endPos != leapPos) {
				Actor.add(new Pushing(this, leapPos, endPos));
			}

			pos = endPos;
			sprite.idle();
			Dungeon.level.occupyCell(this);
		}
		leapPos = -1;
		next();
	}

	@Override
	protected int leapCDStart(){
		return Random.NormalIntRange(6, 10);
	}

	public static class Parasitism extends Buff {

		public Parasite parasite;
		private int Time = 12;

		@Override
		public int icon() {
			return BuffIndicator.PARASITE;
		}

		private static final String TIME_PARA = "time_para";
		private static final String PARASITE =  "parasite";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(TIME_PARA, Time);
			bundle.put(PARASITE, parasite);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			Time = bundle.getInt(TIME_PARA);
			parasite = (Parasite)bundle.get(PARASITE);
		}

		@Override
		public boolean act() {
			Time -= TICK;
			spend(TICK);
			target.damage(12 - Time, this);
			if (Time <= 0) detach();
			return true;
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (12-Time) / 12f);
		}

		@Override
		public String iconTextDisplay() {
			return String.valueOf(Time);
		}

		@Override
		public void detach() {

			if (parasite != null) {
				parasite.pos = target.pos;
				if (Actor.findChar( target.pos ) != null) {
					ArrayList<Integer> candidates = new ArrayList<>();
					for (int n : PathFinder.NEIGHBOURS8) {
						int cell = target.pos + n;
						if (Dungeon.level.passable[cell]
								&& Actor.findChar( cell ) == null
								&& (!Char.hasProp(parasite, Property.LARGE) || Dungeon.level.openSpace[cell]))
							candidates.add( cell );
					}

					if (!candidates.isEmpty()) {
						int newPos = Random.element( candidates );
						Actor.add( new Pushing( parasite, parasite.pos, newPos ) );
						parasite.pos = newPos;

					} else return;
				}
				Actor.add(parasite);
				parasite.timeToNow();
				parasite.spendConstant(1f);
				GameScene.add(parasite);
				Dungeon.level.occupyCell( parasite );

				parasite.sprite.turnTo(parasite.pos, target.pos);
				parasite.sprite.idle();
				Sample.INSTANCE.play(Assets.Sounds.HIT_STAB);
				Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
			}
			super.detach();
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc", Time);
		}
	}
}