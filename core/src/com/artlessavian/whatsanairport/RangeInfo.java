package com.artlessavian.whatsanairport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class RangeInfo
{
	private Unit unit;

	boolean rangeCalcd;

	final HashMap<Tile, Integer> movementCost;
	final HashMap<Tile, Tile> cameFrom;
	final Set<Tile> movable;
	final HashMap<Tile, Tile> attackableFrom;
	final Set<Tile> attackable;

	public RangeInfo(Unit unit)
	{
		this.unit = unit;

		this.rangeCalcd = false;

		this.movementCost = new HashMap<>();
		this.cameFrom = new HashMap<>();
		this.movable = movementCost.keySet();
		this.attackableFrom = new HashMap<>();
		this.attackable = attackableFrom.keySet();
	}

	void invalidateMovement()
	{
		this.rangeCalcd = false;

		for (Tile t : attackable)
		{
			t.hasRangeHere.remove(this);
		}

		this.movementCost.clear();
		this.cameFrom.clear();
		this.attackableFrom.clear();
	}

	void calculateMovement()
	{
		this.invalidateMovement();

		this.rangeCalcd = true;

		// Dijkstra's for movement
		ArrayList<Tile> frontier = new ArrayList<>();

		frontier.add(unit.tile);
		this.movementCost.put(unit.tile, 0);
		this.cameFrom.put(unit.tile, unit.tile);

		while (!frontier.isEmpty())
		{
			// Get least moved
			Tile current = null;
			int cost = 0;

			for (Tile t : frontier)
			{
				if (this.movementCost.get(t) < cost || current == null)
				{
					cost = this.movementCost.get(t);
					current = t;
				}
			}
			frontier.remove(current);

			// Expand
			for (Tile neighbor : current.neighbors)
			{
				if (neighbor == null) {continue;}

				if (unit.unitInfo.isDirect && (current.unit == null || current.unit == unit) && !this.attackable.contains(neighbor))
				{
					this.attackableFrom.put(neighbor, current);
				}

				if (!this.movable.contains(neighbor))
				{
					int newCost = cost + neighbor.tileInfo.movementCost;
					if (newCost <= unit.unitInfo.movement && (neighbor.unit == null || neighbor.unit.owner == unit.owner))
					{
						frontier.add(neighbor);
						this.movementCost.put(neighbor, newCost);
						this.cameFrom.put(neighbor, current);
					}
				}
			}
		}

		Iterator<Tile> iter = movable.iterator();
		while (iter.hasNext())
		{
			Tile t = iter.next();
			if (t.unit != null && !t.unit.equals(this))
			{
				iter.remove();
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
			t.hasRangeHere.add(unit);
		}
	}
}
