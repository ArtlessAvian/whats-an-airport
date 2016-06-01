package com.artlessavian.whatsanairport;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;

import java.util.ArrayList;

class BattleModel implements Screen
{
	private final WarsMain main;
	final BattleView view;

	final InputHandler inputHandler;

	final Map map;
	final TurnHandler turnHandler;

	public BattleModel(WarsMain warsMain)
	{
		this.main = warsMain;

		this.inputHandler = new InputHandler(this);
		Gdx.input.setInputProcessor(inputHandler);

		// Init Stuff
		FileHandle file = Gdx.files.internal("map.txt");
		String text = file.readString();
		this.map = new Map(text);

		this.inputHandler.cursor = new Cursor(this.inputHandler, map);
		this.inputHandler.activeMenu = null;
		this.inputHandler.menus = new ArrayList<>();
		this.inputHandler.menus.add(new UnitMenu(this.inputHandler));
		this.inputHandler.receivers.add(this.inputHandler.cursor);

		this.turnHandler = new TurnHandler();

		// Then view
		this.view = new BattleView(this);

		// Testing
		this.map.tileMap[5][2].setUnit(new Unit(UnitInfo.SOLDIER, this.map.tileMap[5][2]));
		this.map.tileMap[4][2].setUnit(new Unit(UnitInfo.SOLDIER, this.map.tileMap[4][2]));
		this.map.tileMap[4][3].setUnit(new Unit(UnitInfo.SOLDIER, this.map.tileMap[4][3]));

		this.map.units.add(this.map.tileMap[5][2].getUnit());
		this.map.units.add(this.map.tileMap[4][2].getUnit());
		this.map.units.add(this.map.tileMap[4][3].getUnit());
	}

	@Override
	public void show()
	{

	}

	@Override
	public void render(float delta)
	{
		this.inputHandler.update();

//		for (int i = 0; i < 100; i++)
//		{
//			float random = (float)Math.random();
//
//			if (random < 0.2)
//			{
//				cursor.up();
//			}
//			else if (random < 0.4)
//			{
//				cursor.down();
//			}
//			else if (random < 0.6)
//			{
//				cursor.left();
//			}
//			else if (random < 0.8)
//			{
//				cursor.right();
//			}
//			else
//			{
//				cursor.select();
//			}
//
//			this.map.update();
//		}

		this.map.update();

		this.view.render();
	}

	@Override
	public void resize(int width, int height)
	{
		view.resize(width, height);
	}

	@Override
	public void pause()
	{

	}

	@Override
	public void resume()
	{

	}

	@Override
	public void hide()
	{

	}

	@Override
	public void dispose()
	{

	}
}
