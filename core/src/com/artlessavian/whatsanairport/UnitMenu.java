package com.artlessavian.whatsanairport;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.*;

import java.util.ArrayList;

public class UnitMenu extends BasicMenu
{
	private Map map;
	private ArrayList<Tile> tiles;

	private Unit selectedUnit;
	private Tile finalDestination;
	private Tile originalTile;

	public UnitMenu(InputHandler inputHandler, Map map)
	{
		super(inputHandler);
		this.map = map;
		this.tiles = new ArrayList<>();

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
		this.options.add(MenuOptions.WAIT);

		tiles.clear();
		if (this.finalDestination != this.originalTile)
		{
			if (this.selectedUnit.unitInfo.isDirect)
			{
				map.getAttackable(finalDestination.x, finalDestination.y, 1, 1, tiles);
			}
		}
		else
		{

		}

		for (Tile t : tiles)
		{
			t.debug = true;
			if (t.getUnit() != null)
			{
				this.options.add(MenuOptions.ATTACK);
				break;
			}
		}
	}

	@Override
	public boolean select()
	{
		switch (options.get(selected))
		{
			case WAIT:
			{
				selectedUnit.finalInstruction = UnitInstruction.WAIT;
				inputHandler.activeMenu = null;
				inputHandler.receivers.remove(this);
			}
			case ATTACK:
			{
				inputHandler.receivers.add(new AttackInputReceiver());
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
}
