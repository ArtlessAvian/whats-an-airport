package com.artlessavian.whatsanairport.ControlStates;

import com.artlessavian.whatsanairport.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Deque;

public class MoveUnitControlState extends CursorControlState
{
	private Unit selectedUnit;
	private final Deque<WarsConst.CardinalDir> path;
	private int movementCost;
	private RangeInfo range;
	private boolean wasOutside = false;

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
		wasOutside = false;

		originX = (Integer)varargs[0];
		originY = (Integer)varargs[1];

		selectedUnit = (Unit)varargs[2];

		range = selectedUnit.getRange();
		for (MapTile t : range.attackable)
		{
			t.register(this, WarsConst.selectRed);
			//t.debug = true;
		}
		for (MapTile t : range.movable)
		{
			t.register(this, WarsConst.selectBlue);
		}
	}

	@Override
	public void onExit()
	{
		for (MapTile t : range.attackable)
		{
			t.deregister(this);
		}
		for (MapTile t : range.movable)
		{
			t.deregister(this);
		}
	}

	@Override
	public void onReturn()
	{
		for (MapTile t : range.attackable)
		{
			t.register(this, WarsConst.selectRed);
			//t.debug = true;
		}
		for (MapTile t : range.movable)
		{
			t.register(this, WarsConst.selectBlue);
		}
	}

	private void pathStuff(WarsConst.CardinalDir dir)
	{
		if (range.movable.contains(battle.map.map[cursorX][cursorY]))
		{
			if (wasOutside)
			{
				recalculatePath();
				wasOutside = false;
			} else
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

					if (movementCost > selectedUnit.movement && range.movable.contains(battle.map.map[cursorX][cursorY]))
					{
						recalculatePath();
					}
				}
			}
		} else
		{
			wasOutside = true;
		}
	}

	private void recalculatePath()
	{
		MapTile current = battle.map.map[cursorX][cursorY];

		if (range.attackable.contains(current) || range.movable.contains(current))
		{
			if (!range.movable.contains(current) && range.attackable.contains(current))
			{
				current = range.attackableFrom.get(current);
			}

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
			pathStuff(direction);
			return true;
		}
		return false;
	}


	@Override
	public void pick(int screenX, int screenY, int x, int y)
	{
		if (range.attackable.contains(battle.map.map[x][y]))
		{
			recalculatePath();
		}
		super.pick(screenX, screenY, x, y);
	}

	@Override
	public void weakPick(int screenX, int screenY, int x, int y)
	{
		if (range.attackable.contains(battle.map.map[x][y]))
		{
			recalculatePath();
		}
		super.weakPick(screenX, screenY, x, y);
	}

	@Override
	public void release(int screenX, int screenY, int x, int y)
	{
		if (range.attackable.contains(battle.map.map[x][y]))
		{
			recalculatePath();
		}
		super.release(screenX, screenY, x, y);
	}

	@Override
	public void select()
	{
		if (range.attackable.contains(battle.map.map[cursorX][cursorY]))
		{
			if (battle.map.map[cursorX][cursorY].unit == null || battle.map.map[cursorX][cursorY].unit.team.equals(selectedUnit.team))
			{
				controlStateSystem.setState(MovingUnitControlState.class).onEnter(selectedUnit, path, originX, originY, false);
			}
			else if (battle.map.map[cursorX][cursorY].unit != null && !battle.map.map[cursorX][cursorY].unit.team.equals(selectedUnit.team))
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

		int x = originX;
		int y = originY;

		Iterator<WarsConst.CardinalDir> bleh = path.descendingIterator();
		while (bleh.hasNext())
		{
			WarsConst.CardinalDir next = bleh.next();
			switch (next)
			{
				case UP:
				{
					y++;
					break;
				}
				case DOWN:
				{
					y--;
					break;
				}
				case LEFT:
				{
					x--;
					break;
				}
				case RIGHT:
				{
					x++;
					break;
				}
			}

			battle.main.batch.draw(selectedUnit.firstFrame, x + 0.2f, y + 0.2f, 0.6f, 0.6f);
		}

	}
}
