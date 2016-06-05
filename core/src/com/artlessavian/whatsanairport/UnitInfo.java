package com.artlessavian.whatsanairport;

public enum UnitInfo
{
	SOLDIER(0, 4, true),
	MOTORCYCLE(1, 6, true),
	MECH(2, 3, true),
	TANK(3, 7, true);

	final int id;
	final int movement;
	public final boolean isDirect;
	public int moveFrames = 8;

	UnitInfo(int id, int movement, boolean isDirect)
	{
		this.id = id;
		this.movement = movement;
		this.isDirect = isDirect;
	}
}
