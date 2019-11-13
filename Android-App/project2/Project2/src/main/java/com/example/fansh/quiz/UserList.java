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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

public class UserList extends AppCompatActivity {

    private DBUser dbuser;
    private TableLayout tableLayout;
    private Button userAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        dbuser = new DBUser(this,null,null,1);
        List<User> userList = dbuser.getUsers();
        tableLayout = (TableLayout)findViewById(R.id.userTable);
        tableLayout.setStretchAllColumns(true);

        for(User user:userList){
            TableRow tableRow = new TableRow(this);
            TextView textView1 = new TextView(this);
            TextView textView2 = new TextView(this);
            TextView textView3 = new TextView(this);
            TextView textView4 = new TextView(this);
            textView1.setText(user.getId().toString());
            textView2.setText(user.getName());
            textView3.setText(user.getTotalAnswered().toString());
            textView4.setText(user.getTotalCorrect().toString());
            textView1.setBackgroundResource(R.drawable.shapee);
            textView2.setBackgroundResource(R.drawable.shapee);
            textView3.setBackgroundResource(R.drawable.shapee);
            textView4.setBackgroundResource(R.drawable.shapee);
            tableRow.addView(textView1);
            tableRow.addView(textView2);
            tableRow.addView(textView3);
            tableRow.addView(textView4);
            tableRow.setId(user.getId());
            tableRow.setOnClickListener(
                    new View.OnClickListener(){
                        public void onClick(View v){
                            tableClick(v);
                        }
                    }
            );
            tableLayout.addView(tableRow);
        }

        userAdd = (Button)findViewById(R.id.userAdd);
        userAdd.setOnClickListener(
                new View.OnClickListener(){
                    public void onClick(View v){
                        buttonClick(v);
                    }
                }
        );

    }

    private void tableClick(View v){
        finish();
        Intent i = new Intent(this,UserAdd.class);
        i.putExtra("userId",v.getId());
        startActivity(i);
    }

    private void buttonClick(View v){
        finish();
        Intent i = new Intent(this,UserAdd.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
