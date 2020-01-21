package com.bsecure.getlucky.pinstore;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bsecure.getlucky.GetLucky;
import com.bsecure.getlucky.OtpScreen;
import com.bsecure.getlucky.R;
import com.bsecure.getlucky.common.AppPreferences;
import com.chaos.view.PinView;

public class AddPin extends AppCompatActivity {

    private PinView pin_et;

    private Button submit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pin_view);

        pin_et = (PinView) findViewById(R.id.pinView);

        pin_et.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.showSoftInput(pin_et, 0);
            }
        }, 50);
        pin_et.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                getPinData(s.toString());

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });


        submit = findViewById(R.id.submit);
        submit.setVisibility(View.GONE);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
    }

    private void getPinData(String otpone) {


        if (TextUtils.isEmpty(otpone)) {
            Toast.makeText(AddPin.this, "Enter your 4 Digits Pin", Toast.LENGTH_SHORT).show();
            return;
        }
        if (otpone.length() <=3) {
           // Toast.makeText(AddPin.this, "Enter your 4 Digits Pin", Toast.LENGTH_SHORT).show();
            return;
        }
        AppPreferences.getInstance(AddPin.this).addToStore("pin_view", otpone, true);
        AppPreferences.getInstance(AddPin.this).addToStore("first_time", "0", true);
        Intent in = new Intent(AddPin.this, GetLucky.class);
        //in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(in);
        overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
        finish();


    }
}
