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
A InterfaceLibGDX tem o papel de casar a lógica de uma aplicação Java Android - Firebase
com um jogo (core) LibGDX. Como só o código android recebe mensagens do Firebase
caso haja mudanças no conteúdo das coleções assinadas é necessário que um método para
recepção dessas mensagens esteja definido por contrato para que possa ser tratado.
Esse tratamento é feito por uma thread que vai enfileirando as mensagens recebidas, pois nem sempre
é possível seu tratamento imediato.
 */
package br.uff.ic.lek;

public interface InterfaceLibGDX {
    void enqueueMessage(String querySource, String registrationTime, String authUID, String cmd, String lastUpdateTime);
    void parseCmd(String authUID, String cmd);
    String MY_PLAYER_DATA = "MY_PLAYER_DATA";
    String ALL_PLAYERS_DATA = "ALL_PLAYERS_DATA";
}
