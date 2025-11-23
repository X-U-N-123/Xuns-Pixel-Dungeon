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
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;

import java.util.ArrayList;

public class ProtectiveBookpage extends ClericSpell {

    public static final ProtectiveBookpage INSTANCE = new ProtectiveBookpage();

    @Override
    public int icon() {
        return HeroIcon.BOOKPAGE;
    }

    @Override
    public String desc() {
        int pageTime = Dungeon.hero.pointsInTalent(Talent.ENHANCED_BOOKPAGE) >= 3 ? 10 : 8;
        int dmg = Dungeon.hero.lvl / 3;
        if (Dungeon.hero.pointsInTalent(Talent.ENHANCED_BOOKPAGE) >= 2) dmg = Dungeon.hero.lvl / 2;
        int shieldAmt = Dungeon.hero.pointsInTalent(Talent.ENHANCED_BOOKPAGE) >= 1 ? 2 : 1;
        return Messages.get(this, "desc", pageTime, dmg, shieldAmt) +"\n\n"+ Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.subClass == HeroSubClass.PREACHER;
    }

    @Override
    public void onCast(HolyTome tome, Hero hero) {

        Buff.affect(hero, Bookpage.class).addPages();
        //add 2 pages to Bookpage buff
        Sample.INSTANCE.play(Assets.Sounds.TELEPORT);
        onSpellCast(tome, hero);
    }

    public static class Bookpage extends Buff {

        {
            type = buffType.POSITIVE;
        }

        public int pages = 0;
        public int pageTime = 8;
        private int bookpagePos = 0;

        @Override
        public int icon() {
            return BuffIndicator.BOOKPAGE;
        }

        @Override
        public String iconTextDisplay() {
            return Integer.toString(pageTime);
        }

        @Override
        public void fx(boolean on) {
            if (on) target.sprite.aura( 0xAAAA00, 4);
            else target.sprite.clearAura();
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", pages, dispTurns(pageTime));
        }

        public void addPages(){
            pages ++;
            if (pages > 8) pages = 8;
            pageTime = 8;
            if (((Hero)target).pointsInTalent(Talent.ENHANCED_BOOKPAGE) >= 3) pageTime += 2;
            BuffIndicator.refreshHero();
        }

        @Override
        public boolean act(){
            //determine where the bookpage should attack
            ArrayList<Integer> pageCells = new ArrayList<>();

            int j = 0;
            while (j < pages){
                pageCells.add(target.pos + PathFinder.CIRCLE8[Math.round(8f*j/pages + bookpagePos)%8]);
                j ++;
            }

            for (int cell : pageCells){
                CellEmitter.center( cell ).burst( Speck.factory( Speck.PAGES ), 1 );

                Char ch = Actor.findChar(cell);
                if (ch != null){
                    if (ch.alignment == Char.Alignment.ENEMY) {
                        ch.sprite.flash();
                        //it deals 4/7/10 dmg in lvl 12/21/30
                        int dmg = ((Hero)target).lvl / 3;
                        if (((Hero)target).pointsInTalent(Talent.ENHANCED_BOOKPAGE) >= 2) dmg = ((Hero)target).lvl / 2;
                        ch.damage(dmg, this);
                    }
                } else {
                    //protect the preacher
                    Buff.affect(target, Barrier.class).incShield(1);
                    if (((Hero)target).pointsInTalent(Talent.ENHANCED_BOOKPAGE) >= 1)
                        Buff.affect(target, Barrier.class).incShield(1);
                }
            }

            bookpagePos = (bookpagePos+1)%8;
            pageTime --;
            if (pageTime <= 0) detach();
            spend(1f);
            return true;
        }

        private static final String PAGES    = "pages";
        private static final String PAGETIME = "pagetime";
        private static final String POS      = "pos";

        @Override
        public void storeInBundle( Bundle bundle ) {
            super.storeInBundle( bundle );
            bundle.put( PAGES, pages );
            bundle.put(PAGETIME, pageTime);
            bundle.put( POS,   bookpagePos );
        }

        @Override
        public void restoreFromBundle( Bundle bundle ) {
            super.restoreFromBundle( bundle );
            pages = bundle.getInt(PAGES);
            pageTime = bundle.getInt(PAGETIME);
            bookpagePos = bundle.getInt(POS);
        }
    }
}