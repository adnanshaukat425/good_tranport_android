package com.example.adnanshaukat.myapplication.Modals;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.example.adnanshaukat.myapplication.RetrofitInterfaces.RetrofitManager;
import com.example.adnanshaukat.myapplication.View.Common.MapsActivity;
import com.example.adnanshaukat.myapplication.View.Transporter.MapTrackDriverActivity;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
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
@SuppressWarnings("serial")
public class SignalrTrackingManager implements Serializable {
    private HubConnection hubConnection;
    private HubProxy hubProxy;
    private Handler mHandler = new Handler();
    private Context context;
    private String CHANNEL_ID = "1112";
    private static SignalrTrackingManager signalrTrackingManager = new SignalrTrackingManager();

    private SignalrTrackingManager(){
    }

    public static SignalrTrackingManager SignalrTrackingManager(){
        return signalrTrackingManager;
    }

    public SignalrTrackingManager setContext(Context context){
        this.context = context;
        return this;
    }

    public void connectToSignalR(final int user_id, final int order_detail_id){
        String serverUrl = "http://" + RetrofitManager.ip + "/" + RetrofitManager.domain + "/signalr"; // connect to signalr server
        Platform.loadPlatformComponent(new AndroidPlatformComponent());
        Credentials credentials = new Credentials() {
            @Override
            public void prepareRequest(Request request) {
                request.addHeader("user_id", user_id + "");
                request.addHeader("order_detail_id", order_detail_id + "");
            }
        };
        hubConnection = new HubConnection(serverUrl);
        hubConnection.setCredentials(credentials);
        hubConnection.connected(new Runnable() {
            @Override
            public void run() {
                Log.e("Tracking SignalR", "SignalR Running");
            }
        });
        hubProxy = hubConnection.createHubProxy("trackingHub");
        ClientTransport clientTransport = new ServerSentEventsTransport(hubConnection.getLogger());
        SignalRFuture<Void> signalRFuture = hubConnection.start(clientTransport);

        String CLIENT_METHOD_BROADAST_MESSAGE = "UpdateLocation"; // get webAPI server methods
        hubProxy.on(CLIENT_METHOD_BROADAST_MESSAGE, new SubscriptionHandler1<String>() {
            @Override
            public void run(final String data) {
                // we added the list of connected users
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            String latitude = data.split(";")[0];
                            String longitude = data.split(";")[1];
                            Log.e("Tracking SignalR", latitude);
                            Log.e("Tracking SignalR", longitude);
//                        Log.e("Route Id", route.getRoute_id() + "");
//                        Log.e("Order Detail Id", route.getOrder_detail_id() + "");
                            MapsActivity mapsActivity = new MapsActivity();
                            mapsActivity.updateMap(latitude, longitude, context);
                        }
                        catch(Exception ex){
                            ex.printStackTrace();
                        }
                    }
                });
            }
        }, String.class);

        String CLIENT_METHOD_BROADAST_MESSAGE_2 = "UpdateDriverLocationForTransporter"; // get webAPI server methods
        hubProxy.on(CLIENT_METHOD_BROADAST_MESSAGE_2, new SubscriptionHandler1<String>() {
            @Override
            public void run(final String data) {
                // we added the list of connected users
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            String latitude = data.split(";")[0];
                            String longitude = data.split(";")[1];
                            String driver_id = data.split(";")[2];
                            String driver_name = data.split(";")[2];
                            Log.e("Tracking SignalR Driver", latitude);
                            Log.e("Tracking SignalR Driver", longitude);

                            MapTrackDriverActivity mapsActivity = new MapTrackDriverActivity();
                            mapsActivity.update_marker(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)),
                                    Integer.parseInt(driver_id), driver_name, context);
                        }
                        catch(Exception ex){
                            ex.printStackTrace();
                        }
                    }
                });
            }
        }, String.class);

        try {
            signalRFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            Log.e("Tracking SignalR", e.toString());
            return;
        }
    }

    public void insertLocation(String latitude, String longitude, int order_detail_id){
        Log.e("Tracking SignalR", "Invoking Method InsertLocation");
        hubProxy.invoke("InsertLocation", new Route(0, order_detail_id, latitude, longitude, null));
        MapsActivity mapsActivity = new MapsActivity();
        //mapsActivity.updateMap(latitude, longitude);

//        MapsActivity mapsActivity = new MapsActivity();
//        mapsActivity.updateMap(latitude, longitude);
    }

    public void disconnect(){
        hubConnection.stop();
    }

    public void StartTracking(){

    }
}
