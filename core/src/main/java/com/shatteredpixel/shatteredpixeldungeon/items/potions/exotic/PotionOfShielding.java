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

package com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;

public class PotionOfShielding extends ExoticPotion {
	
	{
		icon = ItemSpriteSheet.Icons.POTION_SHIELDING;
	}
	
	@Override
	public void apply(Hero hero) {
		identify();

		if (Dungeon.isChallenged(Challenges.NO_HEALING)){
			PotionOfHealing.pharmacophobiaProc(hero);
		} else {
			//~75% of a potion of healing
			Buff.affect(hero, Barrier.class).setShield((int) (0.6f * hero.HT + 10));
			hero.sprite.showStatusWithIcon( CharSprite.POSITIVE, Integer.toString((int) (0.6f * hero.HT + 10)), FloatingText.SHIELDING );
		}
	}

	@Override
	public void shatter(int cell) {
		Char ch = Actor.findChar(cell);

		if (ch == null){
			super.shatter(cell);
		} else {
			splash( cell );
			if (Dungeon.level.heroFOV[cell]) {
				Sample.INSTANCE.play(Assets.Sounds.SHATTER);
				identify();
			}

			if (ch instanceof Hero && Dungeon.isChallenged(Challenges.NO_HEALING)){
				PotionOfHealing.pharmacophobiaProc((Hero)ch);
			} else {
				//~75% of a potion of healing
				Buff.affect(ch, Barrier.class).setShield((int) (0.6f * Dungeon.hero.HT + 10));
				ch.sprite.showStatusWithIcon( CharSprite.POSITIVE, Integer.toString((int) (0.6f * Dungeon.hero.HT + 10)), FloatingText.SHIELDING );
			}
		}
	}
}
