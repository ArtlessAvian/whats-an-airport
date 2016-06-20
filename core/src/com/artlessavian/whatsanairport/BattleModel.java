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
		this.inputHandler.menus.add(new UnitMenu(this.inputHandler, map));
		this.inputHandler.menus.add(new DayMenu(this.inputHandler));
		this.inputHandler.receivers.add(this.inputHandler.cursor);

		this.turnHandler = new TurnHandler(this.map);

		// Then view
		this.view = new BattleView(this);

		// Testing
		this.map.makeUnit(UnitInfo.SOLDIER, 0, 4, 2);
		this.map.makeUnit(UnitInfo.MECH, 0, 2, 2);
		this.map.makeUnit(UnitInfo.SOLDIER, 0, 2, 4);
		this.map.makeUnit(UnitInfo.MOTORCYCLE, 0, 2, 6);
		this.map.makeUnit(UnitInfo.MECH, 0, 6, 4);

		this.map.makeUnit(UnitInfo.SOLDIER, 1, 4 + 6, 2);
		this.map.makeUnit(UnitInfo.MECH, 1, 2 + 6, 2);
		this.map.makeUnit(UnitInfo.SOLDIER, 1, 2 + 6, 4);
		this.map.makeUnit(UnitInfo.MOTORCYCLE, 1, 2 + 6, 6);
		this.map.makeUnit(UnitInfo.MECH, 1, 6 + 6, 4);
	}

	@Override
	public void show()
	{

	}

	@Override
	public void render(float delta)
	{
		this.inputHandler.update();

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
