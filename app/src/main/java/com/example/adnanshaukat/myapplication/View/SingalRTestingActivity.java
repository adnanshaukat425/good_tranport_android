package com.example.adnanshaukat.myapplication.View;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.adnanshaukat.myapplication.Modals.SignalRService;
import com.example.adnanshaukat.myapplication.R;
import com.microsoft.signalr.Action1;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;

public class SingalRTestingActivity extends AppCompatActivity {

    private final Context mContext = this;
    private SignalRService mService;
    private boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singal_rtesting);

//        Intent intent = new Intent();
//        intent.setClass(mContext, SignalRService.class);
//        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        HubConnection hubConnection = HubConnectionBuilder.create("http://192.168.0.107/signalr/hubs")
                .build();
        hubConnection.send("Send", "Message");
        hubConnection.on("broadcastMessage", new Action1<String>() {
            @Override
            public void invoke(String param1) {
                Log.e("SignalR Android", param1);
            }
        }, String.class);
    }

    @Override
    protected void onStop() {
        // Unbind from the service
//        if (mBound) {
//            unbindService(mConnection);
//            mBound = false;
//        }
        super.onStop();
    }

//    public void sendMessage() {
//        if (mBound) {
//            // Call a method from the SignalRService.
//            // However, if this call were something that might hang, then this request should
//            // occur in a separate thread to avoid slowing down the activity performance.
////            EditText editText = (EditText) findViewById(R.id.edit_message);
//            String editText = "Hello world";
//            if (editText != null) {
//                String message = editText;
//                mService.sendMessage(message);
//            }
//        }
//    }
//
//    /**
//     * Defines callbacks for service binding, passed to bindService()
//     */
//    private final ServiceConnection mConnection = new ServiceConnection() {
//
//        @Override
//        public void onServiceConnected(ComponentName className,
//                                       IBinder service) {
//            // We've bound to SignalRService, cast the IBinder and get SignalRService instance
//            SignalRService.LocalBinder binder = (SignalRService.LocalBinder) service;
//            mService = binder.getService();
//            mBound = true;
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName arg0) {
//            mBound = false;
//        }
//    };
}
