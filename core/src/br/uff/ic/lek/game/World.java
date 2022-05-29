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
package br.uff.ic.lek.game;

import java.util.ArrayList;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Elastic;
import aurelienribon.tweenengine.equations.Expo;
import aurelienribon.tweenengine.equations.Linear;

import br.uff.ic.lek.PlayerData;
import br.uff.ic.lek.actors.Avatar;
//import br.uff.ic.lek.actors.PlayerLife;
import br.uff.ic.lek.utils.CameraZoomAdjust;
import br.uff.ic.lek.utils.ClassToast;
import br.uff.ic.lek.utils.PathPlanning;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;


// see https://libgdx.com/dev/tools/


public class World {

    //----------------------------------
    public static World world=null;
    private static ClassHud hud;
    public static OrthographicCamera camera;
    public static Stage stageCG;
    public static TweenManager tweenManager;
    public SpriteBatch batch;

    //LEKNEW
    private CameraController controller;
    private GestureDetector gestureDetector;
    //LEKNEW

    //public static WorldRenderer worldRenderer;
    public WorldController worldController;
    public InputMultiplexer inputMultiplexer; // LEKNEW
    public static int avatarStartTileX;
    public static int avatarStartTileY;

    public static void load() {
        if (world==null) // singleton, just one World instance
            world = new World(); // private constructor

        World.tweenManager = new TweenManager(); // para animações
        //LEKNEW
        world.worldController = new WorldController(World.world);


        world.batch = new SpriteBatch();
        World.hud = new ClassHud(world.batch);
        world.controller = new CameraController();
        world.gestureDetector = new GestureDetector(20, 0.5f, 2, 0.15f, world.controller);
        world.inputMultiplexer = new InputMultiplexer(); // Order matters here!
        world.inputMultiplexer.addProcessor(World.hud.stage); // 1st priority
        //im.addProcessor(ClassGame.stageCG);
        world.inputMultiplexer.addProcessor(world.worldController); // LEKNEW
        world.inputMultiplexer.addProcessor(world.gestureDetector);
        //Gdx.input.setInputProcessor(inputMultiplexer);
        //LEKNEW




        world.tiledMapRender = new OrthogonalTiledMapRenderer(world.map);
        World.camera = world.getCamera();
        World.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        World.camera.position.set(world.getAvatar().getX() - world.getAvatar().getWidth() / 2, world.getAvatar().getY() - world.getAvatar().getHeight() / 2, 0);
        World.camera.update();
        world.font = new BitmapFont();
        world.font.setColor(Color.RED);
        // LEK metodo deprecado world.font.scale(2);
        mensagem="debug";
        //Gdx.app.log("Multitouch", "multitouch supported: " + Gdx.input.isPeripheralAvailable(Peripheral.MultitouchScreen));
        world.cameraZoomAdjust = new CameraZoomAdjust(0.1f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // preparar para animacao de camera
        Tween.registerAccessor(OrthographicCamera.class, new ClassMyCameraAccessor());
        // Tween.to(camera, POSITION, 2.0f).target(100.0f, 100.0f, 100.0f).ease(Back.OUT).start(tweenManager);  //Linear.INOUT

    }

    public static void disparaAnimacaoCamera(float zoom, float duracao) {
        TweenCallback oneGoes = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                System.out.println("faz zoom ");
            }
        };
        TweenCallback anotherComes = new TweenCallback() { //** myCallBack object runs time reset **//
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                System.out.println("volta para zoom original ");
            }
        };

        float zoomOriginal = World.camera.zoom;

        Timeline ttt = Timeline.createSequence();
        // Move the objects around, one after the other
        ttt.beginSequence();
        ttt.push(Tween.to(World.camera, ClassMyCameraAccessor.ZOOM, duracao)
                .target(zoom)
                .ease(Expo.INOUT)
                .setCallback(oneGoes) // use myTweenCallback created above //
                .setCallbackTriggers(TweenCallback.BEGIN)
        ); //.delay(0.01f)
        ttt.push(Tween.to(World.camera, ClassMyCameraAccessor.ZOOM, duracao)
                .target(zoomOriginal)
                .ease(Expo.INOUT)
                .setCallback(oneGoes) // use myTweenCallback created above //
                .setCallback(anotherComes) // use myTweenCallback created above //
                .setCallbackTriggers(TweenCallback.BEGIN)
        ); //.delay(0.01f)
        ttt.end();

        ttt.start(World.tweenManager);
    }

    public static void dispose() {
        //Gdx.app.log("GameLoader ", "vai liberar memoria!");
        if(world.map != null) world.map.dispose();
        if(World.atlasPlayerS_W_E_N != null) World.atlasPlayerS_W_E_N.dispose();
        if(World.atlasPlayerSW_NW_SE_NE != null) World.atlasPlayerSW_NW_SE_NE.dispose();
        if(world.backgroundMusic != null) world.backgroundMusic.dispose();
        if(world.pathPlan != null) world.pathPlan.dispose();
    }
    //-------------------------------------------


    private OrthogonalTiledMapRenderer tiledMapRender;
    private float count=100.0f;
    private BitmapFont font;
    public static String mensagem;
    CameraZoomAdjust cameraZoomAdjust;

    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        World.tweenManager.update(Gdx.graphics.getDeltaTime());

        this.tiledMapRender.getBatch().setProjectionMatrix(World.camera.combined);
        this.tiledMapRender.setView(World.camera);
        this.tiledMapRender.render();

        //LEKNEW
        cameraZoomAdjust.multitouch(World.camera, delta);// para fazer map zoom in & out

        this.tiledMapRender.getBatch().begin();
        //this.world.getAvatar().setOriginCenter();
        //this.world.getAvatar().rotate90(true);
        mensagem = "debug";
        World.world.getAvatar().draw(camera, font, mensagem, this.tiledMapRender.getBatch());
        this.tiledMapRender.getBatch().end();

        float x = World.world.getAvatar().getX() + World.world.getAvatar().getWidth();
        float y = World.world.getAvatar().getY() + World.world.getAvatar().getHeight();
        World.world.pathPlan.render(delta); // no metodo o target do path planning é alterado


        //Set our batch to now draw what the Hud camera sees.
        batch.setProjectionMatrix(World.world.hud.stage.getCamera().combined);
        World.world.hud.stage.draw();

        // adaptar a matrix de projeção para o toast
        // (coordenadas do dispositivo).
        // Não fosse isso o toast só funcionaria na região inicial do mapa próximo ao (0,0)
        Matrix4 uiMatrix = World.camera.combined.cpy();
        float screenHeight = Gdx.graphics.getHeight();
        float screenWidth = Gdx.graphics.getWidth();
        uiMatrix.setToOrtho2D(0, 0, screenWidth, screenHeight);
        this.tiledMapRender.getBatch().setProjectionMatrix(uiMatrix);
        Batch batch = this.tiledMapRender.getBatch();

        ClassToast.showToasts(batch);
    }

    //-----------------------------------

    public TiledMap map;
    private Avatar avatar;
    //private PlayerLife playerLife;
    //private OrthographicCamera camera; // substituí o atributo por static
    private static float mapWidthPixel;
    private static float mapHeightPixel;
    public Music backgroundMusic;
    public static TextureAtlas atlasPlayerS_W_E_N;
    public static TextureAtlas atlasPlayerSW_NW_SE_NE;
    private AssetManager assets;
    public static ArrayList<Rectangle> bounds;
    public static int xTiles, yTiles;
    public static int tileWidth;
    public static int tileHeight;
    public PathPlanning pathPlan;


    private World() {
        ClassToast.initToastFactory();
        Color backgroundColor = new Color(0f, 0f, 0f, 0.5f);
        Color fontColor = new Color(1, 1, 0, 0.5f);
        String msg = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";
        ClassToast.toastRich(msg, backgroundColor, fontColor, 2f);
        //backgroundColor = new Color(1f, 1f, 1f, 0.5f);
        //fontColor = new Color(1, 0, 1, 0.5f);
        //msg = "outro texto";
        //ClassToast.toastRich(msg, backgroundColor, fontColor, 11f);

        // terrain tutorials
        // https://www.youtube.com/watch?v=jJsfzjVUlPI
        // https://www.youtube.com/watch?v=WEtml6DuwqQ
        // https://gamefromscratch.com/tiled-map-editor-tutorial-series/
        //

        this.map = new TmxMapLoader().load("maps/alchemy.tmx");
        //this.map = new TmxMapLoader().load("maps/all.tmx");
        //this.map.getLayers().get("Collision").setVisible(true);// mostra ou não os tiles de colisao
        World.bounds = new ArrayList<Rectangle>();
        xTiles = map.getProperties().get("width", Integer.class);
        yTiles = map.getProperties().get("height", Integer.class);
        Gdx.app.log("World ", " xTiles="+xTiles + " yTiles="+yTiles);

        World.tileWidth = map.getProperties().get("tilewidth", Integer.class);
        World.tileHeight = map.getProperties().get("tileheight", Integer.class);
        World.mapWidthPixel =  xTiles * map.getProperties().get("tilewidth", Integer.class);
        World.mapHeightPixel = yTiles * map.getProperties().get("tileheight", Integer.class);
        //Gdx.app.log("World ", " mapWidthPixel="+World.mapWidthPixel + " mapHeightPixel="+World.mapHeightPixel);

        //*
        //		for (int i = 0; i < World.mapWidthPixel; i++) {
        //			for (int j = 0; j < World.mapHeightPixel; j++) {
        for (int i = 0; i < xTiles; i++) {
            for (int j = 0; j < yTiles; j++) {
                TiledMapTileLayer cur = (TiledMapTileLayer) this.map.getLayers().get("ground");
                Cell cell = cur.getCell(i, j);
                if(cell != null) {
                    //cell = cur.getCell(i, j);
                    //int code = cell.getTile().getId();
                    //Gdx.app.log("World ", "tile x="+i+" tile y="+j+" code="+code);
                    bounds.add(new Rectangle((i) * 32, j * 32, 32, 32));
                }
            }
        }
        //*/


        // Carrega o jogador
        this.assets = new AssetManager();
        this.assets.load("img/guerreiraS_W_E_N_210x280.pack", TextureAtlas.class);
        this.assets.load("img/guerreiraSW_NW_SE_NE_210x280.pack", TextureAtlas.class);
        this.assets.finishLoading();
        World.atlasPlayerS_W_E_N = this.assets.get("img/guerreiraS_W_E_N_210x280.pack");
        World.atlasPlayerSW_NW_SE_NE = this.assets.get("img/guerreiraSW_NW_SE_NE_210x280.pack");


        // observe que estou criando o avatar que representa
        // o jogador em cujo smartphone está rodando esse programa
        // porém, ao iniciar um jogo multiplayer
        // será necessário a criação de avatares que representem os
        // demais jogadores, com sua própria representação gráfica
        // e authUID. A coleção de avatares deverá ser tratada e deverá refletir
        // por parsing  as mensagens recebidas do(s) outro(s) jogador(es)
        // no mesmo ambiente
        this.avatar = new Avatar(new Sprite(World.atlasPlayerS_W_E_N.findRegion("South02")), (avatarStartTileX)*World.tileWidth, avatarStartTileY*World.tileHeight, PlayerData.myPlayerData().getAuthUID());

        World.camera = new OrthographicCamera(this.avatar.getX(), this.avatar.getY());
        World.camera.zoom = 0.5f;
        //Gdx.app.log(" ", "Camera created!");
        //this.backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/03-escaping-castle-wolfenstein.mp3"));
        //this.backgroundMusic.setLooping(true);
        //this.backgroundMusic.play(); // LEK

        //LEKNEW era aqui

        pathPlan = new PathPlanning(this);//LEK
        pathPlan.create();

        // ajusta o avatar do jogador para uma posição válida do path planning
        this.avatar.setX(avatarStartTileX*World.tileWidth);
        this.avatar.setY(avatarStartTileY*World.tileHeight);
    }

    public static float getMapWidthPixel() {
        return World.mapWidthPixel;
    }

    public static float getMapHeightPixel() {
        return World.mapHeightPixel;
    }

    public Avatar getAvatar() {
        return avatar;
    }


    public OrthographicCamera getCamera() {
        return World.camera;
    }


    public static ArrayList<Rectangle> getBounds() {
        return bounds;
    }

    public void setBounds(ArrayList<Rectangle> bounds) {
        World.bounds = bounds;
    }

}



