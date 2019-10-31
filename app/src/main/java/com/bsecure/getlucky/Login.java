package com.bsecure.getlucky;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

       // drawer = findViewById(R.id.drawer_layout);
        if (CheckingPermissionIsEnabledOrNot()) {
            // Toast.makeText(getApplicationContext(),"Permissions Accessed",Toast.LENGTH_LONG).show();
        } else {
            RequestMultiplePermission();
            //CheckingPermissionIsEnabledOrNot();
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
    public boolean CheckingPermissionIsEnabledOrNot() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED;
    }

    //Permission function starts from here
    private void RequestMultiplePermission() {

        // Creating String Array with Permissions.
        ActivityCompat.requestPermissions(this, new String[]
                {
                        WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE,

                }, 101);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 101:

                if (grantResults.length > 0) {

                    boolean FirstPermissionResult = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean SecondPermissionResult = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (FirstPermissionResult && SecondPermissionResult ) {

                        // Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        //Toast.makeText(MainActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();

                    }
                }

                break;
        }
    }

}
