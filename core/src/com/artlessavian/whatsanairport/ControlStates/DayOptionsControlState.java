package com.artlessavian.whatsanairport.ControlStates;

import com.artlessavian.whatsanairport.ControlStateSystem;
import com.artlessavian.whatsanairport.MapTile;

public class DayOptionsControlState extends MenuControlState
{
	public DayOptionsControlState(ControlStateSystem controlStateSystem)
	{
		super(controlStateSystem);
	}

	@Override
	public void fillOptions()
	{
		addOption("OPTIONS");
		addOption("CLEAR ZONES");
		addOption("END DAY");
	}


	@Override
	public void select()
	{
		switch (options.get(position))
		{
			case "OPTIONS":
			{
				controlStateSystem.setState(OptionsOptionsControlState.class).onEnter();
				break;
			}

			case "CLEAR ZONES":
			{
				for (MapTile[] a : battle.map.map)
				{
					for (MapTile b : a)
					{
						if (b.unit != null && b.unit.isDangerZoned)
						{
							b.unit.removeDangerZone();
						}
					}
				}
				break;
			}

			case "END DAY":
			{
				battle.dayAndCoHandler.nextDay();
				controlStateSystem.setState(NewDayControlState.class).onEnter();
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
