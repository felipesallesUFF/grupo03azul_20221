/*
    Fábrica de Software para Educação
    Professor Lauro Kozovits, D.Sc.
    ProfessorKozovits@gmail.com
    Universidade Federal Fluminense, UFF
    Rio de Janeiro, Brasil
    Subprojeto: Alchemie Zwei

    Partes do software registradas no INPI como integrantes de alguns apps para smartphones
    Copyright @ 2016..2022

    Se você deseja usar partes do presente software em seu projeto, por favor mantenha esse cabeçalho e peça autorização de uso.
    If you wish to use parts of this software in your project, please keep this header and ask for authorization to use.

 */
package br.uff.ic.lek.screens;

import br.uff.ic.lek.game.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;

public class PlayScreen implements Screen {

    @Override
    public void show() {
        //LEKNEW
        //Gdx.input.setInputProcessor(World.world.worldController);
        Gdx.input.setInputProcessor(World.world.inputMultiplexer);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //LEKNEW
        World.world.worldController.update(delta);
        World.world.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        World.world.getCamera().viewportWidth = width;
        World.world.getCamera().viewportHeight = height;
        World.world.getCamera().update();
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub
    }

    @Override
    public void hide() {
        this.dispose();
    }

    @Override
    public void dispose() {
    }

}
