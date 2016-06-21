package com.artlessavian.whatsanairport;

import java.util.ArrayList;
import java.util.Iterator;

public class AttackInputReceiver implements InputReceiver
{
	private InputHandler inputHandler;

	private Unit selectedUnit;
	float[] grading;
	ArrayList<Tile> tiles;
	Tile current;

	public AttackInputReceiver(InputHandler inputHandler)
	{
		this.inputHandler = inputHandler;
		this.tiles = new ArrayList<>();
	}

	public void init(Unit selectedUnit, ArrayList<Tile> incomingTiles)
	{
		this.selectedUnit = selectedUnit;
		this.tiles.clear();
		this.tiles.addAll(incomingTiles);

		// Units only

		Iterator<Tile> tilesIter = this.tiles.iterator();
		while (tilesIter.hasNext())
		{
			Unit unit = tilesIter.next().getUnit();
			if (unit == null || unit.owner == selectedUnit.owner)
			{
				tilesIter.remove();
			}
		}

		// Current = Taxicab closest
		if (grading == null || tiles.size() > grading.length)
		{
			grading = new float[tiles.size()];
		}

		for (int i = 0; i < tiles.size(); i++)
		{
			Tile t = tiles.get(i);
			grading[i] = Math.abs(t.x - selectedUnit.tile.x) + Math.abs(t.y - selectedUnit.tile.y);
		}

		int id = -1;
		for (int i = 0; i < tiles.size(); i++)
		{
			if (id == -1 || grading[i] < grading[id])
			{
				id = i;
			}
		}
		current = tiles.get(id);
	}

	// TODO: Gross time complexity n

	public void gradeAndFind(boolean vertical, boolean flip)
	{
		for (int i = 0; i < tiles.size(); i++)
		{
			Tile t = tiles.get(i);
			if (t.equals(current))
			{
				grading[i] = -1;
				continue;
			}

			// lmao this will be unreadable
			int tXPrime;
			int tYPrime;
			int currentXPrime;
			int currentYPrime;

			if (vertical)
			{
				if (flip) // Down
				{
					tXPrime = current.y;
					tYPrime = current.x;
					currentXPrime = t.y;
					currentYPrime = t.x;
				}
				else // Up
				{
					tXPrime = t.y;
					tYPrime = t.x;
					currentXPrime = current.y;
					currentYPrime = current.x;
				}
			}
			else
			{
				if (flip) // Left
				{
					tXPrime = current.x;
					tYPrime = current.y;
					currentXPrime = t.x;
					currentYPrime = t.y;
				}
				else // Normal -- Right
				{
					tXPrime = t.x;
					tYPrime = t.y;
					currentXPrime = current.x;
					currentYPrime = current.y;
				}
			}

			if (tXPrime <= currentXPrime)
			{
				grading[i] = -2;
				continue;
			}

			grading[i] = 2520f / (tXPrime - currentXPrime) - Math.abs(currentYPrime - tYPrime);
		}

		int id = -1;
		for (int i = 0; i < tiles.size(); i++)
		{
			if (id == -1 || grading[i] > grading[id])
			{
				id = i;
			}
		}
		current = tiles.get(id);
	}

	@Override
	public boolean up()
	{
		gradeAndFind(true, false);
		return true;
	}

	@Override
	public boolean down()
	{
		gradeAndFind(true, true);
		return true;
	}

	@Override
	public boolean left()
	{
		gradeAndFind(false, true);
		return true;
	}

	@Override
	public boolean right()
	{
		gradeAndFind(false, false);
		return true;
	}

	@Override
	public boolean select()
	{
		selectedUnit.attack(current.getUnit());
		selectedUnit.finalInstruction = UnitInstruction.WAIT;
		inputHandler.receivers.remove(this);
		return true;
	}

	@Override
	public boolean cancel()
	{
		inputHandler.receivers.remove(this);
		inputHandler.receivers.add(inputHandler.menus.get(0));
		inputHandler.activeMenu = inputHandler.menus.get(0);

		return true;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, float tileX, float tileY)
	{
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, float tileX, float tileY)
	{
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, float tileX, float tileY)
	{
		return false;
	}
}
