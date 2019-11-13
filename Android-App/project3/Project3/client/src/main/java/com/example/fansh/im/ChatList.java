package com.example.fansh.im;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ChatList extends AppCompatActivity {

    Button groupToAdd;
    Button groupToJoin;
    Button groupToQuit;
    ListView groupChatList;
    ListAdapter adapter;
    TextView connectState;
    TextView groupState;
    //List<String> groupList = new ArrayList<String>();
    Map<String,String> chatRecord= new HashMap<String,String>();

    NotificationCompat.Builder builder;
    NotificationManager nm;


    InnerReceiver receiver = new InnerReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            String type =  intent.getStringExtra("type");
            String groupId = intent.getStringExtra("groupId");
            String msg = intent.getStringExtra("msg");

            if("groupState".equals(type)) {
                connectState.setText("System Status:"+msg);
            }else if("restart".equals(type)) {
                chatGroupRestart(msg);
            }else if("groupMsg".equals(type)) {
                String chatRecordValue = chatRecord.get(groupId);
                if (chatRecordValue == null) {
                    chatRecordValue = "";
                }
                chatRecord.put(groupId, chatRecordValue + msg + "\n");
                sendMsgNotice(groupId,msg,chatRecord.get(groupId));
            }else if("groupMsgSelf".equals(type)) {
                String chatRecordValue = chatRecord.get(groupId);
                if (chatRecordValue == null) {
                    chatRecordValue = "";
                }
                chatRecord.put(groupId, chatRecordValue + msg + "\n");
                //sendMsgNotice(groupId,msg,chatRecord.get(groupId));
            }else if("groupAdd".equals(type)){
                if(groupId.isEmpty()) {
                    Toast.makeText(ChatList.this, "groupAdd: "+groupId+" exist", Toast.LENGTH_SHORT).show();
                }else{
                    chatGroupAdd(groupId);
                    Toast.makeText(ChatList.this, "groupAdd: "+groupId, Toast.LENGTH_SHORT).show();
                }
            }else if("groupJoin".equals(type)){
                if(groupId.isEmpty()) {
                    Toast.makeText(ChatList.this, "groupJoin: "+groupId+" error", Toast.LENGTH_SHORT).show();
                }else{
                    chatGroupJoin(groupId,msg);
                    Toast.makeText(ChatList.this, "groupJoin: "+groupId, Toast.LENGTH_SHORT).show();
                }
            }else if("groupQuit".equals(type)){
                if(groupId.isEmpty()) {
                    Toast.makeText(ChatList.this, "groupQuit: "+groupId+" error", Toast.LENGTH_SHORT).show();
                }else{
                    chatGroupQuit(groupId);
                    Toast.makeText(ChatList.this, "groupQuit: "+groupId, Toast.LENGTH_SHORT).show();
                }
                sendOuitNotice(groupId);
            }else if("groupToJoin".equals(type) ||"groupToQuit".equals(type)){
                MyDialog myDialog = new MyDialog();
                Bundle param = new Bundle();
                param.putString("type", type);
                param.putString("data", msg);
                myDialog.setArguments(param);
                myDialog.show(getFragmentManager(), "myDialog");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        groupToAdd = (Button) findViewById(R.id.groupToAdd);
        groupToJoin = (Button) findViewById(R.id.groupToJoin);
        groupToQuit = (Button) findViewById(R.id.groupToQuit);
        connectState = (TextView) findViewById(R.id.connectState);
        groupState = (TextView) findViewById(R.id.groupState);

        //注册广播并接受
        IntentFilter filter = new IntentFilter("resBroadcast");
        registerReceiver(receiver, filter);

        Log.i("msg","Bundle userData");
        Bundle userData = getIntent().getExtras();
        Log.i("msg","Bundle userData"+userData.getString("signUp"));
        chatRecord = initChatRecord(userData.getString("signUp"));
        groupState.setVisibility(View.INVISIBLE);
        if(chatRecord.isEmpty()){
            groupState.setVisibility(View.VISIBLE);
        }

        nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setAutoCancel(true);
        builder.setOnlyAlertOnce(true);

        connectState.setText("System Status:Up");
        groupChatList = (ListView)findViewById(R.id.groupChatList);
        adapter = new ArrayAdapter<String>(this, R.layout.group_item, new ArrayList<String>(chatRecord.keySet()));
        groupChatList.setAdapter(adapter);
        groupChatList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Object item = parent.getItemAtPosition(position);
                        String groupId = String.valueOf(item);
                        chatStart(groupId,chatRecord.get(groupId));
                    }
                }
        );

        groupToAdd.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                MyDialog myDialog = new MyDialog();
                Bundle param = new Bundle();
                param.putString("type","groupToAdd");
                param.putString("data","0");
                myDialog.setArguments(param);
                myDialog.show(getFragmentManager(),"myDialog");
            }
        });

        groupToJoin.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("reqBroadcast");
                intent.putExtra("msg", "groupToJoin"+"\n");
                sendBroadcast(intent);
            }
        });

        groupToQuit.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("reqBroadcast");
                intent.putExtra("msg", "groupToQuit"+"\n");
                sendBroadcast(intent);
            }
        });

        printRecord(chatRecord);

    }

    public void sendMsgNotice(String groupId,String msg,String totalMsg){
        builder.setTicker(msg);
        builder.setContentTitle(groupId);
        builder.setContentText(msg);
//        Intent intent = new Intent(this, Chat.class);
//        intent.putExtra("groupId",groupId);
//        intent.putExtra("msg",totalMsg);
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);
//        builder.setContentIntent(contentIntent);
        nm.notify(1,builder.build());
    }

    public void sendOuitNotice(String groupId){
//        builder.setTicker("quit group:"+groupId);
//        builder.setContentTitle("quit");
//        builder.setContentText("group:"+ groupId);
//        builder.setContentIntent(null);
//        nm.notify(1,builder.build());
        nm.cancel(1);
    }

    public void sendOutByDialog(String msg){
        Intent intent = new Intent("reqBroadcast");
        intent.putExtra("msg", msg);
        sendBroadcast(intent);
    }

    public void chatStart(String groupId, String msg){
        Intent i = new Intent(this, Chat.class);
        i.putExtra("groupId",groupId);
        i.putExtra("msg",msg);
        startActivity(i);
    }

    public void chatGroupRestart(String msg){
        Log.i("msg","chatGroupRestart");
        Map<String,String> newChatRecord = initChatRecord(msg);
        if(!newChatRecord.isEmpty()){
            for(String key:chatRecord.keySet()){
                String newValue = newChatRecord.get(key);
                String value = chatRecord.get(key);
                String remain="";
                Scanner sc1 = new Scanner(newValue);
                Scanner sc2 = new Scanner(value);
                while(sc1.hasNext() && sc2.hasNext()){
                    sc1.nextLine();
                }
                while(sc1.hasNext()){
                    remain = remain+sc1.nextLine();
                }
                Log.i("msg","chatGroupRestart_remain:"+remain);
                if(!remain.isEmpty()){
                    sendMsgNotice(key,remain,newValue);
                }
            }
        }
        chatRecord = newChatRecord;
    }


    public void chatGroupAdd(String groupId){
        chatRecord.put(groupId,"");
        adapter = new ArrayAdapter<String>(this, R.layout.group_item, new ArrayList<String>(chatRecord.keySet()));
        groupChatList.setAdapter(adapter);
        groupState.setVisibility(View.INVISIBLE);
    }

    public void chatGroupJoin(String groupId,String msg){
        chatRecord.put(groupId,msg);
        adapter = new ArrayAdapter<String>(this, R.layout.group_item, new ArrayList<String>(chatRecord.keySet()));
        groupChatList.setAdapter(adapter);
        groupState.setVisibility(View.INVISIBLE);
    }

    public void chatGroupQuit(String groupId){
        chatRecord.remove(groupId);
        adapter = new ArrayAdapter<String>(this, R.layout.group_item, new ArrayList<String>(chatRecord.keySet()));
        groupChatList.setAdapter(adapter);
        if(chatRecord.isEmpty()){
            groupState.setVisibility(View.VISIBLE);
        }
    }

    public Map<String,String> initChatRecord(String msg){
        Log.i("msg","initChatRecord");
        Log.i("msg",msg);
        Map<String,String> result = new HashMap<String,String>();
        try{
            JSONObject object = new JSONObject(msg);
            Iterator<String> iterator = object.keys();
            while(iterator.hasNext()){
                String key = (String) iterator.next();
                String value = object.getString(key);
                result.put(key,value);
            }
            return result;
        }catch (JSONException e){
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent("reqBroadcast");
        intent.putExtra("msg", "signDown"+"\n");
        sendBroadcast(intent);
        unregisterReceiver(receiver);
        finish();
        return;
    }

    public void printRecord(Map<String,String> msg){
        Log.i("msg","chatRecord");
        for(String key:msg.keySet()){
            Log.i("msg","chatRecordkey:"+key);
            Log.i("msg","chatRecordValue:"+msg.get(key));
        }
    }
}
