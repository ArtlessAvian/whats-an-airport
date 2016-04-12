package com.artlessavian.whatsanairport.ControlStates;

import com.artlessavian.whatsanairport.BattleScreen;
import com.artlessavian.whatsanairport.ControlStateSystem;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;

public abstract class CursorControlState implements ControlState
{
	private final ControlStateSystem controlStateSystem;
	private final BattleScreen battle;
	private final Sprite scrollBox;

	int cursorX;
	int cursorY;
	private final Vector3 cursorPos;

	private boolean initPressReleased;

	CursorControlState(ControlStateSystem controlStateSystem)
	{
		this.controlStateSystem = controlStateSystem;
		this.battle = controlStateSystem.battle;
		cursorPos = new Vector3();
		scrollBox = new Sprite(battle.grid);
		initPressReleased = true;
	}

	@Override
	public void enter(Object... varargs)
	{
		cursorX = (Integer)varargs[0];
		cursorY = (Integer)varargs[1];
		initPressReleased = false;
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
		if (initPressReleased)
		{
			if (cursorX == x && cursorY == y)
			{
				select();
			}
			cursorX = x;
			cursorY = y;

			moveCam();
		}
	}

	@Override
	public void weakPick(int screenX, int screenY, int x, int y)
	{
		if (initPressReleased)
		{
			cursorX = x;
			cursorY = y;
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
		cursorPos.x = cursorX + 0.5f;
		cursorPos.y = cursorY + 0.5f;

		cursorPos.sub(battle.trueCamPos);
		while (cursorPos.x > battle.world.viewportWidth / 2f - 0.3 * battle.world.viewportHeight)
		{
			battle.trueCamPos.x++;
			cursorPos.x--;
		}
		while (cursorPos.x < -battle.world.viewportWidth / 2f + 0.3 * battle.world.viewportHeight)
		{
			battle.trueCamPos.x--;
			cursorPos.x++;
		}
		while (cursorPos.y > 0.3 * battle.world.viewportHeight)
		{
			battle.trueCamPos.y++;
			cursorPos.y--;
		}
		while (cursorPos.y < -0.3 * battle.world.viewportHeight)
		{
			battle.trueCamPos.y--;
			cursorPos.y++;
		}
	}

	@Override
	public void draw()
	{
		battle.main.batch.draw(battle.grid, cursorX, cursorY, 1, 1);
		scrollBox.setSize(battle.world.viewportWidth - 0.4f * battle.world.viewportHeight, 0.6f * battle.world.viewportHeight);
		scrollBox.setCenter(battle.world.position.x, battle.world.position.y);
		scrollBox.draw(battle.main.batch, 0.3f);
	}
}
