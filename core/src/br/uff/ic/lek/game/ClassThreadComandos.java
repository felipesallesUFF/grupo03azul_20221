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
A ClassThreadComandos junto com a InterfaceLibGDX tem o papel de casar a lógica de uma aplicação Java Android - Firebase
com um jogo (core) LibGDX. Como só o código android recebe mensagens do Firebase
caso haja mudanças no conteúdo das coleções assinadas é necessário que um método para
recepção dessas mensagens esteja definido por contrato para que possa ser tratado.
Esse tratamento é feito por uma thread que vai enfileirando as mensagens recebidas, pois nem sempre
é possível seu tratamento imediato.
 */
package br.uff.ic.lek.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import br.uff.ic.lek.Alquimia;
import br.uff.ic.lek.InterfaceAndroidFireBase;
import br.uff.ic.lek.InterfaceLibGDX;
import br.uff.ic.lek.PlayerData;
import br.uff.ic.lek.actors.Avatar;
import br.uff.ic.lek.utils.ClassToast;

public class ClassThreadComandos extends Thread implements InterfaceLibGDX {
    public static final ConcurrentLinkedQueue<ClassComandos> filaComandos = new ConcurrentLinkedQueue<ClassComandos>();
    public static ClassThreadComandos produtorConsumidor;
    // Thread para processar as mensagens que entraram numa fila
    private int contador =0;
    private final int lineNumber=1;

    // é nesse objeto Android FireBase que os métodos de Android
    // e de controle do FireBase residem e onde são invocados
    public static InterfaceAndroidFireBase objetoAndroidFireBase;
    public static Alquimia screenJogo; // a tela principal do jogo
    public ClassThreadComandos(Alquimia screenJogo){
        ClassThreadComandos.screenJogo = screenJogo;

        // aqui ficam os métodos que a porção Android FireBase vão chamar
        // para comunicação com o jogo. Por exemplo o colocar
        // mensagens na fila para serem processados.
        // Por isso, a Interface InterfaceLibGDX é implementada aqui (this)
        ClassThreadComandos.objetoAndroidFireBase.setLibGDXScreen(this);
        this.start(); // começa a Thread
    }

