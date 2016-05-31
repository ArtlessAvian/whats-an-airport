package com.artlessavian.whatsanairport;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Iterator;

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
		this.terrainTileSet.setSize(model.tileSize, model.tileSize);
		this.terrainTileSet.setOrigin(model.tileSize/2f, model.tileSize/2f);
		this.unitTileSet = new Sprite(new Texture("Unit.png"));
		this.unitTileSet.setSize(model.tileSize, model.tileSize);
		this.unitTileSet.setOrigin(model.tileSize/2f, model.tileSize/2f);
		this.box = new Sprite(new Texture("Grid.png"));
		this.box.setSize(model.tileSize, model.tileSize);
		this.box.setOrigin(model.tileSize/2f, model.tileSize/2f);
		this.white = new Sprite(new Texture("White.png"));
		this.white.setSize(model.tileSize, model.tileSize);
		this.white.setOrigin(model.tileSize/2f, model.tileSize/2f);

		this.worldSpace = new OrthographicCamera();
	}

	public void render(float delta)
	{
		Gdx.gl.glClearColor(0.1f, 0.0f, 0.1f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		worldSpace.position.x = model.map.width * model.tileSize/2f;
		worldSpace.position.y = model.map.height * model.tileSize/2f;
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
				terrainTileSet.setPosition(x * model.tileSize, y * model.tileSize);
				terrainTileSet.draw(batch);

				if (tile.unit != null)
				{
					white.setColor(Color.BLACK);
					white.setPosition(x * model.tileSize, y * model.tileSize);
					white.draw(batch, 0.3f);
				}

				//bitmapFont.draw(batch, x + " " + y, x * model.tileSize, y * model.tileSize);
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
				if (!tile.highlight.isEmpty())
				{
					white.setColor(tile.highlight.get(tile.highlight.size()-1));
					white.setPosition(x * model.tileSize, y * model.tileSize);
					white.draw(batch, 0.3f);
				}
			}
		}
	}

	private void drawUnits()
	{
		int timeOffset = (int)(2 - 2 * Math.cos((Gdx.graphics.getFrameId() / 12)));

		for (Unit unit : model.map.units)
		{
			uvShenanigans(4 * model.turnHandler.orderToColor[unit.owner] + timeOffset, 8, unit.unitInfo.id, 4, unitTileSet);
			unitTileSet.setPosition(unit.tile.x * model.tileSize, unit.tile.y * model.tileSize);

			if (!unit.instructions.isEmpty())
			{
				switch (unit.instructions.getFirst())
				{
					case RIGHT:
					{
						unitTileSet.translateX(unit.accumulator * model.tileSize / unit.unitInfo.moveFrames);
						break;
					}
					case UP:
					{
						unitTileSet.translateY(unit.accumulator * model.tileSize / unit.unitInfo.moveFrames);
						break;
					}
					case LEFT:
					{
						unitTileSet.translateX(unit.accumulator * -model.tileSize / unit.unitInfo.moveFrames);
						break;
					}
					case DOWN:
					{
						unitTileSet.translateY(unit.accumulator * -model.tileSize / unit.unitInfo.moveFrames);
						break;
					}
				}
			}

			unitTileSet.draw(batch);

			if (unit.selector != null)
			{
				box.setPosition(unit.tile.x * model.tileSize, unit.tile.y * model.tileSize);
				box.setSize(model.tileSize/2f, model.tileSize/2f);
				if (!unit.selector.instructions.isEmpty())
				{
					Iterator<UnitInstruction> iter = model.cursor.instructions.iterator();
					while (iter.hasNext())
					{
						switch (iter.next())
						{
							case RIGHT:
							{
								box.translateX(model.tileSize);
								break;
							}
							case UP:
							{
								box.translateY(model.tileSize);
								break;
							}
							case LEFT:
							{
								box.translateX(-model.tileSize);
								break;
							}
							case DOWN:
							{
								box.translateY(-model.tileSize);
								break;
							}
						}
						box.draw(batch);
					}
				}
			}
		}
	}

	private void drawCursor()
	{
		box.setSize(model.tileSize,model.tileSize);
		box.setPosition(model.cursor.x * model.tileSize, model.cursor.y * model.tileSize);
		box.draw(batch);


	}

	private void drawDebug()
	{
		bitmapFont.draw(batch, model.cursor.x + " " + model.cursor.y, 5, 35);
	}

	public void resize(int width, int height)
	{
		worldSpace.viewportHeight = screenTileHeight * model.tileSize;
		worldSpace.viewportWidth = (float)width * worldSpace.viewportHeight / (float)height;
		worldSpace.update();

		screenWorldScale = height / screenTileHeight;

		centimetersPerTile = Math.round(10000 * screenWorldScale / Gdx.graphics.getPpcY()) / 10000f;
	}
}
