package com.bsecure.getlucky.interfaces;

public interface IItemHandler {

    void onFinish(Object results, int requestId);

    void onError(String errorCode, int requestId);

    void onProgressChange(int requestId, Long... values);

}