package com.artlessavian.whatsanairport;

public class MovingUnitWait extends InputReceiver
{
	private Unit selectedUnit;
	private Tile finalDestination;
	private Tile originalTile;

	public MovingUnitWait(InputHandler inputHandler)
	{
		super(inputHandler);
	}

	@Override
	public void reset(Object[] args)
	{
		selectedUnit = (Unit)args[0];
		finalDestination = (Tile)args[1];
		originalTile = (Tile)args[2];
	}

	@Override
	void reactivate()
	{
		inputHandler.pop();
	}

	@Override
	public boolean up()
	{
		return false;
	}

	@Override
	public boolean down()
	{
		return false;
	}

	@Override
	public boolean left()
	{
		return false;
	}

	@Override
	public boolean right()
	{
		return false;
	}

	@Override
	public boolean select()
	{
		return false;
	}

	@Override
	public boolean cancel()
	{
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, float tileX, float tileY)
	{
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, float tileX, float tileY)
	{
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, float tileX, float tileY)
	{
		return false;
	}

	@Override
	public boolean update()
	{
		if (selectedUnit.tile == finalDestination)
		{
			inputHandler.addState(UnitMenu.class, true, false, selectedUnit, finalDestination, originalTile);
		}
		return true;
	}
}
