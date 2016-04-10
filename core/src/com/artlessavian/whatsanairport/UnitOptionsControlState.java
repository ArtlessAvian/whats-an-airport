package com.artlessavian.whatsanairport;

import com.badlogic.gdx.math.Vector3;

import java.util.Iterator;
import java.util.LinkedList;

public class UnitOptionsControlState implements ControlState
{
	private enum Options
	{
		END, ATTACK, JOIN, CAPTURE
	}

	private final ControlStateSystem controlStateSystem;
	private final BattleScreen battle;

	private Unit selectedUnit;
	private int x;
	private int y;
	private int originX;
	private int originY;
	private Unit displaced;

	private final LinkedList<Float> pushOptionRight;
	private final LinkedList<Options> options;
	private int position;

	private Vector3 unitPosition;

	public UnitOptionsControlState(ControlStateSystem controlStateSystem)
	{
		this.controlStateSystem = controlStateSystem;
		this.battle = controlStateSystem.battle;

		options = new LinkedList<Options>();
		pushOptionRight = new LinkedList<Float>();
		unitPosition = new Vector3();
	}

	@Override
	public void enter(Object... varargs)
	{
		selectedUnit = (Unit)varargs[0];
		originX = (Integer)varargs[1];
		originY = (Integer)varargs[2];
		x = (Integer)varargs[3];
		y = (Integer)varargs[4];
		displaced = (Unit)varargs[5];

		position = 0;

		pushOptionRight.clear();
		options.clear();

		// Join if standing on friendly
		if (displaced != null && displaced != selectedUnit)
		{
			options.addLast(Options.JOIN);
			pushOptionRight.addLast(0f);
		} else
		{
			// Capture if standing on property and not joining
			//		options.addLast(Options.END);
			//		pushOptionRight.addLast(0f);

			// Attack if not joining and can attack
			boolean canAttack = false;
			for (MapTile t : selectedUnit.tile.neighbors.keySet())
			{
				if (t.unit != null && !t.unit.team.equals(selectedUnit.team))
				{
					canAttack = true;
					break;
				}
			}
			if (canAttack)
			{
				options.addLast(Options.ATTACK);
				pushOptionRight.addLast(0f);
			}

			options.addLast(Options.END);
			pushOptionRight.addLast(0f);
		}
	}

	@Override
	public void up()
	{
		position--;
		if (position < 0)
		{
			position += options.size();
		}
	}

	@Override
	public void down()
	{
		position++;
		if (position >= options.size())
		{
			position -= options.size();
		}
	}

	@Override
	public void left()
	{

	}

	@Override
	public void right()
	{

	}

	@Override
	public void pick(int screenX, int screenY, int x, int y)
	{
		float xb;
		float yb;

		if (unitPosition.x > 0) {xb = battle.main.screen.viewportHeight / 10f;} else
		{
			xb = 9 * battle.main.screen.viewportHeight / 10f;
		}
		if (unitPosition.y > 0)
		{
			yb = battle.main.screen.viewportHeight / 10f + 10 - (1 - options.size()) * (battle.main.font.getLineHeight() + 10f);
		} else {yb = battle.main.screen.viewportHeight / 10f;}

		if (xb < screenX && screenX < xb + 4 * (battle.main.font.getLineHeight() + 10f))
		{
			if (0 <= ((yb - screenY) / (battle.main.font.getLineHeight() + 10f)) + 1 && ((yb - screenY) / (battle.main.font.getLineHeight() + 10f)) + 1 < options.size())
			{
				if (position == (int)(((yb - screenY) / (battle.main.font.getLineHeight() + 10f)) + 1))
				{
					select();
				} else
				{
					position = (int)(((yb - screenY) / (battle.main.font.getLineHeight() + 10f)) + 1);
				}
			}
		}
	}

	@Override
	public void weakPick(int screenX, int screenY, int x, int y)
	{
		// TODO use delta screen coords
	}

	@Override
	public void release(int screenX, int screenY, int x, int y)
	{

	}

	@Override
	public void select()
	{
		switch (options.get(position))
		{
			case ATTACK:
			{
				// Probably an attack control state
				for (MapTile t : selectedUnit.tile.neighbors.keySet())
				{
					if (t.unit != null && !t.unit.team.equals(selectedUnit.team))
					{
						selectedUnit.attack(t.unit, false);
						break;
					}
				}

				controlStateSystem.setState(SelectUnitControlState.class);
				controlStateSystem.state.enter(x, y);
				break;
			}

			case JOIN:
			{
				selectedUnit.health += displaced.health;
				if (selectedUnit.tile.unit.health > 10) {selectedUnit.tile.unit.health = 10;}

				controlStateSystem.setState(SelectUnitControlState.class);
				controlStateSystem.state.enter(x, y);
				break;
			}

			case END:
			{
				controlStateSystem.setState(SelectUnitControlState.class);
				controlStateSystem.state.enter(x, y);
				break;
			}
		}
	}

	@Override
	public void cancel()
	{
		selectedUnit.move(battle.map.map[originX][originY]);
		selectedUnit.sprite.setPosition(originX, originY);
		if (displaced != null)
		{
			displaced.tile = battle.map.map[x][y];
			battle.map.map[x][y].unit = displaced;
		}
		controlStateSystem.setState(MoveUnitControlState.class);
		MovementRange range = selectedUnit.getRange();
		for (MapTile t : range.edgeAttackable)
		{
			t.register(this, WarsConst.selectRed);
			//t.debug = true;
		}
		for (MapTile t : range.movable)
		{
			t.register(this, WarsConst.selectBlue);
		}
	}

	@Override
	public void update(float delta)
	{
		for (int i = 0; i < pushOptionRight.size(); i++)
		{
			if (i == position)
			{
				float a = pushOptionRight.pollFirst();
				a += 0.3f * (60 - a);
				pushOptionRight.addLast(a);
			} else
			{
				float a = pushOptionRight.pollFirst();
				a += 0.3f * (0 - a);
				pushOptionRight.addLast(a);
			}
		}
	}

	@Override
	public void moveCam()
	{

	}

	@Override
	public void draw()
	{
		battle.main.batch.setProjectionMatrix(battle.main.screen.combined);

		unitPosition.x = x + 0.5f;
		unitPosition.y = y + 0.5f;

		unitPosition.sub(battle.trueCamPos);

		float xb;
		float yb;

		if (unitPosition.x > 0) {xb = battle.main.screen.viewportHeight / 10f;} else
		{
			xb = battle.main.screen.viewportWidth - battle.main.screen.viewportHeight / 10f - 4 * (battle.main.font.getLineHeight() + 10f);
		}
		if (unitPosition.y > 0) {yb = battle.main.screen.viewportHeight / 10f;} else
		{
			yb = 9 * battle.main.screen.viewportHeight / 10f - (options.size()) * (battle.main.font.getLineHeight() + 10f);
		}

		for (int i = 0; i < options.size(); i++)
		{
			battle.main.batch.draw(battle.white, xb, yb + i * (battle.main.font.getLineHeight() + 10f), 4 * (battle.main.font.getLineHeight() + 10f), (battle.main.font.getLineHeight() + 10f));
		}

		int i = 0;
		Iterator<Options> iter = options.iterator();
		while (iter.hasNext())
		{
			battle.main.font.draw(battle.main.batch, iter.next().name() + " " + i, xb + 5 + pushOptionRight.get(i), yb + (options.size() - i) * (battle.main.font.getLineHeight() + 10f) - 10f);
			i++;
		}

		battle.main.batch.setProjectionMatrix(battle.world.combined);
	}
}
