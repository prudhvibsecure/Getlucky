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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bsecure.getlucky.GetLucky;
import com.bsecure.getlucky.OtpScreen;
import com.bsecure.getlucky.R;
import com.bsecure.getlucky.Splash;
import com.bsecure.getlucky.common.AppPreferences;
import com.chaos.view.PinView;

public class VerifyPin extends AppCompatActivity {

    private PinView pin_et;

    private Button submit;
    private TextView verifytext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pin_view);

        pin_et = (PinView) findViewById(R.id.pinView);
        submit = findViewById(R.id.submit);
        verifytext = findViewById(R.id.verifytext);
        submit.setVisibility(View.GONE);
        verifytext.setText("VERIFY PIN");
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

    }

    private void getPinData(String otpone) {

        String pin = AppPreferences.getInstance(VerifyPin.this).getFromStore("pin_view");
        if (TextUtils.isEmpty(otpone)) {
            Toast.makeText(VerifyPin.this, "Enter your 4 Digits Pin", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pin.equalsIgnoreCase(otpone)) {
            Intent in = new Intent(VerifyPin.this, GetLucky.class);
            in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(in);
            finish();
        } else {
            if (otpone.length() >= 4) {
                Toast.makeText(VerifyPin.this, "Wrong Pin", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
