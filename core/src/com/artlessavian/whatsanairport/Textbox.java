package com.artlessavian.whatsanairport;

public class Textbox implements InputReceiver
{
	private final InputHandler inputHandler;
	String contents;

	Textbox(InputHandler inputHandler)
	{
		this.inputHandler = inputHandler;
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
		inputHandler.receivers.remove(this);
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
