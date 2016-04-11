package com.artlessavian.whatsanairport;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.HashMap;

public class Map
{
	BattleScreen battle;

	final MapTile[][] map;
	final int mapWidth;
	final int mapHeight;

	final HashMap<Object, Color> highlightRegister;
	final HashMap<MovementRange, Object> tileToObj;

	public Map(BattleScreen battle, int width, int height)
	{
		this.battle = battle;

		map = new MapTile[width][height];
		highlightRegister = new HashMap<Object, Color>();
		tileToObj = new HashMap<MovementRange, Object>();

		mapWidth = width;
		mapHeight = height;
	}

	public void debugGeneration(Texture terrain)
	{
		for (int x = 0; x < mapWidth; x++)
		{
			for (int y = 0; y < mapHeight; y++)
			{
				// This makes a cool thingy
				int tileID = (int)(Math.cos(x * y / 2) * 1.5 + 1.5);

				map[x][y] = new MapTile(battle, x, y, WarsConst.getTerrain(tileID), terrain);
			}
		}
	}

	public void establishNeighbors()
	{
		for (int x = 0; x < mapWidth; x++)
		{
			for (int y = 0; y < mapHeight; y++)
			{
				// lol gross
				try
				{
					map[x][y].neighbors.put(map[x + 1][y], WarsConst.CardinalDir.RIGHT);
				}
				catch (Exception e) {}
				try
				{
					map[x][y].neighbors.put(map[x - 1][y], WarsConst.CardinalDir.LEFT);
				}
				catch (Exception e) {}
				try
				{
					map[x][y].neighbors.put(map[x][y + 1], WarsConst.CardinalDir.UP);
				}
				catch (Exception e) {}
				try
				{
					map[x][y].neighbors.put(map[x][y - 1], WarsConst.CardinalDir.DOWN);
				}
				catch (Exception e) {}
			}
		}
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
