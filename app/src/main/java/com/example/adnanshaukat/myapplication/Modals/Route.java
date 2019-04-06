package com.example.adnanshaukat.myapplication.Modals;

import java.util.Date;

/**
 * Created by AdnanShaukat on 31/03/2019.
 */

public class Route {
    int route_id;
    int order_detail_id;
    String latitude;
    String longitude;
    Date date_time;

    public Route(int route_id, int order_detail_id, String latitude, String longitude, Date date_time) {
        this.route_id = route_id;
        this.order_detail_id = order_detail_id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date_time = date_time;
    }

    public int getRoute_id() {
        return route_id;
    }

    public void setRoute_id(int route_id) {
        this.route_id = route_id;
    }

    public int getOrder_detail_id() {
        return order_detail_id;
    }

    public void setOrder_detail_id(int order_detail_id) {
        this.order_detail_id = order_detail_id;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Date getDate_time() {
        return date_time;
    }

    public void setDate_time(Date date_time) {
        this.date_time = date_time;
    }
}
