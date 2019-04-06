package com.example.adnanshaukat.myapplication.Modals;

import android.content.Context;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.adnanshaukat.myapplication.R;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.RetrofitManager;
import com.example.adnanshaukat.myapplication.View.MapsActivity;

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
 * Created by AdnanShaukat on 31/03/2019.
 */

public class SignalrTrackingManager {
    public HubConnection hubConnection;
    public HubProxy hubProxy;
    Handler mHandler = new Handler();
    Context context;
    String CHANNEL_ID = "111";
    public SignalrTrackingManager(Context context){
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
        hubProxy = hubConnection.createHubProxy("trackingHub");
        ClientTransport clientTransport = new ServerSentEventsTransport(hubConnection.getLogger());
        SignalRFuture<Void> signalRFuture = hubConnection.start(clientTransport);

        String CLIENT_METHOD_BROADAST_MESSAGE = "UpdateLocation"; // get webAPI server methods
        hubProxy.on(CLIENT_METHOD_BROADAST_MESSAGE, new SubscriptionHandler1<Route>() {
            @Override
            public void run(final Route route) {
                // we added the list of connected users
                Log.e("Route Id", route.getRoute_id() + "");
                Log.e("Order Detail Id", route.getOrder_detail_id() + "");
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        MapsActivity mapsActivity = new MapsActivity();
                        mapsActivity.updateMap(route.latitude, route.longitude);
                    }
                });
            }
        }, Route.class);

        //hubProxy.invoke("insert_location", new Route(0, 1, "24.00", "67.25151", null));
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
            Log.e("SignalR", e.toString());
            return;
        }
    }

    public void disconnect(){
        hubConnection.stop();
    }
}