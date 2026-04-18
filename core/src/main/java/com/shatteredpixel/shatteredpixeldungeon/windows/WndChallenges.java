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

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.Image;

import java.util.ArrayList;

public class WndChallenges extends Window {

	private static final int WIDTH        = 100;
	private static final int TTL_HEIGHT   = 16;
	private static final int BTN_HEIGHT   = 16;
	private static final int GAP          = 2;

	private final boolean editable;
	private final ArrayList<CheckBox> boxes;

	public WndChallenges( int checked, boolean editable ) {

		super();

		this.editable = editable;

		RenderedTextBlock title = PixelScene.renderTextBlock( Messages.get(this, "title"), 12 );
		title.hardlight( TITLE_COLOR );

		title.setPos((WIDTH - title.width()) / 2, (TTL_HEIGHT - title.height()) / 2);

		PixelScene.align(title);
		add( title );

		boxes = new ArrayList<>();

		float posY = TTL_HEIGHT;
		for (int i=0; i < Challenges.NAME_IDS.length; i++) {

			final String challenge = Challenges.NAME_IDS[i];

			Image icon = Icons.getChalIcon(i);
			icon.x = (i % 2) * 50 + 1;
			icon.y = (i / 2) * (BTN_HEIGHT + GAP) + title.bottom() + 3;
			add( icon );

			CheckBox cb = new CheckBox(""){
				@Override
				protected String hoverText() {
					return Messages.titleCase(Messages.get(Challenges.class, challenge));
				}
			};
			cb.checked( (checked & Challenges.MASKS[i]) != 0 );
			cb.active = editable;

			cb.setRect( (i % 2) * 50 + 18, (i / 2) * (BTN_HEIGHT + GAP) + title.bottom() + 3, 16, BTN_HEIGHT );

			add( cb );
			boxes.add( cb );
			
			IconButton info = new IconButton(Icons.get(Icons.INFO)){
				@Override
				protected void onClick() {
					super.onClick();
					ShatteredPixelDungeon.scene().add(
							new WndMessage("**" + Messages.titleCase(Messages.get(Challenges.class, challenge)) + "**\n\n"
									+ Messages.get(Challenges.class, challenge+"_desc"))
					);
				}

				@Override
				protected String hoverText() {
					return Messages.titleCase(Messages.get(Challenges.class, challenge));
				}
			};
			info.setRect(cb.right(), cb.top(), 16, BTN_HEIGHT);
			add(info);
			
			posY = cb.bottom();
		}
		resize( WIDTH, (int)posY + GAP );
	}

	@Override
	public void onBackPressed() {

		if (editable) {
			int value = 0;
			for (int i=0; i < boxes.size(); i++) {
				if (boxes.get( i ).checked()) {
					value |= Challenges.MASKS[i];
				}
			}
			SPDSettings.challenges( value );
		}

		super.onBackPressed();
	}
}