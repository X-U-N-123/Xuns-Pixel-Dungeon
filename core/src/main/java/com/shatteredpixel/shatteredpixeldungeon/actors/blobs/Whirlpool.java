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

package com.shatteredpixel.shatteredpixeldungeon.actors.blobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Piranha;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.WhirlpoolParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.PathFinder;

public class Whirlpool extends Blob {

	{
		alwaysVisible = true;
	}

    @Override
    protected void evolve() {

        int cell;

        for (int i = area.left; i < area.right; i++){
            for (int j = area.top; j < area.bottom; j++){
                cell = i + j*Dungeon.level.width();
                off[cell] = cur[cell] > 0 ? cur[cell] - 1 : 0;

                volume += off[cell];
            }
        }

        for (int k = 0; k < Dungeon.level.length(); k ++) {
            if (cur[k] <= 0) continue;

            if (!Dungeon.level.water[k]){
                clear(k);
                continue;
            }

            //throws other chars around the center.
            for (int i : PathFinder.NEIGHBOURS9){
                Char ch = Actor.findChar(k + i);

                if (ch == null) continue;

                if ((ch.isFlying() && Dungeon.hero.pointsInTalent(Talent.UNDERCURRENT) < 3)
                        || ch.alignment == Char.Alignment.ALLY || Char.hasProp(ch, Char.Property.IMMOVABLE))
                    continue;

                if (i == 0){
                    if (Dungeon.hero.hasTalent(Talent.DROWNING) && !(ch instanceof Piranha))
                        ch.damage(Math.round(Dungeon.hero.lvl * Dungeon.hero.pointsInTalent(Talent.DROWNING) / 3f), this);
                    continue;
                }

				Ballistica trajectory = new Ballistica(k + i, k, Ballistica.MAGIC_BOLT);
				WandOfBlastWave.throwChar(ch, trajectory, 1, false, true, this);

                switch (Dungeon.hero.pointsInTalent(Talent.UNDERCURRENT)){
                    case 3: case 2: Buff.prolong(ch, Cripple.class, 2);
                    case 1: Buff.prolong(ch, Roots.class, 1);
                    default: break;
                }
			}
        }
    }

    @Override
    public void use( BlobEmitter emitter ) {
        super.use( emitter );

        emitter.pour( WhirlpoolParticle.FACTORY, 0.05f );
    }

    @Override
    public String tileDesc() {
        String desc = Messages.get(this, "desc");
        int drowningDmg = Math.round(Dungeon.hero.lvl * Dungeon.hero.pointsInTalent(Talent.DROWNING) / 3f);
        if (drowningDmg > 0) desc += Messages.get(this, "desc_drowning", drowningDmg);
        return desc;
    }

}