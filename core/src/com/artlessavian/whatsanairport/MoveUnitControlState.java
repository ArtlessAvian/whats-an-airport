package com.artlessavian.whatsanairport;

import com.badlogic.gdx.math.Vector3;

import java.util.LinkedList;
import java.util.Stack;

public class MoveUnitControlState implements ControlState
{
	private final ControlStateSystem controlStateSystem;
	private final BattleScreen battle;

	private Unit selectedUnit;
	private final Stack<WarsConst.CardinalDir> path;
	private int movementCost;
	private LinkedList<MapTile> movement;

	private int originX;
	private int originY;
	private int cursorX;
	private int cursorY;

	private final Vector3 cursorPos;

	public MoveUnitControlState(ControlStateSystem controlStateSystem)
	{
		this.controlStateSystem = controlStateSystem;
		this.battle = controlStateSystem.battle;

		path = new Stack<WarsConst.CardinalDir>();

		cursorPos = new Vector3();
	}

	@Override
	public void enter(Object... varargs)
	{
		path.clear();
		movementCost = 0;

		originX = (Integer)varargs[0];
		originY = (Integer)varargs[1];
		cursorX = originX;
		cursorY = originY;

		selectedUnit = (Unit)varargs[2];

		movement = selectedUnit.getMovement();
		for (MapTile t : movement)
		{
			t.setHighlight(WarsConst.selectBlue, 100);
		}
	}

	@Override
	public void up()
	{
		if (cursorY < battle.mapHeight - 1) {cursorY++;}
	}

	@Override
	public void down()
	{
		if (cursorY > 0) {cursorY--;}
	}

	@Override
	public void left()
	{
		if (cursorX > 0) {cursorX--;}
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
		} else
		{
			cursorX = x;
			cursorY = y;
		}
	}

	@Override
	public void select()
	{
		if (selectedUnit.move(battle.map[cursorX][cursorY]))
		{
			for (MapTile t : movement)
			{
				t.clearHighlight();
			}
			controlStateSystem.setState(SelectUnitControlState.class);
			controlStateSystem.state.enter(cursorX, cursorY);
		}
	}

	@Override
	public void cancel()
	{
		for (MapTile t : movement)
		{
			t.clearHighlight();
		}
		controlStateSystem.setState(SelectUnitControlState.class);
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
