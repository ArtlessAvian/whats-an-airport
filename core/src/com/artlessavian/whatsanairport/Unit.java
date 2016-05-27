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
	public static class UnitInfo
	{
		public String type = "Soldier";

		public final int movement = 3;

		public final boolean isDirect = true;
		public final int minIndirectRange = 0;
		public final int maxIndirectRange = 0;
	}

	public int health = 10;

	public UnitInfo unitInfo;

	public boolean isDangerZoned;
	private RangeInfo oldDangerZone;
	Color dangerColor;

	public final String team;
	public boolean used = false;

	public MapTile tile;

	public final Sprite sprite;
	public final TextureRegion firstFrame;

	static HashMap<String, Texture> textures;
	private BattleScreen battle;

	public Unit(MapTile tile, UnitInfo unitInfo, String team)
	{
		this.battle = BattleScreen.getInstance();

		this.unitInfo = unitInfo;

		this.sprite = new Sprite(textures.get(team + "/" + unitInfo.type));
		sprite.setRegion(0, 0, sprite.getTexture().getHeight(), sprite.getTexture().getHeight());
		sprite.setOrigin(0.5f, 0.5f);
		sprite.setPosition(tile.x, tile.y);
		firstFrame = TextureRegion.split(sprite.getTexture(), sprite.getTexture().getHeight(), sprite.getTexture().getHeight())[0][0];

		this.team = team;

		this.tile = tile;
		tile.unit = this;
	}

	public RangeInfo getRange()
	{
		return new RangeInfo(tile, unitInfo.movement, this);
	}

	public ArrayList<Unit> getAttackableUnits(boolean moved)
	{
		if (moved && !unitInfo.isDirect) {return new ArrayList<Unit>();}

		RangeInfo temp = new RangeInfo(tile, 0, this);
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
		if (health > 0)
		{
			if (dangerColor == null) {registerColor();}

			oldDangerZone = new RangeInfo(tile, unitInfo.movement, this);
			for (MapTile t : oldDangerZone.attackable)
			{
				t.register(this, dangerColor);
			}

			sprite.setColor(dangerColor);

			isDangerZoned = true;

			tile.debugSpin = true;
		}
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

		sprite.setColor(Color.WHITE);

		isDangerZoned = false;
		oldDangerZone = null;

		tile.debugSpin = false;
		tile.sprite.setRotation(0);
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
			oldDangerZone = new RangeInfo(this.tile, unitInfo.movement, this);

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

		if (!isCounter && other.unitInfo.isDirect)
		{
			if (other.getAttackableUnits(false).contains(this))
			{
				other.attack(this, true);
			}
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

		battle.screenShake.add(0, 1.2f, 0);
		battle.screenShake.rotate((float)(Math.random() * 360), 0, 0, 1);
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
		if (!used && !unitInfo.isDirect) {sprite.rotate(1);}

		WarsConst.uvTime(sprite, (int)(2 + 1.9 * Math.cos(Gdx.graphics.getFrameId() / 20f)), 4);
		sprite.setSize(1, health / 20f + 0.5f);
		sprite.draw(batch);

		if (health != 10)
		{
			batch.draw(WarsConst.healthTextures[health - 1], sprite.getX() + 3 / 4f, sprite.getY(), 1 / 4f, 1 / 2f);
		}
	}
}
