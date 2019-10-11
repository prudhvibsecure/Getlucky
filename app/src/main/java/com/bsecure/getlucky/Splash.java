package com.bsecure.getlucky;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.bsecure.getlucky.common.AppPreferences;
import com.bsecure.getlucky.fragments.HomeFragment;
import com.bsecure.getlucky.interfaces.RequestHandler;
import com.bsecure.getlucky.volleyhttp.Constants;
import com.bsecure.getlucky.volleyhttp.MethodResquest;

import org.json.JSONArray;
import org.json.JSONObject;

public class Splash extends AppCompatActivity implements RequestHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getListCats();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                startActivity(new Intent(Splash.this, GetLucky.class));
                finish();

            }

        }, 5000);

    }

    private void getListCats() {

        JSONObject object = new JSONObject();
        MethodResquest resquest = new MethodResquest(this, this, Constants.PATH + "get_category", object.toString(), 100);
        resquest.dismissProgress(this);
    }

    @Override
    public void requestStarted() {

    }

    @Override
    public void requestCompleted(JSONObject response, int requestType) {
        try {
            switch (requestType) {

                case 100:
                    JSONObject object=new JSONObject(response.toString());
                    if (object.optString("statuscode").equalsIgnoreCase("200")){
                        JSONArray array=object.getJSONArray("category_details");
                        AppPreferences.getInstance(this).addToStore("category",array.toString(),true);
                    }
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public void requestEndedWithError(String error, int errorcode) {

    }
}
