package com.artlessavian.whatsanairport;

import com.badlogic.gdx.math.Vector3;

public class SelectUnitControlState implements ControlState
{
	private final ControlStateSystem controlStateSystem;
	private final BattleScreen battle;

	private int cursorX;
	private int cursorY;
	private Vector3 cursorPos;

	public SelectUnitControlState(ControlStateSystem controlStateSystem)
	{
		this.controlStateSystem = controlStateSystem;
		this.battle = controlStateSystem.battle;
		cursorPos = new Vector3();
	}

	@Override
	public void enter(Object... varargs)
	{
		cursorX = (Integer)varargs[0];
		cursorY = (Integer)varargs[1];
		cursorPos = new Vector3(cursorX, cursorY, 0);
	}

	@Override
	public void up()
	{
		if (cursorY < battle.mapHeight - 1)
		{
			cursorY++;
		}
	}

	@Override
	public void down()
	{
		if (cursorY > 0)
		{
			cursorY--;
		}
	}

	@Override
	public void left()
	{
		if (cursorX > 0)
		{
			cursorX--;
		}
	}

	@Override
	public void right()
	{
		if (cursorX < battle.mapWidth - 1) {cursorX++;}
	}

	@Override
	public void pick(int screenX, int screenY, int x, int y)
	{
		if (cursorX == x && cursorY == y)
		{
			select();
		}
		weakPick(screenX, screenY, x, y);
	}

	@Override
	public void weakPick(int screenX, int screenY, int x, int y)
	{
		cursorX = x;
		cursorY = y;
	}

	@Override
	public void release(int screenX, int screenY, int x, int y)
	{

	}

	@Override
	public void select()
	{
		Unit unit = battle.map.map[cursorX][cursorY].unit;
		if (unit != null)
		{
			controlStateSystem.setState(MoveUnitControlState.class);
			controlStateSystem.state.enter(cursorX, cursorY, unit);
		}
	}

	@Override
	public void cancel()
	{
		Unit unit = battle.map.map[cursorX][cursorY].unit;
		if (unit != null)
		{
			if (unit.dangerZoned == null)
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

	@Override
	public void moveCam()
	{
		cursorPos.x = cursorX + 0.5f;
		cursorPos.y = cursorY + 0.5f;

		cursorPos.sub(battle.trueCamPos);
		if (cursorPos.x > 0.3 * battle.world.viewportWidth)
		{
			battle.trueCamPos.x++;
		}
		if (cursorPos.x < -0.3 * battle.world.viewportWidth)
		{
			battle.trueCamPos.x--;
		}
		if (cursorPos.y > 0.3 * battle.world.viewportHeight)
		{
			battle.trueCamPos.y++;
		}
		if (cursorPos.y < -0.3 * battle.world.viewportHeight)
		{
			battle.trueCamPos.y--;
		}

		battle.world.position.lerp(battle.trueCamPos, 0.3f);
	}

	@Override
	public void draw()
	{
		battle.main.batch.draw(battle.grid, cursorX, cursorY, 1, 1);
	}
}
