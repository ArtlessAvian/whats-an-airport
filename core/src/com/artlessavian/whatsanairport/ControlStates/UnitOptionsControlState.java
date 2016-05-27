package com.artlessavian.whatsanairport.ControlStates;

import com.artlessavian.whatsanairport.ControlStateSystem;
import com.artlessavian.whatsanairport.PropertyTile;
import com.artlessavian.whatsanairport.Unit;
import com.badlogic.gdx.graphics.Color;

public class UnitOptionsControlState extends MenuControlState
{
	private Unit selectedUnit;
	private int x;
	private int y;
	private int originX;
	private int originY;
	private Unit displaced;

	public UnitOptionsControlState(ControlStateSystem controlStateSystem)
	{
		super(controlStateSystem);
	}

	@Override
	public void onEnter(Object... varargs)
	{
		selectedUnit = (Unit)varargs[0];
		originX = (Integer)varargs[1];
		originY = (Integer)varargs[2];
		x = (Integer)varargs[3];
		y = (Integer)varargs[4];
		displaced = (Unit)varargs[5];

		super.onEnter(varargs);
	}

	@Override
	public void fillOptions()
	{
		// Join if standing on friendly
		if (displaced != null && displaced != selectedUnit)
		{
			super.addOption("JOIN");
		} else
		{
			// Capture if standing on property and not joining
			try
			{
				if (((PropertyTile)selectedUnit.tile).owner != battle.dayAndCoHandler.turn)
				{
					super.addOption("CAPTURE");
				}
			}
			catch (Exception e)
			{
				// my b
			}

			// Attack if not joining and can attack
			if (!selectedUnit.getAttackableUnits(originX != x || originY != y).isEmpty())
			{
				super.addOption("ATTACK");
			}

			super.addOption("END");

			while (super.options.size() < 6)
			{
				super.addOption("END");
			}
		}
	}

	@Override
	public void select()
	{
		switch (options.get(position))
		{
			case "CAPTURE":
			{
				// Probably an attack control state
				((PropertyTile)selectedUnit.tile).capProgress -= selectedUnit.health;
				if (((PropertyTile)selectedUnit.tile).capProgress <= 0)
				{
					((PropertyTile)selectedUnit.tile).owner = battle.dayAndCoHandler.turn;
					// TODO: YAAAYYY sound
				}

				selectedUnit.sprite.setRotation(0);
				selectedUnit.used = true;
				selectedUnit.sprite.setColor(Color.GRAY);
				selectedUnit.sprite.setRotation(180);
				controlStateSystem.setState(SelectUnitControlState.class).onEnter(x, y);
				break;
			}

			case "ATTACK":
			{
				// Probably an attack control state
				selectedUnit.sprite.setRotation(0);
				controlStateSystem.setState(AttackControlState.class).onEnter(x, y, selectedUnit, originX != x || originY != y);
				break;
			}

			case "JOIN":
			{
				displaced.joined(selectedUnit);

				selectedUnit.sprite.setRotation(0);
				selectedUnit.used = true;
				selectedUnit.sprite.setColor(Color.GRAY);
				selectedUnit.sprite.setRotation(180);
				controlStateSystem.setState(SelectUnitControlState.class).onEnter(x, y);
				break;
			}

			case "END":
			{
				selectedUnit.sprite.setRotation(0);
				selectedUnit.used = true;
				selectedUnit.sprite.setColor(Color.GRAY);
				selectedUnit.sprite.setRotation(180);
				controlStateSystem.setState(SelectUnitControlState.class).onEnter(x, y);
				break;
			}
		}
	}

	@Override
	public void cancel()
	{
		selectedUnit.move(battle.map.map[originX][originY]);
		selectedUnit.sprite.setPosition(originX, originY);
		selectedUnit.sprite.setRotation(0);
		if (displaced != null)
		{
			displaced.tile = battle.map.map[x][y];
			battle.map.map[x][y].unit = displaced;
		}
		controlStateSystem.setState(MoveUnitControlState.class).onReturn();
	}

	@Override
	public void draw()
	{
		if (displaced != null)
		{
			displaced.draw(battle.main.batch);
		}
		selectedUnit.sprite.rotate(145);
		super.draw();
	}
}
