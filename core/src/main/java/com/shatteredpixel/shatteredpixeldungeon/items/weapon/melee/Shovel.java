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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class Shovel extends MeleeWeapon{

    public static final String AC_BUILD = "build";

    {
        image = ItemSpriteSheet.SHOVEL;
        hitSound = Assets.Sounds.HIT_SLASH;
        hitSoundPitch = 1.2f;

        tier = 1;
        DLY = 0.8f; //1.25x speed

        defaultAction = AC_BUILD;
        usesTargeting = false;

        unique = true;
        bones = false;
    }

    @Override
    public ArrayList<String> actions(Hero hero ) {
        ArrayList<String> actions = super.actions( hero );
        actions.add(AC_BUILD);
        return actions;
    }

    @Override
    public int max(int lvl) {
        return  4*(tier+1) +    //8 base, down from 10
                lvl*(tier+1);   //scaling unchanged
    }

    @Override
    public void execute( Hero hero, String action ) {

        super.execute(hero, action);

        if (action.equals(AC_BUILD)){
            GameScene.selectCell(new CellSelector.Listener() {
                @Override public String prompt() {
                    return Messages.get(this, "build");
                }

                @Override public void onSelect(Integer cell) {
                    if (cell == null)return;
                    if (!Dungeon.level.adjacent(cell, Dungeon.hero.pos) || !Dungeon.level.heroFOV[cell] || Actor.findChar(cell) != null) {
                        GLog.w(Messages.get(this, "reach"));
                        return;
                    }

                    if (Dungeon.level.map[cell] == Terrain.BARRICADE){
                        Level.set( cell, Terrain.EMPTY );
                        Sample.INSTANCE.play( Assets.Sounds.BUILD );
                        GameScene.updateMap(cell);
                        Dungeon.observe();
                        curUser.sprite.turnTo( curUser.pos, cell);
                        curUser.sprite.zap(cell);
                        hero.spendAndNext(Actor.TICK);
                        return;
                    }

                    if (curUser.buff(BarricadeCooldown.class) == null
                    &&(Dungeon.level.map[cell] == Terrain.EMPTY || Dungeon.level.map[cell] == Terrain.EMPTY_DECO || Dungeon.level.map[cell] == Terrain.EMPTY_SP
                    || Dungeon.level.map[cell] == Terrain.EMBERS || Dungeon.level.map[cell] == Terrain.WATER
                    || Dungeon.level.map[cell] == Terrain.FURROWED_GRASS || Dungeon.level.map[cell] == Terrain.GRASS || Dungeon.level.map[cell] == Terrain.HIGH_GRASS
                    || Dungeon.level.map[cell] == Terrain.INACTIVE_TRAP)){
                        Level.set( cell, Terrain.BARRICADE );

                        Sample.INSTANCE.play( Assets.Sounds.BUILD );
                        Buff.affect(curUser, BarricadeCooldown.class, 15f);
                        hero.spendConstant(Actor.TICK);
                        GameScene.updateMap(cell);
                        Dungeon.observe();
                        curUser.sprite.turnTo( curUser.pos, cell);
                        curUser.sprite.zap(cell);
                        hero.next();
                    } else if (curUser.buff(BarricadeCooldown.class) != null){
                        GLog.w(Messages.get(this, "cd"));
                    } else {
                        GLog.w(Messages.get(this, "hard"));
                    }
                }
            });
            defaultAction = AC_BUILD;
        }
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        beforeAbilityUsed(hero, null);
        //1 turn less as using the ability is instant
        Buff.prolong(hero, Scimitar.SwordDance.class, 4+buffedLvl());
        hero.sprite.operate(hero.pos);
        defaultAction = AC_ABILITY;
        hero.next();
        afterAbilityUsed(hero);
    }

    @Override
    public String abilityInfo() {
        if (levelKnown){
            return Messages.get(this, "ability_desc", 5+buffedLvl());
        } else {
            return Messages.get(this, "typical_ability_desc", 5);
        }
    }

    @Override
    public String upgradeAbilityStat(int level) {
        return Integer.toString(5+level);
    }

    public static class BarricadeCooldown extends FlavourBuff {
        public int icon() { return BuffIndicator.TIME; }
        public void tintIcon(Image icon) { icon.hardlight(0.5f, 0.3f, 0f); }
        public float iconFadePercent() { return Math.max(0, visualcooldown() / 15); }
    };

}