package com.example.adnanshaukat.myapplication.Adapters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by AdnanShaukat on 03/03/2019.
 */

public abstract class CancelableCallback<T> implements Callback<T> {
    private static List<CancelableCallback> mList = new ArrayList<>();

    private boolean isCanceled = false;
    private Object mTag = null;

    public static void cancelAll() {
        Iterator<CancelableCallback> iterator = mList.iterator();
        while (iterator.hasNext()){
            iterator.next().isCanceled = true;
            iterator.remove();
        }
    }

    public static void cancel(Object tag) {
        if (tag != null) {
            Iterator<CancelableCallback> iterator = mList.iterator();
            CancelableCallback item;
            while (iterator.hasNext()) {
                item = iterator.next();
                if (tag.equals(item.mTag)) {
                    item.isCanceled = true;
                    iterator.remove();
                }
            }
        }
    }

    public CancelableCallback() {
        mList.add(this);
    }

    public CancelableCallback(Object tag) {
        mTag = tag;
        mList.add(this);
    }

    public static Boolean runningProcess(){

        return mList.size()>0;
    }

    public void cancel() {
        isCanceled = true;
        mList.remove(this);
    }

    public abstract void onSuccess(T t, Response response);
}
