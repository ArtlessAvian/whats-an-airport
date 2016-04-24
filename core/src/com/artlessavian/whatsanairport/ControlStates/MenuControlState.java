package com.artlessavian.whatsanairport.ControlStates;

import com.artlessavian.whatsanairport.ControlStateSystem;
import com.artlessavian.whatsanairport.Unit;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

public abstract class MenuControlState extends ControlState
{
	private final ArrayList<Float> pushOptionRight;
	final ArrayList<String> options;
	int position;

	private final Sprite selector;

	public MenuControlState(ControlStateSystem controlStateSystem)
	{
		super(controlStateSystem);

		options = new ArrayList<String>();
		pushOptionRight = new ArrayList<Float>();

		selector = new Sprite(battle.grid);
	}

	@Override
	public void onEnter(Object... varargs)
	{
		position = 0;
		options.clear();
		pushOptionRight.clear();

		fillOptions();
	}

	public abstract void fillOptions();

	public void addOption(String yey)
	{
		options.add(yey);
		pushOptionRight.add(0f);
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

	private float[] getBoxCoord(boolean left, boolean top)
	{
		float[] thing = new float[4];

		thing[3] = battle.main.font.getLineHeight(); // + 10f;
		thing[2] = 5 * thing[3];

		if (left)
		{
			thing[0] = battle.main.screenSpace.viewportHeight / 10f;
		}
		else
		{
			thing[0] = battle.main.screenSpace.viewportWidth
				- battle.main.screenSpace.viewportHeight / 10f
				- thing[2];
		}

		if (top)
		{
			thing[1] = 9 * battle.main.screenSpace.viewportHeight / 10f
				- options.size() * thing[3];
		}
		else
		{
			thing[1] = battle.main.screenSpace.viewportHeight / 10f;
		}

		return thing;
	}

	@Override
	public void pick(int screenX, int screenY, int x, int y)
	{
		if (controlStateSystem.doubleTap)
		{
			float[] thing = getBoxCoord(false, true);

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
		float[] thing = getBoxCoord(false, true);

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
			float[] thing = getBoxCoord(false, true);

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
	public abstract void select();

	@Override
	public abstract void cancel();

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
		battle.main.batch.setProjectionMatrix(battle.main.screenSpace.combined);

		float[] thing = getBoxCoord(false, true);

		for (int i = 0; i < options.size(); i++)
		{
			battle.main.batch.draw(battle.white, thing[0], thing[1] + i * (thing[3]), thing[2], (thing[3]));
		}

		for (int i = 0; i < options.size(); i++)
		{
			float grayval = pushOptionRight.get(i) / 4f + 0.75f;
			battle.main.font.getColor().set(grayval, grayval, grayval, 1);

			battle.main.font.draw(battle.main.batch, options.get(i),
				thing[0] + pushOptionRight.get(i) * thing[2] / 4f,
				thing[1] + (options.size() - i) * (thing[3]) - battle.main.font.getData().padTop);
		}

		selector.setSize(thing[2], thing[3]);
		selector.setPosition(thing[0], thing[1] + (options.size() - 1 - position) * (thing[3]));
		selector.draw(battle.main.batch);

		battle.main.batch.setProjectionMatrix(battle.worldSpace.combined);
	}
}
