package com.artlessavian.whatsanairport;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;

public class BattleModel implements Screen
{
	private WarsMain main;
	private BattleView view;

	final Map map;


	public BattleModel(WarsMain warsMain)
	{
		this.main = warsMain;

		// Everything
		FileHandle file = Gdx.files.internal("map.txt");
		String text = file.readString();
		this.map = new Map(text);

		// Then view
		this.view = new BattleView(this);
	}

	@Override
	public void show()
	{

	}

	@Override
	public void render(float delta)
	{


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
