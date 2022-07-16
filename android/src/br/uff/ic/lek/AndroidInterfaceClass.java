package br.uff.ic.lek;
import java.math.BigInteger;
import java.util.*;
import android.app.Activity;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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

import br.uff.ic.lek.actors.Avatar;
import br.uff.ic.lek.game.World;

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
     }


     private void currentUserDefined(FirebaseUser currentUser) {
         if (currentUser != null) {
             email = currentUser.getEmail();
             try {
                 final String before = email.split("@")[0];
                 playerNickName = before;//ou aqui que o problema está.

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
        this.playerNickName = playerNickName;//aqui ele está atualizando a variavel nickname e depois email com
        //os valores originais.
        this.runningTimes = runningTimes;
        Log.d(TAG, "construtor AndroidInterfaceClass execucoes:" +runningTimes+ " playerNickName="+playerNickName+" emailCRC32="+emailCRC32+" pwdCRC32="+pwdCRC32);

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

     /*
        Esta função toma conta da escuta alterações de players que estão na mesma sala
        e encaminha os comandos para processamento por meio da função enqueueMessage()
      */

    @Override
    public void handleMultiplayer(){
        PlayerData pd = PlayerData.myPlayerData();

        //Testar primeiro se o usuário local está conectado a uma sala, e esperar ele se conectar
        DatabaseReference player = referencia.child("players").child(pd.getAuthUID());
        Log.d("Wait for players", String.valueOf(player.child("isConnected")));



        player.child("isConnectedToARoom").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("Wait for players", "updated isConnectedTOARoom " + String.valueOf(snapshot.getValue()));

                /*

                    Se estiver connectado, pegar instâncias de outros players que estão conectados na mesma sala
                    e adicionar um listener para escutar mudanças de posição nesses players

                 */

                if((Boolean) snapshot.getValue()){
                    referencia.child("players").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            HashMap<String, Object> player = (HashMap<String, Object>) snapshot.getValue();
                            if(((String) player.get("connectedRoomID")).equals(pd.getConnectedRoomID())){
                                /*
                                    Ao receber alguma mudança de algum player que está na mesma sala e não é o player local,
                                    encaminhar mensagem para processamento.

                                 */

                                Log.d("Wait for players", "Atualizou alguem da sala" + String.valueOf(snapshot.getValue()));
                                //Apenas levar em conta mudanças se não for o mesmo ID que ID local
                                if(!((String) player.get("authUID")).equals(pd.getAuthUID())){
                                    //Recebendo mensagem de comando dos usuarios conectados
                                    String registrationTime = "" + player.get("registrationTime");
                                    String authUID = "" + player.get("authUID");
                                    String cmd = "" + player.get("cmd");
                                    String lastUpdateTime = "" + player.get("lastUpdateTime");

                                    Log.d("Wait for players", "Resposta" + String.valueOf(snapshot.getValue()));

                                    AndroidInterfaceClass.gameLibGDX.enqueueMessage(InterfaceLibGDX.ALL_PLAYERS_DATA, registrationTime, authUID, cmd, lastUpdateTime);
                                }
                            }
                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void writePlayerData(Avatar player){
        Log.d(TAG, "******** writePlayerData");
        myRef = database.getReference("players").child(player.getAuthUID());
        myRef.setValue(player.sincFirebaseData());
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
            playerNickName = pd.getPlayerNickName();
            pd.setPlayerNickName(playerNickName);//aqui que está sendo feito a atualização com o valor antigo .
            email = pd.getEmail();
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
            //Testar se está conectado ou não em uma sala
            myRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("Set isConnectedToARoom", "Error getting data", task.getException());
                    } else {
                        if(task.getResult().exists()){
                            Log.d("Set isConnectedToARoom", "Existe: " + String.valueOf(task.getResult().getValue()));

                            HashMap<String, Object> playerHashMap = new HashMap<>();
                            playerHashMap = (HashMap<String, Object>) task.getResult().getValue();

                            if((Boolean) playerHashMap.get("isConnectedToARoom") == null){
                                pd.setIsConnectedToARoom(false);
                            } else {
                                pd.setIsConnectedToARoom((Boolean) playerHashMap.get("isConnectedToARoom"));
                            }

                            if((String) playerHashMap.get("connectedRoomID") == null){
                                pd.setConnectedRoomID("");
                            } else {
                                pd.setConnectedRoomID((String) playerHashMap.get("connectedRoomID"));
                            }
                        }
                    }

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
                    }
                    Log.d(TAG," fazSoUmaVez:"+fazSoUmaVez);
                }
            });
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

    //Função para atualizar instância local de room com informações do firebase
    @Override
    public void sincronizeLocalRoom(){
        PlayerData pd = PlayerData.myPlayerData();

        DatabaseReference pdRef = database.getReference("players").child(uID);

        pdRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("Sincronize local Room", "Error getting data", task.getException());
                } else {
                    Log.d("Sincronize local Room", String.valueOf(task.getResult().getValue()));

                    HashMap<String, Object> playerHashMap = new HashMap<>();
                    playerHashMap = (HashMap<String, Object>) task.getResult().getValue();

                    if((Boolean) playerHashMap.get("isConnectedToARoom") == null){
                        pd.setIsConnectedToARoom(false);
                    } else {
                        pd.setIsConnectedToARoom((Boolean) playerHashMap.get("isConnectedToARoom"));
                    }

                    if((String) playerHashMap.get("connectedRoomID") == null){
                        pd.setConnectedRoomID("");
                    } else {
                        pd.setConnectedRoomID((String) playerHashMap.get("connectedRoomID"));
                    }
                }
            }
        });
    }

    //Dado um id de uma sala especifica, desconectar o usuario dessa sala e conectar na nova
    @Override
    public void chooseSpecificRoom(final String newRoomID){
        PlayerData pd = PlayerData.myPlayerData();
        DatabaseReference pdRef = database.getReference("players").child(uID);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Room room = Room.myRoom();

        pdRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                ArrayList<String> connectedPlayersIDsArray = new ArrayList<String>();
                if (!task.isSuccessful()) {
                    Log.e("Sincronize local Room", "Error getting data", task.getException());
                } else {
                    Log.d("Sincronize local Room", "testePrimeiro: " + String.valueOf(task.getResult().getValue()));

                    HashMap<String, Object> playerHashMap = new HashMap<>();
                    playerHashMap = (HashMap<String, Object>) task.getResult().getValue();

                    if((Boolean) playerHashMap.get("isConnectedToARoom") == null){
                        pd.setIsConnectedToARoom(false);
                    } else {
                        pd.setIsConnectedToARoom((Boolean) playerHashMap.get("isConnectedToARoom"));
                    }

                    if((String) playerHashMap.get("connectedRoomID") == null){
                        pd.setConnectedRoomID("");
                    } else {
                        pd.setConnectedRoomID((String) playerHashMap.get("connectedRoomID"));
                    }

                    Log.d("Disconnect from room", "teste: " + String.valueOf(pd.getIsConnectedToARoom()));
                    //Testar se está connectado
                    if(pd.getIsConnectedToARoom()){
                        Room room = Room.myRoom();
                        DatabaseReference roomRef = database.getReference("rooms").child(pd.getConnectedRoomID());

                        //Desconectar player da sala atual
                        roomRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            ArrayList<String> connectedPlayersIDsArray = new ArrayList<String>();


                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                ArrayList<String> connectedPlayersIDsArray = new ArrayList<String>();
                                if (!task.isSuccessful()) {
                                    Log.e("Disconnect from room", "Error getting data", task.getException());
                                } else {
                                    Log.d("Disconnect from room", String.valueOf(task.getResult().getValue()));

                                    HashMap<String, Object> roomHashMap = new HashMap<>();
                                    roomHashMap = (HashMap<String, Object>) task.getResult().getValue();

                                    connectedPlayersIDsArray = (ArrayList<String>) roomHashMap.get("connectedPlayersIDs");
                                    Log.d("Disconnect from room", "Array: " + String.valueOf(connectedPlayersIDsArray));

                                    int index = 0;
                                    for (int i = 0; i < connectedPlayersIDsArray.size(); i++) {
                                        Log.d("Disconnect from room", "esq: " + String.valueOf(connectedPlayersIDsArray.get(i)) + " dir: " + String.valueOf(currentUser.getUid().toString()));
                                        Log.d("Disconnect from room", "teste: " + String.valueOf(connectedPlayersIDsArray.get(i).equals(currentUser.getUid())));
                                        if(connectedPlayersIDsArray.get(i).equals(currentUser.getUid())){
                                            Log.d("Disconnect from room", "removed: " + String.valueOf(connectedPlayersIDsArray.get(i)));
                                            index = i;
                                            break;
                                        }
                                    }

                                    connectedPlayersIDsArray.remove(index);
                                    room.setconnectedPlayersIDs(connectedPlayersIDsArray);
                                    room.setIsFull(false);
                                    room.setNumberOfConnectedPlayers((Long) roomHashMap.get("numberOfConnectedPlayers") - 1);
                                    room.setLimit((Long) roomHashMap.get("limit"));

                                    pd.setIsConnectedToARoom(false);
                                    pd.setConnectedRoomID("");

                                    //Atualizando objetos do firebase
                                    Log.d("Disconnect from room", "UpdatedRoomObj: " + String.valueOf(room.toString()));
                                    roomRef.setValue(room);
                                    pdRef.setValue(pd);

                                    //Conectar em nova sala
                                    DatabaseReference newRoomRef = database.getReference("rooms").child(newRoomID);
                                    newRoomRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                            ArrayList<String> connectedPlayersIDsArray = new ArrayList<String>();
                                            if (!task.isSuccessful()) {
                                                Log.e("Connect Specific Room", "Error getting data", task.getException());
                                            } else {
                                                Log.d("Connect Specific Room", String.valueOf(task.getResult().getValue()));

                                                HashMap<String, Object> roomHashMap = new HashMap<>();
                                                roomHashMap = (HashMap<String, Object>) task.getResult().getValue();

                                                //Testar se ID é de uma sala existente
                                                if(task.getResult().exists()){
                                                    //Só pode conectar se a sala não estiver cheia
                                                    if(!((Boolean) roomHashMap.get("isFull"))){
                                                        //Atualizar instancia de sala local
                                                        connectedPlayersIDsArray = (ArrayList<String>) roomHashMap.get("connectedPlayersIDs");
                                                        connectedPlayersIDsArray.add(currentUser.getUid());

                                                        room.setconnectedPlayersIDs(connectedPlayersIDsArray);
                                                        room.setRoomID(newRoomID);
                                                        room.setIsFull((Boolean) roomHashMap.get("isFull"));
                                                        room.setLimit((Long) roomHashMap.get("limit"));
                                                        room.setNumberOfConnectedPlayers((Long) roomHashMap.get("numberOfConnectedPlayers"));

                                                        //Após uma sala ser escolhida, atualizar instância de sala no firebase

                                                        if(room.getNumberOfConnectedPlayers() == room.getLimit() - 1){
                                                            newRoomRef.child("isFull").setValue(true);
                                                        }

                                                        newRoomRef.child("numberOfConnectedPlayers").setValue(room.getNumberOfConnectedPlayers() + 1);
                                                        newRoomRef.child("connectedPlayersIDs").setValue(room.getconnectedPlayersIDs());
                                                    }
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        //Se não estiver conectado em alguma sala, conectar a sala dada
                        DatabaseReference newRoomRef = database.getReference("rooms").child(newRoomID);
                        newRoomRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                ArrayList<String> connectedPlayersIDsArray = new ArrayList<String>();
                                if (!task.isSuccessful()) {
                                    Log.e("Connect Specific Room", "Error getting data", task.getException());
                                } else {
                                    Log.d("Connect Specfic Room", String.valueOf(task.getResult().getValue()));

                                    HashMap<String, Object> roomHashMap = new HashMap<>();
                                    roomHashMap = (HashMap<String, Object>) task.getResult().getValue();

                                    //Testar se ID é de uma sala existente
                                    if(task.getResult().exists()){
                                        //Só pode conectar se a sala não estiver cheia
                                        if(!((Boolean) roomHashMap.get("isFull"))){
                                            //Atualizar instancia de sala local
                                            connectedPlayersIDsArray = (ArrayList<String>) roomHashMap.get("connectedPlayersIDs");
                                            connectedPlayersIDsArray.add(currentUser.getUid());

                                            room.setconnectedPlayersIDs(connectedPlayersIDsArray);
                                            room.setRoomID(newRoomID);
                                            room.setIsFull((Boolean) roomHashMap.get("isFull"));
                                            room.setLimit((Long) roomHashMap.get("limit"));
                                            room.setNumberOfConnectedPlayers((Long) roomHashMap.get("numberOfConnectedPlayers"));

                                            //Após uma sala ser escolhida, atualizar instância de sala no firebase

                                            if(room.getNumberOfConnectedPlayers() == room.getLimit() - 1){
                                                newRoomRef.child("isFull").setValue(true);
                                            }

                                            newRoomRef.child("numberOfConnectedPlayers").setValue(room.getNumberOfConnectedPlayers() + 1);
                                            newRoomRef.child("connectedPlayersIDs").setValue(room.getconnectedPlayersIDs());
                                        }
                                    }
                                }
                            }
                        });
                    }

                    pd.setConnectedRoomID(newRoomID);
                    pd.setIsConnectedToARoom(true);
                    pdRef.setValue(pd);
                }
            }
        });
    }

    //Função para desconectar da sala conectada atualmente
    @Override
    public void disconnectFromRoom(){
        PlayerData pd = PlayerData.myPlayerData();
        DatabaseReference pdRef = database.getReference("players").child(uID);
        FirebaseUser currentUser = mAuth.getCurrentUser();

        pdRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("Sincronize local Room", "Error getting data", task.getException());
                } else {
                    //Editar informações do jogador no firebase para desconectalo
                    Log.d("Sincronize local Room", String.valueOf(task.getResult().getValue()));

                    HashMap<String, Object> playerHashMap = new HashMap<>();
                    playerHashMap = (HashMap<String, Object>) task.getResult().getValue();

                    if((Boolean) playerHashMap.get("isConnectedToARoom") == null){
                        pd.setIsConnectedToARoom(false);
                    } else {
                        pd.setIsConnectedToARoom((Boolean) playerHashMap.get("isConnectedToARoom"));
                    }

                    if((String) playerHashMap.get("connectedRoomID") == null){
                        pd.setConnectedRoomID("");
                    } else {
                        pd.setConnectedRoomID((String) playerHashMap.get("connectedRoomID"));
                    }

                    if(pd.getIsConnectedToARoom()){
                        //Atualizar informações da instância da sala do firebase para desconectalo
                        Room room = Room.myRoom();
                        DatabaseReference roomRef = database.getReference("rooms").child(pd.getConnectedRoomID());

                        roomRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            ArrayList<String> connectedPlayersIDsArray = new ArrayList<String>();

                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (!task.isSuccessful()) {
                                    Log.e("Disconnect from room", "Error getting data", task.getException());
                                } else {
                                    Log.d("Disconnect from room", String.valueOf(task.getResult().getValue()));

                                    HashMap<String, Object> roomHashMap = new HashMap<>();
                                    roomHashMap = (HashMap<String, Object>) task.getResult().getValue();

                                    connectedPlayersIDsArray = (ArrayList<String>) roomHashMap.get("connectedPlayersIDs");
                                    Log.d("Disconnect from room", "Array: " + String.valueOf(connectedPlayersIDsArray));

                                    int index = 0;
                                    for (int i = 0; i < connectedPlayersIDsArray.size(); i++) {
                                        Log.d("Disconnect from room", "esq: " + String.valueOf(connectedPlayersIDsArray.get(i)) + " dir: " + String.valueOf(currentUser.getUid().toString()));
                                        Log.d("Disconnect from room", "teste: " + String.valueOf(connectedPlayersIDsArray.get(i).equals(currentUser.getUid())));
                                        if(connectedPlayersIDsArray.get(i).equals(currentUser.getUid())){
                                            Log.d("Disconnect from room", "removed: " + String.valueOf(connectedPlayersIDsArray.get(i)));
                                            index = i;
                                            break;
                                        }
                                    }

                                    connectedPlayersIDsArray.remove(index);
                                    room.setconnectedPlayersIDs(connectedPlayersIDsArray);
                                    room.setIsFull(false);
                                    room.setNumberOfConnectedPlayers((Long) roomHashMap.get("numberOfConnectedPlayers") - 1);
                                    room.setLimit((Long) roomHashMap.get("limit"));

                                    pd.setIsConnectedToARoom(false);
                                    pd.setConnectedRoomID("");

                                    //Atualizando objetos do firebase
                                    Log.d("Disconnect from room", "UpdatedRoomObj: " + String.valueOf(room.toString()));
                                    roomRef.setValue(room);
                                    pdRef.setValue(pd);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    /*
        As respostas do firebase são manipuladas por meios de Hashmaps com strings para as chaves.
        O firebase não oferece um meio de verificar os tipos dos valores recebidos, mas como faz parte da nossa modelagem,
        sabemos os tipos de valores que esperar para cada campo, assim é possível fazer cast sem problemas.
     */

    //Buscar primeira sala vazia para se conectar
    @Override
    public void searchForAvailableRooms(){
        PlayerData pd = PlayerData.myPlayerData();
        Room newRoom = Room.myRoom();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        ArrayList<String> connectedUsersIDs = new ArrayList<String>();
        DatabaseReference pdRef = database.getReference("players").child(uID);

        //Primeiro verificar se está conectado
        //É necessário receber status do servidor para verificar isso
        pdRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("Set isConnectedToARoom", "Error getting data", task.getException());
                } else {
                    Log.d("Set isConnectedToARoom", String.valueOf(task.getResult().getValue()));

                    HashMap<String, Object> playerHashMap = new HashMap<>();
                    playerHashMap = (HashMap<String, Object>) task.getResult().getValue();

                    if((Boolean) playerHashMap.get("isConnectedToARoom") == null){
                        pd.setIsConnectedToARoom(false);
                    } else {
                        pd.setIsConnectedToARoom((Boolean) playerHashMap.get("isConnectedToARoom"));
                    }

                    if((String) playerHashMap.get("connectedRoomID") == null){
                        pd.setConnectedRoomID("");
                    } else {
                        pd.setConnectedRoomID((String) playerHashMap.get("connectedRoomID"));
                    }

                    //Se jogador não estiver conectado, fazer processo
                    if(pd.getIsConnectedToARoom() == false) {
                        /*
                             Conectar a uma sala, que não está cheia, se não existirem salas, não cheias criar uma sala nova
                         */
                        DatabaseReference roomsRef = database.getReference("rooms");

                        roomsRef.orderByChild("isFull").equalTo(false).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                String newRoomID = "";
                                if (!task.isSuccessful()) {
                                    Log.e("Search Rooms", "Error getting data", task.getException());
                                } else {
                                    Iterable<DataSnapshot> resData = task.getResult().getChildren();

                                    Log.d("Search Rooms", String.valueOf(task.getResult().getValue()));

                                    HashMap<String, Object> chosenRoom = new HashMap<>();
                                    String chosenRoomID = "";

                                    pd.setIsConnectedToARoom(true);
                                    pd.setConnectedRoomID(newRoomID);
                                    pdRef.setValue(pd);

                                    if(task.getResult().exists()){
                                        for(DataSnapshot room : resData){
                                            chosenRoom = (HashMap<String, Object>) room.getValue();
                                            chosenRoomID = room.getKey();
                                            Log.d("Search Rooms", "Chosen Room:" + String.valueOf(room.getKey()) + ": " + String.valueOf(String.valueOf(chosenRoom)));
                                            //Aqui é dado um break, pois como pela query validamos se a sala está cheia ou não a primeira sala não cheia serve
                                            break;
                                        }
                                        ArrayList<String> IDsArray = (ArrayList<String>) chosenRoom.get("connectedPlayersIDs");
                                        if(IDsArray != null){
                                            //Não é necessário testar se o ID do usuário está contido, já que essa parte do código só será rodado
                                            //caso ele não esteja conectado a nenhuma sala
                                            IDsArray.add(currentUser.getUid());
                                        } else {
                                            IDsArray = new ArrayList<String>();
                                            IDsArray.add(currentUser.getUid());
                                        }

                                        //Atualizar instancia de sala local
                                        newRoom.setconnectedPlayersIDs(IDsArray);
                                        newRoom.setRoomID(chosenRoomID);
                                        newRoom.setIsFull((Boolean) chosenRoom.get("isFull"));
                                        newRoom.setLimit((Long) chosenRoom.get("limit"));
                                        newRoom.setNumberOfConnectedPlayers((Long) chosenRoom.get("numberOfConnectedPlayers"));

                                        newRoomID = chosenRoomID;

                                        //Após uma sala ser escolhida, atualizar instância de sala no firebase
                                        DatabaseReference chosenRoomRef = roomsRef.child(chosenRoomID);

                                        if(newRoom.getNumberOfConnectedPlayers() == newRoom.getLimit() - 1){
                                            chosenRoomRef.child("isFull").setValue(true);
                                        }

                                        chosenRoomRef.child("numberOfConnectedPlayers").setValue(newRoom.getNumberOfConnectedPlayers() + 1);
                                        chosenRoomRef.child("connectedPlayersIDs").setValue(newRoom.getconnectedPlayersIDs());
                                    } else {
                                        Log.d("Search Rooms", "Room not found, creating new Room:");
                                        newRoom.setLimit(4L);
                                        newRoom.setNumberOfConnectedPlayers(1L);
                                        connectedUsersIDs.add(currentUser.getUid());
                                        newRoom.setconnectedPlayersIDs(connectedUsersIDs);
                                        newRoom.setIsFull(false);

                                        newRoomID = roomsRef.push().getKey();
                                        roomsRef.child(newRoomID).setValue(newRoom);
                                    }

                                    //Após isso estara conectado
                                    pd.setIsConnectedToARoom(true);
                                    pd.setConnectedRoomID(newRoomID);
                                    pdRef.setValue(pd);
                                }

                                /*
                                    Tratamento de lixo
                                    Pegar todos os jogadores, testar se há algum player que pode ser desconectado (passou do seu limite de tempo)
                                    se sim, desconectar jogador de sua sala.
                                */

                                database.getReference("players").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                                        if (!task.isSuccessful()) {
                                            Log.e("Search Trash cleaning", "Error getting data", task.getException());
                                        } else {
                                            Iterable<DataSnapshot> resData = task.getResult().getChildren();

                                            Log.d("Search Trash cleaning", "Jogadores: " + String.valueOf(task.getResult().getValue()));

                                            if(task.getResult().exists()){
                                                for(DataSnapshot player : resData){
                                                    HashMap<String, Object> currentPlayer = (HashMap<String, Object>) player.getValue();
                                                    String currentPlayerID = player.getKey();
                                                    Date now = new Date();

                                                    //É preciso trabalhar com bigInteger pelo tamanho do número, depois é convertido para inteiro
                                                    BigInteger nowBig = BigInteger.valueOf(now.getTime());
                                                    BigInteger currentPlayerLastUpdateTime = new BigInteger((String) currentPlayer.get("lastUpdateTime"));
                                                    Integer secondsDif = (nowBig.subtract(currentPlayerLastUpdateTime)).intValue()/1000;

                                                    System.out.println("Aqui: " + String.valueOf(currentPlayerLastUpdateTime));
                                                    System.out.println("Aqui: " + String.valueOf(BigInteger.valueOf(now.getTime())));
                                                    System.out.println("Aqui: " + String.valueOf(secondsDif));

                                                    //limite de 5 horas, em segundos
                                                    Integer limit = 3600 * 5;
                                                    if((Boolean) currentPlayer.get("isConnectedToARoom") == true & secondsDif > limit){
                                                        Log.d("Search Trash cleaning", "Inactive player: " + currentPlayerID);
                                                        //Desconectar-lo da sala
                                                        String inactivePlayerRoomID = (String)currentPlayer.get("connectedRoomID");

                                                        //Mudar informações no objeto do player do firebase
                                                        DatabaseReference inactivePlayerRef = database.getReference("players").child(currentPlayerID);
                                                        inactivePlayerRef.child("isConnectedToARoom").setValue(false);
                                                        inactivePlayerRef.child("connectedRoomID").setValue("");

                                                        //Mudar informações da sala que ele está conectado
                                                        DatabaseReference roomRef = database.getReference("rooms").child(inactivePlayerRoomID);
                                                        roomRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                                if (!task.isSuccessful()) {
                                                                    Log.e("Disconnect from room", "Error getting data", task.getException());
                                                                } else {
                                                                    Log.d("Disconnect from room", String.valueOf(task.getResult().getValue()));

                                                                    HashMap<String, Object> roomHashMap = new HashMap<>();
                                                                    roomHashMap = (HashMap<String, Object>) task.getResult().getValue();

                                                                    ArrayList<String> connectedPlayersIDsArray = (ArrayList<String>) roomHashMap.get("connectedPlayersIDs");
                                                                    Log.d("Disconnect from room", "Array: " + String.valueOf(connectedPlayersIDsArray));

                                                                    int index = 0;
                                                                    for (int i = 0; i < connectedPlayersIDsArray.size(); i++) {
                                                                        if(connectedPlayersIDsArray.get(i) != null){
                                                                            Log.d("Disconnect from room", "esq: " + String.valueOf(connectedPlayersIDsArray.get(i)) + " dir: " + String.valueOf(currentPlayerID));
                                                                            Log.d("Disconnect from room", "teste: " + String.valueOf(connectedPlayersIDsArray.get(i).equals(currentPlayerID)));
                                                                            if (connectedPlayersIDsArray.get(i).equals(currentPlayerID)) {
                                                                                Log.d("Disconnect from room", "removed: " + String.valueOf(connectedPlayersIDsArray.get(i)));
                                                                                index = i;
                                                                                break;
                                                                            }
                                                                        }
                                                                    }

                                                                    //Se for a unica pessoa na sala, remover a sala
                                                                    if(((Long) roomHashMap.get("numberOfConnectedPlayers")) - 1 == 0){
                                                                        roomRef.removeValue();
                                                                    } else {
                                                                        connectedPlayersIDsArray.remove(index);
                                                                        roomRef.child("numberOfConnectedPlayers").setValue(((Long) roomHashMap.get("numberOfConnectedPlayers")) - 1);
                                                                        roomRef.child("isFull").setValue(false);
                                                                        roomRef.child("connectedPlayersIDs").setValue(connectedPlayersIDsArray);
                                                                    }
                                                                }

                                                                //Após checar se é possível remover outros playuer
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            }
        });
    }
}




