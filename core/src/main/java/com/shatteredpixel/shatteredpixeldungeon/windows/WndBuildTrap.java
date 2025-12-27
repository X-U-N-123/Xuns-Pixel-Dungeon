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
import com.shatteredpixel.shatteredpixeldungeon.items.LiquidMetal;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Shovel;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.TerrainFeaturesTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.Reflection;

public class WndBuildTrap extends Window {

    private static final int MARGIN  = 1;

    public WndBuildTrap(TrapChoose choose){
        super();

        int width = (int)Math.min(PixelScene.uiCamera.width * 0.9, 300);
        int height = (int)(PixelScene.uiCamera.height * 0.9);

        float pos = MARGIN;

        RenderedTextBlock title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(TrapChoose.class, "action")), 9);
        title.hardlight(TITLE_COLOR);
        title.setPos((width-title.width())/2, pos);
        title.maxWidth(width - MARGIN * 2);
        add(title);

        pos = title.bottom() + 3*MARGIN;

        ScrollPane trapList = new ScrollPane( new Component() ){};

        for (Class<?> trapCls : Bestiary.TRAP.entities()) {
            if (!choose.TrapClasses().containsKey(trapCls)) continue;

            Trap trap = Reflection.newInstance((Class<Trap>)trapCls);

            String text = "_" + trap.name() + "_  " + trap.desc().replace("\n\n", "\n")
            + "\n_" + Messages.get(this, "lmcost") + choose.TrapClasses().get(trapCls) + "_";

            Image icon = TerrainFeaturesTilemap.getTrapVisual(trap);

            RedButton trapBtn = new RedButton(text, 6){
                @Override
                protected void onClick() {
                    super.onClick();
                    hide();
                    choose.trapClass = (Class<? extends Trap>) trapCls;
                    ActionIndicator.refresh();
                    if (Dungeon.hero.buff(Shovel.ExplorerCooldown.class) != null){
                        GLog.w(Messages.get(TrapChoose.class, "cd"));
                        return;
                    }
                    LiquidMetal metal = Dungeon.hero.belongings.getItem(LiquidMetal.class);
                    if (metal == null || metal.quantity() < choose.TrapClasses().get(trapCls)){
                        GLog.w(Messages.get(TrapChoose.class, "no_metal"));
                        return;
                    }

                    GameScene.selectCell(new CellSelector.Listener() {
                        @Override
                        public void onSelect(Integer cell) {
                            if (cell == null) return;

                            if (Dungeon.level.passable[cell] && Actor.findChar(cell) == null){
                                Level.set(cell, Terrain.TRAP);
                                Dungeon.level.setTrap( trap, cell).reveal();

                                if (metal.quantity() <= choose.TrapClasses().get(trapCls))
                                    metal.detachAll(Dungeon.hero.belongings.backpack);
                                else
                                    metal.quantity(metal.quantity() - choose.TrapClasses().get(trapCls));

                                GameScene.updateMap(cell);
                                Dungeon.observe();
                                Sample.INSTANCE.play( Assets.Sounds.UNLOCK );
                                Shovel.ExplorerCooldown.affectCD(50, Dungeon.hero);
                                Dungeon.hero.spendAndNext(Actor.TICK);
                                Dungeon.hero.sprite.operate(cell);
                            }
                        }

                        @Override
                        public String prompt() {
                            return Messages.get(this, "build", trap.name());
                        }
                    });
                }
            };
            trapBtn.icon(icon);
            trapBtn.leftJustify = true;
            trapBtn.multiline = true;
            trapBtn.setSize(width, trapBtn.reqHeight());
            trapBtn.setRect(0, pos, width, trapBtn.reqHeight());

            trapList.add(trapBtn);

            pos = trapBtn.bottom() + MARGIN;
        }

        add(trapList);
        trapList.content().setSize(width, pos);
        trapList.content().setRect(0, title.bottom() + 3*MARGIN, width, pos);
        trapList.setSize(width, pos - title.bottom() - 3*MARGIN);
        trapList.setRect(0, title.bottom(), width, pos - title.bottom() - 3*MARGIN);

        resize(width, (int)Math.min(height, trapList.height()));
    }
}
