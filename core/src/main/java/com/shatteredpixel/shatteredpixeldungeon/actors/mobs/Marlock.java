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
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WarlockSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class Marlock extends Warlock {

	{
		spriteClass = WarlockSprite.MarlockSprite.class;
	}

	@Override
	public int attackSkill( Char target ) {
		return 40;
	}

	@Override
	protected boolean canAttack( Char enemy ) {
		return !Dungeon.level.adjacent( pos, enemy.pos )
				&& (super.canAttack(enemy) || new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE).collisionPos == enemy.pos);
	}

	@Override
	protected void hitProc(Char enemy){
		if (buff(MagicImmune.class) == null){
			MarlockMark b = enemy.buff(MarlockMark.class);
			if (b != null){
				b.countDown();
			} else {
				Buff.affect(enemy, MarlockMark.class, 5);
				Sample.INSTANCE.play(Assets.Sounds.CURSED);
			}
		}
	}

	@Override
	public Item createLoot(){
		return new PotionOfExperience();
	}

	@Override
	protected boolean getCloser( int target ) {
		if (state == HUNTING) {
			return enemySeen && getFurther( target );
		} else {
			return super.getCloser( target );
		}
	}

	@Override
	public void aggro(Char ch) {
		//cannot be aggroed to something it can't see
		//skip this check if FOV isn't initialized
		if (ch == null || fieldOfView == null
				|| fieldOfView.length != Dungeon.level.length() || fieldOfView[ch.pos]) {
			super.aggro(ch);
		}
	}

	public static class MarlockMark extends FlavourBuff {

		int countDown = 4;

		@Override
		public int icon() {
			return BuffIndicator.INVERT_MARK;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(0.6f, 0.6f, 0.6f);
		}

		public void countDown() {
			countDown --;
			timeToNow();
			spend(5f);

			if (countDown <= 0){
				detach();

				if (Char.hasProp(target, Property.BOSS) || Char.hasProp(target, Property.BOSS_MINION)){
					target.damage(5 * Dungeon.scalingDepth(), this);
				} else {
					target.HP = 0;
					target.die(this);
					Sample.INSTANCE.play(Assets.Sounds.CURSED);

					if (target == Dungeon.hero) {
						Badges.validateDeathFromEnemyMagic();
						Dungeon.fail( this );
						GLog.n( Messages.get(this, "mark_kill") );
					}
				}
			}
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (3-visualcooldown()) / 3f);
		}

		@Override
		public String iconTextDisplay() {
			return Integer.toString(countDown);
		}

		private static final String COUNTDOWN = "count_down";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(COUNTDOWN, countDown);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			countDown = bundle.getInt(COUNTDOWN);
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc", countDown, visualcooldown());
		}
	}
}