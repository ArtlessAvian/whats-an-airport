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

	final OrthographicCamera worldSpace;

	private final float screenTileHeight = 12;
	final float tileSize = 64;
	private float centimetersPerTile;


	public BattleView(BattleModel battleModel)
	{
		this.model = battleModel;
		this.batch = WarsMain.getInstance().batch;
		this.bitmapFont = WarsMain.getInstance().bitmapFont;

		this.terrainTileSet = new Sprite(new Texture("Terrain.png"));
		this.terrainTileSet.setSize(tileSize, tileSize);
		this.terrainTileSet.setOrigin(tileSize/2f, tileSize/2f);

		this.unitTileSet = new Sprite(new Texture("Unit.png"));
		this.unitTileSet.setSize(tileSize, tileSize);
		this.unitTileSet.setOrigin(tileSize/2f, tileSize/2f);

		this.box = new Sprite(new Texture("Grid.png"));
		this.box.setSize(tileSize, tileSize);
		this.box.setOrigin(tileSize/2f, tileSize/2f);

		this.white = new Sprite(new Texture("White.png"));
		this.white.setSize(tileSize, tileSize);
		this.white.setOrigin(tileSize/2f, tileSize/2f);

		this.worldSpace = new OrthographicCamera();
	}

	public void render(float delta)
	{
		Gdx.gl.glClearColor(0.1f, 0.0f, 0.1f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		worldSpace.position.x = model.map.width * tileSize/2f;
		worldSpace.position.y = model.map.height * tileSize/2f;
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
				terrainTileSet.setPosition(x * tileSize, y * tileSize);
				terrainTileSet.draw(batch);

				if (tile.getUnit() != null)
				{
					white.setColor(Color.BLACK);
					white.setPosition(x * tileSize, y * tileSize);
					white.draw(batch, 0.3f);
				}

				//bitmapFont.draw(batch, x + " " + y, x * tileSize, y * tileSize);
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
					white.setPosition(x * tileSize, y * tileSize);
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
			unitTileSet.setPosition(unit.tile.x * tileSize, unit.tile.y * tileSize);

			if (!unit.instructions.isEmpty())
			{
				switch (unit.instructions.getFirst())
				{
					case RIGHT:
					{
						unitTileSet.translateX(unit.accumulator * tileSize / unit.unitInfo.moveFrames);
						break;
					}
					case UP:
					{
						unitTileSet.translateY(unit.accumulator * tileSize / unit.unitInfo.moveFrames);
						break;
					}
					case LEFT:
					{
						unitTileSet.translateX(unit.accumulator * -tileSize / unit.unitInfo.moveFrames);
						break;
					}
					case DOWN:
					{
						unitTileSet.translateY(unit.accumulator * -tileSize / unit.unitInfo.moveFrames);
						break;
					}
				}
			}

			unitTileSet.draw(batch);

			if (unit.selector != null)
			{
				box.setPosition(unit.tile.x * tileSize, unit.tile.y * tileSize);
				box.setSize(tileSize/2f, tileSize/2f);
				if (!unit.selector.instructions.isEmpty())
				{
					Iterator<UnitInstruction> iter = model.cursor.instructions.iterator();
					while (iter.hasNext())
					{
						switch (iter.next())
						{
							case RIGHT:
							{
								box.translateX(tileSize);
								break;
							}
							case UP:
							{
								box.translateY(tileSize);
								break;
							}
							case LEFT:
							{
								box.translateX(-tileSize);
								break;
							}
							case DOWN:
							{
								box.translateY(-tileSize);
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
		box.setSize(tileSize,tileSize);
		box.setPosition(model.cursor.x * tileSize, model.cursor.y * tileSize);
		box.draw(batch);


	}

	private void drawDebug()
	{
		bitmapFont.draw(batch, model.cursor.x + " " + model.cursor.y, 5, 35);

		for (int y = 0; y < model.map.height; y++)
		{
			for (int x = 0; x < model.map.width; x++)
			{
				Unit unit = model.map.tileMap[y][x].getUnit();
				if (unit != null)
				{
					bitmapFont.draw(batch, unit.instructions + "", x * tileSize, y * tileSize);
				}
			}
		}
	}

	public void resize(int width, int height)
	{
		worldSpace.viewportHeight = screenTileHeight * tileSize;
		worldSpace.viewportWidth = (float)width * worldSpace.viewportHeight / (float)height;
		worldSpace.update();

		centimetersPerTile = Math.round(10000 * tileSize / Gdx.graphics.getPpcY()) / 10000f;
	}
}
