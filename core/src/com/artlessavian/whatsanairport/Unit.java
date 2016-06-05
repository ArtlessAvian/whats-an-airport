package com.artlessavian.whatsanairport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

class Unit
{
	final UnitInfo unitInfo;
	int owner;
	Tile tile;

	private RangeInfo rangeInfo;

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
	LinkedList<UnitInstruction> instructions;
	float accumulator;

	public Unit(UnitInfo info, Tile tile, int owner)
	{
		this.unitInfo = info;
		this.tile = tile;
		this.owner = owner;

		this.rangeInfo = new RangeInfo(this);

		this.selected = false;
		this.instructions = new LinkedList<>();
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

	public boolean goTo(Tile other)
	{
		invalidateMovement();
		if (other.getUnit() != null && !other.getUnit().equals(this) && (other.getUnit().owner != this.owner || instructions.size() == 1)) // TODO
		{
			return true;
		}

		this.tile = other;
		return false;
	}

	public void doNext()
	{
		UnitInstruction instruction = instructions.removeFirst();
		if (instruction.isDir)
		{
			if (this.goTo(tile.neighbors[instruction.id]))
			{
				instructions.clear();
				instructions.add(UnitInstruction.WAIT);
			}
		}
		else
		{
			switch (instruction)
			{
				case WAIT:
				{
					done = true;
					break;
				}
				case ATTACK: {break;} // TODO
			}
		}
	}

	void receiveInstructions(LinkedList<UnitInstruction> received, Tile finalTile)
	{
		Iterator<UnitInstruction> iter = received.iterator();
		while (iter.hasNext())
		{
			this.instructions.add(iter.next());
		}
		this.tile.setUnit(null);
		if (finalTile.getUnit() != null)
		{
			System.err.println("Yo something died");
		}
		else
		{
			finalTile.setUnit(this);
		}
	}

	public void update()
	{
		if (instructions != null && !instructions.isEmpty())
		{
			this.accumulator++;
			while (!instructions.isEmpty() && (accumulator >= unitInfo.moveFrames || !instructions.peekFirst().isDir))
			{
				if (instructions.peekFirst().isDir)
				{
					accumulator -= unitInfo.moveFrames;
				}
				this.doNext();
			}
		}
	}
}
