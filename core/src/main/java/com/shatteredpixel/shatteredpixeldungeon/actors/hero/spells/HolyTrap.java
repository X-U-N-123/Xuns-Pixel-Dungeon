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
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.HolyTrapParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class HolyTrap extends TargetedClericSpell {

    public static HolyTrap INSTANCE = new HolyTrap();

    @Override
    public int icon() {
        return HeroIcon.HOLY_TRAP;
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.hasTalent(Talent.HOLY_TRAP);
    }

    @Override
    public int targetingFlags() {
        return -1; //no auto-aim
    }

    @Override
    public String desc() {
        String desc = Messages.get(this, "desc");
        switch (Dungeon.hero.pointsInTalent(Talent.HOLY_TRAP)){
            case 1: default:
                desc += Messages.get(this, "desc_1", 6);
                break;
            case 2:
                desc += Messages.get(this, "desc_2", 10, 4);
                break;
            case 3:
                desc += Messages.get(this, "desc_3", 10, 4, Dungeon.hero.lvl / 3);
                break;
        }
        return desc + "\n\n" + Messages.get(this, "stats_desc");
    }

    @Override
    protected void onTargetSelected(HolyTome tome, Hero hero, Integer target){
        if (target == null) return;

        if (!Dungeon.level.heroFOV[target]){
            GLog.w(Messages.get(this, "no_target"));
            return;
        }

        if (Dungeon.level.solid[target] || Dungeon.level.pit[target] || Actor.findChar(target) != null){
            GLog.w(Messages.get(this, "invalid_target"));
            return;
        }

        GameScene.add(Blob.seed(target, 1, HolyTrapBlob.class));

        onSpellCast(tome, hero);
        Sample.INSTANCE.play(Assets.Sounds.UNLOCK);
        hero.sprite.operate(target);
    }

    public static class HolyTrapBlob extends Blob {

        @Override
        protected void evolve() {
            int cell;
            for (int i=area.top-1; i <= area.bottom; i++) {
                for (int j = area.left-1; j <= area.right; j++) {
                    cell = j + i* Dungeon.level.width();
                    if (Dungeon.level.insideMap(cell)) {
                        off[cell] = cur[cell];

                        volume += off[cell];
                    }
                }
            }
        }

        //affects characters as they step on it. See Level.OccupyCell and Level.PressCell
        public static void affectChar( Char ch ){
            if (Dungeon.hero == null)
                return;

            if (Dungeon.hero.fieldOfView[ch.pos]) Sample.INSTANCE.play(Assets.Sounds.TRAP);

            switch (Dungeon.hero.pointsInTalent(Talent.HOLY_TRAP)){
                case 1:
                    Buff.prolong(ch, Cripple.class, 6f);
                    break;
                case 3:
                    Buff.affect(ch, Bleeding.class).set(Dungeon.hero.lvl /3f, HolyTrapBlob.class);
                case 2:
                    Buff.prolong(ch, Cripple.class, 10f);
                    Buff.prolong(ch, Roots.class, 4f);
                    break;
            }
        }

        @Override
        public void use( BlobEmitter emitter ) {
            super.use( emitter );

            emitter.pour( HolyTrapParticle.FACTORY, 0.01f );
        }

        @Override
        public String tileDesc() {
            return Messages.get(this, "desc");
        }

    }
}