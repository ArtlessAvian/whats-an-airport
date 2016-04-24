package com.artlessavian.whatsanairport.ControlStates;

import com.artlessavian.whatsanairport.ControlStateSystem;
import com.badlogic.gdx.Gdx;

public class DayOptionsControlState extends MenuControlState
{
	public DayOptionsControlState(ControlStateSystem controlStateSystem)
	{
		super(controlStateSystem);
	}

	@Override
	public void fillOptions()
	{
		addOption("ZOOM OUT");
		addOption("ZOOM IN");
		addOption("END DAY");
	}


	@Override
	public void select()
	{
		switch (options.get(position))
		{
			case "ZOOM OUT": {battle.screenTileHeight += 2; battle.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); break;}
			case "ZOOM IN": {if (battle.screenTileHeight > 2) {battle.screenTileHeight -= 2; battle.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());} break;}

			case "END DAY":
			{
				battle.dayAndCoHandler.nextDay();
				controlStateSystem.setState(SelectUnitControlState.class);
				break;
			}
		}
	}

	@Override
	public void cancel()
	{
		controlStateSystem.setState(SelectUnitControlState.class).onReturn();
	}
}
