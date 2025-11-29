package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.explorer;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

public class RockFall extends ArmorAbility {

    {
        baseChargeUse = 50;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target){}

    @Override
    public int icon() {
        return HeroIcon.ROCKFALL;
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.HEROIC_ENERGY};
    }

}