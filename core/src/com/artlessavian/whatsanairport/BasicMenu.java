package com.artlessavian.whatsanairport;

import java.util.ArrayList;

public abstract class BasicMenu extends InputReceiver
{
	final ArrayList<MenuOptions> options;
	final ArrayList<Float> pushOptionRight;
	int selected;

	int position; // Counterclockwise from top right
	float xPadding;
	float yPadding;
	static float xSize;
	float ySize;

	public BasicMenu(InputHandler inputHandler)
	{
		super(inputHandler);
		this.options = new ArrayList<MenuOptions>();
		this.pushOptionRight = new ArrayList<Float>();
		this.selected = 0;

		xPadding = 64;
		yPadding = 64;
		position = 0;
	}

	protected abstract void initLogic(Object... objects);

	@Override
	public void reset(Object[] args)
	{
		selected = 0;

		this.options.clear();
		this.pushOptionRight.clear();

		initLogic(args);

		for (MenuOptions o : this.options)
		{
			this.pushOptionRight.add(0f);
		}
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
		select();
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, float tileX, float tileY)
	{
		// TODO: Fix?

//		double toSelect = 0;
//
//		switch (position)
//		{
//			case 0:
//			{
//				if (screenX <= Gdx.graphics.getWidth() - xPadding && screenX >= Gdx.graphics.getWidth() - xPadding - xSize)
//				{
//					toSelect = (screenY - yPadding) * (options.size()) / ySize;
//				}
//				break;
//			}
//			case 1:
//			{
//				if (screenX <= xPadding + xSize && screenX >= xPadding)
//				{
//					toSelect = (screenY - yPadding) * (options.size()) / ySize;
//				}
//				break;
//			}
//			case 2:
//			{
//				if (screenX <= xPadding + xSize && screenX >= xPadding)
//				{
//					toSelect = (screenY - (Gdx.graphics.getHeight() - yPadding - ySize)) * (options.size()) / ySize;
//				}
//			}
//			case 3:
//			{
//				if (screenX <= Gdx.graphics.getWidth() - xPadding && screenX >= Gdx.graphics.getWidth() - xPadding - xSize)
//				{
//					toSelect = (screenY - (Gdx.graphics.getHeight() - yPadding - ySize)) * (options.size()) / ySize;
//				}
//			}
//		}
//
//		System.out.println(toSelect);
//
//		if (toSelect < options.size() && toSelect >= 0)
//		{
//			selected = (int)toSelect;
//		}
		return true;
	}

	@Override
	public boolean update()
	{
		return false;
	}
}
