package com.example.fansh.quiz;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class QuestionAnswer extends AppCompatActivity {

    class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onFinish() {
            timer.setText("Time: 0 sec");
            showAnswer();
        }
        @Override
        public void onTick(long millisUntilFinished) {
            timer.setText("Time: " + millisUntilFinished / 1000 + "sec");
        }
    }

    private MyCountDownTimer mc;
    private Integer userId;
    private String userName;
    private Integer totalCorrectCount;
    private Integer totalCount;
    private Integer quizCorrectCount;
    private Integer quizCount;
    private Integer quizValid;
    private Integer questionId;
    private Integer time;
    private Integer tryAnswer=0;

    private DBUser dbuser;
    private DBQuestion dbquestion;
    private DBUserQuestion dbuserquestion;
    private String choice;
    private String answer;

    private RelativeLayout layout;
    private TextView timer;
    private RadioGroup radioGroupAnswer;
    private TextView questionView;
    private TextView questionRecord;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_answer);

        layout = (RelativeLayout) findViewById(R.id.answerLayout);
        questionView = (TextView)findViewById(R.id.questionView);
        questionRecord = (TextView) findViewById(R.id.questionRecord);
        radioGroupAnswer = (RadioGroup) findViewById(R.id.radioGroupAnswer);
        timer = (TextView)findViewById(R.id.timer);
        radioGroupAnswer.setEnabled(false);
        radioGroupAnswer.setClickable(false);

        RadioButton q1 = (RadioButton)findViewById(R.id.c1);
        RadioButton q2 = (RadioButton)findViewById(R.id.c2);
        RadioButton q3 = (RadioButton)findViewById(R.id.c3);
        RadioButton q4 = (RadioButton)findViewById(R.id.c4);

        // Intent Bundle
        Bundle userData = getIntent().getExtras();
        if(userData==null){
            layout.removeAllViews();
            return;
        }else{
            userId = userData.getInt("userId");
            userName = userData.getString("userName");
            totalCount = userData.getInt("totalCount");
            totalCorrectCount = userData.getInt("totalCorrectCount");
            quizCount = userData.getInt("quizCount");
            quizCorrectCount = userData.getInt("quizCorrectCount");
            quizValid = userData.getInt("quizValid");
        }

        // DB
        dbuser = new DBUser(this,null,null,1);
        dbquestion = new DBQuestion(this,null,null,1);
        dbuserquestion = new DBUserQuestion(this,null,null,1);
        String questionIds = dbuserquestion.getFinishedQuestionId(userId);
        Question question = dbquestion.getUnfinishedQuestion(questionIds);
        if(quizValid==0) quizValid= dbquestion.getUnfinishedQuestionCount(questionIds);
        if(question==null || quizValid<5){
            layout.removeAllViews();
            showDialogNoQuiz();
            return;
        }else{
            questionId = question.getId();
        }

        // View value
        questionView.setText(question.getQuestion());
        q1.setText(question.getQ1());
        q2.setText(question.getQ2());
        q3.setText(question.getQ3());
        q4.setText(question.getQ4());
        questionRecord.setText(userName+":  "+totalCorrectCount+"/"+totalCount);
        answer = question.getAnswer();
        time = question.getTime();

        // timer
        mc = new MyCountDownTimer(time*1000, 1000);
        mc.start();

        // radioGroup
        radioGroupAnswer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Button button = (Button)findViewById(radioGroupAnswer.getCheckedRadioButtonId());
                choice = button.getTag().toString();
                mc.onFinish();
                mc.cancel();
            }
        });

