package com.artlessavian.whatsanairport.ControlStates;

import com.artlessavian.whatsanairport.BattleScreen;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;

class CommonStateFunctions
{
	private static final Vector3 helper = new Vector3();
	private static final Sprite scrollBox = new Sprite();

	private CommonStateFunctions() {}

	public static void focus(float leeway, float focusX, float focusY)
	{
		BattleScreen battle = BattleScreen.getInstance();

		helper.x = focusX;
		helper.y = focusY;

		helper.sub(battle.trueCamPos);

		if (leeway >= battle.worldSpace.viewportHeight / 2f || leeway >= battle.worldSpace.viewportWidth / 2f)
		{
			battle.trueCamPos.x = focusX;
			battle.trueCamPos.y = focusY;
			return;
		}

		while (helper.x > battle.worldSpace.viewportWidth / 2f - leeway)
		{
			battle.trueCamPos.x++;
			helper.x--;
		}
		while (helper.x < -battle.worldSpace.viewportWidth / 2f + leeway)
		{
			battle.trueCamPos.x--;
			helper.x++;
		}
		while (helper.y > battle.worldSpace.viewportHeight / 2f - leeway)
		{
			battle.trueCamPos.y++;
			helper.y--;
		}
		while (helper.y < -battle.worldSpace.viewportHeight / 2f + leeway)
		{
			battle.trueCamPos.y--;
			helper.y++;
		}
	}

	public static void drawFocus(float leeway)
	{
		BattleScreen battle = BattleScreen.getInstance();
		if (scrollBox.getTexture() == null) {scrollBox.setTexture(battle.grid); scrollBox.setRegion(0f,0f,1f,1f);}
		scrollBox.setSize(battle.worldSpace.viewportWidth - 2 * leeway, battle.worldSpace.viewportHeight - 2 * leeway);
		scrollBox.setCenter(battle.worldSpace.position.x, battle.worldSpace.position.y);
		scrollBox.draw(battle.main.batch, 0.3f);
	}
}
