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
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.CorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class AcidRain extends Buff implements ActionIndicator.Action {

	{
		revivePersists = true;
	}

	private boolean prepared = true;

	@Override
	public boolean act() {
		if (!((Hero)target).hasTalent(Talent.ACID_RAIN)){
			detach();
			return true;
		}
		ActionIndicator.setAction(this);
		prepared = true;
		BuffIndicator.refreshHero();
		diactivate();
		return true;
	}

	@Override
	public int icon() {
		return prepared ? BuffIndicator.NONE : BuffIndicator.TIME;
	}

	@Override
	public void tintIcon(Image icon) { icon.hardlight(0.6f, 0.4f, 0f); }

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
		return HeroIcon.ACID;
	}

	@Override
	public int indicatorColor() {
		return 0x87471D;
	}

	@Override
	public void doAction() {
		if (!((Hero)target).hasTalent(Talent.ACID_RAIN)){ //to prevent player get free boost from elixir of amnesia
			ActionIndicator.clearAction();
			detach();
			return;
		}

		GameScene.selectCell(new CellSelector.Listener() {
			@Override
			public String prompt() {
				return Messages.get(this, "prompt");
			}

			@Override
			public void onSelect(Integer cell) {
				if (cell == null) return;

				if (!Dungeon.level.passable[cell] || !Dungeon.level.heroFOV[cell]){
					GLog.w(Messages.get(this, "invalid_pos"));
					return;
				}
				CorrosiveGas gas = Blob.seed(cell, 25, CorrosiveGas.class);
				CellEmitter.get(cell).burst(Speck.factory(Speck.CORROSION), 10 );
				int gasQuantity = 1 + (Dungeon.scalingDepth() - 1) / 5;
				gas.setStrength(gasQuantity, getClass());
				GameScene.add(gas);
				Sample.INSTANCE.play(Assets.Sounds.GAS);

				ActionIndicator.clearAction();
				timeToNow();
				spend(100f - 20f * Dungeon.hero.pointsInTalent(Talent.ACID_RAIN));
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
