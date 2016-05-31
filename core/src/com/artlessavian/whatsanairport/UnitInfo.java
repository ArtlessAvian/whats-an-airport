package com.artlessavian.whatsanairport;

public enum UnitInfo
{
	SOLDIER(0, 4, true),
	TANK(1, 7, true),
	HELICOPTER(2, 10, true);

	final int id;
	final int movement;
	public final boolean isDirect;
	public int moveFrames = 15;

	UnitInfo(int id, int movement, boolean isDirect)
	{
		this.id = id;
		this.movement = movement;
		this.isDirect = isDirect;
	}
}
