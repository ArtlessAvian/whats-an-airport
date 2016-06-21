package com.artlessavian.whatsanairport;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

class InputHandler implements InputProcessor
{
	BattleModel model;

	final ArrayList<InputReceiver> receivers = new ArrayList<>(); // Things to send inputs to
	BasicMenu activeMenu; // Thing to draw

	// Thing
	Cursor cursor;
	ArrayList<BasicMenu> menus;
	AttackInputReceiver attackInputReceiver;

	private int framesHeld = 0;
	private int lastDirectional = -1;

	public InputHandler(BattleModel model)
	{
		this.model = model;
	}

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
						this.up();
						break;
					}
					case Input.Keys.S:
					{
						this.down();
						break;
					}
					case Input.Keys.A:
					{
						this.left();
						break;
					}
					case Input.Keys.D:
					{
						this.right();
						break;
					}
				}
			}
		}
	}

	public void up()
	{
		for (int i = receivers.size() - 1; i >= 0; i--)
		{
			if (receivers.get(i).up()) {break;}
		}
	}

	public void down()
	{
		for (int i = receivers.size() - 1; i >= 0; i--)
		{
			if (receivers.get(i).down()) {break;}
		}
	}

	public void left()
	{
		for (int i = receivers.size() - 1; i >= 0; i--)
		{
			if (receivers.get(i).left()) {break;}
		}
	}

	public void right()
	{
		for (int i = receivers.size() - 1; i >= 0; i--)
		{
			if (receivers.get(i).right()) {break;}
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

			case Input.Keys.PERIOD:
			{
				model.view.debuggery++;
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

	static Vector3 helper = new Vector3();

	public void helperScreenToTile(int screenX, int screenY)
	{
		helper.set(screenX, screenY, 0);
		model.view.worldSpace.unproject(helper);
		helper.scl(1f / model.view.tileSize);
	}

	// TODO: oh my god these functions race condition

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		helperScreenToTile(screenX, screenY);
		for (int i = receivers.size() - 1; i >= 0; i--)
		{
			if (receivers.get(i).touchDown(screenX, screenY, helper.x, helper.y)) {return true;}
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button)
	{
		helperScreenToTile(screenX, screenY);
		for (int i = receivers.size() - 1; i >= 0; i--)
		{
			if (receivers.get(i).touchUp(screenX, screenY, helper.x, helper.y)) {return true;}
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer)
	{
		helperScreenToTile(screenX, screenY);
		for (int i = receivers.size() - 1; i >= 0; i--)
		{
			if (receivers.get(i).touchDragged(screenX, screenY, helper.x, helper.y)) {return true;}
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
