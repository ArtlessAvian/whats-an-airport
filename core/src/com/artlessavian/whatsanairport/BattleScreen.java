package com.artlessavian.whatsanairport;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

class BattleScreen implements Screen
{
	final WarsMain main;
	private final ControlStateSystem controlStateSystem;

	final MapTile[][] map; // x, y
	final int mapHeight = 20;
	final int mapWidth = 30;

	final Texture grid;
	Sprite scrollBox;
	private final TextureRegion[] terrain;

	final Vector3 trueCamPos;
	final OrthographicCamera world;

	public BattleScreen(WarsMain main)
	{
		this.main = main;

		controlStateSystem = new ControlStateSystem(this);

		grid = new Texture("Grid.png");
		scrollBox = new Sprite(grid);
		Texture temp = new Texture("Terrain.png");
		terrain = TextureRegion.split(temp, temp.getHeight(), temp.getHeight())[0];

		// Probably bad practice idk
		// As long as its consistent: [x][y]
		map = new MapTile[mapWidth][mapHeight];

		for (int x = 0; x < mapWidth; x++)
		{
			for (int y = 0; y < mapHeight; y++)
			{
				// This makes a cool thingy
				int tileID = (int)(Math.cos(x * y / 2) * 1.5 + 1.5);

				map[x][y] = new MapTile(x, y, WarsConst.getTerrain(tileID), terrain);
			}
		}

		for (int x = 0; x < mapWidth; x++)
		{
			for (int y = 0; y < mapHeight; y++)
			{
				// lol gross
				try {map[x][y].neighbors.add(map[x + 1][y]);}
				catch (Exception e) {}
				try {map[x][y].neighbors.add(map[x][y + 1]);}
				catch (Exception e) {}
				try {map[x][y].neighbors.add(map[x - 1][y]);}
				catch (Exception e) {}
				try {map[x][y].neighbors.add(map[x][y - 1]);}
				catch (Exception e) {}
			}
		}

		map[3][3].createUnit();
		map[3][5].createUnit();
		map[5][3].createUnit();

		trueCamPos = new Vector3(mapWidth / 2f, mapHeight / 2f, 0);
		world = new OrthographicCamera();
		world.position.x = mapWidth / 2f;
		world.position.y = mapHeight / 2f;
	}

	@Override
	public void show()
	{

	}

	@Override
	public void render(float delta)
	{
		controlStateSystem.update(delta);

		controlStateSystem.state.moveCam();
		world.update();

		main.batch.setProjectionMatrix(world.combined);
		main.batch.begin();

		// Draw Tiles
		for (int x = 0; x < mapWidth; x++)
		{
			for (int y = 0; y < mapHeight; y++)
			{
				map[x][y].draw(main.batch);
			}
		}

		scrollBox.setCenter(world.position.x, world.position.y);
		scrollBox.draw(main.batch, 0.1f);

		controlStateSystem.draw();

		main.batch.end();
	}

	@Override
	public void resize(int width, int height)
	{
		world.viewportHeight = 10;
		world.viewportWidth = (float)width * world.viewportHeight / (float)height;
		world.update();

		scrollBox.setSize(0.6f * world.viewportWidth, 0.6f * world.viewportHeight);
	}

	@Override
	public void pause()
	{

	}

	@Override
	public void resume()
	{

	}

	@Override
	public void hide()
	{

	}

	@Override
	public void dispose()
	{

	}
}
