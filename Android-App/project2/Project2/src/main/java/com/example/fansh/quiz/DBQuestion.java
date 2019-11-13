package com.example.fansh.quiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteFullException;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fansh on 2016/10/12.
 */
public class DBQuestion extends SQLiteOpenHelper {

    @Override
    public void onCreate(SQLiteDatabase db){
        String query = "CREATE TABLE question (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "question TEXT,"+
                "choice TEXT,"+
                "answer TEXT,"+
                "time INTEGER"+
                ");";
        db.execSQL(query);
    }

    public DBQuestion(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "question.db", factory, 1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXIST question";
        db.execSQL(query);
        onCreate(db);
    }

    public List<Question> getQuestions(){
        String sql = "select * from question";
        SQLiteDatabase db = getWritableDatabase();
        List<Question> questionList = new ArrayList<Question>();
        Question question = null;
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            question = new Question();
            question.setId(cursor.getInt(cursor.getColumnIndex("id")));
            question.setQuestion(cursor.getString(cursor.getColumnIndex("question")));
            question = getJsonObject(question,cursor.getString(cursor.getColumnIndex("choice")));
            question.setAnswer(cursor.getString(cursor.getColumnIndex("answer")));
            question.setTime(cursor.getInt(cursor.getColumnIndex("time")));
            questionList.add(question);
        }
        db.close();
        return questionList;
    }

    public Question getQuestion(Integer questionId){
        String sql = "select * from question where id='"+questionId+"'";
        SQLiteDatabase db = getWritableDatabase();
        Question question = null;
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            question = new Question();
            question.setId(cursor.getInt(cursor.getColumnIndex("id")));
            question.setQuestion(cursor.getString(cursor.getColumnIndex("question")));
            question = getJsonObject(question,cursor.getString(cursor.getColumnIndex("choice")));
            question.setAnswer(cursor.getString(cursor.getColumnIndex("answer")));
            question.setTime(cursor.getInt(cursor.getColumnIndex("time")));
        }
        db.close();
        return question;
    }

    public Question getUnfinishedQuestion(String finishedQuestionId){
        String sql = "";
        if(finishedQuestionId==null || finishedQuestionId.isEmpty()){
            sql = "select * from question limit 0,1";
        }else{
            sql = "select * from question where id not in ("+finishedQuestionId+") limit 0,1";
        }
        SQLiteDatabase db = getWritableDatabase();
        Question question = null;
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            question = new Question();
            question.setId(cursor.getInt(cursor.getColumnIndex("id")));
            question.setQuestion(cursor.getString(cursor.getColumnIndex("question")));
            question = getJsonObject(question,cursor.getString(cursor.getColumnIndex("choice")));
            question.setAnswer(cursor.getString(cursor.getColumnIndex("answer")));
            question.setTime(cursor.getInt(cursor.getColumnIndex("time")));
        }
        db.close();
        return question;
    }

    public Integer getUnfinishedQuestionCount(String finishedQuestionId){
        String sql = "";
        if(finishedQuestionId==null || finishedQuestionId.isEmpty()){
            sql = "select count(*) as count from question";
        }else{
            sql = "select count(*) as count from question where id not in ("+finishedQuestionId+")";
        }
        SQLiteDatabase db = getWritableDatabase();
        Integer count = 0;
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            count = cursor.getInt(cursor.getColumnIndex("count"));
        }
        db.close();
        return count;
    }

    public boolean addQuestion(Question question) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("question", question.getQuestion());
        values.put("choice", getJsonStr(question));
        values.put("answer", question.getAnswer());
        values.put("time", question.getTime());
        try{
            db.insertOrThrow("question", null, values);
        }catch (SQLiteFullException e){
            db.close();
            return false;
        }
        db.close();
        return true;
    }

    public void modifyQuestion(Question question){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("question", question.getQuestion());
        values.put("choice", getJsonStr(question));
        values.put("answer", question.getAnswer());
        values.put("time", question.getTime());
        db.update("question", values, "id = ?", new String[]{question.getId().toString()});
        db.close();
    }

    public void deleteQuestion(Integer questionId){
        SQLiteDatabase db = getWritableDatabase();
        db.delete("question", "id = ?", new String[]{questionId.toString()});
        db.close();
    }


    public Question getJsonObject(Question question,String questionStr){
        try{
            JSONObject object = new JSONObject(questionStr);
            question.setQ1(object.getString("q1"));
            question.setQ2(object.getString("q2"));
            question.setQ3(object.getString("q3"));
            question.setQ4(object.getString("q4"));
        }catch(JSONException e){
            e.getMessage();
        }
        return question;
    }

    public String getJsonStr(Question question){
        JSONObject object = new JSONObject();
        try {
            object.put("q1",question.getQ1());
            object.put("q2",question.getQ2());
            object.put("q3",question.getQ3());
            object.put("q4",question.getQ4());
        }catch(JSONException e) {
            e.getMessage();
        }
        return object.toString();
    }

}
