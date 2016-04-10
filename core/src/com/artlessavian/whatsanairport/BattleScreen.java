package com.artlessavian.whatsanairport;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;


class BattleScreen implements Screen
{
	final WarsMain main; // Or i could do Gdx.app.getApplicationListener()
	private final ControlStateSystem controlStateSystem;

	final Map map; // x, y
	final int mapHeight = 20;
	final int mapWidth = 30;

	final Texture grid;
	final Texture white;
	Sprite scrollBox;

	final Vector3 trueCamPos;
	final OrthographicCamera world;

	public BattleScreen(WarsMain main)
	{
		this.main = main;

		controlStateSystem = new ControlStateSystem(this);

		grid = main.assetManager.get("Grid.png", Texture.class);
		white = main.assetManager.get("White.png", Texture.class);
		scrollBox = new Sprite(grid);

		Texture terrain = main.assetManager.get("Terrain.png", Texture.class);

		// I can probably figure out how to do palette swaps, but nah
		String[] teams = {"Red", "Blue"};
		String[] types = {"Soldier"};

		for (String team : teams)
		{
			for (String type : types)
			{
				String combined = team + "/" + type;
				if (!Unit.textures.containsKey(combined))
				{
					Unit.textures.put(combined, main.assetManager.get("Units/" + combined + ".png", Texture.class));
				}
			}
		}

		// Probably bad practice idk
		// As long as its consistent: [x][y]
		map = new Map(mapWidth, mapHeight);

		for (int x = 0; x < mapWidth; x++)
		{
			for (int y = 0; y < mapHeight; y++)
			{
				// This makes a cool thingy
				int tileID = (int)(Math.cos(x * y / 2) * 1.5 + 1.5);

				map.map[x][y] = new MapTile(this, x, y, WarsConst.getTerrain(tileID), terrain);
			}
		}

		for (int x = 0; x < mapWidth; x++)
		{
			for (int y = 0; y < mapHeight; y++)
			{
				// lol gross
				try
				{
					map.map[x][y].neighbors.put(map.map[x + 1][y], WarsConst.CardinalDir.RIGHT);
				}
				catch (Exception e) {}
				try
				{
					map.map[x][y].neighbors.put(map.map[x - 1][y], WarsConst.CardinalDir.LEFT);
				}
				catch (Exception e) {}
				try
				{
					map.map[x][y].neighbors.put(map.map[x][y + 1], WarsConst.CardinalDir.UP);
				}
				catch (Exception e) {}
				try
				{
					map.map[x][y].neighbors.put(map.map[x][y - 1], WarsConst.CardinalDir.DOWN);
				}
				catch (Exception e) {}
			}
		}

		map.map[3][3].createUnit("Blue/Soldier");
		map.map[3][5].createUnit("Red/Soldier");
		map.map[5][3].createUnit("Red/Soldier");

		map.map[0][0].debug = true;

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
		if (Gdx.graphics.getFrameId() % 60 == 0)
		{
			if (map.map[1][1].unit == null)
			{
				map.map[1][1].createUnit("Red/Soldier");
			}
			if (map.map[3][1].unit == null)
			{
				map.map[3][1].createUnit("Blue/Soldier");
			}
		}

		controlStateSystem.update(delta);

		world.update();

		main.batch.setProjectionMatrix(world.combined);
		main.batch.begin();

		// Draw Tiles
		map.draw(main.batch);

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

		for (int i = 0; i < 30; i++)
		{
			controlStateSystem.state.moveCam();
		}
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
