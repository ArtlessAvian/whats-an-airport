package com.artlessavian.whatsanairport;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PropertyTile extends MapTile
{
	public int capProgress = 20;
	public int owner = -1;

	public Sprite property;

	public PropertyTile(int x, int y, WarsConst.TerrainType tileType, Texture tiles)
	{
		super(x, y, tileType, tiles);

		debugSpaz = true;
		isCapturable = true;
	}

	@Override
	public void draw(SpriteBatch batch)
	{
		super.draw(batch);
		// Draw the property i guess?
	}
}
