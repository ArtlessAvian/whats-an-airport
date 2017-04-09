package com.artlessavian.whatsanairport;

import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.HashSet;

class Tile
{
	final Map map;

	boolean debug;

	final TileInfo tileInfo;

	private Unit unit;
	final HashSet<Unit> hasRangeHere;

	final int x;
	final int y;

	final Tile[] neighbors;
	public final ArrayList<Color> highlight;

	public Tile(Map map, TileInfo tileInfo, int x, int y)
	{
		this.map = map;

		this.tileInfo = tileInfo;
		this.unit = null;

		this.hasRangeHere = new HashSet<Unit>();

		this.x = x;
		this.y = y;

		this.neighbors = new Tile[4];

		this.highlight = new ArrayList<Color>();
	}

	public UnitInstruction getNeighbor(Tile other)
	{
		if (neighbors[0] == other) {return UnitInstruction.RIGHT;}
		if (neighbors[1] == other) {return UnitInstruction.UP;}
		if (neighbors[2] == other) {return UnitInstruction.LEFT;}
		if (neighbors[3] == other) {return UnitInstruction.DOWN;}

		return UnitInstruction.WAIT;
	}

	public Unit getUnit()
	{
		return this.unit;
	}

	public void setUnit(Unit newUnit)
	{
		for (Unit u : hasRangeHere)
		{
			if (!u.equals(newUnit))
			{
				u.invalidateMovement();
			}
		}

		this.unit = newUnit;
	}

	public void getAttackable(int minRange, int maxRange, ArrayList<Tile> tiles)
	{
		map.getAttackable(this.x, this.y, minRange, maxRange, tiles);
	}
}
