package com.example.fansh.im;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    class MyCountDownTimer extends Thread{
        @Override
        public void run() {
            try{
                while(true){
                    connect();
                    sleep(30000);
                    if(doubleBackToExitPressedTwice){
                        break;
                    }
                }
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    Socket socket = null;
    DataOutputStream writer = null;
    DataInputStream reader = null;
    AsyncTask<Void,String,Void> read;
    String ipStr="10.0.2.2";
    Boolean isLogin=false;
    String usernameStr;
    String passwordStr;

    boolean doubleBackToExitPressedOnce = false;
    boolean doubleBackToExitPressedTwice = false;
    EditText username;
    EditText password;
    Button login;
    Button register;
    Button setting;
    MyCountDownTimer myCountDownTimer;

    InnerReceiver receiver = new InnerReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra("msg");
            Scanner sc = new Scanner(msg);
            while(sc.hasNext()){
                String type = sc.nextLine();
                if("signDown".equals(type)){
                    isLogin=false;
                    break;
                }
            }
            out(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login = (Button) findViewById(R.id.login);
        register = (Button) findViewById(R.id.register);
        setting = (Button) findViewById(R.id.setting);
        username = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById(R.id.password);

        login.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        loginAction(v);
                    }
                }
        );

        register.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        MyDialog myDialog = new MyDialog();
                        Bundle param = new Bundle();
                        param.putString("type","register");
                        param.putString("data","0");
                        myDialog.setArguments(param);
                        myDialog.show(getFragmentManager(),"myDialog");
                    }
                }
        );

        setting.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        MyDialog myDialog = new MyDialog();
                        Bundle param = new Bundle();
                        param.putString("type","setting");
                        param.putString("data",ipStr);
                        myDialog.setArguments(param);
                        myDialog.show(getFragmentManager(),"myDialog");
                    }
                }
        );

        //注册广播并接受
        IntentFilter filter = new IntentFilter("reqBroadcast");
        registerReceiver(receiver, filter);

        myCountDownTimer = new MyCountDownTimer();
        myCountDownTimer.start();

    }

    public void loginAction(View view){
        usernameStr = username.getText().toString();
        passwordStr = password.getText().toString();

        if(!usernameStr.isEmpty() && !passwordStr.isEmpty()){
            out("signUp"+"\n"+usernameStr+"\n"+passwordStr+"\n");
        }else{
            Toast.makeText(MainActivity.this,"username or password is error", Toast.LENGTH_LONG).show();
        }

    }

    public void connect(){
        read= new AsyncTask<Void,String,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    socket = new Socket();
                    socket.connect(new InetSocketAddress(ipStr, 12345), 5000);
                    reader = new DataInputStream(socket.getInputStream());
                    writer = new DataOutputStream(socket.getOutputStream());
                    publishProgress("success");

                    if(isLogin){
                        out("restart"+"\n"+usernameStr+"\n"+passwordStr+"\n");
                    }

                    String responseStr;
                    String responseType;
                    String responseElse;
                    while(true){
                        //socket.sendUrgentData(0xFF);
                        responseStr = reader.readUTF();
                        if(responseStr!=null && !responseStr.isEmpty()){
                            responseType = responseStr.substring(0,responseStr.indexOf("\n"));
                            responseElse = responseStr.substring(responseStr.indexOf("\n")+1);
                            if("register".equals(responseType)){
                                publishProgress("register",responseElse);
                            }else if("signUp".equals(responseType)) {
                                String responseState = responseElse.substring(0, responseElse.indexOf("\n"));
                                String responseMsg = responseElse.substring(responseElse.indexOf("\n") + 1);
                                publishProgress("signUp", responseState, responseMsg);
                                isLogin=true;
                            }else if("restart".equals(responseType)){
                                String responseState = responseElse.substring(0,responseElse.indexOf("\n"));
                                String responseMsg =  responseElse.substring(responseElse.indexOf("\n")+1);
                                publishProgress("restart",responseState,responseMsg);
                                isLogin=true;
                            }else if("msg".equals(responseType)){
                                String responseGroupId = responseElse.substring(0,responseElse.indexOf("\n"));
                                String responseGroupMsg =  responseElse.substring(responseElse.indexOf("\n")+1);
                                String responseUserName = responseGroupMsg.substring(0,responseGroupMsg.indexOf("\n"));
                                Log.i("msg","name:"+responseUserName);
                                if(!responseUserName.equals(usernameStr)){
                                    publishProgress("msg",responseGroupId,responseGroupMsg);
                                }else{
                                    publishProgress("msgSelf",responseGroupId,responseGroupMsg);
                                }
                            }else if("groupJoin".equals(responseType)){
                                String responseGroupId = responseElse.substring(0,responseElse.indexOf("\n"));
                                String responseGroupMsg =  responseElse.substring(responseElse.indexOf("\n")+1);
                                publishProgress("groupJoin",responseGroupId,responseGroupMsg);
                            }else if("groupToJoin".equals(responseType)){
                                publishProgress("groupToJoin",responseElse);
                            }else if("groupToQuit".equals(responseType)){
                                publishProgress("groupToQuit",responseElse);
                            }else if("groupShow".equals(responseType)){
                                publishProgress("groupShow",responseElse);
                            }else if("groupAdd".equals(responseType)){
                                publishProgress("groupAdd",responseElse);
                            }else if("groupQuit".equals(responseType)){
                                publishProgress("groupQuit",responseElse);
                            }else if("signDown".equals(responseType)){
                                isLogin=false;
                                break;
                            }
                        }
                    }
                }catch(UnknownHostException e){
                    Log.i("msg","UnknownHostException");
                    publishProgress("error");
                    e.printStackTrace();
                }catch(IOException e){
                    Log.i("msg","IOException");
                    publishProgress("error");
                    e.printStackTrace();
                }catch(Exception e){
                    Log.i("msg","Exception");
                    publishProgress("error");
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                if (values[0] != null && !values[0].isEmpty()) {

                    if (values[0].equals("success")) {
                        chatResposeMsg("groupState",null,"Up");
                        Toast.makeText(MainActivity.this, " connect to server success", Toast.LENGTH_SHORT).show();
                    } else if (values[0].equals("error")) {
                        chatResposeMsg("groupState",null,"Down");
                        Toast.makeText(MainActivity.this, " connect to server error", Toast.LENGTH_SHORT).show();
                    } else if (values[0].equals("register")) {
                        if("registerSuccess".equals(values[1])){
                            Toast.makeText(MainActivity.this, " register success", Toast.LENGTH_SHORT).show();
                        }else if("registerError".equals(values[1])) {
                            Toast.makeText(MainActivity.this, " register error", Toast.LENGTH_SHORT).show();
                        }
                    } else if (values[0].equals("restart")) {
                        if("restartSuccess".equals(values[1])){
                            chatResposeMsg("restart",null,values[2]);
                            Toast.makeText(MainActivity.this, " login success", Toast.LENGTH_SHORT).show();
                        }else if("restartError".equals(values[1])){
                            Toast.makeText(MainActivity.this, " login error", Toast.LENGTH_SHORT).show();
                        }
                    } else if (values[0].equals("signUp")) {
                        if("signUpSuccess".equals(values[1])){
                            chatLogin(values[2]);
                            Toast.makeText(MainActivity.this, " login success", Toast.LENGTH_SHORT).show();
                        }else if("signUpError".equals(values[1])){
                            Toast.makeText(MainActivity.this, " login error", Toast.LENGTH_SHORT).show();
                        }
                    } else if (values[0].equals("msg")) {
                        chatResposeMsg("msg",values[1],values[2]);
                        chatResposeMsg("groupMsg",values[1],values[2]);
                    } else if (values[0].equals("msgSelf")) {
                        chatResposeMsg("msg",values[1],values[2]);
                        chatResposeMsg("groupMsgSelf",values[1],values[2]);
                    }else if (values[0].equals("groupAdd")) {
                        chatResposeMsg("groupAdd",values[1],null);
                    } else if (values[0].equals("groupJoin")) {
                        chatResposeMsg("groupJoin",values[1],values[2]);
                    } else if (values[0].equals("groupQuit")) {
                        chatResposeMsg("groupQuit",values[1],null);
                    } else if (values[0].equals("groupShow")) {
                        chatResposeMsg("groupShow",null,values[1]);
                    } else if (values[0].equals("groupToJoin")) {
                        chatResposeMsg("groupToJoin",null,values[1]);
                    } else if (values[0].equals("groupToQuit")) {
                        chatResposeMsg("groupToQuit",null,values[1]);
                    }
                    super.onProgressUpdate(values);
                }
            }
        };
        read.execute();
    }



    public void chatResposeMsg(String type,String groupId,String msg){
        Intent i = new Intent("resBroadcast");
        i.putExtra("type",type);
        i.putExtra("groupId",groupId);
        i.putExtra("msg", msg);
        sendBroadcast(i);
    }

    public void chatLogin(String msg){
        Intent i = new Intent(this, ChatList.class);
        i.putExtra("signUp",msg);
        startActivity(i);
    }

    public void setIp(String ipParam){
        ipStr = ipParam;
    }

    public void out(String msg){
        try {
            DataOutputStream writer = new DataOutputStream(socket.getOutputStream());
            writer.writeUTF(msg);
            writer.flush();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedTwice = true;
            finish();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
    }

}
