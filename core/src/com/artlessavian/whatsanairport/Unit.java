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

	Color dangerZoned;

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
		dangerZoned = WarsConst.registerColor();

		for (MapTile t : tile.getRange(movement, team, true, 0, 0).attackable)
		{
			t.register(this, dangerZoned);
		}
	}

	public void refreshDangerZone()
	{
		removeDangerZone();
		makeDangerZone();
	}

	public void removeDangerZone()
	{
		WarsConst.unRegisterColor(dangerZoned);
		dangerZoned = null;

		for (MapTile t : tile.getRange(movement, team, true, 0, 0).attackable)
		{
			t.deregister(this);
		}
	}

	public Unit move(MapTile target)
	{
		Unit temp;

		MovementRange range = tile.getRange(movement, team, true, 0, 0);

		if (range.movable.contains(target))
		{
			if (dangerZoned != null)
			{
				for (MapTile t : tile.getRange(movement, team, true, 0, 0).attackable)
				{
					t.deregister(this);
				}
			}

			temp = target.unit;
			this.tile.unit = null;
			target.unit = this;
			this.tile = target;

			range = this.tile.getRange(movement, team, true, 0, 0);

			if (dangerZoned != null)
			{
				for (MapTile t : range.attackable)
				{
					t.register(this, dangerZoned);
				}
			}

			return temp;
		}

		return null;
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
		if (dangerZoned != null)
		{
			removeDangerZone();
		}
		this.tile.unit = null;
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
