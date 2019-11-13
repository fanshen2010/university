package com.example.fansh.im;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

public class Chat extends AppCompatActivity {

    Button send;
    Button groupShow;
    ScrollView msgShowScroll;
    EditText msgSend;
    TextView msgShow;
    String groupId;

    InnerReceiver receiver = new InnerReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            String type =  intent.getStringExtra("type");
            if("msg".equals(type)){
                groupId = intent.getStringExtra("groupId");
                msgShow.append(intent.getStringExtra("msg"));
                rolldowm();
            }else if("groupShow".equals(type)){
                String msg = intent.getStringExtra("msg");
                MyDialog myDialog = new MyDialog();
                Bundle param = new Bundle();
                param.putString("type", "groupShow");
                param.putString("data", msg);
                myDialog.setArguments(param);
                myDialog.show(getFragmentManager(), "myDialog");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        send = (Button)findViewById(R.id.send);
        msgSend = (EditText)findViewById(R.id.msgSend);
        msgShow = (TextView)findViewById(R.id.msgShow);
        groupShow = (Button) findViewById(R.id.groupShow);
        msgShowScroll = (ScrollView) findViewById(R.id.msgShowScroll);

        //注册广播并接受
        IntentFilter filter = new IntentFilter("resBroadcast");
        registerReceiver(receiver, filter);

        Bundle userData = getIntent().getExtras();
        groupId = userData.getString("groupId");
        msgShow.setText(userData.getString("msg"));
        rolldowm();

        send.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (msgSend.getText()!= null && !msgSend.getText().toString().isEmpty()){
                    Intent intent = new Intent("reqBroadcast");
                    intent.putExtra("msg", "msg"+"\n"+groupId+"\n"+ msgSend.getText().toString()+"\n");
                    sendBroadcast(intent);
                }
            }
        });

        groupShow.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("reqBroadcast");
                intent.putExtra("msg", "groupShow"+"\n"+ groupId +"\n");
                sendBroadcast(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        unregisterReceiver(receiver);
        finish();
        return;
    }

    public void rolldowm(){
        msgShowScroll.post(new Runnable() {
            public void run() {
                msgShowScroll.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

}
