/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * Xun's Pixel Dungeon
 * Copyright (C) 2025-2025 Jiarun Chen
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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Awareness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Foresight;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSight;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfFeatherFall;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Game;

import java.util.ArrayList;

public class Goldarrow extends Item {

	private static final String AC_TELEPORT = "teleport";
	private static final String AC_RETURN   = "return";
	private static final String AC_AWARE    = "aware";
	private static final String AC_GOTO     = "goto";
	private static final String AC_TARGET   = "target";

    {
        defaultAction = AC_GOTO;
        image = ItemSpriteSheet.GOLDARROW;
        cursedKnown = levelKnown = true;
        unique = true;
        bones = false;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_TELEPORT);
        actions.add(AC_RETURN);
        actions.add(AC_AWARE);
        actions.add(AC_GOTO);
        actions.add(AC_TARGET);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {

        super.execute(hero, action);
        if (action.equals(AC_TELEPORT)) {
            Buff.affect(hero, ElixirOfFeatherFall.FeatherBuff.class, 10f);
            Chasm.heroFall(hero.pos);
            defaultAction = AC_TELEPORT;
        }
        if (action.equals(AC_RETURN)) {
            InterlevelScene.mode = InterlevelScene.Mode.RETURN;
            InterlevelScene.returnDepth = Math.max(1, (Dungeon.depth - 1));
            InterlevelScene.returnBranch = 0;
            InterlevelScene.returnPos = -2;
            Game.switchScene( InterlevelScene.class );
            defaultAction = AC_RETURN;
        }
        if (action.equals(AC_AWARE)) {
            int length = Dungeon.level.length();
            int[] map = Dungeon.level.map;
            boolean[] mapped = Dungeon.level.mapped;
            boolean[] discoverable = Dungeon.level.discoverable;
            for (int i=0; i < length; i++) {

                int terr = map[i];

                if (discoverable[i]) {

                    mapped[i] = true;
                    if ((Terrain.flags[terr] & Terrain.SECRET) != 0) {

                        Dungeon.level.discover( i );
                    }
                }
            }

            Class<? extends FlavourBuff>[] buffs = new Class[]{Awareness.class, MindVision.class, MagicalSight.class, Foresight.class};
            for (Class<? extends FlavourBuff> buffCls : buffs){
                if (curUser.buff(buffCls) != null) curUser.buff(buffCls).detach();
                else                               Buff.prolong(curUser, buffCls, Short.MAX_VALUE);
            }
            Dungeon.observe();
            Dungeon.hero.checkVisibleMobs();
            BuffIndicator.refreshHero();
			AttackIndicator.updateState();
            defaultAction = AC_AWARE;
        }
        if (action.equals(AC_GOTO)){
            GameScene.selectCell(new CellSelector.Listener() {
                @Override public String prompt() {
                    return Messages.get(Goldarrow.class, "where");
                }
                @Override public void onSelect(Integer cell) {
                    if (cell == null) return;
                    ScrollOfTeleportation.appear(curUser, cell);

                    hero.next();
                }
            });
            defaultAction = AC_GOTO;
        }
        if (action.equals(AC_TARGET)){
			GameScene.show(new TargetWindow());
            defaultAction = AC_TARGET;
        }
        GameScene.updateFog();
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

	public static class TargetWindow extends Window {
		private static final int GAP = 2;
		private static int projectileProp = 0;
		private static final boolean[] propList = new boolean[]{false, false, false, false};

		public TargetWindow(){
			int WIDTH = PixelScene.landscape() ? 180 : 120;

			RenderedTextBlock title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(this, "title")), 9);
			title.hardlight(TITLE_COLOR);
			title.setPos((WIDTH-title.width())/2, GAP);
			title.maxWidth(WIDTH - GAP * 2);
			add(title);

			RenderedTextBlock desc = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(this, "desc")), 6);
			desc.maxWidth(WIDTH);
			desc.setPos(0, title.bottom() + 3);
			add(desc);

			CheckBox stopTargetBox = new CheckBox(Messages.titleCase(Messages.get(this, "stop_target"))) {
				@Override
				protected void onClick() {
					super.onClick();
					propList[0] = checked();
				}
			};
			stopTargetBox.setRect(0, desc.bottom() + GAP, WIDTH, 16);
			stopTargetBox.checked(propList[0]);
			add(stopTargetBox);

			CheckBox stopCharsBox = new CheckBox(Messages.titleCase(Messages.get(this, "stop_chars"))) {
				@Override
				protected void onClick() {
					super.onClick();
					propList[1] = checked();
				}
			};
			stopCharsBox.setRect(0, stopTargetBox.bottom() + GAP, WIDTH, 16);
			stopCharsBox.checked(propList[1]);
			add(stopCharsBox);

			CheckBox stopSolidBox = new CheckBox(Messages.titleCase(Messages.get(this, "stop_solid"))) {
				@Override
				protected void onClick() {
					super.onClick();
					propList[2] = checked();
				}
			};
			stopSolidBox.setRect(0, stopCharsBox.bottom() + GAP, WIDTH, 16);
			stopSolidBox.checked(propList[2]);
			add(stopSolidBox);

			CheckBox ignoreSoftSolidBox = new CheckBox(Messages.titleCase(Messages.get(this, "ignore_soft_solid"))) {
				@Override
				protected void onClick() {
					super.onClick();
					propList[3] = checked();
				}
			};
			ignoreSoftSolidBox.setRect(0, stopSolidBox.bottom() + GAP, WIDTH, 16);
			ignoreSoftSolidBox.checked(propList[3]);
			add(ignoreSoftSolidBox);

			RedButton targetButton = new RedButton(Messages.get(Goldarrow.class, "ac_target")) {
				@Override
				protected void onClick() {
					hide();
					projectileProp = 0;
					for (int i = 0; i < propList.length; i++){
						if (propList[i]) projectileProp = projectileProp | (int)Math.pow(2, i);
					}
					GameScene.selectCell(new CellSelector.Listener() {
						@Override public String prompt() {
							return Messages.get(TargetWindow.class, "target");
						}
						@Override public void onSelect(Integer cell) {
							if (cell == null) return;
							Ballistica trajectory = new Ballistica(curUser.pos, cell, projectileProp);
							for (int i : trajectory.path){
								if (i == trajectory.collisionPos)
									 curUser.sprite.parent.addToFront(new TargetedCell(i, Window.XUN_COLOR));
								else curUser.sprite.parent.addToFront(new TargetedCell(i, Window.TITLE_COLOR));
							}
						}
					});
				}
			};
			targetButton.setRect(0, ignoreSoftSolidBox.bottom() + GAP, WIDTH, 16);
			add(targetButton);

			resize(WIDTH, (int)targetButton.bottom() + GAP);
		}
	}
}