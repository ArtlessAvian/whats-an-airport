package com.artlessavian.whatsanairport;

public enum TileInfo
{
	PLAIN(0),
	MOUNTAIN(1),
	RIVER(2);

	final int id;

	TileInfo(int id)
	{
		this.id = id;
	}
}
