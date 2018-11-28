package com.example.adnanshaukat.myapplication.Modals;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by AdnanShaukat on 28/11/2018.
 */

public class SqlLiteDatabaseHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "users.db";
    private static final int DATABASE_VERSION = 5;
    protected String table_name = "tbl_users";

    private static final String TABLE_CREATE = "CREATE TABLE tbl_users (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "user_id INTEGER, email TEXT, first_name TEXT, user_type_id INTEGER, phone_number TEXT, password TEXT, is_logged_in INTEGER)";

    public SqlLiteDatabaseHandler(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tbl_users");
        db.execSQL(TABLE_CREATE);
    }
}
