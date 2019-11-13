package com.example.fansh.quiz;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuestionAdd extends AppCompatActivity {

    private DBQuestion dbquestion;
    private DBUserQuestion dbuserquestion;
    private EditText questionNameAdd;
    private EditText questionQ1Add;
    private EditText questionQ2Add;
    private EditText questionQ3Add;
    private EditText questionQ4Add;
    private RadioGroup questionAnswerAdd;
    private EditText questionTimeAdd;
    private Button questionAddSubmit;
    private Button questionDeleteSubmit;
    private Question question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_add);
        Bundle questionData = getIntent().getExtras();
        dbquestion = new DBQuestion(this,null,null,1);
        dbuserquestion = new DBUserQuestion(this,null,null,1);
        if(questionData!=null){
            int questionId = questionData.getInt("questionId");
            question = dbquestion.getQuestion(questionId);
        }
        if(question==null){
            question = new Question();
            question.setTime(10);
            question.setAnswer("c1");
        }

        questionNameAdd = (EditText) findViewById(R.id.questionNameAdd);
        questionQ1Add = (EditText) findViewById(R.id.questionQ1Add);
        questionQ2Add = (EditText) findViewById(R.id.questionQ2Add);
        questionQ3Add = (EditText) findViewById(R.id.questionQ3Add);
        questionQ4Add = (EditText) findViewById(R.id.questionQ4Add);
        questionAnswerAdd = (RadioGroup) findViewById(R.id.questionAnswerAdd);
        questionTimeAdd = (EditText) findViewById(R.id.questionTimeAdd);
        questionAddSubmit = (Button) findViewById(R.id.questionAddSubmit);
        questionDeleteSubmit = (Button) findViewById(R.id.questionDeleteSubmit);

        questionNameAdd.setText(question.getQuestion());
        questionQ1Add.setText(question.getQ1());
        questionQ2Add.setText(question.getQ2());
        questionQ3Add.setText(question.getQ3());
        questionQ4Add.setText(question.getQ4());
        questionTimeAdd.setText(question.getTime().toString());
        if(question.getId()==null){
            //add
            questionAddSubmit.setText("Add Quiz");
            questionDeleteSubmit.setVisibility(View.GONE);
        }
        if("c1".equals(question.getAnswer())){
            questionAnswerAdd.check(R.id.q1Add);
        }else if("c2".equals(question.getAnswer())){
            questionAnswerAdd.check(R.id.q2Add);
        }else if("c3".equals(question.getAnswer())){
            questionAnswerAdd.check(R.id.q3Add);
        }else if("c4".equals(question.getAnswer())){
            questionAnswerAdd.check(R.id.q4Add);
        }

        questionDeleteSubmit.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        dbuserquestion.deleteByQuestionId(question.getId());
                        dbquestion.deleteQuestion(question.getId());
                        Toast.makeText(QuestionAdd.this, "quiz has deleted", Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }
                }
        );

        questionAddSubmit.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        String questionNameStr = questionNameAdd.getText().toString();
                        String q1Str = questionQ1Add.getText().toString();
                        String q2Str = questionQ2Add.getText().toString();
                        String q3Str = questionQ3Add.getText().toString();
                        String q4Str = questionQ4Add.getText().toString();
                        String timeStr = questionTimeAdd.getText().toString();
                        Button checkbutton = (Button)findViewById(questionAnswerAdd.getCheckedRadioButtonId());
                        String answerStr = checkbutton.getText().toString();
                        if(!questionNameStr.isEmpty() && !q1Str.isEmpty() && !q2Str.isEmpty() && !q3Str.isEmpty() && !q4Str.isEmpty()
                                && !answerStr.isEmpty() && checkTime(timeStr)){
                            question.setQuestion(questionNameStr);
                            question.setQ1(q1Str);
                            question.setQ2(q2Str);
                            question.setQ3(q3Str);
                            question.setQ4(q4Str);
                            question.setTime(Integer.valueOf(timeStr));
                            question.setAnswer(answerStr);
                            if(question.getId()==null){
                                //add
                                if(dbquestion.addQuestion(question)){
                                    Toast.makeText(QuestionAdd.this,"question add success", Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(QuestionAdd.this,"database is full", Toast.LENGTH_LONG).show();
                                }
                            }else{
                                //modify
                                dbquestion.modifyQuestion(question);
                                Toast.makeText(QuestionAdd.this,"question changed success", Toast.LENGTH_LONG).show();
                            }
                            onBackPressed();
                        }else{
                            Toast.makeText(QuestionAdd.this, "input wrong", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    private boolean checkTime(String timeStr){
        Pattern pattern = Pattern.compile("^[1-9]\\d*$");
        Matcher matcher = pattern.matcher(timeStr);
        if(matcher.matches()){
            int timeInt = Integer.valueOf(timeStr);
            if(timeInt<=30 && timeInt>=10){
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent i = new Intent(this,QuestionList.class);
        startActivity(i);
    }

}
