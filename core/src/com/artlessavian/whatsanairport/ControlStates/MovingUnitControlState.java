package com.artlessavian.whatsanairport.ControlStates;

import com.artlessavian.whatsanairport.*;
import com.badlogic.gdx.math.Vector3;

import java.util.Iterator;
import java.util.LinkedList;

public class MovingUnitControlState implements ControlState
{
	private final ControlStateSystem controlStateSystem;
	private final BattleScreen battle;

	private final float timePerTile = 0.15f;

	private Unit selectedUnit;
	private Iterator<WarsConst.CardinalDir> path;
	private WarsConst.CardinalDir yee;

	private int x;
	private int y;

	private int originX;
	private int originY;

	private float timeAccum;
	private final Vector3 cursorPos;

	public MovingUnitControlState(ControlStateSystem controlStateSystem)
	{
		this.controlStateSystem = controlStateSystem;
		this.battle = controlStateSystem.battle;

		this.cursorPos = new Vector3();
	}

	@Override
	public void enter(Object... varargs)
	{
		selectedUnit = (Unit)varargs[0];
		path = ((LinkedList<WarsConst.CardinalDir>)((LinkedList<WarsConst.CardinalDir>)varargs[1]).clone()).iterator();
		yee = null;

		timeAccum = 0;

		x = (Integer)varargs[2];
		y = (Integer)varargs[3];
		originX = x;
		originY = y;
	}

	@Override
	public void cancelReturn()
	{

	}

	@Override
	public void up()
	{

	}

	@Override
	public void down()
	{

	}

	@Override
	public void left()
	{

	}

	@Override
	public void right()
	{

	}

	@Override
	public void pick(int screenX, int screenY, int x, int y)
	{

	}

	@Override
	public void weakPick(int screenX, int screenY, int x, int y)
	{

	}

	@Override
	public void release(int screenX, int screenY, int x, int y)
	{

	}

	@Override
	public void select()
	{

	}

	@Override
	public void cancel()
	{
		selectedUnit.sprite.setPosition(originX, originY);
		selectedUnit.move(battle.map.map[originX][originY]);
		controlStateSystem.setState(MoveUnitControlState.class).cancelReturn();
	}

	@Override
	public void update(float delta)
	{
		timeAccum += delta;

		if (yee == null)
		{
			if (path.hasNext())
			{
				yee = path.next();
			} else
			{
				Unit displaced = selectedUnit.move(battle.map.map[x][y]);
				controlStateSystem.setState(UnitOptionsControlState.class).enter(selectedUnit, originX, originY, x, y, displaced);
			}
		} else if (timeAccum >= timePerTile)
		{
			switch (yee)
			{
				case UP:
				{
					y++;
					break;
				}
				case DOWN:
				{
					y--;
					break;
				}
				case LEFT:
				{
					x--;
					break;
				}
				case RIGHT:
				{
					x++;
					break;
				}
			}
			selectedUnit.sprite.setPosition(x, y);
			timeAccum -= timePerTile;
			yee = null;
		} else
		{
			switch (yee)
			{
				case UP:
				{
					selectedUnit.sprite.setY(y + timeAccum / timePerTile);
					break;
				}
				case DOWN:
				{
					selectedUnit.sprite.setY(y - timeAccum / timePerTile);
					break;
				}
				case LEFT:
				{
					selectedUnit.sprite.setX(x - timeAccum / timePerTile);
					break;
				}
				case RIGHT:
				{
					selectedUnit.sprite.setX(x + timeAccum / timePerTile);
					break;
				}
			}
		}
	}

	@Override
	public void moveCam()
	{
		cursorPos.x = x + 0.5f;
		cursorPos.y = y + 0.5f;

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

	}
}
