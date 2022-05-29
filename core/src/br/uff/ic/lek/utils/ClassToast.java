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
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Android-like toast implementation for LibGDX projects
 *
 * @author Tomas Chalupnik (tchalupnik.cz)
 * https://github.com/wentsa/Toast-LibGDX
 *
 * fixed and modified by LEK - Lauro Eduardo Kozovits
 */
public class ClassToast { //LEK extends Actor {
    public static final float SHORT = 2.0f;
    public static final float LONG = 3.5f;
    public enum Length2 {
        SHORT2(2f),
        LONG2(3.5f);
        private final float duration; // in seconds
        Length2(float duration) {
            this.duration = duration;
        }
    }


    //* LEKREMOVER
    private static List<ClassToast> toasts = new LinkedList<ClassToast>();
    private static ClassToast.ClassToastFactory toastFactory;
    //private ClassDisplayText cdt;

    public static void initToastFactory(){

        if (ClassToast.renderer  == null){
            ClassToast.renderer = new ShapeRenderer();
        }

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("ToastFont.ttf"));
        //FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("LiberationSans-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        float correcao = Gdx.graphics.getDensity(); // Gdx.graphics.getDensity(); //Gdx.graphics.getWidth()/1920.0f; //Gdx.graphics.getDensity()*
        parameter.characters = " abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789:;,/*!?-+()[]_^.áàéíóúâêôçÇ@#&%=";
        //e.g. abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.!'()>?:
        // These characters should not repeat!
        correcao = 1.0f; // cancelo, pois Gdx.graphics.getDensity() não está funcionando
        parameter.size = (int) (50f * correcao); //TODO o font está ficando com tamanhos diferentes em cada celular
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        toastFactory = new ClassToast.ClassToastFactory.Builder()
                .font(font)  //.font(font) pode usar o font default ou passar um font
                .margin(20)
                .maxTextRelativeWidth(0.7f)
                .build();

    }


    public static void toastLong(String text) {
        toasts.add(toastFactory.create(text, ClassToast.LONG));
    }

    public static void toastRich(String text, Color bgndColor, Color fontColor, float length) {
        toasts.add(toastFactory.create(text, length, bgndColor, fontColor));
    }

    public static void toastShort(String text) {
        toasts.add(toastFactory.create(text, ClassToast.SHORT));
    }


    public static void showToasts(Batch batch) {
        Iterator<ClassToast> it = toasts.iterator();
        while (it.hasNext()) {
            ClassToast t = it.next();
            if (!t.render(batch, Gdx.graphics.getDeltaTime())) {//float offsetX, float offsetY,
                it.remove(); // toast finished -> remove
            } else {
                break; // first toast still active, break the loop
            }
        }
    }
    //LEKREMOVER */



    private String msg;
    private final BitmapFont font;
    private float fadingDuration;
    private final Color fontColor;
    private final Color backgroundColor;
    //private SpriteBatch spriteBatch = new SpriteBatch();//LEK new SpriteBatch();//LEK
    public static ShapeRenderer renderer ;//LEK usei static

    private float opacity = 1f;
    private int toastWidth;
    private int toastHeight;
    private float timeToLive;
    private float positionX, positionY; // left bottom corner
    private float fontX, fontY; // left top corner
    private int fontWidth;
    private int fontHeight;

    ClassToast(
            String text,
            float length, //ClassToast.Length length,
            BitmapFont font,
            Color backgroundColor,
            float fadingDuration,
            float maxRelativeWidth,
            Color fontColor,
            //float positionX,
            float positionY,
            Integer customMargin
    ) {
        this.msg = text;
        this.font = font;
        this.fadingDuration = fadingDuration;
        this.positionY = positionY;
        //this.positionX = positionX;
        this.fontColor = fontColor;
        this.backgroundColor = backgroundColor;

        this.timeToLive = length; // length.duration;
        renderer.setColor(backgroundColor);

        // measure text box
        GlyphLayout layoutSimple = new GlyphLayout();
        layoutSimple.setText(this.font, text);

        int lineHeight = (int) layoutSimple.height;
        fontWidth = (int) (layoutSimple.width);
        fontHeight = (int) (layoutSimple.height);

        int margin = customMargin == null ? lineHeight * 2 : customMargin;

        float screenWidth = Gdx.graphics.getWidth();
        float maxTextWidth = screenWidth * maxRelativeWidth;
        if (fontWidth > maxTextWidth) {
            BitmapFontCache cache = new BitmapFontCache(this.font, true);
            GlyphLayout layout = cache.addText(text, 0, 0, maxTextWidth, Align.center, true);
            fontWidth = (int) layout.width;
            fontHeight = (int) layout.height;
        }

        toastHeight = fontHeight + 2 * margin;
        toastWidth = fontWidth + 2 * margin;

        //LEK CODIGO MUITO SUSPEITO, procure por positionX
        positionX = (screenWidth / 2) - toastWidth / 2;

        fontX = positionX + margin;
        fontY = positionY + margin + fontHeight;
    }

    /**
     * Displays toast<br/>
     * Must be called at the end of {@link Game#render()}<br/>
     * @param delta {@link Graphics#getDeltaTime()}
     * @return activeness of the toast (true while being displayed, false otherwise)
     */

    //long tempoAnterior; //float offsetX, float offsetY,
    public boolean render(Batch batch, float delta) {
        //LEK @Override
        //LEK public void draw(Batch batch, float parentAlpha) {
        //long tempoAtual=System.currentTimeMillis();
        //float delta = ((float)(tempoAtual - tempoAnterior))/1000.0f;
        //tempoAnterior = tempoAtual;
        timeToLive -= delta;

        if (timeToLive < 0) {
            return false;
            //LEK return;
        }

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        //renderer.setColor(0,0,0, 0.5f);
        renderer.setColor(backgroundColor);
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.arc(positionX, positionY + toastHeight / 2, toastHeight / 2, 90f, 180.0f);
        //renderer.circle(positionX, positionY + toastHeight / 2, toastHeight / 2);
        renderer.rect(positionX, positionY, toastWidth, toastHeight);
        //renderer.circle(positionX + toastWidth, positionY + toastHeight / 2, toastHeight / 2);
        renderer.arc(positionX + toastWidth, positionY + toastHeight / 2, toastHeight / 2, 270.0f, 180.2f);
        renderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

        //LEK spriteBatch.begin();

        batch.begin();
        if (timeToLive > 0 && opacity > 0.15) {
            if (timeToLive < fadingDuration) {
                opacity = timeToLive / fadingDuration;
            }

            font.setColor(fontColor.r, fontColor.g, fontColor.b, fontColor.a * opacity);
            //font.draw(batch, msg, fontX, fontY);//LEK , fontWidth, Align.center, true);
            font.draw(batch, msg, fontX, fontY, fontWidth, Align.center, true);
            //font.draw(batch, msg, 100, 100, fontWidth, Align.center, true);


        }
        batch.end();
        //LEK spriteBatch.end();

        return true;
        //return;
    }

    /**
     * Factory for creating toasts
     */
    public static class ClassToastFactory {

        private BitmapFont font;
        private Color backgroundColor = new Color(55f / 256, 55f / 256, 55f / 256, 1f);
        private Color fontColor = new Color(1, 1, 1, 1f);
        private float positionY;
        //private float positionX;
        private float fadingDuration = 0.5f;
        private float maxRelativeWidth = 0.65f;
        private Integer customMargin;

        private ClassToastFactory() {
            float screenHeight = Gdx.graphics.getHeight();
            float screenWidth = Gdx.graphics.getWidth();
            float bottomGap = 100;
            float leftGap = 100;
            positionY = bottomGap + ((screenHeight - bottomGap) / 10);
            //positionX = leftGap + ((screenWidth - leftGap) / 10);
        }

        /**
         * Creates new toast
         * @param text message
         * @param length toast duration
         * @return newly created toast
         */
        public ClassToast create(String text, float length) {
            return new ClassToast(
                    text,
                    length,
                    font,
                    backgroundColor,
                    fadingDuration,
                    maxRelativeWidth,
                    fontColor,
                    //positionX,
                    positionY,
                    customMargin);
        }

        public ClassToast create(String text, float length, Color backgroundColor, Color fontColor) {
            return new ClassToast(
                    text,
                    length,
                    font,
                    backgroundColor,
                    fadingDuration,
                    maxRelativeWidth,
                    fontColor,
                    //positionX,
                    positionY,
                    customMargin);
        }



        /**
         * Builder for creating factory
         */
        public static class Builder {

            private boolean built = false;
            private ClassToast.ClassToastFactory factory = new ClassToast.ClassToastFactory();


            /**
             * Specify font for toasts
             * @param font font
             * @return this
             */
            public ClassToast.ClassToastFactory.Builder font(BitmapFont font) {
                check();
                factory.font = font;
                return this;
            }

            /**
             * Specify background color for toasts.<br/>
             * Note: Alpha channel is not supported (yet).<br/>
             * Default: rgb(55,55,55)
             * @param color background color
             * @return this
             */
            public ClassToast.ClassToastFactory.Builder backgroundColor(Color color) {
                check();
                factory.backgroundColor = color;
                return this;
            }

            /**
             * Specify font color for toasts.<br/>
             * Default: white
             * @param color font color
             * @return this
             */
            public ClassToast.ClassToastFactory.Builder fontColor(Color color) {
                check();
                factory.fontColor = color;
                return this;
            }

            /**
             * Specify vertical position for toasts<br/>
             * Default: bottom part
             * @param positionY vertical position of bottom left corner
             * @return this
             */
            public ClassToast.ClassToastFactory.Builder positionY(float positionY) {
                check();
                factory.positionY = positionY;
                return this;
            }

            /**
             * Specify fading duration for toasts<br/>
             * Default: 0.5s
             * @param fadingDuration duration in seconds which it takes to disappear
             * @return this
             */
            public ClassToast.ClassToastFactory.Builder fadingDuration(float fadingDuration) {
                check();
                if (fadingDuration < 0) {
                    throw new IllegalArgumentException("Duration must be non-negative number");
                }
                factory.fadingDuration = fadingDuration;
                return this;
            }

            /**
             * Specify max text width for toasts<br/>
             * Default: 0.65
             * @param maxTextRelativeWidth max text width relative to screen (Eg. 0.5 = max text width is equal to 50% of screen width)
             * @return this
             */
            public ClassToast.ClassToastFactory.Builder maxTextRelativeWidth(float maxTextRelativeWidth) {
                check();
                factory.maxRelativeWidth = maxTextRelativeWidth;
                return this;
            }

            /**
             * Specify text margin for toasts<br/>
             * Default: line height
             * @param margin margin in px
             * @return this
             */
            public ClassToast.ClassToastFactory.Builder margin(int margin) {
                check();
                factory.customMargin = margin;
                return this;
            }

            /**
             * Builds factory
             * @return new factory
             */
            public ClassToast.ClassToastFactory build() {
                check();
                if (factory.font == null) {
                    throw new IllegalStateException("Font is not set");
                }
                built = true;
                return factory;
            }

            private void check() {
                if (built) {
                    throw new IllegalStateException("Builder can be used only once");
                }
            }
        }
    }

}

