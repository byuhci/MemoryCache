package com.example.jipark.memorycache.notification;

import android.content.Context;

/**
 * Created by brandonderbidge on 2/10/18.
 */

public interface INotify {

    void Notify(String url, String Text, IInformation information, Context context);
}