    @Override
    public void run(){

        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!filaComandos.isEmpty()) {
                ClassComandos elementoFilaComandos = filaComandos.poll();
                if (elementoFilaComandos != null) {
                    processaCmd(elementoFilaComandos);
                }
            } else {
                contador++;
                if (contador % 100 == 0) // 10segundos
                    System.out.println("idle "+ contador);
            }
        }
    }


    public void processaCmd(ClassComandos elementoFilaComandos) {

        // TODO:LEK
        String cmd = elementoFilaComandos.getCmd();
        // TODO: pegar os outros dados que chegam no comando
        String querySource = elementoFilaComandos.getQuerySource();

        if (querySource == InterfaceLibGDX.MY_PLAYER_DATA) {
            Color backgroundColor = new Color(1f, 1f, 1f, 0.9f);
            Color fontColor = new Color(1, 0, 0, 0.9f);
            ClassToast.toastRich(
                    querySource + " " + cmd,
                    backgroundColor, fontColor, ClassToast.LONG);

            /*
            O que preciso fazer aqui? Definir quais comandos do jogo
            e disparar o parsing de comandos do próprio jogador e dos demais
            Ex:
            {cmd:MOVETO,px:500.0,py:1000.0,pz:0,cardNumber:4,uID:5QLRDT7kUTYpWBEMlxfbUjHNRjI3}

            this.player.setX(avatarStartTileX*World.tileWidth);
            this.player.setY(avatarStartTileY*World.tileHeight);
            */
        } else { // InterfaceLibGDX.ALL_PLAYERS_DATA
            Color backgroundColor = new Color(0f, 0f, 0f, 0.9f);
            Color fontColor = new Color(1, 1, 1, 0.9f);
            ClassToast.toastRich(
                    querySource + " " + cmd,
                    backgroundColor, fontColor, ClassToast.LONG);
        }

        parseCmd(elementoFilaComandos.getAuthUID(), cmd);


        // recebemos informacao vinda de
        //
        // InterfaceLibGDX.MY_PLAYER_DATA
        // InterfaceLibGDX.ALL_PLAYERS_DATA
        //
        // lembrando que dados do próprio player também chegam via
        // InterfaceLibGDX.ALL_PLAYERS_DATA e devem ser possivelmente ignorados


        //{nome:MOVETO,px:500.5,py:500.2,teste:789}
        // TODO: mensagens iniciais e processar comandos básicos
        // da sala de espera de jogos
        /*
        ClassMessagePos gp = new ClassMessagePos("MOVETO", 500.5f, 500.2f, 789f);
        String json = ClassMessage.encodeCurrentPos(gp);
        System.out.println(cmd+ " json->"+json+"<-");
        Object obj = ClassMessage.decodeCurrentPos(ClassMessagePos.class, json);
        System.out.println(
                "@@@@@@@classe "+((ClassMessage) obj).getClssName()+
                        " class "+((ClassMessage) obj).getClss()+
                        " objeto "+((ClassMessage) obj).getNome()+
                        " x="+((ClassMessage) obj).getPx()+
                        " y="+((ClassMessage) obj).getPy() );

        */

        // json parser
        //ClassAnimation.disparaAnimacao(worldCoordinates.x, worldCoordinates.y);
        //String msg = "centro x="+worldCoordinates.x+ " y="+worldCoordinates.y;
        //ClassMessage gp = new ClassMessage("lek", 200.5f, 300.2f);
        /*

        // TESTE DE ENCODER / DECODER
        // enviando ClassMessagePos e recebendo

        ClassMessagePos gp = new ClassMessagePos("gmp", 200.5f, 300.2f, 789f);
        String json = ClassMessage.encodeCurrentPos(gp);
        System.out.println(cmd+ " json->"+json+"<-");
        Object obj = ClassMessage.decodeCurrentPos(ClassMessagePos.class, json);
        System.out.println(
                "@@@@@@@classe "+((ClassMessage) obj).getClssName()+
                " class "+((ClassMessage) obj).getClss()+
                " objeto "+((ClassMessage) obj).getNome()+
                " x="+((ClassMessage) obj).getPx()+
                " y="+((ClassMessage) obj).getPy() );
        // se eu sei que o json é da classe ClassMessagePos.class
        System.out.println("***** obj GMP "+((ClassMessagePos) obj).getTeste());

        // ideia possível : mando uma mensagem com a classe do objeto
        // em seguida gero o objeto daquela subclasse de ClassMessage
        String json2 = json;
        Object obj2 = ClassMessage.decodeCurrentPos(((ClassMessage) obj).getClss(), json2);
        System.out.println("***** obj GMP2 "+((ClassMessagePos) obj).getTeste());

         */

        //ClassAnimation.disparaAnimacao(worldCoordinates.x, worldCoordinates.y);
    }


    // Por meio desse método os comandos que chegam do Firebase são enfileirados
    @Override
    public void enqueueMessage(String querySource, String registrationTime, String authUID, String cmd, String lastUpdateTime) {
        System.out.println("chegou msg do FireBase module " + " registrationTime=" + registrationTime + " authUID=" + authUID + " cmd=" + cmd);
        PlayerData pd = PlayerData.myPlayerData();
        //if (authUID.equals(pd.authUID) == false) { // vou ignorar e enviar os dois tipos e no parsing
        //    ClassComandos elementoFilaComandos = new ClassComandos(querySource, registrationTime, authUID, cmd, lastUpdateTime);
        //    ClassThreadComandos.filaComandos.add(elementoFilaComandos);
        //} else {
        ClassComandos elementoFilaComandos = new ClassComandos(querySource, registrationTime, authUID, cmd, lastUpdateTime);
        ClassThreadComandos.filaComandos.add(elementoFilaComandos);
        // TODO:LEK
        //System.out.println(querySource+" mensagem de minha própria alteração não é colocada na fila de eventos a processar");
        //}
        // cmds consome as mensagens, preciso de uma Thread para processar
    }

    @Override
    public void parseCmd(String authUID, String cmdJson)  {
        Object obj = ClassMessage.decodeCurrentPos(ClassMessage.class, cmdJson);
        cmdJson = cmdJson.substring(1, cmdJson.length()-1);
        String[] keyValuePairs = cmdJson.split(",");

        HashMap<String, String> cmdHashMap = new HashMap<String, String>();

        for(String pair : keyValuePairs)
        {
            String[] entry = pair.split(":");
            cmdHashMap.put(entry[0].trim(), entry[1].trim());
        }

        String realCmd = cmdHashMap.get("cmd");


        if (!authUID.equals(PlayerData.myPlayerData().getAuthUID())){
            //Criar Avatar
            ArrayList<Avatar> newAvatars = World.world.getAvatars();
            Boolean jaFoiCriado = false;

            for (int i = 0; i < newAvatars.size(); i++) {
                if(newAvatars.get(i).getAuthUID().equals(authUID)){
                    jaFoiCriado = true;
                    break;
                }
            }

            float x = Float.parseFloat(cmdHashMap.get("px"));
            float y = Float.parseFloat(cmdHashMap.get("py"));

            System.out.println("Parsing cmd");
            System.out.println(cmdHashMap);
            System.out.println(String.valueOf(x));
            System.out.println(String.valueOf(y));

            Avatar novoPlayer = new Avatar(new Sprite(World.atlasPlayerS_W_E_N.findRegion("South02")), x, y, authUID);

            if(!jaFoiCriado){
                System.out.println("Criou um avatar novo!");
                newAvatars.add(novoPlayer);
                World.world.setAvatars(newAvatars);
            }


//            World.world.worldController.comandoMoveTo(x,y);

            Avatar avatar = World.world.getAvatar();

            ArrayList<Avatar> avatarsArray = World.world.getAvatars();
            for (int i = 0; i < avatarsArray.size(); i++) {
                if(avatarsArray.get(i).getAuthUID().equals(authUID)){
                    avatar = avatarsArray.get(i);
                }
            }

            System.out.println("Setting position of " + avatar.getAuthUID());
            System.out.println("Message ID: " + authUID);
            avatar.setX(x);
            avatar.setY(y);
        }

    }
}
