package com.artlessavian.whatsanairport;

import com.badlogic.gdx.math.Vector3;

import java.util.LinkedList;

public class MoveUnitControlState implements ControlState
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
	private int cursorX;
	private int cursorY;

	private final Vector3 cursorPos;

	public MoveUnitControlState(ControlStateSystem controlStateSystem)
	{
		this.controlStateSystem = controlStateSystem;
		this.battle = controlStateSystem.battle;

		path = new LinkedList<WarsConst.CardinalDir>();

		cursorPos = new Vector3();
	}

	@Override
	public void enter(Object... varargs)
	{
		path.clear();
		movementCost = 0;
		wasOutside = false;

		originX = (Integer)varargs[0];
		originY = (Integer)varargs[1];
		cursorX = originX;
		cursorY = originY;

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
		if (cursorY < battle.mapHeight - 1)
		{
			cursorY++;
			pathStuff(WarsConst.CardinalDir.UP);
		}
	}

	@Override
	public void down()
	{
		if (cursorY > 0)
		{
			cursorY--;
			pathStuff(WarsConst.CardinalDir.DOWN);
		}
	}

	@Override
	public void left()
	{
		if (cursorX > 0)
		{
			cursorX--;
			pathStuff(WarsConst.CardinalDir.LEFT);
		}
	}

	@Override
	public void right()
	{
		if (cursorX < battle.mapWidth - 1)
		{
			cursorX++;
			pathStuff(WarsConst.CardinalDir.RIGHT);
		}
	}

	@Override
	public void pick(int screenX, int screenY, int x, int y)
	{
		if (cursorX == x && cursorY == y)
		{
			select();
		}
		weakPick(screenX, screenY, x, y);
	}

	@Override
	public void weakPick(int screenX, int screenY, int x, int y)
	{
		cursorX = x;
		cursorY = y;
		if (range.movable.contains(battle.map.map[x][y]))
		{
			recalculatePath();
		}
	}

	@Override
	public void release(int screenX, int screenY, int x, int y)
	{

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
				controlStateSystem.setState(MovingUnitControlState.class);
				controlStateSystem.state.enter(selectedUnit, path, originX, originY);
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
	public void moveCam()
	{
		cursorPos.x = cursorX + 0.5f;
		cursorPos.y = cursorY + 0.5f;

		cursorPos.sub(battle.trueCamPos);
		if (cursorPos.x > 0.3 * battle.world.viewportWidth)
		{
			battle.trueCamPos.x++;
		}
		if (cursorPos.x < -0.3 * battle.world.viewportWidth)
		{
			battle.trueCamPos.x--;
		}
		if (cursorPos.y > 0.3 * battle.world.viewportHeight)
		{
			battle.trueCamPos.y++;
		}
		if (cursorPos.y < -0.3 * battle.world.viewportHeight)
		{
			battle.trueCamPos.y--;
		}

		battle.world.position.lerp(battle.trueCamPos, 0.3f);
	}

	@Override
	public void draw()
	{
		battle.main.batch.draw(battle.grid, cursorX, cursorY, 1, 1);

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
