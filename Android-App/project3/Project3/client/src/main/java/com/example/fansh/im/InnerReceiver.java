package com.example.fansh.im;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by fansh on 2016/11/26.
 */
public class InnerReceiver extends BroadcastReceiver {

    private String msg;
    @Override
    public void onReceive(Context context, Intent intent) {
        //使用intent获取发送过来的数据
        msg = intent.getStringExtra("msg");
        Log.i("msg","onReceive:"+msg);
    }

    public String getMsg(){
        return this.msg;
    }
}
