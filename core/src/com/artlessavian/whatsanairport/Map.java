package com.artlessavian.whatsanairport;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

class Map
{
	final Tile[][] tileMap;
	ArrayList<Unit> units;

	final int width;
	final int height;

	public Map(String mapFile)
	{
		mapFile = mapFile.replaceAll("\\s++", "");
		String[] tokens = mapFile.split(";");

		ArrayList<TileInfo> mapPalette = new ArrayList<>();
		String[] tileInfoString = tokens[0].split(",");
		for (String string : tileInfoString)
		{
			mapPalette.add(TileInfo.valueOf(string));
		}

		String[] size = tokens[1].split(",");
		this.width = Integer.parseInt(size[0]);
		this.height = Integer.parseInt(size[1]);

		this.tileMap = new Tile[height][width];
		this.units = new ArrayList<>();

		for (int y = 0; y < height; y++)
		{
			String[] row = tokens[height + 1 - y].split(",");
			for (int x = 0; x < width; x++)
			{
				this.tileMap[y][x] = new Tile(this, mapPalette.get(Integer.parseInt(row[x])), x, y);
			}
		}

		for (int y = 0; y < height; y++)
		{
			String[] row = tokens[height + 1 - y].split(",");
			for (int x = 0; x < width; x++)
			{
				try {this.tileMap[y][x].neighbors[0] = this.tileMap[y][x + 1];} catch (Exception e) {}
				try {this.tileMap[y][x].neighbors[1] = this.tileMap[y + 1][x];} catch (Exception e) {}
				try {this.tileMap[y][x].neighbors[2] = this.tileMap[y][x - 1];} catch (Exception e) {}
				try {this.tileMap[y][x].neighbors[3] = this.tileMap[y - 1][x];} catch (Exception e) {}
			}
		}
	}

	public void makeUnit(UnitInfo unitInfo, int team, int x, int y)
	{
		if (tileMap[y][x].getUnit() == null)
		{
			Unit u = new Unit(unitInfo, this.tileMap[y][x], team);
			this.tileMap[y][x].setUnit(u);
			units.add(u);
		}
	}

	void update()
	{
		for (Unit unit : units)
		{
			if (unit != null)
			{
				unit.update();
			}
		}
	}

	public void getAttackable(int x, int y, int minRange, int maxRange, ArrayList<Tile> tiles)
	{
		// Cheapo Way
		for (int deltaY = -maxRange; deltaY <= maxRange; deltaY++)
		{
			for (int deltaX = -maxRange; deltaX <= maxRange; deltaX++)
			{
				if (deltaX + deltaY <= maxRange && deltaX + deltaY >= -maxRange && deltaX - deltaY <= maxRange && deltaX - deltaY >= -maxRange)
				{
					if (deltaX + deltaY <= -minRange || deltaX + deltaY >= minRange || deltaX - deltaY <= -minRange || deltaX - deltaY >= minRange)
					{
						try
						{
							//attackableFrom.put(tileMap[x + deltaX][y + deltaY], tileMap[x][y]);
							tiles.add(tileMap[y + deltaY][x + deltaX]);
						}
						catch (Exception e)
						{
							//lol
						}
					}
				}
			}
		}
	}
}
