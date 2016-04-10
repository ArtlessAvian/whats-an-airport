package com.artlessavian.whatsanairport;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;

public class WarsMain extends Game
{
	SpriteBatch batch;
	AssetManager assetManager;
	BitmapFont font;
	private boolean finishedLoading;

	OrthographicCamera screen;

	@Override
	public void create()
	{
		batch = new SpriteBatch();
		assetManager = new AssetManager();

		assetManager.load("Grid.png", Texture.class);
		assetManager.load("White.png", Texture.class);
		assetManager.load("Health.png", Texture.class);
		assetManager.load("Terrain.png", Texture.class);
		assetManager.load("Units/Red/Soldier.png", Texture.class);
		assetManager.load("Units/Blue/Soldier.png", Texture.class);

		font = new BitmapFont();

		screen = new OrthographicCamera();

		Unit.textures = new HashMap<String, Texture>();
	}

	@Override
	public void render()
	{
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		
		if (!finishedLoading && assetManager.update())
		{
			Texture tempg = assetManager.get("Health.png", Texture.class);
			WarsConst.healthTextures = TextureRegion.split(tempg, 16, 32)[0];

			this.setScreen(new BattleScreen(this));
			finishedLoading = true;
		}
		
		super.render();
	}

	@Override
	public void resize(int width, int height)
	{
		super.resize(width, height);

		screen.viewportWidth = width;
		screen.viewportHeight = height;
		screen.position.x = width / 2f;
		screen.position.y = height / 2f;

		font.getData().setScale(height / 20f / 12f);

		screen.update();
	}

	@Override
	public void dispose()
	{
		assetManager.dispose();
		batch.dispose();
		font.dispose();
	}
}
