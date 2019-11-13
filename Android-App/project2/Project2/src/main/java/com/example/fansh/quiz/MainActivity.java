package com.example.fansh.quiz;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{

    private EditText username;
    private EditText password;
    private Button login;
    private DBUser dbuser;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        login(v);
                    }
                }
        );
    }

    public void login(View view){
        dbuser = new DBUser(this,null,null,1);
        String usernameStr = username.getText().toString();
        String passwordStr = password.getText().toString();
        if(!usernameStr.isEmpty() && !passwordStr.isEmpty()){
            if("admin".equals(usernameStr) && "123456".equals(passwordStr)) {
                //admin
                Intent i = new Intent(this, AdminIndex.class);
                startActivity(i);
            }else{
                //user
                User user = dbuser.checkPassword(usernameStr,passwordStr);
                if(user!=null){
                    Intent i = new Intent(this,QuestionAnswer.class);
                    i.putExtra("userId",user.getId());
                    i.putExtra("userName",user.getName());
                    i.putExtra("totalCount",user.getTotalAnswered());
                    i.putExtra("totalCorrectCount",user.getTotalCorrect());
                    i.putExtra("quizCount",0);
                    i.putExtra("quizCorrectCount",0);
                    i.putExtra("quizValid",0);
                    startActivity(i);
                }else{
                    Toast.makeText(MainActivity.this,"username or password is wrong", Toast.LENGTH_LONG).show();
                }
            }
        }else{
            Toast.makeText(MainActivity.this,"username or password is empty", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
