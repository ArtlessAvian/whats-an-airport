package com.artlessavian.whatsanairport;

import com.badlogic.gdx.graphics.Color;

import java.util.HashSet;

class Tile
{
	final TileInfo tileInfo;
	Unit unit;

	final HashSet<Unit> hasRangeHere;

	private final int x;
	private final int y;

	final Tile[] neighbors;
	public Color highlight;
	public float highlightStrength;

	public Tile(TileInfo tileInfo, int x, int y)
	{
		this.tileInfo = tileInfo;
		this.unit = null;

		this.hasRangeHere = new HashSet<>();

		this.x = x;
		this.y = y;

		this.neighbors = new Tile[4];

		this.highlight = null;
		this.highlightStrength = 0;
	}
}
