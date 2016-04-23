package com.artlessavian.whatsanairport.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.artlessavian.whatsanairport.WarsMain;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 960;
		config.height = 540;
		config.title = "What's an airport, again?";
		config.x = -1;
		config.y = -1;
		new LwjglApplication(new WarsMain(), config);
	}
}
