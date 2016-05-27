package com.artlessavian.whatsanairport.ControlStates;

import com.artlessavian.whatsanairport.*;

import java.util.LinkedList;

public class MoveUnitControlState extends CursorControlState
{
	// TODO: RNG crashed here by pathing out of bounds?

	private Unit selectedUnit;
	private final LinkedList<WarsConst.CardinalDir> path;
	private boolean pathStuffInvalid;
	private int movementCost;
	private RangeInfo range;

	private int originX;
	private int originY;

	public MoveUnitControlState(ControlStateSystem controlStateSystem)
	{
		super(controlStateSystem);

		path = new LinkedList<WarsConst.CardinalDir>();
	}

	@Override
	public void onEnter(Object... varargs)
	{
		super.onEnter(varargs[0], varargs[1]);
		
		path.clear();
		movementCost = 0;
		pathStuffInvalid = false;

		originX = (Integer)varargs[0];
		originY = (Integer)varargs[1];

		selectedUnit = (Unit)varargs[2];

		range = selectedUnit.getRange();
		if (selectedUnit.unitInfo.isDirect)
		{
			for (MapTile t : range.attackable)
			{
				t.register(this, WarsConst.selectRed);
				//t.debug = true;
			}
		}
		for (MapTile t : range.movable)
		{
			t.register(this, WarsConst.selectBlue);
		}
	}

	@Override
	public void onExit()
	{
		if (selectedUnit.unitInfo.isDirect)
		{
			for (MapTile t : range.attackable)
			{
				t.deregister(this);
			}
		}
		for (MapTile t : range.movable)
		{
			t.deregister(this);
		}
	}

	@Override
	public void onReturn()
	{
		if (selectedUnit.unitInfo.isDirect)
		{
			for (MapTile t : range.attackable)
			{
				t.register(this, WarsConst.selectRed);
			}

		}
		for (MapTile t : range.movable)
		{
			t.register(this, WarsConst.selectBlue);
		}
	}

	private void pushDirection(WarsConst.CardinalDir dir)
	{
		if (pathStuffInvalid)
		{
			recalculatePath(cursorX, cursorY);
		} else if (range.movable.contains(battle.map.map[cursorX][cursorY]))
		{
			if (path.peek() == WarsConst.CardinalDir.UP && dir == WarsConst.CardinalDir.DOWN)
			{
				path.pop();
				movementCost -= battle.map.map[cursorX][cursorY].terrainType.infantryMove;
			} else if (path.peek() == WarsConst.CardinalDir.DOWN && dir == WarsConst.CardinalDir.UP)
			{
				path.pop();
				movementCost -= battle.map.map[cursorX][cursorY].terrainType.infantryMove;
			} else if (path.peek() == WarsConst.CardinalDir.LEFT && dir == WarsConst.CardinalDir.RIGHT)
			{
				path.pop();
				movementCost -= battle.map.map[cursorX][cursorY].terrainType.infantryMove;
			} else if (path.peek() == WarsConst.CardinalDir.RIGHT && dir == WarsConst.CardinalDir.LEFT)
			{
				path.pop();
				movementCost -= battle.map.map[cursorX][cursorY].terrainType.infantryMove;
			} else
			{
				path.push(dir);
				movementCost += battle.map.map[cursorX][cursorY].terrainType.infantryMove;

				if (movementCost > selectedUnit.unitInfo.movement && range.movable.contains(battle.map.map[cursorX][cursorY]))
				{
					recalculatePath(cursorX, cursorY);
					pathStuffInvalid = true;
				}
			}
		} else if (range.attackable.contains(battle.map.map[cursorX][cursorY]))
		{
			recalculatePath(cursorX, cursorY);
			pathStuffInvalid = true;
		} else
		{
			pathStuffInvalid = true;
		}
	}

	private void recalculatePath(int x, int y)
	{
		MapTile current = battle.map.map[x][y];

		pathStuffInvalid = !range.movable.contains(current);

		if (selectedUnit.unitInfo.isDirect && range.attackable.contains(current) && !range.movable.contains(current))
		{
			current = range.attackableFrom.get(current);
		}

		if (range.movable.contains(current))
		{
			movementCost = range.movementCost.get(current);
			path.clear();

			while (current != battle.map.map[originX][originY])
			{
				MapTile from = range.cameFrom.get(current);
				path.addLast(from.neighborToDir.get(current));
				current = from;
			}
		}
	}

	@Override
	public boolean doDirection(WarsConst.CardinalDir direction)
	{
		if (super.doDirection(direction))
		{
			pushDirection(direction);
			return true;
		}
		return false;
	}


	@Override
	public void pick(int screenX, int screenY, int worldX, int worldY)
	{
		recalculatePath(worldX, worldY);
		super.pick(screenX, screenY, worldX, worldY);
	}

	@Override
	public void weakPick(int screenX, int screenY, int worldX, int worldY)
	{
		recalculatePath(worldX, worldY);
		super.weakPick(screenX, screenY, worldX, worldY);
	}

	@Override
	public void release(int screenX, int screenY, int worldX, int worldY)
	{
		recalculatePath(worldX, worldY);
		super.release(screenX, screenY, worldX, worldY);
	}

	@Override
	public void select()
	{
		if (range.attackable.contains(battle.map.map[cursorX][cursorY]) || range.movable.contains(battle.map.map[cursorX][cursorY]))
		{
			if (battle.map.map[cursorX][cursorY].unit == null || battle.map.map[cursorX][cursorY].unit.team.equals(selectedUnit.team))
			{
				controlStateSystem.setState(MovingUnitControlState.class).onEnter(selectedUnit, path, originX, originY, false);
			} else if (battle.map.map[cursorX][cursorY].unit != null && !battle.map.map[cursorX][cursorY].unit.team.equals(selectedUnit.team))
			{
				controlStateSystem.setState(MovingUnitControlState.class).onEnter(selectedUnit, path, originX, originY, true);
			}
		}
	}

	@Override
	public void cancel()
	{
		controlStateSystem.setState(SelectUnitControlState.class);
	}

	@Override
	public void update(float delta)
	{

	}

	@Override
	public void draw()
	{
		super.draw();

		CommonStateFunctions.drawPath(path, originX, originY, 0);

		battle.main.batch.setProjectionMatrix(battle.main.screenSpace.combined);
		battle.main.font.draw(battle.main.batch, movementCost + " " + selectedUnit.unitInfo.movement, 0, battle.main.font.getLineHeight());
		battle.main.font.draw(battle.main.batch, pathStuffInvalid + "", 0, 2 * battle.main.font.getLineHeight());
	}
}
