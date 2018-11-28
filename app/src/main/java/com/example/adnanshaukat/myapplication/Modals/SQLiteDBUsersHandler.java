package com.example.adnanshaukat.myapplication.Modals;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by AdnanShaukat on 28/11/2018.
 */

public class SQLiteDBUsersHandler extends SqlLiteDatabaseHandler {
    public SQLiteDBUsersHandler(Context context) {
        super(context);
    }


    ContentValues values = new ContentValues();

    public boolean create(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        values.put("user_id", user.getUser_id());
        values.put("user_type_id", user.getUser_type_id());
        values.put("first_name", user.getFirst_name());
        values.put("phone_number", user.getPhone_number());
        values.put("password", user.getPassword());
        values.put("email", user.getEmail());
        values.put("is_logged_in", 1);
        boolean result = db.insert("tbl_users", null, values) > 0;
        db.close();
        return result;
    }

    public boolean update_logged_in_status(int is_logged_in, User user) {

        if(!check_if_user_already_present(user)){
            return create(user);
        }
        else{
            ContentValues values = new ContentValues();

            values.put("is_logged_in", is_logged_in);

            if(is_logged_in != 0){
                values.put("password", user.getPassword());
                values.put("email", user.getEmail());
            }

            String where = "user_id = ?";

            String[] whereArgs = { Integer.toString(user.getUser_id()) };

            SQLiteDatabase db = this.getWritableDatabase();

            boolean updateSuccessful = db.update("tbl_users", values, where, whereArgs) > 0;
            db.close();

            return updateSuccessful;
        }

    }

    public User get_logged_in_user(){
        String[] columns = {"email", "password"};
        String where ="is_logged_in = ?";
        String[] whereArgs ={ "1" };
        SQLiteDatabase db = this.getReadableDatabase();

        User users = new User();
        //Cursor cursor = db.query(this.table_name, columns, where, whereArgs,null,null,null);
        Cursor cursor = db.query(this.table_name, columns, where, whereArgs,null, null,null);
        if (cursor != null){
            cursor.moveToFirst();
            if(cursor.getCount() > 0 && cursor.getColumnCount() > 0){
                users.setEmail(cursor.getString(0));
                users.setPassword(cursor.getString(1));
                Log.e("get_logged_in_user", users.getEmail());
                Log.e("get_logged_in_user", users.getPassword());
            }
        }
        db.close();
        return users;
    }

    public boolean check_if_user_already_present(User user){
        String[] columns = {"user_id", "email", "password"};
        SQLiteDatabase db = this.getReadableDatabase();

        String where ="user_id = ?";
        String[] whereArgs ={Integer.toString(user.getUser_id()) };

        int user_id = -1;
        Cursor cursor = db.query(this.table_name, columns, where, whereArgs,null,null,null);
        //Cursor cursor = db.rawQuery("Select email, password where is_logged_in = 1", null);
        if (cursor != null){
            cursor.moveToFirst();
            if(cursor.getCount() > 0 && cursor.getColumnCount() > 0) {
                user_id = cursor.getInt(0);
                Log.e("SQLITE USER ID", Integer.toString(user_id));
            }
        }
        db.close();
        if(user_id != -1){
            return true;
        }
        return false;
    }

    public boolean storeCredentialsToSQLite(User user){
        return update_logged_in_status(1, user);
    }
}
