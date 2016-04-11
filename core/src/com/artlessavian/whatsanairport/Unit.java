package com.artlessavian.whatsanairport;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

class Unit
{
	BattleScreen battle;

	int health = 10;
	int movement = 3;

	boolean isDangerZoned;
	MovementRange oldDangerZone;
	Color dangerColor;

	String team;

	MapTile tile;

	Sprite sprite;
	TextureRegion firstFrame;

	static HashMap<String, Texture> textures;

	public Unit(BattleScreen battle, MapTile tile, String type)
	{
		this.battle = battle;

		this.sprite = new Sprite(textures.get(type));
		sprite.setRegion(0, 0, sprite.getTexture().getHeight(), sprite.getTexture().getHeight());
		sprite.setPosition(tile.x, tile.y);

		firstFrame = TextureRegion.split(sprite.getTexture(), sprite.getTexture().getHeight(), sprite.getTexture().getHeight())[0][0];

		this.team = type.replaceAll("/.+", "");
		this.tile = tile;
		tile.unit = this;
	}

	public MovementRange getRange()
	{
		return tile.getRange(movement, team, true, 0, 0);

	}

	public void makeDangerZone()
	{
		if (dangerColor == null) {registerColor();}

		oldDangerZone = tile.getRange(movement, team, true, 0, 0);
		for (MapTile t : oldDangerZone.attackable)
		{
			t.register(this, dangerColor);
		}

		isDangerZoned = true;
	}

	public void refreshDangerZone()
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

	public void registerColor()
	{
		dangerColor = WarsConst.registerColor();
	}

	public void deregisterColor()
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

		for(Object o : this.tile.colorRegister.keySet())
		{
			System.out.println("from");
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

		temp = target.unit;
		this.tile.unit = null;
		target.unit = this;
		this.tile = target;

		for(Object o : target.colorRegister.keySet())
		{
			System.out.println("to");
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
			oldDangerZone = this.tile.getRange(movement, team, true, 0, 0);

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

	public void die()
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
		WarsConst.uvGarbage(sprite, (int)(2 + 1.9 * Math.cos(Gdx.graphics.getFrameId() / 20f)));
		sprite.setSize(1, health / 20f + 0.5f);
		sprite.draw(batch);

		if (health != 10)
		{
			batch.draw(WarsConst.healthTextures[health - 1], sprite.getX() + 3 / 4f, sprite.getY(), 1 / 4f, 1 / 2f);
		}

	}
}
