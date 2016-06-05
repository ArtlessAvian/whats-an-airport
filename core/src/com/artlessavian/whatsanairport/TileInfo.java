package com.artlessavian.whatsanairport;

public enum TileInfo
{
	PLAIN(0, 1),
	MOUNTAIN(1, 2),
	RIVER(2, 2);

	final int id;
	final int movementCost;

	TileInfo(int id, int movementCost)
	{
		this.id = id;
		this.movementCost = movementCost;
	}
}
