package com.artlessavian.whatsanairport.ControlStates;

import com.artlessavian.whatsanairport.*;

import java.util.ArrayList;
import java.util.Iterator;


public class AttackControlState extends ControlState
{
	private Unit selectedUnit;

	private ArrayList<Unit> attackable;
	private Iterator<Unit> attackableIter;
	private Unit enemyUnit;

	private int cursorX;
	private int cursorY;

	public AttackControlState(ControlStateSystem controlStateSystem)
	{
		super(controlStateSystem);
	}

	@Override
	public void onEnter(Object... varargs)
	{
		cursorX = (Integer)varargs[0];
		cursorY = (Integer)varargs[1];
		selectedUnit = (Unit)varargs[2];

		attackable = selectedUnit.getAttackableUnits((Boolean)varargs[3]);
		if (attackable.size() == 0) {controlStateSystem.setState(UnitOptionsControlState.class).onReturn();}
		attackableIter = attackable.iterator();

		for (Unit u : attackable)
		{
			u.tile.register(this, WarsConst.selectRed);
		}

		enemyUnit = null;
	}

	@Override
	public void onExit()
	{
		for (Unit u : attackable)
		{
			u.tile.deregister(this);
		}
	}

	@Override
	public void onReturn()
	{
		for (Unit u : attackable)
		{
			u.tile.register(this, WarsConst.selectRed);
		}
	}

	@Override
	public boolean up()
	{
		// TODO make not garbage
		if (!attackableIter.hasNext())
		{
			attackableIter = attackable.iterator();
		}

		enemyUnit = attackableIter.next();
		return true;
	}

	@Override
	public boolean down()
	{
		up();
		return true;
	}

	@Override
	public boolean left()
	{
		up();
		return true;
	}

	@Override
	public boolean right()
	{
		up();
		return true;
	}

	@Override
	public void pick(int screenX, int screenY, int x, int y)
	{
		if (controlStateSystem.doubleTap && enemyUnit != null && enemyUnit.tile.x == x && enemyUnit.tile.y == y)
		{
			select();
		}
		else
		{
			weakPick(screenX, screenY, x, y);
		}
	}

	@Override
	public void weakPick(int screenX, int screenY, int x, int y)
	{
		if (attackable.contains(battle.map.map[x][y].unit))
		{
			enemyUnit = battle.map.map[x][y].unit;
		}
	}

	@Override
	public void release(int screenX, int screenY, int x, int y)
	{
		if (!controlStateSystem.doubleTap && enemyUnit != null && enemyUnit.tile.x == x && enemyUnit.tile.y == y)
		{
			select();
		}
	}

	@Override
	public void select()
	{
		if (enemyUnit != null)
		{
			for (Unit u : attackable)
			{
				u.tile.deregister(this);
			}

			selectedUnit.attack(enemyUnit, false);
			controlStateSystem.setState(SelectUnitControlState.class).onEnter(cursorX, cursorY);
		}
	}

	@Override
	public void cancel()
	{
		controlStateSystem.setState(UnitOptionsControlState.class).onReturn();
	}

	@Override
	public void update(float delta)
	{

	}

	@Override
	public void moveCam()
	{

	}

	@Override
	public void draw()
	{
		String toWrite = "??";
		if (enemyUnit != null)
		{
			battle.main.batch.draw(battle.grid, enemyUnit.sprite.getX(), enemyUnit.sprite.getY(), 1, 1);
			toWrite = (int)(selectedUnit.health * 5) + "%";
		}
		battle.main.batch.setProjectionMatrix(battle.main.screenSpace.combined);
		battle.main.font.draw(battle.main.batch, toWrite, battle.main.screenSpace.viewportWidth/2f, battle.main.screenSpace.viewportHeight/2f);
	}
}