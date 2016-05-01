package com.artlessavian.whatsanairport.ControlStates;

import com.artlessavian.whatsanairport.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class MovingUnitControlState extends ControlState
{
	private final float timePerTileDefault = 0.12f;
	private float timeThisTile = 0.12f;

	private Unit selectedUnit;
	private Iterator<WarsConst.CardinalDir> pathIterator;
	private WarsConst.CardinalDir yee;

	private int x;
	private int y;

	private int originX;
	private int originY;

	private float timeAccum;
	private boolean attackAfter;
	private LinkedList<WarsConst.CardinalDir> path;
	private int consumed;

	public MovingUnitControlState(ControlStateSystem controlStateSystem)
	{
		super(controlStateSystem);
	}

	@Override
	public void onEnter(Object... varargs)
	{
		selectedUnit = (Unit)varargs[0];
		path = (LinkedList<WarsConst.CardinalDir>)(varargs[1]);
		pathIterator = path.descendingIterator();
		consumed = 0;

		timeAccum = 0;
		timeThisTile = timePerTileDefault;

		x = (Integer)varargs[2];
		y = (Integer)varargs[3];
		attackAfter = (Boolean)varargs[4];

		originX = x;
		originY = y;


		if (pathIterator.hasNext())
		{
			yee = pathIterator.next();
		}
		else
		{
			finish();
		}
	}

	@Override
	public void onExit()
	{

	}

	@Override
	public void onReturn()
	{

	}

	@Override
	public boolean up()
	{
		return true;
	}

	@Override
	public boolean down()
	{
		return true;
	}

	@Override
	public boolean left()
	{
		return true;
	}

	@Override
	public boolean right()
	{
		return true;
	}

	@Override
	public void pick(int screenX, int screenY, int worldX, int worldY)
	{

	}

	@Override
	public void weakPick(int screenX, int screenY, int worldX, int worldY)
	{

	}

	@Override
	public void release(int screenX, int screenY, int worldX, int worldY)
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
		controlStateSystem.setState(MoveUnitControlState.class).onReturn();
	}

	public void finish()
	{
		selectedUnit.sprite.setPosition(x, y);

		Unit displaced = selectedUnit.move(battle.map.map[x][y]);

		if (attackAfter && (displaced == null || displaced == selectedUnit))
		{
			controlStateSystem.stateHashMap.get(UnitOptionsControlState.class).onEnter(selectedUnit, originX, originY, x, y, displaced);
			controlStateSystem.setState(AttackControlState.class).onEnter(x, y, selectedUnit, originX != x || originY != y);
		} else
		{
			controlStateSystem.setState(UnitOptionsControlState.class).onEnter(selectedUnit, originX, originY, x, y, displaced);
		}
	}

	@Override
	public void update(float delta)
	{
		timeAccum += delta;

		while (timeAccum >= timeThisTile)
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
			timeAccum -= timeThisTile;
			//timeThisTile *= 2;
			consumed++;

			if (pathIterator.hasNext())
			{
				yee = pathIterator.next();
			}
			else
			{
				finish();
				return;
			}
		}

		switch (yee)
		{
			case UP:
			{
				selectedUnit.sprite.setY(y + timeAccum / timeThisTile);
				break;
			}
			case DOWN:
			{
				selectedUnit.sprite.setY(y - timeAccum / timeThisTile);
				break;
			}
			case LEFT:
			{
				selectedUnit.sprite.setX(x - timeAccum / timeThisTile);
				break;
			}
			case RIGHT:
			{
				selectedUnit.sprite.setX(x + timeAccum / timeThisTile);
				break;
			}
		}
	}

	@Override
	public void moveCam()
	{
		CommonStateFunctions.focus(3, selectedUnit.sprite.getX() + 0.5f, selectedUnit.sprite.getY() + 0.5f);
	}

	@Override
	public void draw()
	{
		CommonStateFunctions.drawFocus(3);
		CommonStateFunctions.drawPath(path, originX, originY, consumed);
	}
}
