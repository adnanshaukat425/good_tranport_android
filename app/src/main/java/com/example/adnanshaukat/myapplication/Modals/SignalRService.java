package com.example.adnanshaukat.myapplication.Modals;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.microsoft.signalr.Action;
import com.microsoft.signalr.Action1;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;

import java.util.concurrent.ExecutionException;

public class SignalRService{
    private HubConnection mHubConnection;
    private Handler mHandler; // to display Toast message
//    private final IBinder mBinder = new LocalBinder(); // Binder given to clients

//    public SignalRService() {
//    }
//
//    public void callBack(String message){
//        Log.e("SignalR Message", message);
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        mHandler = new Handler(Looper.getMainLooper());
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        int result = super.onStartCommand(intent, flags, startId);
//        startSignalR();
//        return result;
//    }
//
//    @Override
//    public void onDestroy() {
//        mHubConnection.stop();
//        super.onDestroy();
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        // Return the communication channel to the service.
//        startSignalR();
//        return mBinder;
//    }
//
//    /**
//     * Class used for the client Binder.  Because we know this service always
//     * runs in the same process as its clients, we don't need to deal with IPC.
//     */
//    public class LocalBinder extends Binder {
//        public SignalRService getService() {
//            // Return this instance of SignalRService so clients can call public methods
//            return SignalRService.this;
//        }
//    }
//
//    /**
//     * method for clients (activities)
//     */
//    public void sendMessage(String message) {
//        String SERVER_METHOD_SEND = "Send";
//        mHubProxy.invoke(SERVER_METHOD_SEND, message);
//    }
//
//    private void startSignalR() {
//        Platform.loadPlatformComponent(new AndroidPlatformComponent());
//
//        Credentials credentials = new Credentials() {
//            @Override
//            public void prepareRequest(Request request) {
//                request.addHeader("User-Name", "BNK");
//            }
//        };
//
//        String serverUrl = "http://192.168.1.100";
//        mHubConnection = new HubConnection(serverUrl);
//        mHubConnection.setCredentials(credentials);
//        String SERVER_HUB_CHAT = "ChatHub";
//        mHubProxy = mHubConnection.createHubProxy(SERVER_HUB_CHAT);
//        ClientTransport clientTransport = new ServerSentEventsTransport(mHubConnection.getLogger());
//        SignalRFuture<Void> signalRFuture = mHubConnection.start(clientTransport);
//
//        try {
//            signalRFuture.get();
//        } catch (InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//            return;
//        }
//
//        String HELLO_MSG = "Hello from Android!";
//        sendMessage(HELLO_MSG);
//
//        String CLIENT_METHOD_BROADAST_MESSAGE = "broadcastMessage";
//        mHubProxy.on(CLIENT_METHOD_BROADAST_MESSAGE,
//                new SubscriptionHandler1<CustomMessage>() {
//                    @Override
//                    public void run(final CustomMessage msg) {
//                        final String finalMsg = msg.UserName + " says " + msg.Message;
//                        // display Toast message
//                        mHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(getApplicationContext(), finalMsg, Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                }
//                , CustomMessage.class);
//    }
}