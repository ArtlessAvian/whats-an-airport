package com.artlessavian.whatsanairport.ControlStates;

import com.artlessavian.whatsanairport.BattleScreen;
import com.artlessavian.whatsanairport.WarsConst;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;

import java.util.Iterator;
import java.util.LinkedList;

class CommonStateFunctions
{
	private static final Vector3 helper = new Vector3();
	private static final Sprite scrollBox = new Sprite();

	private CommonStateFunctions() {}

	public static boolean withinFocus(float leeway, float focusX, float focusY)
	{
		BattleScreen battle = BattleScreen.getInstance();

		helper.x = focusX;
		helper.y = focusY;

		helper.sub(battle.trueCamPos);

		if (helper.y > battle.worldSpace.viewportHeight / 2f - leeway)
		{
			return false;
		}
		if (helper.y < -battle.worldSpace.viewportHeight / 2f + leeway)
		{
			return false;
		}

		if (helper.x > battle.worldSpace.viewportWidth / 2f - leeway)
		{
			return false;
		}
		if (helper.x < -battle.worldSpace.viewportWidth / 2f + leeway)
		{
			return false;
		}

		return true;
	}

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

		// Y Positioning
		if (battle.worldSpace.viewportHeight >= battle.map.mapHeight)
		{
			battle.trueCamPos.y = battle.map.mapHeight/2f;
			battle.camVelocity.y = 0;
		}
		else
		{
			if (helper.y > battle.worldSpace.viewportHeight / 2f - leeway)
			{
				battle.trueCamPos.y += helper.y - (battle.worldSpace.viewportHeight / 2f - leeway);
				battle.camVelocity.y = 0;
			}
			if (helper.y < -battle.worldSpace.viewportHeight / 2f + leeway)
			{
				battle.trueCamPos.y += helper.y - (-battle.worldSpace.viewportHeight / 2f + leeway);
				battle.camVelocity.y = 0;
			}
		}

		if (battle.worldSpace.viewportWidth >= battle.map.mapWidth)
		{
			battle.trueCamPos.x = battle.map.mapWidth/2f;
			battle.camVelocity.x = 0;
		}
		else
		{
			if (helper.x > battle.worldSpace.viewportWidth / 2f - leeway)
			{
				battle.trueCamPos.x += helper.x - (battle.worldSpace.viewportWidth / 2f - leeway);
				battle.camVelocity.x = 0;
			}
			if (helper.x < -battle.worldSpace.viewportWidth / 2f + leeway)
			{
				battle.trueCamPos.x += helper.x - (-battle.worldSpace.viewportWidth / 2f + leeway);
				battle.camVelocity.x = 0;
			}
		}
	}

	public static void drawFocus(float leeway)
	{
		BattleScreen battle = BattleScreen.getInstance();
		if (scrollBox.getTexture() == null) {scrollBox.setTexture(battle.grid); scrollBox.setRegion(0f,0f,1f,1f);}
		scrollBox.setSize(battle.worldSpace.viewportWidth / 2f - leeway, battle.worldSpace.viewportHeight / 2f - leeway);

		scrollBox.setCenter(battle.worldSpace.position.x - scrollBox.getWidth()/2f, battle.worldSpace.position.y - scrollBox.getHeight()/2f);
		scrollBox.draw(battle.main.batch, 0.3f);
		scrollBox.setCenter(battle.worldSpace.position.x - scrollBox.getWidth()/2f, battle.worldSpace.position.y + scrollBox.getHeight()/2f);
		scrollBox.draw(battle.main.batch, 0.3f);
		scrollBox.setCenter(battle.worldSpace.position.x + scrollBox.getWidth()/2f, battle.worldSpace.position.y - scrollBox.getHeight()/2f);
		scrollBox.draw(battle.main.batch, 0.3f);
		scrollBox.setCenter(battle.worldSpace.position.x + scrollBox.getWidth()/2f, battle.worldSpace.position.y + scrollBox.getHeight()/2f);
		scrollBox.draw(battle.main.batch, 0.3f);
	}

	public static void drawPath(LinkedList<WarsConst.CardinalDir> path, int originX, int originY, int start)
	{
		BattleScreen battle = BattleScreen.getInstance();

		int x = originX;
		int y = originY;

		int consumed = 0;

		Iterator<WarsConst.CardinalDir> bleh = path.descendingIterator();
		while (bleh.hasNext())
		{
			WarsConst.CardinalDir next = bleh.next();
			switch (next)
			{
				case UP:
				{
					y++;
					break;
				}
				case DOWN:
				{
					y--;
					break;
				}
				case LEFT:
				{
					x--;
					break;
				}
				case RIGHT:
				{
					x++;
					break;
				}
			}

			if (consumed >= start)
			{
				battle.main.batch.draw(battle.grid, x + 0.2f, y + 0.2f, 0.6f, 0.6f);
			}
			consumed++;
		}

	}
}
