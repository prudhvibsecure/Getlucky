package com.bsecure.getlucky;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;

import com.bsecure.getlucky.common.AppPreferences;
import com.bsecure.getlucky.fragments.HomeFragment;
import com.bsecure.getlucky.interfaces.IItemHandler;
import com.bsecure.getlucky.interfaces.RequestHandler;
import com.bsecure.getlucky.pinstore.AddPin;
import com.bsecure.getlucky.pinstore.VerifyPin;
import com.bsecure.getlucky.volleyhttp.Constants;
import com.bsecure.getlucky.volleyhttp.HTTPPostTask;
import com.bsecure.getlucky.volleyhttp.MethodResquest;

import org.json.JSONArray;
import org.json.JSONObject;

public class Splash extends AppCompatActivity implements IItemHandler {

    private ViewGroup hiddenPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppPreferences.getInstance(this).addToStore("first_time", "0", true);

        getListCats();

        hiddenPanel = (ViewGroup) findViewById(R.id.hidden_panel);
        hiddenPanel.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                findViewById(R.id.bottom_img).setVisibility(View.VISIBLE);

                TranslateAnimation animate;
                if (findViewById(R.id.bottom_img).getHeight() == 0) {
                    findViewById(R.id.bottom_img).getHeight(); // parent layout
                    animate = new TranslateAnimation(findViewById(R.id.bottom_img).getWidth() / 2,
                            0, 0, 0);
                } else {
                    animate = new TranslateAnimation(findViewById(R.id.bottom_img).getWidth(), 0, 0, 0);
                }

                animate.setDuration(1500);
                animate.setFillAfter(true);
                findViewById(R.id.bottom_img).startAnimation(animate);
                findViewById(R.id.bottom_img).setVisibility(View.VISIBLE);
            }
        }, 2000);


        Animation bottomUp = AnimationUtils.loadAnimation(this,
                R.anim.bottom_up);

        hiddenPanel.startAnimation(bottomUp);
        hiddenPanel.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                String pin = AppPreferences.getInstance(Splash.this).getFromStore("pin_view");
                if (pin.length() != 0 || !TextUtils.isEmpty(pin)) {
                    startActivity(new Intent(Splash.this, VerifyPin.class));
                    overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
                    finish();
                } else {
                    startActivity(new Intent(Splash.this, GetLucky.class));
                    overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
                    finish();
                }

            }

        }, 5000);

    }

    private void getListCats() {

        JSONObject object = new JSONObject();
        HTTPPostTask task = new HTTPPostTask(this, this);
        task.userRequest("", 100, Constants.PATH + "get_category", object.toString());
    }

    @Override
    public void onFinish(Object results, int requestId) {
        try {
            switch (requestId) {

                case 100:
                    JSONObject object = new JSONObject(results.toString());
                    if (object.optString("statuscode").equalsIgnoreCase("200")) {
                        JSONArray array = object.getJSONArray("category_details");
                        AppPreferences.getInstance(this).addToStore("category", array.toString(), true);
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String errorCode, int requestId) {

    }

    @Override
    public void onProgressChange(int requestId, Long... values) {

    }
}
