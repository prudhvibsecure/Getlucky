package com.bsecure.getlucky.operator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bsecure.getlucky.Login;
import com.bsecure.getlucky.OtpScreen;
import com.bsecure.getlucky.R;
import com.bsecure.getlucky.cashback.AddCashback;
import com.bsecure.getlucky.common.AppPreferences;
import com.bsecure.getlucky.interfaces.RequestHandler;
import com.bsecure.getlucky.volleyhttp.Constants;
import com.bsecure.getlucky.volleyhttp.MethodResquest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OperatorLogin extends AppCompatActivity implements RequestHandler {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.operator_login);
        findViewById(R.id.op_login).setVisibility(View.VISIBLE);
        findViewById(R.id.op_add).setVisibility(View.GONE);
        findViewById(R.id.bacl_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });findViewById(R.id.submit_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OperatorLoginv();
            }
        });
    }

    private void OperatorLoginv() {

        //mahi123
        try {
            JSONObject object = new JSONObject();
            String u_name = ((EditText) findViewById(R.id.name_u)).getText().toString().trim();
            if (u_name.length() == 0) {
                Toast.makeText(this, "Please Fill Required Fields", Toast.LENGTH_SHORT).show();
                return;
            }
            String password = ((EditText) findViewById(R.id.password_u)).getText().toString().trim();
            if (password.length() == 0) {
                Toast.makeText(this, "Please Fill Required Fields", Toast.LENGTH_SHORT).show();
                return;
            }
            object.put("username", u_name);
            object.put("password", password);
            MethodResquest req = new MethodResquest(this, this, Constants.PATH + "operator_login", object.toString(), 101);
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
                        AppPreferences.getInstance(this).addToStore("user_type","2",true);
                        Toast.makeText(this, object.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                        Intent in = new Intent(OperatorLogin.this, AddCashback.class);
                        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(in);
                        overridePendingTransition(R.anim.fade_in_anim,R.anim.fade_out_anim);
                        finishAffinity();
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
