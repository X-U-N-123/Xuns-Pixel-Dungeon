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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArtifactRecharge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.audio.Sample;

public class SharedCharge extends ClericSpell{

    public static final SharedCharge INSTANCE = new SharedCharge();

    @Override
    public int icon() {
        return HeroIcon.SHARED_CHARGE;
    }

    @Override
    public float chargeUse(Hero hero) {
        return 2;
    }

    @Override
    public String desc(){
        return Messages.get(this, "desc", 2+Dungeon.hero.pointsInTalent(Talent.SHARED_CHARGE)) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.hasTalent(Talent.SHARED_CHARGE);
    }

    @Override
    public void onCast(HolyTome tome, Hero hero) {

        Buff.affect(hero, Recharging.class, 2+hero.pointsInTalent(Talent.SHARED_CHARGE));
        //2/3 turns of artifact recharging
        ArtifactRecharge recharge = Buff.affect(hero, ArtifactRecharge.class).extend(2 + hero.pointsInTalent(Talent.SHARED_CHARGE));
        recharge.ignoreHornOfPlenty = false;
        recharge.ignoreHolyTome = true;

        Sample.INSTANCE.play( Assets.Sounds.CHARGEUP );
        ScrollOfRecharging.charge(hero);

        onSpellCast(tome, hero);
        hero.sprite.operate(hero.pos);
    }

}