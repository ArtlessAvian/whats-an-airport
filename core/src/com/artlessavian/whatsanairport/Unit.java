package com.artlessavian.whatsanairport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

class Unit
{
	final UnitInfo unitInfo;
	int owner;
	private final Tile tile;

	boolean selected = false;

	boolean rangeCalcd = false;

	private final HashMap<Tile, Integer> movementCost;
	private final HashMap<Tile, Tile> cameFrom;
	final Set<Tile> movable;
	private final HashMap<Tile, Tile> attackableFrom;
	final Set<Tile> attackable;


	public Unit(UnitInfo info, Tile tile)
	{
		this.unitInfo = info;
		this.tile = tile;

		this.movementCost = new HashMap<>();
		this.cameFrom = new HashMap<>();
		this.movable = cameFrom.keySet();
		this.attackableFrom = new HashMap<>();
		this.attackable = attackableFrom.keySet();

		//this.calculateMovement();
	}

	void calculateMovement()
	{
		rangeCalcd = true;

		// Dijkstra's for movement
		ArrayList<Tile> frontier = new ArrayList<>();

		frontier.add(tile);
		movementCost.put(tile, 0);
		cameFrom.put(tile, tile);

		while (!frontier.isEmpty())
		{
			// Get least moved
			Tile current = null;
			int cost = 0;

			for (Tile t : frontier)
			{
				if (movementCost.get(t) < cost || current == null)
				{
					cost = movementCost.get(t);
					current = t;
				}
			}
			frontier.remove(current);

			// Expand
			for (Tile neighbor : current.neighbors)
			{
				if (neighbor == null) {continue;}

				if (unitInfo.isDirect && (current.unit == null || current.unit == this) && !attackable.contains(neighbor))
				{
					attackableFrom.put(neighbor, current);
				}

				if (!movable.contains(neighbor))
				{
					int newCost = cost + (1); // TODO: Replace with Tile Thingy
					if (newCost <= unitInfo.movement && (neighbor.unit == null || neighbor.unit.owner == this.owner))
					{
						frontier.add(neighbor);
						movementCost.put(neighbor, newCost);
						cameFrom.put(neighbor, current);
					}
				}
			}
		}

//		if (!unitInfo.isDirect)
//		{
//			// Cheapo Way
//			for (int y = -unitInfo.maxIndirectRange; y <= unitInfo.maxIndirectRange; y++)
//			{
//				for (int x = -unitInfo.maxIndirectRange; x <= unitInfo.maxIndirectRange; x++)
//				{
//					if (x + y <= unitInfo.maxIndirectRange && x + y >= -unitInfo.maxIndirectRange && x - y <= unitInfo.maxIndirectRange && x - y >= -unitInfo.maxIndirectRange)
//					{
//						if (x + y <= -unitInfo.minIndirectRange || x + y >= unitInfo.minIndirectRange || x - y <= -unitInfo.minIndirectRange || x - y >= unitInfo.minIndirectRange)
//						{
//							try
//							{
//								//BattleScreen.getInstance().map.map[start.x + x][start.y + y].debugSpin = true;
//								attackableFrom.put(BattleScreen.getInstance().map.map[start.x + x][start.y + y], start);
//							}
//							catch (Exception e)
//							{
//								//lol
//							}
//						}
//					}
//				}
//			}
//		}

		for (Tile t : attackable)
		{
			t.hasRangeHere.add(this);
		}
	}
}
