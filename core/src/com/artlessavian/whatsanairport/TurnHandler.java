package com.artlessavian.whatsanairport;

class TurnHandler
{
	private final Map map;

	final int[] orderToColor = {0, 1};
	final boolean[] eliminated = {false, false};

	int turn;
	int day;

	public TurnHandler(Map map)
	{
		this.map = map;
	}

	public void endTurn()
	{
		do
		{
			turn++;
			if (turn >= orderToColor.length)
			{
				day++;
				turn = 0;
			}
		} while (eliminated[turn]);

		for (int y = 0; y < map.height; y++)
		{
			for (int x = 0; x < map.width; x++)
			{
				Unit unit = map.tileMap[y][x].getUnit();
				if (unit != null)
				{
					unit.done = false;
				}
			}
		}
	}
}
