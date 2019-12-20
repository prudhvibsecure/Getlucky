package com.bsecure.getlucky.wallet;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bsecure.getlucky.R;
import com.bsecure.getlucky.common.AppPreferences;
import com.bsecure.getlucky.interfaces.RequestHandler;
import com.bsecure.getlucky.volleyhttp.Constants;
import com.bsecure.getlucky.volleyhttp.MethodResquest;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ViewWallet extends AppCompatActivity implements RequestHandler, View.OnClickListener {
    JSONArray ayArray;
    String wallet_amt = "";
    TextView total_amt, total_amt_b, total_amt_clr;
    TextInputEditText tv_wamt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_wallet);

        total_amt_b = findViewById(R.id.total_amt_b);
        total_amt = findViewById(R.id.total_amt);
        total_amt_clr = findViewById(R.id.total_amt_clr);
        tv_wamt = findViewById(R.id.tv_amount);
        String session_data = AppPreferences.getInstance(this).getFromStore("userData");
        if (session_data != null && !TextUtils.isEmpty(session_data)) {
            try {
                ayArray = new JSONArray(session_data);
                ImageView profile = (ImageView) findViewById(R.id.tv_profileicon);
                Glide.with(this).load(ayArray.getJSONObject(0).optString("profile_image")).into(profile);
                ((TextView) findViewById(R.id.name_n)).setText(ayArray.getJSONObject(0).optString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        findViewById(R.id.tv_transfer).setOnClickListener(this);
        getWallet();
    }

    private void getWallet() {

        try {
            JSONObject object = new JSONObject();
            object.put("customer_id", ayArray.getJSONObject(0).optString("customer_id"));
            MethodResquest req = new MethodResquest(this, this, Constants.PATH + "wallet/view_wallet", object.toString(), 101);
            req.dismissProgress(this);
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
                case 101:
                    JSONObject oob = new JSONObject(response.toString());
                    if (oob.optString("statuscode").equalsIgnoreCase("200")) {

                        wallet_amt = oob.optString("wallet_amount");
                        total_amt.setText("₹ " + oob.optString("wallet_amount"));
                        total_amt_b.setText("₹ " + oob.optString("business_wallet_amount"));
                        total_amt_clr.setText("* ₹ " + oob.optString("wallet_subject_to_clearance_amount")+" is subject to clearance");
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
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.tv_transfer:
                String amt = tv_wamt.getText().toString().trim();
                if (amt.length() == 0) {
                    Toast.makeText(this, "Please Enter Amount", Toast.LENGTH_SHORT).show();
                    tv_wamt.requestFocus();
                    return;
                }
                float w_amount = Float.parseFloat(wallet_amt);
                float amount = Float.parseFloat(amt);
                if (amount > w_amount) {
                    Toast.makeText(this, "Insufficient Funds", Toast.LENGTH_SHORT).show();
                    tv_wamt.requestFocus();
                    return;
                }
                break;
        }

    }
}
