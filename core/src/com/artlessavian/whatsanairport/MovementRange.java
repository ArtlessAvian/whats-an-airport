package com.artlessavian.whatsanairport;

import java.util.HashMap;
import java.util.LinkedList;

public class MovementRange
{
	LinkedList<MapTile> movable;
	HashMap<MapTile, Integer> movementCost;
	HashMap<MapTile, MapTile> cameFrom;

	LinkedList<MapTile> attackable;
	LinkedList<MapTile> edgeAttackable;

	public MovementRange()
	{
		movable = new LinkedList<MapTile>();
		movementCost = new HashMap<MapTile, Integer>();
		cameFrom = new HashMap<MapTile, MapTile>();

		attackable = new LinkedList<MapTile>();
		edgeAttackable = new LinkedList<MapTile>();
	}
}
