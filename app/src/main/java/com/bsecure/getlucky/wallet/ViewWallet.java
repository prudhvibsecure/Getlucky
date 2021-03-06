package com.bsecure.getlucky.wallet;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import afu.org.checkerframework.checker.javari.qual.ThisMutable;

public class ViewWallet extends AppCompatActivity implements RequestHandler, View.OnClickListener {
    JSONArray ayArray;
    String wallet_amt = "", bwallet_amt, customer_number, payment_type;
    TextView total_amt, total_amt_b, total_amt_clr, history_wl, history_w2;
    TextInputEditText tv_wamt, tv_amount_b;
    private IntentFilter filter;
    private static DecimalFormat df = new DecimalFormat("0.00");
    LinearLayout add, pay;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_wallet);

        add = findViewById(R.id.add);
        pay = findViewById(R.id.pay);

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewWallet.this,PayStore.class));
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(ViewWallet.this, StoreCharge.class);
                in.putExtra("code","1");
                startActivity(in);
            }
        });
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        filter = new IntentFilter("com.addbank_refrsh2");
        filter.setPriority(1);
        total_amt_b = findViewById(R.id.total_amt_b);
        tv_amount_b = findViewById(R.id.tv_amount_b);
        tv_amount_b.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                String text = arg0.toString();
                if (text.contains(".") && text.substring(text.indexOf(".") + 1).length() > 2) {
                    tv_amount_b.setText(text.substring(0, text.length() - 1));
                    tv_amount_b.setSelection(tv_amount_b.getText().length());
                }
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            public void afterTextChanged(Editable arg0) {
            }
        });
        total_amt = findViewById(R.id.total_amt);
        total_amt_clr = findViewById(R.id.total_amt_clr);
        tv_wamt = findViewById(R.id.tv_amount);
        tv_wamt.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                String text = arg0.toString();
                if (text.contains(".") && text.substring(text.indexOf(".") + 1).length() > 2) {
                    tv_wamt.setText(text.substring(0, text.length() - 1));
                    tv_wamt.setSelection(tv_wamt.getText().length());
                }
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            public void afterTextChanged(Editable arg0) {
            }
        });
        history_wl = findViewById(R.id.history_wl);
        history_w2 = findViewById(R.id.history_w2);
        history_wl.setOnClickListener(this);
        history_w2.setOnClickListener(this);
        findViewById(R.id.bacl_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.hideKeyboard(ViewWallet.this);
                overridePendingTransition(R.anim.fade_out_anim, R.anim.fade_in_anim);
                finish();
            }
        });
        String session_data = AppPreferences.getInstance(this).getFromStore("userData");
        Log.e("session::::", session_data);
        if (session_data != null && !TextUtils.isEmpty(session_data)) {
            try {
                ayArray = new JSONArray(session_data);
                ImageView profile = (ImageView) findViewById(R.id.tv_profileicon);
                Glide.with(this).load(ayArray.getJSONObject(0).optString("profile_image")).into(profile);
                ((TextView) findViewById(R.id.name_n)).setText(ayArray.getJSONObject(0).optString("name"));
              //  customer_number = ayArray.getJSONObject(0).optString("customer_number");
                customer_number =AppPreferences.getInstance(this).getFromStore("customer_number");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        if (TextUtils.isEmpty(customer_number)||customer_number.equalsIgnoreCase("1")) {
            findViewById(R.id.v1_wall).setVisibility(View.VISIBLE);
            findViewById(R.id.v1_wall2).setVisibility(View.GONE);
            add.setVisibility(View.GONE);
        } else {
            findViewById(R.id.v1_wall).setVisibility(View.VISIBLE);
            findViewById(R.id.v1_wall2).setVisibility(View.VISIBLE);
            add.setVisibility(View.VISIBLE);

        }
        findViewById(R.id.tv_transfer).setOnClickListener(this);
        findViewById(R.id.tv_recharge_b).setOnClickListener(this);
        //findViewById(R.id.tv_transfer_b).setOnClickListener(this);
        getWallet();
    }

    @Override
    protected void onResume() {
        registerReceiver(mBroadcastReceiver, filter);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            tv_wamt.setText("");
            tv_amount_b.setText("");
            getWallet();
        }
    };

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
                        bwallet_amt = oob.optString("business_wallet_amount");
                        total_amt_clr.setText("* ₹ " + oob.optString("wallet_subject_to_clearance_amount") + " is subject to clearance");
                    }
                    break;
                case 102:
                    JSONObject oob1 = new JSONObject(response.toString());
                    if (oob1.optString("statuscode").equalsIgnoreCase("200")) {
                        final Dialog custom_alert_cash = new Dialog(this, R.style.Theme_MaterialComponents_Light_BottomSheetDialog);
                        custom_alert_cash.setContentView(R.layout.custom_alert_show_new);
                        custom_alert_cash.show();
                        custom_alert_cash.setCancelable(false);
                        ((TextView) custom_alert_cash.findViewById(R.id.text_message)).setText(Html.fromHtml(oob1.optString("statusdescription")));
                        custom_alert_cash.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                getWallet();
                                tv_amount_b.setText("");
                                custom_alert_cash.dismiss();
                            }
                        });
                    }
                    break;

                case 200:

                    JSONObject obj = new JSONObject(response.toString());
                    if(obj.optString("statuscode").equalsIgnoreCase("200"))
                    {
                        Toast.makeText(this, obj.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                        tv_wamt.setText("");
                        tv_wamt.setHint("Amount");
                        getWallet();
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
                if (amt.startsWith(".")){
                    tv_wamt.requestFocus();
                    return;
                }

                if (amt.length() == 0 || amt.equals("0")) {
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

                final Dialog mDialog = new Dialog(this, R.style.Theme_MaterialComponents_Light_BottomSheetDialog);
                mDialog.setContentView(R.layout.paymetoption);
                mDialog.show();

                mDialog.findViewById(R.id.net_bank).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        payment_type = "Net Banking";
                        redirectClass();
                        AppPreferences.getInstance(getApplicationContext()).addToStore("amount", tv_wamt.getText().toString().trim(), true);
                        AppPreferences.getInstance(getApplicationContext()).addToStore("p_type", payment_type, true);
                        mDialog.dismiss();
                        //addBanks();
                    }
                });
                mDialog.findViewById(R.id.net_gp).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        payment_type = "google pay";
                        Toast.makeText(ViewWallet.this, "Coming Soon", Toast.LENGTH_SHORT).show();
                        //addBanks();
                        // mDialog.dismiss();
                        redirectClass();
                    }
                });
                mDialog.findViewById(R.id.net_paytim).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        payment_type = "paytm";
                        Toast.makeText(ViewWallet.this, "Coming Soon", Toast.LENGTH_SHORT).show();
                        // addBanks();
                        // redirectClass();
                        mDialog.dismiss();
                    }
                });

                mDialog.findViewById(R.id.net_bv).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        payment_type = "Business Wallet";
                        //Toast.makeText(ViewWallet.this, "Coming Soon", Toast.LENGTH_SHORT).show();
                        // addBanks();
                        // redirectClass();
                        transferToBv();
                        mDialog.dismiss();
                    }
                });

                break;
            case R.id.history_wl:
                Intent mp_hist = new Intent(this, HistoryPayment.class);
                mp_hist.putExtra("case", "2");
                startActivity(mp_hist);
                overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
                break;
            case R.id.history_w2:
                Intent mp_hist1 = new Intent(this, HistoryPayment.class);
                mp_hist1.putExtra("case", "1");
                startActivity(mp_hist1);
                overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
                break;
            case R.id.tv_recharge_b:
                String bamt = tv_amount_b.getText().toString().trim();
                if (bamt.startsWith(".")){
                    tv_amount_b.requestFocus();
                    return;
                }
                if (bamt.length() == 0 || bamt.equals("0")) {
                    Toast.makeText(this, "Please Enter Amount", Toast.LENGTH_SHORT).show();
                    tv_amount_b.requestFocus();
                    return;
                }
                float bw_amount = Float.parseFloat(bwallet_amt);
                float bamount = Float.parseFloat(bamt);
                if (bamount > bw_amount) {
                    Toast.makeText(this, "Insufficient Funds", Toast.LENGTH_SHORT).show();
                    tv_amount_b.requestFocus();
                    return;
                }
                addBanks();
                break;
        }

    }

    private void transferToBv() {

        try {
            JSONObject object = new JSONObject();
            object.put("customer_id", ayArray.getJSONObject(0).optString("customer_id"));
            object.put("amount", tv_wamt.getText().toString().trim());
            new MethodResquest(this, this, Constants.PATH + "wallet_to_business_wallet", object.toString(), 200);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void redirectClass() {
        Intent mp_hist = new Intent(this, ViewBankList.class);
        mp_hist.putExtra("payment_type", payment_type);
        mp_hist.putExtra("amount", tv_wamt.getText().toString().trim());
        startActivity(mp_hist);
        overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);

    }

    private void addBanks() {

        try {
            JSONObject object = new JSONObject();
            object.put("customer_id", ayArray.getJSONObject(0).optString("customer_id"));
            object.put("amount", tv_amount_b.getText().toString().trim());
            object.put("request_date", System.currentTimeMillis());
            object.put("bank_id", "");
            object.put("payment_type", "");
            new MethodResquest(this, this, Constants.PATH + "wallet/transfer_wallet", object.toString(), 102);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
