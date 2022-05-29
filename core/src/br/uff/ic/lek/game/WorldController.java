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

import br.uff.ic.lek.actors.Avatar;
import br.uff.ic.lek.actors.Avatar.State;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

public class WorldController implements InputProcessor {

    private World world;
    private Avatar avatar;
    private OrthographicCamera camera;
    float minCameraX;
    float maxCameraX;
    float minCameraY;
    float maxCameraY;
    public static Vector3 vec;
    public static Vector3 target;
    public static boolean clicado;
    private boolean requestMove;

    // TODO touch seguido de up --> move
    // 		touch seguido de drag --> nao move e gera efeitos de ataque
    // acelerometro pode ser dispensado ou usado para outras coisas, porém somente de forma grosseira, pois não deu certo fazer ajustes finos nos angulos
    // Pegar o xy do node e conforme a inclinacao da reta mudar a animacao correspondente

    public WorldController(World world) {
        this.world = world;
        this.avatar = this.world.getAvatar();
        this.camera = this.world.getCamera();
    }

    public void update(float delta) {
        this.avatar.update(delta);
        this.minCameraX = this.camera.zoom * (this.camera.viewportWidth / 2);
        this.maxCameraX = World.getMapWidthPixel() - minCameraX;
        this.minCameraY = camera.zoom * (this.camera.viewportHeight / 2);
        this.maxCameraY = World.getMapHeightPixel() - minCameraY;
        this.camera.position.set(
                Math.min(maxCameraX, Math.max(this.avatar.getX(), minCameraX)),
                Math.min(maxCameraY, Math.max(this.avatar.getY(), minCameraY)),
                0);
        this.camera.update();
    }

