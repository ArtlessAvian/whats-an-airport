package com.artlessavian.whatsanairport;

import com.badlogic.gdx.graphics.Texture;

import java.util.LinkedList;

class Unit
{
	int health = 100;
	int unitType;
	int team = 0;

	private MapTile tile;

	final Texture texture;

	public Unit(MapTile tile)
	{
		texture = new Texture("Unit.png");
		this.tile = tile;
	}

	public LinkedList<MapTile> getMovement()
	{
		return tile.getMovement(3);
	}

	public LinkedList<MapTile> getAttack()
	{
		return tile.getAttack(3, 1);
	}

	public boolean move(MapTile target)
	{
		if (this.tile.getMovement(3).contains(target) && target.unit == null)
		{
			this.tile.unit = null;
			target.unit = this;
			this.tile = target;
			return true;
		}
		return false;
	}

	public void attack(Unit other)
	{
		other.health -= 20;
		this.health -= 15;
	}
}
