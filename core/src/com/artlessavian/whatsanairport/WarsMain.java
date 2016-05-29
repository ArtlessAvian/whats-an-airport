package com.artlessavian.whatsanairport;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class WarsMain extends Game
{
	private static WarsMain instance = null;

	private void setInstance()
	{
		instance = this;
	}

	public static WarsMain getInstance()
	{
		return instance;
	}

	SpriteBatch batch;
	BitmapFont bitmapFont;

	@Override
	public void create()
	{
		this.setInstance();

		this.batch = new SpriteBatch();
		this.bitmapFont = new BitmapFont();

		this.setScreen(new BattleModel(this));
	}

	@Override
	public void render()
	{
		super.render();
	}
}
