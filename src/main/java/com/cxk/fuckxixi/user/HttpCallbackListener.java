package com.cxk.fuckxixi.user;

public interface HttpCallbackListener {
    void onFinish(String response);

    void onError(Exception e);
}
