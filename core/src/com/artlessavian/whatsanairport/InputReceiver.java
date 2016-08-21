package com.artlessavian.whatsanairport;

abstract class InputReceiver
{
	protected InputHandler inputHandler;

	public InputReceiver(InputHandler inputHandler)
	{
		this.inputHandler = inputHandler;
	}

	abstract void reset(Object[] args);

	abstract void reactivate();

	abstract boolean up();

	abstract boolean down();

	abstract boolean left();

	abstract boolean right();

	abstract boolean select();

	abstract boolean cancel();

	abstract boolean touchDown(int screenX, int screenY, float tileX, float tileY);

	abstract boolean touchUp(int screenX, int screenY, float tileX, float tileY);

	abstract boolean touchDragged(int screenX, int screenY, float tileX, float tileY);

	abstract boolean update();
}
