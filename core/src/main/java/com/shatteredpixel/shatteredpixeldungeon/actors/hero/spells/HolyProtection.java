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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invulnerability;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.AscendedForm;
import com.shatteredpixel.shatteredpixeldungeon.effects.Enchanting;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.audio.Sample;

public class HolyProtection extends ClericSpell{

    public static final HolyProtection INSTANCE = new HolyProtection();

    @Override
    public int icon() {
        return HeroIcon.HOLY_PROTECTION;
    }

    @Override
    public float chargeUse(Hero hero) {
        return 3;
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero)
        && hero.hasTalent(Talent.HOLY_PROTECTION)
        && hero.buff(AscendedForm.AscendBuff.class) != null
        && !hero.buff(AscendedForm.AscendBuff.class).HolyProtectionCast;
    }

    @Override
    public void onCast(HolyTome tome, Hero hero) {

        Buff.affect(hero, Invulnerability.class, hero.pointsInTalent(Talent.HOLY_PROTECTION));
        Item.updateQuickslot();//1 turn less as it is instant

        Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
        SpellSprite.show( hero, SpellSprite.ANKH );

        hero.sprite.operate(hero.pos);
        if (hero.belongings.weapon() != null) Enchanting.show(hero, hero.belongings.weapon());
        hero.buff(AscendedForm.AscendBuff.class).HolyProtectionCast = true;

        onSpellCast(tome, hero);
    }

    @Override
    public String desc(){
        return Messages.get(this, "desc", 1+Dungeon.hero.pointsInTalent(Talent.HOLY_PROTECTION)) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
    }

}