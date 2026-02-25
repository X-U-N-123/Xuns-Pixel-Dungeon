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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AcidRain;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Adrenaline;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArtifactRecharge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BrokenArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CounterBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.EnhancedRings;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Haste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LostInventory;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSight;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MonkEnergy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.PhysicalEmpower;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.RevealedArea;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ScrollEmpower;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.WandEmpower;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.Ratmogrify;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.duelist.Challenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.DivineSense;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.RecallInscription;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.CheckedCell;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClothArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HornOfPlenty;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfIntuition;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ShardOfOblivion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.BladeOfUnreal;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dagger;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Shovel;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ShadowCaster;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

@SuppressWarnings("SuspiciousIndentation")
public enum Talent {

	//Warrior T1
	HEARTY_MEAL(0), VETERANS_INTUITION(1), TESTED_REVIVE(2), PROVOKED_ANGER(3), IRON_WILL(4),
	//Warrior T2
	IRON_STOMACH(5), LIQUID_WILLPOWER(6), RUNIC_TRANSFERENCE(7), LETHAL_MOMENTUM(8), IMPROVISED_PROJECTILES(9),FIGHTING_BACK(10),
	//Warrior T3
	INTACT_SEAL(11, 3), STRONGMAN(12, 3), OVERWHELMING(13, 3),
	//Berserker T3
	ENDLESS_RAGE(14, 3), DEATHLESS_FURY(15, 3), ENRAGED_CATALYST(16, 3), BEAR_GRUDGES(17, 3), BLADE_OF_ANGER(18, 3),
	//Gladiator T3
	CLEAVE(19, 3), LETHAL_DEFENSE(20, 3), ENHANCED_COMBO(21, 3), REPEATED_SKILL(22, 3), FAR_STANDOFF(23, 3),
	//Guard T3
	KEEP_GUARDING(24, 3), GUARD_THE_PASS(25, 3), SHIELDING(26, 3), EMERGENCY_SHIELD(27, 3), ARMOR_SEIZING(28, 3),
	//Heroic Leap T4
	BODY_SLAM(29, 4), IMPACT_WAVE(30, 4), DOUBLE_JUMP(31, 4), SHIELDING_JUMP(32, 4),
	//Shockwave T4
	EXPANDING_WAVE(33, 4), STRIKING_WAVE(34, 4), SHOCK_FORCE(35, 4), EARTHQUAKE(36, 4),
	//Endure T4
	SUSTAINED_RETRIBUTION(37, 4), SHRUG_IT_OFF(38, 4), EVEN_THE_ODDS(39, 4), BLOOD_FOR_BLOOD(40, 4),

	//Mage T1
	EMPOWERING_MEAL(48), SCHOLARS_INTUITION(49), TESTED_HYPOTHESIS(50), LINGERING_MAGIC(51), BACKUP_BARRIER(52),
	//Mage T2
	ENERGIZING_MEAL(53), INSCRIBED_POWER(54), WAND_PRESERVATION(55), ARCANE_VISION(56), SHIELD_BATTERY(57), RESERVED_ENERGY(58),
	//Mage T3
	DESPERATE_POWER(59, 3), ALLY_WARP(60, 3), ARCANE_ARMOR(61, 3),
	//Battlemage T3
	EMPOWERED_STRIKE(62, 3), MYSTICAL_CHARGE(63, 3), EXCESS_CHARGE(64, 3), BATTLE_CHARGE(65, 3), VARIED_MAGIC(66, 3),
	//Warlock T3
	SOUL_EATER(67, 3), SOUL_SIPHON(68, 3), NECROMANCERS_MINIONS(69, 3), CLEAR_YOUR_SOUL(70, 3), MANA_EATING(71, 3),
    //Switcher T3
    SHARED_ARCANA(72, 3), SWITCH_MASTER(73, 3), RELAY_RECHARGING(74, 3), ENERGY_RECYCLING(75, 3), MYSTICAL_SWITCH(76, 3),
	//Elemental Blast T4
	BLAST_RADIUS(77, 4), ELEMENTAL_POWER(78, 4), REACTIVE_BARRIER(79, 4), RECHARGING_BLAST(80, 4),
	//Wild Magic T4
	WILD_POWER(81, 4), FIRE_EVERYTHING(82, 4), CONSERVED_MAGIC(83, 4), WILD_PUNISHMENT(84, 4),
	//Warp Beacon T4
	TELEFRAG(85, 4), REMOTE_BEACON(86, 4), LONGRANGE_WARP(87, 4), SPACE_COLLECTING(88, 4),

	//Rogue T1
	CACHED_RATIONS(96), THIEFS_INTUITION(97), TESTED_MYST(98), SUCKER_PUNCH(99), PROTECTIVE_SHADOWS(100),
	//Rogue T2
	MYSTICAL_MEAL(101), INSCRIBED_STEALTH(102), EMERGENCY_CHARGE(103), SILENT_STEPS(104), ROGUES_FORESIGHT(105), ROGUES_INSTINCT(106),
	//Rogue T3
	ENHANCED_RINGS(107, 3), LIGHT_CLOAK(108, 3), STEALTH_METABOLISM(109, 3),
	//Assassin T3
	ENHANCED_LETHALITY(110, 3), ASSASSINS_REACH(111, 3), TERRORIST_ATTACK(112, 3), CHARGE_RECYCLING(113, 3), EXTREMIST(114, 3),
	//Freerunner T3
	EVASIVE_ARMOR(115, 3), PROJECTILE_MOMENTUM(116, 3), SPEEDY_STEALTH(117, 3), ARCANE_STEP(118, 3), STRETCHING(119, 3),
	//Ninja T3
	BLADE_OF_UNREAL(120, 3), HIDDEN_IN_THE_CITY(121, 3), FLYING_LOCUST_STONE(122, 3), STEALTH_LEAP(123, 3), WEAKENING_SNEAK(124, 3),
	//Smoke Bomb T4
	HASTY_RETREAT(125, 4), BODY_REPLACEMENT(126, 4), SHADOW_STEP(127, 4), STATIC_SMOKE(128, 4),
	//Death Mark T4
	FEAR_THE_REAPER(129, 4), DEATHLY_DURABILITY(130, 4), DOUBLE_MARK(131, 4), STRONG_MARK(132, 4),
	//Shadow Clone T4
	SHADOW_BLADE(133, 4), CLONED_ARMOR(134, 4), PERFECT_COPY(135, 4), PRECISE_SHADOW(136, 4),

	//Huntress T1
	NATURES_BOUNTY(144), SURVIVALISTS_INTUITION(145), TESTED_SWIFTNESS(146), FOLLOWUP_STRIKE(147), NATURES_AID(148),
	//Huntress T2
	INVIGORATING_MEAL(149), LIQUID_NATURE(150), REJUVENATING_STEPS(151), HEIGHTENED_SENSES(152), DURABLE_PROJECTILES(153), IVY_BIND(154),
	//Huntress T3
	L_M_MASTER(155, 3), SEER_SHOT(156, 3), ORGANIC_FERTILIZER(157, 3),
	//Sniper T3
	FARSIGHT(158, 3), SHARED_ENCHANTMENT(159, 3), SHARED_UPGRADES(160, 3), SUPRESSING_MARK(161, 3), RESONANCE_FETCH(162, 3),
	//Warden T3
	DURABLE_TIPS(163, 3), BARKSKIN(164, 3), DEW_COLLECTING(165, 3), JUNGLE_GUERRILLA(166, 3), GRASSMAN(167, 3),
	//Scout T3
	STRONG_MARK_SC(168, 3), TRACKING_ARROW(169, 3), SWIFT_COURIER(170, 3), EXPEL_ENEMIES(171, 3), PIONEERING_SPIRIT(172, 3),
	//Spectral Blades T4
	FAN_OF_BLADES(173, 4), PROJECTING_BLADES(174, 4), SPIRIT_BLADES(175, 4), INSTANT_BLADES(176, 4),
	//Natures Power T4
	GROWING_POWER(177, 4), NATURES_WRATH(178, 4), WILD_MOMENTUM(179, 4), REGROWTH(180, 4),
	//Spirit Hawk T4
	EAGLE_EYE(181, 4), GO_FOR_THE_EYES(182, 4), SWIFT_SPIRIT(183, 4), STRONG_HAWK(184, 4),

	//Duelist T1
	STRENGTHENING_MEAL(192), ADVENTURERS_INTUITION(193), TESTED_CHARGE(194), PATIENT_STRIKE(195), AGGRESSIVE_BARRIER(196),
	//Duelist T2
	FOCUSED_MEAL(197), LIQUID_AGILITY(198), WEAPON_RECHARGING(199), LETHAL_HASTE(200), SWIFT_EQUIP(201), POWER_ACCUMULATION(202),
	//Duelist T3
	PRECISE_ASSAULT(203, 3), DEADLY_FOLLOWUP(204, 3), AGILE_COUNTATK(205, 3),
	//Champion T3
	VARIED_CHARGE(206, 3), TWIN_UPGRADES(207, 3), COMBINED_LETHALITY(208, 3), SKILLED_DUAL(209, 3), MARCH_FORWARD(210, 3),
	//Monk T3
	UNENCUMBERED_SPIRIT(211, 3), MONASTIC_VIGOR(212, 3), COMBINED_ENERGY(213, 3), YANG_SEEING(214, 3), YIN_GAIT(215, 3),
	//Phantom T4
	FLEXIBLE_FOOTWORK(216, 3), ENRAGED_SHADOW(217, 3), MULTIPLE_DODGE(218, 3), EIDOLON(219, 3), DIFFUSED_IMAGE(220, 3),
	//Challenge T4
	CLOSE_THE_GAP(221, 4), INVIGORATING_VICTORY(222, 4), ELIMINATION_MATCH(223, 4), BURN_BRIDGES(224, 4),
	//Elemental Strike T4
	ELEMENTAL_REACH(225, 4), STRIKING_FORCE(226, 4), DIRECTED_POWER(227, 4), RECHARGING_STRIKE(228, 4),
	//Feint T4
	FEIGNED_RETREAT(229, 4), EXPOSE_WEAKNESS(230, 4), COUNTER_ABILITY(231, 4), EVASIVE_AFTERIMAGE(232, 4),

