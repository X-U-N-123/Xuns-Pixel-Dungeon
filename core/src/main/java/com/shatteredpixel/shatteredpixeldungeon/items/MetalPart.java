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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MultiTool;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class MetalPart extends Item {

	{
		defaultAction = AC_MODIFY;
		image = ItemSpriteSheet.PART;
		stackable = true;
		bones = false;
	}

	private static final String AC_MODIFY = "modify";

	private static MultiTool tool = null;

	@Override
	public ArrayList<String> actions(Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		actions.add(AC_MODIFY);
		return actions;
	}

	@Override
	public void execute( Hero hero, String action ) {

		super.execute( hero, action );

		if (action.equals(AC_MODIFY)) {
			GameScene.selectItem( itemSelector );
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
	public int value() {
		return 10 * quantity;
	}

	private final WndBag.ItemSelector itemSelector = new WndBag.ItemSelector() {

		@Override
		public String textPrompt() {
			return Messages.get(this, "modify");
		}

		@Override
		public boolean itemSelectable(Item item) {
			return item instanceof KindOfWeapon || item instanceof Armor
					|| (item instanceof Wand && ((Wand) item).modify != Wand.Modification.DISINTEGRATING)
					|| (item instanceof MetalPart && curUser.subClass == HeroSubClass.GRENADIER)
					|| (item instanceof Bomb && curUser.hasTalent(Talent.ADDED_POWDER));
		}

		@Override
		public void onSelect( Item item ) {
			if (item == null) return;
			if (item instanceof MetalPart){
				if (item.quantity() < 3){
					GLog.w(Messages.get(this, "no_enough_part"));
					return;
				}
				item.quantity(item.quantity() - 3);

				item = new Bomb();
				if (curUser.pointsInTalent(Talent.ADDED_POWDER) >= 3) item.upgrade();
				if (!item.collect()) Dungeon.level.drop(item, curUser.pos).sprite.drop();

				updateQuickslot();
				curUser.sprite.operate(curUser.pos);
				Sample.INSTANCE.play(Assets.Sounds.UNLOCK);
				curUser.spendAndNext(1);
			} else if (item instanceof Bomb) {
				if (item.level() < 1
						|| (item.level() < 3 && curUser.pointsInTalent(Talent.ADDED_POWDER) >= 2)){
					Item upgrade;
					if (item.quantity == 1) item.upgrade();
					else {
						upgrade = item.split(1).upgrade();
						if (!upgrade.collect()) Dungeon.level.drop(item, curUser.pos).sprite.drop();
					}
					detach(curUser.belongings.backpack);
					curUser.spendAndNext(1);

					curUser.sprite.operate(curUser.pos);
					updateQuickslot();
					Sample.INSTANCE.play(Assets.Sounds.UNLOCK);
				} else GLog.w(Messages.get(this, "max_lvl"));
			} else GameScene.show(new WndModify(item, MetalPart.this));
		}
	};

	private static boolean multi = true;

	public static class WndModify extends Window {

		private static final int WIDTH_P = 120;

		private static final int MARGIN  = 2;

		public WndModify(Item item, MetalPart part){
			super();

			int width = WIDTH_P;

			float pos = MARGIN;
			RenderedTextBlock title = PixelScene.renderTextBlock(
					Messages.titleCase(Messages.get(MetalPart.class, "ac_modify")), 9);
			title.hardlight(TITLE_COLOR);
			title.setPos((width-title.width())/2, pos);
			title.maxWidth(width - MARGIN * 2);
			add(title);

			pos = title.bottom() + 3*MARGIN;

			if (item instanceof KindOfWeapon){
				if (item instanceof MultiTool && Dungeon.hero.pointsInTalent(Talent.MULTI_MODIFY) >= 2 && multi){
					RedButton wepBtn = new RedButton(Messages.get(this, "wep"), 8) {
						@Override
						protected void onClick() {
							super.onClick();
							hide();
							multi = false;
							GameScene.show(new WndModify(item, part));
						}
					};
					wepBtn.icon(new ItemSprite(ItemSpriteSheet.WEAPON_HOLDER));
					wepBtn.leftJustify = true;
					wepBtn.setSize(width, wepBtn.reqHeight());
					wepBtn.setRect(0, pos, width, wepBtn.reqHeight());
					add(wepBtn);
					pos = wepBtn.bottom() + MARGIN;

					Armor a = new Armor(0);
					RedButton armorBtn = new RedButton(Messages.get(this, "armor"), 8) {
						@Override
						protected void onClick() {
							super.onClick();
							hide();
							tool = (MultiTool)item;
							GameScene.show(new WndModify(a, part));
						}
					};
					armorBtn.icon(new ItemSprite(ItemSpriteSheet.ARMOR_HOLDER));
					armorBtn.leftJustify = true;
					armorBtn.setSize(width, armorBtn.reqHeight());
					armorBtn.setRect(0, pos, width, armorBtn.reqHeight());
					add(armorBtn);
					pos = armorBtn.bottom() + MARGIN;

					if (Dungeon.hero.pointsInTalent(Talent.MULTI_MODIFY) >= 3){
						Wand w = new Wand.PlaceHolder();
						RedButton wandBtn = new RedButton(Messages.get(this, "wand"), 8) {
							@Override
							protected void onClick() {
								super.onClick();
								hide();
								tool = (MultiTool)item;
								GameScene.show(new WndModify(w, part));
							}
						};
						wandBtn.icon(new ItemSprite(ItemSpriteSheet.WAND_HOLDER));
						wandBtn.leftJustify = true;
						wandBtn.setSize(width, wandBtn.reqHeight());
						wandBtn.setRect(0, pos, width, wandBtn.reqHeight());
						add(wandBtn);
						pos = wandBtn.bottom() + MARGIN;
					}

				} else for (KindOfWeapon.Modification mod : KindOfWeapon.Modification.values()) {

					RedButton modBtn = new RedButton("_" + Messages.titleCase(mod.title())
							+ " " + Messages.get(this, "cost", mod.partCost(), mod.maxDurability()) + ":_ "
							+ mod.desc(), 6){
						@Override
						protected void onClick() {
							super.onClick();
							hide();
							((KindOfWeapon) item).modify(mod);
							part.quantity(part.quantity() - mod.partCost());
							if (part.quantity() <= 0)
								part.detachAll(curUser.belongings.backpack);
							Item.updateQuickslot();
						}
					};
					modBtn.leftJustify = true;
					modBtn.multiline = true;
					modBtn.setSize(width, modBtn.reqHeight());
					modBtn.setRect(0, pos, width, modBtn.reqHeight());
					modBtn.enable(part.quantity >= mod.partCost());
					if ((mod != KindOfWeapon.Modification.SMOG_ATTACH || item instanceof MissileWeapon)
					 && (mod != KindOfWeapon.Modification.LONG_HANDLE || item instanceof MeleeWeapon)
							&& (!mod.craftsman() || Dungeon.hero.subClass == HeroSubClass.CRAFTSMAN)) {
						add(modBtn);
						pos = modBtn.bottom() + MARGIN;
					}
				}
				multi = true;
			} else if (item instanceof Armor){
				for (Armor.Modification mod : Armor.Modification.values()) {

					RedButton modBtn = new RedButton("_" + Messages.titleCase(mod.title())
							+ " " + Messages.get(this, "cost", mod.partCost(), mod.maxDurability()) + ":_ "
							+ mod.desc(), 6){
						@Override
						protected void onClick() {
							super.onClick();
							hide();
							((Armor) item).modify(mod);
							if (((Armor) item).tier == 0)
								tool.modify(mod);

							part.quantity(part.quantity() - mod.partCost());
							if (part.quantity() <= 0)
								part.detachAll(curUser.belongings.backpack);
							Item.updateQuickslot();
						}
					};
					modBtn.leftJustify = true;
					modBtn.multiline = true;
					modBtn.setSize(width, modBtn.reqHeight());
					modBtn.setRect(0, pos, width, modBtn.reqHeight());
					modBtn.enable(part.quantity >= mod.partCost());
					if (!mod.craftsman() || Dungeon.hero.subClass == HeroSubClass.CRAFTSMAN){
						add(modBtn);
						pos = modBtn.bottom() + MARGIN;
					}
				}
			} else if (item instanceof Wand) {
				for (Wand.Modification mod : Wand.Modification.values()) {

					RedButton modBtn = new RedButton("_" + Messages.titleCase(mod.title())
							+ " " + Messages.get(this, "cost", mod.partCost(), mod.maxDurability()) + ":_ "
							+ mod.desc(), 6) {
						@Override
						protected void onClick() {
							super.onClick();
							hide();
							if (mod == Wand.Modification.DISINTEGRATING){
								GameScene.show(
									new WndOptions( new ItemSprite(item),
											Messages.titleCase(Messages.get(Wand.Modification.class, "disintegrating")),
											Messages.get(Wand.class, "destructive"),
											Messages.get(Wand.class, "yes"),
											Messages.get(Wand.class, "no") ){

										private float elapsed = 0f;

										@Override
										public synchronized void update() {
											super.update();
											elapsed += Game.elapsed;
										}

										@Override
										public void hide() {
											if (elapsed > 0.2f){
												super.hide();
											}
										}

										@Override
										protected void onSelect( int index ) {
											super.onSelect(index);
											if (index == 0 && elapsed > 0.2f) {
												((Wand) item).modify(mod);
												part.quantity(part.quantity() - mod.partCost());
												if (part.quantity() <= 0)
													part.detachAll(curUser.belongings.backpack);
												Item.updateQuickslot();
											}
										}
									}
								);
							} else {
								((Wand) item).modify(mod);
								if (item instanceof Wand.PlaceHolder)
									tool.modify(mod);

								part.quantity(part.quantity() - mod.partCost());
								if (part.quantity() <= 0)
									part.detachAll(curUser.belongings.backpack);
								Item.updateQuickslot();
							}
						}
					};
					modBtn.leftJustify = true;
					modBtn.multiline = true;
					modBtn.setSize(width, modBtn.reqHeight());
					modBtn.setRect(0, pos, width, modBtn.reqHeight());
					modBtn.enable(part.quantity >= mod.partCost());
					if (!mod.craftsman() || Dungeon.hero.subClass == HeroSubClass.CRAFTSMAN) {
						add(modBtn);
						pos = modBtn.bottom() + MARGIN;
					}
					Item.updateQuickslot();
				}
			}
			resize(width, (int)pos);
		}
	}

	@Override
	protected void onThrow( int cell ) {
		boolean consume = false;

		Char ch = Actor.findChar(cell);
		if (ch != null && ch.alignment != Char.Alignment.ALLY)
			if ((Char.hasProp(ch, Char.Property.MECHANICAL) && Dungeon.hero.hasTalent(Talent.REMOTE_DESTRUCTION))
					|| (Char.hasProp(ch, Char.Property.INORGANIC) && Dungeon.hero.pointsInTalent(Talent.REMOTE_DESTRUCTION) >= 2)){
				Sample.INSTANCE.play(Assets.Sounds.HIT);
				Buff.affect(ch, Cripple.class, 7);
				consume = true;
			}
		if (!consume) super.onThrow(cell);
	}
}