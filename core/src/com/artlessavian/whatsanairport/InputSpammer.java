package com.artlessavian.whatsanairport;

import com.badlogic.gdx.Input;

public class InputSpammer
{
	private BattleModel model;
	private InputHandler handler;

	public InputSpammer(BattleModel model, InputHandler inputHandler)
	{
		this.model = model;
		this.handler = inputHandler;
	}

	public void run(int max)
	{
		for (int yey = 0; yey < max; yey++)
		{
			double random = Math.random();

			if (random < 0.2)
			{
				handler.up();
			}
			else if (random < 0.4)
			{
				handler.down();
			}
			else if (random < 0.6)
			{
				handler.left();
			}
			else if (random < 0.8)
			{
				handler.right();
			}
			else if (random < 0.9)
			{
				handler.keyDown(Input.Keys.J);
				handler.keyUp(Input.Keys.J);
			}
			else
			{
				handler.keyDown(Input.Keys.N);
				handler.keyUp(Input.Keys.N);
			}

			for (int frames = (int)(Math.random() * 1); frames >= 0; frames--)
			{
				model.map.update();
			}
		}
	}
}
