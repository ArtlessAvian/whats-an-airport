package com.artlessavian.whatsanairport.ControlStates;

import com.artlessavian.whatsanairport.*;

import java.util.LinkedList;

public class MoveUnitControlState extends CursorControlState
{
	private final ControlStateSystem controlStateSystem;
	private final BattleScreen battle;

	private Unit selectedUnit;
	private final LinkedList<WarsConst.CardinalDir> path;
	private int movementCost;
	private MovementRange range;
	private boolean wasOutside = false;

	private int originX;
	private int originY;

	public MoveUnitControlState(ControlStateSystem controlStateSystem)
	{
		super(controlStateSystem);
		this.controlStateSystem = controlStateSystem;
		this.battle = controlStateSystem.battle;

		path = new LinkedList<WarsConst.CardinalDir>();
	}

	@Override
	public void enter(Object... varargs)
	{
		super.enter(varargs[0], varargs[1]);
		
		path.clear();
		movementCost = 0;
		wasOutside = false;

		originX = (Integer)varargs[0];
		originY = (Integer)varargs[1];

		selectedUnit = (Unit)varargs[2];

		range = selectedUnit.getRange();
		for (MapTile t : range.edgeAttackable)
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
	public void cancelReturn()
	{
		for (MapTile t : range.edgeAttackable)
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
				if (path.peekLast() == WarsConst.CardinalDir.UP && dir == WarsConst.CardinalDir.DOWN)
				{
					path.removeLast();
				} else if (path.peekLast() == WarsConst.CardinalDir.DOWN && dir == WarsConst.CardinalDir.UP)
				{
					path.removeLast();
				} else if (path.peekLast() == WarsConst.CardinalDir.LEFT && dir == WarsConst.CardinalDir.RIGHT)
				{
					path.removeLast();
				} else if (path.peekLast() == WarsConst.CardinalDir.RIGHT && dir == WarsConst.CardinalDir.LEFT)
				{
					path.removeLast();
				} else
				{
					path.addLast(dir);
					movementCost += WarsConst.getFootMoveCost(battle.map.map[cursorX][cursorY].terrainType);

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
		movementCost = range.movementCost.get(current);
		path.clear();

		while (current != battle.map.map[originX][originY])
		{
			MapTile from = range.cameFrom.get(current);
			path.addFirst(from.neighbors.get(current));
			current = from;
		}
	}

	@Override
	public void up()
	{
		super.up();
		if (cursorY < battle.mapHeight)
		{
			pathStuff(WarsConst.CardinalDir.UP);
		}
	}

	@Override
	public void down()
	{
		super.down();
		if (cursorY >= 0)
		{
			pathStuff(WarsConst.CardinalDir.DOWN);
		}
	}

	@Override
	public void left()
	{
		super.left();
		if (cursorX >= 0)
		{
			pathStuff(WarsConst.CardinalDir.LEFT);
		}
	}

	@Override
	public void right()
	{
		super.right();
		if (cursorX < battle.mapWidth)
		{
			pathStuff(WarsConst.CardinalDir.RIGHT);
		}
	}

	@Override
	public void pick(int screenX, int screenY, int x, int y)
	{
		super.pick(screenX, screenY, x, y);
		weakPick(screenX, screenY, x, y);
	}

	@Override
	public void weakPick(int screenX, int screenY, int x, int y)
	{
		super.weakPick(screenX, screenY, x, y);
		if (range.movable.contains(battle.map.map[cursorX][cursorY]))
		{
			recalculatePath();
		}
	}

	@Override
	public void select()
	{
		if (battle.map.map[cursorX][cursorY].unit == null || battle.map.map[cursorX][cursorY].unit.team.equals(selectedUnit.team))
		{
			if (range.movable.contains(battle.map.map[cursorX][cursorY]))
			{
				for (MapTile t : range.movable)
				{
					t.deregister(this);
				}
				for (MapTile t : range.edgeAttackable)
				{
					t.deregister(this);
				}
				controlStateSystem.setState(MovingUnitControlState.class).enter(selectedUnit, path, originX, originY);
			}
		}
	}

	@Override
	public void cancel()
	{
		for (MapTile t : range.movable)
		{
			t.deregister(this);
		}
		for (MapTile t : range.edgeAttackable)
		{
			t.deregister(this);
		}
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

		for (WarsConst.CardinalDir dir : path)
		{
			switch (dir)
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
