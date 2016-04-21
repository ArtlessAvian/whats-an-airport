package com.artlessavian.whatsanairport;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;
import java.util.HashMap;

public class Unit
{
	//private final BattleScreen battle;

	public int health = 10;
	public final int movement = 3;

	public boolean isDangerZoned;
	private RangeInfo oldDangerZone;
	private Color dangerColor;

	public final String team;

	public MapTile tile;

	public final Sprite sprite;
	public final TextureRegion firstFrame;

	static HashMap<String, Texture> textures;

	public Unit(MapTile tile, String type)
	{
		//this.battle = BattleScreen.getInstance();

		this.sprite = new Sprite(textures.get(type));
		sprite.setRegion(0, 0, sprite.getTexture().getHeight(), sprite.getTexture().getHeight());
		sprite.setOrigin(0.5f, 0.5f);
		sprite.setPosition(tile.x, tile.y);

		firstFrame = TextureRegion.split(sprite.getTexture(), sprite.getTexture().getHeight(), sprite.getTexture().getHeight())[0][0];

		this.team = type.replaceAll("/.+", "");
		this.tile = tile;
		tile.unit = this;
	}

	public RangeInfo getRange()
	{
		return new RangeInfo(tile, movement, team, true, 0, 0);
	}

	public ArrayList<Unit> getAttackableUnits(boolean moved)
	{
		RangeInfo temp = new RangeInfo(tile, 0, team, true, 0, 0);
		ArrayList<Unit> attackable = new ArrayList<Unit>();
		for (MapTile t : temp.attackable)
		{
			if (t.unit != null && !t.unit.team.equals(this.team))
			{
				attackable.add(t.unit);
			}
		}

		return attackable;
	}

	public void makeDangerZone()
	{
		if (dangerColor == null) {registerColor();}

		oldDangerZone = new RangeInfo(tile, movement, team, true, 0, 0);
		for (MapTile t : oldDangerZone.attackable)
		{
			t.register(this, dangerColor);
		}

		isDangerZoned = true;
	}

	private void refreshDangerZone()
	{
		removeDangerZone();
		makeDangerZone();
	}

	public void removeDangerZone()
	{
		if (oldDangerZone != null)
		{
			for (MapTile t : oldDangerZone.attackable)
			{
				t.deregister(this);
			}
		}

		isDangerZoned = false;
		oldDangerZone = null;
	}

	private void registerColor()
	{
		dangerColor = WarsConst.registerColor();
	}

	private void deregisterColor()
	{
		WarsConst.unRegisterColor(dangerColor);
		dangerColor = null;
	}

	public Unit move(MapTile target)
	{
		Unit temp;

		if (isDangerZoned)
		{
			removeDangerZone();
			isDangerZoned = true;
		}

		MapTile oldTile = this.tile;

		temp = target.unit;
		this.tile.unit = null;
		target.unit = this;
		this.tile = target;

		for (Object o : oldTile.colorRegister.keySet().toArray())
		{
			try
			{
				Unit u = (Unit)o;
				u.refreshDangerZone();
			}
			catch (ClassCastException e)
			{
				// ;__; teach me coding habits
			}
		}

		for (Object o : target.colorRegister.keySet().toArray())
		{
			try
			{
				Unit u = (Unit)o;
				u.refreshDangerZone();
			}
			catch (ClassCastException e)
			{
				// ;__; teach me coding habits
			}
		}

		if (isDangerZoned)
		{
			oldDangerZone = new RangeInfo(this.tile, movement, team, true, 0, 0);

			for (MapTile t : oldDangerZone.attackable)
			{
				t.register(this, dangerColor);
			}
		}

		return temp;
	}

	public void attack(Unit other, boolean isCounter)
	{
		other.health -= this.health / 2;
		if (other.health <= 0)
		{
			other.die();
			return;
		}

		if (!isCounter)
		{
			other.attack(this, true);
		}
	}

	private void die()
	{
		if (isDangerZoned)
		{
			removeDangerZone();
		}
		deregisterColor();
		this.tile.unit = null;
	}

	public void joined(Unit mainUnit)
	{
		mainUnit.health += this.health;
		if (mainUnit.tile.unit.health > 10) {mainUnit.tile.unit.health = 10;}

		if (isDangerZoned)
		{
			removeDangerZone();
		}
		deregisterColor();
	}

	public void draw(SpriteBatch batch)
	{
		WarsConst.uvTime(sprite, (int)(2 + 1.9 * Math.cos(Gdx.graphics.getFrameId() / 20f)), 4);
		sprite.setSize(1, health / 20f + 0.5f);
		sprite.draw(batch);

		if (health != 10)
		{
			batch.draw(WarsConst.healthTextures[health - 1], sprite.getX() + 3 / 4f, sprite.getY(), 1 / 4f, 1 / 2f);
		}
	}
}
