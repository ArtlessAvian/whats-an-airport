package com.artlessavian.whatsanairport;

public class Textbox extends InputReceiver
{
	String[][] contents = {{"yoooo", "oooooo!"}, {"wat"}};
	int thingy;
	int line;

	Textbox(InputHandler inputHandler)
	{
		super(inputHandler);
	}

	@Override
	public void reset(Object[] args)
	{
		thingy = 0;
		line = 0;
	}

	@Override
	void reactivate()
	{

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
