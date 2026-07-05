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
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.PulseEffect;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Viscosity;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MultiTool;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoBuff;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Pulse extends Buff implements ActionIndicator.Action {

    {
        revivePersists = true;

    }

    private int CD = 0;
    private static int debuffTurn(){
        return 5 + Dungeon.hero.pointsInTalent(Talent.STRONG_PULSE);
    }

    private int min(){
        return 3 + ((Hero)target).lvl / 4;
    }

    private int max(){
        return 7 + ((Hero)target).lvl / 2;
    }

    @Override
    public boolean act(){
        if (CD > 0) CD --;
        spend(1f);
        ActionIndicator.refresh();
        return true;
    }

    public void decreaseCD(int amount) {
        CD = Math.max(CD - amount, 0);
        ActionIndicator.refresh();
    }

    public String desc(){
        return Messages.get(this, "desc", min(), max(), debuffTurn(), CD);
    }

    private static final String COOLDOWN = "cd";

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(COOLDOWN, CD);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        CD = bundle.getInt(COOLDOWN);
        ActionIndicator.setAction(this);
    }

    @Override
    public String actionName() {
        return name();
    }

    @Override
    public int actionIcon() {
        return HeroIcon.PULSE;
    }

    @Override
    public Visual secondaryVisual() {
        if (CD <= 0) return null;

        BitmapText txt = new BitmapText(PixelScene.pixelFont);
        txt.text( Integer.toString(CD) );
        txt.hardlight(0x1e6ad1);
        txt.measure();
        return txt;
    }

    @Override
    public int indicatorColor() {
        if (CD > 0) return 0x1a5098;
        return 0x1e6ad1;
    }

    @Override
    public void doAction(){
        if (((Hero)target).belongings.getItem(MultiTool.class) == null){
            GLog.w(Messages.get(this, "no_tool"));
            return;
        }
        if (CD > 0){
            GLog.w(Messages.get(this, "cd"));
            GameScene.show(new WndInfoBuff(this));
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

                Ballistica bolt = new Ballistica(target.pos, cell, Ballistica.PROJECTILE);
                if (bolt.collisionPos == target.pos){
                    GLog.w(Messages.get(Wand.class, "self_target"));
                    return;
                }

                //CD += 51;
                ActionIndicator.refresh();
                target.sprite.attack(cell);

                if (((Hero)target).hasTalent(Talent.PULSE_ENERGY)) {
                    int shield = 1 + 2 * ((Hero) target).pointsInTalent(Talent.PULSE_ENERGY);
                    Buff.affect(target, Barrier.class).setShield(shield);
                    target.sprite.showStatusWithIcon(CharSprite.POSITIVE, Integer.toString(shield), FloatingText.SHIELDING);
                }

				Char ch = Actor.findChar(bolt.collisionPos);
				if (ch != null){
                    target.sprite.parent.add(
                        new PulseEffect(target.sprite.center(), ch.sprite.center())
                    );

					float damage = Random.NormalFloat(min(), max());

					if (Char.hasProp(ch, Char.Property.MECHANICAL)){
						damage *= 1.5f;
						ch.sprite.centerEmitter().burst( SparkParticle.FACTORY, 6 );
					} else {
						ch.sprite.centerEmitter().burst( SparkParticle.FACTORY, 4 );
					}
					ch.sprite.flash();

					ch.damage(Math.round(damage), Pulse.this);

                    if (((Hero)target).hasTalent(Talent.IONIZING_RADIATION)){
                        Viscosity.DeferedDamage deferred = Buff.affect(ch, Viscosity.DeferedDamage.class);
                        deferred.extend(damage * ((Hero)target).pointsInTalent(Talent.IONIZING_RADIATION) / 2f);
                    }

					if (Char.hasProp(ch, Char.Property.MECHANICAL))     Buff.prolong(ch, Amok.class, debuffTurn());
					else if (Char.hasProp(ch, Char.Property.INORGANIC)) Buff.prolong(ch, Vertigo.class, debuffTurn());
					else                                                Buff.prolong(ch, Paralysis.class, debuffTurn());

                    if (((Hero) target).hasTalent(Talent.RESONANT_SENSING))
                        Buff.append(target, TalismanOfForesight.CharAwareness.class,
                            6 + 6 * ((Hero) target).pointsInTalent(Talent.RESONANT_SENSING)).charID = ch.id();
				} else {
                    target.sprite.parent.add(
                        new PulseEffect(target.sprite.center(), DungeonTilemap.tileCenterToWorld(bolt.collisionPos))
                    );
					Dungeon.level.pressCell(bolt.collisionPos);

                    int repairCost = 5 - ((Hero) target).pointsInTalent(Talent.ELECTRONIC_REPAIR);
                    if (Dungeon.level.map[bolt.collisionPos] == Terrain.INACTIVE_TRAP
                            && repairCost < 5 && Dungeon.energy >= repairCost
                            && Dungeon.level.traps.get(bolt.collisionPos) != null){
                        Dungeon.level.traps.get(bolt.collisionPos).fix();
                        Dungeon.energy -= repairCost;
                        Item.updateQuickslot();
                    }
                    CellEmitter.center(bolt.collisionPos).burst(SparkParticle.FACTORY, 4);
				}
                Sample.INSTANCE.play(Assets.Sounds.RAY, 0.6f);
				((Hero)target).spendAndNext(1f);
			}
        });
    }
}