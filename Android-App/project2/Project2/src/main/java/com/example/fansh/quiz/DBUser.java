package com.example.fansh.quiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteFullException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fansh on 2016/10/12.
 */
public class DBUser extends SQLiteOpenHelper {

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE user (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT,"+
                "password TEXT,"+
                "totalAnswered INTEGER,"+
                "totalCorrect INTEGER"+
                ");";
        db.execSQL(query);
    }

    public DBUser(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "user.db", factory, 1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXIST user";
        db.execSQL(query);
        onCreate(db);
    }

    public List<User> getUsers(){
        String sql = "select * from user";
        SQLiteDatabase db = getWritableDatabase();
        List<User> userList = new ArrayList<User>();
        User user = null;
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndex("id")));
            user.setName(cursor.getString(cursor.getColumnIndex("name")));
            user.setPassword(cursor.getString(cursor.getColumnIndex("password")));
            user.setTotalAnswered(cursor.getInt(cursor.getColumnIndex("totalAnswered")));
            user.setTotalCorrect(cursor.getInt(cursor.getColumnIndex("totalCorrect")));
            userList.add(user);
        }
        db.close();
        return userList;
    }

    public User getUserById(Integer userId){
        String sql = "select * from user where id='"+userId+"'";
        SQLiteDatabase db = getWritableDatabase();
        User user = null;
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndex("id")));
            user.setName(cursor.getString(cursor.getColumnIndex("name")));
            user.setPassword(cursor.getString(cursor.getColumnIndex("password")));
            user.setTotalAnswered(cursor.getInt(cursor.getColumnIndex("totalAnswered")));
            user.setTotalCorrect(cursor.getInt(cursor.getColumnIndex("totalCorrect")));
        }
        db.close();
        return user;
    }

    public boolean addUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", user.getName());
        values.put("password", user.getPassword());
        values.put("totalAnswered", 0);
        values.put("totalCorrect", 0);
        try{
            db.insertOrThrow("user", null, values);
        }catch (SQLiteFullException e){
            db.close();
            return false;
        }
        db.close();
        return true;
    }

    public void modifyUser(User user){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", user.getName());
        values.put("password", user.getPassword());
        values.put("totalAnswered", user.getTotalAnswered());
        values.put("totalCorrect", user.getTotalCorrect());
        db.update("user", values, "id = ?", new String[]{user.getId().toString()});
        db.close();
    }

    public void modifyUserPerformance(Integer userId, Integer totalAnswered, Integer totalCorrect){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("totalAnswered", totalAnswered);
        values.put("totalCorrect",totalCorrect);
        db.update("user", values, "id = ?", new String[]{userId.toString()});
        db.close();

//        SQLiteDatabase db = getWritableDatabase();
//        String sql = "UPDATE user SET totalAnswered ="+totalAnswered+",totalCorrect="+totalCorrect+" WHERE id="+userId+";";
//        db.rawQuery(sql,null);
//        db.close();
    }

    public void deleteUser(Integer userId){
        SQLiteDatabase db = getWritableDatabase();
        db.delete("user", "id = ?", new String[]{userId.toString()});
        db.close();
    }

    public User checkUser(String userName){
        String sql = "select * from user where name='"+userName+"'";
        SQLiteDatabase db = getWritableDatabase();
        User user= null;
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndex("id")));
            user.setName(cursor.getString(cursor.getColumnIndex("name")));
            user.setPassword(cursor.getString(cursor.getColumnIndex("password")));
            user.setTotalAnswered(cursor.getInt(cursor.getColumnIndex("totalAnswered")));
            user.setTotalCorrect(cursor.getInt(cursor.getColumnIndex("totalCorrect")));
        }
        db.close();
        return user;
    }

    public User checkPassword(String userName,String password){
        String sql = "select * from user where name='"+userName+"' and password='"+password+"'";
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        User user = null;
        while (cursor.moveToNext()) {
            user = new User();
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndex("id")));
            user.setName(cursor.getString(cursor.getColumnIndex("name")));
            user.setPassword(cursor.getString(cursor.getColumnIndex("password")));
            user.setTotalAnswered(cursor.getInt(cursor.getColumnIndex("totalAnswered")));
            user.setTotalCorrect(cursor.getInt(cursor.getColumnIndex("totalCorrect")));
        }
        db.close();
        return user;
    }

}
