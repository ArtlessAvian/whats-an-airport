package com.artlessavian.whatsanairport;

import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;

public class UnitMenu extends BasicMenu
{
	private Map map;
	private final ArrayList<Tile> tiles;

	private Unit selectedUnit;
	private Tile finalDestination;
	private Tile originalTile;

	public UnitMenu(InputHandler inputHandler)
	{
		super(inputHandler);
		this.tiles = new ArrayList<Tile>();

		xPadding = 64;
		yPadding = 64;
		position = 0;
	}

	@Override
	void reactivate()
	{

	}

	public void initLogic(Object... args)
	{
		this.map = inputHandler.model.map;
		this.selectedUnit = (Unit)args[0];
		this.finalDestination = (Tile)args[1];
		this.originalTile = (Tile)args[2];

		//Buncha if statements

		// Join

		// Capture

		// Attack
		tiles.clear();
		finalDestination.getAttackable(selectedUnit.unitInfo.minRange, selectedUnit.unitInfo.maxRange, tiles);

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
				inputHandler.pop();
				break;
			}
			case ATTACK:
			{
				for (Tile t : tiles)
				{
					t.highlight.remove(Color.PINK);
				}

				inputHandler.addState(AttackInputReceiver.class, true, false, selectedUnit, finalDestination, tiles);
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
		selectedUnit.instructionsList.clear();
		selectedUnit.instructions = null;
		finalDestination.setUnit(null);

		((Cursor)inputHandler.getState(Cursor.class)).selectedUnit = selectedUnit;
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

		inputHandler.pop();
		return true;
	}
}
