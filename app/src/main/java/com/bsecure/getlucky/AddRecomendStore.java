package com.bsecure.getlucky;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bsecure.getlucky.common.AppPreferences;
import com.bsecure.getlucky.interfaces.RequestHandler;
import com.bsecure.getlucky.volleyhttp.Constants;
import com.bsecure.getlucky.volleyhttp.MethodResquest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddRecomendStore extends AppCompatActivity implements RequestHandler {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_qrcode);

        Button add = findViewById(R.id.submit_bank);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addOperator();
            }
        });
        findViewById(R.id.bacl_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void addOperator() {
        try {
            JSONObject object = new JSONObject();
            String customer_referral_code = ((EditText) findViewById(R.id.tv_code)).getText().toString().trim();
            if (customer_referral_code.length() == 0) {
                Toast.makeText(this, "Please Fill Required Fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (customer_referral_code.contains(" ")) {
                Toast.makeText(this, "Spaces Not Allowed", Toast.LENGTH_SHORT).show();
                return;
            }
                String session_data = AppPreferences.getInstance(this).getFromStore("userData");
                JSONArray ayArray = new JSONArray(session_data);

                object.put("customer_id", ayArray.getJSONObject(0).optString("customer_id"));
                object.put("customer_referral_code", customer_referral_code);
                MethodResquest req = new MethodResquest(this, this, Constants.PATH + "recommend_store", object.toString(), 101);
                req.dismissProgress(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void requestStarted() {

    }

    @Override
    public void requestCompleted(JSONObject response, int requestType) {
        try {
            switch (requestType) {
                case 101:
                    JSONObject object = new JSONObject(response.toString());
                    if (object.optString("statuscode").equalsIgnoreCase("200")) {
                        Toast.makeText(this, object.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                        Intent bb = new Intent("com.operator_refrsh");
                        bb.putExtra("store_id", getIntent().getStringExtra("store_id"));
                        sendBroadcast(bb);
                        this.finish();
                    } else {
                        Toast.makeText(this, object.optString("statusdescription"), Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void requestEndedWithError(String error, int errorcode) {

    }
}
