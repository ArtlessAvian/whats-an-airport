package com.artlessavian.whatsanairport;

public class NewDayShower implements InputReceiver
{
	private final InputHandler inputHandler;
	private final Map map;

	final int framesOpen = 180;

	private boolean impatient;
	int time;
	private boolean alreadyFinished;

	NewDayShower(InputHandler inputHandler, Map map)
	{
		this.inputHandler = inputHandler;
		this.map = map;

		time = 0;
		impatient = false;
		alreadyFinished = false;
	}

	// Prevent inputs from going down the stack
	@Override
	public boolean up()
	{
		impatient = true;
		return true;
	}

	@Override
	public boolean down()
	{
		impatient = true;
		return true;
	}

	@Override
	public boolean left()
	{
		impatient = true;
		return true;
	}

	@Override
	public boolean right()
	{
		impatient = true;
		return true;
	}

	@Override
	public boolean select()
	{
		impatient = true;
		return true;
	}

	@Override
	public boolean cancel()
	{
		impatient = true;
		return true;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, float tileX, float tileY)
	{
		impatient = true;
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, float tileX, float tileY)
	{
		impatient = true;
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, float tileX, float tileY)
	{
		impatient = true;
		return true;
	}

	@Override
	public boolean update()
	{
		if (map.visuallyFinished())
		{
			if (!alreadyFinished)
			{
				inputHandler.model.turnHandler.endTurn();
				alreadyFinished = true;
			}

			time++;
			if (impatient)
			{
				time += 3;
			}

			if (time > framesOpen)
			{
				inputHandler.receivers.remove(this);
				time = 0;
				impatient = false;
				alreadyFinished = false;
			}
		}
		return true;
	}
}
