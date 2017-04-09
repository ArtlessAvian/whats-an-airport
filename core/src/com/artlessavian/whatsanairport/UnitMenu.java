package com.artlessavian.whatsanairport;

import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;

public class UnitMenu extends BasicMenu
{
	private Map map;
	private final ArrayList<Tile> tiles;

	private Unit selectedUnit;
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
		this.originalTile = (Tile)args[1];

		//Buncha if statements

		// Join

		// Capture

		// Attack
		tiles.clear();
		selectedUnit.trueTile.getAttackable(selectedUnit.unitInfo.minRange, selectedUnit.unitInfo.maxRange, tiles);

		for (Tile t : tiles)
		{
			t.highlight.add(Color.PINK);
		}

		if (this.selectedUnit.unitInfo.isDirect || originalTile.equals(selectedUnit.trueTile))
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

				selectedUnit.endTurn();
				inputHandler.addState(Cursor.class, false, true);
				break;
			}
			case ATTACK:
			{
				for (Tile t : tiles)
				{
					t.highlight.remove(Color.PINK);
				}

				inputHandler.addState(AttackInputReceiver.class, false, false, selectedUnit, tiles, originalTile);
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
		selectedUnit.trueTile.setUnit(null);
		originalTile.setUnit(selectedUnit);
		selectedUnit.tile = originalTile;
		selectedUnit.trueTile = originalTile;

		selectedUnit.instructionsList.clear();
		selectedUnit.instructions = null;

		inputHandler.pop();
		return true;
	}
}
