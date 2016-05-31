package com.artlessavian.whatsanairport;

import com.badlogic.gdx.graphics.Color;

import java.util.LinkedList;

public class Cursor implements InputReceiver
{
	private final Map map;

	int x;
	int y;
	Unit selectedUnit;
	boolean wasOutside = false;
	boolean dontModify = false;
	int movementCost;
	LinkedList<UnitInstruction> instructions;

	public Cursor(Map map)
	{
		this.map = map;
		instructions = new LinkedList<>();
	}

	@Override
	public boolean up()
	{
		if (y < map.height - 1)
		{
			y++;
			if (selectedUnit != null)
			{
				this.pushDir(UnitInstruction.UP);
			}
		}
		return true;
	}

	@Override
	public boolean down()
	{
		if (y > 0)
		{
			y--;
			if (selectedUnit != null)
			{
				this.pushDir(UnitInstruction.DOWN);
			}
		}
		return true;
	}

	@Override
	public boolean left()
	{
		if (x > 0)
		{
			x--;
			if (selectedUnit != null)
			{
				this.pushDir(UnitInstruction.LEFT);
			}
		}
		return true;
	}

	@Override
	public boolean right()
	{
		if (x < map.width - 1)
		{
			x++;
			if (selectedUnit != null)
			{
				this.pushDir(UnitInstruction.RIGHT);
			}
		}
		return true;
	}

	public void pushDir(UnitInstruction unitInstr)
	{
		if (!selectedUnit.getRangeInfo().rangeCalcd) {selectedUnit.calculateMovement();}

		if (!selectedUnit.getRangeInfo().attackable.contains(map.tileMap[y][x]))
		{
			dontModify = true;
			wasOutside = true;
			instructions.clear();
		} // Attackable does contain
		else if (wasOutside || movementCost >= selectedUnit.unitInfo.movement)
		{
			wasOutside = false;
			instructions.clear();

			Tile current = map.tileMap[y][x];
			if (!selectedUnit.getRangeInfo().movable.contains(map.tileMap[y][x]))
			{
				Tile next = selectedUnit.getRangeInfo().attackableFrom.get(current);
				instructions.addFirst(next.getNeighbor(current));
				current = next;
			}
			movementCost = selectedUnit.getRangeInfo().movementCost.get(current);
			while (!current.equals(selectedUnit.tile))
			{
				Tile next = selectedUnit.getRangeInfo().cameFrom.get(current);
				instructions.addFirst(next.getNeighbor(current));
				current = next;
			}
		}
		else if (selectedUnit.getRangeInfo().movable.contains(map.tileMap[y][x]))
		{
			if (dontModify) {dontModify = false;}
			else
			{
				if (!instructions.isEmpty() && (instructions.getLast().id + 2) % 4 == unitInstr.id)
				{
					instructions.removeLast();
					movementCost -= map.tileMap[y][x].tileInfo.movementCost;
				}
				else
				{
					instructions.add(unitInstr);
					movementCost += map.tileMap[y][x].tileInfo.movementCost;
				}
			}
		}
		else
		{
			dontModify = true;
		}
		System.out.println(this.instructions);
	}

	@Override
	public boolean select()
	{
		if (selectedUnit == null)
		{
			if (map.tileMap[y][x].unit != null && map.tileMap[y][x].unit.instructions.isEmpty())
			{
				selectedUnit = map.tileMap[y][x].unit;
				selectedUnit.selector = this;

				if (!selectedUnit.getRangeInfo().rangeCalcd) {selectedUnit.calculateMovement();}

				for (Tile t : selectedUnit.getRangeInfo().attackable)
				{
					t.highlight.add(Color.RED);
				}
				for (Tile t : selectedUnit.getRangeInfo().movable)
				{
					t.highlight.add(Color.BLUE);
				}
			}
		}
		else
		{
			for (Tile t : selectedUnit.getRangeInfo().attackable)
			{
				t.highlight.remove(Color.RED);
			}
			for (Tile t : selectedUnit.getRangeInfo().movable)
			{
				t.highlight.remove(Color.BLUE);
			}

			Tile finalDestination = selectedUnit.tile;

			for (UnitInstruction ui : instructions)
			{
				// TODO This can crash for some reason
				finalDestination = finalDestination.neighbors[ui.id];
			}

			movementCost = 0;
			instructions.add(UnitInstruction.WAIT);
			selectedUnit.receiveInstructions(instructions, finalDestination);
			selectedUnit.selector = null;
			instructions.clear();
			selectedUnit = null;
		}

		return true;
	}

	@Override
	public boolean cancel()
	{
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button)
	{
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer)
	{
		return false;
	}
}
