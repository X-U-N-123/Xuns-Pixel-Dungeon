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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Wandmaker;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTerrainTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

public class devPickaxe extends Item {

	private static final String AC_SET   = "set";
	private static final String AC_MINE  = "mine";
	private static final String AC_RESET = "reset";

	private int chosenTerrain = 0;
	public static int questDepth;

	{
		defaultAction = AC_MINE;
		image = ItemSpriteSheet.DEV_PICKAXE;
		cursedKnown = levelKnown = true;
		unique = true;
		bones = false;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions = super.actions(hero);
		actions.add(AC_SET);
		actions.add(AC_MINE);
		actions.add(AC_RESET);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {

		super.execute(hero, action);
		if (action.equals(AC_SET)) GameScene.show(new WndSelectTerrain(this));
		if (action.equals(AC_MINE)){
			GameScene.selectCell(new CellSelector.Listener() {
				@Override
				public void onSelect(Integer cell) {
					if (cell == null) return;
					if (!Dungeon.level.insideMap(cell)){
						GLog.w(Messages.get(this, "oom"));
						return;
					}
					curUser.sprite.attack(cell);
					Level.set(cell, chosenTerrain);
					Sample.INSTANCE.play(Assets.Sounds.MINE);

					GameScene.updateMap(cell);
					Dungeon.level.buildFlagMaps();
					Dungeon.observe();
					AttackIndicator.updateState();
				}

				@Override
				public String prompt() {
					return Messages.get(this, "prompt");
				}
			});
			defaultAction = AC_MINE;
		}
		if (action.contains(AC_RESET)){
			for (Mob m: Dungeon.level.mobs){
				if (m instanceof Ghost) {
					questDepth = Dungeon.depth;
					Ghost.Quest.reset();
				}
				if (m instanceof Wandmaker) {
					questDepth = Dungeon.depth;
					Wandmaker.Quest.reset();
				}
				if (m instanceof Blacksmith) {
					questDepth = Dungeon.depth;
					Blacksmith.Quest.reset();
				}
				if (m instanceof Imp) {
					questDepth = Dungeon.depth;
					Imp.Quest.reset();
				}
			}
			InterlevelScene.mode = InterlevelScene.Mode.RESET;
			Game.switchScene(InterlevelScene.class);
			defaultAction = AC_RESET;
		}
	}

	private static class WndSelectTerrain extends Window {

		private static final int MARGIN = 1;
		RenderedTextBlock chosenText;

		public WndSelectTerrain(devPickaxe pickaxe){
			super();

			int width1 = 118;

			float posY = 2 * MARGIN;
			float posX = 0;

			RenderedTextBlock title =
					PixelScene.renderTextBlock(Messages.titleCase(Messages.get(this, "choose_terrain")), 9);
			title.hardlight(TITLE_COLOR);
			title.setPos((width1-title.width())/2, posY);
			title.maxWidth(width1 - MARGIN * 2);
			add(title);

			posY = title.bottom() + 4*MARGIN;

			for (int terrain = 0; terrain < 40; terrain++) { //currently 40 kinds of terrain in total

				int finalTerrain = terrain;
				Image image;
				if (terrain == Terrain.WATER) {
					image = new Image(Dungeon.level.waterTex());
					image.scale.set(PixelScene.align(0.49f));
				} else image = DungeonTerrainTilemap.tile(Dungeon.level.width() + 1, finalTerrain);
				IconButton terrBtn = new IconButton(image){
					@Override
					protected void onClick() {
						super.onClick();
						pickaxe.chosenTerrain = finalTerrain;
						chosenText.text(Messages.titleCase(Dungeon.level.tileName(pickaxe.chosenTerrain)));
						chosenText.setPos((width1-chosenText.width())/2, chosenText.top());
					}

					@Override
					protected String hoverText() {
						return Dungeon.level.tileName(finalTerrain);
					}

				};

				add(terrBtn);
				if (posX + 16 > width1){
					posX = 0;
					posY += 17;
				}
				terrBtn.setRect(posX, posY, 16, 16);

				posX += terrBtn.width() + MARGIN;
			}

			chosenText = PixelScene.renderTextBlock(Messages.titleCase(Dungeon.level.tileName(pickaxe.chosenTerrain)), 9);
			chosenText.hardlight(TITLE_COLOR);
			chosenText.setPos((width1-chosenText.width())/2, posY + 19);
			chosenText.maxWidth(width1 - MARGIN * 2);
			add(chosenText);

			resize(width1, (int)chosenText.bottom() + 2 * MARGIN);
		}
	}

	private static final String TERRAIN = "terrain";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(TERRAIN, chosenTerrain);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		chosenTerrain = bundle.getInt(TERRAIN);
	}

}