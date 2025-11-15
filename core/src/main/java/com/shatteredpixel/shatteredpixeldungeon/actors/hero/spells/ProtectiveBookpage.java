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
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
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
        int time = 10;
        if (Dungeon.hero.pointsInTalent(Talent.ENHANCED_BOOKPAGE) >= 3) time += 5;
        int dmg = Dungeon.hero.HT / 12;
        if (Dungeon.hero.pointsInTalent(Talent.ENHANCED_BOOKPAGE) >= 2) dmg = Dungeon.hero.HT / 10;
        int shieldAmt = Dungeon.hero.HT / 50;
        if (Dungeon.hero.pointsInTalent(Talent.ENHANCED_BOOKPAGE) >= 1) shieldAmt ++;
        return Messages.get(this, "desc", time, dmg, shieldAmt) +"\n\n"+ Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.subClass == HeroSubClass.PREACHER;
    }

    @Override
    public float chargeUse(Hero hero) {
        return 2;
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
        public int time = 10;
        private int bookpagePos = 0;

        @Override
        public int icon() {
            return BuffIndicator.BOOKPAGE;
        }

        @Override
        public String iconTextDisplay() {
            return Integer.toString(time);
        }

        @Override
        public void fx(boolean on) {
            if (on) target.sprite.aura( 0xAAAA00, 4);
            else target.sprite.clearAura();
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", pages, dispTurns(time));
        }

        public void addPages(){
            pages += 2;
            if (pages > 8) pages = 8;
            time = 10;
            if (((Hero)target).pointsInTalent(Talent.ENHANCED_BOOKPAGE) >= 3) time += 5;
        }

        @Override
        public boolean act(){
            //determine where the bookpage should attack
            ArrayList<Integer> pageCells = new ArrayList<>();

            pageCells.add(target.pos + PathFinder.CIRCLE8[bookpagePos]);
            switch (pages){
                case 8:
                    pageCells.add(target.pos + PathFinder.CIRCLE8[(bookpagePos+3)%8]);
                    pageCells.add(target.pos + PathFinder.CIRCLE8[(bookpagePos+7)%8]);
                case 6:
                    pageCells.add(target.pos + PathFinder.CIRCLE8[(bookpagePos+1)%8]);
                    pageCells.add(target.pos + PathFinder.CIRCLE8[(bookpagePos+5)%8]);
                case 4:
                    pageCells.add(target.pos + PathFinder.CIRCLE8[(bookpagePos+2)%8]);
                    pageCells.add(target.pos + PathFinder.CIRCLE8[(bookpagePos+6)%8]);
                case 2: default:
                    pageCells.add(target.pos + PathFinder.CIRCLE8[(bookpagePos+4)%8]);
            }

            for (int cell : pageCells){
                target.sprite.parent.add(
                    new Beam.SunRay(DungeonTilemap.raisedTileCenterToWorld(target.pos),
                        DungeonTilemap.raisedTileCenterToWorld(cell)));

                Char ch = Actor.findChar(cell);
                if (ch != null){
                    if (ch.alignment == Char.Alignment.ENEMY) {
                        ch.sprite.flash();
                        //only hero can get this buff, so no need to worry about it deals too less dmg
                        //the hero has 75 HT in lvl 12, has 120 HT in lvl 21
                        int dmg = target.HT/12;
                        if (((Hero)target).pointsInTalent(Talent.ENHANCED_BOOKPAGE) >= 2) dmg = target.HT/10;
                        ch.damage(dmg, this);
                    }
                } else {
                    //protect the preacher
                    Buff.affect(target, Barrier.class).incShield(target.HT/50);
                    if (((Hero)target).pointsInTalent(Talent.ENHANCED_BOOKPAGE) >= 1)
                        Buff.affect(target, Barrier.class).incShield(1);
                }
            }

            bookpagePos = (bookpagePos+1)%8;
            time --;
            if (time <= 0) detach();
            spend(TICK);
            return true;
        }

        private static final String PAGES = "pages";
        private static final String TIME  = "time";
        private static final String POS   = "pos";

        @Override
        public void storeInBundle( Bundle bundle ) {
            super.storeInBundle( bundle );
            bundle.put( PAGES, pages );
            bundle.put( TIME,  time );
            bundle.put( POS,   bookpagePos );
        }

        @Override
        public void restoreFromBundle( Bundle bundle ) {
            super.restoreFromBundle( bundle );
            pages = bundle.getInt(PAGES);
            time  = bundle.getInt(TIME);
            bookpagePos = bundle.getInt(POS);
        }
    }
}