package com.bsecure.getlucky.wallet;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bsecure.getlucky.R;
import com.bsecure.getlucky.store.AddStore;

public class StoreCharge extends AppCompatActivity {

    Button submit;
    String payment_type, code;
    EditText amount;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_charge);


        amount = findViewById(R.id.et_amount);
        submit = findViewById(R.id.proceed);
        back = findViewById(R.id.bacl_btn);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoreCharge.this.finish();
            }
        });

        Intent in = getIntent();
        code = in.getStringExtra("code");
        if(code.equals("0"))
        {
            amount.setText("2000");
        }
        else {
            amount.setText("");
        }
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if(code.equals("0"))
                {
                    if(amount.getText().toString().equals("0") || amount.getText().toString().length() == 0)
                    {
                        Toast.makeText(StoreCharge.this, "Amount should not be zero or empty", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double amt = Double.parseDouble(amount.getText().toString().trim());
                    if(amt <500)
                    {
                        Toast.makeText(StoreCharge.this, "Minimum Amount Should be 500", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                else
                {
                    if(amount.getText().toString().equals("0") || amount.getText().toString().length() == 0)
                    {
                        Toast.makeText(StoreCharge.this, "Amount should not be zero or empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                final Dialog mDialog = new Dialog(StoreCharge.this, R.style.Theme_MaterialComponents_Light_BottomSheetDialog);
                mDialog.setContentView(R.layout.paymetoption);
                mDialog.show();

                mDialog.findViewById(R.id.net_bank).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        payment_type = "Net Banking";
                        Toast.makeText(StoreCharge.this, "Coming Soon", Toast.LENGTH_SHORT).show();
                        StoreCharge.this.finish();
                        //redirectClass();
                        //AppPreferences.getInstance(getApplicationContext()).addToStore("amount", "", true);
                        //AppPreferences.getInstance(getApplicationContext()).addToStore("p_type", payment_type, true);
                        mDialog.dismiss();
                        //addBanks();
                    }
                });
                mDialog.findViewById(R.id.net_gp).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        payment_type = "google pay";
                        Toast.makeText(StoreCharge.this, "Coming Soon", Toast.LENGTH_SHORT).show();
                        StoreCharge.this.finish();
                        //addBanks();
                        // mDialog.dismiss();
                        //redirectClass();
                        mDialog.dismiss();
                    }
                });
                mDialog.findViewById(R.id.net_paytim).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        payment_type = "paytm";
                        Toast.makeText(StoreCharge.this, "Coming Soon", Toast.LENGTH_SHORT).show();
                        StoreCharge.this.finish();
                        // addBanks();
                        // redirectClass();
                        mDialog.dismiss();
                    }
                });

                mDialog.findViewById(R.id.net_bv).setVisibility(View.GONE);
            }
        });

    }
}
