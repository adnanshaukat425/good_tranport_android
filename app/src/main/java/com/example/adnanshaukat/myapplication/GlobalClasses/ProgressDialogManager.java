package com.example.adnanshaukat.myapplication.GlobalClasses;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by AdnanShaukat on 18/11/2018.
 */

public class ProgressDialogManager {

    private static ProgressDialog progressDialog;

    public static ProgressDialog showProgressDialogWithTitle(Context c, String message, String title) {
        progressDialog = new ProgressDialog(c);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(title);
        progressDialog.show();
        if (message != "")
        {
            progressDialog.setMessage(message);
        }
        progressDialog.show();
        return progressDialog;
    }
    public static void closeProgressDialog(ProgressDialog _progressDialog) {
        _progressDialog.dismiss();
    }
}
