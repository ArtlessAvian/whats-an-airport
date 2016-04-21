package com.artlessavian.whatsanairport;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;


public class BattleScreen implements Screen
{
	public final WarsMain main; // Or i could do Gdx.app.getApplicationListener()
	private final ControlStateSystem controlStateSystem;

	public static BattleScreen instance = null;

	public void setAsInstance()
	{
		instance = this;
	}

	public static BattleScreen getInstance()
	{
		return instance;
	}

	public final Map map; // x, y
	public final int mapHeight = 20;
	public final int mapWidth = 30;

	public int screenTileHeight = 10;

	public final Texture grid;
	public final Texture white;

	public final Vector3 trueCamPos;
	public final OrthographicCamera world;
	
	public BattleScreen(WarsMain main)
	{
		this.main = main;

		this.setAsInstance();

		// Get Assets
		Texture terrain = main.assetManager.get("Terrain.png", Texture.class);

		grid = main.assetManager.get("Grid.png", Texture.class);
		white = main.assetManager.get("White.png", Texture.class);

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

		// Game Stuff
		controlStateSystem = new ControlStateSystem();

		// Probably bad practice idk
		// As long as its consistent: [x][y]
		map = new Map(mapWidth, mapHeight);

		map.debugGeneration(terrain);
		map.establishNeighbors();

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
		world.position.lerp(trueCamPos, 0.3f);

		world.update();

		main.batch.setProjectionMatrix(world.combined);
		main.batch.begin();

		// Draw Tiles, then Units
		map.draw(main.batch);

		// Draw stuff
		controlStateSystem.draw();

		main.batch.end();
	}

	@Override
	public void resize(int width, int height)
	{
		world.viewportHeight = screenTileHeight;
		world.viewportWidth = (float)width * world.viewportHeight / (float)height;
		world.update();

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
