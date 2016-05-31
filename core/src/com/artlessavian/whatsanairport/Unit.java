package com.artlessavian.whatsanairport;

import java.util.*;

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

	Cursor selector;
	LinkedList<UnitInstruction> instructions;
	float accumulator;

	public Unit(UnitInfo info, Tile tile)
	{
		this.unitInfo = info;
		this.tile = tile;

		this.rangeInfo = new RangeInfo(this);

		this.selector = null;
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
//		if (other.unit != null) // TODO
//		{
//			return true;
//		}

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
			}
		}
		else
		{
			switch (instruction)
			{
				case WAIT: {break;}
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
		this.tile.unit = null;
		finalTile.unit = this;
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
