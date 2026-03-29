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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class Chixiao extends MeleeWeapon {

	private boolean slow = false;
	private static final float fastDly = 0.5f;
	private static final float slowDly = 1.5f;

	{
		image = ItemSpriteSheet.CHIXIAO;
		hitSound = Assets.Sounds.HIT_SLASH;
		hitSoundPitch = 0.9f;
		DLY = 1.75f;

		tier = 6;
	}

	@Override
	public int damageRoll( Char owner ) {
		return Math.round(super.damageRoll( owner ) * (slow ? 1.75f : 0.25f));
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		DLY = slow ? slowDly : fastDly;
		slow = !slow;
		return super.proc( attacker, defender, damage );
	}

	public String statsInfo(){
		String stats = Messages.get(this, "stats_desc");
		if (slow) stats += Messages.get(this, "stats_slow");
		else      stats += Messages.get(this, "stats_fast");
		return stats;
	}

	@Override
	public String targetingPrompt() {
		return Messages.get(this, "prompt");
	}

	@Override
	protected void duelistAbility(Hero hero, Integer target) {
		if (target == null) {
			return;
		}

		Char enemy = Actor.findChar(target);
		if (enemy == null || enemy == hero || hero.isCharmedBy(enemy) || !Dungeon.level.heroFOV[target]) {
			GLog.w(Messages.get(this, "ability_no_target"));
			return;
		}

		hero.belongings.abilityWeapon = this;
		if (!hero.canAttack(enemy)){
			GLog.w(Messages.get(this, "ability_target_range"));
			hero.belongings.abilityWeapon = null;
			return;
		}
		hero.belongings.abilityWeapon = null;

		hero.sprite.attack(enemy.pos, () -> {
			beforeAbilityUsed(hero, enemy);
			AttackIndicator.target(enemy);

			boolean hit = Char.hit(hero, enemy, false);

			if (hero.attack(enemy, hit ? 1 : 2, 0, Char.INFINITE_ACCURACY)){
				Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
			}

			Invisibility.dispel();
			hero.spendAndNext(hero.attackDelay());

			if (!enemy.isAlive()){
				hero.next();
				onAbilityKill(hero, enemy);
			}
			afterAbilityUsed(hero);
		});
	}

	@Override
	public void hitSound( float pitch ){
		Sample.INSTANCE.play(hitSound, 1, pitch * hitSoundPitch * (slow ? 1.1f : 0.9f));
	}

	private static final String SLOW = "is_slow";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put(SLOW, slow);
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		slow = bundle.getBoolean(SLOW);
		DLY = slow ? fastDly : slowDly;
	}
}