    @Override
    public boolean keyDown(int keycode) {
        if((keycode == Input.Keys.W)) {
            this.avatar.setOrientation(Avatar.Compass.NORTH);
            this.avatar.setState(State.WALKING);
            this.avatar.getVelocity().y = Avatar.SPEED;
            WorldController.clicado = false;
            //Gdx.app.log(" ", "avatar moving upwards!");
        }
        if((keycode == Input.Keys.S)) {
            this.avatar.setOrientation(Avatar.Compass.SOUTH);
            this.avatar.setState(State.WALKING);
            this.avatar.getVelocity().y = -Avatar.SPEED;
            WorldController.clicado = false;
            //Gdx.app.log(" ", "avatar moving downwards!");
        }
        if((keycode == Input.Keys.A)) {
            this.avatar.setOrientation(Avatar.Compass.WEST);
            this.avatar.setState(State.WALKING);
            this.avatar.getVelocity().x = -Avatar.SPEED;
            WorldController.clicado = false;
            //Gdx.app.log(" ", "avatar moving to the left!");
        }
        if((keycode == Input.Keys.D)) {
            this.avatar.setOrientation(Avatar.Compass.EAST);
            this.avatar.setState(State.WALKING);
            this.avatar.getVelocity().x = Avatar.SPEED;
            WorldController.clicado = false;
            //Gdx.app.log(" ", "avatar moving to the right!");
        }
		/*
		if((keycode == Input.Keys.W) && (keycode == Input.Keys.S) || (keycode == Input.Keys.A) && (keycode == Input.Keys.D) ||
				!(keycode == Input.Keys.W) && !(keycode == Input.Keys.S) && !(keycode == Input.Keys.A) && !(keycode == Input.Keys.D)) {
				this.avatar.setState(State.IDLE);
				this.avatar.getVelocity().x = 0;
				this.avatar.getVelocity().y = 0;
				//Gdx.app.log(" ", "avatar is IDLE!");
		}
		*/
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if((keycode == Input.Keys.W) || (keycode == Input.Keys.S)) {
            this.avatar.getVelocity().y = 0;
            //Gdx.app.log(" ", "Velocity.y = 0!");
        }
        if((keycode == Input.Keys.A) || (keycode == Input.Keys.D)) {
            this.avatar.getVelocity().x = 0;
            //Gdx.app.log(" ", "Velocity.x = 0!");

        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {

        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        //World.mensagem ="down "+requestMove + " " +touchCount;
        requestMove = true;
        fezTouchDown = true;
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        //World/.mensagem ="up --->"+requestMove;
        float x = this.avatar.getX();
        float y = this.avatar.getY();
        //if(Gdx.input.isButtonPressed(Buttons.LEFT)) {
        if(requestMove){
            this.world.getAvatar().setState(State.WALKING);
            WorldController.vec = new Vector3(screenX, screenY, 0);
            this.camera.unproject(vec);
            WorldController.target = new Vector3(vec);
            //WorldRenderer.mensagem = " x="+WorldController.target.x+ " y="+WorldController.target.y;

            // invoca o jogador para dar a direcao do personagem
            if (Math.abs(WorldController.target.x - x) > Math.abs(WorldController.target.y - y)){
                // movimento preponderante em x
                if ((WorldController.target.x - x) > 0){
                    // movimento para direita
                    this.avatar.setOrientation(Avatar.Compass.EAST);
                } else {
                    this.avatar.setOrientation(Avatar.Compass.WEST);
                }
            } else {
                // movimento preponderante em y
                if ((WorldController.target.y - y) > 0){
                    // movimento para cima
                    this.avatar.setOrientation(Avatar.Compass.NORTH);
                } else {
                    this.avatar.setOrientation(Avatar.Compass.SOUTH);
                }
            }

            WorldController.clicado = true;
            //world.pathPlan.targetChanged((int) WorldController.target.x, (int) WorldController.target.y);

            world.pathPlan.targetChanged(screenX, screenY);
            // LEK aqui sao marcados os pontos inicial e final do caminhamento. A ideia
            // é você marcar o local atual do avatar como sendo o inicial e o segundo local clicado no mapa como o destino (se for valido)
            // de caminhamento. Observe que no exemplo atual o avatar caminha pelo mapa sem qualquer obediencia ao caminho determinado na criacao do mapa com o Tiled

            fezTouchDown = false;
        }
        //Gdx.app.log("WorldController ", " x="+x +" y="+y +"  target.x="+WorldController.target.x +" target.y="+WorldController.target.y);

        return true;
    }

    private boolean fezTouchDown;
    private int firstX, firstY;
    int manhattanDistance(int screenX, int screenY){
        int distance = Math.abs(screenX - firstX) + Math.abs(screenY - firstY) ;
        return distance;
    }

    public void comandoMoveTo(float moveToX, float moveToY){
        // TODO: esse comando está incompleto, pois a movimentação, no futuro, deverá levar em conta
        // o path planinng e não o caminhamento direto. Está codificado apenas para ilustrar o mecanismo de mensagens
        // VOCÊ DEVE ALTERAR ESSE MÉTODO para uma correta navegação SOMENTE POR CAMINHOS VÁLIDOS no path01 do mapa
        float x = this.avatar.getX();
        float y = this.avatar.getY();
        //if(requestMove){
        if(true){
            this.world.getAvatar().setState(State.WALKING);
            //WorldController.vec = new Vector3(screenX, screenY, 0);
            //this.camera.unproject(vec);
            WorldController.vec = new Vector3(moveToX, moveToY, 0);
            WorldController.target = new Vector3(vec);
            //WorldRenderer.mensagem = " x="+WorldController.target.x+ " y="+WorldController.target.y;

            // invoca o jogador para dar a direcao do personagem
            if (Math.abs(WorldController.target.x - x) > Math.abs(WorldController.target.y - y)){
                // movimento preponderante em x
                if ((WorldController.target.x - x) > 0){
                    // movimento para direita
                    this.avatar.setOrientation(Avatar.Compass.EAST);
                } else {
                    this.avatar.setOrientation(Avatar.Compass.WEST);
                }
            } else {
                // movimento preponderante em y
                if ((WorldController.target.y - y) > 0){
                    // movimento para cima
                    this.avatar.setOrientation(Avatar.Compass.NORTH);
                } else {
                    this.avatar.setOrientation(Avatar.Compass.SOUTH);
                }
            }

            WorldController.clicado = true;
            //world.pathPlan.targetChanged((int) WorldController.target.x, (int) WorldController.target.y);

            //TODO:  modificar world.pathPlan.targetChanged(screenX, screenY);
            // LEK aqui sao marcados os pontos inicial e final do caminhamento. A ideia
            // é você marcar o local atual do avatar como sendo o inicial e o segundo local clicado no mapa como o destino (se for valido)
            // de caminhamento. Observe que no exemplo atual o avatar caminha pelo mapa sem qualquer obediencia ao caminho determinado na criacao do mapa com o Tiled

            fezTouchDown = false;
        }
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {


        // TODO Auto-generated method stub
        Gdx.app.log("WorldController ", "arrastou x="+screenX +" y="+screenY +"  pointer="+pointer);
        if (fezTouchDown == true){
            // pegar 1o xy
            firstX = screenX; firstY=screenY;
            fezTouchDown = false;
        }
        if (manhattanDistance(screenX, screenY) > 128){
            //World.mensagem ="drag "+requestMove;
            requestMove=false;
        }

        // se o ultimo for proximo nao faz requestMove=false;
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }


}
