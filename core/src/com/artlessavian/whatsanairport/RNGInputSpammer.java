package com.artlessavian.whatsanairport;

import com.artlessavian.whatsanairport.ControlStates.ControlState;
import com.badlogic.gdx.Gdx;

public class RNGInputSpammer
{
	public static int cyclesPerFrame = 16666; // At 60 frames, 100k inputs per second
	public static boolean doRNGTesting;

	public static void doAThing(ControlStateSystem controlStateSystem)
	{
		float action = (float)Math.random();
		ControlState state = controlStateSystem.state;

		if (action <= 0.225)
		{
			state.doDirection(WarsConst.CardinalDir.UP);
		} else if (action <= 0.45)
		{
			state.doDirection(WarsConst.CardinalDir.DOWN);
		} else if (action < 0.675)
		{
			state.doDirection(WarsConst.CardinalDir.RIGHT);
		} else if (action <= 0.9)
		{
			state.doDirection(WarsConst.CardinalDir.LEFT);
		} else if (action <= 0.98)
		{
			state.select();
		} else
		{
			state.cancel();
		}
	}

	public static void activate()
	{
		doRNGTesting = true;
		BattleScreen battle = BattleScreen.getInstance();
		battle.screenTileHeight = battle.map.mapHeight;
		battle.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		battle.worldSpace.position.y = battle.map.mapHeight / 2f;
		battle.worldSpace.position.x = battle.map.mapWidth / 2f;
		battle.worldSpace.update();
	}

	public static void doTheThing(ControlStateSystem controlStateSystem)
	{
		if (Gdx.graphics.getDeltaTime() > 1 / 30f)
		{
			cyclesPerFrame -= 100;
		} else
		{
			cyclesPerFrame += 100;
		}

		for (int i = 0; i < cyclesPerFrame && doRNGTesting; i++)
		{
			try
			{
				doAThing(controlStateSystem);
				controlStateSystem.update((float)(Math.random() * 5) + 1 / 60f);
			}
			catch (Exception e)
			{
				doRNGTesting = false;
				e.printStackTrace();
			}
		}

		BattleScreen battle = BattleScreen.getInstance();
		battle.screenTileHeight = battle.map.mapHeight;
		battle.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
}
