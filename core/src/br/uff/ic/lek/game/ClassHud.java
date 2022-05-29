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

/*
O nome HUD ou Head-up Display vem dos painéis de aviação onde a tarefa
 de pilotar ou jogar se desenrola e o painel de comandos é projetado sobre visor.
 É importante ter em mente que o sistema de input é multiplexado
 (veja World.world.inputMultiplexer), para que os comandos
 para os botões não se misturem aos comandos para o jogo. Cada um está num sistema
  de coordenadas diferente.
 */
package br.uff.ic.lek.game;

/**
 * Created by Prof. Lauro Eduardo Kozovits, TCC, UFF. Projeto Memória Periódica on 19/09/2016.
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
//import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;



public class ClassHud implements InputProcessor, Disposable{

    //Scene2D.ui Stage and its own Viewport for HUD
    //Group group;
    public static Stage stage;

    //Mario score/time Tracking Variables
    private Integer worldTimer;
    private boolean timeUp; // true when the world timer reaches 0
    private float timeCount;
    private static Integer score;

    public Table tableGameOver;
    private final Table table;

    /* LEK
    public void ajustTableGameOver(int vitorioso){
        if (ClassGame.gameState == ClassGame.GAME_OVER){
            table.setVisible(false);
            tableGameOver.setVisible(true);
            if (vitorioso == ClassGame.PLAYER_MY_OPPONENT){
                ClassHud.btGAME_OVER.myselfface = false;
            } else {
                ClassHud.btGAME_OVER.myselfface = true;
            }
        } else { // em qualquer outro estado
            table.setVisible(true);
            tableGameOver.setVisible(false);
        }

    }
    // LEK */


    //public static float myPoints=18456.89f;
    //public static float robotPoints=13456.89f;
    //public static float multiplicador=1.0f;
    public static String jogador1="Quem Joga?\n-Você";
    public static String jogador2="Quem Joga?\n-Oponente";
    public static String jogador=jogador1;
    private Label labelBranco1;
    private Label labelBranco2;
    private Label labelBranco3;
    private Label labelBranco4;
    private Label labelCardsToGo;
    private Label jogadaCorrente;
    private Label meusPontosLabel;
    private Label meusPontosNumLabel;
    private Label oponentePontosLabel;
    private Label oponentePontosNumLabel;
    private Label multiplicadorLabel;
    //Scene2D widgets
    private Label countdownLabel;
    private static Label scoreLabel;
    private Label timeLabel;
    private Label levelLabel;
    private Label worldLabel;
    private Label marioLabel;
    private Label voceOutro;
    public static ClassCommandButton btPLAY;
    public static ClassCommandButton btRESIZE;
    public static ClassCommandButton btINTERNET;
    public static ClassCommandButton btSOUND;
    public static ClassCommandButton btHELP;
    public static ClassCommandButton btINFO;
    public static ClassCommandButton btEXIT;

    // LEK public static ClassGameOverButton btGAME_OVER;

    // LEK public ClassDisplayText displayText;

    public BitmapFont fontSmall; //www.1001fonts.com
    public BitmapFont fontMedium; //www.1001fonts.com
    public BitmapFont fontLarge; //www.1001fonts.com


    private NinePatch getNinePatch(String fname) {

        // Get the image
        final Texture t = new Texture(Gdx.files.internal(fname));

        // create a new texture region, otherwise black pixels will show up too, we are simply cropping the image
        // last 4 numbers respresent the length of how much each corner can draw,
        // for example if your image is 50px and you set the numbers 50, your whole image will be drawn in each corner
        // so what number should be good?, well a little less than half would be nice
        return new NinePatch( new TextureRegion(t), 16, 16, 16, 16);
    }

    public ClassHud(SpriteBatch sb){
        //define our tracking variables
        worldTimer = 300;
        timeCount = 0;
        score = 0;

        int V_WIDTH = Gdx.graphics.getWidth();
        int V_HEIGHT = Gdx.graphics.getHeight();


        final Viewport viewport;
        //setup the HUD viewport using a new camera seperate from our gamecam
        //define our stage using that viewport and our games spritebatch
        //viewport = new FillViewport(V_WIDTH, V_HEIGHT, new OrthographicCamera());
        viewport = new ExtendViewport(V_WIDTH, V_HEIGHT, new OrthographicCamera());

        //viewport = new FitViewport(MarioBros.V_WIDTH, MarioBros.V_HEIGHT, new OrthographicCamera());
        ClassHud.stage = new Stage(viewport, sb);

        //define a table used to organize our hud's labels
        this.table = new Table();
        //Top-Align table
        this.table.right();
        //make the table fill the entire stage
        this.table.setFillParent(true);
        //this.table.debug();


        // define our labels using the String, and a Label style consisting of a font and color
        // Jogada: sua vez (fundo verde) ou vez do robot (fundo azul) e toca som
        // cardsToGo:   XXXX  // Multiplicador: XXX
        // Seus Pontos: XXX     Pontos do Robot: XXXX
        // TODO animacao do seu tempo
        // TODO animacao com o crescimento dos pontos e good, great, excelent  e SONS

        // LEK displayText = new ClassDisplayText();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("LiberationSans-Regular.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        float correcao =Gdx.graphics.getDensity(); // Gdx.graphics.getDensity(); //Gdx.graphics.getWidth()/1920.0f; //Gdx.graphics.getDensity()*
        parameter.characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-+()[]_^";
        //e.g. abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.!'()>?:
        // These characters should not repeat!
        parameter.size = (int)(16f * correcao); //size_of_the_font_in_pixels;//TODO o font está ficando com tamanhos diferentes em cada micro
        fontSmall = generator.generateFont(parameter);
        parameter.size = (int)(32f * correcao); //size_of_the_font_in_pixels;//TODO o font está ficando com tamanhos diferentes em cada micro
        fontMedium = generator.generateFont(parameter);
        parameter.size = (int)(64f * correcao); //size_of_the_font_in_pixels;//TODO o font está ficando com tamanhos diferentes em cada micro
        fontLarge = generator.generateFont(parameter);
        generator.dispose();

        /*
        SmartFontGenerator fontGen = new SmartFontGenerator();
        FileHandle exoFile = Gdx.files.internal("CartoonRelief.ttf"); // LiberationMono-Regular.ttf
        BitmapFont fontSmall = fontGen.createFont(exoFile, "exo-small", 60);//24);

        BitmapFont fontMedium = fontGen.createFont(exoFile, "exo-medium", (int) (60f * correcao)); //14
        BitmapFont fontLarge = fontGen.createFont(exoFile, "exo-large", (int) (60f * correcao)); //16
        */

        Label.LabelStyle labelStyle =  new Label.LabelStyle(fontSmall,Color.BLACK); // new Label.LabelStyle(new BitmapFont(), Color.WHITE);  //  new Label.LabelStyle(font,Color.BLACK);
        Label.LabelStyle labelStyleLarge =  new Label.LabelStyle(fontMedium,Color.BLACK); // new Label.LabelStyle(new BitmapFont(), Color.WHITE);  //  new Label.LabelStyle(font,Color.BLACK);
        //label.setPosition(0,0);
        //label.setScale(100,100);

        //countdownLabel = new Label(String.format("%03d", worldTimer), labelStyle);
        //countdownLabel.setScale(50,50);
        //scoreLabel =new Label(String.format("%06d", score), labelStyle);

        /*
        labelBranco1 = new Label(" ", labelStyleLarge);
        labelBranco2 = new Label(" ", labelStyleLarge);
        labelBranco3 = new Label(" ", labelStyleLarge);
        labelBranco4 = new Label(" ", labelStyleLarge);
        labelCardsToGo = new Label(String.format("abrir %3d carta(s)", 2), labelStyle);// TODO localize
        jogadaCorrente = new Label(jogador, labelStyleLarge);
        meusPontosLabel = new Label("Seus Pontos:", labelStyleLarge);
        meusPontosNumLabel = new Label(String.format("%6.2f", myPoints), labelStyle); // TODO localize
        oponentePontosLabel = new Label("Oponente:", labelStyleLarge);
        oponentePontosNumLabel = new Label(String.format("%6.2f", robotPoints), labelStyle); // TODO localize
        multiplicadorLabel = new Label(String.format("valendo %3.1f X", multiplicador), labelStyle); // TODO localize
        */
        //levelLabel = new Label("1-1", labelStyle);
        //worldLabel = new Label("  PONTOS  ", labelStyle);
        //marioLabel = new Label("  VEZ  ", labelStyle);
        //marioLabel.setSize(80, 80);
        //marioLabel.setStyle();


        final Table tableLeft;
        final Table tableRight;
        tableLeft = new Table();



        tableRight = new Table();
        //tableRight.debug();
        //tableRight.setBackground(new NinePatchDrawable(getNinePatch(("white.png"))));
        //table.setX(V_WIDTH);
        //table.setY(V_HEIGHT);



        TextureAtlas atlas;
        atlas = new TextureAtlas(Gdx.files.internal("menu.pack"));


        // botao GAME OVER

        this.tableGameOver = new Table();
        //make the table fill the entire stage
        this.tableGameOver.setFillParent(true);
        this.tableGameOver.center();
        this.tableGameOver.setVisible(true);
        Sprite won = atlas.createSprite("GameOverWinner");
        Sprite lost = atlas.createSprite("GameOverLoser");
        // LEK ClassHud.btGAME_OVER  = new ClassGameOverButton(won, lost); // myself & opponent
        // LEK this.tableGameOver.add(ClassHud.btGAME_OVER).expandX().center();




        // botao liga desliga som
        Sprite frente = atlas.createSprite("roundAplay");
        Sprite fundo = atlas.createSprite("roundApause");
        ClassHud.btPLAY  = new ClassCommandButton(frente, fundo, ClassCommandButton.BT_PLAY);
        tableRight.add(ClassHud.btPLAY).expandX().top().right();


        // botao help TODO dialog deseja assistir video tutorial no youtube? chama video no youtube ou mostra displayText
        tableRight.row();
        frente = atlas.createSprite("roundAhelp");
        fundo = null;
        ClassHud.btHELP  = new ClassCommandButton(frente, fundo, ClassCommandButton.BT_HELP);
        tableRight.add(ClassHud.btHELP).expandX().top().right();

        // botao resize and pos
        tableRight.row();
        Sprite frente1 = atlas.createSprite("roundAresize2");
        Sprite fundo1 =  atlas.createSprite("roundAresize1");
        ClassHud.btRESIZE  = new ClassCommandButton(frente1, fundo1, ClassCommandButton.BT_RESIZE);
        tableRight.add(ClassHud.btRESIZE).expandX().top().right();


        // botao find Internet
        tableRight.row();
        frente = atlas.createSprite("roundAglassZoom");
        fundo = null;
        ClassHud.btINTERNET  = new ClassCommandButton(frente, fundo, ClassCommandButton.BT_INTERNET_SEARCH);
        tableRight.add(ClassHud.btINTERNET).expandX().top().right();

        // botao sound
        tableRight.row();
        frente = atlas.createSprite("roundAsoundON");
        fundo = atlas.createSprite("roundAsoundOFF");
        ClassHud.btSOUND = new ClassCommandButton(frente, fundo, ClassCommandButton.BT_SOUND);
        tableRight.add(ClassHud.btSOUND).expandX().top().right();


        // botao about; ultimo botao info equipe de desenvolvimento
        //* LEK DEBUG retirei botão INFO para gerar o SHA 512 ANdroid Studio Arctic Fox 2020.3.1 patch 3
        tableRight.row();
        frente = atlas.createSprite("roundAinfo");
        fundo = null;
        ClassHud.btINFO  = new ClassCommandButton( frente, fundo, ClassCommandButton.BT_INFO);
        tableRight.add(ClassHud.btINFO).expandX().top().right();
        //*/


        // botao about; ultimo botao info equipe de desenvolvimento
        tableRight.row();
        frente = atlas.createSprite("roundByeAndroid");
        fundo = null;
        ClassHud.btEXIT  = new ClassCommandButton(frente, fundo, ClassCommandButton.BT_EXIT);
        tableRight.add(ClassHud.btEXIT).expandX().top().right();

        table.add(tableLeft).right();
        table.add(tableRight).right();






        //add our table to the stage
        //table.setVisible(false);
        ClassHud.stage.addActor(this.table);
        this.tableGameOver.setVisible(false);
        ClassHud.stage.addActor(this.tableGameOver);
        // LEK ClassHud.stage.addActor(displayText);


    }

    public void update(float dt){
        timeCount += dt;
        if(timeCount >= 1){
            if (worldTimer > 0) {
                worldTimer--;
            } else {
                timeUp = true;
            }
            countdownLabel.setText(String.format("%03d", worldTimer));
            timeCount = 0;
        }
    }

    public static void addScore(int value){
        score += value;
        scoreLabel.setText(String.format("%06d", score));
    }

    @Override
    public void dispose() {
        fontSmall.dispose();
        fontMedium.dispose();
        fontLarge.dispose();
        ClassHud.stage.dispose();
    }


    public boolean isTimeUp() { return timeUp; }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}

