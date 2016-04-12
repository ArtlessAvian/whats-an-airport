package com.artlessavian.whatsanairport;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.LinkedList;

public class WarsConst
{
	// War. War never changes.

	public enum CardinalDir
	{
		UP, DOWN, LEFT, RIGHT
	}

	static TextureRegion[] healthTextures;

	// Used to be cleaner with 1D spritesheets but bleh

	public static void uvTime(Sprite sprite, int timeFrame, int width)
	{
		sprite.setU(timeFrame / (float)width);
		sprite.setU2((timeFrame + 1) / (float)width);

	}

	public static void uvValue(Sprite sprite, int value, int height)
	{
		sprite.setV(value / (float)height);
		sprite.setV2((value + 1) / (float)height);
	}

	// Color Stuff
	public static final Color selectBlue = new Color(0.8f, 0.8f, 1f, 1f);
	public static final Color selectRed = new Color(1f, 0.6f, 0.6f, 1f);

	private static final LinkedList<Color> dangerZones = new LinkedList<Color>();
	private static int inUse = 0;

	public static Color registerColor()
	{
		if (dangerZones.size() == inUse)
		{
			Color newColor = new Color((float)Math.random(), (float)Math.random(), (float)Math.random(), 1);
			dangerZones.add(newColor);
			inUse++;

			return newColor;
		}

		inUse++;
		return dangerZones.get(inUse - 1);
	}

	public static void unRegisterColor(Color color)
	{
		if (dangerZones.contains(color))
		{
			dangerZones.remove(color);
			dangerZones.addLast(color);
			inUse--;
		}
	}

	// Terrain Stuff

	public enum TerrainType
	{
		PLAINS, MOUNTAIN, RIVER
	}

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
			case RIVER: {return 2;}
		}
		return 3333;
	}
}
