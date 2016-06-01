package com.artlessavian.whatsanairport;

public enum MenuOptions
{
	WAIT("Wait"),
	ATTACK("Attack"),
	CAPTURE("Capture");

	String name;

	MenuOptions(String name)
	{
		this.name = name;
	}
}
