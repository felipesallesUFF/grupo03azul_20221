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

import com.badlogic.gdx.ApplicationListener;

import br.uff.ic.lek.game.ClassThreadComandos;
import br.uff.ic.lek.screens.SplashScreen;
import br.uff.ic.lek.screens.Menu;
import br.uff.ic.lek.game.World;
import com.badlogic.gdx.Game;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Alquimia extends Game  implements ApplicationListener{

	//public SpriteBatch batch;
	//public BitmapFont font;

	public Alquimia(InterfaceAndroidFireBase objetoAndroidFireBase){
		ClassThreadComandos.objetoAndroidFireBase = objetoAndroidFireBase;
		// Thread para receber os comandos do Firebase
		ClassThreadComandos.produtorConsumidor = new ClassThreadComandos(this);
		//objetoAndroidFireBase.chooseSpecificRoom("-N6HHQpMSWjuze8AhRyq");
		//objetoAndroidFireBase.searchForAvailableRooms();
		//objetoAndroidFireBase.disconnectFromRoom();
	}

	public Alquimia() {
		// usado pelo Desktop que não faz nada
	}

	@Override
	public void create() {
		//batch = new SpriteBatch();
		//A fonte usada será Arial(padrão da LibGDX)
		//font = new BitmapFont();
		this.setScreen(new Menu(this));//começando com o menu primeiro
		//this.setScreen(new SplashScreen());
	}
	public void render() {
		super.render();
	}


	@Override
	public void dispose() {
		//batch.dispose();
		//font.dispose();

		super.dispose();
		World.dispose();
	}

}
