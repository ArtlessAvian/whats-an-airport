package com.artlessavian.whatsanairport;

import com.badlogic.gdx.graphics.Color;

public class WarsConst
{
	// War. War never changes.

	static final Color selectBlue = new Color(0.8f, 0.8f, 1f, 1f);
	static Color selectRed = new Color(1f, 0.6f, 0.6f, 1f);

	public static int getID(TerrainType type)
	{
		switch (type)
		{
			case PLAINS: {return 0;}
			case MOUNTAIN: {return 1;}
			case RIVER: {return 2;}
		}
		return 0;
	}

	public static TerrainType getTerrain(int id)
	{
		switch (id)
		{
			case 0: {return TerrainType.PLAINS;}
			case 1: {return TerrainType.MOUNTAIN;}
			case 2: {return TerrainType.RIVER;}
		}
		return TerrainType.PLAINS;
	}

	public static int getFootMoveCost(TerrainType type)
	{
		switch (type)
		{
			case PLAINS: {return 1;}
			case MOUNTAIN: {return 2;}
			case RIVER: {return 3;}
		}
		return 3333;
	}

	public enum CardinalDir
	{
		UP, DOWN, LEFT, RIGHT
	}

	public enum TerrainType
	{
		PLAINS, MOUNTAIN, RIVER
	}
}
