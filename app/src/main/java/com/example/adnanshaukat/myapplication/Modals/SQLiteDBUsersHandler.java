package com.example.adnanshaukat.myapplication.Modals;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by AdnanShaukat on 28/11/2018.
 */

public class SQLiteDBUsersHandler extends SqlLiteDatabaseHandler {
    public SQLiteDBUsersHandler(Context context) {
        super(context);
    }
    SQLiteDatabase db = this.getWritableDatabase();

    ContentValues values = new ContentValues();

    public boolean create(User user){
        values.put("user_id", user.getUser_id());
        values.put("user_type_id", user.getUser_type_id());
        values.put("first_name", user.getFirst_name());
        values.put("phone_number", user.getPhone_number());
        values.put("password", user.getPassword());
        values.put("is_logged_in", 1);
        boolean result = db.insert("tbl_users", null, values) > 0;
        db.close();
        return result;
    }

    public boolean update_logged_in_status(int is_logged_in, int user_id) {

        ContentValues values = new ContentValues();

        values.put("is_logged_in", is_logged_in);

        String where = "user_id = ?";

        String[] whereArgs = { Integer.toString(user_id) };

        SQLiteDatabase db = this.getWritableDatabase();

        boolean updateSuccessful = db.update("tbl_users", values, where, whereArgs) > 0;
        db.close();

        return updateSuccessful;
    }
}
