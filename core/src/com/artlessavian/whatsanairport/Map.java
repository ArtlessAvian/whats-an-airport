package com.artlessavian.whatsanairport;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Map
{
	public final MapTile[][] map;
	public final int mapWidth;
	public final int mapHeight;


	public Map(int width, int height)
	{
		map = new MapTile[width][height];
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
				int tileID = (int)(Math.cos((x + 0.5) * (y + 0.5 - mapHeight / 2f) / 10f) * 1.1 + 1.1);

				if (Math.random() > 0.05f)
				{
					map[x][y] = new MapTile(x, y, WarsConst.getTerrain(tileID), terrain);
				}
				else
				{
					map[x][y] = new PropertyTile(x, y, WarsConst.getTerrain(tileID), terrain);
				}
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
					map[x][y].neighborToDir.put(map[x + 1][y], WarsConst.CardinalDir.RIGHT);
					map[x][y].dirToNeighbor.put(WarsConst.CardinalDir.RIGHT, map[x + 1][y]);
				}
				catch (Exception e) {}
				try
				{
					map[x][y].neighborToDir.put(map[x - 1][y], WarsConst.CardinalDir.LEFT);
					map[x][y].dirToNeighbor.put(WarsConst.CardinalDir.LEFT, map[x - 1][y]);
				}
				catch (Exception e) {}
				try
				{
					map[x][y].neighborToDir.put(map[x][y + 1], WarsConst.CardinalDir.UP);
					map[x][y].dirToNeighbor.put(WarsConst.CardinalDir.UP, map[x][y + 1]);
				}
				catch (Exception e) {}
				try
				{
					map[x][y].neighborToDir.put(map[x][y - 1], WarsConst.CardinalDir.DOWN);
					map[x][y].dirToNeighbor.put(WarsConst.CardinalDir.DOWN, map[x][y - 1]);
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
