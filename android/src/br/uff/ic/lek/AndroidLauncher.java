package br.uff.ic.lek;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.CRC32;

import br.uff.ic.lek.Alquimia;
// https://developers.google.com/android/guides/client-auth
// https://www.geeksforgeeks.org/create-and-add-data-to-firebase-firestore-in-android/
public class AndroidLauncher extends AndroidApplication {
	private static final String TAG = "JOGO";
	protected String playerNickName;
	protected String emailCRC32;
	protected String pwdCRC32;
	protected int runningTimes;
	protected String sharedPreferencesName = "ALCH0005";


	protected void defaultAccountGenerator(){
		/*
        TODO:
        A primeira vez que o usuário se logar é criado um auth via crc32
        do registrationTime,
        um gmail inicial
        (TODO fazer tela de atualização de dados do usuário para ele usar o próprio e-mail além de nome)
        Cria-se também a entrada no Firebase com authUID, chat, cmd (WAITING),
        email, registrationTime, lastUpdateTime e talvez timestamp.
        O usuário faz um login na primeira vez e fica com o valor até desinstalação
        com possibilidade de login com conta real
         */

		Date date = Calendar.getInstance().getTime();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String strDate = dateFormat.format(date);
		byte[] byteArray = strDate.getBytes();
		//byte[] bytes = text.getBytes("UTF-8");
		//String text = new String(bytes, "UTF-8");
		CRC32 crc32 = new CRC32();
		crc32.update(byteArray);
		long crc32long = crc32.getValue();

		// Exemplo:
		// I/System.out: construtor AndroidInterfaceClass Data:2021-06-28 07:00:07 crc32:2131264541
		// I/System.out: construtor AndroidInterfaceClass Data:2021-06-28 07:01:29 crc32:305835261
		// I/System.out: construtor AndroidInterfaceClass Data:2021-06-28 07:02:22 crc32:2539510427
        /*
        Com o valor long único a cada segundo e com espalhamento de 1 em 2^32 (4 bilhões)
        é possível criar um e-mail fake e único crc32long@gmail.com
        Com isso é possível autenticar usuários do jogo no Firebase
        sem precisar fazer uma interface de cadastro/coleta de email e senha
        esse email/senha precisa ser salvo localmente.
        Esse datetime de cadastro precisa ser armazenado no Firebase para poder limpar usuários
        antigos
        */
		// para que o email não seja só um número identifico
		// no email e na senha o pm ou periodic memory
		emailCRC32 = "alch" + crc32long + "@gmail.com";
		pwdCRC32 = "alch" + crc32long;
		playerNickName = "" + crc32long;//primeira aposta para apelido do jogador
		runningTimes = 0;

		// Fetching the stored data
		// from the SharedPreference
		// gerou a cada execução
		Log.d(TAG, "################ gen<"+playerNickName+">################");
		Log.d(TAG, "################ gen<"+pwdCRC32+">################");
		Log.d(TAG, "################ gen<"+emailCRC32+">################");
		SharedPreferences sh = getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
		String savedPlayerNickName = sh.getString("playerNickName", "");
		String savedPwdCRC32 = sh.getString("pwdCRC32", "");
		String savedEmailCRC32 = sh.getString("emailCRC32", "");
		runningTimes = sh.getInt("runningTimes", 0);
		if (AndroidInterfaceClass.debugFazPrimeiraVez || "".equals(savedPlayerNickName)) {// nao esta salvo
			// Creating a shared pref object with a file name sharedPreferencesName in private mode
			SharedPreferences sharedPreferences = getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
			SharedPreferences.Editor myEdit = sharedPreferences.edit();

			// write all the data entered by the user in SharedPreference and apply
			myEdit.putString("playerNickName", playerNickName);
			myEdit.putString("emailCRC32", emailCRC32);
			myEdit.putString("pwdCRC32", pwdCRC32);
			runningTimes = 1;// primeira vez
			myEdit.putInt("runningTimes", new Integer(runningTimes));
			myEdit.apply();
			savedPlayerNickName=playerNickName;
			savedPwdCRC32= pwdCRC32;
			savedEmailCRC32=emailCRC32;

			Log.d(TAG, "################ gravando ################");
		} else {
			playerNickName=savedPlayerNickName;
			pwdCRC32= savedPwdCRC32;
			emailCRC32=savedEmailCRC32;
			SharedPreferences sharedPreferences = getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
			SharedPreferences.Editor myEdit = sharedPreferences.edit();
			myEdit.putInt("runningTimes", new Integer(++runningTimes));
			myEdit.apply();
		}
		Log.d(TAG, "################ saved<"+playerNickName+">################");
		Log.d(TAG, "################ saved<"+pwdCRC32+">################");
		Log.d(TAG, "################ saved<"+emailCRC32+">################");
		Log.d(TAG, "################ run "+runningTimes+" X ################");
	}



	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		defaultAccountGenerator();
		Log.d(TAG, "################ playerNickName=<"+playerNickName+">################");

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		config.useWakelock = true;
		config.useAccelerometer = true;
		useImmersiveMode (true);
		initialize(new Alquimia(new AndroidInterfaceClass(playerNickName, emailCRC32, pwdCRC32, runningTimes)), config);
	}
}
