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
Avatar.java
Nesse módulo há a representaçao gráfica do jogador como um Sprite
a lógica de animação de sprites, bem como seu deslocamento são realizadas aqui
O módulo deve ser melhorado para representar os demais jogadores da versão multiplayer
 */
package br.uff.ic.lek.actors;

import br.uff.ic.lek.game.World;
import br.uff.ic.lek.game.WorldController;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Peripheral;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Avatar extends Sprite {

    // authUID para fazer o casamento do avatar gráfico(Sprite) com o jogador armazenado no Firebase
    String authUID;

    public String getAuthUID() {
        return authUID;
    }

    public void setAuthUID(String authUID) {
        this.authUID = authUID;
    }

    public enum State {
        IDLE, WALKING, DYING
    }
    private State state = State.IDLE;

    public State getState() {
        return state;
    }
    public void setState(State state) {
        this.state = state;
    }

    private AvatarPower avatarPower;


    public enum Compass {
        SOUTH, NORTH, WEST, EAST, SOUTH_WEST, NORTH_WEST, SOUTH_EAST, NORTH_EAST
    }

    private Compass orientation;

    public Compass getOrientation(){
        return orientation;
    }
    public void setOrientation(Compass orientation){
        this.orientation = orientation;
    }
    public void defineOrientation(double anguloGraus){
        if (anguloGraus > 22.5 &&  anguloGraus <= 67.5)
            this.orientation = Compass.NORTH_EAST;
        else if (anguloGraus > 67.5 &&  anguloGraus <= 112.5)
            this.orientation = Compass.NORTH;
        else if (anguloGraus > 112.5 &&  anguloGraus <= 157.5)
            this.orientation = Compass.NORTH_WEST;
        else if (anguloGraus > 157.5 &&  anguloGraus <= 202.5)
            this.orientation = Compass.WEST;
        else if (anguloGraus > 202.5 &&  anguloGraus <= 247.5)
            this.orientation = Compass.SOUTH_WEST;
        else if (anguloGraus > 247.5 &&  anguloGraus <= 292.5)
            this.orientation = Compass.SOUTH;
        else if (anguloGraus > 292.5 &&  anguloGraus <= 337.5)
            this.orientation = Compass.SOUTH_EAST;
        else  //		if (anguloGraus > 337.5 &&  anguloGraus <= 22.5)
            this.orientation = Compass.EAST;
    }

    private Vector3 vetorUnitarioMovimento;
    private double playerMovementAngle;

    public static float SPEED = 128f;	// unit per second (two tiles per second)
    // Atenção: se você considerar que um tile tem largura compatível com um metro
    // então a velocidade do jogador seria 4m/segundo ou 14,4km/h
    // Entretanto, o jogador percorre um terreno de 6400 pixels ("200m") em 25s aprox. ou 256 pixels/segundo
    //  Qual é o bug?

    private Vector3 velocity = new Vector3();
    private Vector3 temp = new Vector3(0,0,0);
    private Vector3 current = new Vector3(0,0,0);
    private Vector3 goodPos = new Vector3(0,0,0);
    private Animation walkingWest;
    private Animation walkingEast;
    private Animation walkingNorth;
    private Animation walkingSouth;
    private TextureRegion[] walkingWestFrames = new TextureRegion[3];
    private TextureRegion[] walkingEastFrames = new TextureRegion[3];
    private TextureRegion[] walkingNorthFrames = new TextureRegion[3];
    private TextureRegion[] walkingSouthFrames = new TextureRegion[3];
    private Animation walkingSouthWest;
    private Animation walkingNorthWest;
    private Animation walkingSouthEast;
    private Animation walkingNorthEast;
    private TextureRegion[] walkingSouthWestFrames = new TextureRegion[3];
    private TextureRegion[] walkingNorthWestFrames = new TextureRegion[3];
    private TextureRegion[] walkingSouthEastFrames = new TextureRegion[3];
    private TextureRegion[] walkingNorthEastFrames = new TextureRegion[3];
    private float elapsedTime;
    private TextureRegion currentFrame;
    private float tempoAcumulado;



    public Avatar(Sprite sprite, float x, float y, String authUID) {
        super(sprite);
        vetorUnitarioMovimento = new Vector3(0,1,0);
        this.setPosition(x, y);
        this.authUID = authUID;
        this.avatarPower = new AvatarPower(100.0f);
        temp.x = x;
        temp.y = y;
        temp.z = (float) 0.0;
        this.state = State.IDLE;
        this.orientation = Compass.SOUTH;

        walkingSouthFrames[0] = World.atlasPlayerS_W_E_N.findRegion("South01");
        walkingSouthFrames[1] = World.atlasPlayerS_W_E_N.findRegion("South02");
        walkingSouthFrames[2] = World.atlasPlayerS_W_E_N.findRegion("South03");
        this.walkingSouth = new Animation<>(0.1f, walkingSouthFrames);

        walkingWestFrames[0] = World.atlasPlayerS_W_E_N.findRegion("West01");
        walkingWestFrames[1] = World.atlasPlayerS_W_E_N.findRegion("West02");
        walkingWestFrames[2] = World.atlasPlayerS_W_E_N.findRegion("West03");
        this.walkingWest = new Animation<>(0.1f, walkingWestFrames);
        walkingEastFrames[0] = World.atlasPlayerS_W_E_N.findRegion("East01");
        walkingEastFrames[1] = World.atlasPlayerS_W_E_N.findRegion("East02");
        walkingEastFrames[2] = World.atlasPlayerS_W_E_N.findRegion("East03");
        this.walkingEast = new Animation<>(0.1f, walkingEastFrames);

        walkingNorthFrames[0] = World.atlasPlayerS_W_E_N.findRegion("North01");
        walkingNorthFrames[1] = World.atlasPlayerS_W_E_N.findRegion("North02");
        walkingNorthFrames[2] = World.atlasPlayerS_W_E_N.findRegion("North03");
        this.walkingNorth = new Animation<>(0.1f, walkingNorthFrames);



        walkingSouthWestFrames[0] = World.atlasPlayerSW_NW_SE_NE.findRegion("SouthWest01");
        walkingSouthWestFrames[1] = World.atlasPlayerSW_NW_SE_NE.findRegion("SouthWest02");
        walkingSouthWestFrames[2] = World.atlasPlayerSW_NW_SE_NE.findRegion("SouthWest03");
        this.walkingSouthWest = new Animation<>(0.1f, walkingSouthWestFrames);

        walkingNorthWestFrames[0] = World.atlasPlayerSW_NW_SE_NE.findRegion("NorthWest01");
        walkingNorthWestFrames[1] = World.atlasPlayerSW_NW_SE_NE.findRegion("NorthWest02");
        walkingNorthWestFrames[2] = World.atlasPlayerSW_NW_SE_NE.findRegion("NorthWest03");
        this.walkingNorthWest = new Animation<>(0.1f, walkingNorthWestFrames);

        walkingSouthEastFrames[0] = World.atlasPlayerSW_NW_SE_NE.findRegion("SouthEast01");
        walkingSouthEastFrames[1] = World.atlasPlayerSW_NW_SE_NE.findRegion("SouthEast02");
        walkingSouthEastFrames[2] = World.atlasPlayerSW_NW_SE_NE.findRegion("SouthEast03");
        this.walkingSouthEast = new Animation<>(0.1f, walkingSouthEastFrames);

        walkingNorthEastFrames[0] = World.atlasPlayerSW_NW_SE_NE.findRegion("NorthEast01");
        walkingNorthEastFrames[1] = World.atlasPlayerSW_NW_SE_NE.findRegion("NorthEast02");
        walkingNorthEastFrames[2] = World.atlasPlayerSW_NW_SE_NE.findRegion("NorthEast03");
        this.walkingNorthEast = new Animation<>(0.1f, walkingNorthEastFrames);

        tempoAcumulado = 0.0f;

    }

    public boolean houveColisao;
    public void update(float delta) {


        avatarPower.setPower(avatarPower.getPower() - 0.01f);// decrescimo arbitrário só para ver funcionando

        if(this.velocity.x > Avatar.SPEED) {
            this.velocity.x = Avatar.SPEED;
        }
        if(this.velocity.x < -Avatar.SPEED) {
            this.velocity.x = -Avatar.SPEED;
        }
        if(this.velocity.y > Avatar.SPEED) {
            this.velocity.y = Avatar.SPEED;
        }
        if(this.velocity.y < -Avatar.SPEED) {
            this.velocity.y = -Avatar.SPEED;
        }

        temp.x = this.getX();
        temp.y = this.getY();
        current = temp;


        //Accelerometer movement
        //*
        if(Gdx.input.isPeripheralAvailable(Peripheral.Accelerometer)) {
            //float accelSouth = Gdx.input.getAccelerometerX();
            //float accelEast = Gdx.input.getAccelerometerY();

            //TODO colocar movimentos laterais LEK e diminuir a sensibilidade do acelerometro para permitir combates com dragtouch nos 4 sentidos
            // somente quando muito inclinado > 20 graus eh que comeca andar e a 90 graus vai super rapido


            // suponha uma bussola orientando os vetores de movimento.
            // somente com módulo >= 2.0f havera movimento
            // Por exemplo uma inclinação somente para o NORTH irá resultar em movimento para o norte (para cima) somente quando accelX < -2 (modulo 2)
            // Entretanto, é possível haver movimento com módulo de accelX menor que 2 desde que a composição do movimento com accelY resulte em módulo 2
            // Esse seria o caso de (accelY*accelY+accelX*accelX)>=4.0

            // ver o grau de inclinação
            // se modulo maior que 2 vai andar
            // verificar o angulo é dar a orientacao correspondente

            //int orientation = Gdx.input.getRotation();
            //This will return a value of 0, 90, 180 or 270, giving you the angular difference between the current orientation and the native orientation.
            //The native orientation is either portrait mode (as in the image above) or landscape mode (mostly for tablets). It can be queried as follows:
            //Orientation nativeOrientation = Gdx.input.getNativeOrientation();
            //Gdx.app.log("Orientacao ", orientation + " nome="+nativeOrientation.toString());
            //double angle = Math.atan2(accelSouth, accelEast)/(Math.PI/180);
            //Gdx.app.log("angulo ", " ang="+angle);


            //double angle = Math.atan((double) accelSouth/(double) accelEast)/(Math.PI/180.0);
            tempoAcumulado += delta;
            if (tempoAcumulado > 1.0){
                //float angle = Gdx.input.getAzimuth();
                tempoAcumulado = 0.0f;
                //Gdx.app.log("Player ", " accelSouth="+accelSouth + " accelEast="+accelEast);
                //WorldRenderer.mensagem = " South="+accelSouth+ " East="+accelEast;
            }
			/*
			if(accelEast*accelEast > 4.0f) {
				this.setX(this.getX() + (accelEast * Player.ACCEL_SPEED * delta));
				this.setState(State.WALKING);
				if (accelEast > 0.0f){
					this.setOrientation(Compass.EAST);
					Gdx.app.log("Player direita", " accelSouth="+accelSouth + " accelEast="+accelEast);
				}else{
					this.setOrientation(Compass.WEST);
					Gdx.app.log("Player esquerda", " accelSouth="+accelSouth + " accelEast="+accelEast);
				}
				WorldController.clicado = false;
				current = temp;
			} else if(accelSouth*accelSouth > 4.0f) {
				this.setY(this.getY() - (accelSouth * Player.ACCEL_SPEED * delta));
				this.setState(State.WALKING);
				if (accelSouth > 0.0f){
					this.setOrientation(Compass.SOUTH);
					Gdx.app.log("Player baixo", " accelSouth="+accelSouth + " accelEast="+accelEast);
				}else{
					this.setOrientation(Compass.NORTH);
					Gdx.app.log("Player cima", " accelSouth="+accelSouth + " accelEast="+accelEast);
				}
				WorldController.clicado = false;
				current = temp;
			} else 	if( (accelEast*accelEast + accelSouth*accelSouth) > 4.0f) {
				this.setX(this.getX() + (accelEast * Player.ACCEL_SPEED * delta));
				this.setY(this.getY() - (accelSouth * Player.ACCEL_SPEED * delta));
				this.setState(State.WALKING);
				if (accelEast > 0.0f && accelSouth > 0.0f)
					this.setOrientation(Compass.SOUTH_EAST);
				else if (accelEast < 0.0f && accelSouth < 0.0f)
					this.setOrientation(Compass.NORTH_WEST);
				else if (accelEast > 0.0f && accelSouth < 0.0f)
					this.setOrientation(Compass.NORTH_EAST);
				else if (accelEast < 0.0f && accelSouth > 0.0f)
					this.setOrientation(Compass.SOUTH_WEST);
				WorldController.clicado = false;
				current = temp;
			}
			*/

        }
        //*/

        //Touch movement
        if(WorldController.clicado) {
            //Gdx.app.log("Player ", " clicado x="+this.getX() + " y="+this.getY());
            if(houveColisao == false){
                //current.lerp(new Vector3(WorldController.target.x, WorldController.target.y, 0), 0.005f/world.getCamera().zoom);
                double distancia = Math.sqrt((current.x - WorldController.target.x)*(current.x - WorldController.target.x) + (current.y - WorldController.target.y)*(current.y - WorldController.target.y));
                vetorUnitarioMovimento.x = (float) ((WorldController.target.x -current.x)/distancia);
                vetorUnitarioMovimento.y = (float) ((WorldController.target.y -current.y)/distancia);
                double angulo1 = Math.acos((double) vetorUnitarioMovimento.x )*180.0/Math.PI; // [-90.0 .. 90.0]
                double angulo2 = Math.asin((double) vetorUnitarioMovimento.y )*180.0/Math.PI; // [0.0 .. 180.0]
                if(angulo2 >= 0.0)
                    playerMovementAngle = angulo1;
                else
                    playerMovementAngle = 360.0 - angulo1;
                defineOrientation(playerMovementAngle);
                //System.out.println("angulo1="+angulo1+" angulo2="+angulo2+" Valendo="+playerMovementAngle);


                // TODO: descubra o erro: a velocidade está o dobro da prevista
                double posx = (WorldController.target.x -current.x)/distancia * Avatar.SPEED*delta  + current.x;
                double posy = (WorldController.target.y -current.y)/distancia * Avatar.SPEED*delta  + current.y;
                current.x = (float) posx;
                current.y = (float) posy;
                this.setPosition(current.x, current.y);
                this.setState(State.WALKING);
            }
            if(this.current.dst(WorldController.target.x, WorldController.target.y, 0) < 32/2) {
                WorldController.clicado = false;
                this.setState(State.IDLE);
            }
        }


        //Gdx.app.log("Player", "check indo para tx="+WorldController.target.x+" ty="+WorldController.target.y);
        //Gdx.app.log("Player", "goodPos x="+goodPos.x+" y="+goodPos.y);
        houveColisao = false;
        /*
        for(int i = 0; i < World.getBounds().size(); i++) {
            if(World.getBounds().get(i).overlaps(this.getBoundingRectangle())) {
                Gdx.app.log("Player", "Collision detected indo para tx="+WorldController.target.x+" ty="+WorldController.target.y);
                Gdx.app.log("Player", "goodPos x="+goodPos.x+" y="+goodPos.y);


				//if (((World.getBounds().get(i).x + World.getBounds().get(i).width) < this.getX()) || ((this.getX() +this.getWidth()) < World.getBounds().get(i).x) ){
					// nao houve colisao em x
				//}

                houveColisao = true;
                this.setPosition(goodPos.x, goodPos.y);
                current = goodPos;
                WorldController.clicado = false;
                this.setState(State.IDLE);
            }
        }
        */

        if(houveColisao == false){
            goodPos.x = (int) current.x;
            goodPos.y = (int) current.y;
        }

        //Check world boundaries
        if(this.getX() < 0) {
            Gdx.input.vibrate(50);// precisa setar a permissão em AndroidManifest
            //this.setX(temp.x + 1);
            this.setX(1);
            temp.x = this.getX();
            this.getVelocity().x = 0;
            this.setState(State.IDLE);
        } else if(this.getX() > (World.getMapWidthPixel() - this.getWidth())) {
            Gdx.input.vibrate(50);// precisa setar a permissão em AndroidManifest
            //this.setX(temp.x - 1);
            this.setX(World.getMapWidthPixel() - this.getWidth() - 1);
            temp.x = this.getX();
            this.getVelocity().x = 0;
            this.setState(State.IDLE);
        } else {
            //this.setX(this.getX() + this.velocity.x * delta);
            //this.setState(State.WALKING);
            //Gdx.app.log("Player ", "Player position updated!");
        }
        if(this.getY() < 0) {
            Gdx.input.vibrate(50);// precisa setar a permissão em AndroidManifest
            //this.setY(temp.y + 1);
            this.setY(1);
            temp.y = this.getY();
            this.getVelocity().y = 0;
            this.setState(State.IDLE);
        } else if(this.getY() > (World.getMapHeightPixel() - this.getHeight())) {
            Gdx.input.vibrate(50);// precisa setar a permissão em AndroidManifest
            //this.setY(temp.y - 1);
            this.setY(World.getMapHeightPixel() - this.getHeight() -1);
            temp.y = this.getY();
            this.getVelocity().y = 0;
            this.setState(State.IDLE);
        } else {
            //this.setY(this.getY() + this.velocity.y * delta);
            //this.setState(State.WALKING);
            //Gdx.app.log("Player ", "Player position updated!");
        }
    }

    public void draw(OrthographicCamera camera, BitmapFont font, String mensagem, Batch batch) {
        this.update(Gdx.graphics.getDeltaTime());
        //Gdx.app.log("Player ", "Player updated!");
        elapsedTime += Gdx.graphics.getDeltaTime();


        if(this.getState() == State.IDLE) {

            // LEK TODO trocar um único sprite por uma animação do personagem parado balançando de um lado para o outro. Esta é uma técnica de animação usada profissionalmente

            if(this.orientation == Compass.SOUTH) {
                this.currentFrame = World.atlasPlayerS_W_E_N.findRegion("South02");
            } else if(this.orientation == Compass.NORTH) {
                this.currentFrame = World.atlasPlayerS_W_E_N.findRegion("North02");
            } else	if(this.orientation == Compass.WEST) {
                this.currentFrame = World.atlasPlayerS_W_E_N.findRegion("West02");
            } else 	if(this.orientation == Compass.EAST) {
                this.currentFrame = World.atlasPlayerS_W_E_N.findRegion("East02");
            }
        }
        if(this.getState() == State.WALKING) {
            //Gdx.app.log("Player ", "elapsedTime= "+elapsedTime);
            //System.out.println(this.orientation);
            if(this.orientation == Compass.WEST) {
                this.currentFrame = (TextureRegion) walkingWest.getKeyFrame(this.elapsedTime, true);
            } else 	if(this.orientation == Compass.EAST) {
                this.currentFrame = (TextureRegion)walkingEast.getKeyFrame(this.elapsedTime, true);
            } else 	if(this.orientation == Compass.NORTH) {
                this.currentFrame = (TextureRegion)walkingNorth.getKeyFrame(this.elapsedTime, true);
            } else 	if(this.orientation == Compass.SOUTH) {
                this.currentFrame = (TextureRegion)walkingSouth.getKeyFrame(this.elapsedTime, true);
            } else 	if(this.orientation == Compass.SOUTH_WEST) {
                this.currentFrame = (TextureRegion)walkingSouthWest.getKeyFrame(this.elapsedTime, true);
            } else 	if(this.orientation == Compass.SOUTH_EAST) {
                this.currentFrame = (TextureRegion)walkingSouthEast.getKeyFrame(this.elapsedTime, true);
            } else 	if(this.orientation == Compass.NORTH_WEST) {
                this.currentFrame = (TextureRegion)walkingNorthWest.getKeyFrame(this.elapsedTime, true);
            } else 	if(this.orientation == Compass.NORTH_EAST) {
                this.currentFrame = (TextureRegion)walkingNorthEast.getKeyFrame(this.elapsedTime, true);
            }
        }
        batch.draw(this.currentFrame, this.getX(), this.getY());

        if ("debug".equals(mensagem)){
            double myX = this.getX();
            double myY = this.getY();
            DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
            df.setMaximumFractionDigits(0); //340 = DecimalFormat.DOUBLE_FRACTION_DIGITS
            mensagem = "("+df.format(myX)+", "+df.format(myY)+")";
        } else {
        }
        font.draw(batch, mensagem,  this.getX(), this.getY()+this.getHeight()*1.3f);

        batch.end();
        batch.begin();
        avatarPower.draw(camera, this.getX(), this.getY());
        //
        //Gdx.app.log("Player ", "Player drew!");
    }

    @Override
    public float getX() {
        // TODO Auto-generated method stub
        return super.getX();
    }

    @Override
    public float getY() {
        // TODO Auto-generated method stub
        return super.getY();
    }

    @Override
    public float getWidth() {
        // TODO Auto-generated method stub
        return super.getWidth();
    }

    @Override
    public float getHeight() {
        // TODO Auto-generated method stub
        return super.getHeight();
    }

    public Vector3 getVelocity() {
        return velocity;
    }



}
