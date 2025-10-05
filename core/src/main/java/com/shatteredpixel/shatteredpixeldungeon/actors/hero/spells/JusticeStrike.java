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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class JusticeStrike extends TargetedClericSpell{

    public static JusticeStrike INSTANCE = new JusticeStrike();

    @Override
    public int icon() {
        return HeroIcon.JUSTICE_STRIKE;
    }

    @Override
    public int targetingFlags() {
        return Ballistica.STOP_TARGET; //no auto-aim
    }

    @Override
    public String desc() {
        int point = 1 + Dungeon.hero.pointsInTalent(Talent.JUSTICE_STRIKE);
        return Messages.get(this, "desc",0.06f*point, 0.15f*point, Dungeon.hero.lvl/(8-point)) +"\n\n"+ Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
    }

    @Override
    public float chargeUse(Hero hero) {
        return 2f;
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.hasTalent(Talent.JUSTICE_STRIKE);
    }

    @Override
    protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {
        if (target == null) {
            return;
        }

        Char enemy = Actor.findChar(target);
        if (enemy == null || enemy == hero || enemy.alignment == Char.Alignment.ALLY){
            GLog.w(Messages.get(this, "no_target"));
            return;
        }

        JusticeStrikeBuff buff = Buff.affect(enemy, JusticeStrikeBuff.class);
        if (!Dungeon.level.heroFOV[target]) {
            GLog.w(Messages.get(this, "invalid_enemy"));
            buff.detach();
            return;
        }

        onSpellCast(tome, hero);
        hero.sprite.attack(enemy.pos);

    }

    public static class JusticeStrikeBuff extends Buff {
        @Override
        public int icon(){
            return BuffIndicator.JUSTICE_STRIKE;
        }
    }

}