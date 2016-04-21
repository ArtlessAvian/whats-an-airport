package com.artlessavian.whatsanairport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class RangeInfo
{
	public final ArrayList<MapTile> movable;
	public final HashMap<MapTile, Integer> movementCost;
	public final HashMap<MapTile, MapTile> cameFrom;

	public final ArrayList<MapTile> attackable;

	public RangeInfo(MapTile start, int move, String team, boolean canAttackAfterMove, int minIndir, int maxIndir)
	{
		movable = new ArrayList<MapTile>();
		movementCost = new HashMap<MapTile, Integer>();
		cameFrom = new HashMap<MapTile, MapTile>();
		attackable = new ArrayList<MapTile>();

		calculate(start, move, team, canAttackAfterMove, minIndir, maxIndir);
	}

	private void calculate(MapTile start, int move, String team, boolean canAttackAfterMove, int minIndir, int maxIndir)
	{
		// Dijkstra's for movement
		LinkedList<MapTile> frontier = new LinkedList<MapTile>();

		frontier.add(start);
		movable.add(start);
		movementCost.put(start, 0);

		while (!frontier.isEmpty())
		{
			// Get least moved
			MapTile current = null;
			int cost = 0;

			for (MapTile t : frontier)
			{
				if (current == null || movementCost.get(t) < cost)
				{
					cost = movementCost.get(t);
					current = t;
				}
			}

			frontier.remove(current);

			// Expand
			for (MapTile neighbor : current.neighborToDir.keySet())
			{
				if (!movable.contains(neighbor))
				{
					int newCost = cost + (neighbor.terrainType.infantryMove);
					if (newCost <= move && (neighbor.unit == null || neighbor.unit.team.equals(team)))
					{
						frontier.add(neighbor);
						movable.add(neighbor);
						movementCost.put(neighbor, newCost);
						cameFrom.put(neighbor, current);
					}
				}
			}
		}

//		Iterator<MapTile> iter = visited.iterator();
//		while (iter.hasNext())
//		{
//			MapTile next = iter.next();
//			if (next.unit != null && next.unit != start.unit)
//			{
//				iter.remove();
//			}
//		}

		if (!canAttackAfterMove)
		{

		} else
		{
			// Direct units should be able to reach moved areas
			attackable.addAll(movable);

			for (MapTile t : movable)
			{
				for (MapTile n : t.neighborToDir.keySet())
				{
					if (!attackable.contains(n))
					{
						attackable.add(n);
					}
				}
			}
		}
	}
}
