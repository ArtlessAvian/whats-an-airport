package com.artlessavian.whatsanairport;

import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.HashSet;

class Tile
{
	Map map;

	boolean debug;

	final TileInfo tileInfo;

	private Unit unit;
	final HashSet<Unit> hasRangeHere;

	final int x;
	final int y;

	final Tile[] neighbors;
	public ArrayList<Color> highlight;

	public Tile(Map map, TileInfo tileInfo, int x, int y)
	{
		this.map = map;

		this.tileInfo = tileInfo;
		this.unit = null;

		this.hasRangeHere = new HashSet<>();

		this.x = x;
		this.y = y;

		this.neighbors = new Tile[4];

		this.highlight = new ArrayList<>();
	}

	public UnitInstruction getNeighbor(Tile other)
	{
		try
		{
			if (neighbors[0].equals(other)) {return UnitInstruction.RIGHT;}
		}
		catch (Exception e)
		{
		}
		try
		{
			if (neighbors[1].equals(other)) {return UnitInstruction.UP;}
		}
		catch (Exception e)
		{
		}
		try
		{
			if (neighbors[2].equals(other)) {return UnitInstruction.LEFT;}
		}
		catch (Exception e)
		{
		}
		try
		{
			if (neighbors[3].equals(other)) {return UnitInstruction.DOWN;}
		}
		catch (Exception e)
		{
		}

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
