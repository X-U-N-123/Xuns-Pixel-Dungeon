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

package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hypnosis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Phantom;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Preparation;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.RockFallBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.TrapChoose;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.WhirlpoolBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndChooseSubclass;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class TengusMask extends Item {

	public boolean random = true;
	
	private static final String AC_WEAR	= "WEAR";
	
	{
		stackable = false;
		image = ItemSpriteSheet.MASK;

		defaultAction = AC_WEAR;

		unique = true;
	}
	
	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add( AC_WEAR );
		return actions;
	}
	
	@Override
	public void execute( Hero hero, String action ) {

		super.execute( hero, action );

		if (action.equals( AC_WEAR )) {
			
			curUser = hero;

			GameScene.show( new WndChooseSubclass( this, hero ) );
			
		}
	}
	
	@Override
	public boolean doPickUp(Hero hero, int pos) {
		Badges.validateMastery();
		return super.doPickUp( hero, pos );
	}
	
	@Override
	public boolean isUpgradable() {
		return false;
	}
	
	@Override
	public boolean isIdentified() {
		return true;
	}
	
	public void choose( HeroSubClass way ) {

		if (random && Dungeon.isChallenged(Challenges.RANDOMIZE)){
			way = Random.oneOf(curUser.heroClass.subClasses());
		} else random = true;
		
		detach( curUser.belongings.backpack );
		Catalog.countUse( getClass() );
		
		curUser.spend( Actor.TICK );
		curUser.busy();
		
		curUser.subClass = way;
		Talent.initSubclassTalents(curUser);

		if (way == HeroSubClass.ASSASSIN && curUser.invisible > 0){
			Buff.affect(curUser, Preparation.class);
		}

		if (way == HeroSubClass.PHANTOM){
			ActionIndicator.setAction(Buff.affect(curUser, Phantom.class));
		}

		if (way == HeroSubClass.WAVECHASER){
			ActionIndicator.setAction(Buff.affect(curUser, WhirlpoolBuff.class));
		}

		if (way == HeroSubClass.TRAPPER){
            ActionIndicator.setAction(Buff.affect(curUser, TrapChoose.class));
		}

		if (way == HeroSubClass.ROCKSY){
			Buff.affect(curUser, RockFallBuff.class);
		}

        if (way == HeroSubClass.INCUBUS){
            ActionIndicator.setAction(Buff.affect(curUser, Hypnosis.class));
        }
		
		curUser.sprite.operate( curUser.pos );
		Sample.INSTANCE.play( Assets.Sounds.MASTERY );
		
		Emitter e = curUser.sprite.centerEmitter();
		e.pos(e.x-2, e.y-6, 4, 4);
		e.start(Speck.factory(Speck.MASK), 0.05f, 20);
		GLog.p( Messages.get(this, "used"));
		
	}

	private static final ItemSprite.Glowing WHITE = new ItemSprite.Glowing( 0xFFFFFF );

	@Override
	public ItemSprite.Glowing glowing() {
		return random ? null : WHITE;
	}

	private static final String RANDOM = "random";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( RANDOM, random );
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		random = bundle.getBoolean(RANDOM);
	}

	public static class DestinyControl extends Recipe.SimpleRecipe {

		private static final int OUT_QUANTITY = 1;

		{
			inputs =  new Class[]{TengusMask.class, PotionOfExperience.class};
			inQuantity = new int[]{1, 1};

			cost = 0;

			output = TengusMask.class;
			outQuantity = OUT_QUANTITY;
		}

		@Override
		public boolean testIngredients (ArrayList<Item> ingredients){
			return Dungeon.isChallenged(Challenges.RANDOMIZE) && super.testIngredients(ingredients);
		}

		@Override
		public Item brew(ArrayList<Item> ingredients) {
			TengusMask mask = ((TengusMask)super.brew(ingredients));
			mask.random = false;
			return mask;
		}
	}
}