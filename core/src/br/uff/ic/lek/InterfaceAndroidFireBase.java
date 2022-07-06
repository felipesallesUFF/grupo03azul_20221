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

public interface InterfaceAndroidFireBase {
    void setLibGDXScreen(InterfaceLibGDX libGDXScreen);
    void sincronizeLocalRoom();
    void searchForAvailableRooms();
    void disconnectFromRoom();
    void writePlayerData();
    void waitForMyMessages();
    void waitForPlayers();
    void finishAndRemoveTask();

}
