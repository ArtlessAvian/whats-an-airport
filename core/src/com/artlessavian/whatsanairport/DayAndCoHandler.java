package com.artlessavian.whatsanairport;

import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;

public class DayAndCoHandler
{
	BattleScreen battle;

	public int day;
	public int turn;
	private String[] order = {"Red", "Blue"};
	private ArrayList<ArrayList<Unit>> dangerZoned = new ArrayList<>();
	public String team;

	DayAndCoHandler(BattleScreen battleScreen)
	{
		battle = battleScreen;

		day = 0;
		turn = 0;
		team = order[0];

		for (int i = order.length; i > 0; i--)
		{
			dangerZoned.add(new ArrayList<Unit>());
		}
	}

	public void nextDay()
	{
		dangerZoned.get(turn).clear();

		for (MapTile[] a : battle.map.map)
		{
			for (MapTile b : a)
			{
				if (b.unit != null)
				{
					b.unit.used = false;
					b.unit.sprite.setRotation(0);
					b.unit.sprite.setColor(Color.WHITE);
					if (b.unit.isDangerZoned)
					{
						b.unit.removeDangerZone();
						dangerZoned.get(turn).add(b.unit);
					}
				}

				// TODO: Remove Poor Fix (well it works)
				b.colorRegister.clear();
			}
		}

		turn++;
		if (turn >= order.length)
		{
			turn -= order.length;
			day++;
		}
		team = order[turn];

		for (Unit dangers : dangerZoned.get(turn))
		{
			if (dangers.health > 0)
			{
				dangers.makeDangerZone();
			}
		}
	}
}
