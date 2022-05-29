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
package br.uff.ic.lek.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

public class CameraZoomAdjust {
    private float deltaZoom;
    private int touchCount;
    private int samplesCount;
    private Vector2[] fingers;
    private Vector2[] previousFingers;
    private float squareDiagonal;

    public float squareDistance(Vector2 pointA, Vector2 pointB){
        return (pointA.x-pointB.x)*(pointA.x-pointB.x)+ (pointA.y-pointB.y)*(pointA.y-pointB.y);
    }

    public CameraZoomAdjust(float deltaZoom, int screenWidth, int screenHeight){
        squareDiagonal = (float) Math.sqrt(screenWidth*screenWidth+screenHeight*screenHeight);
        this.deltaZoom = deltaZoom;
        fingers = new Vector2[2];
        fingers[0] = new Vector2();
        fingers[1] = new Vector2();
        previousFingers = new Vector2[2];
        previousFingers[0] = new Vector2();
        previousFingers[1] = new Vector2();
        Gdx.app.log("MTT", "squareDiagonal " + squareDiagonal);
        reset();
    }

    public void reset(){
        samplesCount = touchCount = 0;
        for (int i = 0; i < 2; i++) {
            previousFingers[i].x = fingers[i].x = 0;
            previousFingers[i].y = fingers[i].y = 0;
        }
    }

    public void multitouch(OrthographicCamera camera, float delta) {
        touchCount = 0;
        for (int i = 0; i < 2; i++) {
            if (Gdx.input.isTouched(i) == false) continue;
            touchCount++;
            fingers[i].x = Gdx.input.getX(i);
            fingers[i].y = Gdx.graphics.getHeight() - Gdx.input.getY(i) - 1;
        }
        if (touchCount == 2){
            samplesCount++;
            if (samplesCount == 1){
                previousFingers[0].x = fingers[0].x;
                previousFingers[0].y = fingers[0].y;
                previousFingers[1].x = fingers[1].x;
                previousFingers[1].y = fingers[1].y;
            }
            //Gdx.app.log("MTT", "samplesCount " + samplesCount);
            if (samplesCount >= 2){ // has both fingers and previousFingers
                float distanciaAtual = (float) Math.sqrt(squareDistance(fingers[1], fingers[0]));
                float distanciaAnterior = (float) Math.sqrt(squareDistance(previousFingers[1], previousFingers[0]));
                float percentual = (distanciaAtual - distanciaAnterior)/squareDiagonal*100.0f;

                if (distanciaAtual > distanciaAnterior){
                    //distância entre dedos da amostra atual maior que a distância entre dedos da amostra anterior
                    if (percentual > 3.0f){ // 3%
                        if ((camera.zoom - deltaZoom) < 0.2f){
                            Gdx.input.vibrate(50);// precisa setar a permissão em AndroidManifest
                            camera.zoom = 0.2f;
                        } else {
                            camera.zoom -= deltaZoom;
                        }
                        Gdx.app.log("MTT", ">percentual " + percentual);
                        Gdx.app.log("MTT", "zoom " + camera.zoom);
                        reset();
                    } // vibração do dedo ou movimentação pequena proporcionalmente ao tamanho da tela não faz nada
                } else { // menor ou igual
                    if (percentual < -3.0f){ // 3%
                        if ((camera.zoom + deltaZoom)> 6f){
                            Gdx.input.vibrate(50);// precisa setar a permissão em AndroidManifest
                            camera.zoom = 6f;
                        } else {
                            camera.zoom += deltaZoom;
                        }
                        Gdx.app.log("MTT", ">percentual " + percentual);
                        Gdx.app.log("MTT", "zoom " + camera.zoom);
                        reset();
                    } // vibração do dedo ou movimentação pequena proporcionalmente ao tamanho da tela não faz nada
                }
            }
        } else {
            reset();
        }
    }

}
