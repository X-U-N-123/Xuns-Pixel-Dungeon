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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BrokenArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Daze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hex;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class EvilUnfold extends ArmorAbility {

	{
		baseChargeUse = 50;
	}

	@Override
	protected void activate(ClassArmor armor, Hero hero, Integer target) {
		Buff.affect(hero, Evil.class, 10f);
		hero.sprite.operate(hero.pos);
		Sample.INSTANCE.play(Assets.Sounds.BURNING);
		new Flare(6, 48).color(0xFF0000, true).show(hero.sprite, 2f);

		armor.charge -= chargeUse(hero);
		armor.updateQuickslot();
		Invisibility.dispel();
		hero.spendAndNext(Actor.TICK);
	}

	@Override
	public int icon() {
		return HeroIcon.EVILUNFOLD;
	}

	@Override
	public Talent[] talents() {
		return new Talent[]{Talent.HEROIC_ENERGY};
	}

	public static class Evil extends FlavourBuff {

		{
			type = buffType.POSITIVE;
		}

		public static float DURATION = 10f;
		private float delayedTime = 0f;

		@Override
		public int icon() {
			return BuffIndicator.EVILUNFOLD;
		}

		public static void giveDebuff(int point, Char c){
			ArrayList<Class<? extends FlavourBuff>> debuffs = new ArrayList<>();
			if (c.buff(Vulnerable.class) == null) debuffs.add(Vulnerable.class);
			else if (c.buff(BrokenArmor.class) == null && point >= 1)
				debuffs.add(BrokenArmor.class);
			if (c.buff(Hex.class) == null)        debuffs.add(Hex.class);
			else if (c.buff(Daze.class) == null && point >= 2)
				debuffs.add(Daze.class);
			if (c.buff(Cripple.class) == null)    debuffs.add(Cripple.class);
			else if (c.buff(Roots.class) == null && point >= 3)
				debuffs.add(Roots.class);
			if (c.buff(Slow.class) == null)       debuffs.add(Slow.class);
			else if (c.buff(Paralysis.class) == null && point >= 4)
				debuffs.add(Paralysis.class);

			if (!debuffs.isEmpty()) Buff.prolong(c, debuffs.get(Random.Int(debuffs.size())), 10f);
		}

		public void delayTime(float dmg){
			if (!((Hero)target).hasTalent(Talent.IMMORTAL_EVIL)) return;
			float toDly = dmg / (target.HT * 0.15f - 0.025f * ((Hero)target).pointsInTalent(Talent.IMMORTAL_EVIL));
			toDly = Math.min(toDly, 10 - delayedTime);
			spend(toDly);
			delayedTime += toDly;
		}
	}
}
