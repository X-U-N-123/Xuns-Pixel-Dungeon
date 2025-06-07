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
        Buff.affect(attacker, DMcombo.class).hit();
        if (attacker.buff(DMcombo.class)!=null){
            ACC = (float)Math.pow(1.1f, attacker.buff(DMcombo.class).Getcount());
            damage = (int)(damage*Math.pow(1.1f, attacker.buff(DMcombo.class).Getcount()));
        }
        return super.proc( attacker, defender, damage );
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        hero.buff(DMcombo.class).Overload(2+buffedLvl());
    }

    @Override
    public String abilityInfo() {
        int Overloadtime = levelKnown ? 3 + buffedLvl() : 3;
        if (levelKnown){
            return Messages.get(this, "ability_desc", Overloadtime);
        } else {
            return Messages.get(this, "typical_ability_desc", Overloadtime);
        }
    }

    public String upgradeAbilityStat(int level){
        return String.valueOf(3+level);
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
                icon.hardlight(0.7f, 0, 0.8f);//Purple
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
            return Overloadtime>0 ;
        }

        protected int Getcount(){
            return count;
        }

        protected void hit(){
            count++;
            if (Overloadtime > 0){
                Time += 5f;
            } else if (Time <= 5){
                Time = 5f;
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
            return String.valueOf(count);
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
