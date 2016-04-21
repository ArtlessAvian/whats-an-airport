package com.artlessavian.whatsanairport.ControlStates;

import com.artlessavian.whatsanairport.ControlStateSystem;
import com.badlogic.gdx.math.Vector3;

public abstract class CursorControlState extends ControlState
{
	int cursorX;
	int cursorY;

	private boolean initPressReleased;

	CursorControlState(ControlStateSystem controlStateSystem)
	{
		super(controlStateSystem);
		initPressReleased = true;
	}

	@Override
	public void onEnter(Object... varargs)
	{
		cursorX = (Integer)varargs[0];
		cursorY = (Integer)varargs[1];
		initPressReleased = false;
	}

	@Override
	public boolean up()
	{
		if (cursorY < battle.mapHeight - 1)
		{
			cursorY++;
			return true;
		}
		return false;
	}

	@Override
	public boolean down()
	{
		if (cursorY > 0)
		{
			cursorY--;
			return true;
		}
		return false;
	}

	@Override
	public boolean left()
	{
		if (cursorX > 0)
		{
			cursorX--;
			return true;
		}
		return false;
	}

	@Override
	public boolean right()
	{
		if (cursorX < battle.mapWidth - 1)
		{
			cursorX++;
			return true;
		}
		return false;
	}

	@Override
	public void pick(int screenX, int screenY, int x, int y)
	{
		if (initPressReleased)
		{
			if (cursorX == x && cursorY == y)
			{
				select();
			}
			else
			{
				cursorX = x;
				cursorY = y;

				moveCam();
			}
		}
	}

	@Override
	public void release(int screenX, int screenY, int x, int y)
	{
		initPressReleased = true;
	}

	@Override
	public void moveCam()
	{
		CommonStateFunctions.focus(2, cursorX + 0.5f, cursorY + 0.5f);
	}

	@Override
	public void draw()
	{
		battle.main.batch.draw(battle.grid, cursorX, cursorY, 1, 1);
		CommonStateFunctions.drawFocus(2);
	}
}
