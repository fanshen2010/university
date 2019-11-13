package com.example.fansh.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

public class AdminIndex extends AppCompatActivity {

    private Button userIndex;
    private Button questionIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_index);

        userIndex = (Button)findViewById(R.id.userIndex);
        questionIndex = (Button)findViewById(R.id.questionIndex);

        userIndex.setOnClickListener(
                new View.OnClickListener(){
                    public void onClick(View v){
                        userManage(v);
                    }
                }
        );

        questionIndex.setOnClickListener(
                new View.OnClickListener(){
                    public void onClick(View v){
                        questionManage(v);
                    }
                }
        );

    }

    private void userManage(View v){
        Intent i = new Intent(this,UserList.class);
        startActivity(i);
    }

    private void questionManage(View v){
        Intent i = new Intent(this,QuestionList.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