class CameraController implements GestureDetector.GestureListener {
    float velX, velY;
    boolean flinging = false;
    float initialScale = 1;

    public boolean touchDown (float x, float y, int pointer, int button) {
        flinging = false;
        initialScale = World.camera.zoom;
        Gdx.app.log("botao", "touchdown at " + x + ", " + y);
        //if(ClassGame.gameState == ClassGame.GAME_OVER){
        //    ClassGame.returnMenuState();
        //}
        return false;
    }

    @Override
    public boolean tap (float x, float y, int count, int button) {
        //Gdx.app.log("GestureDetectorTest", "tap at " + x + ", " + y + ", count: " + count);
        return false;
    }

    @Override
    public boolean longPress (float x, float y) {
        //Gdx.app.log("GestureDetectorTest", "altera long press at " + x + ", " + y);
            /*
            if (ClassGame.morto.isVisible()){
                ClassGame.morto.setVisible(false);
                ClassGame.morto.setPosition(ClassGame.morto.getX()+10,ClassGame.morto.getY()+10);
            } else {
                ClassGame.morto.setVisible(true);
            }
            //*/
        return false;
    }

    @Override
    public boolean fling (float velocityX, float velocityY, int button) {
        //Gdx.app.log("GestureDetectorTest", "fling " + velocityX + ", " + velocityY);
        flinging = true;
        velX = World.camera.zoom * velocityX * 0.5f;
        velY = World.camera.zoom * velocityY * 0.5f;
        return false;
    }

