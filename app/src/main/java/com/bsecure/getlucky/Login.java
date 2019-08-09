package com.bsecure.getlucky;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bsecure.getlucky.interfaces.RequestHandler;
import com.bsecure.getlucky.volleyhttp.Constants;
import com.bsecure.getlucky.volleyhttp.MethodResquest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

        toolbar=findViewById(R.id.toolbar);

        drawer=findViewById(R.id.drawer_layout);

        btn=findViewById(R.id.send);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);

        toggle.syncState();

        phoneno = findViewById(R.id.phoneno);



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                phone = phoneno.getText().toString().trim();

                if(TextUtils.isEmpty(phone))
                {
                    Toast.makeText(Login.this, "Please Enter Phone Number", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(phone.length() < 10)
                {
                    Toast.makeText(Login.this, "Phone Number Should be 10 Digits", Toast.LENGTH_SHORT).show();
                    return;
                }
                getLogin(phone);
               // startActivity(new Intent(Login.this,OtpScreen.class));
            }
        });
    }

    private void getLogin(String phone) {

        try {

            JSONObject object=new JSONObject();

            object.put("phone_number", phone);

            new MethodResquest(this,this, Constants.PATH+"send_otp", object.toString(),100);

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

                    if(result.optString("statuscode").equalsIgnoreCase("200"))
                    {
                        Intent in = new Intent(Login.this, OtpScreen.class);

                        in.putExtra("phone", phone);

                        startActivity(in);
                    }
                    else
                    {
                        Toast.makeText(this, result.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    }

                    break;

                    default:

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
