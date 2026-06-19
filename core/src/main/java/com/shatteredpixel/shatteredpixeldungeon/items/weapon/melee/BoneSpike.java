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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hex;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class BoneSpike extends MeleeWeapon {

    public static final String AC_PRICK = "prick";
    public static final Class<? extends FlavourBuff>[] usableDebuffs
        = new Class[]{Cripple.class, Weakness.class, Vulnerable.class, Blindness.class, Vertigo.class, Hex.class};

    {
        image = ItemSpriteSheet.BONE_SPIKE;
        hitSound = Assets.Sounds.HIT_STAB;
        hitSoundPitch = 1.1f;

        tier = 1;

        unique = true;
        bones = false;
    }

    @Override
    public int max(int lvl){
        return Math.round(3.5f * (tier+1)) +
                lvl*tier;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions( hero );
        if (hero.buff(PrickCooldown.class) == null) actions.add(AC_PRICK);
        return actions;
    }

    @Override
    public String defaultAction() {
        return Dungeon.hero.buff(PrickCooldown.class) == null ? AC_PRICK : super.defaultAction();
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (Math.min(damage, defender.HP) >= 4 && attacker.HP < attacker.HT){
            attacker.HP ++;
            attacker.sprite.emitter().start( Speck.factory( Speck.HEALING ), 0.33f, 2 );
        }
        return super.proc( attacker, defender, damage );
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_PRICK) && hero.buff(PrickCooldown.class) == null){
            ArrayList<Class<? extends FlavourBuff>> validDebuffs = new ArrayList<>();

            for (Class<? extends FlavourBuff> cls : usableDebuffs) if (hero.buff(cls) == null) validDebuffs.add(cls);
            Buff.affect(hero, Random.element(validDebuffs), 1f);
            Dungeon.observe();

            if (hero.hasTalent(Talent.BLURING_BODY) && hero.buff(SafePrickCooldown.class) == null){
                Buff.affect(hero, Invisibility.class, 2f);
                Buff.affect(hero, SafePrickCooldown.class, 99 - 20 * hero.pointsInTalent(Talent.BLURING_BODY));
            }

            if (hero.hasTalent(Talent.SAFE_PRICK))
                Buff.affect(hero, Barrier.class).setShield(1 + 2 * hero.pointsInTalent(Talent.SAFE_PRICK));

            Buff.affect(hero, PrickCooldown.class, 14);

            hero.sprite.operate( hero.pos );
            Sample.INSTANCE.play(Assets.Sounds.CURSED);
            hero.sprite.emitter().burst( ShadowParticle.CURSE, 3 );
        }
    }

    public static class SafePrickCooldown extends FlavourBuff {
        public int icon() { return BuffIndicator.TIME; }
        public void tintIcon(Image icon) { icon.hardlight(0.4f, 0.0f, 0.9f); }
    }

    public static class PrickCooldown extends FlavourBuff {
        public int icon() { return BuffIndicator.TIME; }
        public void tintIcon(Image icon) { icon.hardlight(0.6f, 0.0f, 0.6f); }
    }
}