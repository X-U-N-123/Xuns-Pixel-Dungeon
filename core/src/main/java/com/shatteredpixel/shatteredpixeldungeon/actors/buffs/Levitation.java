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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class Levitation extends FlavourBuff {
	
	{
		type = buffType.POSITIVE;
	}

	public static final float DURATION	= 20f;
	public boolean fromSandstorm = false;
	
	@Override
	public boolean attachTo( Char target ) {
		if (super.attachTo( target )) {
			Roots.detach( target, Roots.class );
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void detach() {
		super.detach();
		//only press tiles if we're current in the game screen
		if (ShatteredPixelDungeon.scene() instanceof GameScene && !target.isFlying()) {
			Dungeon.level.occupyCell(target );
			if (fromSandstorm){
				target.damage(Math.round(Hero.heroDamageIntRange(25, 40)), Dungeon.hero.armorAbility);
				if (Dungeon.hero.fieldOfView[target.pos])
					Sample.INSTANCE.play(Assets.Sounds.BLAST, 1f, 0.7f);
			}
		}
	}

	//used to determine if levitation is about to end
	public boolean detachesWithinDelay(float delay){
		if (target.buff(Swiftthistle.TimeBubble.class) != null){
			return false;
		}

		if (target.buff(TimekeepersHourglass.timeFreeze.class) != null){
			return false;
		}

		return cooldown() < delay;
	}
	
	@Override
	public int icon() {
		return BuffIndicator.LEVITATION;
	}

	@Override
	public String desc() {
		String result = Messages.get(this, "desc");
		if (fromSandstorm)
			result += "\n\n" + Messages.get(this, "desc_fall");
		return result + "\n\n" + Messages.get(this, "desc_time", dispTurns());

	}

	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(1f, 2.1f, 2.5f);
	}

	@Override
	public float iconFadePercent() {
		return Math.max(0, (DURATION - visualcooldown()) / DURATION);
	}
	
	@Override
	public void fx(boolean on) {
		if (on) target.sprite.add(CharSprite.State.LEVITATING);
		else target.sprite.remove(CharSprite.State.LEVITATING);
	}

	private static final String FROM_SANDSTORM = "sandstorm";

	@Override
	public void storeInBundle( Bundle bundle ){
		super.storeInBundle(bundle);
		bundle.put(FROM_SANDSTORM, fromSandstorm);
	}

	@Override
	public void restoreFromBundle( Bundle bundle ){
		super.restoreFromBundle(bundle);
		if (bundle.contains(FROM_SANDSTORM)) fromSandstorm = bundle.getBoolean(FROM_SANDSTORM);
	}
}
