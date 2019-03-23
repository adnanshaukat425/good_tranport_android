package com.example.adnanshaukat.myapplication.Modals;

/**
 * Created by AdnanShaukat on 18/03/2019.
 */

public class Notification {
    int notification_id;
    String notification_message;
    int user_id;
    int is_seen;
    int is_pushed;

    public Notification(int notification_id, String notification_message, int user_id, int is_seen, int is_pushed) {
        this.notification_id = notification_id;
        this.notification_message = notification_message;
        this.user_id = user_id;
        this.is_seen = is_seen;
        this.is_pushed = is_pushed;
    }

    public int getNotification_id() {
        return notification_id;
    }

    public void setNotification_id(int notification_id) {
        this.notification_id = notification_id;
    }

    public String getNotification_message() {
        return notification_message;
    }

    public void setNotification_message(String notification_message) {
        this.notification_message = notification_message;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getIs_seen() {
        return is_seen;
    }

    public void setIs_seen(int is_seen) {
        this.is_seen = is_seen;
    }

    public int getIs_pushed() {
        return is_pushed;
    }

    public void setIs_pushed(int is_pushed) {
        this.is_pushed = is_pushed;
    }
}
