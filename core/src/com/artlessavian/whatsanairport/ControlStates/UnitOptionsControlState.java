package com.artlessavian.whatsanairport.ControlStates;

import com.artlessavian.whatsanairport.BattleScreen;
import com.artlessavian.whatsanairport.ControlStateSystem;
import com.artlessavian.whatsanairport.MapTile;
import com.artlessavian.whatsanairport.Unit;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;

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

	private final Sprite selector;

	private final Vector3 unitPosition;

	public UnitOptionsControlState(ControlStateSystem controlStateSystem)
	{
		this.controlStateSystem = controlStateSystem;
		this.battle = controlStateSystem.battle;

		options = new LinkedList<Options>();
		pushOptionRight = new LinkedList<Float>();
		unitPosition = new Vector3();

		selector = new Sprite(battle.grid);
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

		options.clear();

		// Join if standing on friendly
		if (displaced != null && displaced != selectedUnit)
		{
			options.addLast(Options.JOIN);
		} else
		{
			// Capture if standing on property and not joining
			//		options.addLast(Options.END);
			//		pushOptionRight.addLast(0f);

			// Attack if not joining and can attack
			boolean canAttack = false;
			for (MapTile t : selectedUnit.tile.neighborToDir.keySet())
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
			}

			options.addLast(Options.END);

			for (int i = (int)(Math.random() * 7); i >= 0; i--)
			{
				options.addLast(Options.END);
			}

			pushOptionRight.clear();
			pushOptionRight.add(60f);
			while (options.size() > pushOptionRight.size())
			{
				pushOptionRight.add(0f);
			}
		}
	}

	@Override
	public void cancelReturn()
	{

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

	private float[] getBoxCoord(boolean rightOfCenter, boolean topOfCenter)
	{
		float[] thing = new float[4];

		thing[3] = battle.main.font.getLineHeight() + 10f;
		thing[2] = 4 * thing[3];

		// It's better to keep things consistent I suppose
		// Maybe I recycle this later

//		if (rightOfCenter)
//		{
//			thing[0] = Gdx.graphics.getHeight() / 10f;
//		}
//		else
		{
			thing[0] = Gdx.graphics.getWidth()
				- Gdx.graphics.getHeight() / 10f
				- thing[2];
		}

//		if (topOfCenter)
//		{
//			thing[1] = Gdx.graphics.getHeight() / 10f;
//		}
//		else
		{
			thing[1] = 9 * Gdx.graphics.getHeight() / 10f
				- options.size() * thing[3];
		}
		
		return thing;
	}
	
	@Override
	public void pick(int screenX, int screenY, int x, int y)
	{
		float[] thing = getBoxCoord(unitPosition.x > 0, unitPosition.y > 0);
		
		if (thing[0] < screenX && screenX < thing[0] + thing[2])
		{
			float calculatedPosition = options.size() - (screenY - thing[1]) / thing[3];
			if (0 <= calculatedPosition && calculatedPosition < options.size())
			{
				if (position == (int)calculatedPosition)
				{
					select();
				} else
				{
					position = (int)calculatedPosition;
				}
			}
		}
	}

	@Override
	public void weakPick(int screenX, int screenY, int x, int y)
	{
		float[] thing = getBoxCoord(unitPosition.x > 0, unitPosition.y > 0);

		if (thing[0] < screenX && screenX < thing[0] + thing[2])
		{
			float calculatedPosition = options.size() - (screenY - thing[1]) / thing[3];
			if (0 <= calculatedPosition && calculatedPosition < options.size())
			{
				position = (int)calculatedPosition;
			}
		}
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
				selectedUnit.sprite.setRotation(0);
				controlStateSystem.setState(ChooseAttackControlState.class).enter(x, y, selectedUnit);
				break;
			}

			case JOIN:
			{
				displaced.joined(selectedUnit);

				selectedUnit.sprite.setRotation(0);
				controlStateSystem.setState(SelectUnitControlState.class).enter(x, y);
				break;
			}

			case END:
			{
				selectedUnit.sprite.setRotation(0);
				controlStateSystem.setState(SelectUnitControlState.class).enter(x, y);
				break;
			}
		}
	}

	@Override
	public void cancel()
	{
		selectedUnit.move(battle.map.map[originX][originY]);
		selectedUnit.sprite.setPosition(originX, originY);
		selectedUnit.sprite.setRotation(0);
		if (displaced != null)
		{
			displaced.tile = battle.map.map[x][y];
			battle.map.map[x][y].unit = displaced;
		}
		controlStateSystem.setState(MoveUnitControlState.class).cancelReturn();
	}

	@Override
	public void update(float delta)
	{
		for (int i = 0; i < pushOptionRight.size(); i++)
		{
			if (i == position)
			{
				float a = pushOptionRight.pollFirst();
				a += 0.1f * (60 - a);
				pushOptionRight.addLast(a);
			} else
			{
				float a = pushOptionRight.pollFirst();
				a += 0.1f * (0 - a);
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
		if (displaced != null)
		{
			displaced.draw(battle.main.batch);
		}
		selectedUnit.sprite.rotate(145);

		battle.main.batch.setProjectionMatrix(battle.main.screen.combined);

		unitPosition.x = x + 0.5f;
		unitPosition.y = y + 0.5f;
		unitPosition.sub(battle.trueCamPos);
		
		float[] thing = getBoxCoord(unitPosition.x > 0, unitPosition.y > 0);

		for (int i = 0; i < options.size(); i++)
		{
			battle.main.batch.draw(battle.white, thing[0], thing[1] + i * (thing[3]),
				thing[2], (thing[3]));
		}

		int i = 0;
		for (Options option : options)
		{
			battle.main.font.getColor().set(pushOptionRight.get(i) / 120 + 0.5f, pushOptionRight.get(i) / 120 + 0.5f, pushOptionRight.get(i) / 120 + 0.5f, 1);
			battle.main.font.draw(battle.main.batch, option.name(),
				thing[0] + 5 + pushOptionRight.get(i),
				thing[1] + (options.size() - i) * (thing[3]) - 10f);
			i++;
		}

		selector.setSize(thing[2], thing[3]);
		selector.setPosition(thing[0], thing[1] + (options.size() - 1 - position) * (thing[3]));
		selector.draw(battle.main.batch);

		battle.main.batch.setProjectionMatrix(battle.world.combined);
	}
}
