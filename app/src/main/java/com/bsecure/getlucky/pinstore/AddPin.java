package com.bsecure.getlucky.pinstore;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
        submit = findViewById(R.id.submit);
        pin_et.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.showSoftInput(pin_et, 0);
            }
        }, 50);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               String  otpone = pin_et.getText().toString();

                if (TextUtils.isEmpty(otpone)) {
                    Toast.makeText(AddPin.this, "Enter your 4 Digits Pin", Toast.LENGTH_SHORT).show();
                    return;
                }
                AppPreferences.getInstance(AddPin.this).addToStore("pin_view",otpone,true);
                Intent in = new Intent(AddPin.this, GetLucky.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(in);
                finish();

            }
        });
    }
}
