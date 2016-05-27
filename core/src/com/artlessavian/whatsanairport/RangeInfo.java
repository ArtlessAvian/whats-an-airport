package com.artlessavian.whatsanairport;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

public class RangeInfo
{
	public final HashMap<MapTile, Integer> movementCost;
	public final HashMap<MapTile, MapTile> cameFrom;
	public final HashMap<MapTile, MapTile> attackableFrom;

	public final Set<MapTile> attackable;
	public final Set<MapTile> movable;

	public RangeInfo(MapTile start, int movement, Unit unit)
	{
		movementCost = new HashMap<MapTile, Integer>();
		cameFrom = new HashMap<MapTile, MapTile>();
		attackableFrom = new HashMap<MapTile, MapTile>();

		attackable = attackableFrom.keySet();
		movable = cameFrom.keySet();

		calculate(start, movement, unit);
	}

	private void calculate(MapTile start, int movement, Unit unit)
	{
		// Dijkstra's for movement
		LinkedList<MapTile> frontier = new LinkedList<MapTile>();

		frontier.add(start);
		movementCost.put(start, 0);
		cameFrom.put(start, start);

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
				if (unit.unitInfo.isDirect && (current.unit == null || current.unit == unit) && !attackable.contains(neighbor))
				{
					attackableFrom.put(neighbor, current);
				}

				if (!movable.contains(neighbor))
				{
					int newCost = cost + (neighbor.terrainType.infantryMove);
					if (newCost <= movement && (neighbor.unit == null || neighbor.unit.team.equals(unit.team)))
					{
						frontier.add(neighbor);
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

		if (!unit.unitInfo.isDirect)
		{
			// Cheapo Way
			for (int y = -unit.unitInfo.maxIndirectRange; y <= unit.unitInfo.maxIndirectRange; y++)
			{
				for (int x = -unit.unitInfo.maxIndirectRange; x <= unit.unitInfo.maxIndirectRange; x++)
				{
					if (x + y <= unit.unitInfo.maxIndirectRange && x + y >= -unit.unitInfo.maxIndirectRange && x - y <= unit.unitInfo.maxIndirectRange && x - y >= -unit.unitInfo.maxIndirectRange)
					{
						if (x + y <= -unit.unitInfo.minIndirectRange || x + y >= unit.unitInfo.minIndirectRange || x - y <= -unit.unitInfo.minIndirectRange || x - y >= unit.unitInfo.minIndirectRange)
						{
							try
							{
								//BattleScreen.getInstance().map.map[start.x + x][start.y + y].debugSpin = true;
								attackableFrom.put(BattleScreen.getInstance().map.map[start.x + x][start.y + y], start);
							}
							catch (Exception e)
							{
								//lol
							}
						}
					}
				}
			}
		}
	}
}
