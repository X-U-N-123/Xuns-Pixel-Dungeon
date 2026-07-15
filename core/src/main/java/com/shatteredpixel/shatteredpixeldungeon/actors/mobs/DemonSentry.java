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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Torch;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DemonSentrySprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class DemonSentry extends Mob {

	private float callCD = 0;

	{
		spriteClass = DemonSentrySprite.class;

		HP = HT = 100;
		defenseSkill = 15;
		useParry = true;
		viewDistance = Light.DISTANCE;

		EXP = 13;
		maxLvl = 28;

		flying = true;

		loot = Torch.class;
		lootChance = 0.5f;

		properties.add(Property.DEMONIC);
		properties.add(Property.IMMOVABLE);

		WANDERING = new Wandering();
		state = WANDERING;
	}

	@Override
	public int drRoll() {
		return super.drRoll() + Random.NormalIntRange(5, 15);
	}

	@Override
	protected void spend( float time ) {
		callCD -= time;
		super.spend( time );
	}

	@Override
	public void damage(int dmg, Object src) {
		super.damage(dmg, src);
		callCD -= 0.5f;
		callEnemy();
	}

	private class Wandering extends Mob.Wandering {

		@Override
		protected boolean noticeEnemy(){
			super.noticeEnemy();
			callEnemy();
			return true;
		}
	}

	@Override
	protected boolean canAttack(Char enemy) {
		return false;
	}

	@Override
	protected boolean getCloser(int target) {
		return false;
	}
	@Override
	protected boolean getFurther(int target) {
		return false;
	}

	private void callEnemy(){
		if (enemy == null || callCD > 0) return;
		Mob toCall = null;

		for (Mob m : Dungeon.level.mobs.toArray(new Mob[0]))
			if (m.alignment == alignment && m.state != m.HUNTING && m.state != m.FLEEING) {
				if ((toCall == null
						|| Dungeon.level.distance(m.pos, enemy.pos) < Dungeon.level.distance(toCall.pos, enemy.pos))
						&& !Char.hasProp(m, Property.IMMOVABLE))
					toCall = m;
			}

		if (toCall != null){
			toCall.beckon(enemy.pos);
			Buff.affect(toCall, Haste.class, 3f);

			sprite.centerEmitter().start(Speck.factory(Speck.SCREAM), 0.3f, 3);
			Sample.INSTANCE.play(Assets.Sounds.CHALLENGE);
			if (alignment == Alignment.ENEMY) GLog.n(Messages.get(this, "call"));

			callCD = Random.NormalFloat(6f, 8f);
		}
	}

	private static final String CALL_CD = "callcd";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(CALL_CD, callCD);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		callCD = bundle.getFloat(CALL_CD);
	}
}