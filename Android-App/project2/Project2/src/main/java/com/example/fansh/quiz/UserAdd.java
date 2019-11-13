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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserAdd extends AppCompatActivity {

    private DBUser dbuser;
    private DBUserQuestion dbuserquestion;
    private EditText userNameAdd;
    private EditText passwordAdd;
    private EditText totalCountAdd;
    private EditText correctCountAdd;
    private Button userAddSubmit;
    private Button userDeleteSubmit;
    private LinearLayout totalCountAddLayout;
    private LinearLayout correctCountAddLayout;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_add);
        Bundle userData = getIntent().getExtras();
        dbuser = new DBUser(this,null,null,1);
        dbuserquestion = new DBUserQuestion(this,null,null,1);
        if(userData!=null){
            int userId = userData.getInt("userId");
            user = dbuser.getUserById(userId);
        }
        if(user==null){
            user = new User();
            user.setTotalAnswered(0);
            user.setTotalCorrect(0);
        }

        totalCountAddLayout = (LinearLayout)findViewById(R.id.totalCountAddLayout);
        correctCountAddLayout = (LinearLayout)findViewById(R.id.correctCountAddLayout);
        userNameAdd = (EditText) findViewById(R.id.userNameAdd);
        passwordAdd = (EditText) findViewById(R.id.passwordAdd);
        totalCountAdd = (EditText) findViewById(R.id.totalCountAdd);
        correctCountAdd = (EditText) findViewById(R.id.correctCountAdd);
        userAddSubmit = (Button) findViewById(R.id.userAddSubmit);
        userDeleteSubmit = (Button) findViewById(R.id.userDeleteSubmit);

        userNameAdd.setText(user.getName());
        passwordAdd.setText(user.getPassword());
        totalCountAdd.setText(user.getTotalAnswered().toString());
        correctCountAdd.setText(user.getTotalCorrect().toString());

        if(user.getId()==null){
            //add
            userAddSubmit.setText("Add User");
            userDeleteSubmit.setVisibility(View.GONE);
            totalCountAddLayout.setVisibility(View.GONE);
            correctCountAddLayout.setVisibility(View.GONE);
        }

        userDeleteSubmit.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        dbuserquestion.deleteByUserId(user.getId());
                        dbuser.deleteUser(user.getId());
                        Toast.makeText(UserAdd.this, "user has deleted", Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }
                }
        );

        userAddSubmit.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        String usernameStr = userNameAdd.getText().toString();
                        String passwordStr = passwordAdd.getText().toString();
                        String totalCountStr = "0";
                        String correctCountStr = "0";
                        if(user.getId()!=null){
                            //modify
                            totalCountStr = totalCountAdd.getText().toString();
                            correctCountStr = correctCountAdd.getText().toString();
                        }
                        if(!usernameStr.isEmpty() && !passwordStr.isEmpty() && checkCount(totalCountStr) && checkCount(correctCountStr)){
                            User userTemp = dbuser.checkUser(usernameStr);
                            Integer userId = user.getId();
                            if(userId==null && userTemp!=null) {
                                //add
                                Toast.makeText(UserAdd.this, "user has exist", Toast.LENGTH_LONG).show();
                            }else if(userId!=null && userTemp.getId()!=userId){
                                // modify
                                Toast.makeText(UserAdd.this, "user has exist", Toast.LENGTH_LONG).show();
                            }else{
                                user.setName(usernameStr);
                                user.setPassword(passwordStr);
                                user.setTotalAnswered(Integer.valueOf(totalCountStr));
                                user.setTotalCorrect(Integer.valueOf(correctCountStr));
                                if(user.getId()==null){
                                    //add
                                    if(dbuser.addUser(user)){
                                        Toast.makeText(UserAdd.this,"user add success", Toast.LENGTH_LONG).show();
                                    }else{
                                        Toast.makeText(UserAdd.this,"database is full", Toast.LENGTH_LONG).show();
                                    }
                                }else{
                                    //modify
                                    dbuser.modifyUser(user);
                                    Toast.makeText(UserAdd.this,"user changed success", Toast.LENGTH_LONG).show();
                                }
                                onBackPressed();
                            }
                        }else{
                            Toast.makeText(UserAdd.this, "input wrong", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    private boolean checkCount(String countStr){
        Pattern pattern = Pattern.compile("^(0|[1-9]\\d*)$");
        Matcher matcher = pattern.matcher(countStr);
        if(matcher.matches()){
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent i = new Intent(this,UserList.class);
        startActivity(i);
    }

}
