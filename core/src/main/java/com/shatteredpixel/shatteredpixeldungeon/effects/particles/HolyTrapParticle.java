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

package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.ColorMath;
import com.watabou.utils.Random;

public class HolyTrapParticle extends PixelParticle {

    public static final Emitter.Factory FACTORY = new Emitter.Factory() {
        @Override
        public void emit( Emitter emitter, int index, float x, float y ) {
            ((HolyTrapParticle)emitter.recycle( HolyTrapParticle.class )).reset( x, y );
        }
    };

    public HolyTrapParticle() {
        super();

        color( 0xFFFF00 );
        lifespan = 2f;
        this.alpha(0.5f);
    }

    public void reset( float x, float y ) {
        revive();

        this.x = x;
        this.y = y;

        left = lifespan;

        this.y = Math.round((y - 8) /16)*16;
        this.x = Math.round((x - 8) /16)*16;
        int i = Random.Int(3);//0, 1, 2

        switch (Random.Int(4)){
            case 0:                           //left
                this.x += -i + 4;//4, 3, 2
                this.y += 2 * i + 4;//4, 6, 8
                break;
            case 1:                           //up
                this.x += 2 * i + 5;//5, 7, 9
                this.y += i + 2;//2, 3, 4
                break;
            case 2:                           //right
                this.x += -i + 11;//11, 10, 9
                this.y += 2 * i + 5;//5, 7, 9
                break;
            case 3:                           //down
                this.x += 2 * i + 4;//4, 6, 8
                this.y += i + 9;//9, 10, 11
                break;

        }
        if (Random.Float() < 0.5f) this.x ++;
        if (Random.Float() < 0.5f) this.y ++;

        color(ColorMath.random(0xFFFF00, 0xCCD311));
    }

}