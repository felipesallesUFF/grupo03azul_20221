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

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

public class ClassMessage {
    private String cmd;
    private float px;
    private float py;
    private float pz;
    private String uID;

    private int cardNumber;


    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public int getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(int cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Class clss;
    private String clssName;

    public Class getClss() {
        return clss;
    }

    public void setClss(Class clss) {
        this.clss = clss;
    }

    public String getClssName() {
        return clssName;
    }

    public void setClssName(String clssName) {
        this.clssName = clssName;
    }



    public ClassMessage(String cmd,float px, float py, float pz, int cardNumber) {
        this.cmd  = cmd ;
        this.px = px;
        this.py = py;
        this.pz = pz;
        this.cardNumber = cardNumber;
        this.clss = ClassMessage.class;
        this.clssName = "ClassMessage";
    }

    public ClassMessage() {
        this.clss = ClassMessage.class;
        this.clssName = "ClassMessage";
    }

    public String getCmd() {
        return cmd ;
    }

    public void setCmd(String cmd) {
        this.cmd  = cmd ;
    }

    public float getPx() {
        return px;
    }
    public void setPx(float px) {
        this.px = px;
    }
    public float getPy() {
        return py;
    }
    public void setPy(float py) {
        this.py = py;
    }
    public float getPz() {
        return pz;
    }
    public void setPz(float pz) {
        this.pz = pz;
    }

    public static String encodeCurrentPos(ClassMessage obj){
        //Use Libgdx's Json class.
        Json jsonParser = new Json();
        //In this case the object class is a Level, so we use a tag to prevent it
        // using the full class path (uk.co.company.game.gameobjects.Level)
        //jsonParser.addClassTag("ClassMessage", ClassMessage.class);
        // supostamente todos podem ser subclasse de ClassMessage
        jsonParser.addClassTag(obj.getClssName(), obj.getClss());
        //Here's where the actual conversion takes place
        String myJSON = jsonParser.toJson(obj);
        System.out.println("dentro encode: "+myJSON);
        return myJSON;
    }

    // read these references below
    //https://www.codota.com/code/java/classes/javax.json.stream.JsonParser
    //https://www.fairlyusefulcode.co.uk/post/libgdx-objects-and-json/
    //https://xoppa.github.io/blog/loading-models-using-libgdx/
    //https://jackyjjc.wordpress.com/2013/10/07/parsing-json-in-libgdx-tutorial/
    //
    //https://github.com/czyzby/gdx-skins
    //https://www.youtube.com/watch?v=uIPAaDslhPM  MP Game with NodeJS


    public static Object decodeCurrentPos(Class clss, String json){
        // Como descobrir a classe que originou esse json
        // para fazer o correto decoding?

        Json jsonParser = new Json();
        jsonParser.setTypeName("class");
        jsonParser.setUsePrototypes(false);
        jsonParser.setIgnoreUnknownFields(true);
        jsonParser.setOutputType(JsonWriter.OutputType.json);
        jsonParser.addClassTag("ClassMessage", ClassMessage.class);
        //Here where fromJSON converts back to the object that we specify

        // aqui eu preciso saber a classe
        Object obj = jsonParser.fromJson(clss, json);
        return obj;
    }
}
