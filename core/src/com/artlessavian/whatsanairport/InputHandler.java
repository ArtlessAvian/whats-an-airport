package com.artlessavian.whatsanairport;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

import java.util.ArrayList;

class InputHandler implements InputProcessor
{
	final ArrayList<InputReceiver> receivers = new ArrayList<>();

	private int framesHeld = 0;
	private int lastDirectional = -1;

	public void update()
	{
		if (lastDirectional != -1)
		{
			framesHeld++;
			if (framesHeld >= 20 || framesHeld == 1)
			{
				if (framesHeld >= 20)
				{
					framesHeld -= 2;
				}

				switch (lastDirectional)
				{
					case Input.Keys.W:
					{
						for (int i = receivers.size() - 1; i >= 0; i--)
						{
							if (receivers.get(i).up()) {break;}
						}
						break;
					}
					case Input.Keys.S:
					{
						for (int i = receivers.size() - 1; i >= 0; i--)
						{
							if (receivers.get(i).down()) {break;}
						}
						break;
					}
					case Input.Keys.A:
					{
						for (int i = receivers.size() - 1; i >= 0; i--)
						{
							if (receivers.get(i).left()) {break;}
						}
						break;
					}
					case Input.Keys.D:
					{
						for (int i = receivers.size() - 1; i >= 0; i--)
						{
							if (receivers.get(i).right()) {break;}
						}
						break;
					}
				}
			}
		}
	}

	@Override
	public boolean keyDown(int keycode)
	{
		switch (keycode)
		{
			case Input.Keys.W:
			{
				framesHeld = 0;
				lastDirectional = Input.Keys.W;
				break;
			}
			case Input.Keys.S:
			{
				framesHeld = 0;
				lastDirectional = Input.Keys.S;
				break;
			}
			case Input.Keys.A:
			{
				framesHeld = 0;
				lastDirectional = Input.Keys.A;
				break;
			}
			case Input.Keys.D:
			{
				framesHeld = 0;
				lastDirectional = Input.Keys.D;
				break;
			}

			case Input.Keys.J:
			{
				for (int i = receivers.size() - 1; i >= 0; i--)
				{
					if (receivers.get(i).select()) {break;}
				}
				return true;
			}
			case Input.Keys.N:
			{
				for (int i = receivers.size() - 1; i >= 0; i--)
				{
					if (receivers.get(i).cancel()) {break;}
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode)
	{
		if (keycode == lastDirectional)
		{
			lastDirectional = -1;
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character)
	{
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		for (int i = receivers.size() - 1; i >= 0; i--)
		{
			if (receivers.get(i).touchDown(screenX, screenY, pointer, button)) {return true;}
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button)
	{
		for (int i = receivers.size() - 1; i >= 0; i--)
		{
			if (receivers.get(i).touchUp(screenX, screenY, pointer, button)) {return true;}
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer)
	{
		for (int i = receivers.size() - 1; i >= 0; i--)
		{
			if (receivers.get(i).touchDragged(screenX, screenY, pointer)) {return true;}
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY)
	{
		return false;
	}

	@Override
	public boolean scrolled(int amount)
	{
		return false;
	}
}
