package com.artlessavian.whatsanairport;

interface InputReceiver
{
	void receivePrevious(InputReceiver previous, Class previousClass);

	void reset(Object[] args);

	boolean up();

	boolean down();

	boolean left();

	boolean right();

	boolean select();

	boolean cancel();

	boolean touchDown(int screenX, int screenY, float tileX, float tileY);

	boolean touchUp(int screenX, int screenY, float tileX, float tileY);

	boolean touchDragged(int screenX, int screenY, float tileX, float tileY);

	boolean update();
}
