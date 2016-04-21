package com.artlessavian.whatsanairport.ControlStates;

import com.artlessavian.whatsanairport.BattleScreen;
import com.artlessavian.whatsanairport.ControlStateSystem;
import com.artlessavian.whatsanairport.WarsConst;

public abstract class ControlState
{
	final ControlStateSystem controlStateSystem;
	final BattleScreen battle;

	ControlState(ControlStateSystem controlStateSystem)
	{
		this.controlStateSystem = controlStateSystem;
		this.battle = controlStateSystem.battle;
	}

	// Not a fan of this method :/
	protected abstract void onEnter(Object... varargs);

	public abstract void onExit();

	protected abstract void onReturn();

	// Directionals
	public boolean doDirection(WarsConst.CardinalDir direction)
	{
		boolean worked = false;

		switch (direction)
		{
			case UP:
				worked = up();
				break;
			case DOWN:
				worked = down();
				break;
			case LEFT:
				worked = left();
				break;
			case RIGHT:
				worked = right();
				break;
		}

		return worked;
	}

	protected abstract boolean up();

	protected abstract boolean down();

	protected abstract boolean left();

	protected abstract boolean right();

	// Touch Click
	public abstract void pick(int screenX, int screenY, int x, int y);

	public abstract void weakPick(int screenX, int screenY, int x, int y);

	public abstract void release(int screenX, int screenY, int x, int y);

	// Stuff
	public abstract void select();

	public abstract void cancel();

	// More Stuff
	public abstract void update(float delta);

	public abstract void moveCam();

	public void draw()
	{

	}

}
