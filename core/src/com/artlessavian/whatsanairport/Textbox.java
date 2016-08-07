package com.artlessavian.whatsanairport;

public class Textbox implements InputReceiver
{
	private final InputHandler inputHandler;
	String[][] contents = {{"yoooo", "oooooo!"}, {"wat"}};
	int thingy;
	int line;

	Textbox(InputHandler inputHandler)
	{
		this.inputHandler = inputHandler;
	}

	@Override
	public void receivePrevious(InputReceiver previous, Class previousClass)
	{

	}

	@Override
	public void reset(Object[] args)
	{
		thingy = 0;
		line = 0;
	}

	@Override
	public boolean up()
	{
		return false;
	}

	@Override
	public boolean down()
	{
		return false;
	}

	@Override
	public boolean left()
	{
		return false;
	}

	@Override
	public boolean right()
	{
		return false;
	}

	@Override
	public boolean select()
	{
		if (thingy + 1 >= contents[line].length)
		{
			if (line + 1 >= contents.length)
			{
				inputHandler.pop();
			}
			else
			{
				line++;
				thingy = 0;
			}
		}
		else
		{
			thingy++;
		}
		return true;
	}

	@Override
	public boolean cancel()
	{
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, float tileX, float tileY)
	{
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, float tileX, float tileY)
	{
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, float tileX, float tileY)
	{
		return false;
	}

	@Override
	public boolean update()
	{
		return false;
	}
}
