package com.artlessavian.whatsanairport;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class WarsMain extends Game
{
	SpriteBatch batch;
	private BitmapFont text;
	private AssetManager assetManager;

	@Override
	public void create()
	{
		batch = new SpriteBatch();
		text = new BitmapFont();
		assetManager = new AssetManager();

		this.setScreen(new BattleScreen(this));
	}

	@Override
	public void render()
	{
		Gdx.gl.glClearColor(0.5f, 0.5f, 1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		super.render();
	}
}
