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

	public static void uvGarbage(Sprite sprite, int number)
	{
		sprite.setU(sprite.getTexture().getHeight() / (float)sprite.getTexture().getWidth() * number);
		sprite.setU2(sprite.getTexture().getHeight() / (float)sprite.getTexture().getWidth() * (number + 1));
	}

	// Color Stuff
	static final Color selectBlue = new Color(0.8f, 0.8f, 1f, 1f);
	static Color selectRed = new Color(1f, 0.6f, 0.6f, 1f);

	static LinkedList<Color> dangerZones = new LinkedList<Color>();
	static int inUse = 0;

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
