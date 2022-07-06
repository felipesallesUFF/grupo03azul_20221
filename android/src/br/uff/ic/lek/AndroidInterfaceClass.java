package br.uff.ic.lek;
import java.util.*;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.*;
import java.util.*;

public class AndroidInterfaceClass extends Activity implements InterfaceAndroidFireBase {
    public static final boolean debugFazPrimeiraVez = false;
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

    // // exemplo vindo do Firebase
    private static final String TAG = "JOGO";
    // // [START declare_auth]
    private FirebaseAuth mAuth;
    // // [END declare_auth

    // @Override
    // public void onCreate(Bundle savedInstanceState) {
    //     super.onCreate(savedInstanceState);
    //     // [START initialize_auth]
    //     // Initialize Firebase Auth
    //     mAuth = FirebaseAuth.getInstance();
    //     // [END initialize_auth]
    // }

    // // [START on_start_check_user]
    // @Override
    // public void onStart() {
    //     super.onStart();
    //     // Check if user is signed in (non-null) and update UI accordingly.
    //     System.out.println("hmmm")
    //     FirebaseUser currentUser = mAuth.getCurrentUser();
    //     if (currentUser != null) {
    //         reload();
    //     }
    // }
    // // [END on_start_check_user]

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

    // private void sendEmailVerification() {
    //     // Send verification email
    //     // [START send_email_verification]
    //     final FirebaseUser user = mAuth.getCurrentUser();
    //     user.sendEmailVerification()
    //             .addOnCompleteListener(this, new OnCompleteListener<Void>() {
    //                 @Override
    //                 public void onComplete(@NonNull Task<Void> task) {
    //                     // Email sent
    //                 }
    //             });
    //     // [END send_email_verification]
    // }

    // private void reload() {
    // }

     private void updateUI(FirebaseUser currentUser) {
         if (currentUser == null) return;
         providerID = currentUser.getProviderId();
         uID = currentUser.getUid();
         email = currentUser.getEmail();
         Log.d(TAG, "updateUI providerID:" + providerID + " uID:" + uID + " email:" + email);
         currentUserDefined(currentUser);
         updateRealTimeDatabaseUserData(currentUser);
     }


     private void currentUserDefined(FirebaseUser currentUser) {
         if (currentUser != null) {
             email = currentUser.getEmail();
             try {
                 final String before = email.split("@")[0];
                 playerNickName = before;

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

         currentUserDefined(currentUser);

         updateRealTimeDatabaseUserData(currentUser);

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
            myRefInicial = database.getReference("playersData").child(uID);
            if (newAccount){
                Log.d(TAG, "CONTA NOVA:" + pd.getRegistrationTime());
                myRef.setValue(pd);
                myRefInicial.setValue(pd);
            } else {
                Log.d(TAG, "CONTA EXISTENTE:" + pd.getRegistrationTime());
                myRef.setValue(pd);
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

    @Override
    public Room searchForAvailableRooms(){
        //* Falta configurar lista de players em cada sala
        Room newRoom = new Room();

        DatabaseReference roomsRef = database.getReference("rooms");
        roomsRef.orderByChild("isFull").equalTo(false).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("Search Rooms", "Error getting data", task.getException());
                } else {

                    Iterable<DataSnapshot> resData = task.getResult().getChildren();

                    Log.d("Search Rooms", String.valueOf(task.getResult().getValue()));

                    HashMap<String, Object> chosenRoom = new HashMap<>();
                    String chosenRoomID = "";


                    if(task.getResult().exists()){
                        for(DataSnapshot room : resData){
                            chosenRoom = (HashMap<String, Object>) room.getValue();
                            chosenRoomID = room.getKey();
                            Log.d("Search Rooms", "Chosen Room:" + String.valueOf(room.getKey()) + ": " + String.valueOf(String.valueOf(chosenRoom)));
                            break;
                        }

                        //Atualizar instancia de sala local
                        newRoom.setRoomID(chosenRoomID);
                        newRoom.setIsFull((Boolean) chosenRoom.get("isFull"));
                        newRoom.setLimit((Long) chosenRoom.get("limit"));
                        newRoom.setNumberOfConnectedPlayers((Long) chosenRoom.get("numberOfConnectedPlayers"));


                        //Após uma sala ser escolhida, atualizar instância de sala no firebase
                        DatabaseReference chosenRoomRef = roomsRef.child(chosenRoomID);

                        if(newRoom.getNumberOfConnectedPlayers() == newRoom.getLimit() - 1){
                            chosenRoomRef.child("isFull").setValue(true);
                        }

                        chosenRoomRef.child("numberOfConnectedPlayers").setValue(newRoom.getNumberOfConnectedPlayers() + 1);
                    } else {
                        Log.d("Search Rooms", "Room not found, creating new Room:");
                        newRoom.setLimit(4L);
                        newRoom.setNumberOfConnectedPlayers(1L);
                        newRoom.setIsFull(false);
                        roomsRef.push().setValue(newRoom);
                    }
                }
            }
        });

        return newRoom;
    }

}




