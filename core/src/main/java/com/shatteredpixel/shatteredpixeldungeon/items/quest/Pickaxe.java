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

package com.shatteredpixel.shatteredpixeldungeon.items.quest;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bee;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Crab;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Scorpio;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.StonePier;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Swarm;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.MiningLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

import java.util.ArrayList;

public class Pickaxe extends MeleeWeapon {

	public static final String AC_MINE = "MINE";
	
	{
		image = ItemSpriteSheet.PICKAXE;

		levelKnown = true;
		
		unique = true;
		bones = false;

		tier = 2;

		defaultAction = AC_MINE;
	}

	@Override
	public int STRReq(int lvl) {
		return super.STRReq(lvl) + 2; //tier 3 strength requirement with tier 2 damage stats
	}

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add(AC_MINE);
		if (Dungeon.level instanceof MiningLevel){
			actions.remove(AC_DROP);
			actions.remove(AC_THROW);
		}
		return actions;
	}

	@Override
	public boolean keptThroughLostInventory() {
		//pickaxe is always kept when it's needed for the mining level
		return super.keptThroughLostInventory() || Dungeon.level instanceof MiningLevel;
	}

	@Override
	public void execute( Hero hero, String action ) {

		super.execute(hero, action);

		if (action.equals(AC_MINE)){
			GameScene.selectCell(new CellSelector.Listener() {
				@Override public String prompt() {
					return Messages.get(this, "mine");
				}

				@Override public void onSelect(Integer cell) {
					if (cell == null)return;
					if (Dungeon.level.distance(cell, curUser.pos) > 1 || !Dungeon.level.heroFOV[cell]) {
						GLog.w(Messages.get(this, "reach"));
						return;
					}
					if (Dungeon.level.map[cell] == Terrain.REGION_DECO || Dungeon.level.map[cell] == Terrain.REGION_DECO_ALT){
						if (0<Dungeon.depth && Dungeon.depth<=5){//Sewer
							if (Dungeon.level.map[cell] == Terrain.REGION_DECO_ALT){
								Level.set(cell, Terrain.EMPTY_SP);
							} else if (Dungeon.level.map[cell] == Terrain.REGION_DECO){
								Level.set(cell, Terrain.WATER);
								Splash.at(cell, 0xFF507B5D, 10);
							}
						}
						if (5<Dungeon.depth && Dungeon.depth<=10){//Prison
							if (Dungeon.level.map[cell] == Terrain.REGION_DECO_ALT){
								Level.set(cell, Terrain.CHASM);
							} else if (Dungeon.level.map[cell] == Terrain.REGION_DECO){
								Level.set(cell, Terrain.EMPTY);
							}
						}
						if (10<Dungeon.depth && Dungeon.depth<=15){//Cave
							if (Dungeon.level.map[cell] == Terrain.REGION_DECO_ALT){
								Level.set(cell, Terrain.EMPTY_SP);
							} else if (Dungeon.level.map[cell] == Terrain.REGION_DECO){
								Level.set(cell, Terrain.EMPTY);
							}
						}
						if (15<Dungeon.depth && Dungeon.depth<=20){//City
							GLog.w(Messages.get(this, "hard"));
							return;
						}
						if (20<Dungeon.depth && Dungeon.depth<=25){//Hall
							Level.set(cell, Terrain.EMPTY);
						}
						GameScene.updateMap(cell);
						Dungeon.hero.sprite.turnTo( Dungeon.hero.pos, cell);
						Dungeon.hero.sprite.zap(cell);
						Sample.INSTANCE.play( Assets.Sounds.MINE );
						hero.spendConstant(Actor.TICK);
					} else {
						GLog.w(Messages.get(this, "hard"));
						return;
					}

					hero.next();
				}
			});
			defaultAction = AC_MINE;
		}

		if (action.equals(AC_ABILITY)){
			defaultAction = AC_ABILITY;
		}
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

		hero.sprite.attack(enemy.pos, new Callback() {
			@Override
			public void call() {
				int damageBoost = 0;
				if (Char.hasProp(enemy, Char.Property.INORGANIC)
						|| enemy instanceof Swarm
						|| enemy instanceof Bee
						|| enemy instanceof Crab
						|| enemy instanceof Spinner
						|| enemy instanceof Scorpio
						|| enemy instanceof StonePier) {
					//+(8+2*lvl) damage, equivalent to +100% damage
					damageBoost = augment.damageFactor(8 + 2*buffedLvl());
				}
				beforeAbilityUsed(hero, enemy);
				AttackIndicator.target(enemy);
				if (hero.attack(enemy, 1, damageBoost, Char.INFINITE_ACCURACY)) {
					if (enemy.isAlive()) {
						Buff.affect(enemy, Vulnerable.class, 3f);
					} else {
						onAbilityKill(hero, enemy);
					}
					Sample.INSTANCE.play(Assets.Sounds.HIT_STRONG);
				}
				Invisibility.dispel();
				hero.spendAndNext(hero.attackDelay());
				afterAbilityUsed(hero);
			}
		});
	}

	@Override
	public String abilityInfo() {
		int dmgBoost = 8 + 2*buffedLvl();
		return Messages.get(this, "ability_desc", augment.damageFactor(min()+dmgBoost), augment.damageFactor(max()+dmgBoost));
	}

	public String upgradeAbilityStat(int level){
		int dmgBoost = 8 + 2*level;
		return augment.damageFactor(min(level)+dmgBoost) + "-" + augment.damageFactor(max(level)+dmgBoost);
	}

}
