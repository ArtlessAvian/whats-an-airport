package com.artlessavian.whatsanairport;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;

class BattleModel implements Screen
{
	private final WarsMain main;
	private final BattleView view;

	private final InputHandler inputHandler;

	final Map map;
	final Cursor cursor;
	final TurnHandler turnHandler;

	public BattleModel(WarsMain warsMain)
	{
		this.main = warsMain;

		this.inputHandler = new InputHandler();
		Gdx.input.setInputProcessor(inputHandler);

		// Everything
		FileHandle file = Gdx.files.internal("map.txt");
		String text = file.readString();
		this.map = new Map(text);

		this.cursor = new Cursor(map);
		inputHandler.receivers.add(cursor);

		this.turnHandler = new TurnHandler();

		// Then view
		this.view = new BattleView(this);

		// Testing
		this.map.tileMap[5][2].unit = new Unit(UnitInfo.SOLDIER, this.map.tileMap[5][2]);
		this.map.tileMap[4][2].unit = new Unit(UnitInfo.SOLDIER, this.map.tileMap[4][2]);
		this.map.tileMap[4][3].unit = new Unit(UnitInfo.SOLDIER, this.map.tileMap[4][3]);

		this.map.units.add(this.map.tileMap[5][2].unit);
		this.map.units.add(this.map.tileMap[4][2].unit);
		this.map.units.add(this.map.tileMap[4][3].unit);
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

		this.view.render(delta);
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
