package com.artlessavian.whatsanairport;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;

public abstract class BasicMenu implements InputReceiver
{
	InputHandler inputHandler;
	ArrayList<MenuOptions> options;
	ArrayList<Float> pushOptionRight;
	int selected;

	int position; // Counterclockwise from top right
	float xPadding;
	float yPadding;
	float xSize;
	float ySize;

	public BasicMenu(InputHandler inputHandler)
	{
		this.inputHandler = inputHandler;
		this.options = new ArrayList<>();
		this.pushOptionRight = new ArrayList<>();
		this.selected = 0;

		xPadding = 64;
		yPadding = 64;
		position = 0;
	}

	public void init(Object... objects)
	{
		selected = 0;

		this.options.clear();
		this.pushOptionRight.clear();

		initLogic(objects);

		for (MenuOptions o : this.options)
		{
			this.pushOptionRight.add(0f);
		}

	}

	public abstract void initLogic(Object... objects);

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
	public abstract boolean select();

	@Override
	public abstract boolean cancel();

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
		float toSelect = 0;

		switch (position)
		{
			case 0:
			{
				if (screenX <= Gdx.graphics.getWidth() - xPadding && screenX >= Gdx.graphics.getWidth() - xPadding - xSize)
				{
					toSelect = ((screenY - yPadding) / (ySize / options.size()));
				}
				break;
			}
			case 1:
			{
				if (screenX <= xPadding + xSize && screenX >= xPadding)
				{
					toSelect = ((screenY - yPadding) / (ySize / options.size()));
				}
				break;
			}
			case 2:
			{
				if (screenX <= xPadding + xSize && screenX >= xPadding)
				{
					toSelect = ((screenY - (Gdx.graphics.getHeight() - yPadding - ySize)) / (ySize / options.size()));
				}
			}
			case 3:
			{
				if (screenX <= Gdx.graphics.getWidth() - xPadding && screenX >= Gdx.graphics.getWidth() - xPadding - xSize)
				{
					toSelect = ((screenY - (Gdx.graphics.getHeight() - yPadding - ySize)) / (ySize / options.size()));
				}
			}
		}

		System.out.println(toSelect);

		if (toSelect < options.size())
		{
			selected = (int)toSelect;
		}
		return true;
	}
}
