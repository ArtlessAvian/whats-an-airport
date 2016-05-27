package com.artlessavian.whatsanairport.ControlStates;

import com.artlessavian.whatsanairport.ControlStateSystem;
import com.badlogic.gdx.Gdx;

public class OptionsOptionsControlState extends MenuControlState
{
	public OptionsOptionsControlState(ControlStateSystem controlStateSystem)
	{
		super(controlStateSystem);
	}

	@Override
	public void fillOptions()
	{
		addOption("ZOOM OUT");
		addOption("ZOOM IN");
		addOption("doubletap sucks");
		addOption("DRAGPAN");
	}


	@Override
	public void select()
	{
		switch (options.get(position))
		{
			case "ZOOM OUT":
			{
				battle.screenTileHeight += 1;
				battle.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
				break;
			}
			case "ZOOM IN":
			{
				if (battle.screenTileHeight > 1)
				{
					battle.screenTileHeight -= 1;
					battle.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
				}
				break;
			}
			case "doubletap sucks":
			{
				controlStateSystem.doubleTap = !controlStateSystem.doubleTap;
				break;
			}
			case "DRAGPAN":
			{
				controlStateSystem.dragPan = !controlStateSystem.dragPan;
				break;
			}
		}
	}

	@Override
	public void cancel()
	{
		controlStateSystem.setState(DayOptionsControlState.class).onReturn();
	}
}
