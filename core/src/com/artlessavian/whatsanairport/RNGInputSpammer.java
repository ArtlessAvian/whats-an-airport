package com.artlessavian.whatsanairport;

import com.artlessavian.whatsanairport.ControlStates.ControlState;
import com.badlogic.gdx.Gdx;

public class RNGInputSpammer
{
	public static int cyclesPerFrame = 30000;

	public static void doAThing(ControlStateSystem controlStateSystem)
	{
		int action = (int)(Math.random() * 7);
		ControlState state = controlStateSystem.state;

		switch (action)
		{
			case 0: {state.doDirection(WarsConst.CardinalDir.UP); break;}
			case 1: {state.doDirection(WarsConst.CardinalDir.DOWN); break;}
			case 2: {state.doDirection(WarsConst.CardinalDir.LEFT); break;}
			case 3: {state.doDirection(WarsConst.CardinalDir.RIGHT); break;}
			case 4: {state.select(); break;}
			case 5: {state.cancel(); break;}
		}
	}

	public static void doTheThing(ControlStateSystem controlStateSystem)
	{
		for (int i = 0; i < cyclesPerFrame; i++)
		{
			doAThing(controlStateSystem);
			controlStateSystem.update((float)(Math.random() * 5) + 1/60f);
		}
	}
}
