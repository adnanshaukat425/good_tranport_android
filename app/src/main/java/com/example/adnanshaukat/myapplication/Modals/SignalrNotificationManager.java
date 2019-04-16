package com.example.adnanshaukat.myapplication.Modals;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.adnanshaukat.myapplication.GlobalClasses.MyApplication;
import com.example.adnanshaukat.myapplication.R;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.RetrofitManager;
import com.example.adnanshaukat.myapplication.View.Customer.MainActivityCustomer;
import com.example.adnanshaukat.myapplication.View.Driver.MainActivityDriver;
import com.example.adnanshaukat.myapplication.View.Transporter.MainActivityTransporter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import microsoft.aspnet.signalr.client.Credentials;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.Request;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;
import microsoft.aspnet.signalr.client.transport.ClientTransport;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;

/**
 * Created by AdnanShaukat on 24/03/2019.
 */

public class SignalrNotificationManager {
    public HubConnection hubConnection;
    public HubProxy hubProxy;
    Handler mHandler = new Handler();
    Context context;
    String CHANNEL_ID = "111";
    public SignalrNotificationManager(Context context){
        this.context = context;
    }

    public void connectToSignalR(final int user_id){
        String serverUrl="http://" + RetrofitManager.ip + "/" + RetrofitManager.domain + "/signalr"; // connect to signalr server
        Platform.loadPlatformComponent(new AndroidPlatformComponent());
        Credentials credentials = new Credentials() {
            @Override
            public void prepareRequest(Request request) {
                request.addHeader("user_id", user_id + ""); //get username
            }
        };
        hubConnection = new HubConnection(serverUrl);
        hubConnection.setCredentials(credentials);
        hubConnection.connected(new Runnable() {
            @Override
            public void run() {
                Log.e("SignalR", "SignalR Running");
            }
        });
        hubProxy = hubConnection.createHubProxy("notificationHub");
        ClientTransport clientTransport = new ServerSentEventsTransport(hubConnection.getLogger());
        SignalRFuture<Void> signalRFuture = hubConnection.start(clientTransport);

        String CLIENT_METHOD_BROADAST_MESSAGE = "BroadCastNotification"; // get webapi serv methods
        hubProxy.on(CLIENT_METHOD_BROADAST_MESSAGE, new SubscriptionHandler1<Notification>() {
            @Override
            public void run(final Notification notification) {
                // we added the list of connected users
                Log.e("Notification Id", notification.getNotification_id() + "");
                Log.e("User Id", notification.getUser_id() + "");
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = null;

                        if(notification.getNotification_for().trim().toLowerCase().equals("driver")){
                            intent = new Intent(context, MainActivityDriver.class);
                        } else if(notification.getNotification_for().trim().toLowerCase().equals("customer")){
                            intent = new Intent(context, MainActivityCustomer.class);
                        } else if (notification.getNotification_for().trim().toLowerCase().equals("transporter")) {
                            intent = new Intent(context, MainActivityTransporter.class);
                        }

                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("fragment_from_notification", notification.getRedirected_page());

                        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.vehicle_icon)
                                    .setContentTitle(notification.getNotification_title())
                                    .setContentText(notification.getNotification_message())
                                    .setPriority(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE | NotificationCompat.BADGE_ICON_LARGE)
                                    .setContentIntent(pendingIntent)
                                    .setAutoCancel(true);

                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                            //notificationId is a unique int for each notification that you must define
                            notificationManager.notify(notification.getNotification_id(), builder.build());
                    }
                });
            }
        }, Notification.class);
//
////        hubProxy.on("sendMessage", new SubscriptionHandler2<String ,String>() {
////
////            @Override
////            public void run(final String s, final String s2) {
////                mHandler.post(new Runnable() {
////                    @Override
////                    public void run() {
////                        send_message.setText(send_message.getText()+"\n"+s2+" : "+s);
////                    }
////                });
////            }
////        },String.class,String.class);
        try {
            signalRFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            Log.e("Notification SignalR", e.toString());
            return;
        }
    }

    public void disconnect(){
        hubConnection.stop();
    }
}
