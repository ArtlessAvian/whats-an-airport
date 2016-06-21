package com.artlessavian.whatsanairport;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

import java.util.Iterator;

class BattleView
{
	private final BattleModel model;

	private final SpriteBatch batch;
	private final BitmapFont bitmapFont;
	private final GlyphLayout glyphLayout;

	private final Sprite terrainTileSet;
	private final Sprite unitTileSet;
	private final Sprite box;
	private final Sprite white;
	private final Sprite options;
	private final Sprite path;

	Vector3 helper = new Vector3();

	final OrthographicCamera worldSpace;
	final Vector3 trueCamPos;

	private final float screenTileHeight = 12;
	final float tileSize = 64;
	public int debuggery;


	public BattleView(BattleModel battleModel)
	{
		this.model = battleModel;
		this.batch = WarsMain.getInstance().batch;
		this.bitmapFont = WarsMain.getInstance().bitmapFont;
		this.glyphLayout = WarsMain.getInstance().glyphLayout;

		this.terrainTileSet = new Sprite(new Texture("Terrain.png"));
		this.terrainTileSet.setSize(tileSize, tileSize);
		this.terrainTileSet.setOrigin(tileSize / 2f, tileSize / 2f);

		this.unitTileSet = new Sprite(new Texture("Unit.png"));
		this.unitTileSet.setSize(tileSize, tileSize);
		this.unitTileSet.setOrigin(tileSize / 2f, tileSize / 2f);

		this.box = new Sprite(new Texture("Grid.png"));
		this.box.setSize(tileSize, tileSize);
		this.box.setOrigin(tileSize / 2f, tileSize / 2f);

		this.white = new Sprite(new Texture("White.png"));
		this.white.setSize(tileSize, tileSize);
		this.white.setOrigin(tileSize / 2f, tileSize / 2f);

		this.options = new Sprite(new Texture("Options.png"));

		this.path = new Sprite(new Texture("Path.png"));
		this.path.setSize(tileSize, tileSize);
		this.path.setOrigin(tileSize / 2f, tileSize / 2f);

		this.worldSpace = new OrthographicCamera();
		this.trueCamPos = new Vector3(model.map.width * tileSize / 2f, model.map.height * tileSize / 2f, 0);

		worldSpace.position.x = model.map.width * tileSize / 2f;
		worldSpace.position.y = model.map.height * tileSize / 2f;
		worldSpace.update();
	}

	public void focus(float tileLeeway, float tileX, float tileY)
	{
		tileLeeway *= tileSize;
		tileX *= tileSize;
		tileY *= tileSize;

		helper.set(tileX, tileY, 0);

		// Terrible fix imo.
		// I dunno how costly recalculating the camera is, but it shouldnt be too bad.
		float tempX = worldSpace.position.x;
		float tempY = worldSpace.position.y;
		worldSpace.position.set(trueCamPos);
		worldSpace.update();
		worldSpace.project(helper);
		worldSpace.position.set(tempX, tempY, 0);

		if (Gdx.graphics.getWidth() > model.map.width * tileSize)
		{
			trueCamPos.x = model.map.width * tileSize / 2f;
		}
		else if (helper.x < tileLeeway)
		{
			trueCamPos.x -= tileSize;
		}
		else if (helper.x > Gdx.graphics.getWidth() - tileLeeway)
		{
			trueCamPos.x += tileSize;
		}

		if (screenTileHeight > model.map.height)
		{
			trueCamPos.y = model.map.height * tileSize / 2f;
		}
		else if (helper.y < tileLeeway)
		{
			trueCamPos.y -= tileSize;
		}
		else if (helper.y > Gdx.graphics.getHeight() - tileLeeway)
		{
			trueCamPos.y += tileSize;
		}
	}

