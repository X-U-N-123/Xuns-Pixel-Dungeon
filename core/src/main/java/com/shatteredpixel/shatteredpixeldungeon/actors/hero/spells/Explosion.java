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

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.level;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.HolyBomb;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfSirensSong;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Callback;

public class Explosion extends TargetedClericSpell{

    public static Explosion INSTANCE = new Explosion();

    @Override
    public int icon() {
        return HeroIcon.EXPLOSION;
    }

    @Override
    public int targetingFlags() {
        return Ballistica.STOP_TARGET; //no auto-aim
    }

    @Override
    public String desc() {
        String desc = "迅";
        switch (Dungeon.hero.pointsInTalent(Talent.EXPLOSION)){
            case 1:
                desc = Messages.get(this, "desc_1");
                break;
            case 2:
                desc = Messages.get(this, "desc_2");
                break;
        }
        if (Dungeon.hero.pointsInTalent(Talent.EXPLOSION) > 2){
            desc = Messages.get(this, "desc_2");
            desc += Messages.get(this, "desc_immune");
        }
        desc += "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
        return desc;
    }

    @Override
    public float chargeUse(Hero hero) {
        return 3f;
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.hasTalent(Talent.EXPLOSION);
    }

    @Override
    protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {
        if (target == null) {
            return;
        }

        if (!level.heroFOV[target]) {
            GLog.w(Messages.get(this, "invalid_target"));
            return;
        }

        hero.sprite.attack(target, new Callback() {
            @Override
            public void call() {

                switch (hero.pointsInTalent(Talent.EXPLOSION)){
                    case 1:
                        new Bomb().explode(target);
                        break;
                    case 3:
                        Buff.affect(hero, Holyexplosionimmune.class, 0f);
                        for (Mob m : level.mobs.toArray(new Mob[0])){
                            if (m.alignment == Char.Alignment.ALLY || m.buff(ScrollOfSirensSong.Enthralled.class) != null){
                                Buff.affect(m, Holyexplosionimmune.class, 0f);
                            }
                        }
                    case 2:
                        new HolyBomb().explode(target);
                        break;
                }

                Invisibility.dispel();
                hero.spendAndNext(Actor.TICK);
                onSpellCast(tome, hero);
            }
        });

    }

    public static class Holyexplosionimmune extends FlavourBuff {}

}