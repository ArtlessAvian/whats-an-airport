package com.artlessavian.whatsanairport;

import com.badlogic.gdx.graphics.Color;

public class UnitMenu extends BasicMenu
{
	private Unit selectedUnit;
	private Tile finalDestination;
	private Tile originalTile;

	int position; // Counterclockwise from top right
	float xPadding;
	float yPadding;
	float xSize;
	float ySize;

	public UnitMenu(InputHandler inputHandler)
	{
		super(inputHandler);

		xPadding = 64;
		yPadding = 64;
		position = 0;
	}

	public void initLogic(Object... objects)
	{
		this.selectedUnit = (Unit)objects[0];
		this.finalDestination = (Tile)objects[1];
		this.originalTile = (Tile)objects[2];

		//Buncha if statements
		this.options.add(MenuOptions.ATTACK);
		this.options.add(MenuOptions.ATTACK);
		this.options.add(MenuOptions.CAPTURE);

		this.options.add(MenuOptions.WAIT);
	}

	@Override
	public boolean up()
	{
		selected--;
		if (selected < 0)
		{
			selected += options.size();
		}
		return true;
	}

	@Override
	public boolean down()
	{
		selected++;
		if (selected >= options.size())
		{
			selected -= options.size();
		}
		return true;
	}

	@Override
	public boolean left()
	{
		return true;
	}

	@Override
	public boolean right()
	{
		return true;
	}

	@Override
	public boolean select()
	{
		switch (options.get(selected))
		{
			case WAIT:
			{
				selectedUnit.instructions.add(UnitInstruction.WAIT);
				inputHandler.activeMenu = null;
				inputHandler.receivers.remove(this);
			}
		}
		return true;
	}

	@Override
	public boolean cancel()
	{
		// TODO: Make work
		selectedUnit.tile = originalTile;
		originalTile.setUnit(selectedUnit);
		finalDestination.setUnit(null);
		selectedUnit.instructions.clear();

		inputHandler.cursor.selectedUnit = selectedUnit;
		selectedUnit.selected = true;

		if (!selectedUnit.getRangeInfo().rangeCalcd) {selectedUnit.calculateMovement();}

		for (Tile t : selectedUnit.getRangeInfo().attackable)
		{
			t.highlight.add(Color.RED);
		}
		for (Tile t : selectedUnit.getRangeInfo().movable)
		{
			t.highlight.add(Color.BLUE);
		}

		inputHandler.activeMenu = null;
		inputHandler.receivers.remove(this);
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
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, float tileX, float tileY)
	{

		return true;
	}
}
