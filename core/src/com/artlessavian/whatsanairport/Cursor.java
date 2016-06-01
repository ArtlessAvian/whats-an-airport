package com.artlessavian.whatsanairport;

import com.badlogic.gdx.graphics.Color;

import java.util.LinkedList;

public class Cursor implements InputReceiver
{
	private final Map map;

	int x;
	int y;
	Unit selectedUnit;
	int movementCost;
	LinkedList<UnitInstruction> instructions;

	int lastTileCursored = 0;

	public Cursor(Map map)
	{
		this.map = map;
		instructions = new LinkedList<>();
	}

	public void rePath(boolean attackMove)
	{
		instructions.clear();

		Tile current = map.tileMap[y][x];
		if (attackMove)
		{
			current = selectedUnit.getRangeInfo().attackableFrom.get(current);
		}
		movementCost = selectedUnit.getRangeInfo().movementCost.get(current);
		while (!current.equals(selectedUnit.tile))
		{
			Tile next = selectedUnit.getRangeInfo().cameFrom.get(current);
			instructions.addFirst(next.getNeighbor(current));
			current = next;
		}
	}

	public void pushDir(UnitInstruction unitInstr)
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

			if (movementCost > selectedUnit.unitInfo.movement) {rePath(false);}
		}
	}

	public void inputtedDir(UnitInstruction unitInstr)
	{
		// 0 Movable, 1 Attackable, 2 Non
		int thisTileCursored = 0;

		if (!selectedUnit.getRangeInfo().movable.contains(map.tileMap[y][x]))
		{
			thisTileCursored++;
			if (!selectedUnit.getRangeInfo().attackable.contains(map.tileMap[y][x]))
			{
				thisTileCursored++;
			}
		}

		switch (thisTileCursored + lastTileCursored * 3)
		{
			case 0: {pushDir(unitInstr); break;} // move to move
			case 1: {break;} // move to attack
			case 2: {instructions.clear(); movementCost = 0; break;} // move to empty (wont happen)
			case 3: {rePath(false); break;} // attack to move
			case 4: {rePath(true); break;} // attack to attack
			case 5: {instructions.clear(); movementCost = 0; break;} // attack to empty
			case 6: {rePath(false); break;} // empty to move (wont happen)
			case 7: {rePath(true); break;} // empty to attack
			case 8: {break;} // empty to empty
		}

		lastTileCursored = thisTileCursored;
	}

	@Override
	public boolean up()
	{
		if (y < map.height - 1)
		{
			y++;
			if (selectedUnit != null)
			{
				this.inputtedDir(UnitInstruction.UP);
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
				this.inputtedDir(UnitInstruction.DOWN);
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
				this.inputtedDir(UnitInstruction.LEFT);
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
				this.inputtedDir(UnitInstruction.RIGHT);
			}
		}
		return true;
	}

	@Override
	public boolean select()
	{
		if (selectedUnit == null)
		{
			if (map.tileMap[y][x].getUnit() != null && map.tileMap[y][x].getUnit().instructions.isEmpty())
			{
				lastTileCursored = 0;
				movementCost = 0;

				selectedUnit = map.tileMap[y][x].getUnit();
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
	public boolean touchDown(int screenX, int screenY, float tileX, float tileY)
	{
		touchDragged(screenX, screenY, tileX,tileY);

		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, float tileX, float tileY)
	{
		select();
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, float tileX, float tileY)
	{
		while ((int)tileX > x) {this.right();}
		while ((int)tileX < x) {this.left();}
		while ((int)tileY > y) {this.up();}
		while ((int)tileY < y) {this.down();}

		return true;
	}
}
