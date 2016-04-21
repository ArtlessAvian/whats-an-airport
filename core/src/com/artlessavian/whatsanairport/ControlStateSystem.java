package com.artlessavian.whatsanairport;

import com.artlessavian.whatsanairport.ControlStates.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector3;

import java.util.HashMap;


public class ControlStateSystem extends InputAdapter
{
	public final BattleScreen battle;

	ControlState state;
	private final HashMap<Class, ControlState> stateHashMap;

	private WarsConst.CardinalDir heldDirection;
	private float timeHeld;
	private float accumulator;

	private int pointer = -1;
	private float timePressed;
	private boolean isCancelling;

	private final Vector3 helper = new Vector3();
	
	public ControlStateSystem()
	{
		this.battle = BattleScreen.getInstance();
		Gdx.input.setInputProcessor(this);

		heldDirection = null;
		timeHeld = 0;
		accumulator = 0;

		stateHashMap = new HashMap<Class, ControlState>();
		// Populate States
		stateHashMap.put(SelectUnitControlState.class, new SelectUnitControlState(this));
		stateHashMap.put(MoveUnitControlState.class, new MoveUnitControlState(this));
		stateHashMap.put(MovingUnitControlState.class, new MovingUnitControlState(this));
		stateHashMap.put(UnitOptionsControlState.class, new UnitOptionsControlState(this));
		stateHashMap.put(AttackControlState.class, new AttackControlState(this));

		setState(SelectUnitControlState.class);
	}

	public ControlState setState(Class<? extends ControlState> clazz)
	{
		if (state != null) {state.onExit();}
		state = stateHashMap.get(clazz);
		if (state == null)
		{
			System.out.println("[ControlStateSystem] You're missing a state you dunk.");
			try
			{
				stateHashMap.put(clazz, clazz.getConstructor(ControlStateSystem.class).newInstance(this));
				state = stateHashMap.get(clazz);
			}
			catch (Exception e)
			{
				// Goddammit
				e.printStackTrace();
			}
		}

		return state;
	}

	public void draw()
	{
		state.draw();
	}

	@Override
	public boolean keyDown(int keycode)
	{
		accumulator = 0;

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
				battle.screenTileHeight += 2;
				battle.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
				break;
			}
			case Input.Keys.L:
			{
				battle.screenTileHeight -= 2;
				battle.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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

	private Vector3 screenToWorld(int screenX, int screenY)
	{
		helper.x = screenX;
		helper.y = screenY;
		battle.world.unproject(helper);

		helper.x = Math.max(0, Math.min(battle.mapWidth - 1, helper.x));
		helper.y = Math.max(0, Math.min(battle.mapHeight - 1, helper.y));

		return helper;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		this.pointer = pointer;
		screenToWorld(screenX, screenY);
		state.pick(screenX, Gdx.graphics.getHeight() - screenY, (int)helper.x, (int)helper.y);

		return true;
	}

	public boolean touchUp(int screenX, int screenY, int pointer, int button)
	{
		if (this.pointer == pointer)
		{
			this.pointer = -1;
			this.timePressed = 0;
			if (!isCancelling)
			{
				screenToWorld(screenX, screenY);
				state.release(screenX, Gdx.graphics.getHeight() - screenY, (int)helper.x, (int)helper.y);
			}
			isCancelling = false;
		}
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer)
	{
		if (this.pointer == pointer)
		{
			timePressed = -1;

			screenToWorld(screenX, screenY);
			state.weakPick(screenX, Gdx.graphics.getHeight() - screenY, (int)helper.x, (int)helper.y);
		}

		return true;
	}

	private void doDirection()
	{
		state.doDirection(heldDirection);
	}

	public void update(float delta)
	{
		// Touch Stuff
		if (this.pointer != -1)
		{
			if (this.timePressed >= 0)
			{
				this.timePressed += delta;
			}

			if (this.timePressed > 0.3)
			{
				state.cancel();
				this.timePressed = -1;
				isCancelling = true;
			}
		}

		// Keyboard Stuff
		if (heldDirection != null)
		{
			if (timeHeld > 0.3)
			{
				while (accumulator > 0.03f)
				{
					doDirection();
					accumulator -= 0.03f;
				}
				accumulator += delta;
			}

			if (timeHeld == 0)
			{
				accumulator += delta;
				doDirection();
			}
			timeHeld += delta;
		}

		// State stuff
		state.update(delta);
		state.moveCam();
	}
}
