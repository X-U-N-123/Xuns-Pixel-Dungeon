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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LifeLink;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.PowerOfMany;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

public class HolyChampion extends ClericSpell {

    public static HolyChampion INSTANCE = new HolyChampion();

    @Override
    public int icon() {
        return HeroIcon.HOLY_CHAMPION;
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", 20*(Dungeon.hero.pointsInTalent(Talent.HOLY_CHAMPION) +1)) +"\n\n"+ Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero)
        && hero.hasTalent(Talent.HOLY_CHAMPION)
        && (PowerOfMany.getPoweredAlly() != null || Stasis.getStasisAlly() != null);
    }

    @Override
    public float chargeUse(Hero hero) {
        return 3;
    }

    @Override
    public void onCast(HolyTome tome, Hero hero) {

        Char ally = PowerOfMany.getPoweredAlly();

        if (ally != null) {
            hero.sprite.zap(ally.pos);
        } else {
            ally = Stasis.getStasisAlly();
            hero.sprite.operate(hero.pos);
        }

        if (ally == Stasis.getStasisAlly()){
            LifeLink lifeLink = ally.buff(LifeLink.class);
            if (lifeLink != null) lifeLink.clearTime();
            ally.buff(LifeLinkSpell.LifeLinkSpellBuff.class).clearTime();
        }

        ChampionEnemy.GiveChampion((Mob)ally);
        Buff.affect(ally, HolyChampionTracker.class, 20*(hero.pointsInTalent(Talent.HOLY_CHAMPION)+1));

        onSpellCast(tome, hero);

    }

    public static class HolyChampionTracker extends FlavourBuff {
        @Override
        public void detach() {
            super.detach();
            for (ChampionEnemy buff : target.buffs(ChampionEnemy.class)){
                buff.detach();
            }
        }
    }

}