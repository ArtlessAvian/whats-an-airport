package com.artlessavian.whatsanairport.ControlStates;

import com.artlessavian.whatsanairport.ControlStateSystem;
import com.artlessavian.whatsanairport.WarsConst;

public abstract class CursorControlState extends ControlState
{
	int cursorX;
	int cursorY;

	float lastX;
	float lastY;

	boolean allowTapPanning;
	boolean wasInside;
	boolean allowDragPanning;

	CursorControlState(ControlStateSystem controlStateSystem)
	{
		super(controlStateSystem);
	}

	@Override
	public void onEnter(Object... varargs)
	{
		cursorX = (Integer)varargs[0];
		cursorY = (Integer)varargs[1];
	}

	@Override
	public boolean doDirection(WarsConst.CardinalDir direction)
	{
		allowTapPanning = true;
		return super.doDirection(direction);
	}

	@Override
	public boolean up()
	{
		if (cursorY < battle.map.mapHeight - 1)
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
		if (cursorX < battle.map.mapWidth - 1)
		{
			cursorX++;
			return true;
		}
		return false;
	}

	@Override
	public void pick(int screenX, int screenY, int worldX, int worldY)
	{
		wasInside = CommonStateFunctions.withinFocus(2, worldX + 0.5f, worldY + 0.5f);

		allowTapPanning = false;
		allowDragPanning = false;

		cursorX = worldX;
		cursorY = worldY;

		lastX = screenX;
		lastY = screenY;

		moveCam();
	}

	@Override
	public void weakPick(int screenX, int screenY, int worldX, int worldY)
	{
		if (controlStateSystem.dragPan)
		{
			if (!allowDragPanning && screenX < lastX - battle.screenWorldScale || screenX > lastX + battle.screenWorldScale || screenY < lastY - battle.screenWorldScale || screenY > lastY + battle.screenWorldScale)
			{
				allowDragPanning = true;
			}

			if (allowDragPanning)
			{
				if (battle.worldSpace.viewportWidth < battle.map.mapWidth)
				{
					battle.camVelocity.x += (lastX - screenX) / battle.screenWorldScale;
				}

				//battle.worldSpace.position.x = battle.map.mapWidth/2f;

				if (battle.worldSpace.viewportHeight < battle.map.mapHeight)
				{
					battle.camVelocity.y += (lastY - screenY) / battle.screenWorldScale;
				}

				lastX = screenX;
				lastY = screenY;
			}
		} else
		{
			allowTapPanning = false;
			cursorX = worldX;
			cursorY = worldY;
		}
	}

	int lastReleaseX;
	int lastReleaseY;

	@Override
	public void release(int screenX, int screenY, int worldX, int worldY)
	{
		if (wasInside && !allowDragPanning) // && CommonStateFunctions.withinFocus(2, cursorX + 0.5f, cursorY + 0.5f))
		{
			if (controlStateSystem.doubleTap)
			{
				if (lastReleaseX == worldX && lastReleaseY == worldY)
				{
					select();
				}
				lastReleaseX = worldX;
				lastReleaseY = worldY;

			} else
			{
				if (cursorX == worldX && cursorY == worldY)
				{
					select();
				}
			}
		}
		allowTapPanning = true;
	}

	@Override
	public void moveCam()
	{
		if (controlStateSystem.dragPan)
		{
			battle.trueCamPos.add(battle.camVelocity);
			battle.worldSpace.position.add(battle.camVelocity);
			if (controlStateSystem.pointer != -1)
			{
				battle.camVelocity.scl(0);
			} else
			{
				battle.camVelocity.scl(0.9f);
			}
		}
		if (allowTapPanning)
		{
			CommonStateFunctions.focus(2, cursorX + 0.5f, cursorY + 0.5f);
		}
	}

	@Override
	public void draw()
	{
		battle.main.batch.draw(battle.grid, cursorX, cursorY, 1, 1);
		CommonStateFunctions.drawFocus(2);
	}
}
