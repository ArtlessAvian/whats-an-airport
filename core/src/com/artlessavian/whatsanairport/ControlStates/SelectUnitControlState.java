package com.artlessavian.whatsanairport.ControlStates;

import com.artlessavian.whatsanairport.BattleScreen;
import com.artlessavian.whatsanairport.ControlStateSystem;
import com.artlessavian.whatsanairport.Unit;

public class SelectUnitControlState extends CursorControlState
{
	private final ControlStateSystem controlStateSystem;
	private final BattleScreen battle;

	private float pressedAccumulator;
	private boolean isPressing;

	// Prevent click when entering state
	private boolean initPressReleased;

	public SelectUnitControlState(ControlStateSystem controlStateSystem)
	{
		super(controlStateSystem);
		this.controlStateSystem = controlStateSystem;
		this.battle = controlStateSystem.battle;

		initPressReleased = true;
	}

	@Override
	public void enter(Object... varargs)
	{
		super.enter(varargs[0], varargs[1]);
		cursorX = (Integer)varargs[0];
		cursorY = (Integer)varargs[1];

		initPressReleased = false;
	}

	@Override
	public void cancelReturn()
	{

	}

	@Override
	public void select()
	{
		Unit unit = battle.map.map[cursorX][cursorY].unit;
		if (unit != null)
		{
			controlStateSystem.setState(MoveUnitControlState.class).enter(cursorX, cursorY, unit);
		}
	}

	@Override
	public void cancel()
	{
		Unit unit = battle.map.map[cursorX][cursorY].unit;
		if (unit != null)
		{
			if (!unit.isDangerZoned)
			{
				unit.makeDangerZone();
			} else
			{
				unit.removeDangerZone();
			}
		}
	}

	@Override
	public void update(float delta)
	{

	}
}
