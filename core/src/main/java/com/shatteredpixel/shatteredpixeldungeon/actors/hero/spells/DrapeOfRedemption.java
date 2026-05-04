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

import static com.shatteredpixel.shatteredpixeldungeon.Dungeon.hero;

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
import com.watabou.utils.Random;

import java.util.ArrayList;

public class DrapeOfRedemption extends ClericSpell {

    public static final DrapeOfRedemption INSTANCE = new DrapeOfRedemption();

    @Override
    public int icon() {
        return HeroIcon.HOLY_DRAPE;
    }

    @Override
    public String desc() {
        int max = 24 + 12 * hero.pointsInTalent(Talent.DRAPE_OF_REDEMPTION);

        int amount = 0;
        for (Char ch : Dungeon.level.mobs.toArray(new Mob[0]))
            if (hero.fieldOfView[ch.pos])
                if (ch.alignment != Char.Alignment.NEUTRAL && !(ch instanceof Hero)
                    && !ch.isInvulnerable(DrapeOfRedemption.class)) amount ++;

        int min = Math.round(max * amount / (float)(amount + 1));

        return Messages.get(this, "desc", min, max)
                + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(hero));
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.hasTalent(Talent.DRAPE_OF_REDEMPTION);
    }

    @Override
    public float chargeUse(Hero hero) {
        return 2;
    }

    @Override
    public void onCast(HolyTome tome, Hero hero) {
        float max = 24 + 12 * Dungeon.hero.pointsInTalent(Talent.DRAPE_OF_REDEMPTION);
        ArrayList<Char> affectedChars = new ArrayList<>();

        for (Char ch : Dungeon.level.mobs.toArray(new Mob[0]))
            if (hero.fieldOfView[ch.pos])
                if (ch.alignment != Char.Alignment.NEUTRAL && ch != hero
                    && !ch.isInvulnerable(DrapeOfRedemption.class)) affectedChars.add(ch);
        //either enemy, or ally

        float min = Math.round(max * affectedChars.size() / (float)(affectedChars.size() + 1));

        if (min == 0){
            GLog.w(Messages.get(ClericSpell.class, "no_target"));
            return;
        }

        hero.busy();

        int temp = 0;
        float temp2 = 0;
        min /= affectedChars.size();
        max /= affectedChars.size();
        for (Char ch : affectedChars) {

            float amountForOne = Random.NormalFloat(min, max);
            if (Char.hasProp(ch, Char.Property.DEMONIC) || Char.hasProp(ch, Char.Property.UNDEAD))
                amountForOne = max;

            int realAmount = Math.round(amountForOne);
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
                    Buff.affect(ch, Barrier.class).setShield(realAmount - toHeal);
                    ch.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(realAmount - toHeal), FloatingText.SHIELDING);
                }
            } else ch.damage(realAmount, DrapeOfRedemption.this); //damage enemies
        }

        WandOfBlastWave.BlastWave.blast(hero.pos, 4);
        Sample.INSTANCE.play(Assets.Sounds.SCAN);
        ((HeroSprite)hero.sprite).read();
        hero.spendAndNext(1f);
        onSpellCast(tome, hero);
    }
}