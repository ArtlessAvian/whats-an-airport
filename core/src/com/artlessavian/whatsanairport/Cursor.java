package com.artlessavian.whatsanairport;

import com.badlogic.gdx.graphics.Color;

public class Cursor implements InputReceiver
{
	private final Map map;

	int x;
	int y;
	private Unit selectedUnit;

	public Cursor(Map map)
	{
		this.map = map;
	}

	@Override
	public boolean up()
	{
		if (y < map.height - 1) {y++;}
		return false;
	}

	@Override
	public boolean down()
	{
		if (y > 0) {y--;}
		return false;
	}

	@Override
	public boolean left()
	{
		if (x > 0) {x--;}
		return false;
	}

	@Override
	public boolean right()
	{
		if (x < map.width - 1) {x++;}
		return false;
	}

	@Override
	public boolean select()
	{
		if (selectedUnit == null)
		{
			if (map.tileMap[y][x].unit != null)
			{
				selectedUnit = map.tileMap[y][x].unit;
				selectedUnit.selected = true;

				if (!selectedUnit.rangeCalcd) {selectedUnit.calculateMovement();}

				for (Tile t : selectedUnit.attackable)
				{
					t.highlight = Color.RED;
				}
				for (Tile t : selectedUnit.movable)
				{
					t.highlight = Color.BLUE;
				}
			}
		}

		return false;
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
