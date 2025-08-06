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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShieldBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.AscendedForm;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class HolyRegeneration extends TargetedClericSpell{

    public static final HolyRegeneration INSTANCE = new HolyRegeneration();

    @Override
    public int icon() {
        return HeroIcon.HOLY_REGENERATION;
    }

    @Override
    public int targetingFlags(){
        return -1; //auto-targeting behaviour is often wrong, so we don't use it
    }

    @Override
    public float chargeUse(Hero hero) {
        return 1;
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero)
        && hero.hasTalent(Talent.HOLY_REGENERATION)
        && hero.buff(AscendedForm.AscendBuff.class) != null
        && !hero.buff(AscendedForm.AscendBuff.class).holyRegenerationCast;
    }

    @Override
    protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {
        if (target == null){
            return;
        }

        Char ch = Actor.findChar(target);
        if (ch == null || !Dungeon.level.heroFOV[target]){
            GLog.w(Messages.get(this, "no_target"));
            return;
        }

        Sample.INSTANCE.play(Assets.Sounds.TELEPORT);

        if (ch == hero || ch.alignment == Char.Alignment.ALLY){
            hero.busy();
            hero.sprite.operate(ch.pos);

            if (ch.shielding() > 0){
                int toHeal = Math.round(ch.shielding() * 0.2f*(1+hero.pointsInTalent(Talent.HOLY_REGENERATION)));
                toHeal = Math.min(toHeal, ch.HT - ch.HP);
                if (toHeal > 0){
                    ShieldBuff.processDamage(ch, toHeal, this);
                    ch.HP += toHeal;
                    ch.sprite.showStatusWithIcon(CharSprite.POSITIVE, String.valueOf(toHeal), FloatingText.HEALING);
                    hero.buff(AscendedForm.AscendBuff.class).holyRegenerationCast = true;
                } else return;
            } else {
                GLog.w(Messages.get(this, "invalid_target"));
                return;
            }
        } else {
            GLog.w(Messages.get(this, "invalid_enemy"));
            return;
        }

        onSpellCast(tome, hero);
    }

    @Override
    public String desc(){
        return Messages.get(this, "desc", 20*(1+Dungeon.hero.pointsInTalent(Talent.HOLY_REGENERATION)) ) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
    }

}