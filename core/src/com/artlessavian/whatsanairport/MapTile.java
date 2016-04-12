package com.artlessavian.whatsanairport;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class MapTile
{
	private final BattleScreen battle;

	int x = -1;
	int y = -1;

	public Unit unit;
	public final WarsConst.TerrainType terrainType;
	private final Sprite sprite;

	boolean debug; // TODO remove lol

	private final ArrayList<Color> colorPile;
	final HashMap<Object, Color> colorRegister;

	public final HashMap<MapTile, WarsConst.CardinalDir> neighbors;

	public MapTile(BattleScreen battle, int x, int y, WarsConst.TerrainType tileType, Texture tiles)
	{
		this.battle = battle;

		this.x = x;
		this.y = y;
		this.terrainType = tileType;

		neighbors = new HashMap<MapTile, WarsConst.CardinalDir>();

		colorPile = new ArrayList<Color>();
		colorRegister = new HashMap<Object, Color>();

		sprite = new Sprite(tiles);
		WarsConst.uvTime(sprite, 0, 1);
		WarsConst.uvValue(sprite, WarsConst.getID(terrainType), WarsConst.TerrainType.values().length);

		sprite.setSize(1, 1);
		sprite.setOrigin(0.5f, 0.5f);
		sprite.setPosition(x, y);
	}

	public void createUnit(String type)
	{
		new Unit(battle, this, type);
	}

	public MovementRange getRange(int move, String team, boolean direct, int minIndir, int maxIndir)
	{
		MovementRange range = new MovementRange();

		LinkedList<MapTile> visited = range.movable;
		LinkedList<MapTile> edgeAttackable = range.edgeAttackable;
		LinkedList<MapTile> attackable = range.attackable;

		// Dijkstra's for movement
		LinkedList<MapTile> frontier = new LinkedList<MapTile>();

		HashMap<MapTile, Integer> movementCost = range.movementCost;
		HashMap<MapTile, MapTile> cameFrom = range.cameFrom;

		frontier.add(this);
		visited.add(this);
		movementCost.put(this, 0);

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
			for (MapTile neighbor : current.neighbors.keySet())
			{
				if (!visited.contains(neighbor))
				{
					int newCost = cost + WarsConst.getFootMoveCost(neighbor.terrainType);
					if (newCost <= move && (neighbor.unit == null || neighbor.unit.team.equals(team)))
					{
						frontier.add(neighbor);
						visited.add(neighbor);
						movementCost.put(neighbor, newCost);
						cameFrom.put(neighbor, current);
					} else if (direct && !edgeAttackable.contains(neighbor))
					{
						edgeAttackable.add(neighbor);
					}
				}
			}
		}

		if (!direct)
		{
			// TODO add indirect attack
		} else
		{
			// Direct units should be able to reach moved areas
			attackable.addAll(visited);
			attackable.addAll(edgeAttackable);
		}

		return range;
	}

	private HashMap<MapTile, WarsConst.CardinalDir> getNeighbors()
	{
		return neighbors;
	}

	public void register(Object caller, Color target)
	{
		if (colorRegister.containsKey(caller))
		{
			deregister(caller);
		}
		colorRegister.put(caller, target);
		colorPile.add(target);
	}

	public void deregister(Object object)
	{
		colorPile.remove(colorRegister.get(object));
		colorRegister.remove(object);
	}

	public void draw(SpriteBatch batch)
	{
		if (colorPile.isEmpty())
		{
			sprite.setColor(sprite.getColor().lerp(Color.WHITE, 0.3f));
		} else
		{
			for (Color c : colorPile)
			{
				// Its a bit hard to see overlap, but its better than looking like puke
				sprite.setColor(sprite.getColor().lerp(c, 0.3f));
			}
		}

		if (debug) {sprite.rotate(Gdx.graphics.getFrameId());}

		sprite.draw(batch);
	}
}
