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

package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;

public class WeakeningTrap extends Trap{

	{
		color = GREEN;
		shape = WAVES;
	}

	@Override
	public void activate() {
		if (Dungeon.hero.pos == pos && !Dungeon.hero.isFlying()
		&& Dungeon.hero.pointsInTalent(Talent.FRIENDLY_MECHANISM) >= 3 && Dungeon.hero.buff(FriendlyMechanismCooldown.class) == null){
			disarm();
			Buff.affect(Dungeon.hero, FriendlyMechanismCooldown.class, 150f);
			return;
		}
		if (Dungeon.level.heroFOV[ pos ]){
			CellEmitter.get(pos).burst(ShadowParticle.UP, 5);
		}

		Char ch = Actor.findChar( pos );
		if (ch != null){
			if (ch.properties().contains(Char.Property.BOSS)
				|| ch.properties().contains(Char.Property.MINIBOSS)){
				Buff.prolong( ch, Weakness.class, Weakness.DURATION/2f );
			}
			Buff.prolong( ch, Weakness.class, Weakness.DURATION*3f );
			if (ch instanceof Mob){
				Buff.prolong(ch, Trap.HazardAssistTracker.class, HazardAssistTracker.DURATION);
			}
		}
	}
}
