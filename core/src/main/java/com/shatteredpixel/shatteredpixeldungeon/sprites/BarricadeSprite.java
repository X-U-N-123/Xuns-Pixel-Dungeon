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
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class BarricadeSprite extends MobSprite {

    private Animation cracked;//new anim
    private Animation broken;

    public BarricadeSprite() {
        super();

        texture(Assets.Sprites.BARRICADE);

        TextureFilm frames = new TextureFilm(texture, 14, 14);

        idle = new MovieClip.Animation(1, true);
        idle.frames(frames, 0);

        attack = idle.clone();

        run = idle.clone();

        cracked = new Animation(1, true);
        cracked.frames(frames,1);

        broken = new Animation(1, true);
        broken.frames(frames,2);

        die = broken.clone();

        play(idle);
    }

    public void cracked(){//methods for the new anim
        play(cracked);
    }

    public void broken(){
        play(broken);
    }

    @Override
    public void idle() {
        if (ch == null) play (idle);
        else linkVisuals(ch);
    }

    //walls show no emotions
    @Override
    public void showAlert() {}
    @Override
    public void showLost() {}
    @Override
    public void showSleep() {}

    @Override
    public void linkVisuals(Char ch) {
        super.linkVisuals(ch);
        if (ch.HP < ch.HT /3f) {
            broken();
        } else if (ch.HP < ch.HT *2/3f) {
            cracked();
        } else play(idle);

    }

    @Override
    public void move(int from, int to) {
        linkVisuals(ch);
        super.move(from, to);
        linkVisuals(ch);
    }

    @Override
    public int blood() {
        return 0xFF966400;
    }

}