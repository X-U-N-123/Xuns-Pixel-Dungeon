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

package com.shatteredpixel.shatteredpixeldungeon.items.armor;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Momentum;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.engineer.ForceField;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.ShadowClone;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.AuraOfProtection;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.BodyForm;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.HolyWard;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.LifeLinkSpell;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.PrismaticImage;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.Transmuting;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SmokeParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.LiquidMetal;
import com.shatteredpixel.shatteredpixeldungeon.items.MetalPart;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.curses.AntiEntropy;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.curses.Bulk;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.curses.Corrosion;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.curses.Displacement;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.curses.Dizziness;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.curses.Metabolism;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.curses.Multiplicity;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.curses.Overgrowth;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.curses.Stench;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Affection;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.AntiMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Brimstone;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Camouflage;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Entanglement;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Flow;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Freezingglyph;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Obfuscation;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Potential;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Repulsion;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Stone;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Swiftness;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Thorns;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Vengeance;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Viscosity;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfArcana;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ParchmentScrap;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ShardOfOblivion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MultiTool;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Arrays;

public class Armor extends EquipableItem {

	protected static final String AC_DETACH       = "DETACH";
	protected static final String AC_SMELT        = "smelt";
	
	public enum Augment {
		EVASION (2f , -1f),
		DEFENSE (-2f, 1f),
		NONE	(0f   ,  0f);
		
		private final float evasionFactor;
		private final float defenceFactor;
		
		Augment(float eva, float df){
			evasionFactor = eva;
			defenceFactor = df;
		}
		
		public int evasionFactor(int level){
			return Math.round((2 + level) * evasionFactor);
		}
		
		public int defenseFactor(int level){
			return Math.round((2 + level) * defenceFactor);
		}
	}

	public enum Modification {
		WEAKNESS_ENHANCE,
		EXPLOSIVE,
		CONDUCTIVE,
		DEFLECTION,
		EXOSKELETON;

		public int partCost(){
			switch (this){
				case WEAKNESS_ENHANCE:return 4;
				case EXPLOSIVE:       return 2;
				case CONDUCTIVE:      return 5;
				case DEFLECTION:      return 5;
				case EXOSKELETON:     return 10;
				default:              return 0;
			}
		}

		public int maxDurability(){
			switch (this){
				case EXPLOSIVE:       return 1;
				case CONDUCTIVE:      return 150;
				case DEFLECTION:return 15;
				case EXOSKELETON:     return 30;
				case WEAKNESS_ENHANCE:
				default:              return 25;
			}
		}

		public String title(){return Messages.get(Modification.class, toString());}
		public String desc() {return Messages.get(Modification.class, this + "_desc");}

		public boolean craftsman(){
			return this == DEFLECTION || this == EXOSKELETON;
		}
	}
	
	public Augment augment = Augment.NONE;
	
	public Glyph glyph;
	public boolean glyphHardened = false;
	public boolean curseInfusionBonus = false;
	public boolean masteryPotionBonus = false;
	public Modification modify = null;
	public int modDurability = 0;
	public ConductiveLoot loot = null;
	
	protected BrokenSeal seal;
	
	public int tier;
	
	private static final int USES_TO_ID = 10;
	private float usesLeftToID = USES_TO_ID;
	private float availableUsesToID = USES_TO_ID/2f;
	
	public Armor( int tier ) {
		this.tier = tier;
	}
	
	private static final String USES_LEFT_TO_ID = "uses_left_to_id";
	private static final String AVAILABLE_USES  = "available_uses";
	private static final String GLYPH			= "glyph";
	private static final String GLYPH_HARDENED	= "glyph_hardened";
	private static final String CURSE_INFUSION_BONUS = "curse_infusion_bonus";
	private static final String MASTERY_POTION_BONUS = "mastery_potion_bonus";
	private static final String SEAL            = "seal";
	private static final String AUGMENT			= "augment";
	private static final String MODIFY          = "modify";
	private static final String DURABILITY		= "durability";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( USES_LEFT_TO_ID, usesLeftToID );
		bundle.put( AVAILABLE_USES, availableUsesToID );
		bundle.put( GLYPH, glyph );
		bundle.put( GLYPH_HARDENED, glyphHardened );
		bundle.put( CURSE_INFUSION_BONUS, curseInfusionBonus );
		bundle.put( MASTERY_POTION_BONUS, masteryPotionBonus );
		bundle.put( SEAL, seal);
		bundle.put( AUGMENT, augment);
		if (modify != null) bundle.put( MODIFY, modify);
		bundle.put( DURABILITY, modDurability);
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle(bundle);
		usesLeftToID = bundle.getInt( USES_LEFT_TO_ID );
		availableUsesToID = bundle.getInt( AVAILABLE_USES );
		inscribe((Glyph) bundle.get(GLYPH));
		glyphHardened = bundle.getBoolean(GLYPH_HARDENED);
		curseInfusionBonus = bundle.getBoolean( CURSE_INFUSION_BONUS );
		masteryPotionBonus = bundle.getBoolean( MASTERY_POTION_BONUS );
		seal = (BrokenSeal)bundle.get(SEAL);
		modDurability = bundle.getInt(DURABILITY);
		
