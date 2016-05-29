package com.artlessavian.whatsanairport;

import java.util.ArrayList;

class Map
{
	final Tile[][] tileMap;
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

		for (int y = 0; y < height; y++)
		{
			String[] row = tokens[height + 1 - y].split(",");
			for (int x = 0; x < width; x++)
			{
				this.tileMap[y][x] = new Tile(mapPalette.get(Integer.parseInt(row[x])), x, y);
			}
		}

		for (int y = 0; y < height; y++)
		{
			String[] row = tokens[height + 1 - y].split(",");
			for (int x = 0; x < width; x++)
			{
				try {this.tileMap[y][x].neighbors[0] = this.tileMap[y][x+1];} catch(Exception e) {}
				try {this.tileMap[y][x].neighbors[1] = this.tileMap[y+1][x];} catch(Exception e) {}
				try {this.tileMap[y][x].neighbors[2] = this.tileMap[y][x-1];} catch(Exception e) {}
				try {this.tileMap[y][x].neighbors[3] = this.tileMap[y-1][x];} catch(Exception e) {}
			}
		}
	}
}