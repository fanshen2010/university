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
 * Created by fansh on 2016/10/16.
 */
public class DBUserQuestion extends SQLiteOpenHelper {
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE userQuestion (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "userId INTEGER,"+
                "questionId INTEGER"+
                ");";
        db.execSQL(query);
    }

    public DBUserQuestion(Context context, String userId, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "userQuestion.db", factory, 1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXIST userQuestion";
        db.execSQL(query);
        onCreate(db);
    }

    public String getFinishedQuestionId(Integer userId){
        String sql = "select questionId from userQuestion where userId='"+userId+"'";
        SQLiteDatabase db = getWritableDatabase();
        String questionIds = "";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            questionIds =questionIds + cursor.getInt(cursor.getColumnIndex("questionId"))+",";
        }
        if(!questionIds.isEmpty()){
            questionIds = questionIds.substring(0,questionIds.length()-1);
        }
        db.close();
        return questionIds;
    }

    public boolean addUserQuestion(Integer userId, Integer questionId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userId", userId);
        values.put("questionId", questionId);
        try {
            db.insertOrThrow("userQuestion", null, values);
        }catch (SQLiteFullException e){
            db.close();
            return false;
        }
        db.close();
        return true;
    }

    public void deleteByUserId(Integer userId){
        SQLiteDatabase db = getWritableDatabase();
        db.delete("userQuestion", "userId = ?", new String[]{userId.toString()});
        db.close();
    }

    public void deleteByQuestionId(Integer questionId){
        SQLiteDatabase db = getWritableDatabase();
        db.delete("userQuestion", "questionId = ?", new String[]{questionId.toString()});
        db.close();
    }

}
