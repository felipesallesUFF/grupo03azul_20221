package br.uff.ic.lek.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;

import br.uff.ic.lek.PlayerData;
import com.badlogic.gdx.Game;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.Input.TextInputListener;


import br.uff.ic.lek.Alquimia;

//essa clase é o menu do jogo
public class Menu implements Screen {

    private Alquimia parent;
    private SpriteBatch batch;
    protected Stage stage;
    private Viewport viewport;
    private OrthographicCamera camera;
    private TextureAtlas atlas;
    protected Skin skin;
    //estou pegando os dados do player
    public String nickname = PlayerData.myPlayerData().getPlayerNickName();
    public String email = PlayerData.myPlayerData().getEmail();
    public TextInputListener nick;
    public TextInputListener Email;

    public Menu(Alquimia alquimia)
    {
        parent = alquimia;
        //
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2, camera);
        viewport.apply();

        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();

        stage = new Stage(viewport, batch);
        //Stage should controll input:
        Gdx.input.setInputProcessor(stage);
    }


    @Override
    public void show() {
        //para organizar os elementos(nesse caso os botões)
        //Create Table
        Table mainTable = new Table();
        //Set table to fill stage
        mainTable.setFillParent(true);
        //Set alignment of contents in the table.
        mainTable.top();

        //Picking the texture
        atlas = new TextureAtlas("skin/glassy-ui.atlas");
        skin = new Skin(Gdx.files.internal("skin/glassy-ui.json"), atlas);

        //Create buttons
        TextButton playButton = new TextButton("Play", skin);
        TextButton NicknameButton = new TextButton("MudarNickname", skin);
        TextButton EmailButton = new TextButton("MudarEmail", skin);
        TextButton exitButton = new TextButton("Exit", skin);

        //Add listeners to buttons
        playButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game)Gdx.app.getApplicationListener()).setScreen(new SplashScreen());//começar jogo
            }
        });
        NicknameButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Gdx.input.getTextInput( nick, "Nickname", nickname, nickname);
                nick = new TextInputListener()
                {
                    @Override
                    public void input(String input)//o que ele vai fazer com o texto digitado
                    {
                        nickname = input;
                        PlayerData.myPlayerData().setPlayerNickName(nickname);
                    }

                    @Override
                    public void canceled()//o que ele faz se vc cancelar
                    {
                    }
                };
                Gdx.input.getTextInput( nick, "Nickname", nickname, nickname);
                Gdx.input.setOnscreenKeyboardVisible(true);//subir o teclado no celular
            }
        });
        EmailButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Gdx.input.getTextInput( Email, "Email", email, email);
                Email = new TextInputListener()
                {
                    @Override
                    public void input(String input)//o que ele vai fazer com o texto digitado
                    {
                        email = input;
                        PlayerData.myPlayerData().setEmail(email);
                    }

                    @Override
                    public void canceled()//o que ele faz se vc cancelar
                    {
                    }
                };
                Gdx.input.getTextInput( Email, "Email", email, email);
                Gdx.input.setOnscreenKeyboardVisible(true);//subir o teclado no celular
            }
        });
        exitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();//sair do jogo
            }
        });

        //Add buttons to table
        mainTable.add(playButton);
        mainTable.row();
        mainTable.add(NicknameButton);
        mainTable.row();
        mainTable.add(EmailButton);
        mainTable.row();
        mainTable.add(exitButton);

        //Add table to stage
        stage.addActor(mainTable);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor( 0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        skin.dispose();
        atlas.dispose();
    }
}
