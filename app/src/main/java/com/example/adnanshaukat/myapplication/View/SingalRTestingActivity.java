package com.example.adnanshaukat.myapplication.View;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.adnanshaukat.myapplication.Modals.SignalrNotificationManager;
import com.example.adnanshaukat.myapplication.R;

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

public class SingalRTestingActivity extends AppCompatActivity {
    EditText username, message, send_message;
    Button connection, disconnection, send;
    Spinner users;
    HubConnection hubConnection;
    HubProxy hubProxy;
    Handler mHandler=new Handler();
    Context cx;
    SignalrNotificationManager signalrNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singal_rtesting);
        username = (EditText) findViewById(R.id.putText);
        message = (EditText) findViewById(R.id.edit_message);
        send_message=(EditText)findViewById(R.id.messageScreen);
        connection = (Button) findViewById(R.id.btnconnect);
        disconnection = (Button) findViewById(R.id.btndisconnect);
        send = (Button) findViewById(R.id.send_message);
        users = (Spinner) findViewById(R.id.userList);
        signalrNotificationManager = new SignalrNotificationManager(this);
        users.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0){
                    //send.setEnabled(true);
                }
                else{
                    //send.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!message.getText().toString().trim().equals("")) { // WebApi Methods
                    hubProxy.invoke("Send", "Hello from android");//we have parameterized what we want in the web API method
                }
            }
        });
        connection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signalrNotificationManager.connectToSignalR(1); // connect chat server
            }
        });
        disconnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signalrNotificationManager.disconnect(); //disconnect chat server
            }
        });

    }

    void connect() {
        Platform.loadPlatformComponent(new AndroidPlatformComponent());
        Credentials credentials = new Credentials() {
            @Override
            public void prepareRequest(Request request) {
                request.addHeader("username", username.getText().toString().trim()); //get username
            }
        };
        String serverUrl="http://192.168.1.106/smart_transport/signalr"; // connect to signalr server
        hubConnection = new HubConnection(serverUrl);
        hubConnection.setCredentials(credentials);
        hubConnection.connected(new Runnable() {
            @Override
            public void run() {
                Log.e("SignalR", "SignalR Running");
            }
        });
        String CLIENT_METHOD_BROADAST_MESSAGE = "broadcastMessage"; // get webapi serv methods
        hubProxy = hubConnection.createHubProxy("notificationHub"); // web api  necessary method name
        ClientTransport clientTransport = new ServerSentEventsTransport(hubConnection.getLogger());
        SignalRFuture<Void> signalRFuture = hubConnection.start(clientTransport);

        hubProxy.on(CLIENT_METHOD_BROADAST_MESSAGE, new SubscriptionHandler1<String>() {
            @Override
            public void run(String s) {
                // we added the list of connected users
                Log.e("MESSAGE", s);
                final String message = s;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
//                            ArrayAdapter<String> adapter=new ArrayAdapter<String>(cx,android.R.layout.simple_list_item_1,user_names);
//                            adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
//                            users.setAdapter(adapter);
                        Log.e("MESSAGES", message);
                    }
                });
            }
        }, String.class);

//        hubProxy.on("sendMessage", new SubscriptionHandler2<String ,String>() {
//
//            @Override
//            public void run(final String s, final String s2) {
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        send_message.setText(send_message.getText()+"\n"+s2+" : "+s);
//                    }
//                });
//            }
//        },String.class,String.class);
        try {
            signalRFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            Log.e("SimpleAASignalR", e.toString());
            return;
        }
    }

    void disconnect(){ //disconnection server
        hubConnection.stop();
        //userList.clear();
        users.setAdapter(null);
        send.setEnabled(false);
        //sender=null;
    }
}