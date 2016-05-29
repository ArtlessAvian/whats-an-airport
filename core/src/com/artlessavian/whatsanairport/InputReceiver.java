package com.artlessavian.whatsanairport;

interface InputReceiver
{
	boolean up();

	boolean down();

	boolean left();

	boolean right();

	boolean select();

	boolean cancel();

	boolean touchDown(int screenX, int screenY, int pointer, int button);

	boolean touchUp(int screenX, int screenY, int pointer, int button);

	boolean touchDragged(int screenX, int screenY, int pointer);
}
