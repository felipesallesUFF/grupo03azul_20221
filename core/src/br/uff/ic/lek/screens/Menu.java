package br.uff.ic.lek.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import br.uff.ic.lek.game.World;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;


import br.uff.ic.lek.Alquimia;

public class Menu implements Screen {

    final Alquimia game;
    OrthographicCamera camera;
    int pilar = 1;
    private Texture texture = new Texture(Gdx.files.internal("img/guerreira3.png"));
    private Image splashImage = new Image(texture);
    private Stage stage = new Stage();

    public Menu(final Alquimia gam) {
        game = gam;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        this.stage.draw();
        game.font.draw(game.batch, "Welcome to Drop!!! ", 100, 150);
        if(pilar == 0){
            game.font.draw(game.batch, "helo world!!! ", 100, 50);
        }
        game.font.draw(game.batch, "Tap anywhere to begin!", 100, 100);
        game.batch.end();

        if (Gdx.input.isTouched()) {
            //game.setScreen(new SplashScreen());
            pilar = 0;
            //dispose();
        }
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            pilar = 1;
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        this.splashImage.setWidth(Gdx.graphics.getWidth());
        this.splashImage.setHeight(Gdx.graphics.getHeight());
        this.stage.addActor(splashImage);
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}
