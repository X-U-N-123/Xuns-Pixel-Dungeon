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

package com.shatteredpixel.shatteredpixeldungeon.items.food;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArtifactRecharge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bless;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.utils.Holiday;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class Pasty extends Food {

	{
		reset();

		energy = Hunger.STARVING;

		bones = true;
	}
	
	@Override
	public void reset() {
		super.reset();
		switch(Holiday.getCurrentHoliday()){
			case NONE: default:
				image = ItemSpriteSheet.PASTY;
				break;
			case LUNAR_NEW_YEAR:
				image = ItemSpriteSheet.STEAMED_FISH;
				break;
			case APRIL_FOOLS:
				image = ItemSpriteSheet.CHOC_AMULET;
				break;
			case XUNS_BIRTHDAY:
				image = ItemSpriteSheet.XUNS_CAKE;
				break;
			case EASTER:
				image = ItemSpriteSheet.EASTER_EGG;
				break;
			case DRAGON_BOAT:
				image = ItemSpriteSheet.ZONGZI;
				break;
			case SHATTEREDPD_BIRTHDAY:
				image = ItemSpriteSheet.SHATTERED_CAKE;
				break;
			case MID_AUTUMN:
				image = ItemSpriteSheet.MOONCAKE;
			break;
			case HALLOWEEN:
				image = ItemSpriteSheet.PUMPKIN_PIE;
				break;
			case PD_BIRTHDAY:
				image = ItemSpriteSheet.VANILLA_CAKE;
				break;
			case WINTER_HOLIDAYS:
				image = ItemSpriteSheet.CANDY_CANE;
				break;
			case NEW_YEARS:
				image = ItemSpriteSheet.SPARKLING_POTION;
				break;
		}
	}

	@Override
	protected void eatSFX() {
		switch(Holiday.getCurrentHoliday()){
			case NEW_YEARS:
				Sample.INSTANCE.play( Assets.Sounds.DRINK );
				return;
		}
		super.eatSFX();
	}

	@Override
	protected void satisfy(Hero hero) {
		if (Holiday.getCurrentHoliday() == Holiday.LUNAR_NEW_YEAR){
			//main item only clears 300 hunger on lunar new year...
			energy = Hunger.HUNGRY;
		}

		super.satisfy(hero);
		
		switch(Holiday.getCurrentHoliday()){
			default:
				break; //do nothing extra
			case LUNAR_NEW_YEAR:
				//...but it also awards an extra item that restores 150 hunger
				FishLeftover left = new FishLeftover();
				if (!left.collect()){
					Dungeon.level.drop(left, hero.pos).sprite.drop();
				}
				break;
			case APRIL_FOOLS:
				Sample.INSTANCE.play(Assets.Sounds.MIMIC);
			case EASTER:
				ArtifactRecharge.chargeArtifacts(hero, 2f);
				ScrollOfRecharging.charge( hero );
				break;
			case DRAGON_BOAT:
				// bless hero for 40 turns
				Buff.affect(hero, Bless.class, 40f);
				new Flare( 6, 32 ).color(0xFFFF00, true).show( hero.sprite, 2f );
				if (Random.Float() < 0.5f){
					GLog.i( Messages.get(this, "sweet") );
				} else {
					GLog.i( Messages.get(this, "salty") );
				}
				break;
			/*case PRIDE:
				Char target = null;

				//charms an adjacent non-boss enemy, prioritizing the one the hero is focusing on
				for (Char ch : Actor.chars()){
					if (!Char.hasProp(ch, Char.Property.BOSS)
							&& !Char.hasProp(ch, Char.Property.MINIBOSS)
							&& ch.alignment == Char.Alignment.ENEMY
							&& Dungeon.level.adjacent(hero.pos, ch.pos)){
						if (target == null || ch == TargetHealthIndicator.instance.target()){
							target = ch;
						}
					}
				}

				if (target != null){
					Buff.affect(target, Charm.class, 5f).object = hero.id();
				}
				hero.sprite.emitter().burst(RainbowParticle.BURST, 15);
				break;*/
			case SHATTEREDPD_BIRTHDAY:
			case XUNS_BIRTHDAY:
			case PD_BIRTHDAY:
				//gives 10% of level in exp, min of 2
				int expToGive = Math.max(2, hero.maxExp()/10);
				hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(expToGive), FloatingText.EXPERIENCE);
				hero.earnExp(expToGive, PotionOfExperience.class);
				break;
			case MID_AUTUMN:
				Buff.affect(hero, Invisibility.class, 10f);
				break;
			case HALLOWEEN:
				//heals for 5% max hp, min of 3
				int toHeal = Math.max(3, hero.HT/20);
				hero.HP = Math.min(hero.HP + toHeal, hero.HT);
				hero.sprite.showStatusWithIcon( CharSprite.POSITIVE, Integer.toString(toHeal), FloatingText.HEALING );
				break;
			case WINTER_HOLIDAYS:
				hero.belongings.charge(0.5f); //2 turns worth
				ScrollOfRecharging.charge( hero );
				break;
			case NEW_YEARS:
				//shields for 10% of max hp, min of 5
				int toShield = Math.max(5, hero.HT/10);
				Buff.affect(hero, Barrier.class).setShield(toShield);
				hero.sprite.showStatusWithIcon( CharSprite.POSITIVE, Integer.toString(toShield), FloatingText.SHIELDING );
				break;
		}
	}

	@Override
	public String name() {
		switch(Holiday.getCurrentHoliday()){
			case NONE: default:
				return super.name();
			case LUNAR_NEW_YEAR:
				return Messages.get(this, "fish_name");
			case APRIL_FOOLS:
				return Messages.get(this, "amulet_name");
			case EASTER:
				return Messages.get(this, "egg_name");
			case XUNS_BIRTHDAY:
				return Messages.get(this, "xuns_name");
			case DRAGON_BOAT:
				return Messages.get(this, "zongzi_name");
			case SHATTEREDPD_BIRTHDAY:
				return Messages.get(this, "shattered_name");
			case HALLOWEEN:
				return Messages.get(this, "pie_name");
			case MID_AUTUMN:
				return Messages.get(this, "mooncake_name");
			case PD_BIRTHDAY:
				return Messages.get(this, "vanilla_name");
			case WINTER_HOLIDAYS:
				return Messages.get(this, "cane_name");
			case NEW_YEARS:
				return Messages.get(this, "sparkling_name");
		}
	}

	@Override
	public String desc() {
		switch(Holiday.getCurrentHoliday()){
			case NONE: default:
				return super.desc();
			case LUNAR_NEW_YEAR:
				return Messages.get(this, "fish_desc");
			case APRIL_FOOLS:
				return Messages.get(this, "amulet_desc");
			case EASTER:
				return Messages.get(this, "egg_desc");
			case XUNS_BIRTHDAY:
				return Messages.get(this, "xuns_desc");
			case SHATTEREDPD_BIRTHDAY:
				return Messages.get(this, "shattered_desc");
			case DRAGON_BOAT:
				return Messages.get(this, "zongzi_desc");
			case HALLOWEEN:
				return Messages.get(this, "pie_desc");
			case MID_AUTUMN:
				return Messages.get(this, "mooncake_desc");
			case PD_BIRTHDAY:
				return Messages.get(this, "vanilla_desc");
			case WINTER_HOLIDAYS:
				return Messages.get(this, "cane_desc");
			case NEW_YEARS:
				return Messages.get(this, "sparkling_desc");
		}
	}
	
	@Override
	public int value() {
		return 20 * quantity;
	}

	public static class FishLeftover extends Food {

		{
			image = ItemSpriteSheet.FISH_LEFTOVER;
			energy = Hunger.HUNGRY/2;
		}

		@Override
		public int value() {
			return 10 * quantity;
		}
	}
}
