package com.example.fansh.im;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by fansh on 2016/11/23.
 */
public class MyDialog extends DialogFragment {

    Button settingIPSubmit;
    EditText settingIP;

    Button groupAdd;
    EditText groupAddView;

    Button registerSubmit;
    EditText registerName;
    EditText registerPassword;

    ListView groupButtonList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        String result;

        Bundle bundle = getArguments();
        String type = bundle.getString("type");
        String data = bundle.getString("data");
        View v;

        if("setting".equals(type)) {
            // dialog text
            v = inflater.inflate(R.layout.setting_ip, null);
            getDialog().setTitle("Setting");
            settingIPSubmit = (Button) v.findViewById(R.id.settingIPSubmit);
            settingIP = (EditText) v.findViewById(R.id.settingIP);
            settingIP.setText(data);
            settingIPSubmit.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String ip = settingIP.getText().toString();
                    if (!ip.isEmpty()) {
                        MainActivity mainActivity = (MainActivity) getActivity();
                        mainActivity.setIp(ip);
                        getDialog().cancel();
                    }
                }
            });
        }else if("register".equals(type)){
            // dialog text
            v = inflater.inflate(R.layout.register, null);
            getDialog().setTitle("Register");
            registerSubmit = (Button) v.findViewById(R.id.registerSubmit);
            registerName = (EditText) v.findViewById(R.id.registerName);
            registerPassword = (EditText) v.findViewById(R.id.registerPassword);
            registerSubmit.setOnClickListener(new Button.OnClickListener(){
                @Override
                public void onClick(View v) {
                    String name = registerName.getText().toString();
                    String password = registerPassword.getText().toString();
                    if(!name.isEmpty() && !password.isEmpty()){
                        MainActivity mainActivity = (MainActivity) getActivity();
                        mainActivity.out("register"+"\n"+name+"\n"+password+"\n");
                        getDialog().cancel();
                    }
                }
            });
        }else if("groupToAdd".equals(type)){
            // dialog text
            v = inflater.inflate(R.layout.group_add, null);
            getDialog().setTitle("groupToAdd");
            groupAdd = (Button) v.findViewById(R.id.groupAdd);
            groupAddView = (EditText) v.findViewById(R.id.groupAddView);
            groupAdd.setOnClickListener(new Button.OnClickListener(){
                @Override
                public void onClick(View v) {
                    String groupName = groupAddView.getText().toString();
                    if(!groupName.isEmpty()){
                        ChatList chatList = (ChatList) getActivity();
                        chatList.sendOutByDialog("groupAdd"+"\n"+groupName+"\n");
                        getDialog().cancel();
                    }
                }
            });
        }else{
            // dialog list
            Scanner sc = new Scanner(data);
            String line;
            List<String> dataList = new ArrayList<String>();
            while (sc.hasNext()) {
                line = sc.nextLine();
                if (line != null && !line.isEmpty()) {
                    dataList.add(line);
                }
            }
            v = inflater.inflate(R.layout.group_list, null);
            groupButtonList = (ListView) v.findViewById(R.id.groupButtonList);
            ListAdapter adapter = new ArrayAdapter<String>(v.getContext(), R.layout.group_item, dataList);
            groupButtonList.setAdapter(adapter);

            if("groupShow".equals(type)) {
                getDialog().setTitle("groupShow");
            }else if("groupToJoin".equals(type)) {
                getDialog().setTitle("groupToJoin");
                groupButtonList.setOnItemClickListener(
                        new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Object item = parent.getItemAtPosition(position);
                                ChatList chatList = (ChatList) getActivity();
                                chatList.sendOutByDialog("groupJoin"+"\n"+String.valueOf(item)+"\n");
                                getDialog().cancel();
                            }
                        }
                );
            }else if("groupToQuit".equals(type)) {
                getDialog().setTitle("groupToQuit");
                groupButtonList.setOnItemClickListener(
                        new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Object item = parent.getItemAtPosition(position);
                                ChatList chatList = (ChatList) getActivity();
                                chatList.sendOutByDialog("groupQuit"+"\n"+String.valueOf(item)+"\n");
                                getDialog().cancel();
                            }
                        }
                );
            }
        }
        return v;
    }

}
