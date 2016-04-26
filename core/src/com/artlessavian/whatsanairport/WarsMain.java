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
	public SpriteBatch batch;
	AssetManager assetManager;

	// TODO: All the font stuff is random functions that seem to work
	public BitmapFont font;
	private float linesPerHeight;

	private boolean finishedLoading;

	public OrthographicCamera screenSpace;

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
		font.getData().setLineHeight(font.getCapHeight() + 10);
		font.getData().padTop = 5;
		font.getData().padBottom = 5;

		linesPerHeight = 12;

		screenSpace = new OrthographicCamera();

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

		screenSpace.viewportWidth = width;
		screenSpace.viewportHeight = height;
		screenSpace.position.x = screenSpace.viewportWidth / 2f;
		screenSpace.position.y = screenSpace.viewportHeight / 2f;

		// TODO: Figure out why divide 20 works
		setFontSize(linesPerHeight);

		screenSpace.update();
	}

	public void setFontSize(float linesPerHeight)
	{
		this.linesPerHeight = linesPerHeight;
		font.getData().setScale(screenSpace.viewportHeight/ 20f / linesPerHeight);
	}

	@Override
	public void dispose()
	{
		assetManager.dispose();
		batch.dispose();
		font.dispose();
	}
}
