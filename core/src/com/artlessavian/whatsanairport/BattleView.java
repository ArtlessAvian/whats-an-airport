package com.artlessavian.whatsanairport;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

class BattleView
{
	private final BattleModel model;

	private final SpriteBatch batch;
	private final BitmapFont bitmapFont;

	private final Sprite terrainTileSet;
	private final Sprite unitTileSet;
	private final Sprite box;
	private final Sprite white;

	private final OrthographicCamera worldSpace;
	private final float screenTileHeight = 12;

	private float screenWorldScale;
	private float centimetersPerTile;


	public BattleView(BattleModel battleModel)
	{
		this.model = battleModel;
		this.batch = WarsMain.getInstance().batch;
		this.bitmapFont = WarsMain.getInstance().bitmapFont;

		this.terrainTileSet = new Sprite(new Texture("Terrain.png"));
		this.terrainTileSet.setSize(64, 64);
		this.terrainTileSet.setOrigin(32, 32);
		this.unitTileSet = new Sprite(new Texture("Unit.png"));
		this.unitTileSet.setSize(64, 64);
		this.unitTileSet.setOrigin(32, 32);
		this.box = new Sprite(new Texture("Grid.png"));
		this.box.setSize(64, 64);
		this.box.setOrigin(32, 32);
		this.white = new Sprite(new Texture("White.png"));
		this.white.setSize(64, 64);
		this.white.setOrigin(32, 32);

		this.worldSpace = new OrthographicCamera();
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

		this.drawMap();
		this.drawUnits();
		this.drawHighlight();
		this.drawCursor();
		this.drawDebug();

		batch.end();
	}

	private void uvShenanigans(int xPos, int xTotal, int yPos, int yTotal, Sprite sprite)
	{
		sprite.setU((float)xPos / (float)xTotal);
		sprite.setU2((float)(xPos + 1) / (float)xTotal);
		sprite.setV((float)yPos / (float)yTotal);
		sprite.setV2((float)(yPos + 1) / (float)yTotal);
	}

	private void drawMap()
	{
		for (int y = 0; y < model.map.height; y++)
		{
			for (int x = 0; x < model.map.width; x++)
			{
				Tile tile = model.map.tileMap[y][x];
				uvShenanigans(0, 1, tile.tileInfo.id, 4, terrainTileSet);
				terrainTileSet.setPosition(x * 64, y * 64);
				terrainTileSet.draw(batch);

				//bitmapFont.draw(batch, x + " " + y, x * 64, y * 64);
			}
		}
	}

	private void drawHighlight()
	{
		for (int y = 0; y < model.map.height; y++)
		{
			for (int x = 0; x < model.map.width; x++)
			{
				Tile tile = model.map.tileMap[y][x];
				if (tile.highlight != null)
				{
					tile.highlightStrength += (1 - tile.highlightStrength) * 0.05f;

					white.setColor(tile.highlight);
					white.setPosition(x * 64, y * 64);
					white.draw(batch, 0.3f * tile.highlightStrength);
				}
				else
				{
					tile.highlightStrength += (0 - tile.highlightStrength) * 0.05f;
				}
			}
		}
	}

	private void drawUnits()
	{
		int timeOffset = (int)(2 - 2 * Math.cos((Gdx.graphics.getFrameId() / 12)));

		for (int y = 0; y < model.map.height; y++)
		{
			for (int x = 0; x < model.map.width; x++)
			{
				Unit unit = model.map.tileMap[y][x].unit;

				if (unit != null)
				{
					uvShenanigans(4 * model.turnHandler.orderToColor[unit.owner] + timeOffset, 8, unit.unitInfo.id, 4, unitTileSet);
					unitTileSet.setPosition(x * 64, y * 64);
					unitTileSet.flip(unit.selected, false);
					unitTileSet.draw(batch);
				}

			}
		}
	}

	private void drawCursor()
	{
		box.setPosition(model.cursor.x * 64, model.cursor.y * 64);
		box.draw(batch);
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
