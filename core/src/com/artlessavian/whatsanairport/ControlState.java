package com.artlessavian.whatsanairport;

interface ControlState
{
	// Not a fan of this method :/
	void enter(Object... varargs);
	void cancelReturn();

	// Directionals
	void up();

	void down();

	void left();

	void right();

	// Touch Click
	void pick(int screenX, int screenY, int x, int y);

	void weakPick(int screenX, int screenY, int x, int y);

	void release(int screenX, int screenY, int x, int y);

	// Stuff
	void select();

	void cancel();

	// More Stuff
	void update(float delta);

	void moveCam();

	void draw();
}
