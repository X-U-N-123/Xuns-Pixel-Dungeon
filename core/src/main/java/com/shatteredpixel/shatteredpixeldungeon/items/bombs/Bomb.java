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

package com.shatteredpixel.shatteredpixeldungeon.items.bombs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.Explosion;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMindVision;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfParalyticGas;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.GooBlob;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.MetalShard;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTerror;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Projecting;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MultiTool;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Bomb extends Item {
	
	{
		image = ItemSpriteSheet.BOMB;

		defaultAction = AC_LIGHTTHROW;
		usesTargeting = true;

		stackable = true;
	}

	public Fuse fuse;

	//FIXME using a static variable for this is kinda gross, should be a better way
	protected static boolean lightingFuse = false;
	private boolean grenadierThrown = false;

	private static final String AC_LIGHTTHROW = "LIGHTTHROW";
	private static final String AC_CRAFT 	= "CRAFT";

	@Override
	public boolean isSimilar(Item item) {
		return super.isSimilar(item) && this.fuse == ((Bomb) item).fuse && level() == item.level();
	}
	
	public boolean explodesDestructively(){
		return true;
	}

	protected int explosionRange(){
		return 1 + level();
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions( hero );
		actions.add ( AC_LIGHTTHROW );
		if (hero.subClass == HeroSubClass.GRENADIER && getClass() == Bomb.class) actions.add(AC_CRAFT);
		return actions;
	}

	private int min(int depth){
		return Math.round((4 + depth) * (1 + 0.25f * level()));
	}

	private int max(int depth){
		return Math.round((12 + 3 * depth) * (1 + 0.25f * level()));
	}

	@Override
	public void execute(Hero hero, String action) {

		if (action.equals(AC_LIGHTTHROW)) {
			lightingFuse = true;
			action = AC_THROW;
			grenadierThrown = hero.subClass == HeroSubClass.GRENADIER;
		} else
			lightingFuse = false;

		super.execute(hero, action);

		if (action.equals(AC_CRAFT)){
			GameScene.selectItem(crafter);
		}
	}

	protected Fuse createFuse(){
		return new Fuse();
	}

	@Override
	protected void onThrow( int cell ) {
		if (!Dungeon.level.pit[ cell ] && lightingFuse) {
			Actor.addDelayed(fuse = createFuse().ignite(this),
					curUser.subClass == HeroSubClass.GRENADIER && grenadierThrown ? 0 : 2);
		}
		super.onThrow( cell );
	}

	@Override
	public boolean doPickUp(Hero hero, int pos) {
		if (fuse != null) {
			GLog.w( Messages.get(this, "snuff_fuse") );
			fuse.snuff();
			fuse = null;
		}
		return super.doPickUp(hero, pos);
	}

	public void explode(int cell){
		//We're blowing up, so no need for a fuse anymore.
		if (fuse != null) {
			fuse.snuff();
		this.fuse = null;
		}

		Sample.INSTANCE.play( Assets.Sounds.BLAST );

		if (explodesDestructively()) {

			ArrayList<Integer> affectedCells = new ArrayList<>();
			ArrayList<Char> affectedChars = new ArrayList<>();
			
			if (Dungeon.level.heroFOV[cell]) {
				CellEmitter.center(cell).burst(BlastParticle.FACTORY, 30);
			}
			
			boolean terrainAffected = false;
			boolean[] explodable = new boolean[Dungeon.level.length()];
			BArray.not( Dungeon.level.solid, explodable);
			BArray.or( Dungeon.level.flamable, explodable, explodable);
			PathFinder.buildDistanceMap( cell, explodable, explosionRange() );
			for (int i = 0; i < PathFinder.distance.length; i++) {
				if (PathFinder.distance[i] != Integer.MAX_VALUE) {
					affectedCells.add(i);
					Char ch = Actor.findChar(i);
					if (ch != null) {
						affectedChars.add(ch);
					}
				}
			}

			for (int i : affectedCells){
				if (Dungeon.level.heroFOV[i]) {
					CellEmitter.get(i).burst(SmokeParticle.FACTORY, 4);
				}

				if (Dungeon.level.flamable[i] && (Dungeon.level.map[i] == Terrain.BARRICADE
						|| curUser.pointsInTalent(Talent.EXPLOSION_PROOF) < 3 || !grenadierThrown)) {
					Dungeon.level.destroy(i);
					GameScene.updateMap(i);
					terrainAffected = true;
				}

				//destroys items / triggers bombs caught in the blast.
				Heap heap = Dungeon.level.heaps.get(i);
				if (heap != null) {
					heap.explode(Bomb.class);
				}
			}
			boolean shockwaveProc = false;
			for (Char ch : affectedChars){

				//if they have already been killed by another bomb
				if(!ch.isAlive() || ch.buff(Explosion.Holyexplosionimmune.class) != null){
					continue;
				}

				if (grenadierThrown && curUser.hasTalent(Talent.SHOCKWAVE) && ch.alignment != Char.Alignment.ALLY
						&& curUser.buff(ShockwaveCooldown.class) == null){
					int strength = 1 + curUser.pointsInTalent(Talent.SHOCKWAVE);
					if (Dungeon.level.distance(ch.pos, cell) == 0){
						Ballistica trajectory = new Ballistica(curUser.pos, ch.pos, Ballistica.WONT_STOP);
						trajectory = new Ballistica(ch.pos, trajectory.collisionPos, Ballistica.PROJECTILE);

						WandOfBlastWave.throwChar(ch, trajectory, strength, false, true, this);
					} else if (Dungeon.level.distance(ch.pos, cell) == 1){
						Ballistica trajectory = new Ballistica(ch.pos, 2 * ch.pos - cell, Ballistica.PROJECTILE);

						WandOfBlastWave.throwChar(ch, trajectory, strength / 2, false, true, this);
					}
					shockwaveProc = true;
				}

				int dmg = Random.NormalIntRange(min(Dungeon.scalingDepth()), max(Dungeon.scalingDepth()));
				if (curUser.subClass != HeroSubClass.GRENADIER || ch.alignment == Char.Alignment.ALLY
						|| !grenadierThrown)
					dmg -= ch.drRoll();

				if (curUser.hasTalent(Talent.EXPLOSION_PROOF) && grenadierThrown
						&& ch.alignment == Char.Alignment.ALLY)
					dmg /= 3;

				MultiTool tool = curUser.belongings.getItem(MultiTool.class);
				if (tool == null && curUser.belongings.weapon() instanceof MultiTool)
					tool = (MultiTool) curUser.belongings.weapon();

				if (tool != null && tool.enchantment != null && ch.alignment != Char.Alignment.ALLY){
					if (Random.Float() < curUser.pointsInTalent(Talent.MAGICAL_EXPLOSION) * 0.3f)
						dmg = tool.enchantment.proc(tool, curUser, ch, dmg);

					if (tool.enchantment instanceof Projecting)
						dmg += Math.round(dmg * curUser.pointsInTalent(Talent.MAGICAL_EXPLOSION) / 15f);
				}

				if (dmg > 0) {
					ch.damage(dmg, this);
				}
				
				if (ch == Dungeon.hero && !ch.isAlive()) {
					if (this instanceof ConjuredBomb){
						Badges.validateDeathFromFriendlyMagic();
					}
					GLog.n(Messages.get(this, "ondeath"));
					Dungeon.fail(this);
				}
			}
			
			if (terrainAffected) {
				Dungeon.observe();
			}
			if (shockwaveProc) Buff.prolong(curUser, ShockwaveCooldown.class, 50);
		}
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}
	
	@Override
	public Item random() {
		return Random.Int(4) == 0 ? new DoubleBomb() : this;
	}

	@Override
	public ItemSprite.Glowing glowing() {
		return fuse != null ? new ItemSprite.Glowing( 0xFF0000, 0.6f) : null;
	}

	@Override
	public int value() {
		return 15 * quantity;
	}
	
	@Override
	public String desc() {
		int depth = Dungeon.hero == null ? 1 : Dungeon.scalingDepth();
		String desc = Messages.get(this, "desc", min(depth), max(depth));
		if (fuse == null) {
			return desc + "\n\n" + Messages.get(this, "desc_fuse");
		} else {
			return desc + "\n\n" + Messages.get(this, "desc_burning");
		}
	}

	private static final String FUSE = "fuse";
	private static final String GRENADIER_THROWN = "grenadier";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put( FUSE, fuse );
		bundle.put( GRENADIER_THROWN, grenadierThrown );
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (bundle.contains( FUSE ))
			Actor.add( fuse = ((Fuse)bundle.get(FUSE)).ignite(this) );
		grenadierThrown = bundle.getBoolean(GRENADIER_THROWN);
	}

	//used to track the death from friendly magic badge, if an explosion was conjured by magic
	public static class ConjuredBomb extends Bomb{
		@Override
		public void explode(int cell) {
			boolean isGrenadier = false;
			if (Dungeon.hero.subClass == HeroSubClass.GRENADIER) {
				Dungeon.hero.subClass = HeroSubClass.NONE;
				isGrenadier = true;
			}
			super.explode(cell);
			if (isGrenadier) Dungeon.hero.subClass = HeroSubClass.GRENADIER;
		}
	}

	public static class Fuse extends Actor{

		{
			actPriority = BLOB_PRIO+1; //after hero, before other actors
		}

		protected Bomb bomb;

		public Fuse ignite(Bomb bomb){
			this.bomb = bomb;
			return this;
		}

		@Override
		protected boolean act() {

			//something caused our bomb to explode early, or be defused. Do nothing.
			if (bomb.fuse != this){
				snuff();
				return true;
			}

			//look for our bomb, remove it from its heap, and blow it up.
			for (Heap heap : Dungeon.level.heaps.valueList()) {
				if (heap.items.contains(bomb)) {

					trigger(heap);
					return true;
				}
			}

			//can't find our bomb, something must have removed it, do nothing.
			bomb.fuse = null;
			snuff();
			return true;
		}

		protected void trigger(Heap heap){
			heap.remove(bomb);
			Catalog.countUse(bomb.getClass());
			bomb.explode(heap.pos);
			snuff();
		}

		public boolean freeze(){
			bomb.fuse = null;
			snuff();
			return true;
		}

		public void snuff(){
			Actor.remove( this );
		}
	}


	public static class DoubleBomb extends Bomb{

		{
			image = ItemSpriteSheet.DBL_BOMB;
			stackable = false;
		}

		@Override
		public boolean doPickUp(Hero hero, int pos) {
			Bomb bomb = new Bomb();
			bomb.quantity(2);
			return bomb.doPickUp(hero, pos);
		}
	}
	
	public static class EnhanceBomb extends Recipe {
		
		public static final LinkedHashMap<Class<?extends Item>, Class<?extends Bomb>> validIngredients = new LinkedHashMap<>();
		static {
			validIngredients.put(PotionOfFrost.class,           FrostBomb.class);
			validIngredients.put(ScrollOfMirrorImage.class,     WoollyBomb.class);
			
			validIngredients.put(PotionOfLiquidFlame.class,     Firebomb.class);
			validIngredients.put(ScrollOfRage.class,            Noisemaker.class);

			validIngredients.put(ScrollOfTerror.class,          AdrenalineBomb.class);
			validIngredients.put(PotionOfToxicGas.class,        StenchBomb.class);
			validIngredients.put(ScrollOfRecharging.class,      FlashBangBomb.class);
			validIngredients.put(ScrollOfTeleportation.class,   WarpBomb.class);

			validIngredients.put(PotionOfMindVision.class,      PhantomBomb.class);
			validIngredients.put(PotionOfHealing.class,         RegrowthBomb.class);
			validIngredients.put(ScrollOfRemoveCurse.class,     HolyBomb.class);
			validIngredients.put(PotionOfParalyticGas.class,    TimeBomb.class);
			
			validIngredients.put(GooBlob.class,                 ArcaneBomb.class);
			validIngredients.put(MetalShard.class,              ShrapnelBomb.class);
		}
		
		private static final HashMap<Class<?extends Bomb>, Integer> bombCosts = new HashMap<>();
		static {
			bombCosts.put(FrostBomb.class,      0);
			bombCosts.put(WoollyBomb.class,     0);
			
			bombCosts.put(Firebomb.class,       1);
			bombCosts.put(Noisemaker.class,     1);

			bombCosts.put(AdrenalineBomb.class, 2);
			bombCosts.put(StenchBomb.class,     2);
			bombCosts.put(FlashBangBomb.class,  2);
			bombCosts.put(WarpBomb.class,       2);

			bombCosts.put(RegrowthBomb.class,   3);
			bombCosts.put(HolyBomb.class,       3);

			bombCosts.put(TimeBomb.class,       4);
			bombCosts.put(PhantomBomb.class,    4);
			
			bombCosts.put(ArcaneBomb.class,     6);
			bombCosts.put(ShrapnelBomb.class,   6);
		}
		
		@Override
		public boolean testIngredients(ArrayList<Item> ingredients) {
			boolean bomb = false;
			boolean ingredient = false;
			
			for (Item i : ingredients){
				if (!i.isIdentified()) return false;
				if (i.getClass().equals(Bomb.class)){
					bomb = true;
				} else if (validIngredients.containsKey(i.getClass())){
					ingredient = true;
				}
			}
			
			return bomb && ingredient;
		}
		
		@Override
		public int cost(ArrayList<Item> ingredients) {
			for (Item i : ingredients){
				if (validIngredients.containsKey(i.getClass())){
					return (bombCosts.get(validIngredients.get(i.getClass())));
				}
			}
			return 0;
		}
		
		@Override
		public Item brew(ArrayList<Item> ingredients) {
			Item result = null;
			
			for (Item i : ingredients){
				i.quantity(i.quantity()-1);
				if (validIngredients.containsKey(i.getClass())){
					result = Reflection.newInstance(validIngredients.get(i.getClass()));
				}
			}

			if (result instanceof ArcaneBomb){
				Catalog.countUse(GooBlob.class);
			} else if (result instanceof ShrapnelBomb){
				Catalog.countUse(MetalShard.class);
			}

			return result;
		}
		
		@Override
		public Item sampleOutput(ArrayList<Item> ingredients) {
			for (Item i : ingredients){
				if (validIngredients.containsKey(i.getClass())){
					return Reflection.newInstance(validIngredients.get(i.getClass()));
				}
			}
			return null;
		}
	}

	public boolean grenadierThrown() {
		return grenadierThrown;
	}

	private final WndBag.ItemSelector crafter = new WndBag.ItemSelector(){

		@Override
		public String textPrompt() {
			return Messages.get(this, "ingredient");
		}

		@Override
		public boolean itemSelectable(Item item) {
			return EnhanceBomb.validIngredients.containsKey(item.getClass())
					&& item.isIdentified();
		}

		@Override
		public void onSelect(Item item) {
			if (item == null) return;

			Class<? extends Bomb> bombCls = EnhanceBomb.validIngredients.get(item.getClass());

			int cost = EnhanceBomb.bombCosts.get(bombCls);
			if (cost <= Dungeon.energy){
				detach(curUser.belongings.backpack);
				item.detach(curUser.belongings.backpack);
				Dungeon.energy -= cost;

				curUser.sprite.operate(curUser.pos);
				curUser.spendAndNext(1);

				Bomb b = Reflection.newInstance(bombCls);
				if (!b.collect()) Dungeon.level.drop(b, curUser.pos).sprite.drop();

			} else GLog.w(Messages.get(this, "no_enough_energy"));
		}
	};

	public static class ShockwaveCooldown extends FlavourBuff{
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0.6f, 0f, 0f); }
		public float iconFadePercent() { return Math.max(0, visualcooldown() / 50); }
	}
	public static class BallisticaCalcTracker extends FlavourBuff{
		public int icon() { return BuffIndicator.TIME; }
		public void extend(){spend(TIME_TO_THROW);}
	}
}
