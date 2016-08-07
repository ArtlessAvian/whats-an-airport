package com.artlessavian.whatsanairport;

import com.badlogic.gdx.graphics.Color;

import java.util.LinkedList;

public class Cursor implements InputReceiver
{
	private final InputHandler inputHandler;
	private Map map;

	int x;
	int y;
	Unit selectedUnit;
	int movementCost;
	final LinkedList<UnitInstruction> instructions;

	int lastTileCursored = 0;

	public Cursor(InputHandler inputHandler)
	{
		this.inputHandler = inputHandler;
		instructions = new LinkedList<UnitInstruction>();
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
	public void receivePrevious(InputReceiver previous, Class previousClass)
	{

	}

	@Override
	public void reset(Object[] args)
	{
		map = inputHandler.model.map;
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
		Unit cursored = map.tileMap[y][x].getUnit();
		if (selectedUnit == null)
		{
			if (cursored != null)
			{
				if (cursored.owner == inputHandler.model.turnHandler.turn && cursored.instructions == null && !cursored.done)
				{
					instructions.clear();
					selectedUnit = cursored;

					lastTileCursored = 0;
					movementCost = 0;

					cursored.selected = true;

					if (!cursored.getRangeInfo().rangeCalcd) {cursored.calculateMovement();}

					if (selectedUnit.unitInfo.isDirect)
					{
						for (Tile t : cursored.getRangeInfo().attackable)
						{
							t.highlight.add(Color.RED);
						}
					}
					for (Tile t : cursored.getRangeInfo().movable)
					{
						t.highlight.add(Color.BLUE);
					}
				}
			}
			else
			{
				inputHandler.addState(DayMenu.class, false, false);
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
				// Moving and then canceling out asap causes the instructions to still be carried out
				// I might have fixed it?
				finalDestination = finalDestination.neighbors[ui.id];
			}

			inputHandler.addState(UnitMenu.class, false, false, selectedUnit, finalDestination, selectedUnit.tile);
			
			selectedUnit.receiveInstructions(instructions, movementCost);

			selectedUnit.selected = false;
			selectedUnit = null;
		}
		return true;
	}

	@Override
	public boolean cancel()
	{
		if (selectedUnit != null)
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
		}

		return true;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, float tileX, float tileY)
	{
		touchDragged(screenX, screenY, tileX, tileY);

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
		for (int i = (int)tileX; i < x; i++) {this.left();}
		for (int i = x; i < (int)tileX; i++) {this.right();}
		for (int i = (int)tileY; i < y; i++) {this.down();}
		for (int i = y; i < (int)tileY; i++) {this.up();}

		return true;
	}

	@Override
	public boolean update()
	{
		return false;
	}
}
