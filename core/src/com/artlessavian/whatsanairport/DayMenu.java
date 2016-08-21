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

	@Override
	void reactivate()
	{

	}

	public void initLogic(Object... objects)
	{
		//Buncha if statements

		for (double i = Math.random() * 1; i > 0; i--)
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
				inputHandler.addState(NewDayShower.class, true, false);
				break;
			}
			case DEBUGFILLER:
			{
//				inputHandler.addState(Textbox.class, true, false);
			}
		}
		return true;
	}

	@Override
	public boolean cancel()
	{
		inputHandler.pop();
		return true;
	}
}
