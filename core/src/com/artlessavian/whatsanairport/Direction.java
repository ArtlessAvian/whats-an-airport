package com.artlessavian.whatsanairport;

public enum Direction
{
	RIGHT(0), UP(1), LEFT(2), DOWN(3);

	int id;

	Direction(int id)
	{
		this.id = id;
	}
}
