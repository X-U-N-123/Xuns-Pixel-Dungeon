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
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Piranha;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.WhirlpoolParticle;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

public class Whirlpool extends Blob {

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

            for (Mob m : Dungeon.level.mobs.toArray( new Mob[0])) {
                if ((m.isFlying() && Dungeon.hero.pointsInTalent(Talent.UNDERCURRENT) < 3)
						|| m.alignment == Char.Alignment.ALLY || m.properties().contains(Char.Property.IMMOVABLE))
					continue;

                if (m.pos == k){
                    if (Dungeon.hero.hasTalent(Talent.DROWNING) && !(m instanceof Piranha))
                        m.damage(Math.round(Dungeon.hero.lvl * Dungeon.hero.pointsInTalent(Talent.DROWNING) / 3f), this);
                    continue;
                }
                Ballistica trajectory = new Ballistica(m.pos, k, Ballistica.PROJECTILE);

                int maxDist = Dungeon.hero.hasTalent(Talent.UNDERCURRENT) ? 2 : 1;

                int dist = Math.min(Dungeon.level.distance(k, m.pos),
                Dungeon.hero.pointsInTalent(Talent.UNDERCURRENT) >= 2 ? 2 : 1);
                int pullPos = trajectory.path.get(dist);

                if (trajectory.collisionPos == k && Dungeon.level.distance(m.pos, k) <= maxDist
                && Dungeon.level.water[m.pos] && Actor.findChar(pullPos) == null
                && (!m.properties().contains(Char.Property.LARGE) || Dungeon.level.openSpace[pullPos])){
                    m.sprite.move(m.pos, pullPos);
                    m.pos = pullPos;
                    Dungeon.level.occupyCell(m);
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