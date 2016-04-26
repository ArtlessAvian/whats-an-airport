package com.artlessavian.whatsanairport;

public class DayAndCoHandler
{
	public int day;
	public int turn;
	private String[] order = {"Red", "Blue"};
	public String team;

	public void nextDay()
	{
		turn++;
		if (turn >= order.length) {turn -= order.length; day++;}
		team = order[turn];
	}
}
