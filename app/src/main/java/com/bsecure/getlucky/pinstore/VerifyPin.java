package com.bsecure.getlucky.pinstore;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
        submit.setText("Verify Pin");
        verifytext.setText("Verify Pin");
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
                String pin = AppPreferences.getInstance(VerifyPin.this).getFromStore("pin_view");
                if (TextUtils.isEmpty(otpone)) {
                    Toast.makeText(VerifyPin.this, "Enter your 4 Digits Pin", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pin.equalsIgnoreCase(otpone)) {
//                AppPreferences.getInstance(VerifyPin.this).addToStore("pin_view",otpone,true);
                    Intent in = new Intent(VerifyPin.this, GetLucky.class);
                    in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(in);
                    finish();
                }else{
                    Toast.makeText(VerifyPin.this, "Wrong Pin", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
