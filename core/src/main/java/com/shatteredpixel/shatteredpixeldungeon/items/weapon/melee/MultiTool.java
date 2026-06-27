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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.PhysicalEmpower;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.MetalPart;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class MultiTool extends MeleeWeapon {

    public static final String AC_DISARM = "disarm";
    public static final String AC_APART  = "apart";

    {
        image = ItemSpriteSheet.TOOL;
        hitSound = Assets.Sounds.HIT_STAB;
        hitSoundPitch = 1.1f;

        tier = 1;

        unique = true;
        bones = false;
    }

    @Override
    public int max(int lvl) {
        return  Math.round(2.5f*(tier+1)) +    //5 base, down from 10
                lvl*(tier-1);                  //+1 scaling, down from +2
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (defender.buff(Knife.Cutabilitytracker.class) == null)
            Buff.affect(defender, Bleeding.class).set( augment.damageFactor((min() + 1) * Random.NormalFloat(1, 1.5f)) );

        return super.proc( attacker, defender, damage );
    }

    @Override
    public String statsInfo(){
        if (isIdentified()){
            return Messages.get(this, "stats_desc",
                    Math.round(augment.damageFactor(min() + 1)),
                    Math.round(augment.damageFactor((min() + 1) * 1.5f)));
        } else {
            return Messages.get(this, "typical_stats_desc",
                    Math.round(augment.damageFactor(min(0) + 1)),
                    Math.round(augment.damageFactor((min(0) + 1) * 1.5f)));
        }
    }

    @Override
    public String upgradeStat(int level){
        return Math.round(augment.damageFactor(min() + 1)) + "-" +
                Math.round(augment.damageFactor((min() + 1) * 1.5f));
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions( hero );
        actions.add(AC_DISARM);
        return actions;
    }

    @Override
    public String defaultAction() {
        return AC_DISARM;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_DISARM)){
            if (Dungeon.level.map[hero.pos] == Terrain.INACTIVE_TRAP){
                Level.set(hero.pos, Terrain.EMPTY);
                GameScene.updateMap( hero.pos );

                MetalPart part = new MetalPart();
                if (hero.subClass == HeroSubClass.CRAFTSMAN) part.quantity(part.quantity() + 1);

                if (!part.collect()) Dungeon.level.drop(part, hero.pos).sprite.drop();

                hero.sprite.operate( hero.pos );
                Sample.INSTANCE.play(Assets.Sounds.UNLOCK);
                hero.spendAndNext(2f);

                if (hero.hasTalent(Talent.GENERAL_DISARM))
                    Buff.affect(hero, PhysicalEmpower.class).set(1 + hero.pointsInTalent(Talent.GENERAL_DISARM), 3);

            } else GLog.w(Messages.get(this, "no_trap"));
        }
        if (action.equals(AC_APART)){

        }
    }
}