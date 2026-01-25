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

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.ColorMath;
import com.watabou.utils.Random;

public class WhirlpoolParticle extends PixelParticle {

    public static final Emitter.Factory FACTORY = new Emitter.Factory() {
        @Override
        public void emit( Emitter emitter, int index, float x, float y ) {
            ((WhirlpoolParticle)emitter.recycle( WhirlpoolParticle.class )).reset( x, y );
        }
    };

    float offset = Random.Float((float) (2 * Math.PI));

    public WhirlpoolParticle() {
        super();

        scale.set(2f);
        lifespan = 1f;
        this.alpha(0.5f);
    }

    public void reset( float x, float y ) {
        revive();

        switch ((Dungeon.depth - 1)/5){ //0-4 for sewers - demon halls
            case 0:
                color(ColorMath.random(0x507B5D, 0x4E9B86));
                break;
            case 1:
                color(ColorMath.random(0x4C5D56, 0x5C8475));
                break;
            case 2:
                color(ColorMath.random(0x385551, 0x3F6862));
                break;
            case 3:
                color(ColorMath.random(0x572B2B, 0x6C3636));
                break;
            case 4: default:
                color(ColorMath.random(0xC63700, 0xD42400));
                break;
        }

        this.x = Math.round((x - 8) /16)*16 + 8 * (1 + (float) Math.sin(offset + Math.PI / 2));
        this.y = Math.round((y - 8) /16)*16 + 8 * (1 + (float) Math.cos(offset + Math.PI / 2));

        left = lifespan;
    }

    public void update(){
        super.update();

        float posFactor = left / lifespan;

        this.x += (float) (0.5f * posFactor * Math.sin(2f * Math.PI * posFactor + offset));
        this.y += (float) (0.5f * posFactor * Math.cos(2f * Math.PI * posFactor + offset));

    }
}