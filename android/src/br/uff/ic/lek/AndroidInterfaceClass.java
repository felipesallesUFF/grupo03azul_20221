package br.uff.ic.lek;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Query;

// para espelhar a tela de seu celular no Ubuntu
// https://diolinux.com.br/tutoriais/espelhe-tela-do-seu-android-no-seu-linux-com-o-scrcpy.html
public class AndroidInterfaceClass extends Activity implements InterfaceAndroidFireBase {
    public static final boolean debugFazPrimeiraVez = false;
// 0dc3nzCSCyP8rxpMY8pz7s83Q3F2
    FirebaseDatabase database;
    DatabaseReference myRef;
    DatabaseReference myRefInicial;
    DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
    String providerID = ""; // firebase é o default email/senha
    public String uID = "";
    String email = "";
    String playerNickName = "";
    int runningTimes = 0;
    String pwd = "";
    boolean newAccount;

    // exemplo vindo do Firebase
    private static final String TAG = "JOGO";
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            reload();
        }
    }
    // [END on_start_check_user]

    private void createAccount(String email, String password) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            updateUI(currentUser);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            //Toast.makeText(AndroidInterfaceClass.this, "Authentication failed.",
                            //        Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        // [END create_user_with_email]
    }

    private void signIn(String email, String password) {
        // [START sign_in_with_email]
        System.out.println("*********************************");
        System.out.println("***** "+email+" ***** "+password);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success"+email+" "+password);
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            updateUI(currentUser);
                        } else {
                            System.out.println("*********************************");
                            System.out.println("***** "+email+" ***** "+password);
                            // If sign in fails, display a message to the user.
                            Log.d(TAG, "signInWithEmail:failure"+email+" "+password, task.getException());
                            updateUI(null);
                        }
                    }
                });
        // [END sign_in_with_email]
    }

    private void sendEmailVerification() {
        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Email sent
                    }
                });
        // [END send_email_verification]
    }

    private void reload() {
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser == null) return;
        providerID = currentUser.getProviderId();
        uID = currentUser.getUid();
        email = currentUser.getEmail();
        Log.d(TAG, "updateUI providerID:" + providerID + " uID:" + uID + " email:" + email);
        currentUserDefined(currentUser);
        updateRealTimeDatabaseUserData(currentUser);
        //keMlXPeet4XQzdZkA400lmyUIP42
        //ope0WrkFnXXJYC7UgcLTHplAYHA3
        //yM08CO231ZYkmjDwAC85v9I6SLn1
        //6tMCwdJrM8U1ZzLip6j3LMI6DHi1
    }


    private void currentUserDefined(FirebaseUser currentUser) {
        if (currentUser != null) {
            email = currentUser.getEmail();
            try {
                final String before = email.split("@")[0]; // "Before"
                playerNickName = before;//outra aposta para apelido do jogador
                //final String after = email.split("@")[1]; // "After"
            } catch (Exception e) {
                Log.d(TAG, "nao encontrou @");
            }
            Log.d(TAG, "playerNickName:" + playerNickName);
            uID = currentUser.getUid();
            providerID = currentUser.getProviderId();
            Log.d(TAG, "currentUser " + email + " " + providerID);
            String original = email;
            String _pwd = original.replace("@gmail.com", "");
            pwd = _pwd;
            Log.d(TAG, "Use SQLite to save email=" + email + " pwd=" + pwd + " uID=" + uID);
        } else {
            Log.d(TAG, "currentUser eh null");
        }
    }

    public AndroidInterfaceClass(String playerNickName, String emailCRC32, String pwdCRC32, int runningTimes) {
        this.playerNickName = playerNickName;
        this.runningTimes = runningTimes;
        Log.d(TAG, "construtor AndroidInterfaceClass execucoes:" +runningTimes+ " playerNickName="+playerNickName+" emailCRC32="+emailCRC32+" pwdCRC32="+pwdCRC32);

        //FirebaseAuth.getInstance().signOut();// comentar
        // para que o email não seja só um número identifico
        // no email e na senha o pm ou periodic memory
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (AndroidInterfaceClass.debugFazPrimeiraVez || currentUser == null) {
            try {
                // se nao há usuario logado
                // crio usuario com gmail gerado nao validado
                //emailCRC32 = "laurokozovits@gmail.com";
                //emailCRC32 = "professorkozovits@gmail.com";
                //pwdCRC32 = "senha123";
                //y12P41OWOlfrqfFYBMxjtEZmsOs1


                this.createAccount(emailCRC32, pwdCRC32);
                Log.d(TAG, "criou um novo auth com email:" + emailCRC32 + " pwd:" + pwdCRC32);
                this.signIn(emailCRC32, pwdCRC32);
                // signIn faz efeito na próxima execução
                // para remover é preciso desinstalar e desligar o telefone
                newAccount = true;
            } catch (Exception e) {
                Log.d(TAG, "Exception signIn " + e.getMessage());
            }
        } else {
            newAccount=false;
        }

        /*
        try {
            // laurokozovits@gmail.com Exemplo123
            //this.signIn("773394836@gmail.com", "773394836");
            //this.signIn("3290544244@gmail.com", "3290544244");
            // signIn faz efeito na próxima execução estou usando no emulador pixel4
            // para remover é preciso desinstalar e desligar o telefone
            this.signIn("laurokozovits@gmail.com", "Exemplo123");
            playerNickName = "laurokozovits" ;//outra aposta para apelido do jogador

        } catch (Exception e){
            Log.d(TAG, "Exception signIn "+e.getMessage());
        }
        */


        // verificacao de e-mail
        //this.sendEmailVerification();

        //How to store data locally in an Android app
        //https://www.androidauthority.com/how-to-store-data-locally-in-android-app-717190/

        currentUserDefined(currentUser);

        updateRealTimeDatabaseUserData(currentUser);

        /*
        Calendar cal = Calendar.getInstance();
        Log.d(TAG,"Current time is :" + cal.getTime());
        cal.setTimeInMillis(5000);
        Log.d(TAG,"After setting Time: " + cal.getTime());
        */

        /*
        if (waitingForTheFirstTime==false) {
            waitingForTheFirstTime = true;
            // pesquisa de valores
            // ATENCAO
            // se players...lastUpdateTime não existir não será ordenado, mas funciona
            // se a child players nao existir, mas for criada posteriormente
            // a tarefa de escutar (listener) vai funcionar normalmente
            int ultimosUsuarios = 3;
            DatabaseReference players = referencia.child("players");
            // se você precisar de muitos campos de pesquisa, refaça a estrutura
            // para criar um único campo composto da união de outros
            // https://stackoverflow.com/questions/33336697/nosql-database-design-for-queries-with-multiple-restrictions-firebase
            Query playerPesquisa = players.startAt("WAITING_-").endAt("WAITING_~").orderByChild("stateAndLastTime").limitToLast(ultimosUsuarios);
            // see that: https://stackoverflow.com/questions/39076284/firebase-orderbykey-with-startat-and-endat-giving-wrong-results

            // ORDEM CRESCENTE: o último a chegar é o mais recente
            // Unfortunately firebase doesn't allow returning by descending order
            //
            // see SQL vs FIREBASE https://www.youtube.com/watch?v=sKFLI5FOOHs&list=PLl-K7zZEsYLlP-k-RKFa7RyNPa9_wCH2s&index=5
            playerPesquisa.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot zoneSnapshot : dataSnapshot.getChildren()) {

                        Log.d(TAG, "listener dos " + ultimosUsuarios + " users ordem lastUpdateTime " + zoneSnapshot.child("cmd").getValue() + " " + zoneSnapshot.child("lastUpdateTime").getValue());
                        // TODO chamar funcao de interface implementada por LIBGDX e setada aqui
                        //if ()
                        //AndroidInterfaceClass.gameLibGDX.exibeLa(msg);
                        if (AndroidInterfaceClass.gameLibGDX != null) {
                            String registrationTime = "" + zoneSnapshot.child("registrationTime").getValue();
                            String authUID = "" + zoneSnapshot.child("authUID").getValue();
                            String cmd = "" + zoneSnapshot.child("cmd").getValue();
                            String lastUpdateTime = "" + zoneSnapshot.child("lastUpdateTime").getValue();

                            // pegar a data do update para eliminar o processamento em dobro ou mensagens duplicadas
                            //TODO:LEK
                            // Qual e´o problema a ser resolvido
                            // quando entra um novo usuario
                            // aparece as mensagens de todos os players
                            // multiplicados por 4
                            // se ha´2 users x4 --> 8 mensagens ou 4 pares
                            // se ha 3 users x4 --> 12 mensagens ou 4 trios
                            // a ideia eh eliminar o processamento de mensagens iguais
                            // a cada WAITING processar só as 10 mais
                            // recentes
                            AndroidInterfaceClass.gameLibGDX.enqueueMessage(InterfaceLibGDX.ALL_PLAYERS_DATA, registrationTime, authUID, cmd, lastUpdateTime);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.i(TAG, "playerPesquisa.addValueEventListener onCancelled", databaseError.toException());
                }
            });

        }

         */
    }
    public boolean waitingForTheFirstTime = false;



    @Override
    public void waitForMyMessages() {
        int ultimosUsuarios = 10;

        // ATENCAO criar as regras do Realtime Database no Firebase

        DatabaseReference players = referencia.child("players");
        // se você precisar de muitos campos de pesquisa, refaça a estrutura
        // para criar um único campo composto da união de outros
        // https://stackoverflow.com/questions/33336697/nosql-database-design-for-queries-with-multiple-restrictions-firebase
        Query playerPesquisa = players.startAt(uID).endAt(uID).orderByChild("authUID").limitToLast(ultimosUsuarios);
        // see that: https://stackoverflow.com/questions/39076284/firebase-orderbykey-with-startat-and-endat-giving-wrong-results

        // ORDEM CRESCENTE: o último a chegar é o mais recente
        // Unfortunately firebase doesn't allow returning by descending order
        //
        // see SQL vs FIREBASE https://www.youtube.com/watch?v=sKFLI5FOOHs&list=PLl-K7zZEsYLlP-k-RKFa7RyNPa9_wCH2s&index=5
        playerPesquisa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot zoneSnapshot : dataSnapshot.getChildren()) {

                    Log.d(TAG, "UID listener dos " + ultimosUsuarios + " users ordem lastUpdateTime " + zoneSnapshot.child("cmd").getValue() + " " + zoneSnapshot.child("lastUpdateTime").getValue());
                    // TODO chamar funcao de interface implementada por LIBGDX e setada aqui
                    //if ()
                    //AndroidInterfaceClass.gameLibGDX.exibeLa(msg);
                    if (AndroidInterfaceClass.gameLibGDX != null) {
                        String registrationTime = "" + zoneSnapshot.child("registrationTime").getValue();
                        String authUID = "" + zoneSnapshot.child("authUID").getValue();
                        String cmd = "" + zoneSnapshot.child("cmd").getValue();
                        String lastUpdateTime = "" + zoneSnapshot.child("lastUpdateTime").getValue();

                        // pegar a data do update para eliminar o processamento em dobro ou mensagens duplicadas
                        //TODO:LEK
                        // Qual e´o problema a ser resolvido
                        // quando entra um novo usuario
                        // aparece as mensagens de todos os players
                        // multiplicados por 4
                        // se ha´2 users x4 --> 8 mensagens ou 4 pares
                        // se ha 3 users x4 --> 12 mensagens ou 4 trios
                        // a ideia eh eliminar o processamento de mensagens iguais
                        // a cada WAITING processar só as 10 mais
                        // recentes
                        AndroidInterfaceClass.gameLibGDX.enqueueMessage(InterfaceLibGDX.MY_PLAYER_DATA, registrationTime, authUID, cmd, lastUpdateTime);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "playerPesquisa.addValueEventListener onCancelled", databaseError.toException());
            }
        });
    }


    @Override
    public void waitForPlayers(){
        int ultimosUsuarios = 10;
        DatabaseReference players = referencia.child("players");
        // se você precisar de muitos campos de pesquisa, refaça a estrutura
        // para criar um único campo composto da união de outros
        // https://stackoverflow.com/questions/33336697/nosql-database-design-for-queries-with-multiple-restrictions-firebase
        Query playerPesquisa = players.startAt("READYTOPLAY_-").endAt("READYTOPLAY_~").orderByChild("stateAndLastTime").limitToLast(ultimosUsuarios);
        // see that: https://stackoverflow.com/questions/39076284/firebase-orderbykey-with-startat-and-endat-giving-wrong-results

        // ORDEM CRESCENTE: o último a chegar é o mais recente
        // Unfortunately firebase doesn't allow returning by descending order
        //
        // see SQL vs FIREBASE https://www.youtube.com/watch?v=sKFLI5FOOHs&list=PLl-K7zZEsYLlP-k-RKFa7RyNPa9_wCH2s&index=5
        playerPesquisa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot zoneSnapshot : dataSnapshot.getChildren()) {

                    Log.d(TAG, "listener dos " + ultimosUsuarios + " users ordem lastUpdateTime " + zoneSnapshot.child("cmd").getValue() + " " + zoneSnapshot.child("lastUpdateTime").getValue());
                    // TODO chamar funcao de interface implementada por LIBGDX e setada aqui
                    //if ()
                    //AndroidInterfaceClass.gameLibGDX.exibeLa(msg);
                    if (AndroidInterfaceClass.gameLibGDX != null) {
                        String registrationTime = "" + zoneSnapshot.child("registrationTime").getValue();
                        String authUID = "" + zoneSnapshot.child("authUID").getValue();
                        String cmd = "" + zoneSnapshot.child("cmd").getValue();
                        String lastUpdateTime = "" + zoneSnapshot.child("lastUpdateTime").getValue();

                        // pegar a data do update para eliminar o processamento em dobro ou mensagens duplicadas
                        //TODO:LEK
                        // Qual e´o problema a ser resolvido
                        // quando entra um novo usuario
                        // aparece as mensagens de todos os players
                        // multiplicados por 4
                        // se ha´2 users x4 --> 8 mensagens ou 4 pares
                        // se ha 3 users x4 --> 12 mensagens ou 4 trios
                        // a ideia eh eliminar o processamento de mensagens iguais
                        // a cada WAITING processar só as 10 mais
                        // recentes
                        AndroidInterfaceClass.gameLibGDX.enqueueMessage(InterfaceLibGDX.ALL_PLAYERS_DATA, registrationTime, authUID, cmd, lastUpdateTime);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(TAG, "playerPesquisa.addValueEventListener onCancelled", databaseError.toException());
            }
        });


    }


    @Override
    public void writePlayerData(){
        Log.d(TAG, "******** writePlayerData");
        // definir seu dados no Realtime Database com dados de usuario logado
        PlayerData pd = PlayerData.myPlayerData();// singleton
        pd.setAuthUID(uID);
        pd.setWriterUID(uID);
        pd.setGameState(PlayerData.States.READYTOPLAY);
        pd.setChat("empty"); // LEK todo: mudar para uma constante melhor
        pd.setCmd("{cmd:READYTOPLAY,px:1.1,py:2.2,pz:3.3,cardNumber:4,uID:"+uID+"}"); // LEK todo: mudar para uma constante melhor
        pd.setAvatarType("A");
        Log.d(TAG,"READYTOPLAY");
        pd.setPlayerNickName(playerNickName);
        pd.setEmail(email);
        Calendar calendar = Calendar.getInstance();
        java.util.Date now = calendar.getTime();
        java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());
        pd.setTimestamp(currentTimestamp);
        pd.setLastUpdateTime("" + now.getTime());
        pd.setRegistrationTime("" + now.getTime());
        pd.setStateAndLastTime(pd.getGameState()+"_"+pd.getLastUpdateTime());
        pd.setRunningTimes(this.runningTimes);
        Log.d(TAG, "local timestamp:" + currentTimestamp.toString());
        Log.d(TAG, "System timestamp:" + System.currentTimeMillis());

        myRef = database.getReference("players").child(uID);
        myRef.setValue(pd);
    }

    // operação para escrever dados iniciais para o próprio usuário
    private void updateRealTimeDatabaseUserData(FirebaseUser currentUser) {
        // veja https://firebase.google.com/docs/database/android/read-and-write
        if (currentUser != null) {
            // definir seu dados no Realtime Database com dados de usuario logado
            PlayerData pd = PlayerData.myPlayerData();// singleton
            //PlayerData.States x = PlayerData.States.WAITING;
            pd.setAuthUID(uID);
            pd.setWriterUID(uID);
            pd.setGameState(PlayerData.States.WAITING);
            pd.setChat("empty"); // LEK todo: mudar para uma constante melhor
            pd.setAvatarType("A");
            pd.setCmd("{cmd:WAITING,px:1.1,py:2.2,pz:3.3,cardNumber:4,uID:"+uID+"}"); // LEK todo: mudar para uma constante melhor
            Log.d(TAG,"WAITING");
            pd.setPlayerNickName(playerNickName);
            pd.setEmail(email);
            // 1) create a java calendar instance, see https://alvinalexander.com/java/java-timestamp-example-current-time-now
            Calendar calendar = Calendar.getInstance();
            // 2) get a java.util.Date from the calendar instance.
            //    this date will represent the current instant, or "now".
            java.util.Date now = calendar.getTime();
            // 3) a java current time (now) instance
            /*
            //veja https://docs.oracle.com/javase/7/docs/api/java/util/Calendar.html
            Log.d(TAG, "ano :" + calendar.get(Calendar.YEAR));
            Log.d(TAG, "mes m+1:" + (calendar.get(Calendar.MONTH)+1));
            Log.d(TAG, "dia do mes :" + calendar.get(Calendar.DAY_OF_MONTH));
            Log.d(TAG, "dia da semana :" + calendar.get(Calendar.DAY_OF_WEEK));
            Log.d(TAG, "hora:" + calendar.get(Calendar.HOUR_OF_DAY));
            Log.d(TAG, "minuto:" + calendar.get(Calendar.MINUTE));
            Log.d(TAG, "segundo:" + calendar.get(Calendar.SECOND));
            Log.d(TAG, "millis:" + calendar.get(Calendar.MILLISECOND));
            */
            java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());

            // usando long ou String para Timestamp
            //long longEpochTimeVar = 1625760239519L;
            //long longEpochTimeVar = new Long("1625760239519").longValue();
            //pd.setTimestamp(new Timestamp(longEpochTimeVar)); //currentTimestamp);

            pd.setTimestamp(currentTimestamp);
            pd.setLastUpdateTime("" + now.getTime());
            pd.setRegistrationTime("" + now.getTime());
            pd.setStateAndLastTime(pd.getGameState()+"_"+pd.getLastUpdateTime());
            pd.setRunningTimes(this.runningTimes);
            Log.d(TAG, "local timestamp:" + currentTimestamp.toString());
            Log.d(TAG, "System timestamp:" + System.currentTimeMillis());


            myRef = database.getReference("players").child(uID);
            myRefInicial = database.getReference("playersData").child(uID);
            if (newAccount){
                Log.d(TAG, "CONTA NOVA:" + pd.getRegistrationTime());
                myRef.setValue(pd);
                myRefInicial.setValue(pd);
            } else {

                // aqui está o problema de gravar 12 x o registro do usuario
                // preciso de uma colecao para guardar dados dos usuarios
                // e outro para a dinamica da troca de mensagens


                // LEK TODO o que ocorre quando a conta de um usuario
                // é apagada e a aplicação roda novamente?
                // Resposta: atualiza ("criando") uma "nova" entrada em player sem registrationTime
                // TODO player sem registration time deveria ser destruido
                // TODO botão de saída do PM não esta fechando direito o app
                Log.d(TAG, "CONTA EXISTENTE:" + pd.getRegistrationTime());
                myRef.setValue(pd);
                /*
                //myRef.child("authUID").setValue(pd.getAuthUID());//nao muda
                myRef.child("chat").setValue(pd.getChat());
                myRef.child("cmd").setValue(pd.getCmd());
                myRef.child("email").setValue(pd.getEmail());// pode alterar eventualmente
                myRef.child("gameState").setValue(pd.getGameState());
                myRef.child("lastUpdateTime").setValue(pd.getLastUpdateTime());
                myRef.child("stateAndLastTime").setValue(pd.getGameState()+"_"+pd.getLastUpdateTime());
                myRef.child("playerNickName").setValue(pd.getPlayerNickName());// pode alterar eventualmente
                // não muda o tempo de registro inicial do jogador
                //myRef.child("registrationTime").setValue(pd.getRegistrationTime());
                myRef.child("timestamp").setValue(pd.getTimestamp());
                myRef.child("writerUID").setValue(pd.getWriterUID());
                myRef.child("runningTimes").setValue(pd.getRunningTimes());

                */
            }
            if (fazSoUmaVez == 0){
                fazSoUmaVez++;
                // qualquer alteração em player->uID será notificada
                //SetOnValueChangedListener();
            }
            Log.d(TAG," fazSoUmaVez:"+fazSoUmaVez);
        }
    }
    private int fazSoUmaVez=0;


    public static InterfaceLibGDX gameLibGDX = null;

    @Override
    public void setLibGDXScreen(InterfaceLibGDX iLibGDX) {
        Log.d(TAG, "chamou setLibGDXScreen");
        // TODO chamar funcao de interface implementada por LIBGDX e setada aqui
        AndroidInterfaceClass.gameLibGDX = iLibGDX;
    }





    @Override
    public void finishAndRemoveTask(){
        this.finishAndRemoveTask();
    }

}




