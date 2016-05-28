package com.artlessavian.whatsanairport;

public class Tile
{
	TileInfo tileInfo;
	Unit unit;

	Tile[] neighbors;

	public Tile(TileInfo tileInfo)
	{
		this.tileInfo = tileInfo;
		this.unit = null;

		neighbors = new Tile[4];
	}
}
