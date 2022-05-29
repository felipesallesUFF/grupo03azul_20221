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
package br.uff.ic.lek;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.gdx.math.MathUtils;

import java.sql.Timestamp;

/*
Ao gravar no inicio os dados de um jogador, gero e guardo
o timestamp (equivalente ao "now") do Firebase.
De posse desse valor e do tempo inicial ao capturar localmente
eu tenho como estimar e guardar com boa segurança o tempo
no servidor via cálculo:
 tempo estimado do servidor = "now obtido" + (tempoAtualLocal - tempoInicialLocal)

Cada jogador tem um número aleatório que é permamentemente gerado
pelo software a cada inicialização e atualizado no servidor.
Esse número permite
que o jogador resultante de uma query (de até 10 players disponíveis)
escolha seu match segundo a seguinte lógica:
partida será realizada entre os jogadores com
min(ABS(numPlayer1 - numPlayer2))
jogador com numPlayer maior faz o convite e estabelece o match
e o outro começa a partida (faz a 1a jogada).
Dessa forma, fica fácil para Player1 e Player2 saberem que a lógica
da partida dar-se-á entre esses dois e não outro.
Naturalmente, há o risco de entrar um jogador
novo entre a query de Player1 e Player2, mas o timeout, a não resposta
avisa ao Player1 que Player2 não quer jogar ou decidiu jogar com outro
Na falta de jogadores, entra a mensagem "quer esperar por jogadores
ou jogar com um robot?"
 */
public class PlayerData {
    // veja https://firebase.google.com/docs/database/android/read-and-write
    String authUID;// UID do usuario registrado, chave da coleção
    String writerUID;// UID do usuario que escreveu, pode ser ele próprio ou outro jogador
    States gameState;
    String chat;
    String cmd;
    String email;
    String playerNickName;
    String lastUpdateTime;
    String registrationTime;
    String stateAndLastTime;
    int runningTimes;
    String avatarType;

    public String getAvatarType() {
        return avatarType;
    }

    public void setAvatarType(String avatarType) {
        this.avatarType = avatarType;
    }

    public String getStateAndLastTime() {
        return stateAndLastTime;
    }

    public void setStateAndLastTime(String stateAndLastTime) {
        this.stateAndLastTime = stateAndLastTime;
    }

    public int getRandomNumber() {
        return randomNumber;
    }

    public void setRandomNumber(int randomNumber) {
        this.randomNumber = randomNumber;
    }

    int randomNumber;

    private static PlayerData pd=null;
    private PlayerData(){ // singleton
    }
    public static PlayerData myPlayerData(){
        if (PlayerData.pd == null){
            MathUtils.random.setSeed(System.currentTimeMillis());
            PlayerData.pd = new PlayerData();
        }
        PlayerData.pd.randomNumber = MathUtils.random(1, Integer.MAX_VALUE);
        return PlayerData.pd; // singleton = sempre a mesma instancia
    }

    public int getRunningTimes() {
        return runningTimes;
    }

    public void setRunningTimes(int runningTimes) {
        this.runningTimes = runningTimes;
    }

    Timestamp timestamp;
    public enum States {
        WAITING,
        READYTOPLAY,
        LETSPLAY,
        PLAYING
    };

    public String getWriterUID() {
        return writerUID;
    }

    public void setWriterUID(String writerUID) {
        this.writerUID = writerUID;
    }

    public States getGameState() {
        return gameState;
    }

    public void setGameState(States gameState) {
        this.gameState = gameState;
    }

    public String getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(String registrationTime) {
        this.registrationTime = registrationTime;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAuthUID() {
        return authUID;
    }

    public void setAuthUID(String authUID) {
        this.authUID = authUID;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getChat() {
        return chat;
    }

    public void setChat(String chat) {
        this.chat = chat;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public void setPlayerNickName(String playerNickName) {
        this.playerNickName = playerNickName;
    }

    public String getPlayerNickName() {
        return playerNickName;
    }
}