		augment = bundle.getEnum(AUGMENT, Augment.class);
		if (bundle.contains(MODIFY)) modify = bundle.getEnum(MODIFY, Modification.class);
	}

	@Override
	public void reset() {
		super.reset();
		usesLeftToID = USES_TO_ID;
		availableUsesToID = USES_TO_ID/2f;
		//armor can be kept in bones between runs, the seal cannot.
		seal = null;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		if (seal != null) actions.add(AC_DETACH);
		else if (hero.hasTalent(Talent.APART_ANYTHING) && hero.heroClass != HeroClass.ENGINEER
				&& !(this instanceof ClassArmor)
				&& !isEquipped(hero) && cursedKnown && !cursed && !hasCurseGlyph())
			actions.add(AC_SMELT);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {

		super.execute(hero, action);

		if (action.equals(AC_DETACH) && seal != null){
			BrokenSeal detaching = detachSeal();
			GLog.i( Messages.get(Armor.class, "detach_seal") );
			hero.sprite.operate(hero.pos);
			if (!detaching.collect()){
				Dungeon.level.drop(detaching, hero.pos);
			}
			updateQuickslot();
		}
		if (action.equals(AC_SMELT)){
			LiquidMetal metal = new LiquidMetal();
			int quantity = (int)Math.pow(2, level()) * (tier + 1) * 3;
			if (glyph != null) quantity = Math.round(quantity * 1.5f);
			if (hero.pointsInTalent(Talent.APART_ANYTHING) >= 2) quantity = Math.round(quantity * 1.67f);

			metal.quantity(quantity);
			if (!metal.collect()) Dungeon.level.drop(metal, hero.pos).sprite.drop();

			detach(hero.belongings.backpack);
			hero.sprite.operate(hero.pos);
			Sample.INSTANCE.play(Assets.Sounds.EVOKE);
		}
	}

	@Override
	public boolean collect(Bag container) {
		if(super.collect(container)){
			if (Dungeon.hero != null && Dungeon.hero.isAlive() && isIdentified() && glyph != null){
				Catalog.setSeen(glyph.getClass());
				Statistics.itemTypesDiscovered.add(glyph.getClass());
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Item identify(boolean byHero) {
		if (glyph != null && byHero && Dungeon.hero != null && Dungeon.hero.isAlive()){
			Catalog.setSeen(glyph.getClass());
			Statistics.itemTypesDiscovered.add(glyph.getClass());
		}
		return super.identify(byHero);
	}

	public void setIDReady(){
		usesLeftToID = -1;
	}

	public boolean readyToIdentify(){
		return !isIdentified() && usesLeftToID <= 0;
	}

	@Override
	public boolean doEquip( Hero hero ) {

		// 15/25% chance
		if (hero.heroClass != HeroClass.CLERIC && hero.hasTalent(Talent.HOLY_INTUITION)
				&& cursed && !cursedKnown
				&& Random.Int(20) < 1 + 2*hero.pointsInTalent(Talent.HOLY_INTUITION)){
			cursedKnown = true;
			GLog.p(Messages.get(this, "curse_detected"));
			return false;
		}

		detach(hero.belongings.backpack);

		Armor oldArmor = hero.belongings.armor;
		if (hero.belongings.armor == null || hero.belongings.armor.doUnequip( hero, true, false )) {
			
			hero.belongings.armor = this;
			
			cursedKnown = true;
			if (cursed) {
				equipCursed( hero );
				GLog.n( Messages.get(Armor.class, "equip_cursed") );
			}
			
			((HeroSprite)hero.sprite).updateArmor();
			activate(hero);
			Talent.onItemEquipped(hero, this);
			hero.spend( timeToEquip( hero ) );

			if (Dungeon.hero.heroClass == HeroClass.WARRIOR && checkSeal() == null){
				BrokenSeal seal = oldArmor != null ? oldArmor.checkSeal() : null;
				if (seal != null && (!cursed || (seal.getGlyph() != null && seal.getGlyph().curse()))){

					GameScene.show(new WndOptions(new ItemSprite(ItemSpriteSheet.SEAL),
							Messages.titleCase(seal.title()),
							Messages.get(Armor.class, "seal_transfer"),
							Messages.get(Armor.class, "seal_transfer_yes"),
							Messages.get(Armor.class, "seal_transfer_no")){
						@Override
						protected void onSelect(int index) {
							super.onSelect(index);
							if (index == 0){
								seal.affixToArmor(Armor.this, oldArmor);
								updateQuickslot();
							}
							super.hide();
						}

						@Override
						public void hide() {
							//do nothing, must press button
						}
					});
				} else {
					hero.next();
				}
			} else {
				hero.next();
			}
			return true;
			
		} else {
			
			collect( hero.belongings.backpack );
			return false;
			
		}
	}

	@Override
	public void activate(Char ch) {
		if (seal != null) Buff.affect(ch, BrokenSeal.WarriorShield.class).setArmor(this);
		
		if (loot != null){
			if (loot.target != null) loot.detach();
			loot = null;
		}
		loot = new ConductiveLoot();
		loot.attachTo(ch);
	}

	public void affixSeal(BrokenSeal seal){
		this.seal = seal;
		if (seal.level() > 0){
			//doesn't trigger upgrading logic such as affecting curses/glyphs
			int newLevel = trueLevel()+1;
			level(newLevel);
			Badges.validateItemLevelAquired(this);
		}
		if (seal.getGlyph() != null){
			inscribe(seal.getGlyph());
		}
		if (isEquipped(Dungeon.hero)){
			Buff.affect(Dungeon.hero, BrokenSeal.WarriorShield.class).setArmor(this);
		}
	}

	public BrokenSeal detachSeal(){
		if (seal != null){

			if (isEquipped(Dungeon.hero)) {
				BrokenSeal.WarriorShield sealBuff = Dungeon.hero.buff(BrokenSeal.WarriorShield.class);
				if (sealBuff != null) sealBuff.setArmor(null);
			}

			BrokenSeal detaching = seal;
			seal = null;

			if (detaching.level() > 0){
				degrade();
			}
			if (Dungeon.hero.hasTalent(Talent.INTACT_SEAL)){
				detaching.image = ItemSpriteSheet.INTACT_SEAL;
			}
			if (detaching.canTransferGlyph()){
				detaching.setGlyph(glyph);
				inscribe(null);
			} else {
				detaching.setGlyph(null);
			}
			return detaching;
		} else {
			return null;
		}
	}

	public BrokenSeal checkSeal(){
		return seal;
	}

	@Override
	public boolean doUnequip( Hero hero, boolean collect, boolean single ) {
		if (super.doUnequip( hero, collect, single )) {

			hero.belongings.armor = null;
			((HeroSprite)hero.sprite).updateArmor();

			BrokenSeal.WarriorShield sealBuff = hero.buff(BrokenSeal.WarriorShield.class);
			if (sealBuff != null) sealBuff.setArmor(null);

			if (loot != null) {
				if (loot.target != null) loot.detach();
				loot = null;
			}

			return true;

		} else {

			return false;

		}
	}
	
	@Override
	public boolean isEquipped( Hero hero ) {
		return hero != null && hero.belongings.armor() == this;
	}

	public final int DRMax(){
		return DRMax(buffedLvl());
	}

	public int DRMax(int lvl){
		if (Dungeon.isChallenged(Challenges.NO_ARMOR)){
			return 1 + tier + lvl + augment.defenseFactor(lvl);
		}

		int max = tier * (2 + lvl) + augment.defenseFactor(lvl);
		if (lvl > max){
			return ((lvl - max)+1)/2;
		} else {
			return max;
		}
	}

	public final int DRMin(){
		return DRMin(buffedLvl());
	}

	public int DRMin(int lvl){
		if (Dungeon.isChallenged(Challenges.NO_ARMOR)){
			lvl = 0;
		}

		int max = DRMax(lvl);
		if (modify == Modification.WEAKNESS_ENHANCE)
			lvl += Math.round((max - lvl) * 0.4f);
		if (Dungeon.hero != null){
			MultiTool tool = Dungeon.hero.belongings.getItem(MultiTool.class);
			if (tool != null && tool.armorModify == Modification.WEAKNESS_ENHANCE
					&& Dungeon.hero.pointsInTalent(Talent.MULTI_MODIFY) >= 2) {
				lvl += Math.round((max - lvl) * 0.4f);
			}
		}
		return Math.min(lvl, max);
	}
	
	public float evasionFactor( Char owner, float evasion ){
		
		if (hasGlyph(Stone.class, owner) && !Stone.testingEvasion()){
			return 0;
		}
		
		if (owner instanceof Hero){
			int aEnc = STRReq() - ((Hero) owner).STR();
			if (aEnc > 0) evasion /= Math.pow(1.5, aEnc);
			
			Momentum momentum = owner.buff(Momentum.class);
			if (momentum != null){
				evasion += momentum.evasionBonus(((Hero) owner).lvl, Math.max(0, -aEnc));
			}
			if (modify != null && ((Hero) owner).hasTalent(Talent.FAVORITE_WORK))
				evasion *= 1 + ((Hero) owner).pointsInTalent(Talent.FAVORITE_WORK) / 12f;
		}

		if (modify == Modification.DEFLECTION) evasion *= 1.5f;
		MultiTool tool = Dungeon.hero.belongings.getItem(MultiTool.class);
		if (tool != null && tool.armorModify == Modification.DEFLECTION
				&& Dungeon.hero.pointsInTalent(Talent.MULTI_MODIFY) >= 2){
			evasion *= 1.5f;
		}
		
		return evasion + augment.evasionFactor(buffedLvl());
	}
	
	public float speedFactor( Char owner, float speed ){
		
		if (owner instanceof Hero) {
			int aEnc = STRReq() - ((Hero) owner).STR();
			if (aEnc > 0) speed /= Math.pow(1.2, aEnc);
		}

		if (modify == Modification.EXOSKELETON) speed *= 1.25f;
		MultiTool tool = Dungeon.hero.belongings.getItem(MultiTool.class);
		if (tool != null && tool.armorModify == Modification.EXOSKELETON
				&& Dungeon.hero.pointsInTalent(Talent.MULTI_MODIFY) >= 2){
			speed *= 1.25f;
		}
		
		return speed;
		
	}
	
	@Override
	public int level() {
		int level = super.level();
		//TODO warrior's seal upgrade should probably be considered here too
		// instead of being part of true level
		if (curseInfusionBonus) level += 1 + level/6;
		return level;
	}
	
	@Override
	public Item upgrade() {
		return upgrade( false );
	}
	
	public Item upgrade( boolean inscribe ) {

		if (inscribe){
			if (glyph == null){
				inscribe( Glyph.random() );
			}
		} else if (glyph != null) {
			//chance to lose harden buff is 10/20/40/80/100% when upgrading from +6/7/8/9/10
			if (glyphHardened) {
				if (level() >= 6 && Random.Float(10) < Math.pow(2, level()-6) && glyph != null){
					glyphHardened = false;
				}

			//chance to remove curse is a static 33%
			} else if (hasCurseGlyph()){
				if (Random.Int(3) == 0) inscribe(null);

			//otherwise chance to lose glyph is 10/20/40/80/100% when upgrading from +4/5/6/7/8
			} else {

				//the chance from +4/5, and then +6 can be set to 0% with metamorphed runic transference
				int lossChanceStart = 4;
				if (Dungeon.hero != null && Dungeon.hero.heroClass != HeroClass.WARRIOR && Dungeon.hero.hasTalent(Talent.RUNIC_TRANSFERENCE)){
					lossChanceStart += 1+Dungeon.hero.pointsInTalent(Talent.RUNIC_TRANSFERENCE);
				}

				if (level() >= lossChanceStart && Random.Float(10) < Math.pow(2, level()-4)) {
					inscribe(null);
				}
			}
		}
		
		cursed = false;

		if (seal != null && seal.level() == 0)
			seal.upgrade();

		return super.upgrade();
	}
	
	public int proc( Char attacker, Char defender, int damage ) {

		if (defender.buff(MagicImmune.class) == null) {
			Glyph trinityGlyph = null;
			//only when it's the hero or a char that uses the hero's armor
			if (Dungeon.hero.buff(BodyForm.BodyFormBuff.class) != null
					&& (defender == Dungeon.hero || defender instanceof PrismaticImage || defender instanceof ShadowClone.ShadowAlly)){
				trinityGlyph = Dungeon.hero.buff(BodyForm.BodyFormBuff.class).glyph();
				if (glyph != null && trinityGlyph != null && trinityGlyph.getClass() == glyph.getClass()){
					trinityGlyph = null;
				}
			}

			if (defender instanceof Hero && isEquipped((Hero) defender)
					&& defender.buff(HolyWard.HolyArmBuff.class) != null){
				if (glyph != null &&
						(((Hero) defender).subClass == HeroSubClass.PALADIN || hasCurseGlyph())){
					damage = glyph.proc( this, attacker, defender, damage );
				}
				if (trinityGlyph != null){
					damage = trinityGlyph.proc( this, attacker, defender, damage );
				}
				int blocking = ((Hero) defender).subClass == HeroSubClass.PALADIN ? 3 : 1;
				damage -= Math.round(blocking * Glyph.genericProcChanceMultiplier(defender));

			} else {
				if (glyph != null) {
					damage = glyph.proc(this, attacker, defender, damage);
				}
				if (trinityGlyph != null){
					damage = trinityGlyph.proc( this, attacker, defender, damage );
				}
				//so that this effect procs for allies using this armor via aura of protection
				if (defender.alignment == Dungeon.hero.alignment
						&& Dungeon.hero.buff(AuraOfProtection.AuraBuff.class) != null
						&& (Dungeon.level.distance(defender.pos, Dungeon.hero.pos) <= 2 || defender.buff(LifeLinkSpell.LifeLinkSpellBuff.class) != null)
						&& Dungeon.hero.buff(HolyWard.HolyArmBuff.class) != null) {
					int blocking = Dungeon.hero.subClass == HeroSubClass.PALADIN ? 3 : 1;
					damage -= Math.round(blocking * Glyph.genericProcChanceMultiplier(defender));
				}
			}
			damage = Math.max(damage, 0);
		}

		if (defender == Dungeon.hero){
			if (!levelKnown) {
				float uses = Math.min( availableUsesToID, Talent.itemIDSpeedFactor(Dungeon.hero, this) );
				availableUsesToID -= uses;
				usesLeftToID -= uses;
				if (usesLeftToID <= 0) {
					if (ShardOfOblivion.passiveIDDisabled()){
						if (usesLeftToID > -1){
							GLog.p(Messages.get(ShardOfOblivion.class, "identify_ready"), name());
						}
						setIDReady();
					} else {
						identify();
						GLog.p(Messages.get(Armor.class, "identify"));
						Badges.validateItemLevelAquired(this);
					}
				}
			}
		}

		if (modify == Modification.EXPLOSIVE){
			explosiveProc(defender);
		}
		MultiTool tool = Dungeon.hero.belongings.getItem(MultiTool.class);
		if (tool != null && tool.armorModify == Modification.EXPLOSIVE
				&& Dungeon.hero.pointsInTalent(Talent.MULTI_MODIFY) >= 2){
			explosiveProc(defender);
		}
		if (modify == Modification.EXPLOSIVE
				|| modify == Modification.WEAKNESS_ENHANCE
				|| modify == Modification.EXOSKELETON)
			decreaseModDurability();

		if (tool != null && Dungeon.hero.pointsInTalent(Talent.MULTI_MODIFY) >= 2
				&& (modify == Modification.EXPLOSIVE
				|| modify == Modification.WEAKNESS_ENHANCE
				|| modify == Modification.EXOSKELETON)){
			tool.decreaseArmorModDura();
		}
		
		return damage;
	}

	private void explosiveProc(Char defender) {
		for (int i : PathFinder.NEIGHBOURS8) {
			Char ch = Actor.findChar(defender.pos + i);
			if (ch != null && ch.alignment != defender.alignment){
				ch.damage(tier * 2 + Random.IntRange(0, level() * 5), Explosive.class);
				//trace a ballistica to our target (which will also extend past them)
				Ballistica trajectory = new Ballistica(defender.pos, ch.pos, Ballistica.STOP_TARGET);
				//trim it to just be the part that goes past them
				trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size() - 1), Ballistica.PROJECTILE);
				//knock them back along that ballistica
				WandOfBlastWave.throwChar(ch, trajectory, 2, false, true, defender);
			}
		}
		if (Dungeon.level.heroFOV[defender.pos]){
			Sample.INSTANCE.play(Assets.Sounds.BLAST);
			CellEmitter.center(defender.pos).burst(BlastParticle.FACTORY, 20);
			CellEmitter.get(defender.pos).burst(SmokeParticle.FACTORY, 5);
		}
	}

	@Override
	public void onHeroGainExp(float levelPercent, Hero hero) {
		levelPercent *= Talent.itemIDSpeedFactor(hero, this);
		if (!levelKnown && isEquipped(hero) && availableUsesToID <= USES_TO_ID/2f) {
			//gains enough uses to ID over 0.5 levels
			availableUsesToID = Math.min(USES_TO_ID/2f, availableUsesToID + levelPercent * USES_TO_ID);
		}
	}
	
	@Override
	public String name() {
		if (isEquipped(Dungeon.hero) && !hasCurseGlyph() && Dungeon.hero.buff(HolyWard.HolyArmBuff.class) != null
			&& (Dungeon.hero.subClass != HeroSubClass.PALADIN || glyph == null)){
				return Messages.get(HolyWard.class, "glyph_name", super.name());
			} else {
				return glyph != null && (cursedKnown || !glyph.curse()) ? glyph.name( super.name() ) : super.name();

		}
	}
	
	@Override
	public String info() {
		String info = super.info();
		
		if (levelKnown) {

			info += "\n\n" + Messages.get(Armor.class, "curr_absorb", tier, DRMin(), DRMax(), STRReq());
			
			if (Dungeon.hero != null && STRReq() > Dungeon.hero.STR()) {
				info += " " + Messages.get(Armor.class, "too_heavy");
			}
		} else {
			info += "\n\n" + Messages.get(Armor.class, "avg_absorb", tier, DRMin(0), DRMax(0), STRReq(0));

			if (Dungeon.hero != null && STRReq(0) > Dungeon.hero.STR()) {
				info += " " + Messages.get(Armor.class, "probably_too_heavy");
			}
		}

		switch (augment) {
			case EVASION:
				info += " " + Messages.get(Armor.class, "evasion");
				break;
			case DEFENSE:
				info += " " + Messages.get(Armor.class, "defense");
				break;
			case NONE:
		}

		if (isEquipped(Dungeon.hero) && !hasCurseGlyph() && Dungeon.hero.buff(HolyWard.HolyArmBuff.class) != null
				&& (Dungeon.hero.subClass != HeroSubClass.PALADIN || glyph == null)){
			info += "\n\n" + Messages.capitalize(Messages.get(Armor.class, "inscribed", Messages.get(HolyWard.class, "glyph_name", Messages.get(Glyph.class, "glyph"))));
			info += " " + Messages.get(HolyWard.class, "glyph_desc");
		} else if (glyph != null  && (cursedKnown || !glyph.curse())) {
			info += "\n\n" +  Messages.capitalize(Messages.get(Armor.class, "inscribed", glyph.name()));
			if (glyphHardened) info += " " + Messages.get(Armor.class, "glyph_hardened");
			info += " " + glyph.desc();
		} else if (glyphHardened){
			info += "\n\n" + Messages.get(Armor.class, "hardened_no_glyph");
		}
		
		if (cursed && isEquipped( Dungeon.hero )) {
			info += "\n\n" + Messages.get(Armor.class, "cursed_worn");
		} else if (cursedKnown && cursed) {
			info += "\n\n" + Messages.get(Armor.class, "cursed");
		} else if (!isIdentified() && cursedKnown){
			if (glyph != null && glyph.curse()) {
				info += "\n\n" + Messages.get(Armor.class, "weak_cursed");
			} else {
				info += "\n\n" + Messages.get(Armor.class, "not_cursed");
			}
		}

		if (seal != null) {
			info += "\n\n" + Messages.get(Armor.class, "seal_attached", seal.maxShield(tier, level()));
		}

		if (modify != null){
			info += "\n\n" + Messages.get(this, "has_modify", modify.title(), modDurability) + modify.desc();
		}
		
		return info;
	}

	@Override
	public Emitter emitter() {
		if (seal == null) return super.emitter();
		Emitter emitter = new Emitter();
		emitter.pos(ItemSpriteSheet.film.width(image)/2f + 2f, ItemSpriteSheet.film.height(image)/3f);
		emitter.fillTarget = false;
		emitter.pour(Speck.factory( Speck.RED_LIGHT ), 0.6f);
		return emitter;
	}

	@Override
	public Item random() {
		//+0: 75% (3/4)
		//+1: 20% (4/20)
		//+2: 5%  (1/20)
		int n = 0;
		if (Random.Int(4) == 0) {
			n++;
			if (Random.Int(5) == 0) {
				n++;
			}
		}
		level(n);

		//we use a separate RNG here so that variance due to things like parchment scrap
		//does not affect levelgen
		Random.pushGenerator(Random.Long());

			//30% chance to be cursed
			//15% chance to be inscribed
			float effectRoll = Random.Float();
			if (effectRoll < 0.3f * ParchmentScrap.curseChanceMultiplier()) {
				inscribe(Glyph.randomCurse());
				cursed = true;
			} else if (effectRoll >= 1f - (0.15f * ParchmentScrap.enchantChanceMultiplier())){
				inscribe();
			}

		Random.popGenerator();

		return this;
	}

	public int STRReq(){
		return STRReq(level());
	}

	public int STRReq(int lvl){
		int req = STRReq(tier, lvl);
		if (masteryPotionBonus){
			req -= 2;
		}
		return req;
	}

	protected int STRReq(int tier, int lvl){
		lvl = Math.max(0, lvl);
		int baseSTR = 8 + tier * 2;
		if (modify == Modification.EXOSKELETON) baseSTR --;

		if (Dungeon.hero != null){
			MultiTool tool = Dungeon.hero.belongings.getItem(MultiTool.class);
			if (tool != null && tool.armorModify == Modification.WEAKNESS_ENHANCE
					&& Dungeon.hero.pointsInTalent(Talent.MULTI_MODIFY) >= 2) {
				baseSTR--;
			}
		}

		if (Dungeon.isChallenged(Challenges.EXERCISES)){
			//in challenge, strength req decreases at +1,+4,+9,+16,etc.
			return baseSTR - (int)Math.sqrt(lvl);
		}
		//strength req decreases at +1,+3,+6,+10,etc.
		return baseSTR - (int)(Math.sqrt(8 * lvl + 1) - 1)/2;
	}
	
	@Override
	public int value() {
		if (seal != null) return 0;

		int price = 20 * tier;
		if (hasGoodGlyph()) {
			price *= 1.5;
		}
		if (cursedKnown && (cursed || hasCurseGlyph())) {
			price /= 2;
		}
		if (levelKnown && level() > 0) {
			price *= (level() + 1);
		}
		if (price < 1) {
			price = 1;
		}
		return price;
	}

	public Armor inscribe( Glyph glyph ) {
		if (glyph == null || !glyph.curse()) curseInfusionBonus = false;
		this.glyph = glyph;
		updateQuickslot();
		//the hero needs runic transference to actually transfer, but we still attach the glyph here
		// in case they take that talent in the future
		if (seal != null){
			seal.setGlyph(glyph);
		}
		if (glyph != null && isIdentified() && Dungeon.hero != null
				&& Dungeon.hero.isAlive() && Dungeon.hero.belongings.contains(this)){
			Catalog.setSeen(glyph.getClass());
			Statistics.itemTypesDiscovered.add(glyph.getClass());
		}
		return this;
	}

	public Armor inscribe() {

		Class<? extends Glyph> oldGlyphClass = glyph != null ? glyph.getClass() : null;
		Glyph gl = Glyph.random( oldGlyphClass );

		return inscribe( gl );
	}

	public boolean hasGlyph(Class<?extends Glyph> type, Char owner) {
		if (owner.buff(MagicImmune.class) != null) {
			return false;
		} else if (glyph != null
				&& !glyph.curse()
				&& owner instanceof Hero
				&& isEquipped((Hero) owner)
				&& owner.buff(HolyWard.HolyArmBuff.class) != null
				&& ((Hero) owner).subClass != HeroSubClass.PALADIN){
			return false;
		} else if (owner.buff(BodyForm.BodyFormBuff.class) != null
				&& owner.buff(BodyForm.BodyFormBuff.class).glyph() != null
				&& owner.buff(BodyForm.BodyFormBuff.class).glyph().getClass().equals(type)){
			return true;
		} else if (glyph != null) {
			return glyph.getClass() == type;
		} else {
			return false;
		}
	}

	//these are not used to process specific glyph effects, so magic immune doesn't affect them
	public boolean hasGoodGlyph(){
		return glyph != null && !glyph.curse();
	}

	public boolean hasCurseGlyph(){
		return glyph != null && glyph.curse();
	}

	private static ItemSprite.Glowing HOLY = new ItemSprite.Glowing( 0xFFFF00 );

	@Override
	public ItemSprite.Glowing glowing() {
		if (isEquipped(Dungeon.hero) && !hasCurseGlyph() && Dungeon.hero.buff(HolyWard.HolyArmBuff.class) != null
				&& (Dungeon.hero.subClass != HeroSubClass.PALADIN || glyph == null)){
			return HOLY;
		} else {
			return glyph != null && (cursedKnown || !glyph.curse()) ? glyph.glowing() : null;
		}
	}
	
	public static abstract class Glyph implements Bundlable {
		
		public static final Class<?>[] common = new Class<?>[]{
				Obfuscation.class, Swiftness.class, Viscosity.class, Potential.class };

		public static final Class<?>[] uncommon = new Class<?>[]{
				Brimstone.class, Stone.class, Entanglement.class,
				Repulsion.class, Camouflage.class, Flow.class, Freezingglyph.class};

		public static final Class<?>[] rare = new Class<?>[]{
				Affection.class, AntiMagic.class, Thorns.class, Vengeance.class};

		public static final float[] typeChances = new float[]{
				35, //8.75% each
				45, //6.42% each
				20  //5% each
		};

		public static final Class<?>[] curses = new Class<?>[]{
				AntiEntropy.class, Corrosion.class, Displacement.class, Metabolism.class,
				Multiplicity.class, Stench.class, Overgrowth.class, Bulk.class, Dizziness.class
		};
		
		public abstract int proc( Armor armor, Char attacker, Char defender, int damage );

		protected float procChanceMultiplier( Char defender ){
			return genericProcChanceMultiplier( defender );
		}

		public static float genericProcChanceMultiplier( Char defender ){
			float multi = RingOfArcana.enchantPowerMultiplier(defender);


			if (Dungeon.hero.alignment == defender.alignment
					&& Dungeon.hero.buff(AuraOfProtection.AuraBuff.class) != null
					&& (Dungeon.level.distance(defender.pos, Dungeon.hero.pos) <= 2 || defender.buff(LifeLinkSpell.LifeLinkSpellBuff.class) != null)){
				multi += 0.25f + 0.25f*Dungeon.hero.pointsInTalent(Talent.AURA_OF_PROTECTION);
			}

			if (defender instanceof Hero){
				if (((Hero) defender).belongings.armor() != null
						&& ((Hero) defender).belongings.armor().modify == Modification.CONDUCTIVE) multi *= 2f;

				MultiTool tool = Dungeon.hero.belongings.getItem(MultiTool.class);
				if (tool != null && tool.armorModify == Modification.CONDUCTIVE
						&& Dungeon.hero.pointsInTalent(Talent.MULTI_MODIFY) >= 2){
					multi *= 2f;
				}

				if (((Hero) defender).hasTalent(Talent.BARBED_WIRE) && ((Hero) defender).heroClass != HeroClass.EXPLORER)
					multi *= 1f + 0.1f * ((Hero) defender).pointsInTalent(Talent.BARBED_WIRE);
			}

			return multi;
		}
		
		public String name() {
			if (!curse())
				return name( Messages.get(this, "glyph") );
			else
				return name( Messages.get(Item.class, "curse"));
		}
		
		public String name( String armorName ) {
			return Messages.get(this, "name", armorName);
		}

		public String desc() {
			String desc = Messages.get(this, "desc");
			if (Dungeon.isChallenged(Challenges.X_U_NS_POWER))
				desc += "\n\n" + Messages.get(Item.class, "class_name", getClass().getSimpleName());
			return desc;
		}

		public boolean curse() {
			return false;
		}
		
		@Override
		public void restoreFromBundle( Bundle bundle ) {
		}

		@Override
		public void storeInBundle( Bundle bundle ) {
		}
		
		public abstract ItemSprite.Glowing glowing();

		@SuppressWarnings("unchecked")
		public static Glyph random( Class<? extends Glyph> ... toIgnore ) {
			switch(Random.chances(typeChances)){
				case 0: default:
					return randomCommon( toIgnore );
				case 1:
					return randomUncommon( toIgnore );
				case 2:
					return randomRare( toIgnore );
			}
		}
		
		@SuppressWarnings("unchecked")
		public static Glyph randomCommon( Class<? extends Glyph> ... toIgnore ){
			ArrayList<Class<?>> glyphs = new ArrayList<>(Arrays.asList(common));
			glyphs.removeAll(Arrays.asList(toIgnore));
			if (glyphs.isEmpty()) {
				return random();
			} else {
				return (Glyph) Reflection.newInstance(Random.element(glyphs));
			}
		}
		
		@SuppressWarnings("unchecked")
		public static Glyph randomUncommon( Class<? extends Glyph> ... toIgnore ){
			ArrayList<Class<?>> glyphs = new ArrayList<>(Arrays.asList(uncommon));
			glyphs.removeAll(Arrays.asList(toIgnore));
			if (glyphs.isEmpty()) {
				return random();
			} else {
				return (Glyph) Reflection.newInstance(Random.element(glyphs));
			}
		}
		
		@SuppressWarnings("unchecked")
		public static Glyph randomRare( Class<? extends Glyph> ... toIgnore ){
			ArrayList<Class<?>> glyphs = new ArrayList<>(Arrays.asList(rare));
			glyphs.removeAll(Arrays.asList(toIgnore));
			if (glyphs.isEmpty()) {
				return random();
			} else {
				return (Glyph) Reflection.newInstance(Random.element(glyphs));
			}
		}
		
		@SuppressWarnings("unchecked")
		public static Glyph randomCurse( Class<? extends Glyph> ... toIgnore ){
			ArrayList<Class<?>> glyphs = new ArrayList<>(Arrays.asList(curses));
			glyphs.removeAll(Arrays.asList(toIgnore));
			if (glyphs.isEmpty()) {
				return random();
			} else {
				return (Glyph) Reflection.newInstance(Random.element(glyphs));
			}
		}
		
	}

	@Override
	public float weight(){
		return STRReq() / 10f;
	}

	@Override
	public float unidWeight(){
		return 0.2f * (tier + 4);
	}

	public void decreaseModDurability(){
		if (Dungeon.hero.buff(ForceField.Field.class) == null
				|| Random.Int(4) >= Dungeon.hero.pointsInTalent(Talent.REPAIR_ABILITY))
			modDurability = Math.max(modDurability - 1, 0);
		if (modDurability <= 0) modify(null);
	}

	public void modify(Modification mod){
		boolean activeRepair = modify == mod && Dungeon.hero.hasTalent(Talent.ACTIVE_REPAIR);
		modify = mod;
		if (mod == null){
			modDurability = 0;
			GLog.n(Messages.get(this, "modify_break"));
			float chance = Dungeon.hero.pointsInTalent(Talent.PART_RECYCLING) / 2f;
			while (Random.Float() < chance){
				MetalPart part = new MetalPart();
				if (!part.collect()) Dungeon.level.drop(part, Dungeon.hero.pos).sprite.drop();
				chance --;
			}
			if (Dungeon.hero.pointsInTalent(Talent.KINETIC_FRAGMENT) >= 2){
				Buff.affect(Dungeon.hero, Barrier.class).setShield(Dungeon.hero.lvl);
				Dungeon.hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(Dungeon.hero.lvl), FloatingText.SHIELDING);
			}
		} else {
			if (!activeRepair)
				modDurability = 0;

			float duraToInc = mod.maxDurability() * (1 + 0.15f * Dungeon.hero.pointsInTalent(Talent.DURABLE_MODIFIES));
			if (Dungeon.hero.pointsInTalent(Talent.ACTIVE_REPAIR) >= 2 && activeRepair) duraToInc *= 1.2f;
			modDurability += Math.round(duraToInc);

			Sample.INSTANCE.play(Assets.Sounds.UNLOCK);
			Transmuting.show(curUser, this, this);
			curUser.sprite.operate(curUser.pos);

			if (Dungeon.hero.pointsInTalent(Talent.ACTIVE_REPAIR) >= 3 && activeRepair){
				MetalPart part = new MetalPart();
				if (!part.collect()) Dungeon.level.drop(part, Dungeon.hero.pos).sprite.drop();
			}
		}
	}

	public class ConductiveLoot extends Buff {

		{
			revivePersists = true;
			actPriority = HERO_PRIO + 1;
		}

		@Override
		public boolean attachTo( Char target ) {
			if (super.attachTo( target )) {
				//if we're loading in and the hero has partially spent a turn, delay for 1 turn
				if (target instanceof Hero && Dungeon.hero == null && cooldown() == 0 && target.cooldown() > 0) {
					spend(TICK);
				}
				return true;
			}
			return false;
		}

		@Override
		public boolean act() {
			if (Armor.this.isEquipped(Dungeon.hero)
					&& Armor.this.modify == Modification.CONDUCTIVE && Armor.this.glyph != null)
				Armor.this.decreaseModDurability();

			spendConstant(1);
			return true;
		}

	}
	public static class Explosive{}
}