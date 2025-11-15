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

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class HolyDrape extends ClericSpell {

    public static final HolyDrape INSTANCE = new HolyDrape();

    @Override
    public int icon() {
        return HeroIcon.HOLY_DRAPE;
    }

    @Override
    public String desc() {
        int quantity = 24 + 12 * Dungeon.hero.pointsInTalent(Talent.HOLY_DRAPE);
        return Messages.get(this, "desc", quantity) +"\n\n"+ Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.hasTalent(Talent.HOLY_DRAPE);
    }

    @Override
    public float chargeUse(Hero hero) {
        return 2;
    }

    @Override
    public void onCast(HolyTome tome, Hero hero) {
        int amount = 24 + 12 * Dungeon.hero.pointsInTalent(Talent.HOLY_DRAPE);
        ArrayList<Char> affectedChars = new ArrayList<>();

        for (Char ch : Dungeon.level.mobs.toArray(new Mob[0])) {
            if (hero.fieldOfView[ch.pos])
                if ((ch.alignment == Char.Alignment.ALLY && ch != hero)
                || ch.alignment == Char.Alignment.ENEMY) affectedChars.add(ch);
        }

        if (affectedChars.isEmpty()){
            GLog.w(Messages.get(ClericSpell.class, "no_target"));
            return;
        }

        hero.busy();
        float amountForOne = (float)amount / affectedChars.size();

        int temp = 0;
        float temp2 = 0;
        for (Char ch : affectedChars) {
            int realAmount = (int)amountForOne;
            temp += realAmount;
            temp2 += amountForOne;
            if (temp2 >= 1 + temp) {
                realAmount ++;
                temp ++;
            }

            if (ch.alignment == Char.Alignment.ALLY) {//heal allies
                int toHeal = Math.min(ch.HT - ch.HP, realAmount);
                ch.HP += toHeal;
                if (toHeal > 0) ch.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(toHeal), FloatingText.HEALING);
                if (realAmount > toHeal) {
                    Buff.affect(ch, Barrier.class).incShield(realAmount - toHeal);
                    ch.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(realAmount - toHeal), FloatingText.SHIELDING);
                }
            } else ch.damage(realAmount, HolyDrape.this); //damage enemies
        }

        WandOfBlastWave.BlastWave.blast(hero.pos, 4);
        Sample.INSTANCE.play(Assets.Sounds.SCAN);
        ((HeroSprite)hero.sprite).read();
        hero.spendAndNext(1f);
        onSpellCast(tome, hero);
    }

}