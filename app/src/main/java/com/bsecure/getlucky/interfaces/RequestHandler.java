package com.bsecure.getlucky.interfaces;

import org.json.JSONObject;

/**
 * Created by Admin on 2018-05-02.
 */

public interface RequestHandler {

    public void requestStarted();

    public void requestCompleted(JSONObject response, int requestType);

    public void requestEndedWithError(String error, int errorcode);

}
