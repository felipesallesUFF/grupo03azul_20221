package br.uff.ic.lek.pathfinder;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.InputAdapter;
//LEK import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
//LEK import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

/** Base class for tests.
 * 
 * @author davebaol */
public abstract class GdxAiTest extends InputAdapter implements ApplicationListener {
	public void create () {
	}

	public void resume () {
	}

	public void render () {
	}

	public void resize (int width, int height) {
	}

	public void pause () {
	}

	public void dispose () {
	}

	public static void launch (ApplicationListener test) {
/* LEK
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.r = config.g = config.b = config.a = 8;
		config.width = 960;
		config.height = 600;
		new LwjglApplication(test, config);
 */
	}
}