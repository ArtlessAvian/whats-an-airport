package com.artlessavian.whatsanairport;

public class DecoratedUnitFactory
{
	// Hopefully im not spamming programming words

	public static Unit build(MapTile tile, String team, String type)
	{
		Unit output = new Unit(tile, new Unit.UnitInfo(), team);

		return output;
	}
}
