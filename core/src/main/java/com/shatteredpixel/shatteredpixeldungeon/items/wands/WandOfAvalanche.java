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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BrokenArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.WildMagic;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class WandOfAvalanche extends DamageWand {

	{
		image = ItemSpriteSheet.WAND_AVALANCHE;

		//only used for targeting, actual projectile logic is Ballistica.STOP_SOLID | Ballistica.IGNORE_SOFT_SOLID
		collisionProperties = Ballistica.WONT_STOP;
	}

	//2/4/6 base damage with 1/2/3 scaling based on charges used
	public int min(int lvl){
		return (2+lvl) * chargesPerCast();
	}

	//8/18/40 base damage with 4/8/12 scaling based on charges used
	public int max(int lvl){
		switch (chargesPerCast()){
			case 1: default:
				return 8 + 4*lvl;
			case 2:
				return 18 + 8*lvl;
			case 3:
				return 40 + 12*lvl;
		}
	}

	ConeAOE cone;

	@Override
	public void onZap(Ballistica bolt) {
		ArrayList<Char> affectedChars = new ArrayList<>();
		for( int cell : cone.cells ){

			//knock doors open, close opened doors
			if (Dungeon.level.map[cell] == Terrain.DOOR){
				Level.set(cell, Terrain.OPEN_DOOR);
			} else if (Dungeon.level.map[cell] == Terrain.OPEN_DOOR) {
				Level.set(cell, Terrain.DOOR);
			}
			GameScene.updateMap(cell);

			Char ch = Actor.findChar( cell );
			if (ch != null) {
				affectedChars.add(ch);
			}
		}

		/*if wand was shot right at a wall
		if (cone.cells.isEmpty()){
			adjacentCells.add(bolt.sourcePos);
		}*/

		for ( Char ch : affectedChars ){
			wandProc(ch, chargesPerCast());
			ch.damage(damageRoll(), this);
			if (ch.isAlive()) {
				Buff.affect(ch, BrokenArmor.class, 4f);
				switch (chargesPerCast()) {
					case 1: break;//do nothing
					case 2:
						Buff.affect(ch, Cripple.class, 4f);
						break;
					case 3:
						Buff.affect(ch, Paralysis.class, 4f);
						break;
				}
			}
		}
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		long level = Math.max( 0, staff.buffedLvl() );

		// lvl 0 - 20%
		// lvl 1 - 25%
		// lvl 2 - 29%
		float procChance = (level/2f+1f)/(level+5f) * procChanceMultiplier(attacker);
		if (Random.Float() < procChance) {
			float powerMulti = Math.max(1f, procChance);
			Buff.affect(defender, Paralysis.class, 2 * powerMulti);
			Buff.detach(defender, Paralysis.ParalysisResist.class);
			Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
		}
	}

	@Override
	public void fx(Ballistica bolt, Callback callback) {

		final Ballistica shot;

		//The direction of the aim only matters if it goes outside the map
		//So we try to aim in the cardinal direction that has the most space
		int x = bolt.collisionPos % Dungeon.level.width();
		int y = bolt.collisionPos / Dungeon.level.width();

		if (Math.max(x, Dungeon.level.width()-x) >= Math.max(y, Dungeon.level.height()-y)){
			if (x > Dungeon.level.width()/2){
				shot = new Ballistica(bolt.collisionPos, bolt.collisionPos - 1, Ballistica.WONT_STOP);
			} else {
				shot = new Ballistica(bolt.collisionPos, bolt.collisionPos + 1, Ballistica.WONT_STOP);
			}
		} else {
			if (y > Dungeon.level.height()/2){
				shot = new Ballistica(bolt.collisionPos, bolt.collisionPos - Dungeon.level.width(), Ballistica.WONT_STOP);
			} else {
				shot = new Ballistica(bolt.collisionPos, bolt.collisionPos + Dungeon.level.width(), Ballistica.WONT_STOP);
			}
		}

		// 2/3/4 distance
		int maxDist = 1 + chargesPerCast();

		cone = new ConeAOE( shot,
				maxDist,
				360,
				Ballistica.STOP_TARGET | Ballistica.STOP_SOLID | Ballistica.IGNORE_SOFT_SOLID);

		for (int cell : cone.cells){
			CellEmitter.get( cell - Dungeon.level.width() ).start(Speck.factory(Speck.ROCK), 0.07f, 10);
		}

		Ballistica longestRay = null;
		for (Ballistica ray : cone.outerRays){
			if (longestRay == null || ray.dist > longestRay.dist){
				longestRay = ray;
			}
			((MagicMissile)curUser.sprite.parent.recycle( MagicMissile.class )).reset(
			MagicMissile.EARTH_CONE,
			curUser.sprite,
			ray.path.get(ray.dist),
			null
			);
		}

		//final zap at half distance of the longest ray, for timing of the actual wand effect
		MagicMissile.boltFromChar( curUser.sprite.parent,
		MagicMissile.EARTH_CONE,
		curUser.sprite,
		longestRay.path.get(longestRay.dist/2),
		callback );
		Sample.INSTANCE.play( Assets.Sounds.ROCKS);
		GLog.i("0");
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
			return Messages.get(this, "stats_desc", chargesPerCast(), min(), max());
		else
			return Messages.get(this, "stats_desc", chargesPerCast(), min(0), max(0));
	}

	@Override
	public String upgradeStat1(int level) {
		return (2+level) + "-" + 8+4*level;
	}

	@Override
	public String upgradeStat2(int level) {
		return (4+2*level) + "-" + 18+8*level;
	}

	@Override
	public String upgradeStat3(int level) {
		return (6+3*level) + "-" + 40+12*level;
	}

	@Override
	public void staffFx(MagesStaff.StaffParticle particle) {
		particle.color(0x888800);
		particle.am = 1f;
		particle.setLifespan(0.75f);
		particle.acc.set(0, -6);
		particle.setSize( 1.25f, 3f);
		particle.shuffleXY( 0.55f );
		float dst = Random.Float(11f);
		particle.x -= dst;
		particle.y += dst;
	}

}
