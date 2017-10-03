package com.oneshot.calculator.editorcalculator;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Tony Choi on 2017-01-31.
 * Local Database Class.
 * Save Calculator History
 */
public class ResultData {

    private static String TABLE = ResultData.class.getSimpleName();
    public int no;
    public String calculus;
    public String result;
    public String date;
    public String time;

    // Create Table only it doesn't exist.
    public static void createTable(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS "
                + TABLE
                + "(no integer primary key autoincrement"
                + ", calculus string, result string, date string, time string)";
        db.execSQL(query);

        query = "CREATE INDEX IF NOT EXISTS no_index ON " + TABLE + "(no)";
        db.execSQL(query);
    }

    //get All Items in Database
    public static ArrayList<ResultData> getAllItems(SQLiteDatabase db) {
        ArrayList<ResultData> itemList = new ArrayList<ResultData>();
        try {
            Cursor cursor = db.query(TABLE, new String[] { "*" }, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    int i = 0;
                    ResultData data = new ResultData();
                    data.no = cursor.getInt(i++);
                    data.calculus = cursor.getString(i++);
                    data.result = cursor.getString(i++);
                    data.date = cursor.getString(i++);
                    data.time = cursor.getString(i++);
                    itemList.add(data);
                    cursor.moveToNext();
                }
                cursor.close();
            }
        } catch (Exception e) {}
        return itemList;
    }

    //Insert Database
    public static int insert(SQLiteDatabase db, ResultData data) {
        ContentValues values = new ContentValues();
        values.put("calculus", data.calculus);
        values.put("result", data.result);
        values.put("date", data.date);
        values.put("time", data.time);

        try {
            db.beginTransaction();
            data.no = (int) db.insert(TABLE, null, values);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
        } finally {
            db.endTransaction();
        }
        return data.no;
    }

    // update Database
    public static int update(SQLiteDatabase db, ResultData data) {
        ContentValues values = new ContentValues();
        values.put("calculus", data.calculus);
        values.put("result", data.result);
        values.put("date", data.date);
        values.put("time", data.time);

        int result = 0;
        try {
            db.beginTransaction();
            result = db.update(TABLE, values, "no=?", new String[] {String.valueOf(data.no)});
            db.setTransactionSuccessful();
        } catch (SQLException e) {
        } finally {
            db.endTransaction();
        }

        return result;
    }

    // Delete index Data (no = index)
    public static int delete(SQLiteDatabase db, ResultData data) {
        return db.delete(TABLE, "no=?", new String[] {String.valueOf(data.no)});
    }

    // Delete All Data
    public static void deleteAll(SQLiteDatabase db) {
//        String query = "DELETE FROM " + TABLE;
//        db.execSQL(query);
        db.delete(TABLE, null, null);
//        return db.delete(TABLE, null, null);
    }

    public String getCalculus() {
        return calculus;
    }

    public String getResult() {
        return result;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
