package com.artlessavian.whatsanairport;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

		this.inputHandler.addState(Cursor.class, false, false);

		this.turnHandler = new TurnHandler(this.map);

		// Then view
		this.view = new BattleView(this);

		// Testing
		this.map.makeUnit(UnitInfo.SOLDIER, 0, 4, 2);
		this.map.makeUnit(UnitInfo.MECH, 0, 2, 2);
		this.map.makeUnit(UnitInfo.SOLDIER, 0, 2, 4);
		this.map.makeUnit(UnitInfo.MOTORCYCLE, 0, 2, 6);
		this.map.makeUnit(UnitInfo.MECH, 0, 6, 4);
		this.map.makeUnit(UnitInfo.HAX, 0, 0, 0);

		for (int i = 0; i < 9; i++)
		{
			this.map.makeUnit(UnitInfo.SOLDIER, 1, (int)(Math.random() * map.width), (int)(Math.random() * map.height));
			this.map.makeUnit(UnitInfo.MECH, 1, (int)(Math.random() * map.width), (int)(Math.random() * map.height));
			this.map.makeUnit(UnitInfo.SOLDIER, 1, (int)(Math.random() * map.width), (int)(Math.random() * map.height));
			this.map.makeUnit(UnitInfo.MOTORCYCLE, 1, (int)(Math.random() * map.width), (int)(Math.random() * map.height));
			this.map.makeUnit(UnitInfo.MECH, 1, (int)(Math.random() * map.width), (int)(Math.random() * map.height));
		}
	}

	@Override
	public void show()
	{

	}

	boolean lol;

	@Override
	public void render(float delta)
	{
		if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSLASH))
		{
			lol = !lol;
		}

		if (lol)
		{
			int deltaY = (int)(Math.random() * 9 - 4);
			for (int i = 0; i < deltaY; i++) {this.inputHandler.up();}
			for (int i = 0; i > deltaY; i--) {this.inputHandler.down();}

			int deltaX = (int)(Math.random() * 9 - 4);
			for (int i = 0; i < deltaX; i++) {this.inputHandler.right();}
			for (int i = 0; i > deltaX; i--) {this.inputHandler.left();}

			if (Math.random() < 0.3f) {this.inputHandler.keyDown(Input.Keys.J);}
			if (Math.random() < 0.1f) {this.inputHandler.keyDown(Input.Keys.N);}
		}

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
