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
AvatarPower.java
Nesse módulo há a representaçao gráfica das vidas e poderes do jogador
 */
package br.uff.ic.lek.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class AvatarPower {


    private ShapeRenderer powerIndicator;
    private float power;
    public static final float LIFE_STATUS_OK = 50.0f;
    public static final float LIFE_STATUS_CRITICAL = 20.0f;

    public AvatarPower(float f) {
        power = f;
        powerIndicator = new ShapeRenderer();
    }

    public float getPower(){
        return power;
    }

    public void setPower(float l){
        if(l < 0.0f) l=0.0f;
        power = l;
    }

    public void draw(OrthographicCamera camera, float x, float y ) {
        //Gdx.gl.glEnable(GL20.GL_BLEND);
        //Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        powerIndicator.setProjectionMatrix(camera.combined);
        //camera.update();
        powerIndicator.end();
        float rectWidth = 20.0f;
        float rectHeight = 6.0f;
        powerIndicator.begin(ShapeType.Filled);
        powerIndicator.setColor(new Color(1, 1, 1, 0.5f));
        powerIndicator.rect(x, y, rectWidth, rectHeight);
        powerIndicator.end();

        powerIndicator.begin(ShapeType.Filled);
        float r,g,b;
        if (power > AvatarPower.LIFE_STATUS_OK){
            r = 0.0f;g = 0.5f;b = 0.5f;		//green
        } else  if (power > AvatarPower.LIFE_STATUS_CRITICAL) {
            r = 1.0f;g = 1.0f;b = 0.0f;		// red
        } else {
            r = 1.0f; g = 0.0f; b = 0.0f;	//yellow
        }
        powerIndicator.setColor(new Color(r,g,b, 0.5f));
        float powerRect = rectWidth*power/100.0f;
        powerIndicator.rect(x, y, powerRect, rectHeight);
        powerIndicator.end();
        powerIndicator.begin(ShapeType.Line);
        powerIndicator.setColor(Color.BLACK);
        powerIndicator.rect(x, y, powerRect, rectHeight);
        powerIndicator.end();

        powerIndicator.begin(ShapeType.Line);
        powerIndicator.setColor(Color.BLACK);
        powerIndicator.rect(x, y, rectWidth, rectHeight);
        powerIndicator.end();
        //Gdx.gl.glDisable(GL20.GL_BLEND);
    }

}
