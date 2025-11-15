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

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class HolyAntimagic extends TargetedClericSpell {

    public static HolyAntimagic INSTANCE = new HolyAntimagic();

    @Override
    public int icon() {
        return HeroIcon.ANTIMAGIC;
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.hasTalent(Talent.HOLY_ANTIMAGIC);
    }

    @Override
    public int targetingFlags() {
        return -1; //no auto-aim
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", 2+Dungeon.hero.pointsInTalent(Talent.HOLY_ANTIMAGIC)) +"\n\n"+ Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
    }

    @Override
    protected void onTargetSelected(HolyTome tome, Hero hero, Integer target){
        if (target == null) {
            return;
        }

        Char ch = Actor.findChar(target);
        if (ch == null || !Dungeon.level.heroFOV[target]){
            GLog.w(Messages.get(this, "no_target"));
            return;
        }

        Buff.affect(ch, MagicImmune.class, 1 + hero.pointsInTalent(Talent.HOLY_ANTIMAGIC));//1 turn less as this is instant
        CellEmitter.get(target).start(Speck.factory(Speck.LIGHT), 0.2f, 3);

        onSpellCast(tome, hero);
        hero.sprite.attack(target);
    }

}