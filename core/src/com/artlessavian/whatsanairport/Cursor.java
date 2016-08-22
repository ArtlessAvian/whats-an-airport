package com.artlessavian.whatsanairport;

public class Cursor extends InputReceiver
{
	private Map map;

	int x;
	int y;

	public Cursor(InputHandler inputHandler)
	{
		super(inputHandler);
		hasFocus = true;
	}

	@Override
	public void reset(Object[] args)
	{
		map = inputHandler.model.map;
	}

	@Override
	void reactivate()
	{

	}

	@Override
	public boolean up()
	{
		if (y < map.height - 1)
		{
			y++;
			focusY = y + 0.5f;
		}
		return true;
	}

	@Override
	public boolean down()
	{
		if (y > 0)
		{
			y--;
			focusY = y + 0.5f;
		}
		return true;
	}

	@Override
	public boolean left()
	{
		if (x > 0)
		{
			x--;
			focusX = x + 0.5f;
		}
		return true;
	}

	@Override
	public boolean right()
	{
		if (x < map.width - 1)
		{
			x++;
			focusX = x + 0.5f;
		}
		return true;
	}

	@Override
	public boolean select()
	{
		Unit cursored = map.tileMap[y][x].getUnit();
		if (cursored != null)
		{
			if (cursored.owner == inputHandler.model.turnHandler.turn && !cursored.done)
			{
				inputHandler.addState(MoveUnit.class, false, false, cursored, cursored.trueTile);
			}
		}
		else
		{
			if (!BattleModel.lol) {inputHandler.addState(DayMenu.class, false, false);}
		}
		return true;
	}

	@Override
	public boolean cancel()
	{
		Unit cursored = map.tileMap[y][x].getUnit();
		if (cursored != null)
		{
			if (cursored.owner != inputHandler.model.turnHandler.turn)
			{
				System.out.println("STUB. Danger Zone");
			}
		}
		return true;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, float tileX, float tileY)
	{
		touchDragged(screenX, screenY, tileX, tileY);
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, float tileX, float tileY)
	{
		select();
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, float tileX, float tileY)
	{
		for (int i = (int)tileX; i < x; i++) {this.left();}
		for (int i = x; i < (int)tileX; i++) {this.right();}
		for (int i = (int)tileY; i < y; i++) {this.down();}
		for (int i = y; i < (int)tileY; i++) {this.up();}

		return true;
	}

	@Override
	public boolean update()
	{
		return false;
	}
}
