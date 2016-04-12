package com.artlessavian.whatsanairport;

import java.util.HashMap;
import java.util.LinkedList;

public class MovementRange
{
	public final LinkedList<MapTile> movable;
	public final HashMap<MapTile, Integer> movementCost;
	public final HashMap<MapTile, MapTile> cameFrom;

	final LinkedList<MapTile> attackable;
	public final LinkedList<MapTile> edgeAttackable;

	public MovementRange()
	{
		movable = new LinkedList<MapTile>();
		movementCost = new HashMap<MapTile, Integer>();
		cameFrom = new HashMap<MapTile, MapTile>();

		attackable = new LinkedList<MapTile>();
		edgeAttackable = new LinkedList<MapTile>();
	}
}