	public void render()
	{
		Gdx.gl.glClearColor(0.1f, 0.0f, 0.1f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		focus(3, model.inputHandler.cursor.x, model.inputHandler.cursor.y);

		worldSpace.position.lerp(trueCamPos, 0.05f);
		worldSpace.update();
		batch.setProjectionMatrix(worldSpace.combined);

		batch.begin();

		Class temp = model.inputHandler.receivers.get(model.inputHandler.receivers.size() - 1).getClass();

		this.drawMap();

		if (temp.equals(AttackInputReceiver.class))
		{
			this.drawAttack();
		}

		this.drawUnits();
		if (temp.equals(Cursor.class))
		{
			this.drawPath();
		}

		this.drawHighlight();

		if (temp.equals(Cursor.class))
		{
			this.drawCursor();
		}

		if (model.inputHandler.activeMenu != null)
		{
			this.drawMenu();
		}

		if (debuggery % 2 == 1)
		{
			this.drawDebug();
		}

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

				if ((debuggery / 2) % 2 == 1)
				{
					if (tile.getUnit() != null)
					{
						white.setColor(Color.BLACK);
						white.setPosition(x * tileSize, y * tileSize);
						white.draw(batch, 0.3f);
					}
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
					white.setColor(tile.highlight.get(tile.highlight.size() - 1));
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

			if (unit.instructions != null && unit.instructions.hasNext())
			{
				switch (unit.instructions.next())
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
				unit.instructions.previous();
			}

			if (unit.done)
			{
				unitTileSet.setColor(Color.DARK_GRAY);
			}
			else
			{
				unitTileSet.setColor(Color.WHITE);
			}

			unitTileSet.draw(batch);

			bitmapFont.draw(batch, unit.health + "", unitTileSet.getX(), unitTileSet.getY() + 35);

		}
	}

	private void drawPath()
	{
		if (model.inputHandler.cursor.selectedUnit != null)
		{
			path.setPosition(model.inputHandler.cursor.selectedUnit.tile.x * tileSize, model.inputHandler.cursor.selectedUnit.tile.y * tileSize);
			if (!model.inputHandler.cursor.instructions.isEmpty())
			{
				Iterator<UnitInstruction> iter = model.inputHandler.cursor.instructions.iterator();
				UnitInstruction last = iter.next();
				while (iter.hasNext())
				{
					UnitInstruction current = iter.next();

					path.setRotation(0);

					if (current.id == last.id)
					{
						uvShenanigans(0, 3, 0, 1, path);
					}
					else
					{
						uvShenanigans(2, 3, 0, 1, path);
						if ((last.id + 1) % 4 == current.id)
						{
							path.rotate(-90);
						}
					}

					switch (last)
					{
						case RIGHT:
						{
							path.rotate(-90);
							path.translateX(tileSize);

							break;
						}
						case UP:
						{
							path.rotate(0);
							path.translateY(tileSize);

							break;
						}
						case LEFT:
						{
							path.rotate(90);
							path.translateX(-tileSize);
							break;
						}
						case DOWN:
						{
							path.rotate(180);
							path.translateY(-tileSize);
							break;
						}
					}
					path.draw(batch);
					last = current;
				}

				uvShenanigans(1, 3, 0, 1, path);
				switch (last)
				{
					case RIGHT:
					{
						path.translateX(tileSize);
						path.setRotation(-90);
						break;
					}
					case UP:
					{
						path.translateY(tileSize);
						path.setRotation(0);
						break;
					}
					case LEFT:
					{
						path.translateX(-tileSize);
						path.setRotation(90);
						break;
					}
					case DOWN:
					{
						path.translateY(-tileSize);
						path.setRotation(180);
						break;
					}
				}

				path.draw(batch);
			}
		}
	}

	private void drawCursor()
	{
		box.setSize(tileSize, tileSize);
		box.setPosition(model.inputHandler.cursor.x * tileSize, model.inputHandler.cursor.y * tileSize);
		box.draw(batch);
	}

	public void drawAttack()
	{
		white.setColor(Color.RED);
		white.setSize(tileSize, tileSize);
		for (int i = 0; i < model.inputHandler.attackInputReceiver.tiles.size(); i++)
		{
			Tile t = model.inputHandler.attackInputReceiver.tiles.get(i);
			white.setPosition(t.x * tileSize, t.y * tileSize);
			if (t.equals(model.inputHandler.attackInputReceiver.current))
			{
				white.draw(batch, 0.7f);
			}
			else
			{
				white.draw(batch, 0.1f);
			}

			if ((debuggery / 4) % 2 == 1)
			{
				bitmapFont.draw(batch, model.inputHandler.attackInputReceiver.grading[i] + "", t.x * tileSize, t.y * tileSize - 35);
			}
		}
	}

	public void menuHelper(BasicMenu menu)
	{
		if (menu.xSize == 0)
		{
			for (MenuOptions options : menu.options)
			{
				glyphLayout.setText(bitmapFont, options.name);
				menu.xSize = Math.max(menu.xSize, glyphLayout.width);
			}

			menu.xSize += 100;
			menu.ySize = bitmapFont.getLineHeight() * menu.options.size();
		}

		switch (menu.position)
		{
			case 0:
			{
				helper.set(Gdx.graphics.getWidth() - menu.xPadding, menu.yPadding, 0);
				worldSpace.unproject(helper);
				options.setPosition(helper.x - menu.xSize, helper.y);
				break;
			}
			case 1:
			{
				helper.set(menu.xPadding, menu.yPadding, 0);
				worldSpace.unproject(helper);
				options.setPosition(helper.x, helper.y);
				break;
			}
			case 2:
			{
				helper.set(menu.xPadding, Gdx.graphics.getHeight() - menu.yPadding, 0);
				worldSpace.unproject(helper);
				options.setPosition(helper.x, helper.y - menu.ySize);
				break;
			}
			case 3:
			{
				helper.set(Gdx.graphics.getWidth() - menu.xPadding, Gdx.graphics.getHeight() - menu.yPadding, 0);
				worldSpace.unproject(helper);
				options.setPosition(helper.x - menu.xSize, helper.y - menu.ySize);
				break;
			}
		}
	}

	private void drawMenu()
	{
		BasicMenu activeMenu = model.inputHandler.activeMenu;

		menuHelper(activeMenu);
		options.setSize(activeMenu.xSize, bitmapFont.getLineHeight());
		for (int i = 0; i < activeMenu.options.size(); i++)
		{
			if (i == activeMenu.selected)
			{
				activeMenu.pushOptionRight.set(i, activeMenu.pushOptionRight.get(i) * 0.8f + 0.2f);
			}
			else
			{
				activeMenu.pushOptionRight.set(i, activeMenu.pushOptionRight.get(i) * 0.2f);
			}

			options.translateY(-options.getHeight());
			options.draw(batch);
			bitmapFont.draw(batch, activeMenu.options.get(i).name,
				options.getX() + 25 + 50 * activeMenu.pushOptionRight.get(i),
				options.getY() + options.getHeight() * 0.9f);
		}
	}

	private void drawDebug()
	{
		bitmapFont.setColor(1, 1, 1, 0.5f);

		bitmapFont.draw(batch, model.inputHandler.cursor.x + " " + model.inputHandler.cursor.y, 5, 35);
		bitmapFont.draw(batch, model.turnHandler.day + " " + model.turnHandler.turn, 5, 70);
		bitmapFont.draw(batch, model.inputHandler.receivers.get(model.inputHandler.receivers.size() - 1).getClass().getSimpleName(), 5, 105);

		for (Unit unit : model.map.units)
		{
			bitmapFont.draw(batch, unit.instructionsList + "", unit.tile.x * tileSize, unit.tile.y * tileSize);
		}

		bitmapFont.setColor(1, 1, 1, 1);
	}

	public void resize(int width, int height)
	{
		worldSpace.viewportHeight = screenTileHeight * tileSize;
		worldSpace.viewportWidth = (float)width * worldSpace.viewportHeight / (float)height;
		worldSpace.update();

		bitmapFont.getData().setScale(32f / 12f);

		if (model.inputHandler.activeMenu != null)
		{
			model.inputHandler.activeMenu.xSize = 0;
		}
	}
}
