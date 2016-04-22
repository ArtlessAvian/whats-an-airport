package com.artlessavian.whatsanairport.ControlStates;

import com.artlessavian.whatsanairport.*;

import java.util.Iterator;
import java.util.LinkedList;

public class MovingUnitControlState extends ControlState
{
	private final float timePerTile = 0.15f;

	private Unit selectedUnit;
	private Iterator<WarsConst.CardinalDir> path;
	private WarsConst.CardinalDir yee;

	private int x;
	private int y;

	private int originX;
	private int originY;

	private float timeAccum;
	private boolean attackAfter;

	public MovingUnitControlState(ControlStateSystem controlStateSystem)
	{
		super(controlStateSystem);
	}

	@Override
	public void onEnter(Object... varargs)
	{
		selectedUnit = (Unit)varargs[0];
		path = ((LinkedList<WarsConst.CardinalDir>)((LinkedList<WarsConst.CardinalDir>)(varargs[1])).clone()).descendingIterator();
		yee = null;

		timeAccum = 0;

		x = (Integer)varargs[2];
		y = (Integer)varargs[3];
		originX = x;
		originY = y;

		attackAfter = (Boolean)varargs[4];
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
		controlStateSystem.setState(MoveUnitControlState.class).onReturn();
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
				if (attackAfter)
				{
					controlStateSystem.setState(AttackControlState.class).onEnter(x, y, selectedUnit, originX != x || originY != y);
				} else
				{
					controlStateSystem.setState(UnitOptionsControlState.class).onEnter(selectedUnit, originX, originY, x, y, displaced);
				}
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
		CommonStateFunctions.focus(3, selectedUnit.sprite.getX() + 0.5f, selectedUnit.sprite.getY() + 0.5f);
	}

	@Override
	public void draw()
	{
		CommonStateFunctions.drawFocus(3);
	}
}