	//Cleric T1
	SATIATED_SPELLS(240), HOLY_INTUITION(241), TESTED_HOLINESS(242), SEARING_LIGHT(243), SHIELD_OF_LIGHT(244),
	//Cleric T2
	ENLIGHTENING_MEAL(245), RECALL_INSCRIPTION(246), SUNRAY(247), DIVINE_SENSE(248), BLESS(249), ASCETICISM(250),
	//Cleric T3
	CLEANSE(251, 3), LIGHT_READING(252, 3), SHARED_CHARGE(253, 3),
	//Priest T3
	HOLY_LANCE(254, 3), HALLOWED_GROUND(255, 3), MNEMONIC_PRAYER(256, 3), EXPLOSION(257, 3), ENHANCED_RADIANCE(258, 3),
	//Paladin T3
	LAY_ON_HANDS(259, 3), AURA_OF_PROTECTION(260, 3), WALL_OF_LIGHT(261, 3), JUSTICE_STRIKE(262, 3), ENHANCED_SMITE(263, 3),
	//Preacher T3
	PUNISHMENT(264, 3), DRAPE_OF_REDEMPTION(265, 3), HOLY_TRAP(266, 3), HOLY_GHOST(267, 3), ENHANCED_BOOKPAGE(268, 3),
	//Ascended Form T4
	DIVINE_INTERVENTION(269, 4), JUDGEMENT(270, 4), FLASH(271, 4), HOLY_REGENERATION(272, 4),
	//Trinity T4
	BODY_FORM(273, 4), MIND_FORM(274, 4), SPIRIT_FORM(275, 4), MIMIC_FORM(276, 4),
	//Power of Many T4
	BEAMING_RAY(277, 4), LIFE_LINK(278, 4), STASIS(279, 4), HOLY_CHAMPION(280, 4),

	//Explorer T1
	KEEN_MEAL(288), SECRET_FORESIGHT(289), TESTED_AWARENESS(290), HOME_ADVANTAGE(291), SAFE_SURVEY(292),
	//Explorer T2
	PREPARING_MEAL(293), LIQUID_CLAIRVOYANCE(294), BARBED_WIRE(295), WINDING_PORCH(296), REKINDLED_EMBER(297), AGGRESSIVE_ROADBLOCK(298),
	//Explorer T3
	DEMOLITION(299, 3), DIG_THE_WELL(300, 3), CONVENIENT_SHOVEL(301, 3),
	//WaveChaser T3
	RIVER_EROSION(302, 3), SON_OF_SEA(303, 3), DROWNING(304, 3), LAKE_DEVELOPMENT(305, 3), UNDERCURRENT(306, 3),
	//Trapper T3
	TRAP_MASTER(307, 3), FRIENDLY_MECHANISM(308, 3), SIMPLE_STRUCTURE(309, 3), SENSITIVE_PEDAL(310, 3), LIQUID_COLLECTING(311, 3),
	//Rocksy T3
	METEROIC_IRON(312, 3), ROCK_PROTECTOR(313, 3), MIND_CONTROL(314, 3), DESTRUCTIVE_STRIKE(315, 3), METEOR_CRATER(316, 3),
	//Optical Camouflage T4
	LASTING_DISGUISE(317, 4), STRAIN_CAPACITY(318, 4), STANDBY(319, 4), ENERGY_SURPLUS(320, 4),
	//Sandstorm T4
	GLOOM_ABOVE(321, 4), SAND_FLOW(322, 4), HEART_OF_STORM(323, 4), DRIFT_SAND(324, 4),
	//Underpass T4
	EXPRESS_UNDERWAY(325, 4), MONITOR_BENEATH(326, 4), SLY_RABBIT(327, 4), GRENADE_COVER(328, 4),

    //Wraith T1
    ANCESTRAL_TRIBUTE(336), BLOOD_INTUITION(337), TESTED_ANTIMAGIC(338), BURIAL_CEREMONY(339), FLEET_BARRIER(340),
    //Wraith T2
    TEARING_MEAL(341), INSCRIBED_REGENERATION(342), BLOODLETTING(343), PSIONIC_BLAST(344), SCAPEGOAT(345), THROWN_EVIL(346),
    //Wraith T3
    VICIOUS_BETRAYAL(347, 3), CURSED_POWER(348, 3), BLURRING_BODY(349, 3),
    //Incubus T3
    LULLABY(350, 3), SLEEPWALKING(351, 3), SLEEPING_IN(352, 3), WRONG_SIDE_OF_THE_BED(353, 3), NIGHTMARE_HAUNTING(354, 3),
	//PlagueGod T3
	CORPSE_DECAY(355, 3), HOMEMADE_DRUG(356, 3), MAGICAL_VENT(357, 3), ACID_RAIN(358, 3), PLAGUE_EUCHARIST(359, 3),
	//Lifeloan T4
	HIGH_QUOTA(365, 4), EXTRA_GIFT(366, 4), BREACH_OF_TRUST(367, 4), PHILANTHROPIST(368, 4),
	//GhostWander T4
    FACE_TO_FACE_FRIGHT(373, 4), SOUL_VANISHING(374, 4), FEAR_SPREADING(375, 4), SOULFREE_GHOST(376, 4),

    //universal T4
	HEROIC_ENERGY(41, 4), //See icon() and title() for special logic for this one
	//Ratmogrify T4
	RATSISTANCE(507, 4), RATLOMACY(508, 4), RATFORCEMENTS(509, 4), ENRATGEMENT(510, 4);

