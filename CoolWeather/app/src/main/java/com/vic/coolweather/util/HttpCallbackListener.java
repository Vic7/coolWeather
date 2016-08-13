package com.vic.coolweather.util;

/**
 * Created by Vic on 8/9/2016.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
