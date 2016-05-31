package com.artlessavian.whatsanairport;

import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.HashSet;

class Tile
{
	final TileInfo tileInfo;
	Unit unit;

	final HashSet<Unit> hasRangeHere;

	final int x;
	final int y;

	final Tile[] neighbors;
	public ArrayList<Color> highlight;

	public Tile(TileInfo tileInfo, int x, int y)
	{
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
			if (neighbors[0].equals(other)) {return UnitInstruction.RIGHT;}}
		catch (Exception e)
		{}
		try
		{
			if (neighbors[1].equals(other)) {return UnitInstruction.UP;}
		}
		catch (Exception e)
		{}
		try
		{
			if (neighbors[2].equals(other)) {return UnitInstruction.LEFT;}}
		catch (Exception e)
		{}
		try
		{
			if (neighbors[3].equals(other)) {return UnitInstruction.DOWN;}}
		catch (Exception e)
		{}

		return UnitInstruction.WAIT;
	}
}
