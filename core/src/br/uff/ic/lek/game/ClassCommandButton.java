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


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

import br.uff.ic.lek.utils.ClassToast;


/**
 * Created by Prof. Lauro Eduardo Kozovits, TCC, UFF. Projeto Memória Periódica on 26/08/2016.
 */
public class ClassCommandButton extends ClassActorAccessor {


    public static final int  BT_NONE = 0;
    public static final int  BT_PLAY = 1;
    public static final int  BT_RESIZE = 2;
    public static final int  BT_INTERNET_SEARCH = 3;
    public static final int  BT_SOUND = 4;
    public static final int  BT_INFO = 5;
    public static final int  BT_HELP = 6;
    public static final int  BT_EXIT = 7;
    protected ShapeRenderer shapeRenderer = new ShapeRenderer();
    protected Sprite frente, fundo;
    protected boolean frontface;
    protected int acao;
    protected static int acaoAtual= ClassCommandButton.BT_NONE;
    protected SpriteDrawable spriteDrawableFrente;
    protected SpriteDrawable spriteDrawableFundo;
    protected ClassCommandButton previousButton;



    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(this.getColor());

        float x = getX();
        float y = getY();
        float height = getHeight();
        float width = getWidth();

        //*
        if (ClassCommandButton.acaoAtual == acao){
            batch.end();
            shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
            shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
            shapeRenderer.translate(x, y, 0);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled); // Filled);
            shapeRenderer.setColor(0, 0, 1, 0);
            shapeRenderer.rect(0, 0, width, height);
            shapeRenderer.end();
            batch.begin();
        }
        //*/

        //*
        if(frontface) {
            spriteDrawableFrente.draw(batch, x, y, width, height);
        }else {
            spriteDrawableFundo.draw(batch, x, y, width, height);
        }
        //*/


    }

    // ClassCommandButton são usados no HUD
    public ClassCommandButton(Sprite tfrente, Sprite tfundo, int acao)  {
        super();
        frontface = true;
        previousButton = null;
        this.frente = tfrente;
        this.fundo = tfundo; //new Sprite(tfundo);
        //setScale(0.5f, 0.5f);
        // caber 10 botoes em uma tela vertical
        float escala = Gdx.graphics.getHeight()/(8.0f*frente.getHeight());
        setBounds(frente.getX(),frente.getY(),frente.getWidth()*escala,frente.getHeight()*escala);

        this.spriteDrawableFrente = new SpriteDrawable(this.frente);
        if (this.fundo == null){
            frontface = true;
            this.spriteDrawableFundo = this.spriteDrawableFrente;
        } else {
            this.spriteDrawableFundo = new SpriteDrawable(this.fundo);
        }
        setTouchable(Touchable.enabled);
        setOrigin(frente.getWidth()/2f, frente.getHeight()/2f);
        this.acao = acao;
        final int action = acao;
        final ClassCommandButton myself = this;
        Gdx.app.log("botao", "adiciona listener"+action);
        addListener(new InputListener(){

            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log("botao", "touchDown " + x + ", " + y + " acao"+action);
                // LEK GameControl.gameState = GameControl.GAME_RUNNING;
                return true;// true não repassa false repassa
            }

            public boolean executouUmaVez = false;
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                Gdx.app.log("botao", "touchDown " + x + ", " + y + " acao"+action);
                ClassCommandButton.acaoAtual = action;
                if(action == ClassCommandButton.BT_RESIZE) {
                    /* LEK
                    if (ClassGame.screenState == ClassGame.STATE_RESIZING) {
                        ClassGame.screenState = ClassGame.STATE_NORMAL;
                        ClassHud.btRESIZE.frontface = true;
                        ClassHud.btINFO.frontface = true;
                        // nao altera Hud.btSOUND.frontface;
                        ClassHud.btINTERNET.frontface = true;
                        // nao altera Hud.btPLAY.frontface;
                        ClassCommandButton.acaoAtual= ClassCommandButton.BT_NONE;
                    } else if (ClassGame.screenState == ClassGame.STATE_NORMAL) {
                        ClassGame.screenState = ClassGame.STATE_RESIZING;
                        // TODO trocar botao para fundo
                        myself.frontface = false;

                    }
                    //LEK */
                } else if (action != ClassCommandButton.BT_RESIZE) {
                    /* LEK
                    ClassCommandButton.acaoAtual= ClassCommandButton.BT_NONE;
                    ClassGame.screenState = ClassGame.STATE_NORMAL;
                    // cor de fundo normal
                    //LEK */
                    if(action == ClassCommandButton.BT_INTERNET_SEARCH) {
                        World.disparaAnimacaoCamera(2.5f, 7.0f);

                    } else if(action == ClassCommandButton.BT_SOUND) {
                        /* LEK
                        // TODO ao clicar troca para sound off e desliga a musica de fundo
                        ClassHud.btRESIZE.frontface = true;
                        ClassHud.btINFO.frontface = true;
                        ClassHud.btINTERNET.frontface = true;
                        // nao altera Hud.btPLAY.frontface;
                        if (ClassHud.btSOUND.frontface){
                            // TODO stop music e som de cartas
                            ClassGame.sounds.stopBackgroundMusic();
                            ClassGame.sounds.disableSounds();
                            ClassHud.btSOUND.frontface = false;
                        } else {
                            // TODO playSound music, inicia jogo tocando musica
                            ClassGame.sounds.enableSounds();
                            if(ClassGame.gameState == ClassGame.GAME_RUNNING){
                                ClassGame.sounds.playBackgroundMusic();
                            }
                            ClassHud.btSOUND.frontface = true;
                        }
                        // LEK */
                    } else if(action == ClassCommandButton.BT_HELP) {
                        //* LEK

                        ClassThreadComandos.objetoAndroidFireBase.writePlayerData();
                        Color backgroundColor = new Color(0f, 0f, 0f, 0.5f);
                        Color fontColor = new Color(1, 1, 0, 0.5f);
                        String msg = "Apenas grava dados do jogador. Verifique como outros smartphones tambem executando sao afetados";
                        ClassToast.toastRich(msg, backgroundColor, fontColor, 5f);

                        // LEK */
                    } else if(action == ClassCommandButton.BT_INFO) {
                        //* LEK

                        // LEK */
                    } else if(action == ClassCommandButton.BT_PLAY) {
                        ClassHud.btRESIZE.frontface = true;
                        ClassHud.btINFO.frontface = true;
                        ClassHud.btINTERNET.frontface = true;

                        if (executouUmaVez == false) {
                            executouUmaVez=true;
                            ClassThreadComandos.objetoAndroidFireBase.waitForPlayers();
                            ClassThreadComandos.objetoAndroidFireBase.waitForMyMessages();
                            Color backgroundColor = new Color(0f, 0f, 0f, 0.5f);
                            Color fontColor = new Color(1, 1, 0, 0.5f);
                            String msg = "Por enquanto, o app se prepara para receber mensagens do Firebase";
                            ClassToast.toastRich(msg, backgroundColor, fontColor, 5f);
                        }
                    } else if(action == ClassCommandButton.BT_EXIT) {
                        //Gdx.app.exit();
                        ClassThreadComandos.objetoAndroidFireBase.finishAndRemoveTask();
                        /* LEK
                        ClassHud.btRESIZE.frontface = true;
                        ClassHud.btINFO.frontface = true;
                        // nao altera Hud.btSOUND.frontface;
                        ClassHud.btINTERNET.frontface = true;
                        if (ClassHud.btPLAY.frontface){
                            // TODO apenas sai do jogo, pois nao tem nenhum em andamento

                            ClassGame.gameState = ClassGame.GAME_MENU;
                            ClassGame.player=ClassGame.PLAYER_MYSELF; //.PLAYER_MYSELF;
                            ClassGame.ajustGameState();
                            //Gdx.app.exit();
                            ClassThreadComandos.objetoAndroidFireBase.finishAndRemoveTask();
                        } else {
                            ClassGame.ajudaDebug = true; // se cancelar o proximo jogo é curtinho
                            // TODO interromper salvando o jogo para retomar posteriormente
                            if (ClassGame.prepareDialog(0.3f)) { // para o dialogo nao ficar pequeno
                                Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));
                                Dialog dialog = new Dialog("AVISO", skin, "dialog") {
                                    public void result(Object obj) {
                                        ClassGame.finishDialog(); // para o dialogo nao ficar pequeno
                                        if ("true".equals(obj.toString()) ){
                                            //
                                            ClassGame.gameState = ClassGame.GAME_MENU;
                                            ClassGame.player=ClassGame.PLAYER_MYSELF; //.PLAYER_MYSELF;
                                            ClassGame.ajustGameState();

                                            //Gdx.app.exit();
                                            ClassThreadComandos.objetoAndroidFireBase.finishAndRemoveTask();
                                        }

                                    }
                                };
                                dialog.text("Voce quer interromper o jogo e sair?");
                                dialog.button("Sim", true); //sends "true" as the result
                                dialog.button("Nao", false);  //sends "false" as the result
                                dialog.key(Input.Keys.ENTER, true); //sends "true" when the ENTER key is pressed
                                dialog.show(ClassGame.stageCG);
                            }
                        }
                        // LEK */
                    }
                }

            }

        });
    }


    static boolean flag=false;



    /***************************************************
     // http://stackoverflow.com/questions/29144352/libgdx-alert-dialog

     public void quitGameConfirm() {

     Label.LabelStyle style = new Label.LabelStyle(_fontChat, Color.WHITE);
     Label label1 = new Label("Are you sure that you want to exit?", style);
     label1.setAlignment(Align.center);
     //style.font.setScale(1, -1);
     style.fontColor = Color.WHITE;

     Skin tileSkin = new Skin();
     Texture tex = new Texture(myButtontexture);
     tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
     tileSkin.add("white", tex);
     tileSkin.add("default", new BitmapFont());

     TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
     textButtonStyle.up = tileSkin.newDrawable("white");
     textButtonStyle.down = tileSkin.newDrawable("white", Color.DARK_GRAY);
     textButtonStyle.checked = tileSkin.newDrawable("white",
     Color.LIGHT_GRAY);
     textButtonStyle.over = tileSkin.newDrawable("white", Color.LIGHT_GRAY);
     textButtonStyle.font = _myTextBitmapFont;
     textButtonStyle.font.setScale(1, -1);
     textButtonStyle.fontColor = Color.WHITE;
     tileSkin.add("default", textButtonStyle);

     TextButton btnYes = new TextButton("Exit", tileSkin);
     TextButton btnNo = new TextButton("Cancel", tileSkin);

     // /////////////////
     Skin skinDialog = new Skin(Gdx.files.internal("data/uiskin.json"));
     final Dialog dialog = new Dialog("", skinDialog) {
    @Override
    public float getPrefWidth() {
    // force dialog width
    // return Gdx.graphics.getWidth() / 2;
    return 700f;
    }

    @Override
    public float getPrefHeight() {
    // force dialog height
    // return Gdx.graphics.getWidth() / 2;
    return 400f;
    }
    };
     dialog.setModal(true);
     dialog.setMovable(false);
     dialog.setResizable(false);

     btnYes.addListener(new InputListener() {
    @Override
    public boolean touchDown(InputEvent event, float x, float y,
    int pointer, int button) {

    // Do whatever here for exit button

    //LEK _parent.changeState("StateMenu");
    dialog.hide();
    dialog.cancel();
    dialog.remove();

    return true;
    }

    });

     btnNo.addListener(new InputListener() {
    @Override
    public boolean touchDown(InputEvent event, float x, float y,
    int pointer, int button) {

    //Do whatever here for cancel

    dialog.cancel();
    dialog.hide();

    return true;
    }

    });

     TextureRegion myTex = new TextureRegion(_dialogBackgroundTextureRegion);
     myTex.flip(false, true);
     myTex.getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
     Drawable drawable = new TextureRegionDrawable(myTex);
     dialog.setBackground(drawable);

     float btnSize = 80f;
     Table t = new Table();
     // t.debug();

     dialog.getContentTable().add(label1).padTop(40f);

     t.add(btnYes).width(btnSize).height(btnSize);
     t.add(btnNo).width(btnSize).height(btnSize);

     dialog.getButtonTable().add(t).center().padBottom(80f);
     dialog.show(stage).setPosition(
     (MyGame.VIRTUAL_WIDTH / 2) - (720 / 2),
     (MyGame.VIRTUAL_HEIGHT) - (MyGame.VIRTUAL_HEIGHT - 40));

     dialog.setName("quitDialog");
     stage.addActor(dialog);

     }

     //**************************************************/
}
