package com.artlessavian.whatsanairport.ControlStates;

import com.artlessavian.whatsanairport.ControlStateSystem;
import com.badlogic.gdx.graphics.Color;

public class NewDayControlState extends ControlState
{
	int framesIn;

	public NewDayControlState(ControlStateSystem controlStateSystem)
	{
		super(controlStateSystem);
	}

	@Override
	protected void onEnter(Object... varargs)
	{
		framesIn = 0;
		battle.trueCamPos.x = battle.map.mapWidth/2f;
		battle.trueCamPos.y = battle.map.mapHeight/2f;

		for (int i = 0; i < 3; i++)
		{
			int random = (int)(Math.random() * battle.map.mapWidth);
			if (battle.map.map[random][7].unit == null)
			{
				battle.map.map[random][7].createUnit("Red");
			}

			random = (int)(Math.random() * battle.map.mapHeight);
			if (battle.map.map[7][random].unit == null)
			{
				battle.map.map[7][random].createUnit("Blue");
			}
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
	protected boolean up()
	{
		return false;
	}

	@Override
	protected boolean down()
	{
		return false;
	}

	@Override
	protected boolean left()
	{
		return false;
	}

	@Override
	protected boolean right()
	{
		return false;
	}

	@Override
	public void pick(int screenX, int screenY, int worldX, int worldY)
	{
		controlStateSystem.setState(SelectUnitControlState.class);
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
		controlStateSystem.setState(SelectUnitControlState.class);
	}

	@Override
	public void cancel()
	{

	}

	@Override
	public void update(float delta)
	{
		framesIn += 1;

		if (framesIn > 180)
		{
			controlStateSystem.setState(SelectUnitControlState.class);
		}
	}

	@Override
	public void moveCam()
	{

	}

	public void draw()
	{
		battle.main.batch.setProjectionMatrix(battle.main.screenSpace.combined);
		battle.main.font.setColor(Color.WHITE);
		battle.main.font.draw(battle.main.batch, "NEW DAYYYYYYY " + (battle.dayAndCoHandler.day + 1), 100, battle.main.screenSpace.viewportHeight / 2f);
	}
}
