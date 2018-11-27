package com.example.adnanshaukat.myapplication.GlobalClasses;

import android.app.Application;

/**
 * Created by AdnanShaukat on 28/11/2018.
 */

public class MyApplication extends Application {
    private int user_id;

    public int get_user_id() {
        return user_id;
    }

    public void set_user_id(int user_id) {
        this.user_id= user_id;
    }
}
