/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * Pixel Towers / Towers Pixel Dungeon
 * Copyright (C) 2024-2025 FixAkaTheFix (initials R. A. A.)
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

package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.MissileTower;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class MissileTowerSprite extends MobSprite {

    private Animation ready;
    private Animation load;
    private Animation cast;

    public MissileTowerSprite() {
        super();

        texture( Assets.Sprites.MIS_TOWER);

        TextureFilm frames = new TextureFilm( texture, 13, 11 );

        idle = new MovieClip.Animation( 1, true );
        idle.frames( frames, 1);

        ready = new MovieClip.Animation( 1, true );
        ready.frames( frames, 0);

        load = new MovieClip.Animation( 5, false );
        load.frames( frames, 2, 3);

        run = idle.clone();

        attack = new MovieClip.Animation( 12, false );
        attack.frames( frames, 1, 2, 3, 0);

        cast = attack.clone();

        die = new MovieClip.Animation( 1, false );
        die.frames( frames, 1);

        play( idle );
    }

    @Override
    public void attack( int cell ) {
        turnTo( ch.pos , cell );
        play( cast );
        ((MissileSprite)parent.recycle( MissileSprite.class )).
                reset( this, cell, ((MissileTower)ch).mis(), () -> ch.onAttackComplete());
    }

    @Override
    public void onComplete( Animation anim ) {

        super.onComplete( anim );

        if (anim == load) play(ready);
    }

    public void load(int cell){
        turnTo(ch.pos, cell);
        play(load);
    }

    @Override
    public void showLost() {
        super.showLost();
        idle();
    }
}