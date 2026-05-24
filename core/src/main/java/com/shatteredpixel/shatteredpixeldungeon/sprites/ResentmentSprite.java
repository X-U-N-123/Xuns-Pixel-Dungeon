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

package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.watabou.noosa.TextureFilm;

public class ResentmentSprite extends MobSprite {

    public ResentmentSprite(){
        super();

        texture( Assets.Sprites.RESENT );

        TextureFilm frames = new TextureFilm( texture, 16, 16 );
        idle = new Animation( 7, true );
        idle.frames( frames, 0,1,2,3,4,5,6,7);

        run = new Animation( 9, true );
        run.frames( frames,  8,9,10,11,12,13);

        attack = new Animation( 11, false );
        attack.frames( frames, 14,15,16);

        die = new Animation( 9, false );
        die.frames( frames, 17,18,19,20,21);

        play(idle);
    }
}