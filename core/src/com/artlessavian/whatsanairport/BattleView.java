package com.artlessavian.whatsanairport;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BattleView
{
	private BattleModel model;

	private SpriteBatch batch;
	private BitmapFont bitmapFont;

	private Sprite tileSet;

	private OrthographicCamera worldSpace;
	private float screenTileHeight = 12;

	private float screenWorldScale;
	private float centimetersPerTile;


	public BattleView(BattleModel battleModel)
	{
		this.model = battleModel;
		batch = WarsMain.getInstance().batch;
		bitmapFont = WarsMain.getInstance().bitmapFont;

		tileSet = new Sprite(new Texture("Terrain.png"));
		tileSet.setSize(64, 64);
		tileSet.setOrigin(32, 32);

		worldSpace = new OrthographicCamera();
	}

	public void render(float delta)
	{
		Gdx.gl.glClearColor(0.1f, 0.0f, 0.1f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		worldSpace.position.x = model.map.width * 32;
		worldSpace.position.y = model.map.height * 32;
		worldSpace.update();

		batch.setProjectionMatrix(worldSpace.combined);

		batch.begin();
		// Stub Method
		this.drawMap();
		this.drawUnits();
		this.drawDebug();

		batch.end();
	}

	private void uvShenanigans(int xPos, int xTotal, int yPos, int yTotal, Sprite sprite)
	{
		sprite.setU((float)xPos/(float)xTotal);
		sprite.setU2((float)(xPos+1)/(float)xTotal);
		sprite.setV((float)yPos/(float)yTotal);
		sprite.setV2((float)(yPos+1)/(float)yTotal);
	}

	private void drawMap()
	{
		for (int y = 0; y < model.map.height; y++)
		{
			for (int x = 0; x < model.map.width; x++)
			{
				Tile tile = model.map.tileMap[y][x];
				uvShenanigans(0,1,tile.tileInfo.id,4,tileSet);
				tileSet.setPosition(x * 64, y * 64);
				tileSet.draw(batch);

				//bitmapFont.draw(batch, x + " " + y, x * 64, y * 64);
			}
		}
	}

	private void drawUnits()
	{
	}

	private void drawDebug()
	{
	}

	public void resize(int width, int height)
	{
		worldSpace.viewportHeight = screenTileHeight * 64;
		worldSpace.viewportWidth = (float)width * worldSpace.viewportHeight / (float)height;
		worldSpace.update();

		screenWorldScale = height / screenTileHeight;

		centimetersPerTile = Math.round(10000 * screenWorldScale / Gdx.graphics.getPpcY()) / 10000f;
	}
}
