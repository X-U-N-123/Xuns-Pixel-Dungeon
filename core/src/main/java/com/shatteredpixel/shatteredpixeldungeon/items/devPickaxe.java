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
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WaterOfAwareness;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WaterOfHealth;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WellWater;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Wandmaker;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTerrainTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTextInput;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PointF;

import java.util.ArrayList;

public class devPickaxe extends Item {

	private static final String AC_SET   = "set";
	private static final String AC_MINE  = "mine";
	private static final String AC_RESET = "reset";
	private static final String AC_HEWELL = "hewell";
	private static final String AC_AWWELL = "awwell";

	private int radius = 1;
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
		actions.add(AC_RESET);
		actions.add(AC_HEWELL);
		actions.add(AC_AWWELL);
		actions.add(AC_SET);
		actions.add(AC_MINE);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {

		super.execute(hero, action);
		if (action.equals(AC_SET)) GameScene.show(new WndSelectTerrain(this));
		if (action.equals(AC_MINE) || action.equals(AC_HEWELL) || action.equals(AC_AWWELL)){
			GameScene.selectCell(new CellSelector.Listener() {
				@Override
				public void onSelect(Integer cell) {
					if (cell == null) return;
					if (!Dungeon.level.insideMap(cell)){
						GLog.w(Messages.get(this, "oom"));
						return;
					}
					if (action.equals(AC_HEWELL) || action.equals(AC_AWWELL)){
						if (Dungeon.level.map[cell] == Terrain.WELL || Dungeon.level.map[cell] == Terrain.EMPTY_WELL){
							if (Dungeon.level.map[cell] == Terrain.EMPTY_WELL){
								Level.set(cell, Terrain.WELL);
								GameScene.updateMap(cell);
							}
							Splash.at( DungeonTilemap.tileCenterToWorld( cell ), -PointF.PI/2, PointF.PI/2, 0x5bc1e3, 10, 0.01f);
							Sample.INSTANCE.play(Assets.Sounds.GAS, 1f, 0.8f);

							if (defaultAction.equals(AC_HEWELL)) WellWater.seed(cell, 1, WaterOfHealth.class, Dungeon.level);
							else                                 WellWater.seed(cell, 1, WaterOfAwareness.class, Dungeon.level);
						} else GLog.w(Messages.get(this, "no_well"));

						return;
					}
					for (int i = 1-radius; i < radius; i++) {
						for (int j = 1-radius; j < radius; j++) {
							if (Dungeon.level.insideMap(cell + i*Dungeon.level.width() + j)){
								Level.set(cell + i*Dungeon.level.width() + j, chosenTerrain);
								GameScene.updateMap(cell + i*Dungeon.level.width() + j);
							}
						}
					}
					curUser.sprite.attack(cell);
					Sample.INSTANCE.play(Assets.Sounds.MINE);

					Dungeon.level.buildFlagMaps();
					Dungeon.observe();
					AttackIndicator.updateState();
				}

				@Override
				public String prompt() {
					return Messages.get(this, "prompt");
				}
			});
			defaultAction = action;
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

	@Override
	public boolean isIdentified() {
		return true;
	}

	@Override
	public boolean isUpgradable() {
		return false;
	}

	@Override
	public float weight(){
		return 0;
	}

	private static class WndSelectTerrain extends Window {

		private static final int MARGIN = 1;
		RenderedTextBlock chosenText;

		public WndSelectTerrain(devPickaxe pickaxe){
			super();

			int WIDTH = 120;

			float posY = 2 * MARGIN;
			float posX = 1;

			RenderedTextBlock title =
					PixelScene.renderTextBlock(Messages.titleCase(Messages.get(this, "choose_terrain")), 9);
			title.hardlight(TITLE_COLOR);
			title.setPos((WIDTH-title.width())/2, posY);
			title.maxWidth(WIDTH - MARGIN * 2);
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
					}

					@Override
					protected String hoverText() {
						return Dungeon.level.tileName(finalTerrain);
					}

				};

				add(terrBtn);
				if (posX + 16 > WIDTH){
					posX = 1;
					posY += 17;
				}
				terrBtn.setRect(posX, posY, 16, 16);

				posX += terrBtn.width() + MARGIN;
			}

			RedButton radiusButton = new RedButton(Messages.get(this, "radius_button", pickaxe.radius)) {
				@Override
				protected void onClick() {
					Game.runOnRenderThread(() ->GameScene.show(new WndTextInput(
							Messages.get(WndSelectTerrain.class, "title"), Messages.get(WndSelectTerrain.class, "desc"),
							Integer.toString(pickaxe.radius),
							Short.MAX_VALUE, false, Messages.get(WndSelectTerrain.class, "confirm"),
							Messages.get(WndSelectTerrain.class, "cancel")) {
						@Override
						public void onSelect(boolean check, String text) {
							if (check && text.matches("\\d+")) {
								pickaxe.radius = Math.min(Integer.parseInt(text), 30);
								text(Messages.get(WndSelectTerrain.class, "radius_button", pickaxe.radius));
								AttackIndicator.updateState();
								Dungeon.observe();
							}
						}
					}));
					super.onClick();
				}
			};
			radiusButton.setSize(50, 14);
			radiusButton.setPos(WIDTH - radiusButton.width(), posY + 17);
			add(radiusButton);

			chosenText = PixelScene.renderTextBlock(Messages.titleCase(Dungeon.level.tileName(pickaxe.chosenTerrain)), 9);
			chosenText.hardlight(TITLE_COLOR);
			chosenText.setPos(0, radiusButton.top() + 7 - chosenText.height()/2);
			chosenText.maxWidth(WIDTH - MARGIN * 2);
			add(chosenText);

			resize(WIDTH, (int)radiusButton.bottom() + 2 * MARGIN);
		}
	}

	private static final String TERRAIN = "terrain";
	private static final String RADIUS  = "radius";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(TERRAIN, chosenTerrain);
		bundle.put(RADIUS, radius);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		chosenTerrain = bundle.getInt(TERRAIN);
		radius = bundle.getInt(RADIUS);
	}
}