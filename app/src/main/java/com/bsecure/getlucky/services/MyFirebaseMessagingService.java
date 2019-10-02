package com.bsecure.getlucky.services;


import com.bsecure.getlucky.common.AppPreferences;
import com.bsecure.getlucky.utils.TraceUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    String m_type;
    public MyFirebaseMessagingService() {

    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        if (token != null) {

            AppPreferences.getInstance(this).addToStore("token", token, false);

        }

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ

        TraceUtils.logE("From: ", remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {


            try {

                Map<String, String> params = remoteMessage.getData();
                Object object = new JSONObject(params);
                sendPushNotification(object);
            } catch (Exception e) {
                TraceUtils.logException(e);
            }


        }
    }
    private void sendPushNotification(Object json) {
        try {
            //getting the json data
            JSONObject data = new JSONObject(json.toString());

        } catch (JSONException e) {
            TraceUtils.logException(e);
        } catch (Exception e) {
            TraceUtils.logException(e);
        }

    }
}
