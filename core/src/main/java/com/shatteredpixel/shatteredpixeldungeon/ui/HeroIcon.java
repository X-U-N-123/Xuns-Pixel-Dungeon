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

package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.ClericSpell;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;

//icons for hero subclasses and abilities atm, maybe add classes?
public class HeroIcon extends Image {

	private static TextureFilm film;
	private static final int SIZE = 16;

	//transparent icon
	public static final int NONE    = 255;

	//subclasses
	public static final int BERSERKER   = 0;
	public static final int GLADIATOR   = 1;
	public static final int GUARD       = 2;
	public static final int BATTLEMAGE  = 4;
	public static final int WARLOCK     = 5;
	public static final int SWITCHER    = 6;
	public static final int ASSASSIN    = 8;
	public static final int FREERUNNER  = 9;
	public static final int NINJA       = 10;
	public static final int SNIPER      = 12;
	public static final int WARDEN      = 13;
	public static final int CHAMPION    = 16;
	public static final int MONK        = 17;
	public static final int PHANTOM     = 18;
	public static final int PRIEST      = 20;
	public static final int PALADIN     = 21;

	//abilities
	public static final int HEROIC_LEAP     = 48;
	public static final int SHOCKWAVE       = 49;
	public static final int ENDURE          = 50;
	public static final int ELEMENTAL_BLAST = 51;
	public static final int WILD_MAGIC      = 52;
	public static final int WARP_BEACON     = 53;
	public static final int SMOKE_BOMB      = 54;
	public static final int DEATH_MARK      = 55;
	public static final int SHADOW_CLONE    = 56;
	public static final int SPECTRAL_BLADES = 57;
	public static final int NATURES_POWER   = 58;
	public static final int SPIRIT_HAWK     = 59;
	public static final int CHALLENGE       = 60;
	public static final int ELEMENTAL_STRIKE= 61;
	public static final int FEINT           = 62;
	public static final int ASCENDED_FORM   = 63;
	public static final int TRINITY         = 64;
	public static final int POWER_OF_MANY   = 65;
	public static final int RATMOGRIFY      = 66;

	//cleric spells
	public static final int GUIDING_LIGHT   = 80;
	public static final int HOLY_WEAPON     = 81;
	public static final int HOLY_WARD       = 82;
	public static final int HOLY_INTUITION  = 83;
	public static final int SHIELD_OF_LIGHT = 84;
	public static final int RECALL_GLYPH    = 85;
	public static final int SUNRAY          = 86;
	public static final int DIVINE_SENSE    = 87;
	public static final int BLESS           = 88;
	public static final int CLEANSE         = 89;
	public static final int RADIANCE        = 90;
	public static final int HOLY_LANCE      = 91;
	public static final int HALLOWED_GROUND = 92;
	public static final int MNEMONIC_PRAYER = 93;
	public static final int SMITE           = 94;
	public static final int LAY_ON_HANDS    = 95;
	public static final int AURA_OF_PROTECTION= 96;
	public static final int WALL_OF_LIGHT   = 97;
	public static final int DIVINE_INTERVENTION= 98;
	public static final int JUDGEMENT       = 99;
	public static final int FLASH           = 100;
	public static final int BODY_FORM       = 101;
	public static final int MIND_FORM       = 102;
	public static final int SPIRIT_FORM     = 103;
	public static final int BEAMING_RAY     = 104;
	public static final int LIFE_LINK       = 105;
	public static final int STASIS          = 106;
	public static final int EXPLOSION       = 107;
	public static final int JUSTICE_STRIKE  = 109;
	public static final int SHARED_CHARGE   = 110;
	public static final int HOLY_CHAMPION   = 111;
	public static final int HOLY_REGENERATION = 112;
	public static final int MIMIC_FORM      = 113;

	//all cleric spells have a separate icon with no background for the action indicator
	public static final int SPELL_ACTION_OFFSET      = 48;

	//action indicator visuals
	public static final int BERSERK         = 176;
	public static final int COMBO           = 177;
	public static final int PREPARATION     = 178;
	public static final int MOMENTUM        = 179;
	public static final int SNIPERS_MARK    = 180;
	public static final int WEAPON_SWAP     = 181;
	public static final int MONK_ABILITIES  = 182;
	public static final int NINJA_TELEPORT  = 183;
	public static final int MIRROR_IMAGE    = 184;

	public HeroIcon(HeroSubClass subCls){
		super( Assets.Interfaces.HERO_ICONS );
		if (film == null){
			film = new TextureFilm(texture, SIZE, SIZE);
		}
		frame(film.get(subCls.icon()));
	}

	public HeroIcon(ArmorAbility abil){
		super( Assets.Interfaces.HERO_ICONS );
		if (film == null){
			film = new TextureFilm(texture, SIZE, SIZE);
		}
		frame(film.get(abil.icon()));
	}

	public HeroIcon(ActionIndicator.Action action){
		super( Assets.Interfaces.HERO_ICONS );
		if (film == null){
			film = new TextureFilm(texture, SIZE, SIZE);
		}
		frame(film.get(action.actionIcon()));
	}

	public HeroIcon(ClericSpell spell){
		super( Assets.Interfaces.HERO_ICONS );
		if (film == null){
			film = new TextureFilm(texture, SIZE, SIZE);
		}
		frame(film.get(spell.icon()));
	}

}
