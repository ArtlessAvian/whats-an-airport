package com.artlessavian.whatsanairport;

import com.badlogic.gdx.graphics.Color;

class TurnHandler
{
	private Map map;

	final int[] orderToColor = {0, 1};

	int turn;
	int day;

	public TurnHandler(Map map)
	{

		this.map = map;
	}

	public void endTurn()
	{
		turn++;
		if (turn >= orderToColor.length)
		{
			day++;
			turn = 0;
		}

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
