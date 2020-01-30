package com.bsecure.getlucky.cashback;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bsecure.getlucky.R;
import com.bsecure.getlucky.common.AppPreferences;
import com.bsecure.getlucky.interfaces.RequestHandler;
import com.bsecure.getlucky.utils.Utils;
import com.bsecure.getlucky.volleyhttp.Constants;
import com.bsecure.getlucky.volleyhttp.MethodResquest;

import org.json.JSONObject;

public class AddCashback extends AppCompatActivity implements RequestHandler {
    String add_amount = "", add_cust_code = "",add_bll_no;
    Dialog custom_alert_cash;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_cashback);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        findViewById(R.id.bacl_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.hideKeyboard(AddCashback.this);
                overridePendingTransition(R.anim.fade_out_anim, R.anim.fade_in_anim);
                finish();
            }
        });
        findViewById(R.id.bt_add_cash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // addCashBack();
                alertGet();
            }
        });
        ((EditText) findViewById(R.id.add_amount)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

               if( charSequence.length()>0){
                   add_amount=charSequence.toString();
                   ((TextView) findViewById(R.id.fv)).setText("Note : Cashback is applicable for purchase amount of \n₹ " + add_amount + " or less");
               }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        ((TextView) findViewById(R.id.store_nm)).setText(AppPreferences.getInstance(this).getFromStore("store_name"));
        ((TextView) findViewById(R.id.operator_nm)).setText(AppPreferences.getInstance(this).getFromStore("operator_name"));
        ((TextView) findViewById(R.id.fv)).setText("Note : Cashback is applicable for purchase amount of \n ₹ " + "0" + " or less");
    }

    private void alertGet() {
        add_amount = ((EditText) findViewById(R.id.add_amount)).getText().toString().trim();
        if (add_amount.length() == 0) {
            Toast.makeText(AddCashback.this, "Please Fill Required Fields", Toast.LENGTH_SHORT).show();
            return;
        }
        add_cust_code = ((EditText) findViewById(R.id.add_cust_code)).getText().toString().trim();
        if (add_cust_code.length() == 0) {
            Toast.makeText(AddCashback.this, "Please Fill Required Fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (add_cust_code.contains(" ")) {
            Toast.makeText(this, "Spaces Not Allowed", Toast.LENGTH_SHORT).show();
            return;
        }
        add_bll_no = ((EditText) findViewById(R.id.add_bll_no)).getText().toString();
        if (add_bll_no.contains(" ")||add_bll_no.startsWith(" ")) {
            Toast.makeText(this, "Spaces Not Allowed", Toast.LENGTH_SHORT).show();
            return;
        }
        custom_alert_cash = new Dialog(this, R.style.Theme_MaterialComponents_Light_BottomSheetDialog);
        custom_alert_cash.setContentView(R.layout.custom_alert_cash);
        custom_alert_cash.show();
        custom_alert_cash.setCancelable(false);
        ((TextView) custom_alert_cash.findViewById(R.id.text_message)).setText(Html.fromHtml("Amount - " + add_amount + ",<br/> Customer Code - " + add_cust_code + ",<br/><br/>Are You Sure You Want To Submit"));
        custom_alert_cash.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                custom_alert_cash.dismiss();
                addCashBack();

            }
        });
        custom_alert_cash.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                custom_alert_cash.dismiss();
            }
        });
    }

    private void addCashBack() {
        //store_id,customer_id,amount,coupon_code,bill_no,username
        try {

            String user_nm = AppPreferences.getInstance(this).getFromStore("username");
            JSONObject object = new JSONObject();

            object.put("store_id", AppPreferences.getInstance(this).getFromStore("store_id"));
            object.put("customer_id", AppPreferences.getInstance(this).getFromStore("customer_id"));
            object.put("amount", add_amount);
            object.put("customer_code", add_cust_code);
            object.put("bill_no", add_bll_no);
            object.put("username", user_nm);
            MethodResquest req = new MethodResquest(this, this, Constants.PATH + "cash_back/cash_back", object.toString(), 101);
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
                    JSONObject object = new JSONObject(response.toString());
                    if (object.optString("statuscode").equalsIgnoreCase("200")) {
                        ((EditText) findViewById(R.id.add_amount)).setText("");
                        ((EditText) findViewById(R.id.add_cust_code)).setText("");
                        ((EditText) findViewById(R.id.add_bll_no)).setText("");
                        add_amount = "";
                        add_cust_code = "";
                        Toast.makeText(this, object.optString("statusdescription"), Toast.LENGTH_LONG).show();
                        custom_alert_cash.dismiss();
                    } else {
                        Toast.makeText(this, object.optString("statusdescription"), Toast.LENGTH_LONG).show();
                    }
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
