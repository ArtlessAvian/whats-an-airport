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
		originalTile = (Tile)args[1];
	}

	@Override
	void reactivate()
	{
		inputHandler.pop();
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
	public boolean select()
	{
		return true;
	}

	@Override
	public boolean cancel()
	{
		return true;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, float tileX, float tileY)
	{
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, float tileX, float tileY)
	{
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, float tileX, float tileY)
	{
		return true;
	}

	@Override
	public boolean update()
	{
		if (!selectedUnit.isAnimating)
		{
			inputHandler.addState(UnitMenu.class, true, false, selectedUnit, originalTile);
		}
		return true;
	}
}
