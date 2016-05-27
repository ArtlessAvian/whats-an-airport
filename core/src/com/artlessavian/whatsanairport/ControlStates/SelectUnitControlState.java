package com.artlessavian.whatsanairport.ControlStates;

import com.artlessavian.whatsanairport.BattleScreen;
import com.artlessavian.whatsanairport.ControlStateSystem;
import com.artlessavian.whatsanairport.Unit;

public class SelectUnitControlState extends CursorControlState
{
	private final ControlStateSystem controlStateSystem;
	private final BattleScreen battle;

	public SelectUnitControlState(ControlStateSystem controlStateSystem)
	{
		super(controlStateSystem);
		this.controlStateSystem = controlStateSystem;
		this.battle = controlStateSystem.battle;
	}

	@Override
	public void onEnter(Object... varargs)
	{
		super.onEnter(varargs[0], varargs[1]);
	}

	@Override
	public void onExit()
	{
		// This will never happen
	}

	@Override
	public void onReturn()
	{

	}

	@Override
	public void select()
	{
		Unit unit = battle.map.map[cursorX][cursorY].unit;
		if (unit != null)
		{
			if (unit.team.equals(battle.dayAndCoHandler.team))
			{
				if (!unit.used)
				{
					controlStateSystem.setState(MoveUnitControlState.class).onEnter(cursorX, cursorY, unit);
				} else
				{
					// TODO: make a noise
				}
			}
			else
			{
				if (!unit.isDangerZoned)
				{
					unit.makeDangerZone();
				} else
				{
					unit.removeDangerZone();
				}
			}
		} else
		{
			controlStateSystem.setState(DayOptionsControlState.class).onEnter();
		}
	}

	@Override
	public void cancel()
	{
//		Unit unit = battle.map.map[cursorX][cursorY].unit;
//		if (unit != null)
//		{
//			if (!unit.team.equals(battle.dayAndCoHandler.team))
//			{
//				if (!unit.isDangerZoned)
//				{
//					unit.makeDangerZone();
//				} else
//				{
//					unit.removeDangerZone();
//				}
//			}
//		}

		controlStateSystem.setState(DayOptionsControlState.class).onEnter();
	}

	@Override
	public void update(float delta)
	{

	}
}
