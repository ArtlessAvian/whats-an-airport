package com.artlessavian.whatsanairport;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

class Unit
{
	final UnitInfo unitInfo;
	int owner;
	Tile tile;
	Tile lastTile;

	private RangeInfo rangeInfo;
	int health = 10;

	public RangeInfo getRangeInfo()
	{
		if (!rangeInfo.rangeCalcd)
		{
			rangeInfo.calculateMovement();
		}
		return rangeInfo;
	}

	public boolean done = false;
	public boolean selected;

	ListIterator<UnitInstruction> instructions;
	UnitInstruction finalInstruction;
	ArrayList<UnitInstruction> instructionsList;

	float accumulator;

	public Unit(UnitInfo info, Tile tile, int owner)
	{
		this.unitInfo = info;
		this.tile = tile;
		this.lastTile = tile;
		this.owner = owner;

		this.rangeInfo = new RangeInfo(this);

		this.selected = false;
		this.finalInstruction = null;
		this.instructionsList = new ArrayList<>();
		this.accumulator = 0;
	}

	public void invalidateMovement()
	{
		rangeInfo.invalidateMovement();
	}

	public void calculateMovement()
	{
		rangeInfo.calculateMovement();
	}

	void receiveInstructions(LinkedList<UnitInstruction> received)
	{
		instructionsList.clear();
		instructionsList.addAll(received);
		this.instructions = instructionsList.listIterator();
	}

	public void doNextDirection()
	{
		invalidateMovement();

		UnitInstruction instruction = instructions.next();
		Tile next = tile.neighbors[instruction.id];

		this.tile = next;
	}

	public void update()
	{
		if (instructions != null && instructions.hasNext())
		{
			this.accumulator++;
			while (instructions.hasNext() && accumulator >= unitInfo.moveFrames)
			{
				accumulator -= unitInfo.moveFrames;
				this.doNextDirection();
			}
		}
		else
		{
			if (finalInstruction != null)
			{
				switch (finalInstruction)
				{
					case WAIT:
					{
						done = true;
						finalInstruction = null;

						this.lastTile.setUnit(null);
						this.tile.setUnit(this);
						this.lastTile = this.tile;

						this.instructions = null;
						break;
					}
					case ATTACK: {break;} // TODO
				}
			}
		}
	}

	public void attack(Unit other)
	{
		other.health -= this.health/2;
		if (other.checkDead())
		{
			return;
		}
		if (other.unitInfo.isDirect)
		{
			for (Tile t : other.tile.neighbors)
			{
				if (t.equals(this.tile))
				{
					other.counterAttack(this);
					break;
				}
			}
		}
	}

	public void counterAttack(Unit other)
	{
		other.health -= this.health/2;
		other.checkDead();
	}

	public boolean checkDead()
	{
		if (this.health <= 0)
		{
			this.tile.map.units.remove(this);
			this.tile.setUnit(null);
			this.tile = null;
			return true;
		}
		return false;
	}
}
