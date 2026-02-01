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

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.TrapChoose;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.explorer.Sandstorm;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.LiquidMetal;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.TerrainFeaturesTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Reflection;

public class WndBuildTrap extends Window {

    private static final int MARGIN  = 2;

    public WndBuildTrap(TrapChoose choose){
        super();

        int width1 = 118;
        int height = (int)(PixelScene.uiCamera.height * 0.9);

        float posY = MARGIN;
        float posX = 0;

        RenderedTextBlock title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(TrapChoose.class, "action")), 9);
        title.hardlight(TITLE_COLOR);
        title.setPos((width1-title.width())/2, posY);
        title.maxWidth(width1 - MARGIN * 2);
        add(title);

        posY = title.bottom() + 3*MARGIN;

        for (Class<?> trapCls : Bestiary.TRAP.entities()) {
            if (!choose.TrapClasses().containsKey(trapCls)) continue;

            if (posX > 110){
                posX = 0;
                posY += 24;
            }

            Trap trap = Reflection.newInstance((Class<Trap>)trapCls);

            Image icon = TerrainFeaturesTilemap.getTrapVisual(trap);

            RedButton trapBtn = new RedButton("", 6){
                @Override
                protected void onClick() {
                    GameScene.show(new WndTrapConfirm(WndBuildTrap.this, choose, trap));
                }
            };

            add(trapBtn);
            trapBtn.icon(icon);
            trapBtn.leftJustify = true;
            trapBtn.setRect(posX, posY, 22, 22);

            posX += trapBtn.width() + MARGIN;
        }

        resize(width1, (int)Math.min(height, posY + 24));
    }

    public static class WndTrapConfirm extends WndTitledMessage {

        public WndTrapConfirm(Window parentWnd, TrapChoose choose, Trap trap){
            super(TerrainFeaturesTilemap.getTrapVisual(trap), Messages.titleCase(trap.name()), getText(trap, choose));

            String text = Messages.get(WndBuildTrap.class, "will_build", trap.name());

            RedButton btnConfirm = new RedButton(text){
                @Override
                protected void onClick() {
                    parentWnd.hide();
                    hide();
                    LiquidMetal metal = Dungeon.hero.belongings.getItem(LiquidMetal.class);
                    if (metal == null || metal.quantity() < choose.TrapClasses().get(trap.getClass())){
                        GLog.w(Messages.get(TrapChoose.class, "no_metal"));
                        return;
                    }

                    GameScene.selectCell(new CellSelector.Listener() {
                        @Override
                        public void onSelect(Integer cell) {
                            if (cell == null) return;

                            if (!Dungeon.level.adjacent(cell, Dungeon.hero.pos)){
                                GLog.w(Messages.get(WndBuildTrap.class, "far"));
                                return;
                            }

                            if ((Actor.findChar(cell) != null && Dungeon.hero.pointsInTalent(Talent.SIMPLE_STRUCTURE) < 2)
                            || (!Sandstorm.canDrift(Dungeon.level.map[cell])
                            && Dungeon.level.map[cell] != Terrain.HIGH_GRASS && Dungeon.level.map[cell] != Terrain.INACTIVE_TRAP)){
                                GLog.w(Messages.get(WndBuildTrap.class, "invalid_pos"));
                            } else {

                                Level.set(cell, Terrain.TRAP);
                                Dungeon.level.setTrap( trap, cell).reveal();//build a trap

                                if (metal.quantity() <= choose.TrapClasses().get(trap.getClass()))
                                    metal.detachAll(Dungeon.hero.belongings.backpack);
                                else {
                                    metal.quantity(metal.quantity() - choose.TrapClasses().get(trap.getClass()));
                                    Item.updateQuickslot();
                                }

                                GameScene.updateMap(cell);
                                Dungeon.observe();
                                Sample.INSTANCE.play( Assets.Sounds.UNLOCK );
                                choose.CD += 51; // 1 more turn as building the trap takes a turn
                                if (!Dungeon.hero.hasTalent(Talent.SIMPLE_STRUCTURE)) Dungeon.hero.spendAndNext(Actor.TICK);
                                Dungeon.hero.sprite.operate(cell);
                            }
                        }

                        @Override
                        public String prompt() {
                            return Messages.get(WndBuildTrap.class, "build", trap.name());
                        }
                    });
                }
            };
            btnConfirm.setRect(0, height+2, width, 16);
            add(btnConfirm);

            resize(width, (int)btnConfirm.bottom());

        }

        private static String getText(Trap trap, TrapChoose choose){
            return trap.desc() + "\n\n_"
                + Messages.get(WndBuildTrap.class, "lmcost", choose.TrapClasses().get(trap.getClass())) + "_";
        }

    }
}