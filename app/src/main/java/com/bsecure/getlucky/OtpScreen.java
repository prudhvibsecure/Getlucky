package com.bsecure.getlucky;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bsecure.getlucky.interfaces.RequestHandler;
import com.bsecure.getlucky.volleyhttp.Constants;
import com.bsecure.getlucky.volleyhttp.MethodResquest;

import org.json.JSONArray;
import org.json.JSONObject;

public class OtpScreen extends AppCompatActivity implements RequestHandler {

    private EditText otp1, otp2, otp3, otp4;

    private String otpone, otptwo, otpthree, otpfour;

    private Button submit;

    private String phone;

    private TextView resend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_screen);

        Intent in = getIntent();

        Bundle bd = in.getExtras();

        phone = bd.getString("phone");

        otp1 = findViewById(R.id.otp1);

        otp2 = findViewById(R.id.otp2);

        otp3 = findViewById(R.id.otp3);

        otp4 = findViewById(R.id.otp4);

        submit = findViewById(R.id.submit);

        resend = findViewById(R.id.resend);


        otp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().length() == 1)
                {
                    otp2.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        otp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().length() == 1)
                {
                    otp3.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        otp3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().length() == 1)
                {
                    otp4.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                otpone = otp1.getText().toString().trim();

                otptwo = otp2.getText().toString().trim();

                otpthree = otp3.getText().toString().trim();

                otpfour = otp4.getText().toString().trim();


                if(TextUtils.isEmpty(otpone) || TextUtils.isEmpty(otptwo) || TextUtils.isEmpty(otpthree) || TextUtils.isEmpty(otpfour))
                {
                    Toast.makeText(OtpScreen.this, "OTP Must be 4 Digits", Toast.LENGTH_SHORT).show();
                    return;
                }
                String otp = otpone+otptwo+otpthree+otpfour;
                verifyOtp(otp);
            }
        });

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                otp1.setText("");
                otp2.setText("");
                otp3.setText("");
                otp4.setText("");
                resendOtp(phone);

            }
        });

    }

    private void resendOtp(String phone) {

        try {

            JSONObject object=new JSONObject();

            object.put("phone_number", phone);

            new MethodResquest(this,this, Constants.PATH+"send_otp", object.toString(),200);

        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    private void verifyOtp(String otp) {

        try {

            JSONObject object=new JSONObject();

            object.put("otp", otp);

            object.put("phone_number", phone);

            object.put("regidand", "");

            new MethodResquest(this,this, Constants.PATH+"verify_otp", object.toString(),100);

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
                        JSONArray array = result.getJSONArray("customer_details");

                        if(array.length() == 0)
                        {
                            startActivity(new Intent(OtpScreen.this, Dashboard.class));

                            return;

                        }

                        Intent in = new Intent(OtpScreen.this, Dashboard.class);

                        startActivity(in);
                    }
                    else
                    {
                        Toast.makeText(this, result.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    }

                    break;

                case 200:

                    JSONObject result2 = new JSONObject(response.toString());

                    if(result2.optString("statuscode").equalsIgnoreCase("200"))
                    {

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
