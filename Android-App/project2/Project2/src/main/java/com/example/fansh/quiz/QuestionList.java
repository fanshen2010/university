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

public class QuestionList extends AppCompatActivity {

    private DBQuestion dbquestion;
    private TableLayout tableLayout;
    private Button questionAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_list);

        dbquestion = new DBQuestion(this,null,null,1);
        List<Question> questionList = dbquestion.getQuestions();
        tableLayout = (TableLayout)findViewById(R.id.questionTable);
        tableLayout.setStretchAllColumns(true);

        for(Question question:questionList){
            TableRow tableRow = new TableRow(this);
            TextView textView1 = new TextView(this);
            TextView textView2 = new TextView(this);
            TextView textView3 = new TextView(this);
            textView1.setText(question.getId().toString());
            if(question.getQuestion().length()<=6){
                textView2.setText(question.getQuestion());
            }else{
                textView2.setText(question.getQuestion().substring(0,6)+"...");
            }
            textView3.setText(question.getAnswer());
            textView1.setBackgroundResource(R.drawable.shapee);
            textView2.setBackgroundResource(R.drawable.shapee);
            textView3.setBackgroundResource(R.drawable.shapee);
            tableRow.addView(textView1);
            tableRow.addView(textView2);
            tableRow.addView(textView3);
            tableRow.setId(question.getId());
            tableRow.setOnClickListener(
                    new View.OnClickListener(){
                        public void onClick(View v){
                            tableClick(v);
                        }
                    }
            );
            tableLayout.addView(tableRow);
        }

        questionAdd = (Button)findViewById(R.id.questionAdd);
        questionAdd.setOnClickListener(
                new View.OnClickListener(){
                    public void onClick(View v){
                        buttonClick(v);
                    }
                }
        );

    }

    private void tableClick(View v){
        finish();
        Intent i = new Intent(this,QuestionAdd.class);
        i.putExtra("questionId",v.getId());
        startActivity(i);
    }

    private void buttonClick(View v){
        finish();
        Intent i = new Intent(this,QuestionAdd.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}
