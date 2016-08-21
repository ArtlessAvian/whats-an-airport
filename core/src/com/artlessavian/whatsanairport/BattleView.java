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
import com.badlogic.gdx.utils.Array;

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

	final Vector3 helper = new Vector3();

	final OrthographicCamera worldSpace;
	final Vector3 trueCamPos;

	float textboxThingy = 0;

	private final float defaultScreenTileHeight = 12;
	float screenTileHeight = 12;
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

		if (worldSpace.viewportWidth >= model.map.width * tileSize)
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

		if (screenTileHeight >= model.map.height)
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

		Class topClass = model.inputHandler.getTop().getClass();
		InputReceiver topObject = model.inputHandler.getTop();

		if (topClass.equals(Cursor.class))
		{
			focus(3, ((Cursor)topObject).x, ((Cursor)topObject).y);
		}
		else if (topObject.equals(AttackInputReceiver.class))
		{
			focus(1, ((AttackInputReceiver)topObject).current.x, ((AttackInputReceiver)topObject).current.y);
		}

		worldSpace.position.lerp(trueCamPos, 0.05f);
		worldSpace.update();
		batch.setProjectionMatrix(worldSpace.combined);

		batch.begin();

		this.drawMap();

		if (topClass.equals(AttackInputReceiver.class))
		{
			this.drawAttack(topObject);
		}

		this.drawUnits();
		if (topClass.equals(Cursor.class))
		{
			this.drawPath(topObject);
		}

		this.drawHighlight();

		if (topClass.equals(Cursor.class))
		{
			this.drawCursor(topObject);
		}

		if (topClass.getSuperclass().equals(BasicMenu.class))
		{
			this.drawMenu(topObject);
		}

		this.drawTextbox(topClass.equals(Textbox.class), topObject);

		if (topClass.equals(NewDayShower.class))
		{
			this.drawNewDay(topObject);
		}

		if (debuggery % 2 == 1)
		{
			this.drawDebug(topObject);
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
						white.setSize(tileSize, tileSize);
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
					white.setSize(tileSize, tileSize);
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

			bitmapFont.setColor(1, unit.health / 20f + 0.5f, unit.health / 20f + 0.5f, 1);
			bitmapFont.draw(batch, unit.health + "", unitTileSet.getX(), unitTileSet.getY() + 35);

		}
	}

	private void drawPath(InputReceiver topObject)
	{
		if (((Cursor)topObject).selectedUnit != null)
		{
			path.setPosition(((Cursor)topObject).selectedUnit.tile.x * tileSize, ((Cursor)topObject).selectedUnit.tile.y * tileSize);
			if (!((Cursor)topObject).instructions.isEmpty())
			{
				Iterator<UnitInstruction> iter = ((Cursor)topObject).instructions.iterator();
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

	private void drawCursor(InputReceiver topObject)
	{
		box.setSize(tileSize, tileSize);
		box.setPosition(((Cursor)topObject).x * tileSize, ((Cursor)topObject).y * tileSize);
		box.draw(batch);
	}

	public void drawAttack(InputReceiver topObject)
	{
		white.setColor(Color.RED);
		white.setSize(tileSize, tileSize);
		for (int i = 0; i < ((AttackInputReceiver)topObject).tiles.size(); i++)
		{
			Tile t = ((AttackInputReceiver)topObject).tiles.get(i);
			white.setPosition(t.x * tileSize, t.y * tileSize);
			if (t.equals(((AttackInputReceiver)topObject).current))
			{
				white.draw(batch, 0.7f);
			}
			else
			{
				white.draw(batch, 0.1f);
			}

			if ((debuggery / 4) % 2 == 1)
			{
				bitmapFont.draw(batch, ((AttackInputReceiver)topObject).grading[i] + "", t.x * tileSize, t.y * tileSize - 35);
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

	private void drawMenu(InputReceiver topObject)
	{
		bitmapFont.setColor(1,1,1,1);

		BasicMenu activeMenu = (BasicMenu)topObject;

		menuHelper(activeMenu);
		options.setSize(activeMenu.xSize, bitmapFont.getLineHeight());
		for (int i = 0; i < activeMenu.options.size(); i++)
		{
			if (i == activeMenu.selected)
			{
				float temp = activeMenu.pushOptionRight.get(i);
				temp = temp * 0.8f + 0.2f;
				temp += 0.01;
				if (temp > 1) {temp = 1;}
				activeMenu.pushOptionRight.set(i, temp);
			}
			else
			{
				float temp = activeMenu.pushOptionRight.get(i);
				temp *= 0.2;
				temp -= 0.01;
				if (temp < 0) {temp = 0;}
				activeMenu.pushOptionRight.set(i, temp);
			}

			options.translateY(-options.getHeight());
			options.draw(batch);
			bitmapFont.draw(batch, activeMenu.options.get(i).name,
				options.getX() + 25 + 50 * activeMenu.pushOptionRight.get(i),
				options.getY() + options.getHeight() * 0.9f);
		}
	}

	private void drawTextbox(boolean open, InputReceiver topObject)
	{
//		if (open)
//		{
//			textboxThingy = textboxThingy * 0.8f + 0.2f;
//			textboxThingy += 0.01;
//			if (textboxThingy > 1) {textboxThingy = 1;}
//		}
//		else
//		{
//			textboxThingy = textboxThingy * 0.8f;
//			textboxThingy -= 0.01;
//			if (textboxThingy < 0) {textboxThingy = 0;}
//		}
//		float top = bitmapFont.getLineHeight() * 3f * textboxThingy;
//
//		white.setColor(0.3f,0.3f,0.3f,1);
//
//		white.setSize(worldSpace.viewportWidth, bitmapFont.getLineHeight()*3);
//		white.setCenterX(worldSpace.position.x);
//		white.setY(worldSpace.position.y - worldSpace.viewportHeight/2f + top - white.getHeight());
//		white.draw(batch);
//
//		Textbox t = (Textbox)model.inputHandler.getState(Textbox.class);
//
//		bitmapFont.draw(batch, t.contents[t.line][t.thingy], 0, white.getY() + bitmapFont.getLineHeight() * 1.5f);
//		try
//		{
//			bitmapFont.draw(batch, t.contents[t.line][t.thingy - 1], 0, white.getY() + bitmapFont.getLineHeight() * 2.5f);
//		}
//		catch (ArrayIndexOutOfBoundsException e)
//		{}
	}

	private float easingFunction(float t, float b, float c, float d)
	{
		float ts = (t /= d) * t;
		float tc = ts * t;
		return b + c * (4 * tc + -6 * ts + 3 * t);
	}

	private void drawNewDay(InputReceiver topObject)
	{
		float x = easingFunction(((NewDayShower)topObject).time, 0, 1, ((NewDayShower)topObject).framesOpen);
		float y = worldSpace.position.y;

		bitmapFont.setColor(1, 1, 1, 4 * (-x * x + x));
		white.setColor(0.7f, 0.7f, 0.7f, 4 * (-x * x + x));

		x *= worldSpace.viewportWidth;
		x += worldSpace.position.x - worldSpace.viewportWidth / 2f;

		white.setSize(worldSpace.viewportWidth, bitmapFont.getLineHeight() * 2);
		white.setCenter(worldSpace.position.x, worldSpace.position.y);
		white.draw(batch);

		glyphLayout.setText(bitmapFont, "Player " + model.turnHandler.turn + " Go!");
		bitmapFont.draw(batch, "Player " + model.turnHandler.turn + " Go!", x - glyphLayout.width / 2f, y + bitmapFont.getLineHeight());
		glyphLayout.setText(bitmapFont, "Day " + model.turnHandler.day);
		bitmapFont.draw(batch, "Day " + model.turnHandler.day, x - glyphLayout.width / 2f, y);
	}

	private void drawDebug(InputReceiver topObject)
	{
		bitmapFont.setColor(1, 1, 1, 0.5f);

		bitmapFont.draw(batch, ((Cursor)model.inputHandler.getState(Cursor.class)).x + " " + ((Cursor)model.inputHandler.getState(Cursor.class)).y, 5, 35);
		bitmapFont.draw(batch, model.turnHandler.day + " " + model.turnHandler.turn, 5, 70);

		for (int i = 0; i < model.inputHandler.receiversClass.size(); i++)
		{
			bitmapFont.draw(batch, model.inputHandler.receiversClass.get(i).getSimpleName(), 5, 105 + 15 * i);
		}

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
		BasicMenu.xSize = 0;
	}
}
