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

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class Hypnosis extends Buff implements ActionIndicator.Action {

    {
        revivePersists = true;
    }

    private boolean prepared = true;

    @Override
    public boolean act() {
        ActionIndicator.setAction(this);
        prepared = true;
        BuffIndicator.refreshHero();
        diactivate();
        return true;
    }

    @Override
    public int icon() {
        return prepared ? BuffIndicator.NONE : BuffIndicator.MAGIC_SLEEP;
    }

    @Override
    public String iconTextDisplay() {
        return Integer.toString((int)visualcooldown());
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", visualcooldown());
    }

    @Override
    public String actionName() {
        return Messages.get(this, "action_name");
    }

    @Override
    public int actionIcon(){
        return HeroIcon.HYPNOSIS;
    }

    @Override
    public int indicatorColor() {
        return 0x2E0D2E;
    }

    @Override
    public void doAction() {
        GameScene.selectCell(new CellSelector.Listener() {
            @Override
            public String prompt() {
                return Messages.get(this, "prompt");
            }

            @Override
            public void onSelect(Integer cell) {
                if (cell == null) return;

                Char c = Actor.findChar(cell);
                if (!(c instanceof Mob) || target.isImmune(Sleep.class) //the Incubus can't hypnotize itself
						|| c.properties().contains(Char.Property.MINIBOSS) || c.properties().contains(Char.Property.BOSS)){
                    GLog.w(Messages.get(this, "invalid_char"));
                    return;
                }
                if (((Mob) c).state == ((Mob) c).SLEEPING) Buff.affect(c, MagicalSleep.class);
                else                                       ((Mob) c).state = ((Mob) c).SLEEPING;
                Sample.INSTANCE.play(Assets.Sounds.LULLABY);
                c.sprite.centerEmitter().start( Speck.factory( Speck.NOTE ), 0.3f, 5 );

                ActionIndicator.clearAction();
                timeToNow();
                spend(75f);
                prepared = false;
                BuffIndicator.refreshHero();
                target.next();
            }
        });

    }

    private static final String PREPARED = "prep";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(PREPARED, prepared);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        prepared = bundle.getBoolean(PREPARED);
        if (prepared) ActionIndicator.setAction(this);
    }
}