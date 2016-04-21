package com.artlessavian.whatsanairport;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.*;

public class MapTile
{
	private final BattleScreen battle;

	public int x = -1;
	public int y = -1;

	public Unit unit;
	public final WarsConst.TerrainType terrainType;
	private final Sprite sprite;

	boolean debug; // TODO remove lol

	private final ArrayList<Color> colorPile;
	final HashMap<Object, Color> colorRegister;

	// BiMap?
	public final HashMap<MapTile, WarsConst.CardinalDir> neighborToDir;
	public final EnumMap<WarsConst.CardinalDir, MapTile> dirToNeighbor;

	public MapTile(int x, int y, WarsConst.TerrainType tileType, Texture tiles)
	{
		this.battle = BattleScreen.getInstance();

		this.x = x;
		this.y = y;
		this.terrainType = tileType;

		neighborToDir = new HashMap<MapTile, WarsConst.CardinalDir>();
		dirToNeighbor = new EnumMap<WarsConst.CardinalDir, MapTile>(WarsConst.CardinalDir.class);

		colorPile = new ArrayList<Color>();
		colorRegister = new HashMap<Object, Color>();

		sprite = new Sprite(tiles);
		WarsConst.uvTime(sprite, 0, 1);
		WarsConst.uvValue(sprite, terrainType.id, WarsConst.TerrainType.values().length);

		sprite.setSize(1, 1);
		sprite.setOrigin(0.5f, 0.5f);
		sprite.setPosition(x, y);
	}

	public void createUnit(String type)
	{
		new Unit(this, type);
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
