package com.artlessavian.whatsanairport;

public enum UnitInstruction
{
	RIGHT(0, true), UP(1, true), LEFT(2, true), DOWN(3, true),

	WAIT(4, false), ATTACK(5, false), CAPTURE(6, false);

	final int id;
	final boolean isDir;

	UnitInstruction(int id, boolean isDir)
	{
		this.id = id;
		this.isDir = isDir;
	}
}
