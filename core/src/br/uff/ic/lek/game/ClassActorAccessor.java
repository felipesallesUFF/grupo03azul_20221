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
ClassActorAccessor.java
Permite a animação de atores usando a tween-engine (procure no Google)
https://github.com/AurelienRibon/universal-tween-engine

 */
package br.uff.ic.lek.game;

import aurelienribon.tweenengine.TweenAccessor;
// implementation "org.mini2Dx:universal-tween-engine:6.3.3"

// TEMPLATE para ser usado em atores


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;


public class ClassActorAccessor extends Actor implements TweenAccessor<Actor> {

    // ===========================================================
    // Constants
    // ===========================================================

    public static final int POS_XY = 1;
    public static final int CPOS_XY = 2;
    public static final int SCALE_XY = 3;
    public static final int OPACITY = 4;
    public static final int ROTATE = 5;
    public static final int POS_Y = 6;
    public static final int WIDTH = 7;
    public static final int POS_X = 8;
    public static final int HEIGHT = 9;
    public static final int FLIP = 10;

    // ===========================================================
    // Fields
    // ===========================================================

    // ===========================================================
    // Constructors
    // ===========================================================
    public ClassActorAccessor(){
        super();
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    //LEK OLD VERSION @Override
    public int getValues(Actor target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case POS_XY:
                returnValues[0] = target.getX();
                returnValues[1] = target.getY();
                return 2;
            case CPOS_XY:
                returnValues[0] = target.getX() + target.getWidth() / 2;
                returnValues[1] = target.getY() + target.getHeight() / 2;
                return 2;

            case SCALE_XY:
                returnValues[0] = target.getScaleX();
                returnValues[1] = target.getScaleY();
                return 2;
            case OPACITY:
                returnValues[0] = target.getColor().a;
                return 1;
            case ROTATE:
                returnValues[0] = target.getRotation();
                return 1;
            case POS_Y:
                returnValues[0] = target.getY();
                return 1;
            case WIDTH:
                returnValues[0] = target.getWidth();
                return 1;
            case POS_X:
                returnValues[0] = target.getX();
                return 1;
            case HEIGHT:
                returnValues[0] = target.getHeight();
                return 1;
            default:
                assert false;
                return -1;
        }
    }

    //LEK OLD VERSION @Override
    public void setValues(Actor target, int tweenType, float[] newValues) {
//      if (Gdx.graphics.getDeltaTime() > 0.018f)
//      Gdx.app.log("test", Gdx.graphics.getDeltaTime()+"" );
        switch (tweenType) {
            case POS_XY:
                target.setPosition(newValues[0], newValues[1]);
                break;
            case CPOS_XY:
                target.setPosition(newValues[0] - target.getWidth() / 2,
                        newValues[1] - target.getHeight() / 2);
                break;
            case SCALE_XY:
                target.setScale(newValues[0], newValues[1]);
                break;
            case ROTATE:
                target.setRotation(newValues[0]);
                break;
            case OPACITY:
                Color c = target.getColor();
                c.set(c.r, c.g, c.b, newValues[0]);
                target.setColor(c);
                break;
            case POS_Y:
                target.setY(newValues[0]);
                break;
            case WIDTH:
                target.setWidth(newValues[0]);
                break;
            case POS_X:
                target.setX(newValues[0]);
                break;
            case HEIGHT:
                target.setHeight(newValues[0]);
                break;

        }
    }

    // ===========================================================
    // Methods
    // ===========================================================

    float flip;

    public void setFlip(float flip){
        this.flip = flip;
    }

    public float getFlip(){
        return this.flip;
    }
    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
