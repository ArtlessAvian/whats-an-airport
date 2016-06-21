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

		// Join

		// Capture

		// Attack
		tiles.clear();
		map.getAttackable(finalDestination.x, finalDestination.y, selectedUnit.unitInfo.minRange, selectedUnit.unitInfo.maxRange, tiles);

		for (Tile t : tiles)
		{
			t.highlight.add(Color.PINK);
		}

		if (this.selectedUnit.unitInfo.isDirect || originalTile.equals(finalDestination))
		{
			for (Tile t : tiles)
			{
				if (t.getUnit() != null && t.getUnit().owner != selectedUnit.owner)
				{
					this.options.add(MenuOptions.ATTACK);
					break;
				}
			}
		}

		// Wait
		this.options.add(MenuOptions.WAIT);
	}

	@Override
	public boolean select()
	{
		switch (options.get(selected))
		{
			case WAIT:
			{
				for (Tile t : tiles)
				{
					t.highlight.remove(Color.PINK);
				}

				selectedUnit.finalInstruction = UnitInstruction.WAIT;
				inputHandler.activeMenu = null;
				inputHandler.receivers.remove(this);
				break;
			}
			case ATTACK:
			{
				for (Tile t : tiles)
				{
					t.highlight.remove(Color.PINK);
				}

				inputHandler.receivers.add(inputHandler.attackInputReceiver);
				inputHandler.attackInputReceiver.init(selectedUnit, tiles);

				inputHandler.activeMenu = null;
				inputHandler.receivers.remove(this);
				break;
			}
		}
		return true;
	}

	@Override
	public boolean cancel()
	{
		for (Tile t : tiles)
		{
			t.highlight.remove(Color.PINK);
		}

		// TODO: Make work
		selectedUnit.tile = originalTile;
		originalTile.setUnit(selectedUnit);
		finalDestination.setUnit(null);

		inputHandler.cursor.selectedUnit = selectedUnit;
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

		inputHandler.activeMenu = null;
		inputHandler.receivers.remove(this);
		return true;
	}
}
