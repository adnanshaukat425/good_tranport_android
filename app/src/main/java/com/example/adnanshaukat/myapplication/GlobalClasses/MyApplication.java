package com.example.adnanshaukat.myapplication.GlobalClasses;

import android.app.Application;
import android.os.Handler;
import android.util.Log;

import com.example.adnanshaukat.myapplication.Modals.User;
import com.example.adnanshaukat.myapplication.RetrofitInterfaces.RetrofitManager;

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
 * Created by AdnanShaukat on 28/11/2018.
 */

public class MyApplication extends Application {
    private static int user_id;
    private static User globalUser;

    public int get_user_id() {
        return user_id;
    }

    public void set_user_id(int user_id) {this.user_id= user_id; }

    public User getGlobalUser() {
        return globalUser;
    }

    public void setGlobalUser(User globalUser) {
        this.globalUser = globalUser;
    }
}
