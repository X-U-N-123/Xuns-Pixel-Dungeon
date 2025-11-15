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

package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Daze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.WildMagic;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.RockFall;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Callback;
import com.watabou.utils.ColorMath;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class WandOfAvalanche extends DamageWand {

	{
		image = ItemSpriteSheet.WAND_AVALANCHE;

		collisionProperties = Ballistica.STOP_SOLID | Ballistica.STOP_TARGET;
	}

	//2/4/6 base damage with 1/2/3 scaling based on charges used
	public int min(int lvl){
		return (2+lvl) * chargesPerCast();
	}

	//8/18/40 base damage with 4/8/12 scaling based on charges used
	public int max(int lvl){
		switch (chargesPerCast()){
			case 1: default:
				return 8 + (3*lvl);
			case 2:
				return 18 + (6*lvl);
			case 3:
				return 38 + (9*lvl);
		}
	}

	ArrayList<Integer> placesRockFall = new ArrayList<>();

	@Override
	public void onZap(Ballistica bolt) {
		ArrayList<Char> affectedChars = new ArrayList<>();
		for ( int cell : placesRockFall){
			CellEmitter.get( cell ).burst( Speck.factory( Speck.ROCK ), 3 );

			//knock doors open, press grass down
			if (Dungeon.level.map[cell] == Terrain.DOOR){
				Level.set(cell, Terrain.OPEN_DOOR);
			} else if (Dungeon.level.map[cell] == Terrain.HIGH_GRASS) {
				Level.set(cell, Terrain.GRASS);
			}
			GameScene.updateMap(cell);

			Char ch = Actor.findChar( cell );
			if (ch != null) {
				affectedChars.add(ch);
			}
		}

		Sample.INSTANCE.play( Assets.Sounds.ROCKS );
		PixelScene.shake( 2 + chargesPerCast(), 0.2f + 0.1f*chargesPerCast() );

		for ( Char ch : affectedChars ){
			int dmg = wandProc(ch, chargesPerCast(), damageRoll());

			ch.damage(dmg, this);
			if (ch.isAlive()) {
				Buff.affect(ch, Daze.class, 6f);
				switch (chargesPerCast()) {
					case 1: break;//do nothing
					case 2:
						Buff.affect(ch, Cripple.class, 4f);
						break;
					case 3:
						Buff.affect(ch, Paralysis.class, 4f);
						break;
				}
			} else if (ch == curUser) {
				Badges.validateDeathFromFriendlyMagic();
				Dungeon.fail(this);
				GLog.n(Messages.get(RockFall.class, "ondeath"));
			}
		}
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		Buff.affect(defender, ParalysisContinueTracker.class);

		int level = Math.max( 0, staff.buffedLvl() );
		/* lvl 0 - 25%
		   lvl 1 - 30%
		   lvl 2 - 33% */
		float procChance = (level/2f+1f)/(level+4f) * procChanceMultiplier(attacker);
		if (Random.Float() < procChance) {
			float powerMulti = Math.max(1f, procChance);
			Buff.affect(defender, Paralysis.class, 3 * powerMulti);
			Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
		}
	}

	@Override
	public void fx(Ballistica bolt, Callback callback) {
		//need to perform rock fall logic here so we can determine where the rocks should fall.
		placesRockFall.clear();
		PathFinder.buildDistanceMap( bolt.collisionPos, BArray.not( Dungeon.level.solid, null ), 3 );
		for (int i = 0; i < PathFinder.distance.length; i++) {
			if (PathFinder.distance[i] <= chargesPerCast()){
				placesRockFall.add(i);
			}
		}

		MagicMissile.boltFromChar( curUser.sprite.parent,
		MagicMissile.EARTH,
		curUser.sprite,
		bolt.collisionPos,
		callback );
	}

	@Override
	protected int chargesPerCast() {
		if (cursed ||
				(charger != null && charger.target != null && charger.target.buff(WildMagic.WildMagicTracker.class) != null)){
			return 1;
		}
		//consumes 30% of current charges, rounded up, with a min of 1 and a max of 3.
		return (int) GameMath.gate(1, (int)Math.ceil(curCharges*0.3f), 3);
	}

	@Override
	public String statsDesc() {
		if (levelKnown)
			return Messages.get(this, "stats_desc", chargesPerCast(), 2 * chargesPerCast()+1, min(), max());
		else
			return Messages.get(this, "stats_desc", 1, 3, min(0), max(0));
	}

	@Override
	public String upgradeStat1(int level) {
		return (2+level) + "-" + (8+4*level);
	}

	@Override
	public String upgradeStat2(int level) {
		return (4+2*level) + "-" + (18+8*level);
	}

	@Override
	public String upgradeStat3(int level) {
		return (6+3*level) + "-" + (38+12*level);
	}

	@Override
	public void staffFx(MagesStaff.StaffParticle particle) {
		particle.color(ColorMath.random(0x805500, 0x332500));
		particle.am = 1f;
		particle.setLifespan(2f);
		particle.speed.set(0, 5);
		particle.setSize( 0.5f, 2f);
		particle.shuffleXY(1f);
	}

	public static class ParalysisContinueTracker extends Buff{}
}
