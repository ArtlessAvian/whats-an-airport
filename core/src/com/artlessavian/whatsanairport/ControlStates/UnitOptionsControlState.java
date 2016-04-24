package com.artlessavian.whatsanairport.ControlStates;

import com.artlessavian.whatsanairport.ControlStateSystem;
import com.artlessavian.whatsanairport.Unit;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

public class UnitOptionsControlState extends ControlState
{
	private enum Options
	{
		END, ATTACK, JOIN, CAPTURE
	}

	private Unit selectedUnit;
	private int x;
	private int y;
	private int originX;
	private int originY;
	private Unit displaced;

	private final ArrayList<Float> pushOptionRight;
	private final ArrayList<Options> options;
	private int position;

	private final Sprite selector;

	private final Vector3 unitPosition;

	public UnitOptionsControlState(ControlStateSystem controlStateSystem)
	{
		super(controlStateSystem);

		options = new ArrayList<Options>();
		pushOptionRight = new ArrayList<Float>();
		unitPosition = new Vector3();

		selector = new Sprite(battle.grid);
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

		position = 0;

		options.clear();

		// Join if standing on friendly
		if (displaced != null && displaced != selectedUnit)
		{
			options.add(Options.JOIN);
		} else
		{
			// Capture if standing on property and not joining
			//		options.addLast(Options.END);
			//		pushOptionRight.addLast(0f);

			// Attack if not joining and can attack
			if (!selectedUnit.getAttackableUnits(originX != x || originY != y).isEmpty())
			{
				options.add(Options.ATTACK);
			}

			options.add(Options.END);

			while (options.size() < 6)
			{
				options.add(Options.END);
			}

			pushOptionRight.clear();
			pushOptionRight.add(1f);
			while (options.size() > pushOptionRight.size())
			{
				pushOptionRight.add(0f);
			}
		}
	}

	@Override
	public void onExit()
	{

	}

	@Override
	public void onReturn()
	{

	}

	@Override
	public boolean up()
	{
		position--;
		if (position < 0)
		{
			position += options.size();
		}
		return true;
	}

	@Override
	public boolean down()
	{
		position++;
		if (position >= options.size())
		{
			position -= options.size();
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

	private float[] getBoxCoord(boolean rightOfCenter, boolean topOfCenter)
	{
		float[] thing = new float[4];

		thing[3] = battle.main.font.getLineHeight(); // + 10f;
		thing[2] = 4 * thing[3];

		// It's better to keep things consistent I suppose
		// Maybe I recycle this later

//		if (rightOfCenter)
//		{
//			thing[0] = battle.main.screenSpace.viewportHeight / 10f;
//		}
//		else
		{
			thing[0] = battle.main.screenSpace.viewportWidth
				- battle.main.screenSpace.viewportHeight / 10f
				- thing[2];
		}

//		if (topOfCenter)
//		{
//			thing[1] = battle.main.screenSpace.viewportHeight / 10f;
//		}
//		else
		{
			thing[1] = 9 * battle.main.screenSpace.viewportHeight / 10f
				- options.size() * thing[3];
		}

		return thing;
	}

	@Override
	public void pick(int screenX, int screenY, int x, int y)
	{
		if (controlStateSystem.doubleTap)
		{
			float[] thing = getBoxCoord(false, false);

			if (thing[0] < screenX && screenX < thing[0] + thing[2])
			{
				float calculatedPosition = options.size() - (screenY - thing[1]) / thing[3];
				if (0 <= calculatedPosition && calculatedPosition < options.size())
				{
					if (position == (int)calculatedPosition)
					{
						select();
					} else
					{
						position = (int)calculatedPosition;
					}
				}
			}
		}
		else
		{
			weakPick(screenX, screenY, x, y);
		}
	}

	@Override
	public void weakPick(int screenX, int screenY, int x, int y)
	{
		float[] thing = getBoxCoord(unitPosition.x > 0, unitPosition.y > 0);

		if (thing[0] < screenX && screenX < thing[0] + thing[2])
		{
			float calculatedPosition = options.size() - (screenY - thing[1]) / thing[3];
			if (0 <= calculatedPosition && calculatedPosition < options.size())
			{
				position = (int)calculatedPosition;
			}
		}
	}

	@Override
	public void release(int screenX, int screenY, int x, int y)
	{
		if (!controlStateSystem.doubleTap)
		{
			float[] thing = getBoxCoord(false, false);

			if (thing[0] < screenX && screenX < thing[0] + thing[2])
			{
				float calculatedPosition = options.size() - (screenY - thing[1]) / thing[3];
				if (0 <= calculatedPosition && calculatedPosition < options.size())
				{
					if (position == (int)calculatedPosition)
					{
						select();
					} else
					{
						position = (int)calculatedPosition;
					}
				}
			}
		}
	}

	@Override
	public void select()
	{
		switch (options.get(position))
		{
			case ATTACK:
			{
				// Probably an attack control state
				selectedUnit.sprite.setRotation(0);
				controlStateSystem.setState(AttackControlState.class).onEnter(x, y, selectedUnit, originX != x || originY != y);
				break;
			}

			case JOIN:
			{
				displaced.joined(selectedUnit);

				selectedUnit.sprite.setRotation(0);
				controlStateSystem.setState(SelectUnitControlState.class).onEnter(x, y);
				break;
			}

			case END:
			{
				selectedUnit.sprite.setRotation(0);
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
	public void update(float delta)
	{
		for (int i = 0; i < pushOptionRight.size(); i++)
		{
			if (i == position)
			{
				float a = pushOptionRight.get(i);
				a += 0.1f * (1 - a);
				pushOptionRight.set(i, a);
			} else
			{
				float a = pushOptionRight.get(i);
				a += 0.1f * (0 - a);
				pushOptionRight.set(i, a);
			}
		}
	}

	@Override
	public void moveCam()
	{

	}

	@Override
	public void draw()
	{
		if (displaced != null)
		{
			displaced.draw(battle.main.batch);
		}
		selectedUnit.sprite.rotate(145);

		battle.main.batch.setProjectionMatrix(battle.main.screenSpace.combined);
		
		float[] thing = getBoxCoord(false, false);

		for (int i = 0; i < options.size(); i++)
		{
			battle.main.batch.draw(battle.white, thing[0], thing[1] + i * (thing[3]), thing[2], (thing[3]));
		}

		for (int i = 0; i < options.size(); i++)
		{
			Options option = options.get(i);

			float grayval = pushOptionRight.get(i) / 4f + 0.75f;

			battle.main.font.getColor().set(grayval, grayval, grayval, 1);
			battle.main.font.draw(battle.main.batch, option.name(),
				thing[0] + pushOptionRight.get(i) * thing[2] / 4f,
				thing[1] + (options.size() - i) * (thing[3]) - battle.main.font.getData().padTop);
		}

		selector.setSize(thing[2], thing[3]);
		selector.setPosition(thing[0], thing[1] + (options.size() - 1 - position) * (thing[3]));
		selector.draw(battle.main.batch);

		battle.main.batch.setProjectionMatrix(battle.worldSpace.combined);
	}
}
