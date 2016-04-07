package com.artlessavian.whatsanairport;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector3;

import java.util.HashMap;


class ControlStateSystem extends InputAdapter
{
	final BattleScreen battle;

	ControlState state;
	private final HashMap<Class, ControlState> stateHashMap;

	private WarsConst.CardinalDir heldDirection;
	private float timeHeld;
	private float accumulator;

	private final Vector3 helper = new Vector3();

	public ControlStateSystem(BattleScreen battleScreen)
	{
		battle = battleScreen;
		Gdx.input.setInputProcessor(this);

		heldDirection = null;
		timeHeld = 0;
		accumulator = 0;

		stateHashMap = new HashMap<Class, ControlState>();
		// Populate States
		stateHashMap.put(SelectUnitControlState.class, new SelectUnitControlState(this));
		stateHashMap.put(MoveUnitControlState.class, new MoveUnitControlState(this));

		setState(SelectUnitControlState.class);
	}

	public void setState(Class clazz)
	{
		state = stateHashMap.get(clazz);
		if (state == null)
		{
			System.out.println("[ControlStateSystem] You're missing a state you dunk.");
			try
			{
				stateHashMap.put(clazz, (ControlState)(clazz.getConstructor(ControlStateSystem.class).newInstance(this)));
				state = stateHashMap.get(clazz);
			}
			catch (Exception e)
			{
				// Goddammit
				e.printStackTrace();
			}
		}
	}

	public void draw()
	{
		state.draw();
	}

	@Override
	public boolean keyDown(int keycode)
	{
		switch (keycode)
		{
			case Input.Keys.W:
			{
				heldDirection = WarsConst.CardinalDir.UP;
				timeHeld = 0;
				break;
			}
			case Input.Keys.S:
			{
				heldDirection = WarsConst.CardinalDir.DOWN;
				timeHeld = 0;
				break;
			}
			case Input.Keys.A:
			{
				heldDirection = WarsConst.CardinalDir.LEFT;
				timeHeld = 0;
				break;
			}
			case Input.Keys.D:
			{
				heldDirection = WarsConst.CardinalDir.RIGHT;
				timeHeld = 0;
				break;
			}
			case Input.Keys.J:
			{
				state.select();
				break;
			}
			case Input.Keys.N:
			{
				state.cancel();
				break;
			}
			case Input.Keys.K:
			{
				battle.world.zoom *= 2f;
				break;
			}
			case Input.Keys.L:
			{
				battle.world.zoom /= 2f;
				break;
			}

			default: {return false;}
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode)
	{
		if (keycode == Input.Keys.W && heldDirection == WarsConst.CardinalDir.UP) {heldDirection = null;}
		if (keycode == Input.Keys.S && heldDirection == WarsConst.CardinalDir.DOWN) {heldDirection = null;}
		if (keycode == Input.Keys.A && heldDirection == WarsConst.CardinalDir.LEFT) {heldDirection = null;}
		if (keycode == Input.Keys.D && heldDirection == WarsConst.CardinalDir.RIGHT) {heldDirection = null;}

		return true;
	}

	public void update(float delta)
	{
		// This is pretty bad

		if (heldDirection != null)
		{
			if (timeHeld > 0.3)
			{
				while (accumulator > 0.03f)
				{
					switch (heldDirection)
					{
						case UP:
						{
							state.up();
							break;
						}
						case DOWN:
						{
							state.down();
							break;
						}
						case LEFT:
						{
							state.left();
							break;
						}
						case RIGHT:
						{
							state.right();
							break;
						}
					}
					accumulator -= 0.03f;
				}
				accumulator += delta;
			}

			if (timeHeld == 0)
			{
				switch (heldDirection)
				{
					case UP:
					{
						state.up();
						break;
					}
					case DOWN:
					{
						state.down();
						break;
					}
					case LEFT:
					{
						state.left();
						break;
					}
					case RIGHT:
					{
						state.right();
						break;
					}
				}
			}
			timeHeld += delta;
		}
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		helper.x = screenX;
		helper.y = screenY;
		battle.world.unproject(helper);
		helper.x = Math.max(0,Math.min(battle.mapWidth-1, helper.x));
		helper.y = Math.max(0,Math.min(battle.mapHeight-1, helper.y));
		state.pick(screenX, screenY, (int)helper.x, (int)helper.y);
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer)
	{
		return false;
	}
}
