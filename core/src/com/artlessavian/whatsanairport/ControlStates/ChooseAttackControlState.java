package com.artlessavian.whatsanairport.ControlStates;

import com.artlessavian.whatsanairport.*;

import java.util.HashMap;
import java.util.LinkedList;

public class ChooseAttackControlState implements ControlState
{
	private final ControlStateSystem controlStateSystem;
	private final BattleScreen battle;

	Unit selectedUnit;

	LinkedList<Unit> attackable;
	Unit enemyUnit;

	int cursorX;
	int cursorY;

	public ChooseAttackControlState(ControlStateSystem controlStateSystem)
	{
		this.controlStateSystem = controlStateSystem;
		this.battle = controlStateSystem.battle;
	}

	@Override
	public void enter(Object... varargs)
	{
		cursorX = (Integer)varargs[0];
		cursorY = (Integer)varargs[1];
		selectedUnit = (Unit)varargs[2];

		attackable = selectedUnit.getAttackableUnits(true);
		enemyUnit = attackable.getFirst();
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
		selectedUnit.attack(enemyUnit, false);
		controlStateSystem.setState(SelectUnitControlState.class).enter(cursorX, cursorY);
	}

	@Override
	public void cancel()
	{

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
		battle.main.batch.draw(battle.grid, enemyUnit.sprite.getX(), enemyUnit.sprite.getY(), 1, 1);
	}
}
