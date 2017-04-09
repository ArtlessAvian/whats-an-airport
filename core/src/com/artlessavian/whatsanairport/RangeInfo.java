package com.artlessavian.whatsanairport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class RangeInfo
{
	private final Unit unit;

	boolean rangeCalcd;

	final HashMap<Tile, Integer> movementCost;
	final HashMap<Tile, Tile> cameFrom;
	final Set<Tile> movable;
	final HashMap<Tile, Tile> attackableFrom;
	final Set<Tile> attackable;

	final ArrayList<Tile> temp;

	public RangeInfo(Unit unit)
	{
		this.unit = unit;

		this.rangeCalcd = false;

		this.movementCost = new HashMap<Tile, Integer>();
		this.cameFrom = new HashMap<Tile, Tile>();
		this.movable = movementCost.keySet();
		this.attackableFrom = new HashMap<Tile, Tile>();
		this.attackable = attackableFrom.keySet();
		this.temp = new ArrayList<Tile>();
	}

	void invalidateMovement()
	{
		this.rangeCalcd = false;
	}

	void calculateMovement()
	{
		this.invalidateMovement();

		for (Tile t : attackable)
		{
			t.hasRangeHere.remove(unit);
		}

		this.movementCost.clear();
		this.cameFrom.clear();
		this.attackableFrom.clear();

		this.rangeCalcd = true;

		// Dijkstra's for movement
		ArrayList<Tile> frontier = new ArrayList<Tile>();

		frontier.add(unit.trueTile);
		this.movementCost.put(unit.trueTile, 0);
		this.cameFrom.put(unit.trueTile, unit.trueTile);

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

			if (unit.unitInfo.isDirect && (current.getUnit() == null || current.getUnit() == unit))
			{
				temp.clear();
				current.getAttackable(unit.unitInfo.minRange, unit.unitInfo.maxRange, temp);
				for (Tile currentRange : temp)
				{
					if (!this.attackable.contains(currentRange))
					{
						this.attackableFrom.put(currentRange, current);
					}
				}
			}

			// Expand
			for (Tile neighbor : current.neighbors)
			{
				if (neighbor == null) {continue;}

				if (!this.movable.contains(neighbor))
				{
					int newCost = cost + neighbor.tileInfo.movementCost;
					if (newCost <= unit.unitInfo.movement && newCost <= unit.fuel && (neighbor.getUnit() == null || neighbor.getUnit().owner == unit.owner))
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
			if (t.getUnit() != null && !t.getUnit().equals(unit))
			{
				iter.remove();
			}
		}

		for (Tile t : attackable)
		{
			t.hasRangeHere.add(unit);
		}
	}
}
