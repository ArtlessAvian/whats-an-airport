package com.artlessavian.whatsanairport;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

class Unit
{
	final UnitInfo unitInfo;
	final int owner;

	Tile trueTile; // logic
	Tile tile; // visual
	Tile lastTile;

	private final RangeInfo rangeInfo;
	int health = 10;
	// TODO: Cancelling causes invalid movement from fuel costs ==> crash
	int fuel = 30;

	boolean isAnimating = false;

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
	final ArrayList<UnitInstruction> instructionsList;

	float accumulator;

	public Unit(UnitInfo info, Tile tile, int owner)
	{
		this.unitInfo = info;
		this.trueTile = tile;
		this.tile = tile;
		this.lastTile = tile;
		this.owner = owner;

		this.rangeInfo = new RangeInfo(this);

		this.selected = false;
		this.instructionsList = new ArrayList<UnitInstruction>();
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

	void receiveInstructions(LinkedList<UnitInstruction> received, Integer movementCost, Tile finalDestination)
	{
		isAnimating = true;
		trueTile.setUnit(null);
		trueTile = finalDestination;
		finalDestination.setUnit(this);

		fuel -= movementCost;

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
		// Only visual stuff i guess

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
			isAnimating = false;
		}
	}

	public void attack(Unit other)
	{
		other.health -= this.health / 2;
		if (other.checkDead())
		{
			return;
		}
		if (other.unitInfo.isDirect)
		{
			for (Tile t : other.trueTile.neighbors)
			{
				if (t != null && t.equals(this.trueTile))
				{
					other.counterAttack(this);
					break;
				}
			}
		}

		endTurn();
	}

	public void endTurn()
	{
		done = true;

		this.instructions = null;
	}

	public void counterAttack(Unit other)
	{
		other.health -= this.health / 2;
		other.checkDead();
	}

	public boolean checkDead()
	{
		if (this.health <= 0)
		{
			this.trueTile.map.units.remove(this);
			this.trueTile.setUnit(null);

			this.trueTile.map.checkRout(this.owner);

			this.trueTile = null;
			return true;
		}
		return false;
	}
}
