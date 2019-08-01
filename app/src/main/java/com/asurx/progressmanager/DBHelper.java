package com.asurx.progressmanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static final String TB_NAME = "works";
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //创建新表
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TB_NAME + "(" +
                MyWork.ID       + " integer primary key, " +
                MyWork.NUMBER   + " integer, "    +
                MyWork.NAME     + " varchar, "    +
                MyWork.CURRENT  + " integer, "    +
                MyWork.TOTAL    + " integer, "    +
                MyWork.WEIGHT   + " integer"      + ")");
    }
    //更新时删除旧表，创建新表
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TB_NAME);
        onCreate(db);
    }
    /**
     * 变更列名
     * @param db
     * @param oldColumn
     * @param newColumn
     * @param typeColumn
     */
    public void updateColumn(SQLiteDatabase db, String oldColumn, String newColumn, String typeColumn){
        try{
            db.execSQL("ALTER TABLE " + TB_NAME + " CHANGE " +
                    oldColumn + " "+ newColumn + " " + typeColumn
            );
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
