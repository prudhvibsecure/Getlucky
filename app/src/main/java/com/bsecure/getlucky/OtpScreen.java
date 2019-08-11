package com.bsecure.getlucky;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bsecure.getlucky.fragments.HomeFragment;
import com.bsecure.getlucky.interfaces.RequestHandler;
import com.bsecure.getlucky.volleyhttp.Constants;
import com.bsecure.getlucky.volleyhttp.MethodResquest;
import com.chaos.view.PinView;

import org.json.JSONArray;
import org.json.JSONObject;

public class OtpScreen extends AppCompatActivity implements RequestHandler {

    private PinView pin_et;

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

        submit = findViewById(R.id.submit);

        resend = findViewById(R.id.resend);

        pin_et = (PinView) findViewById(R.id.pinView);
        pin_et.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.showSoftInput(pin_et, 0);
            }
        }, 50);
       /* pin_et.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Integer textlength1 = pin_et.getText().length();

                if (textlength1 >= 4) {
                   // makeAddPinRequest();

                }else {

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });
*/
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                otpone = pin_et.getText().toString();

                if (TextUtils.isEmpty(otpone)) {
                    Toast.makeText(OtpScreen.this, "OTP Must be 4 Digits", Toast.LENGTH_SHORT).show();
                    return;
                }
                // String otp = otpone+otptwo+otpthree+otpfour;
                verifyOtp(otpone);
            }
        });

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendOtp(phone);

            }
        });

    }

    private void resendOtp(String phone) {

        try {

            JSONObject object = new JSONObject();

            object.put("phone_number", phone);

            new MethodResquest(this, this, Constants.PATH + "send_otp", object.toString(), 200);

        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    private void verifyOtp(String otp) {

        try {

            JSONObject object = new JSONObject();

            object.put("otp", otp);

            object.put("phone_number", phone);

            object.put("regidand", "");

            new MethodResquest(this, this, Constants.PATH + "verify_otp", object.toString(), 100);

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
                        JSONArray array = result.getJSONArray("customer_details");

                        if (array.length() == 0) {
                            startActivity(new Intent(OtpScreen.this, GetLucky.class));
                            return;

                        }

                        Intent in = new Intent(OtpScreen.this, GetLucky.class);
                        startActivity(in);
                    } else {
                        Toast.makeText(this, result.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    }

                    break;

                case 200:

                    JSONObject result2 = new JSONObject(response.toString());

                    if (result2.optString("statuscode").equalsIgnoreCase("200")) {

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
}
