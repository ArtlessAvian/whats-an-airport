package com.artlessavian.whatsanairport;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;


public class BattleScreen implements Screen
{
	public final WarsMain main;

	// Literal Singleton
	public static BattleScreen instance = null;

	public void setInstance()
	{
		instance = this;
	}
	public static BattleScreen getInstance()
	{
		return instance;
	}

	private final ControlStateSystem controlStateSystem;

	public final Map map; // x, y

	private int day = 0;
	public int turn = 0;
	private final String[] turnToTeam = {"Red", "Blue"};

	public DayAndCoHandler dayAndCoHandler;

	public final Texture grid;
	public final Texture white;

	public int screenTileHeight = 10;
	public final Vector3 trueCamPos;
	public final Vector3 camVelocity;
	public final OrthographicCamera worldSpace;
	public float screenWorldScale;

	public float magicNumba;
	
	public BattleScreen(WarsMain main)
	{
		this.main = main;
		this.setInstance();

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

		dayAndCoHandler = new DayAndCoHandler();

		// Game Stuff
		controlStateSystem = new ControlStateSystem();

		map = new Map(20,15);

		map.debugGeneration(terrain);
		map.establishNeighbors();

		map.map[3][3].createUnit("Blue/Soldier");
		map.map[3][5].createUnit("Red/Soldier");
		map.map[5][3].createUnit("Red/Soldier");

		map.map[0][0].debugMakeObvious = true;

		worldSpace = new OrthographicCamera();
		//screenTileHeight = (int)(Gdx.graphics.getHeight()/Gdx.graphics.getPpcX()/1f);

		// TODO: Temporary stuff
		trueCamPos = new Vector3(15.5f, 10.5f, 0);
		worldSpace.position.x = 15.5f;
		worldSpace.position.y = 10.5f;
		camVelocity = new Vector3(0, 0, 0);
		worldSpace.position.x = 0;
		worldSpace.position.y = 0;
	}

	@Override
	public void show()
	{

	}

	@Override
	public void render(float delta)
	{
//		try
//		{
//			Thread.sleep(100);
//		}
//		catch (Exception e)
//		{
//
//		}

		// TODO: Temporary Stuff
		//if (Gdx.graphics.getFrameId() % 60 == 0)
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

		if (RNGInputSpammer.doRNGTesting)
		{
			RNGInputSpammer.doTheThing(controlStateSystem);
		}
		else
		{
			controlStateSystem.update(delta);

			worldSpace.position.lerp(trueCamPos, 0.3f);
			worldSpace.update();
		}

		main.batch.setProjectionMatrix(worldSpace.combined);
		main.batch.begin();

		// Draw Tiles, then Units
		map.draw(main.batch);

		// Draw stuff
		controlStateSystem.draw();

		main.batch.setProjectionMatrix(main.screenSpace.combined);
		main.font.setColor(Color.WHITE);
		main.font.draw(main.batch, controlStateSystem.state.getClass().getSimpleName(), 0, main.screenSpace.viewportHeight);
		main.font.draw(main.batch, magicNumba + " cm/tile", 0, main.screenSpace.viewportHeight - main.font.getLineHeight());
		main.font.draw(main.batch, Math.ceil(trueCamPos.x * 10000)/10000f + "", 0, main.screenSpace.viewportHeight - 2 * main.font.getLineHeight());
		main.font.draw(main.batch, Math.ceil(trueCamPos.y * 10000)/10000f + "", 0, main.screenSpace.viewportHeight - 3 * main.font.getLineHeight());
		main.font.draw(main.batch, Math.ceil(controlStateSystem.touchTime * 100)/100f + "", 0, main.screenSpace.viewportHeight - 4 * main.font.getLineHeight());

		main.batch.end();
	}

	@Override
	public void resize(int width, int height)
	{
		worldSpace.viewportHeight = screenTileHeight;
		worldSpace.viewportWidth = (float)width * worldSpace.viewportHeight / (float)height;
		worldSpace.update();

		screenWorldScale = height / screenTileHeight;

		magicNumba = Math.round(10000 * height/screenTileHeight/Gdx.graphics.getPpcY()) / 10000f;

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
