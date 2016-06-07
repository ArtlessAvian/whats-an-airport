package com.artlessavian.whatsanairport;

public class DayMenu extends BasicMenu
{
	public DayMenu(InputHandler inputHandler)
	{
		super(inputHandler);

		xPadding = 64;
		yPadding = 64;
		position = 0;
	}

	public void initLogic(Object... objects)
	{
		//Buncha if statements

		for (double i = Math.random() * 9; i > 0; i--)
		{
			this.options.add(MenuOptions.DEBUGFILLER);
		}

		this.options.add(MenuOptions.ENDTURN);
	}

	@Override
	public boolean select()
	{
		switch (options.get(selected))
		{
			case ENDTURN:
			{
				inputHandler.model.turnHandler.endTurn();
				inputHandler.activeMenu = null;
				inputHandler.receivers.remove(this);
			}
		}
		return true;
	}

	@Override
	public boolean cancel()
	{
		inputHandler.activeMenu = null;
		inputHandler.receivers.remove(this);
		return true;
	}
}
