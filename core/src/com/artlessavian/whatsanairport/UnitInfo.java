package com.artlessavian.whatsanairport;

public enum UnitInfo
{
	SOLDIER(0, 4, true, 1, 1),
	MOTORCYCLE(1, 6, false, 2, 4),
	MECH(2, 3, true, 1, 2),
	TANK(3, 7, true, 1, 1);

	final int id;
	final int movement;
	public final boolean isDirect;
	final int minRange;
	final int maxRange;

	public final int moveFrames = 8;

	UnitInfo(int id, int movement, boolean isDirect, int minRange, int maxRange)
	{
		this.id = id;
		this.movement = movement;
		this.isDirect = isDirect;
		this.minRange = minRange;
		this.maxRange = maxRange;
	}
}
