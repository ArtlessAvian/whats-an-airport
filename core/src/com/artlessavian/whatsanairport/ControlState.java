package com.artlessavian.whatsanairport;

interface ControlState
{
	// Not a fan of this method :/
	void enter(Object... varargs);

	void up();
	void down();
	void left();
	void right();

	void pick(int screenX, int screenY, int x, int y);

	void select();
	void cancel();

	void moveCam();
	void draw();
}