	public static class ImprovisedProjectileCooldown extends FlavourBuff{
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0.15f, 0.2f, 0.5f); }
		public float iconFadePercent() { return Math.max(0, visualcooldown() / 40); }
	}
	public static class LethalMomentumTracker extends FlavourBuff{}
	public static class StrikingWaveTracker extends FlavourBuff{}
	public static class WandPreservationCounter extends CounterBuff{{revivePersists = true;}}
	public static class EmpoweredStrikeTracker extends FlavourBuff{
		//blast wave on-hit doesn't resolve instantly, so we delay detaching for it
		public boolean delayedDetach = false;
	}
	public static class ProtectiveShadowsTracker extends Buff {
		float barrierInc = 0.5f;

		@Override
		public boolean act() {
			//barrier every 2/1 turns, to a max of 3/5
			if (((Hero)target).hasTalent(PROTECTIVE_SHADOWS) && target.invisible > 0){
				Barrier barrier = Buff.affect(target, Barrier.class);
				if (barrier.shielding() < 1 + 2*((Hero)target).pointsInTalent(PROTECTIVE_SHADOWS)) {
					barrierInc += 0.5f * ((Hero) target).pointsInTalent(PROTECTIVE_SHADOWS);
				}
				if (barrierInc >= 1){
					barrierInc = 0;
					barrier.incShield(1);
				} else if (barrier.shielding() == 1 + 2*((Hero)target).pointsInTalent(PROTECTIVE_SHADOWS)){
					barrier.incShield(0); //resets barrier decay
				}
			} else {
				detach();
			}
			spend( TICK );
			return true;
		}

		private static final String BARRIER_INC = "barrier_inc";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put( BARRIER_INC, barrierInc);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			barrierInc = bundle.getFloat( BARRIER_INC );
		}
	}

	public static void waitForShield(Hero hero){
		//barrier to a max of 3/5
		if (hero.hasTalent(SAFE_SURVEY)){
			Barrier barrier = Buff.affect(hero, Barrier.class);
			if (barrier.shielding() < 1 + 2*hero.pointsInTalent(SAFE_SURVEY)) {
				barrier.incShield(1);
			} else if (barrier.shielding() == 1 + 2*hero.pointsInTalent(SAFE_SURVEY)){
				barrier.incShield(0); //resets barrier decay
			}
		}
	}

	public static class NaturesaidTracker extends Buff {
		float barrierInc = 0.5f;

		@Override
		public boolean act() {
			boolean HeroposTerr = Dungeon.level.map[Dungeon.hero.pos] == Terrain.HIGH_GRASS
					|| Dungeon.level.map[Dungeon.hero.pos] == Terrain.GRASS
					|| Dungeon.level.map[Dungeon.hero.pos] == Terrain.FURROWED_GRASS;
			//barrier every 2/1 turns, to a max of 3/5
			if (((Hero)target).hasTalent(NATURES_AID) && HeroposTerr){
				Barrier barrier = Buff.affect(target, Barrier.class);
				if (barrier.shielding() < 1 + 2*((Hero)target).pointsInTalent(NATURES_AID)) {
					barrierInc += 0.5f * ((Hero) target).pointsInTalent(NATURES_AID);
				}
				if (barrierInc >= 1){
					barrierInc = 0;
					barrier.incShield(1);
				} else if (barrier.shielding() == 1 + 2*((Hero)target).pointsInTalent(NATURES_AID)){
					barrier.incShield(0); //resets barrier decay
				}
			} else {
				detach();
			}
			spend( TICK );
			return true;
		}

		private static final String BARRIER_INC = "barrier_inc";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put( BARRIER_INC, barrierInc);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			barrierInc = bundle.getFloat( BARRIER_INC );
		}
	}
	public static class RoguesInstinctCooldown extends Buff{
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0.15f, 0.2f, 0.5f); }

		@Override
		public String iconTextDisplay() {
			return Integer.toString(CD);
		}

		private int CD = 0;

		public void decreaseCD(){
			CD --;
			if (CD <= 0) detach();
		}

		public static void setup(int cd){
			Buff.affect(Dungeon.hero, RoguesInstinctCooldown.class).CD = cd;
		}

		private static final String COOLDOWN = "cooldown";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put( COOLDOWN, CD);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			CD = bundle.getInt( COOLDOWN );
		}

		@Override
		public String desc(){
			return Messages.get(this, "desc", CD);
		}
	}
	public static class RejuvenatingStepsCooldown extends FlavourBuff{
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0f, 0.35f, 0.15f); }
		public float iconFadePercent() { return GameMath.gate(0, visualcooldown() / (15 - 5*Dungeon.hero.pointsInTalent(REJUVENATING_STEPS)), 1); }
	}
	public static class RejuvenatingStepsFurrow extends CounterBuff{{revivePersists = true;}}
	public static class SeerShotCooldown extends FlavourBuff{
		public int icon() { return target.buff(RevealedArea.class) != null ? BuffIndicator.NONE : BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0.7f, 0.4f, 0.7f); }
		public float iconFadePercent() { return Math.max(0, visualcooldown() / 20); }
	}
	public static class SpiritBladesTracker extends FlavourBuff{}
	public static class PatientStrikeTracker extends Buff {
		public int pos;
		{ type = Buff.buffType.POSITIVE; }
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0.5f, 0f, 1f); }
		@Override
		public boolean act() {
			if (pos != target.pos) {
				detach();
			} else {
				spend(TICK);
			}
			return true;
		}
		private static final String POS = "pos";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(POS, pos);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			pos = bundle.getInt(POS);
		}
	}
	public static class AggressiveBarrierCooldown extends FlavourBuff{
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0.35f, 0f, 0.7f); }
		public float iconFadePercent() { return Math.max(0, visualcooldown() / 25); }
	}
	public static class LiquidAgilEVATracker extends FlavourBuff{}
	public static class LiquidAgilACCTracker extends FlavourBuff{
		public int uses;

		{ type = buffType.POSITIVE; }
		public int icon() { return BuffIndicator.INVERT_MARK; }
		public void tintIcon(Image icon) { icon.hardlight(0.5f, 0f, 1f); }
		public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }

		private static final String USES = "uses";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(USES, uses);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			uses = bundle.getInt(USES);
		}
	}
	public static class AgileCountATKTracker extends FlavourBuff{}
	public static class LethalHasteCooldown extends FlavourBuff{
		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) { icon.hardlight(0.35f, 0f, 0.7f); }
		public float iconFadePercent() { return Math.max(0, visualcooldown() / 100); }
	}
	public static class SwiftEquipCooldown extends FlavourBuff{
		public boolean secondUse;
		public boolean hasSecondUse(){
			return secondUse;
		}

		public int icon() { return BuffIndicator.TIME; }
		public void tintIcon(Image icon) {
			if (hasSecondUse()) icon.hardlight(0.85f, 0f, 1.0f);
			else                icon.hardlight(0.35f, 0f, 0.7f);
		}
		public float iconFadePercent() { return GameMath.gate(0, visualcooldown() / 20f, 1); }

		private static final String SECOND_USE = "second_use";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(SECOND_USE, secondUse);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			secondUse = bundle.getBoolean(SECOND_USE);
		}
	}
	public static class MarchForwardTracker extends FlavourBuff{
		{ type = Buff.buffType.POSITIVE; }
		public int icon() { return BuffIndicator.MOMENTUM; }

		public void tintIcon(Image icon) {
			float a = Math.min(2/3f, step / 120f);
			icon.hardlight(1/3f + a, 0f, 1/3f + a);
		}

		public int step = 0;

		public float dmgResist(float dmg) {
			detach();
			return (float)(dmg * Math.max(0.8-0.2*Dungeon.hero.pointsInTalent(MARCH_FORWARD), 1-(step * 0.01) ));
		}

		@Override
		public String desc(){
			return Messages.get(this, "desc", dispTurns(), Math.min(20*(Dungeon.hero.pointsInTalent(MARCH_FORWARD) + 1), step));
		}
		private static final String STEP = "step";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(STEP, step);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			step = bundle.getInt(STEP);
		}
	}
	public static class DeadlyFollowupTracker extends FlavourBuff{
		public int object;
		{ type = Buff.buffType.POSITIVE; }
		public int icon() { return BuffIndicator.INVERT_MARK; }
		public void tintIcon(Image icon) { icon.hardlight(0.5f, 0f, 1f); }
		public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }
		private static final String OBJECT    = "object";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(OBJECT, object);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			object = bundle.getInt(OBJECT);
		}
	}
	public static class PreciseAssaultTracker extends FlavourBuff{
		{ type = buffType.POSITIVE; }
		public int icon() { return BuffIndicator.INVERT_MARK; }
		public void tintIcon(Image icon) { icon.hardlight(1f, 1f, 0.0f); }
		public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }
	}
	public static class VariedChargeTracker extends Buff{
		public Class weapon;

		private static final String WEAPON    = "weapon";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(WEAPON, weapon);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			weapon = bundle.getClass(WEAPON);
		}
	}

	public static class SkilleddualTracker extends FlavourBuff{
		{
			type = buffType.POSITIVE;
		}
		public int icon() { return BuffIndicator.SKILLED_DUAL; }
		private int stack = 0;
		private Weapon wep = null;

		public void hit(Weapon weapon){
			if (wep != weapon) {
				wep = weapon;
				stack = Math.min(stack +Dungeon.hero.pointsInTalent(SKILLED_DUAL), 10*Dungeon.hero.pointsInTalent(SKILLED_DUAL));
			}
		}

		public float attackBoost(){
			return stack /100f;
		}

		private static final String STACK = "stack";
		private static final String WEP = "wep";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(STACK, stack);
			bundle.put(WEP, wep);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			stack = bundle.getInt(STACK);
			wep = (Weapon) bundle.get(WEP);
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc", dispTurns(), stack, wep.name());
		}
	}

	public static void secretForesightID(Hero hero){
		if (hero.hasTalent(SECRET_FORESIGHT) && hero.buff(SecretForesightCooldown.class) == null){
			EquipableItem itemToID;

			ArrayList<EquipableItem> equipmentsToID = new ArrayList<>();
			if (hero.belongings.weapon != null && !hero.belongings.weapon.isIdentified())
				equipmentsToID.add(hero.belongings.weapon);
			if (hero.belongings.armor != null && !hero.belongings.armor.isIdentified())
				equipmentsToID.add(hero.belongings.armor);
			if (hero.belongings.ring != null && !hero.belongings.ring.isIdentified())
				equipmentsToID.add(hero.belongings.ring);
			if (hero.belongings.misc != null && !hero.belongings.misc.isIdentified())
				equipmentsToID.add(hero.belongings.misc);

			if (!equipmentsToID.isEmpty()){
				itemToID = equipmentsToID.remove(Random.Int(equipmentsToID.toArray().length));

				if (itemToID != null){
					itemToID.identify();
					GLog.p(Messages.get(Hero.class, "talent_id", itemToID.name()));
					Buff.affect(hero, SecretForesightCooldown.class, 499f);//1 turn less as this is instant
				}

			} else if (hero.pointsInTalent(SECRET_FORESIGHT) >= 2) {
				for (Item e : hero.belongings) {
					if (e instanceof EquipableItem && !e.cursedKnown)
						equipmentsToID.add((EquipableItem) e);
				}
				if (!equipmentsToID.isEmpty()){
					itemToID = equipmentsToID.remove(Random.Int(equipmentsToID.toArray().length));

					if (itemToID != null){
						itemToID.cursedKnown = true;
						GLog.p(Messages.get(Hero.class, "talent_id", itemToID.name()));
						Item.updateQuickslot();
						Buff.affect(hero, SecretForesightCooldown.class, 499f);//1 turn less as this is instant
					}
				}
			}
		}
	}
	public static class SecretForesightCooldown extends FlavourBuff{
		@Override
		public int icon() {
			return BuffIndicator.TIME;
		}
		public void tintIcon(Image icon) { icon.hardlight(0.6f, 0.6f, 0f); }
		public float iconFadePercent() { return Math.max(0, visualcooldown() / 500f); }
	}
    public static class RiverErosionTracker extends Buff {

        {
            actPriority = MOB_PRIO + 2; // to make sure it gives chill correctly
        }

        @Override
        public boolean act() {
            if (Dungeon.level.water[target.pos]){
                Buff.prolong(target, Chill.class, 0.1f + Dungeon.hero.pointsInTalent(RIVER_EROSION));
            } else detach();
            spend( 0.1f * TICK );
            return true;
        }
    }

	public static class CombinedLethalityAbilityTracker extends FlavourBuff{
		public MeleeWeapon weapon;
	}
	public static class CombinedEnergyAbilityTracker extends FlavourBuff{
		public boolean monkAbilused = false;
		public boolean wepAbilUsed = false;

		private static final String MONK_ABIL_USED  = "monk_abil_used";
		private static final String WEP_ABIL_USED   = "wep_abil_used";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(MONK_ABIL_USED, monkAbilused);
			bundle.put(WEP_ABIL_USED, wepAbilUsed);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			monkAbilused = bundle.getBoolean(MONK_ABIL_USED);
			wepAbilUsed = bundle.getBoolean(WEP_ABIL_USED);
		}
	}

	public static int MonkViewBoost(){
		MonkEnergy Energy = Dungeon.hero.buff(MonkEnergy.class);
		if (Energy != null && Dungeon.hero.hasTalent(YANG_SEEING)) {
			return Math.min((Energy.Getenergy() / (5 - Dungeon.hero.pointsInTalent(YANG_SEEING))),
			2 * Dungeon.hero.pointsInTalent(YANG_SEEING));
		}
		return 0;
	}

	public static class CounterAbilityTacker extends FlavourBuff{}
	public static class SatiatedSpellsTracker extends Buff{
		@Override
		public int icon() {
			return BuffIndicator.SPELL_FOOD;
		}
	}
	//used for metamorphed searing light
	public static class SearingLightCooldown extends FlavourBuff{
		@Override
		public int icon() {
			return BuffIndicator.TIME;
		}
		public void tintIcon(Image icon) { icon.hardlight(0f, 0f, 1f); }
		public float iconFadePercent() { return Math.max(0, visualcooldown() / 20); }
	}

	int icon;
	int maxPoints;

	// tiers 1/2/3/4 start at levels 2/7/13/20
	public static int[] tierLevelThresholds = new int[]{0, 2, 7, 13, 21, 31};

	Talent( int icon ){
		this(icon, 2);
	}

	Talent( int icon, int maxPoints ){
		this.icon = icon;
		this.maxPoints = maxPoints;
	}

	public int icon(){
		if (this == HEROIC_ENERGY){
			HeroClass cls = Dungeon.hero != null ? Dungeon.hero.heroClass : GamesInProgress.selectedClass;
            if (Ratmogrify.useRatroicEnergy || cls == null) return 511;
            return cls.ordinal() * 48 + 41;
            //currently every hero has 48 talent slot, with 41 of them used
		} else return icon;
	}

	public int maxPoints(){
		return maxPoints;
	}

	public String title(){
		if (this == HEROIC_ENERGY && Ratmogrify.useRatroicEnergy){
			return Messages.get(this, name() + ".rat_title");
		}
		return Messages.get(this, name() + ".title");
	}

	public final String desc(){
		return desc(false);
	}

	public String desc(boolean metamorphed){
		if (metamorphed){
			String metaDesc = Messages.get(this, name() + ".meta_desc");
			if (!metaDesc.equals(Messages.NO_TEXT_FOUND)){
				return Messages.get(this, name() + ".desc") + "\n\n" + metaDesc;
			}
		}
		return Messages.get(this, name() + ".desc");
	}

	public static void onItemIdentified( Hero hero, Item item ){
		if (hero.hasTalent(TESTED_REVIVE)){
			//heal for 2/3 HP
			hero.HP = Math.min(hero.HP + 1 + hero.pointsInTalent(TESTED_REVIVE), hero.HT);
			if (hero.sprite != null) {
				hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(1 + hero.pointsInTalent(TESTED_REVIVE)), FloatingText.HEALING);
			}
		}
		if (hero.hasTalent(TESTED_HYPOTHESIS)){
			//2/3 turns of wand recharging
			Buff.affect(hero, Recharging.class, 1f + hero.pointsInTalent(TESTED_HYPOTHESIS));
			ScrollOfRecharging.charge(hero);
		}
		if (hero.hasTalent(TESTED_MYST)){
			//2/3 turns of artifact recharging
			ArtifactRecharge recharge = Buff.affect(hero, ArtifactRecharge.class)
					.extend(1f + hero.pointsInTalent(TESTED_MYST));
			recharge.ignoreHornOfPlenty = false;
			recharge.ignoreHolyTome = false;
		}
		if (hero.hasTalent(TESTED_SWIFTNESS)){
			//effectively 2/3 turns of haste
			Buff.affect(hero, Haste.class, 1.67f + hero.pointsInTalent(TESTED_SWIFTNESS));
		}
		if (hero.hasTalent(TESTED_CHARGE)){
			if (hero.heroClass == (HeroClass.DUELIST)){
				MeleeWeapon.Charger charger = Buff.affect(hero, MeleeWeapon.Charger.class);
				charger.gainCharge(0.1f + 0.2f*hero.pointsInTalent(TESTED_CHARGE));
				ScrollOfRecharging.charge(hero);
			} else {
				//Empower next melee attack by 2 / 3 points
				Buff.affect(hero, PhysicalEmpower.class).set(1 + hero.pointsInTalent(TESTED_CHARGE), 1);
			}
		}
		if (hero.hasTalent(TESTED_HOLINESS)){
			if (hero.heroClass == HeroClass.CLERIC) {
				//0.4/0.6 point of tome charge
				HolyTome tome = hero.belongings.getItem(HolyTome.class);
				if (tome != null) {
					tome.directCharge( 0.2f + 0.2f*(hero.pointsInTalent(TESTED_HOLINESS)));
					ScrollOfRecharging.charge(hero);
				}
			} else {
				//1/2 turns of recharging
				ArtifactRecharge buff = Buff.affect( hero, ArtifactRecharge.class);
				if (buff.left() < 1 + (hero.pointsInTalent(TESTED_HOLINESS))){
					ArtifactRecharge recharge = Buff.affect(hero, ArtifactRecharge.class)
					.set(hero.pointsInTalent(TESTED_HOLINESS));
					recharge.ignoreHornOfPlenty = false;
					recharge.ignoreHolyTome = false;
				}
				Buff.prolong( hero, Recharging.class, hero.pointsInTalent(ENLIGHTENING_MEAL));
				ScrollOfRecharging.charge( hero );
				SpellSprite.show(hero, SpellSprite.CHARGE);
			}
		}
		if (hero.hasTalent(TESTED_AWARENESS)){
			Buff.affect(hero, MagicalSight.class, 1 + hero.pointsInTalent(TESTED_AWARENESS));
            Dungeon.observe();
            Dungeon.hero.checkVisibleMobs();
		}
        if (hero.hasTalent(TESTED_ANTIMAGIC)){
            Buff.affect(hero, MagicImmune.class, 1 + hero.pointsInTalent(TESTED_ANTIMAGIC));
        }
	}

	public static void onTalentUpgraded( Hero hero, Talent talent ){
		//for metamorphosis
		if (talent == IRON_WILL && hero.heroClass != HeroClass.WARRIOR){
			Buff.affect(hero, BrokenSeal.WarriorShield.class);
		}

		if (talent == VETERANS_INTUITION && hero.pointsInTalent(VETERANS_INTUITION) == 2){
			if (hero.belongings.armor() != null && !ShardOfOblivion.passiveIDDisabled())  {
				hero.belongings.armor.identify();
			}
		}
		if (talent == THIEFS_INTUITION && hero.pointsInTalent(THIEFS_INTUITION) == 2){
			if (hero.belongings.ring instanceof Ring && !ShardOfOblivion.passiveIDDisabled()) {
				hero.belongings.ring.identify();
			}
			if (hero.belongings.misc instanceof Ring && !ShardOfOblivion.passiveIDDisabled()) {
				hero.belongings.misc.identify();
			}
			for (Item item : Dungeon.hero.belongings){
				if (item instanceof Ring){
					((Ring) item).setKnown();
				}
			}
		}
		if (talent == THIEFS_INTUITION && hero.pointsInTalent(THIEFS_INTUITION) == 1){
			if (hero.belongings.ring instanceof Ring) hero.belongings.ring.setKnown();
			if (hero.belongings.misc instanceof Ring) ((Ring) hero.belongings.misc).setKnown();
		}
		if (talent == ADVENTURERS_INTUITION && hero.pointsInTalent(ADVENTURERS_INTUITION) == 2){
			if (hero.belongings.weapon() != null && !ShardOfOblivion.passiveIDDisabled()){
				hero.belongings.weapon().identify();
			}
		}

		if (talent == PROTECTIVE_SHADOWS && hero.invisible > 0){
			Buff.affect(hero, ProtectiveShadowsTracker.class);
		}
		boolean HeroposTerr = Dungeon.level.map[Dungeon.hero.pos] == Terrain.HIGH_GRASS
				|| Dungeon.level.map[Dungeon.hero.pos] == Terrain.GRASS
				|| Dungeon.level.map[Dungeon.hero.pos] == Terrain.FURROWED_GRASS;
		if (talent == NATURES_AID && HeroposTerr){
			Buff.affect(hero, NaturesaidTracker.class);
		}

		if (talent == LIGHT_CLOAK && hero.heroClass == HeroClass.ROGUE){
			for (Item item : Dungeon.hero.belongings.backpack){
				if (item instanceof CloakOfShadows){
					if (!hero.belongings.lostInventory() || item.keptThroughLostInventory()) {
						((CloakOfShadows) item).activate(Dungeon.hero);
					}
				}
			}
		}

		if (talent == HEIGHTENED_SENSES || talent == FARSIGHT || talent == DIVINE_SENSE || talent == YANG_SEEING || talent == WINDING_PORCH){
			Dungeon.observe();
			Dungeon.hero.checkVisibleMobs();
		}

        BrokenSeal seal = Dungeon.hero.belongings.getItem(BrokenSeal.class);
        if (seal != null && talent == INTACT_SEAL) seal.image = ItemSpriteSheet.INTACT_SEAL;

		if (talent == INTACT_SEAL || talent == TWIN_UPGRADES || talent == DESPERATE_POWER || talent == SHARED_ARCANA
				|| talent == STRONGMAN || talent == DURABLE_PROJECTILES || talent == ARCANE_STEP){
			Item.updateQuickslot();
		}

		if (talent == UNENCUMBERED_SPIRIT && hero.pointsInTalent(talent) == 3){
			Item toGive = new ClothArmor().identify();
			if (!toGive.collect()){
				Dungeon.level.drop(toGive, hero.pos).sprite.drop();
			}
			toGive = new Dagger().identify();
			if (!toGive.collect()){
				Dungeon.level.drop(toGive, hero.pos).sprite.drop();
			}
		}

		if (talent == LIGHT_READING && hero.heroClass == HeroClass.CLERIC){
			for (Item item : Dungeon.hero.belongings.backpack){
				if (item instanceof HolyTome){
					if (!hero.belongings.lostInventory() || item.keptThroughLostInventory()) {
						((HolyTome) item).activate(Dungeon.hero);
					}
				}
			}
		}

		if (talent == BLADE_OF_UNREAL){
			if (hero.pointsInTalent(talent) == 1){
	            Item toGive = new BladeOfUnreal().identify();
				if (!toGive.collect()){
					Dungeon.level.drop(toGive, hero.pos).sprite.drop();
				}
			} else if (hero.pointsInTalent(talent) == 3) {
				AttackIndicator.updateState();
			}
		}

		if (talent == STEALTH_LEAP){
			ActionIndicator.refresh();
		}

		//if we happen to have spirit form applied with a ring of might
		if (talent == SPIRIT_FORM){
			Dungeon.hero.updateHT(false);
		}

		if (Dungeon.level.map[hero.pos] == Terrain.EMBERS && talent == REKINDLED_EMBER && hero.buff(Burning.class) != null)
			Buff.detach(hero, Burning.class);

        if (talent == RIVER_EROSION || (talent == UNDERCURRENT && hero.pointsInTalent(talent) >= 3)){
            for (Mob m : Dungeon.level.mobs.toArray(new Mob[0])) {
                if (m.alignment == Char.Alignment.ENEMY && m.state != m.SLEEPING
                && (!m.isFlying() || Dungeon.hero.pointsInTalent(Talent.UNDERCURRENT) >= 3)){
                    Buff.affect(m, Talent.RiverErosionTracker.class).act();
                }
            }
        }

		if (talent == HOMEMADE_DRUG && hero.pointsInTalent(talent) >= 2)
			Buff.affect(hero, HomemadeDrugTracker.class);

		if (talent == ACID_RAIN) {
			AcidRain acid = Buff.affect(hero, AcidRain.class);
			if (hero.pointsInTalent(talent) == 1) ActionIndicator.setAction(acid);
		}
	}

	public static class CachedRationsDropped extends CounterBuff{{revivePersists = true;}}
	public static class NatureBerriesDropped extends CounterBuff{{revivePersists = true;}}
	public static class HomemadeDrugTracker extends Buff {
		@Override
		public boolean act(){
			if (((Hero)target).pointsInTalent(HOMEMADE_DRUG) < 2){ //to prevent player get free boost from elixir of amnesia
				detach();
			} else if (target.buff(Poison.class) == null){
				Poison p = Buff.affect(target, Poison.class);
			}
			spend(TICK);
			return true;
		}
		@Override
		public void spend(float time){
			super.spend(time);
		}
	}

	public static void onFoodEaten( Hero hero, float foodVal, Item foodSource ){
		if (hero.hasTalent(HEARTY_MEAL)){
			//3/5 HP healed, when hero is below 75% health
			if (hero.HP/(float)hero.HT <= 0.75f) {
				int healing = 1 + 2 * hero.pointsInTalent(HEARTY_MEAL);
				hero.HP = Math.min(hero.HP + healing, hero.HT);
				hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(healing), FloatingText.HEALING);

			}
		}
		if (hero.hasTalent(IRON_STOMACH)){
			if (hero.cooldown() > 0) {
				Buff.affect(hero, WarriorFoodImmunity.class, hero.cooldown());
			}
		}

		if (hero.hasTalent(EMPOWERING_MEAL)){
			//2/3 bonus wand damage for next 3 zaps
			Buff.affect( hero, WandEmpower.class).set(1 + hero.pointsInTalent(EMPOWERING_MEAL), 3);
			ScrollOfRecharging.charge( hero );
		}
		if (hero.hasTalent(ENERGIZING_MEAL)){
			//5/8 turns of recharging
			Buff.prolong( hero, Recharging.class, 2 + 3*(hero.pointsInTalent(ENERGIZING_MEAL)) );
			ScrollOfRecharging.charge( hero );
			SpellSprite.show(hero, SpellSprite.CHARGE);
		}

		if (hero.hasTalent(MYSTICAL_MEAL)){
			//3/5 turns of recharging
			ArtifactRecharge buff = Buff.affect( hero, ArtifactRecharge.class);
			if (buff.left() < 1 + 2*(hero.pointsInTalent(MYSTICAL_MEAL))){
				//2/3 turns of artifact recharging
				ArtifactRecharge recharge = Buff.affect(hero, ArtifactRecharge.class)
				.set(1f + 2*hero.pointsInTalent(MYSTICAL_MEAL));
				recharge.ignoreHornOfPlenty = foodSource instanceof HornOfPlenty;
				recharge.ignoreHolyTome = false;
			}
			ScrollOfRecharging.charge( hero );
			SpellSprite.show(hero, SpellSprite.CHARGE, 0, 1, 1);
		}

		if (hero.hasTalent(INVIGORATING_MEAL)){
			//effectively 2/3 turns of haste
			Buff.prolong( hero, Haste.class, 1.67f+hero.pointsInTalent(INVIGORATING_MEAL));
		}

		if (hero.hasTalent(STRENGTHENING_MEAL)){
			//3 bonus physical damage for next 2/3 attacks
			Buff.affect( hero, PhysicalEmpower.class).set(3, 1 + hero.pointsInTalent(STRENGTHENING_MEAL));
		}
		if (hero.hasTalent(FOCUSED_MEAL)){
			if (hero.heroClass == HeroClass.DUELIST){
				//0.67/1 charge for the duelist
				Buff.affect( hero, MeleeWeapon.Charger.class ).gainCharge((hero.pointsInTalent(FOCUSED_MEAL)+1)/3f);
				ScrollOfRecharging.charge( hero );
			} else {
				// lvl/3 / lvl/2 bonus dmg on next hit for other classes
				Buff.affect( hero, PhysicalEmpower.class).set(Math.round(hero.lvl / (4f - hero.pointsInTalent(FOCUSED_MEAL))), 1);
			}
		}

		if (hero.hasTalent(SATIATED_SPELLS)){
			if (hero.heroClass == HeroClass.CLERIC) {
				Buff.affect(hero, SatiatedSpellsTracker.class);
			} else {
				//3/5 shielding, delayed up to 10 turns
				int amount = 1 + 2*hero.pointsInTalent(SATIATED_SPELLS);
				Barrier b = Buff.affect(hero, Barrier.class);
				if (b.shielding() <= amount){
					b.setShield(amount);
					b.delay(Math.max(10-b.cooldown(), 0));
				}
			}
		}
		if (hero.hasTalent(ENLIGHTENING_MEAL)){
			if (hero.heroClass == HeroClass.CLERIC) {
				HolyTome tome = hero.belongings.getItem(HolyTome.class);
				if (tome != null) {
					// 2/3 of a charge at +1, 1 full charge at +2
					tome.directCharge( (1+hero.pointsInTalent(ENLIGHTENING_MEAL))/3f );
					ScrollOfRecharging.charge(hero);
				}
			} else {
				//2/3 turns of recharging
				ArtifactRecharge buff = Buff.affect( hero, ArtifactRecharge.class);
				if (buff.left() < 1 + (hero.pointsInTalent(ENLIGHTENING_MEAL))){
					//2/3 turns of artifact recharging
					ArtifactRecharge recharge = Buff.affect(hero, ArtifactRecharge.class)
						.set(1f + hero.pointsInTalent(ENLIGHTENING_MEAL));
					recharge.ignoreHornOfPlenty = foodSource instanceof HornOfPlenty;
					recharge.ignoreHolyTome = false;
				}
				Buff.prolong( hero, Recharging.class, 1 + (hero.pointsInTalent(ENLIGHTENING_MEAL)) );
				ScrollOfRecharging.charge( hero );
				SpellSprite.show(hero, SpellSprite.CHARGE);
			}
		}

		if (hero.hasTalent(KEEN_MEAL)){
			int DIST = 2 + 3*hero.pointsInTalent(KEEN_MEAL);

			Point c = Dungeon.level.cellToPoint(hero.pos);

			int sX = Math.max(0, c.x - DIST);
			int eX = Math.min(Dungeon.level.width()-1, c.x + DIST);

			int sY = Math.max(0, c.y - DIST);
			int eY = Math.min(Dungeon.level.height()-1, c.y + DIST);

			ArrayList<Trap> disarmCandidates = new ArrayList<>();

			for (int y = sY; y <= eY; y++){
				int curr = y*Dungeon.level.width() + sX;
				for ( int x = sX; x <= eX; x++){

					Trap t = Dungeon.level.traps.get(curr);
					if (t != null && t.active){
						disarmCandidates.add(t);
					}

					curr++;
				}
			}

			Collections.shuffle(disarmCandidates);
			Collections.sort(disarmCandidates, new Comparator<Trap>() {
				@Override
				public int compare(Trap o1, Trap o2) {
					float diff = Dungeon.level.trueDistance(hero.pos, o1.pos) - Dungeon.level.trueDistance(hero.pos, o2.pos);
					if (diff < 0){
						return -1;
					} else if (diff == 0){
						return 0;
					} else {
						return 1;
					}
				}
			});

			//disarms at most 4/9 traps
			while (disarmCandidates.size() > 2 + 2*hero.pointsInTalent(KEEN_MEAL)){
				disarmCandidates.remove(2 + 2*hero.pointsInTalent(KEEN_MEAL));
			}

			boolean disarmed = false;
			for ( Trap t : disarmCandidates){
				t.reveal();
				t.disarm();
				disarmed = true;
				CellEmitter.get(t.pos).burst(Speck.factory(Speck.STEAM), 6);
			}

			if (disarmed)Sample.INSTANCE.play( Assets.Sounds.TELEPORT );
		}
		if (hero.hasTalent(PREPARING_MEAL)){
            Shovel.ExplorerCooldown cd = hero.buff(Shovel.ExplorerCooldown.class);
            if (hero.heroClass == HeroClass.EXPLORER){
                if (cd != null) cd.decreaseCD(10 + 10 * hero.pointsInTalent(PREPARING_MEAL));
            } else Buff.affect(Dungeon.hero, Swiftthistle.TimeBubble.class)
                    .reset(hero.pointsInTalent(PREPARING_MEAL) - 1); // effectively 2/3 turn of time bubble
		}

        if (hero.hasTalent(ANCESTRAL_TRIBUTE))
            Buff.affect(hero, Adrenaline.class, 2 * hero.pointsInTalent(ANCESTRAL_TRIBUTE) + hero.cooldown());

        if (hero.hasTalent(TEARING_MEAL)) Buff.affect(hero, TearingMealTracker.class);
	}

	public static class WarriorFoodImmunity extends FlavourBuff{
		{ actPriority = HERO_PRIO+1; }
	}

    public static class TearingMealTracker extends Buff {
        @Override public int icon() {return BuffIndicator.AMOK;}
    }

	public static float itemIDSpeedFactor( Hero hero, Item item ){
		// 1.75x/2.5x speed with Huntress talent
		float factor = 1f + 0.75f*hero.pointsInTalent(SURVIVALISTS_INTUITION);

		// Affected by both Warrior(1.75x/2.5x) and Duelist(2.5x/inst.) talents
		if (item instanceof MeleeWeapon){
			factor *= 1f + 1.5f*hero.pointsInTalent(ADVENTURERS_INTUITION); //instant at +2 (see onItemEquipped)
			factor *= 1f + 0.75f*hero.pointsInTalent(VETERANS_INTUITION);
		}
		// Affected by both Warrior(2.5x/inst.) and Duelist(1.75x/2.5x) talents
		if (item instanceof Armor){
			factor *= 1f + 0.75f*hero.pointsInTalent(ADVENTURERS_INTUITION);
			factor *= 1f + hero.pointsInTalent(VETERANS_INTUITION); //instant at +2 (see onItemEquipped)
		}
		// 3x/instant for Mage (see Wand.wandUsed())
		if (item instanceof Wand){
			factor *= 1f + 2.0f*hero.pointsInTalent(SCHOLARS_INTUITION);
		}
		// 2x/instant for Rogue (see onItemEqupped), also id's type on equip/on pickup
		if (item instanceof Ring){
			factor *= 1f + hero.pointsInTalent(THIEFS_INTUITION);
		}
		return factor;
	}

	public static void onPotionUsed( Hero hero, int cell, float factor ){
		if (hero.hasTalent(LIQUID_WILLPOWER)){
			// 8/12% of max HP
			int shieldToGive = Math.round( factor * hero.HT * (0.04f + 0.04f*hero.pointsInTalent(LIQUID_WILLPOWER)));
			hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(shieldToGive), FloatingText.SHIELDING);
			Buff.affect(hero, Barrier.class).setShield(shieldToGive);
        }

		if (hero.hasTalent(LIQUID_NATURE)){
			ArrayList<Integer> grassCells = new ArrayList<>();
			for (int i : PathFinder.NEIGHBOURS9){
				grassCells.add(cell+i);
			}
			Random.shuffle(grassCells);
			for (int grassCell : grassCells){
				Char ch = Actor.findChar(grassCell);
				if (ch != null && ch.alignment == Char.Alignment.ENEMY){
					//2/3 turns of roots
					Buff.affect(ch, Roots.class, factor * (1 + hero.pointsInTalent(LIQUID_NATURE)));
				}
				if (Dungeon.level.map[grassCell] == Terrain.EMPTY ||
						Dungeon.level.map[grassCell] == Terrain.EMBERS ||
						Dungeon.level.map[grassCell] == Terrain.EMPTY_DECO){
					Level.set(grassCell, Terrain.GRASS);
					GameScene.updateMap(grassCell);
				}
				CellEmitter.get(grassCell).burst(LeafParticle.LEVEL_SPECIFIC, 4);
			}
			// 5/8 cells total
			int totalGrassCells = (int) (factor * (2 + 3 * hero.pointsInTalent(LIQUID_NATURE)));
			while (grassCells.size() > totalGrassCells){
				grassCells.remove(0);
			}
			for (int grassCell : grassCells){
				int t = Dungeon.level.map[grassCell];
				if ((t == Terrain.EMPTY || t == Terrain.EMPTY_DECO || t == Terrain.EMBERS
						|| t == Terrain.GRASS || t == Terrain.FURROWED_GRASS)
						&& Dungeon.level.plants.get(grassCell) == null){
					Level.set(grassCell, Terrain.HIGH_GRASS);
					GameScene.updateMap(grassCell);
				}
			}
			Dungeon.observe();
		}
		if (hero.hasTalent(LIQUID_AGILITY)){
			Buff.prolong(hero, LiquidAgilEVATracker.class, hero.cooldown() + Math.max(0, factor-1));
			if (factor >= 0.5f){
				Buff.prolong(hero, LiquidAgilACCTracker.class, 5f).uses = Math.round(factor);
			}
		}
		if (hero.hasTalent(LIQUID_CLAIRVOYANCE)){
			//copied from stone of clairvoyance ;)
			Point c = Dungeon.level.cellToPoint(hero.pos);
			int DIST = (int)(3 * hero.pointsInTalent(LIQUID_CLAIRVOYANCE) * factor);

			int[] rounding = ShadowCaster.rounding[DIST];

			int left, right;
			int curr;
			boolean noticed = false;
			for (int y = Math.max(0, c.y - DIST); y <= Math.min(Dungeon.level.height()-1, c.y + DIST); y++) {
				if (rounding[Math.abs(c.y - y)] < Math.abs(c.y - y)) {
					left = c.x - rounding[Math.abs(c.y - y)];
				} else {
					left = DIST;
					while (rounding[left] < rounding[Math.abs(c.y - y)]){
						left--;
					}
					left = c.x - left;
				}
				right = Math.min(Dungeon.level.width()-1, c.x + c.x - left);
				left = Math.max(0, left);
				for (curr = left + y * Dungeon.level.width(); curr <= right + y * Dungeon.level.width(); curr++){

					GameScene.effectOverFog( new CheckedCell( curr, hero.pos ) );
					Dungeon.level.mapped[curr] = true;

					if (Dungeon.level.secret[curr]) {
						Dungeon.level.discover(curr);

						secretForesightID(hero);

						if (Dungeon.level.heroFOV[curr]) {
							GameScene.discoverTile(curr, Dungeon.level.map[curr]);
							ScrollOfMagicMapping.discover(curr);
							noticed = true;
						}
					}

				}
			}

			if (noticed) {
				Sample.INSTANCE.play( Assets.Sounds.SECRET );
			}

			Sample.INSTANCE.play( Assets.Sounds.TELEPORT );
			GameScene.updateFog();
		}
	}

	public static void onScrollUsed( Hero hero, int pos, float factor, Class<?extends Item> cls ){
		if (hero.hasTalent(INSCRIBED_POWER)){
			// 2/3 empowered wand zaps
			Buff.affect(hero, ScrollEmpower.class).reset((int) (factor * (1 + hero.pointsInTalent(INSCRIBED_POWER))));
		}
		if (hero.hasTalent(INSCRIBED_STEALTH)){
			// 4/6 turns of stealth
			Buff.affect(hero, Invisibility.class, factor * (2 + 2*hero.pointsInTalent(INSCRIBED_STEALTH)));
			Sample.INSTANCE.play( Assets.Sounds.MELD );
		}
		if (hero.hasTalent(RECALL_INSCRIPTION) && Scroll.class.isAssignableFrom(cls) && cls != ScrollOfUpgrade.class){
			if (hero.heroClass == HeroClass.CLERIC){
				Buff.prolong(hero, RecallInscription.UsedItemTracker.class, hero.pointsInTalent(RECALL_INSCRIPTION) == 2 ? 300 : 10).item = cls;
			} else {
				// 10/15%
				if (Random.Int(20) < 1 + hero.pointsInTalent(RECALL_INSCRIPTION)){
					Reflection.newInstance(cls).collect();
					GLog.p(Messages.get(Talent.class, "refunded"));
				}
			}
		}
        if (hero.hasTalent(INSCRIBED_REGENERATION)){
            // 25/40% of level
            int toHeal = Math.round( factor * hero.lvl * (0.1f + 0.15f*hero.pointsInTalent(INSCRIBED_REGENERATION)));
            toHeal = Math.min(hero.HT - hero.HP, toHeal);
            hero.HP += toHeal;
            hero.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(toHeal), FloatingText.HEALING);
        }
	}

	public static void onRunestoneUsed( Hero hero, int pos, Class<?extends Item> cls ){
		if (hero.hasTalent(RECALL_INSCRIPTION) && Runestone.class.isAssignableFrom(cls)){
			if (hero.heroClass == HeroClass.CLERIC){
				Buff.prolong(hero, RecallInscription.UsedItemTracker.class, hero.pointsInTalent(RECALL_INSCRIPTION) == 2 ? 300 : 10).item = cls;
			} else {

				//don't trigger on 1st intuition use
				if (cls.equals(StoneOfIntuition.class) && hero.buff(StoneOfIntuition.IntuitionUseTracker.class) != null){
					return;
				}
				// 10/15%
				if (Random.Int(20) < 1 + hero.pointsInTalent(RECALL_INSCRIPTION)){
					Reflection.newInstance(cls).collect();
					GLog.p("refunded!");
				}
			}
		}
	}

	public static void onArtifactUsed( Hero hero ){
		if (hero.hasTalent(ENHANCED_RINGS)){
			Buff.prolong(hero, EnhancedRings.class, 3f*hero.pointsInTalent(ENHANCED_RINGS));
		}

		if (Dungeon.hero.heroClass != HeroClass.CLERIC
				&& Dungeon.hero.hasTalent(DIVINE_SENSE)){
			Buff.prolong(Dungeon.hero, DivineSense.DivineSenseTracker.class, Dungeon.hero.cooldown()+1);
		}

		if (Dungeon.hero.heroClass != HeroClass.CLERIC
		&& Random.Float() < Dungeon.hero.pointsInTalent(SHARED_CHARGE) * 0.15f){
			Buff.prolong( hero, Recharging.class, 1);
			ScrollOfRecharging.charge( hero );
		}

		// 10/20/30%
		if (Dungeon.hero.heroClass != HeroClass.CLERIC
				&& Dungeon.hero.hasTalent(CLEANSE)
				&& Random.Int(10) < Dungeon.hero.pointsInTalent(CLEANSE)){
			boolean removed = false;
			for (Buff b : Dungeon.hero.buffs()) {
				if (b.type == Buff.buffType.NEGATIVE
						&& !(b instanceof LostInventory)) {
					b.detach();
					removed = true;
				}
			}
			if (removed && Dungeon.hero.sprite != null) {
				new Flare( 6, 32 ).color(0xFF4CD2, true).show( Dungeon.hero.sprite, 2f );
			}
		}
	}

	public static void onItemEquipped( Hero hero, Item item ){
		boolean identify = false;
		if (hero.pointsInTalent(VETERANS_INTUITION) == 2 && item instanceof Armor){
			identify = true;
		}
		if (hero.hasTalent(THIEFS_INTUITION) && item instanceof Ring){
			if (hero.pointsInTalent(THIEFS_INTUITION) == 2){
				identify = true;
			}
			((Ring) item).setKnown();
		}
		if (hero.pointsInTalent(ADVENTURERS_INTUITION) == 2 && item instanceof Weapon){
			identify = true;
		}

		if (identify && !ShardOfOblivion.passiveIDDisabled()){
			item.identify();
		}
	}

	public static void onItemCollected( Hero hero, Item item ){
		if (hero.pointsInTalent(THIEFS_INTUITION) == 2){
			if (item instanceof Ring) ((Ring) item).setKnown();
		}
	}

	public static int onAttackProc( Hero hero, Char enemy, int dmg ){

		if (hero.hasTalent(PROVOKED_ANGER)
			&& hero.buff(ProvokedAngerTracker.class) != null){
			dmg += 1 + 2*hero.pointsInTalent(PROVOKED_ANGER);
			hero.buff(ProvokedAngerTracker.class).detach();
		}

		if (hero.hasTalent(LINGERING_MAGIC)
				&& hero.buff(LingeringMagicTracker.class) != null){
			dmg += Random.IntRange(hero.pointsInTalent(LINGERING_MAGIC) , 2);
			hero.buff(LingeringMagicTracker.class).detach();
		}

		if (enemy instanceof Mob && ((Mob) enemy).surprisedBy(hero)){
			if (hero.hasTalent(SUCKER_PUNCH) && enemy.buff(SuckerPunchTracker.class) == null){
				dmg += Random.IntRange(hero.pointsInTalent(SUCKER_PUNCH) , 2);
				Buff.affect(enemy, SuckerPunchTracker.class);
			}

			int point = hero.pointsInTalent(WEAKENING_SNEAK);
			if (point > 0 && enemy.buff(WeakeningSneakTracker.class) == null){
				Buff.affect(enemy, Blindness.class, 5f);
				Buff.affect(enemy, WeakeningSneakTracker.class);
				if (point > 2) Buff.affect(enemy, Vertigo.class, 5f);
				if (point > 1) Buff.affect(enemy, Cripple.class, 5f);
			}
		}

		if (hero.hasTalent(FOLLOWUP_STRIKE) && enemy.isAlive() && enemy.alignment == Char.Alignment.ENEMY) {
			if (hero.belongings.attackingWeapon() instanceof MissileWeapon) {
				Buff.prolong(hero, FollowupStrikeTracker.class, 5f).object = enemy.id();
			} else if (hero.buff(FollowupStrikeTracker.class) != null
					&& hero.buff(FollowupStrikeTracker.class).object == enemy.id()){
				dmg += 1 + hero.pointsInTalent(FOLLOWUP_STRIKE);
				hero.buff(FollowupStrikeTracker.class).detach();
			}
		}

		if (hero.buff(SpiritBladesTracker.class) != null
				&& Random.Int(10) < 3*hero.pointsInTalent(SPIRIT_BLADES)){
			SpiritBow bow = hero.belongings.getItem(SpiritBow.class);
			if (bow != null) dmg = bow.proc( hero, enemy, dmg );
			hero.buff(SpiritBladesTracker.class).detach();
		}

		if (hero.hasTalent(PATIENT_STRIKE)){
			if (hero.buff(PatientStrikeTracker.class) != null
					&& !(hero.belongings.attackingWeapon() instanceof MissileWeapon)){
				hero.buff(PatientStrikeTracker.class).detach();
				dmg += Random.IntRange(hero.pointsInTalent(PATIENT_STRIKE), 2);
			}
		}

		if(hero.hasTalent(ARMOR_SEIZING)){
			Buff.affect(enemy, BrokenArmor.class, 1 + hero.pointsInTalent(ARMOR_SEIZING));
		}

		if (hero.hasTalent(DEADLY_FOLLOWUP) && enemy.alignment == Char.Alignment.ENEMY) {
			if (hero.belongings.attackingWeapon() instanceof MissileWeapon) {
				if (!(hero.belongings.attackingWeapon() instanceof SpiritBow.SpiritArrow)) {
					Buff.prolong(hero, DeadlyFollowupTracker.class, 5f).object = enemy.id();
				}
			} else if (hero.buff(DeadlyFollowupTracker.class) != null
					&& hero.buff(DeadlyFollowupTracker.class).object == enemy.id()){
				dmg = Math.round(dmg * (1.0f + .1f*hero.pointsInTalent(DEADLY_FOLLOWUP)));
			}
		}

		if (hero.hasTalent(GUARD_THE_PASS) && !Dungeon.level.openSpace[hero.pos]){
			dmg = Math.round(dmg * (1.0f + 0.1f*hero.pointsInTalent(GUARD_THE_PASS)));
		}

		Buff buffs = hero.buff(SkilleddualTracker.class);
		if (hero.hasTalent(SKILLED_DUAL) && buffs != null){
			dmg = Math.round(dmg * (1f + hero.buff(SkilleddualTracker.class).attackBoost()));
		}

		buffs = hero.buff(Challenge.DuelParticipant.class);
		if (hero.hasTalent(BURN_BRIDGES) && buffs != null){
			dmg += Math.round(dmg * 0.1f * Dungeon.hero.pointsInTalent(BURN_BRIDGES));
		}
		
		if (Dungeon.level.map[hero.pos] != Dungeon.level.map[enemy.pos] && hero.hasTalent(HOME_ADVANTAGE)){
			dmg += Random.IntRange(1, hero.pointsInTalent(HOME_ADVANTAGE));
		}

		if (Dungeon.level.map[hero.pos] == Terrain.EMBERS && hero.pointsInTalent(REKINDLED_EMBER) >= 2){
			Buff.affect( enemy, Burning.class ).reignite( enemy, 5);
			enemy.sprite.emitter().burst( FlameParticle.FACTORY, 2 );
        }

        if (enemy.HP <= enemy.HT * (0.2f + 0.3f * hero.pointsInTalent(BURIAL_CEREMONY)) && hero.hasTalent(BURIAL_CEREMONY)){
            dmg ++;
        }

        TearingMealTracker tear = hero.buff(TearingMealTracker.class);
        if (tear != null){
            tear.detach();

            if (!enemy.isImmune(Bleeding.class)){
                Bleeding b = enemy.buff(Bleeding.class);
                if (b == null) b = new Bleeding();

                b.announced = false;
                b.set(dmg * (0.65f + 0.25f * hero.pointsInTalent(TEARING_MEAL)), TearingMealTracker.class);
                b.attachTo(enemy);
                enemy.sprite.showStatusWithIcon(CharSprite.WARNING, "+" + (int)b.level(), FloatingText.BLEEDING);
                dmg = 0;
            }
        }
		return dmg;
	}

	public static class ProvokedAngerTracker extends FlavourBuff{
		{ type = Buff.buffType.POSITIVE; }
		public int icon() { return BuffIndicator.WEAPON; }
		public void tintIcon(Image icon) { icon.hardlight(1.43f, 1.43f, 1.43f); }
		public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }
	}
	public static class LingeringMagicTracker extends FlavourBuff{
		{ type = Buff.buffType.POSITIVE; }
		public int icon() { return BuffIndicator.WEAPON; }
		public void tintIcon(Image icon) { icon.hardlight(1.43f, 1.43f, 0f); }
		public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }
	}
	public static class SuckerPunchTracker extends Buff{}
	public static class WeakeningSneakTracker extends Buff{}
	public static class FollowupStrikeTracker extends FlavourBuff{
		public int object;
		{ type = Buff.buffType.POSITIVE; }
		public int icon() { return BuffIndicator.INVERT_MARK; }
		public void tintIcon(Image icon) { icon.hardlight(0f, 0.75f, 1f); }
		public float iconFadePercent() { return Math.max(0, 1f - (visualcooldown() / 5)); }
		private static final String OBJECT    = "object";
		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(OBJECT, object);
		}
		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			object = bundle.getInt(OBJECT);
		}
	}

	public static final int MAX_TALENT_TIERS = 4;

	public static void initClassTalents( Hero hero ){
		initClassTalents( hero.heroClass, hero.talents, hero.metamorphedTalents, hero.corroLostTalent);
	}

	public static void initClassTalents( HeroClass cls, ArrayList<LinkedHashMap<Talent, Integer>> talents){
		initClassTalents( cls, talents, new LinkedHashMap<>(), null);
	}

	public static void initClassTalents( HeroClass cls, ArrayList<LinkedHashMap<Talent, Integer>> talents, LinkedHashMap<Talent, Talent> replacements, Talent corroTalent){
		while (talents.size() < MAX_TALENT_TIERS){
			talents.add(new LinkedHashMap<>());
		}

		ArrayList<Talent> tierTalents = new ArrayList<>();

		//tier 1
		switch (cls){
			case WARRIOR: default:
				Collections.addAll(tierTalents, HEARTY_MEAL, VETERANS_INTUITION, TESTED_REVIVE, PROVOKED_ANGER, IRON_WILL);
				break;
			case MAGE:
				Collections.addAll(tierTalents, EMPOWERING_MEAL, SCHOLARS_INTUITION, TESTED_HYPOTHESIS, LINGERING_MAGIC, BACKUP_BARRIER);
				break;
			case ROGUE:
				Collections.addAll(tierTalents, CACHED_RATIONS, THIEFS_INTUITION, TESTED_MYST, SUCKER_PUNCH, PROTECTIVE_SHADOWS);
				break;
			case HUNTRESS:
				Collections.addAll(tierTalents, NATURES_BOUNTY, SURVIVALISTS_INTUITION, TESTED_SWIFTNESS, FOLLOWUP_STRIKE, NATURES_AID);
				break;
			case DUELIST:
				Collections.addAll(tierTalents, STRENGTHENING_MEAL, ADVENTURERS_INTUITION, TESTED_CHARGE, PATIENT_STRIKE, AGGRESSIVE_BARRIER);
				break;
			case CLERIC:
				Collections.addAll(tierTalents, SATIATED_SPELLS, HOLY_INTUITION, TESTED_HOLINESS, SEARING_LIGHT, SHIELD_OF_LIGHT);
				break;
			case EXPLORER:
				Collections.addAll(tierTalents, KEEN_MEAL, SECRET_FORESIGHT, TESTED_AWARENESS, HOME_ADVANTAGE, SAFE_SURVEY);
				break;
            case WRAITH:
                Collections.addAll(tierTalents, ANCESTRAL_TRIBUTE, BLOOD_INTUITION, TESTED_ANTIMAGIC, BURIAL_CEREMONY, FLEET_BARRIER);
                break;
		}
		for (Talent talent : tierTalents){
			if (replacements.containsKey(talent)){
				talent = replacements.get(talent);
			}
			talents.get(0).put(talent, 0);
		}
		tierTalents.clear();

		//tier 2
		switch (cls){
			case WARRIOR: default:
				Collections.addAll(tierTalents, IRON_STOMACH, LIQUID_WILLPOWER, RUNIC_TRANSFERENCE, LETHAL_MOMENTUM, IMPROVISED_PROJECTILES, FIGHTING_BACK);
				break;
			case MAGE:
				Collections.addAll(tierTalents, ENERGIZING_MEAL, INSCRIBED_POWER, WAND_PRESERVATION, ARCANE_VISION, SHIELD_BATTERY, RESERVED_ENERGY);
				break;
			case ROGUE:
				Collections.addAll(tierTalents, MYSTICAL_MEAL, INSCRIBED_STEALTH, EMERGENCY_CHARGE, SILENT_STEPS, ROGUES_FORESIGHT, ROGUES_INSTINCT);
				break;
			case HUNTRESS:
				Collections.addAll(tierTalents, INVIGORATING_MEAL, LIQUID_NATURE, REJUVENATING_STEPS, HEIGHTENED_SENSES, DURABLE_PROJECTILES, IVY_BIND);
				break;
			case DUELIST:
				Collections.addAll(tierTalents, FOCUSED_MEAL, LIQUID_AGILITY, WEAPON_RECHARGING, LETHAL_HASTE, SWIFT_EQUIP, POWER_ACCUMULATION);
				break;
			case CLERIC:
				Collections.addAll(tierTalents, ENLIGHTENING_MEAL, RECALL_INSCRIPTION, SUNRAY, DIVINE_SENSE, BLESS, ASCETICISM);
				break;
			case EXPLORER:
				Collections.addAll(tierTalents, PREPARING_MEAL, LIQUID_CLAIRVOYANCE, BARBED_WIRE, WINDING_PORCH, REKINDLED_EMBER, AGGRESSIVE_ROADBLOCK);
				break;
            case WRAITH:
                Collections.addAll(tierTalents, TEARING_MEAL, INSCRIBED_REGENERATION, BLOODLETTING, PSIONIC_BLAST, SCAPEGOAT, THROWN_EVIL);
                break;
		}
		for (Talent talent : tierTalents){
			if (replacements.containsKey(talent)){
				talent = replacements.get(talent);
			}
			talents.get(1).put(talent, 0);
		}
		tierTalents.clear();

		//tier 3
		switch (cls){
			case WARRIOR: default:
				Collections.addAll(tierTalents, INTACT_SEAL, STRONGMAN, OVERWHELMING);
				break;
			case MAGE:
				Collections.addAll(tierTalents, DESPERATE_POWER, ALLY_WARP, ARCANE_ARMOR);
				break;
			case ROGUE:
				Collections.addAll(tierTalents, ENHANCED_RINGS, LIGHT_CLOAK, STEALTH_METABOLISM);
				break;
			case HUNTRESS:
				Collections.addAll(tierTalents, L_M_MASTER, SEER_SHOT, ORGANIC_FERTILIZER);
				break;
			case DUELIST:
				Collections.addAll(tierTalents, PRECISE_ASSAULT, DEADLY_FOLLOWUP, AGILE_COUNTATK);
				break;
			case CLERIC:
				Collections.addAll(tierTalents, CLEANSE, LIGHT_READING, SHARED_CHARGE);
				break;
			case EXPLORER:
				Collections.addAll(tierTalents, DEMOLITION, DIG_THE_WELL, CONVENIENT_SHOVEL);
				break;
            case WRAITH:
                Collections.addAll(tierTalents, VICIOUS_BETRAYAL, CURSED_POWER, BLURRING_BODY);
                break;
		}
		for (Talent talent : tierTalents){
			if (replacements.containsKey(talent)){
				talent = replacements.get(talent);
			}
			if (corroTalent != talent) talents.get(2).put(talent, 0);
		}
		tierTalents.clear();

		//tier4
		//TBD
	}

	public static void initSubclassTalents( Hero hero ){
		initSubclassTalents( hero.subClass, hero.talents, hero.corroLostTalent);
	}

	public static void initSubclassTalents( HeroSubClass cls, ArrayList<LinkedHashMap<Talent, Integer>> talents, Talent corroTalent){
		if (cls == HeroSubClass.NONE) return;

		while (talents.size() < MAX_TALENT_TIERS){
			talents.add(new LinkedHashMap<>());
		}

		ArrayList<Talent> tierTalents = new ArrayList<>();

		//tier 3
		switch (cls){
			case BERSERKER: default:
				Collections.addAll(tierTalents, ENDLESS_RAGE, DEATHLESS_FURY, ENRAGED_CATALYST, BEAR_GRUDGES, BLADE_OF_ANGER);
				break;
			case GLADIATOR:
				Collections.addAll(tierTalents, CLEAVE, LETHAL_DEFENSE, ENHANCED_COMBO, REPEATED_SKILL, FAR_STANDOFF);
				break;
			case GUARD:
				Collections.addAll(tierTalents, KEEP_GUARDING, GUARD_THE_PASS, SHIELDING, EMERGENCY_SHIELD, ARMOR_SEIZING);
				break;
			case BATTLEMAGE:
				Collections.addAll(tierTalents, EMPOWERED_STRIKE, MYSTICAL_CHARGE, EXCESS_CHARGE, BATTLE_CHARGE, VARIED_MAGIC);
				break;
			case WARLOCK:
				Collections.addAll(tierTalents, SOUL_EATER, SOUL_SIPHON, NECROMANCERS_MINIONS, CLEAR_YOUR_SOUL, MANA_EATING);
				break;
            case SWITCHER:
                Collections.addAll(tierTalents, SHARED_ARCANA, SWITCH_MASTER, RELAY_RECHARGING, ENERGY_RECYCLING, MYSTICAL_SWITCH);
                break;
			case ASSASSIN:
				Collections.addAll(tierTalents, ENHANCED_LETHALITY, ASSASSINS_REACH, TERRORIST_ATTACK, CHARGE_RECYCLING, EXTREMIST);
				break;
			case FREERUNNER:
				Collections.addAll(tierTalents, EVASIVE_ARMOR, PROJECTILE_MOMENTUM, SPEEDY_STEALTH, ARCANE_STEP, STRETCHING);
				break;
			case NINJA:
				Collections.addAll(tierTalents, BLADE_OF_UNREAL, HIDDEN_IN_THE_CITY, FLYING_LOCUST_STONE, STEALTH_LEAP, WEAKENING_SNEAK);
				break;
			case SNIPER:
				Collections.addAll(tierTalents, FARSIGHT, SHARED_ENCHANTMENT, SHARED_UPGRADES, SUPRESSING_MARK, RESONANCE_FETCH);
				break;
			case WARDEN:
				Collections.addAll(tierTalents, DURABLE_TIPS, BARKSKIN, DEW_COLLECTING, JUNGLE_GUERRILLA, GRASSMAN);
				break;
			case SCOUT:
				Collections.addAll(tierTalents, STRONG_MARK_SC, TRACKING_ARROW, SWIFT_COURIER, EXPEL_ENEMIES, PIONEERING_SPIRIT);
				break;
			case CHAMPION:
				Collections.addAll(tierTalents, VARIED_CHARGE, TWIN_UPGRADES, COMBINED_LETHALITY, SKILLED_DUAL, MARCH_FORWARD);
				break;
			case MONK:
				Collections.addAll(tierTalents, UNENCUMBERED_SPIRIT, MONASTIC_VIGOR, COMBINED_ENERGY, YANG_SEEING, YIN_GAIT);
				break;
			case PHANTOM:
				Collections.addAll(tierTalents, FLEXIBLE_FOOTWORK, ENRAGED_SHADOW, MULTIPLE_DODGE, EIDOLON, DIFFUSED_IMAGE);
				break;
			case PRIEST:
				Collections.addAll(tierTalents, HOLY_LANCE, HALLOWED_GROUND, MNEMONIC_PRAYER, EXPLOSION, ENHANCED_RADIANCE);
				break;
			case PALADIN:
				Collections.addAll(tierTalents, LAY_ON_HANDS, AURA_OF_PROTECTION, WALL_OF_LIGHT, JUSTICE_STRIKE, ENHANCED_SMITE);
				break;
			case PREACHER:
				Collections.addAll(tierTalents, PUNISHMENT, DRAPE_OF_REDEMPTION, HOLY_TRAP, HOLY_GHOST, ENHANCED_BOOKPAGE);
				break;
			case WAVECHASER:
				Collections.addAll(tierTalents, RIVER_EROSION, SON_OF_SEA, DROWNING, LAKE_DEVELOPMENT, UNDERCURRENT);
				break;
			case TRAPPER:
				Collections.addAll(tierTalents, TRAP_MASTER, FRIENDLY_MECHANISM, SIMPLE_STRUCTURE, SENSITIVE_PEDAL, LIQUID_COLLECTING);
				break;
			case ROCKSY:
				Collections.addAll(tierTalents, METEROIC_IRON, ROCK_PROTECTOR, MIND_CONTROL, DESTRUCTIVE_STRIKE, METEOR_CRATER);
				break;
            case INCUBUS:
                Collections.addAll(tierTalents, LULLABY, SLEEPWALKING, SLEEPING_IN, WRONG_SIDE_OF_THE_BED, NIGHTMARE_HAUNTING);
                break;
			case PLAGUEGOD:
				Collections.addAll(tierTalents, CORPSE_DECAY, HOMEMADE_DRUG, MAGICAL_VENT, ACID_RAIN, PLAGUE_EUCHARIST);
				break;
		}
		for (Talent talent : tierTalents){
			if (corroTalent != talent) talents.get(2).put(talent, 0);
		}
		tierTalents.clear();

	}

	public static void initArmorTalents( Hero hero ){
		initArmorTalents( hero.armorAbility, hero.talents);
	}

	public static void initArmorTalents(ArmorAbility abil, ArrayList<LinkedHashMap<Talent, Integer>> talents ){
		if (abil == null) return;

		while (talents.size() < MAX_TALENT_TIERS){
			talents.add(new LinkedHashMap<>());
		}

		for (Talent t : abil.talents()){
			talents.get(3).put(t, 0);
		}
	}

	private static final String TALENT_TIER = "talents_tier_";
	private static final String CORRO_TALENT = "corro_talent";

	public static void storeTalentsInBundle( Bundle bundle, Hero hero ){
		for (int i = 0; i < MAX_TALENT_TIERS; i++){
			LinkedHashMap<Talent, Integer> tier = hero.talents.get(i);
			Bundle tierBundle = new Bundle();

			for (Talent talent : tier.keySet()){
				if (tier.get(talent) > 0){
					tierBundle.put(talent.name(), tier.get(talent));
				}
				if (tierBundle.contains(talent.name())){
					tier.put(talent, Math.min(tierBundle.getInt(talent.name()), talent.maxPoints()));
				}
			}
			bundle.put(TALENT_TIER+(i+1), tierBundle);
		}

		Bundle replacementsBundle = new Bundle();
		for (Talent t : hero.metamorphedTalents.keySet()){
			replacementsBundle.put(t.name(), hero.metamorphedTalents.get(t));
		}
		bundle.put("replacements", replacementsBundle);

		bundle.put(CORRO_TALENT, hero.corroLostTalent);
	}

	private static final HashSet<String> removedTalents = new HashSet<>();
	static{
		//v2.2.0
		removedTalents.add("X_U_N");
	}

	private static final HashMap<String, String> renamedTalents = new HashMap<>();
	static{
        //X_U_N v1.0.1
        renamedTalents.put("FARSIGHT_MEAL",             "PREPARING_MEAL");
        renamedTalents.put("FLUORESCENCE",              "SENSITIVE_PEDAL");
		//X_U_N v1.0.0
        renamedTalents.put("VARIED_ENVIRONMENT",        "DEMOLITION");
		renamedTalents.put("HASHASHINS",                "EXTREMIST");
		renamedTalents.put("HOLY_DRAPE",                "DRAPE_OF_REDEMPTION");
		renamedTalents.put("HOLY_ANTIMAGIC",            "HOLY_TRAP");
		renamedTalents.put("HOLY_IMAGE",                "HOLY_GHOST");
		//X_U_N v0.2.5
		renamedTalents.put("TESTED_ADRENALINE",         "TESTED_SWIFTNESS");
		//X_U_N v0.2.2
		renamedTalents.put("SHIELDING_DEW",             "DEW_COLLECTING");
		renamedTalents.put("IN_BATTLE",                 "REPEATED_SKILL");
		//X_U_N v0.2.0
		renamedTalents.put("BOUNTY_HUNTER",             "TERRORIST_ATTACK");
		//X_U_N v0.1.4
		renamedTalents.put("POINT_BLANK",               "L_M_MASTER");
		//X_U_N v0.1.2
		renamedTalents.put("SACRED_ARCANE",             "ASCETICISM");
		//X_U_N v0.1.1
		renamedTalents.put("WIDE_SEARCH",               "STEALTH_METABOLISM");
		//X_U_N v0.1.0
		renamedTalents.put("TEST_SUBJECT",              "TESTED_REVIVE");
		renamedTalents.put("HOLD_FAST",                 "INTACT_SEAL");
	}

	public static void restoreTalentsFromBundle( Bundle bundle, Hero hero ){
		if (bundle.contains("replacements")){
			Bundle replacements = bundle.getBundle("replacements");
			for (String key : replacements.getKeys()){
				String value = replacements.getString(key);
				if (renamedTalents.containsKey(key)) key = renamedTalents.get(key);
				if (renamedTalents.containsKey(value)) value = renamedTalents.get(value);
				if (!removedTalents.contains(key) && !removedTalents.contains(value)){
					try {
						hero.metamorphedTalents.put(valueOf(key), valueOf(value));
					} catch (Exception e) {
						ShatteredPixelDungeon.reportException(e);
					}
				}
			}
		}

		if (bundle.contains(CORRO_TALENT)) hero.corroLostTalent = bundle.getEnum(CORRO_TALENT, Talent.class);

		if (hero.heroClass != null)     initClassTalents(hero);
		if (hero.subClass != null)      initSubclassTalents(hero);
		if (hero.armorAbility != null)  initArmorTalents(hero);

		for (int i = 0; i < MAX_TALENT_TIERS; i++){
			LinkedHashMap<Talent, Integer> tier = hero.talents.get(i);
			Bundle tierBundle = bundle.contains(TALENT_TIER+(i+1)) ? bundle.getBundle(TALENT_TIER+(i+1)) : null;

			if (tierBundle != null){
				for (String tName : tierBundle.getKeys()){
					int points = tierBundle.getInt(tName);
					if (renamedTalents.containsKey(tName)) tName = renamedTalents.get(tName);
					if (!removedTalents.contains(tName)) {
						try {
							Talent talent = valueOf(tName);
							if (tier.containsKey(talent)) {
								tier.put(talent, Math.min(points, talent.maxPoints()));
							}
						} catch (Exception e) {
							ShatteredPixelDungeon.reportException(e);
						}
					}
				}
			}
		}
	}

}
