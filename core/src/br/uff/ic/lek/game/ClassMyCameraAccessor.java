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
A ClassMyCameraAccessor necessária ("é um" TweenAccessor de câmera) para animação da câmera (zoom out seguida de  zoom in)
possibilitada pela tecnologia tweenengine
 */
package br.uff.ic.lek.game;

import com.badlogic.gdx.graphics.OrthographicCamera;


import aurelienribon.tweenengine.TweenAccessor;
// implementation "org.mini2Dx:universal-tween-engine:6.3.3"

/**
 * Created by Prof. Lauro Eduardo Kozovits, TCC, UFF. Projeto Memória Periódica on 25/09/2016.
 */
public class ClassMyCameraAccessor implements TweenAccessor<OrthographicCamera> {
    public static final int POSITION = 1;
    public static final int ZOOM = 2;

    public int getValues(OrthographicCamera target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case ZOOM:
                returnValues[0] = target.zoom;
                return 1;
            case POSITION:
                returnValues[0] = target.position.x;
                returnValues[1] = target.position.y;
                returnValues[2] = target.position.z;
                return 3;
        }
        return 0;
    }

    public void setValues(OrthographicCamera target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case ZOOM:
                target.zoom = newValues[0];
                break;
            case POSITION:
                target.position.x = newValues[0];
                target.position.y = newValues[1];
                target.position.z = newValues[2];
                break;
        }
    }
}