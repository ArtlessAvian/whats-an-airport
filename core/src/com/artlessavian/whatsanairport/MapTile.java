package com.artlessavian.whatsanairport;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

class MapTile
{
	private int x = -1;
	private int y = -1;

	Unit unit;
	private final WarsConst.TerrainType terrainType;
	final Sprite sprite;

	private Color targetColor;
	private int colorPriority;

	final List<MapTile> neighbors;

	public MapTile(int x, int y, WarsConst.TerrainType tileType, TextureRegion[] tiles)
	{
		this.x = x;
		this.y = y;
		this.terrainType = tileType;

		neighbors = new LinkedList<MapTile>();

		targetColor = Color.WHITE;
		sprite = new Sprite(tiles[WarsConst.getID(tileType)]);
		sprite.setSize(1, 1);
		sprite.setOrigin(0.5f, 0.5f);
		sprite.setPosition(x, y);
	}

	public void createUnit()
	{
		unit = new Unit(this);
	}

	public LinkedList<MapTile> getMovement(int move)
	{
		LinkedList<MapTile> frontier = new LinkedList<MapTile>();
		LinkedList<MapTile> visited = new LinkedList<MapTile>();
		HashMap<MapTile, Integer> movementCost = new HashMap<MapTile, Integer>();
		frontier.add(this);
		visited.add(this);
		movementCost.put(this, move);

		while (!frontier.isEmpty())
		{
			MapTile current = frontier.removeFirst();
			for (MapTile t : current.neighbors)
			{
				if (!visited.contains(t) && movementCost.get(current) - WarsConst.getFootMoveCost(t.terrainType) >= 0)
				{
					frontier.add(t);
					visited.add(t);
					movementCost.put(t, movementCost.get(current) - WarsConst.getFootMoveCost(t.terrainType));
				}
			}
		}

		return visited;
	}

	public LinkedList<MapTile> getAttack(int move, int range)
	{
		LinkedList<MapTile> covered = new LinkedList<MapTile>();

		if (range == 1)
		{
			LinkedList<MapTile> movement = this.getMovement(move);
			for (MapTile t : movement)
			{
				for (MapTile neighbors : t.getNeighbors())
				{
					if (!covered.contains(neighbors)) {covered.add(neighbors);}
				}
			}
		} else
		{
			// TODO ranged thingies
		}

		return covered;
	}

	private List<MapTile> getNeighbors()
	{
		return neighbors;
	}

	public void setHighlight(Color target, int priority)
	{
		if (priority > colorPriority)
		{
			targetColor = target;
			colorPriority = priority;
		}
	}

	public void clearHighlight()
	{
		colorPriority = 0;
		targetColor = Color.WHITE;
	}

	public void draw(SpriteBatch batch)
	{
		sprite.setColor(sprite.getColor().lerp(targetColor, 0.3f));

		sprite.draw(batch);
		if (unit != null)
		{
			batch.draw(unit.texture, x, y, 1, unit.health / 100f);
		}
	}
}
