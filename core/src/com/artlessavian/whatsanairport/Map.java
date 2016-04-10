package com.artlessavian.whatsanairport;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.HashMap;

public class Map
{
	final MapTile[][] map;
	final int mapWidth;
	final int mapHeight;

	final HashMap<Object, Color> highlightRegister;
	final HashMap<MovementRange, Object> tileToObj;

	public Map(int width, int height)
	{
		map = new MapTile[width][height];
		highlightRegister = new HashMap<Object, Color>();
		tileToObj = new HashMap<MovementRange, Object>();

		mapWidth = width;
		mapHeight = height;
	}

	public void draw(SpriteBatch batch)
	{
		for (int x = 0; x < mapWidth; x++)
		{
			for (int y = 0; y < mapHeight; y++)
			{
				map[x][y].draw(batch);
			}
		}
		for (int x = 0; x < mapWidth; x++)
		{
			for (int y = 0; y < mapHeight; y++)
			{
				if (map[x][y].unit != null) {map[x][y].unit.draw(batch);}
			}
		}
	}
}