//        radioGroupAnswer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                Button button = (Button)findViewById(radioGroupAnswer.getCheckedRadioButtonId());
//                choice = button.getTag().toString();
//                for (int i = 0; i<radioGroupAnswer.getChildCount();i++) {
//                    radioGroupAnswer.getChildAt(i).setEnabled(false);
//                }
//                mc.onFinish();
//                mc.cancel();
//            }
//        });

    }

    public void next(){
        finish();
        Intent i = new Intent(this,QuestionAnswer.class);
        i.putExtra("userId",userId);
        i.putExtra("userName",userName);
        i.putExtra("totalCount",totalCount);
        i.putExtra("totalCorrectCount",totalCorrectCount);
        i.putExtra("quizCount",quizCount);
        i.putExtra("quizCorrectCount",quizCorrectCount);
        i.putExtra("quizValid",quizValid);
        startActivity(i);
    }

    public void showDialog(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        myDialog.setMessage(quizCorrectCount+"/"+quizCount+","+totalCorrectCount+"/"+totalCount);
        myDialog.setPositiveButton("continue", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                next();
            }
        });
        myDialog.setNegativeButton("logout", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();;
            }
        });
        myDialog.setCancelable(false);
        myDialog.show();
    }

    public void showDialogNoQuiz(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        myDialog.setMessage("no quiz");
        myDialog.setPositiveButton("logout", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        myDialog.setCancelable(false);
        myDialog.show();
    }

    public void showDialogError(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        myDialog.setMessage("database is full");
        myDialog.setPositiveButton("logout", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        myDialog.setCancelable(false);
        myDialog.show();
    }

    public void showAnswer(){
        if(answer.equals(choice)){
            // right
            for (int i = 0; i < radioGroupAnswer.getChildCount(); i++) {
                radioGroupAnswer.getChildAt(i).setEnabled(false);
            }
            if(tryAnswer==0){
                // correct answer
                totalCorrectCount++;
                quizCorrectCount++;
            }
            quizCount++;
            totalCount++;
            if(!dbuserquestion.addUserQuestion(userId,questionId)){
                showDialogError();
            }else{
                dbuser.modifyUserPerformance(userId,totalCount,totalCorrectCount);
                if(quizCount<5){
                    next();
                }else{
                    showDialog();
                    quizCorrectCount=0;
                    quizCount=0;
                    quizValid=0;
                }
            }
        }else{
            Button chooseButton = (Button)findViewById(radioGroupAnswer.getCheckedRadioButtonId());
            if(!answer.equals(choice) && choice!=null && !choice.isEmpty()){
                chooseButton.setTextColor(Color.RED);
            }
            for (int i = 0; i < radioGroupAnswer.getChildCount(); i++) {
                if(radioGroupAnswer.getChildAt(i).getTag().equals(answer)){
                    Button rightButton = (Button) radioGroupAnswer.getChildAt(i);
                    ObjectAnimator colorAnim = ObjectAnimator.ofInt(rightButton, "textColor", Color.GREEN, Color.WHITE);
                    colorAnim.setDuration(500);
                    colorAnim.setEvaluator(new ArgbEvaluator());
                    colorAnim.setRepeatCount(ValueAnimator.INFINITE);
                    colorAnim.setRepeatMode(ValueAnimator.REVERSE);
                    colorAnim.start();
                }
            }
            tryAnswer = 1;
        }
    }

//    public void showAnswer(){
//        //anim
//        questionAnswer.setText(answer);
//        if(answer.equals(choice)|| choice==null||choice.isEmpty()){
//            anim = new AlphaAnimation(0.0f, 1.0f);
//            questionAnswer.setTextColor(Color.GREEN);
//        }else{
//            anim = new AlphaAnimation(1.0f, 1.0f);
//            questionAnswer.setTextColor(Color.RED);
//        }
//        anim.setDuration(500);
//        anim.setStartOffset(20);
//        anim.setRepeatCount(2);
//        questionAnswer.setAnimation(anim);
//
//        anim.setAnimationListener(new Animation.AnimationListener(){
//            @Override
//            public void onAnimationStart(Animation arg0) {
//            }
//            @Override
//            public void onAnimationRepeat(Animation arg0) {
//            }
//            @Override
//            public void onAnimationEnd(Animation arg0) {
//                if(answer.equals(choice)){
//                    totalCorrectCount++;
//                    quizCorrectCount++;
//                }
//                quizCount++;
//                totalCount++;
//
//                if(!dbuserquestion.addUserQuestion(userId,questionId)){
//                    showDialogError();
//                }else{
//                    dbuser.modifyUserPerformance(userId,totalCount,totalCorrectCount);
//                    if(quizCount<5){
//                        next();
//                    }else{
//                        showDialog();
//                        quizCorrectCount=0;
//                        quizCount=0;
//                        quizValid=0;
//                    }
//                }
//            }
//        });
//    }

    @Override
    public void onBackPressed() {
    }

}
