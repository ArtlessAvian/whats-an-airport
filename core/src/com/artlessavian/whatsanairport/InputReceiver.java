package com.artlessavian.whatsanairport;

import java.util.ArrayList;

abstract class InputReceiver
{
	protected InputHandler inputHandler;

	boolean hasFocus = false;
	float focusX = 0;
	float focusY = 0;

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