    @Override
    public boolean pan (float x, float y, float deltaX, float deltaY) {
        // Gdx.app.log("GestureDetectorTest", "pan at " + x + ", " + y);
        World.camera.position.add(-deltaX * World.camera.zoom, deltaY * World.camera.zoom, 0);
        return false;
    }

    @Override
    public boolean panStop (float x, float y, int pointer, int button) {
        //Gdx.app.log("GestureDetectorTest", "pan stop at " + x + ", " + y);
        return false;
    }

    @Override
    public boolean zoom (float originalDistance, float currentDistance) {
        float ratio = originalDistance / currentDistance;
        World.camera.zoom = initialScale * ratio;
        return false;
    }

    @Override
    public boolean pinch (Vector2 initialFirstPointer, Vector2 initialSecondPointer, Vector2 firstPointer, Vector2 secondPointer) {
        return false;
    }

    public void update () {
        if (flinging) {
            velX *= 0.98f;
            velY *= 0.98f;
            World.camera.position.add(-velX * Gdx.graphics.getDeltaTime(), velY * Gdx.graphics.getDeltaTime(), 0);
            if (Math.abs(velX) < 0.01f) velX = 0;
            if (Math.abs(velY) < 0.01f) velY = 0;
        }
    }

    @Override
    public void pinchStop () {
    }
}

