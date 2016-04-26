package com.artlessavian.whatsanairport.ControlStates;

import com.artlessavian.whatsanairport.ControlStateSystem;

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
