package com.artlessavian.whatsanairport.ControlStates;

import com.artlessavian.whatsanairport.BattleScreen;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;

public class CommonStateFunctions
{
	private static Vector3 helper = new Vector3();
	private static final Sprite scrollBox = new Sprite();

	private CommonStateFunctions() {};

	public static void focus(BattleScreen battle, float leeway, float focusX, float focusY)
	{
		helper.x = focusX;
		helper.y = focusY;

		helper.sub(battle.trueCamPos);

		while (leeway > battle.world.viewportHeight / 2f || leeway > battle.world.viewportWidth / 2f)
		{
			leeway--;
		}

		while (helper.x > battle.world.viewportWidth / 2f - leeway)
		{
			battle.trueCamPos.x++;
			helper.x--;
		}
		while (helper.x < -battle.world.viewportWidth / 2f + leeway)
		{
			battle.trueCamPos.x--;
			helper.x++;
		}
		while (helper.y > battle.world.viewportHeight / 2f - leeway)
		{
			battle.trueCamPos.y++;
			helper.y--;
		}
		while (helper.y < -battle.world.viewportHeight / 2f + leeway)
		{
			battle.trueCamPos.y--;
			helper.y++;
		}
	}

	public static void drawFocus(BattleScreen battle, float leeway)
	{
		if (scrollBox.getTexture() == null) {scrollBox.setTexture(battle.grid); scrollBox.setRegion(0f,0f,1f,1f);}
		scrollBox.setSize(battle.world.viewportWidth - 2 * leeway, battle.world.viewportHeight - 2 * leeway);
		scrollBox.setCenter(battle.world.position.x, battle.world.position.y);
		scrollBox.draw(battle.main.batch, 0.3f);
	}
}
