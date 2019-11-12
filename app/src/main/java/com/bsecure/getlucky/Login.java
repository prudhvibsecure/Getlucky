package com.bsecure.getlucky;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bsecure.getlucky.interfaces.RequestHandler;
import com.bsecure.getlucky.volleyhttp.Constants;
import com.bsecure.getlucky.volleyhttp.MethodResquest;

import org.json.JSONObject;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Login extends AppCompatActivity implements RequestHandler {

    private Toolbar toolbar;

    private ActionBarDrawerToggle toggle;

    private DrawerLayout drawer;

    private Button btn;

    private EditText phoneno;

    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        toolbar = findViewById(R.id.toolbar);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        101);
            }
        }

        btn = findViewById(R.id.send);

        phoneno = findViewById(R.id.phoneno);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLogin();
            }
        });
        findViewById(R.id.bacl_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                overridePendingTransition(R.anim.fade_out_anim,R.anim.fade_in_anim);
                finish();
            }
        });
    }

    private void getLogin() {

        try {
            phone = phoneno.getText().toString().trim();

            if (TextUtils.isEmpty(phone)) {
                Toast.makeText(Login.this, "Please Enter Mobile Number", Toast.LENGTH_SHORT).show();
                return;
            }

            if (phone.length() < 10) {
                Toast.makeText(Login.this, "Mobile Number Must Be 10 Digits", Toast.LENGTH_SHORT).show();
                return;
            }

            JSONObject object = new JSONObject();

            object.put("phone_number", phone);

            new MethodResquest(this, this, Constants.PATH + "send_otp", object.toString(), 100);

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

                case 100:

                    JSONObject result = new JSONObject(response.toString());

                    if (result.optString("statuscode").equalsIgnoreCase("200")) {
                        Intent in = new Intent(Login.this, OtpScreen.class);
                        in.putExtra("phone", phone);
                        startActivity(in);
                        overridePendingTransition(R.anim.fade_in_anim,R.anim.fade_out_anim);
                        this.finish();
                    } else {
                        Toast.makeText(this, result.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    }

                    break;

                default:

                    break;
            }
        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    @Override
    public void requestEndedWithError(String error, int errorcode) {

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 101
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
}
