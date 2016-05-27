package com.artlessavian.whatsanairport;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.*;

public class MapTile
{
	public int x = -1;
	public int y = -1;

	public boolean isCapturable = false;

	public Unit unit;
	public final WarsConst.TerrainType terrainType;
	final Sprite sprite;

	boolean debugSpin;
	boolean debugSpaz;

	final HashMap<Object, Color> colorRegister;

	// BiMap?
	public final HashMap<MapTile, WarsConst.CardinalDir> neighborToDir;
	public final EnumMap<WarsConst.CardinalDir, MapTile> dirToNeighbor;

	public MapTile(int x, int y, WarsConst.TerrainType tileType, Texture tiles)
	{
		this.x = x;
		this.y = y;
		this.terrainType = tileType;

		neighborToDir = new HashMap<MapTile, WarsConst.CardinalDir>();
		dirToNeighbor = new EnumMap<WarsConst.CardinalDir, MapTile>(WarsConst.CardinalDir.class);

		colorRegister = new HashMap<Object, Color>();

		sprite = new Sprite(tiles);
		WarsConst.uvTime(sprite, 0, 1);
		WarsConst.uvValue(sprite, terrainType.id, WarsConst.TerrainType.values().length);

		sprite.setSize(1, 1);
		sprite.setOrigin(0.5f, 0.5f);
		sprite.setPosition(x, y);
	}

	public void createUnit(String team)
	{
		unit = DecoratedUnitFactory.build(this, team, "Soldier");
	}


	public void register(Object caller, Color target)
	{
		if (colorRegister.containsKey(caller))
		{
			deregister(caller);
		}
		colorRegister.put(caller, target);
	}

	public void deregister(Object object)
	{
		colorRegister.remove(object);
	}

	public void draw(SpriteBatch batch)
	{
		if (colorRegister.values().isEmpty())
		{
			sprite.setColor(sprite.getColor().lerp(Color.WHITE, 0.2f));
		} else
		{
			for (Color c : colorRegister.values())
			{
				// Its a bit hard to see overlap, but its better than looking like puke
				sprite.setColor(sprite.getColor().lerp(c, 0.2f));
			}
		}

		if (debugSpin) {sprite.rotate(Gdx.graphics.getFrameId());}
		if (debugSpaz) {sprite.setPosition((float)(x + Math.random() * 0.3 - 0.15), (float)(y + Math.random() * 0.3 - 0.15));}

		sprite.draw(batch);
	}
}
