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

package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class DMdrill extends MeleeWeapon {

    {
        image = ItemSpriteSheet.DMdrill;
        hitSound = Assets.Sounds.HIT_STAB;
        hitSoundPitch = 0.8f;

        tier = 5;
    }

    @Override
    public int max(int lvl) {
        return 5 * tier +     //25 base, down from 30
                lvl * tier;  //+5 per level, down from +6
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        Buff.affect(attacker, DMcombo.class).hit(4 + level());
        DMcombo dmcombo = attacker.buff(DMcombo.class);
        if (dmcombo != null){
            damage = (int)(damage * Math.pow(1.1f, attacker.buff(DMcombo.class).Getcount()));
        }
        return super.proc( attacker, defender, damage );
    }

    @Override
    public float accuracyFactor(Char owner, Char target) {
        DMcombo dmcombo = owner.buff(DMcombo.class);
        if (dmcombo != null) {
            return (float)Math.pow(1.1f, dmcombo.Getcount());
        } else {
            return super.accuracyFactor(owner, target);
        }
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {

        beforeAbilityUsed(hero, null);
        Buff.affect(hero, DMcombo.class).Overload(4+buffedLvl());
        hero.sprite.operate(hero.pos);

        afterAbilityUsed(hero);
    }

    @Override
    public String abilityInfo() {
        int Overloadtime = levelKnown ? 4 + buffedLvl() : 4;
        if (levelKnown){
            return Messages.get(this, "ability_desc", Overloadtime);
        } else {
            return Messages.get(this, "typical_ability_desc", Overloadtime);
        }
    }

    public String upgradeAbilityStat(int level){
        return String.valueOf(4+level);
    }

    public static class DMcombo extends Buff {

        {
            type = buffType.POSITIVE;
        }

        private int count = 0;
        private float Time = 5f;
        private float Overloadtime = 0f;

        @Override
        public int icon() {
            return BuffIndicator.DMcombo;
        }

        @Override
        public void tintIcon(Image icon) {
            if (Overloadtime > 0){
                icon.hardlight(0.8f, 0, 0.9f);//Purple
            } else {
                switch (count){
                    case 0://White
                    icon.hardlight(1, 1, 1);
                    break;
                    case 1://Blue
                    case 2:
                    case 3:
                    case 4:
                    case 5://Green
                        icon.hardlight(0, (count-1)/4f, (5-count)/4f);
                        break;
                    case 6:
                    case 7:
                    case 8:
                    case 9://Yellow
                        icon.hardlight((count-5)/4f, 1, 0);
                        break;
                    case 10:
                    case 11:
                    case 12:
                        icon.hardlight(1, (13-count)/4f, 0);
                        break;
                    default://Red
                        icon.hardlight(1, 0, 0);
                        break;
                }
            }
        }

        private static final String COUNT = "count";
        private static final String TIME = "time";
        private static final String OVERLOADTIME = "overloadtime";


        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(COUNT, count);
            bundle.put(TIME, Time);
            bundle.put(OVERLOADTIME, Overloadtime);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            count = bundle.getInt(COUNT);
            Time = bundle.getFloat(TIME);
            Overloadtime = bundle.getFloat(OVERLOADTIME);
        }

        public boolean isOverloading(){
            return Overloadtime > 0;
        }

        protected int Getcount(){
            return count;
        }

        protected void hit(int time){
            count++;
            if (Overloadtime > 0){
                Time += time;
            } else if (Time <= 5){
                Time = time;
            }
            BuffIndicator.refreshHero();
        }

        protected void Overload(float Duration){
            Overloadtime = Duration;
            Time = Math.max(Duration, Time);
            Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
            BuffIndicator.refreshHero();
        }

        @Override
        public boolean act() {
            Time-=TICK;
            Overloadtime = Math.max(Overloadtime-TICK, 0f);
            BuffIndicator.refreshHero();
            spend(TICK);
            if (Time <= 0) {
                detach();
            }
            return true;
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0, (5f-Time) / 5f);
        }

        @Override
        public String iconTextDisplay() {
            return String.valueOf(Time);
        }

        @Override
        public String name() {
            if (Overloadtime > 0){
                return Messages.get(this, "overload_name");
            } else {
                return Messages.get(this, "name");
            }
        }

        @Override
        public String desc() {
            if (Overloadtime > 0){
                return Messages.get(this, "overload_desc", count, Time, Overloadtime);
            } else {
                return Messages.get(this, "desc", count, Time);
            }
        }
    }
}
