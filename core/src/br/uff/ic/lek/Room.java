package br.uff.ic.lek;
import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.gdx.math.MathUtils;

import java.sql.Timestamp;

import java.util.ArrayList; 

/*
    Classe para interação com as salas

    Cada sala terá um limite de 4 jogadores, 
    uma indicação de quantos jogadores estão conectados, 
    um indicação se está cheia, 
    e uma referência para cada jogador conectado.

    Ao entrar no jogo, se o jogador não estiver conectado em uma sala, deve
    procurar uma sala disponivel e entrar nela.

    Se não houverem salas disponíveis o jogador criará uma sala nova e entrará nela.

    Deverá ser possível convidar um jogador para sua sala e 
    entrar na sala de um jogador por meio do convite, via notificação.

    quando a sala ficar vazia(ou outro gatilho)uma busca é feita por todas as salas e se tiverem vazias as elimina.

*/

public class Room {
    String roomID;
    Boolean isConnectedToARoom;
    ArrayList<String> connectedPlayers;
    Integer limit;
    Integer numberOfConnectedPlayers;
    Boolean isFull;
    
    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public void setConnectedStatus(Boolean isConnectedToARoom){
        this.isConnectedToARoom = isConnectedToARoom;
    }

    public Boolean getConnectedStatus(){
        return isConnectedToARoom;
    }

    public ArrayList<String> getConnectedPlayers() {
        return connectedPlayers;
    }

    public void setConnectedPlayers(ArrayList<String> connectedPlayers) {
        this.connectedPlayers = connectedPlayers;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getNumberOfConnectedPlayers() {
        return numberOfConnectedPlayers;
    }

    public void setNumberOfConnectedPlayers(Integer numberOfConnectedPlayers) {
        this.numberOfConnectedPlayers = numberOfConnectedPlayers;
    }

    public Boolean getFull() {
        return isFull;
    }

    public void setFull(Boolean full) {
        isFull = full;
    }
}
