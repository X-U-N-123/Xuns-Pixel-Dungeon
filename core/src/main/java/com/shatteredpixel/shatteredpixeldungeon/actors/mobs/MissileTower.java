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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Sleep;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon.Enchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileTowerSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class MissileTower extends Mob {

	{
		spriteClass = MissileTowerSprite.class;

		EXP = 0;

		properties.add(Property.INORGANIC);
		properties.add(Property.IMMOVABLE);
		properties.add(Property.MECHANICAL);

		useParry = true;

		WANDERING = new Wandering();
		state = WANDERING;

		viewDistance = 12;

		resistances.add(Grim.class);
		immunities.add(Sleep.class);
	}

	protected MissileWeapon mis;

	public MissileTower() {
		super();
		
		HP = HT = 15 + Dungeon.depth * 5;
		defenseSkill = 4 + Dungeon.depth;
	}

	public void createWeapon( boolean useDecks ){
		if (useDecks) {
			mis = (MissileWeapon) Generator.random(Generator.Category.MISSILE);
		} else {
			mis = (MissileWeapon) Generator.randomUsingDefaults(Generator.Category.MISSILE);
		}
		mis.cursed = false;
		mis.enchant( Enchantment.random() );
		mis.quantity(mis.quantity() + 1);
		mis.inTower = true;
	}
	
	private static final String MISSILE = "missile";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put(MISSILE, mis);
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		mis = (MissileWeapon) bundle.get(MISSILE);
	}
	
	@Override
	public int damageRoll() {
		return mis.damageRoll(this);
	}
	
	@Override
	public int attackSkill( Char target ) {
		return (int)((9 + Dungeon.depth) * mis.accuracyFactor( this, target )
				* (Dungeon.level.distance(pos, target.pos) - 1) / 4);
	}
	
	@Override
	public float attackDelay() {
		return super.attackDelay()* mis.delayFactor( this );
	}

	//cannot move
	@Override
	protected boolean getCloser(int target) {
		return false;
	}

	@Override
	protected boolean getFurther(int target) {
		return false;
	}

	@Override
	protected boolean canAttack( Char enemy ) {
		return new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE).collisionPos == enemy.pos;
	}
	
	@Override
	public int attackProc( Char enemy, int damage ) {
		damage = super.attackProc( enemy, damage );
		damage = mis.proc( this, enemy, damage );
		if (!enemy.isAlive() && enemy == Dungeon.hero){
			Dungeon.fail(this);
			GLog.n( Messages.capitalize(Messages.get(Char.class, "kill", name())) );
		}
		return damage;
	}
	
	@Override
	public void die( Object cause ) {
		Dungeon.level.drop(mis, pos ).sprite.drop();
		mis.inTower = false;
		super.die( cause );
	}

	@Override
	public Notes.Landmark landmark() {
		return Notes.Landmark.MIS_TOWER;
	}

	@Override
	public void destroy() {
		if (landmark() != null) {
			Notes.remove( landmark() );
		}
		super.destroy();
	}

	@Override
	public float spawningWeight() {
		return 0f;
	}

	@Override
	public boolean reset() {
		return true;
	}

	@Override
	public String description() {
		String desc = Messages.get(this, "desc");
		if (mis != null){
			desc += "\n\n" + Messages.get(this, "desc_weapon", mis.name());
		}
		return desc;
	}

	@Override
	public CharSprite sprite() {// changes the icon in the mob info window
		MissileTowerSprite sprite = (MissileTowerSprite) super.sprite();

		if (state == HUNTING)   sprite.ready();
		else                    sprite.idle();

		return sprite;
	}

	private class Wandering extends Mob.Wandering {

		@Override
		public boolean act( boolean enemyInFOV, boolean justAlerted ) {
			if (enemyInFOV) return noticeEnemy();
			else            return continueWandering();
		}

		@Override
		protected boolean noticeEnemy(){
			enemySeen = true;

			notice();
			alerted = true;
			state = HUNTING;
			target = enemy.pos;

			spend(1f);

			if (alignment != Char.Alignment.ALLY) {
				GLog.n(Messages.get(MissileTower.class, "load"));

				Buff.affect(Dungeon.hero, TalismanOfForesight.CharAwareness.class, 1)
						.charID = MissileTower.this.id();
				Dungeon.observe();
			}
			if (Dungeon.level.heroFOV[pos]) Sample.INSTANCE.play(Assets.Sounds.UNLOCK);
			((MissileTowerSprite)sprite).load(enemy.pos);

			return true;
		}
	}

	public MissileWeapon mis(){
		return mis;
	}
}