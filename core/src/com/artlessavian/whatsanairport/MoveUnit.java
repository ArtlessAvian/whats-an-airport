package com.artlessavian.whatsanairport;

import com.badlogic.gdx.graphics.Color;

import java.util.LinkedList;

public class MoveUnit extends InputReceiver
{
	private Map map;

	int x;
	int y;

	LinkedList<UnitInstruction> instructions;
	int lastTileCursored = 0;
	int movementCost;

	Unit selectedUnit;

	public MoveUnit(InputHandler inputHandler)
	{
		super(inputHandler);
		instructions = new LinkedList<UnitInstruction>();
	}

	@Override
	public void reset(Object[] args)
	{
		map = inputHandler.model.map;

		selectedUnit = (Unit)args[0];

		reactivate();

		selectedUnit.selected = true;

		if (!selectedUnit.getRangeInfo().rangeCalcd) {selectedUnit.calculateMovement();}

		if (selectedUnit.unitInfo.isDirect)
		{
			for (Tile t : selectedUnit.getRangeInfo().attackable)
			{
				t.highlight.add(Color.RED);
			}
		}
		for (Tile t : selectedUnit.getRangeInfo().movable)
		{
			t.highlight.add(Color.BLUE);
		}
	}

	@Override
	void reactivate()
	{
		x = selectedUnit.trueTile.x;
		y = selectedUnit.trueTile.y;

		instructions.clear();

		lastTileCursored = 0;
		movementCost = 0;
	}

	private void rePath(boolean attackMove)
	{
		instructions.clear();

		Tile current = map.tileMap[y][x];
		if (attackMove)
		{
			current = selectedUnit.getRangeInfo().attackableFrom.get(current);
		}
		movementCost = selectedUnit.getRangeInfo().movementCost.get(current);
		while (!current.equals(selectedUnit.trueTile))
		{
			Tile next = selectedUnit.getRangeInfo().cameFrom.get(current);
			instructions.addFirst(next.getNeighbor(current));
			current = next;
		}
	}

	private void pushDir(UnitInstruction unitInstr)
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

	private void inputtedDir(UnitInstruction unitInstr)
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

		if (!selectedUnit.unitInfo.isDirect && thisTileCursored == 1)
		{
			thisTileCursored--;
		}

		switch (thisTileCursored + lastTileCursored * 3)
		{
			case 0:
			{
				pushDir(unitInstr);
				break;
			} // move to move
			case 1: {break;} // move to attack
			case 2:
			{
				instructions.clear();
				movementCost = 0;
				break;
			} // move to empty (wont happen)
			case 3:
			{
				rePath(false);
				break;
			} // attack to move
			case 4:
			{
				rePath(true);
				break;
			} // attack to attack
			case 5:
			{
				instructions.clear();
				movementCost = 0;
				break;
			} // attack to empty
			case 6:
			{
				rePath(false);
				break;
			} // empty to move (wont happen)
			case 7:
			{
				rePath(true);
				break;
			} // empty to attack
			case 8: {break;} // empty to empty
		}

		lastTileCursored = thisTileCursored;
	}

	@Override
	boolean up()
	{
		if (y < map.height - 1)
		{
			y++;
			this.inputtedDir(UnitInstruction.UP);

		}
		return true;
	}

	@Override
	boolean down()
	{
		if (y > 0)
		{
			y--;
			this.inputtedDir(UnitInstruction.DOWN);
		}
		return true;
	}

	@Override
	boolean left()
	{
		if (x > 0)
		{
			x--;

			this.inputtedDir(UnitInstruction.LEFT);

		}
		return true;
	}

	@Override
	boolean right()
	{
		if (x < map.width - 1)
		{
			x++;

			this.inputtedDir(UnitInstruction.RIGHT);

		}
		return true;
	}

	@Override
	boolean select()
	{
		for (Tile t : selectedUnit.getRangeInfo().attackable)
		{
			t.highlight.remove(Color.RED);
		}
		for (Tile t : selectedUnit.getRangeInfo().movable)
		{
			t.highlight.remove(Color.BLUE);
		}

		Tile finalDestination = selectedUnit.trueTile;

		for (UnitInstruction ui : instructions)
		{
			// TODO This can crash for some reason
			// Moving and then canceling out asap causes the instructions to still be carried out
			// I might have fixed it?
			finalDestination = finalDestination.neighbors[ui.id];
		}

		inputHandler.addState(MovingUnitWait.class, false, false, selectedUnit, selectedUnit.trueTile);
		selectedUnit.receiveInstructions(instructions, movementCost, selectedUnit.trueTile);

		return true;
	}

	@Override
	boolean cancel()
	{
		for (Tile t : selectedUnit.getRangeInfo().attackable)
		{
			t.highlight.remove(Color.RED);
		}
		for (Tile t : selectedUnit.getRangeInfo().movable)
		{
			t.highlight.remove(Color.BLUE);
		}

		selectedUnit.selected = false;
		selectedUnit = null;

		inputHandler.pop();

		return true;
	}

	@Override
	boolean touchDown(int screenX, int screenY, float tileX, float tileY)
	{
		return false;
	}

	@Override
	boolean touchUp(int screenX, int screenY, float tileX, float tileY)
	{
		return false;
	}

	@Override
	boolean touchDragged(int screenX, int screenY, float tileX, float tileY)
	{
		return false;
	}

	@Override
	boolean update()
	{
		return false;
	}
}
