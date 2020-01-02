package com.bsecure.getlucky.wallet;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bsecure.getlucky.Login;
import com.bsecure.getlucky.R;
import com.bsecure.getlucky.ViewStoreDetails;
import com.bsecure.getlucky.adpters.BankAccountAdapter;
import com.bsecure.getlucky.adpters.StoreListOwnerAdapter;
import com.bsecure.getlucky.common.AppPreferences;
import com.bsecure.getlucky.helper.RecyclerViewSwipeHelper;
import com.bsecure.getlucky.interfaces.RequestHandler;
import com.bsecure.getlucky.models.AccountModel;
import com.bsecure.getlucky.models.StoreListModel;
import com.bsecure.getlucky.operator.OperatorsList;
import com.bsecure.getlucky.store.AddStore;
import com.bsecure.getlucky.store.EditStore;
import com.bsecure.getlucky.volleyhttp.Constants;
import com.bsecure.getlucky.volleyhttp.MethodResquest;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViewBankList extends AppCompatActivity implements View.OnClickListener, RequestHandler, BankAccountAdapter.BankAccountAdapterListListener {
    private SwipeRefreshLayout mSwipeRefreshLayout = null;
    private List<AccountModel> bankListModelList;
    private BankAccountAdapter adapter;
    private RecyclerView mRecyclerView;
    Dialog mDialog, InactiveDiloag;
    private String text_stats, message;
    private IntentFilter filter;
    Dialog dialog;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_banks_list);

        filter = new IntentFilter("com.addbank_refrsh");
        filter.setPriority(1);
        findViewById(R.id.id_add_store).setOnClickListener(this);
        findViewById(R.id.trs_wallet).setOnClickListener(this);
        ((Button)findViewById(R.id.id_add_store)).setText("Add Account");
        ((Button)findViewById(R.id.id_add_store)).setTextColor(Color.WHITE);
        mRecyclerView = findViewById(R.id.view_store_rec);
        mRecyclerView.setHasFixedSize(true);
        linearLayoutManager= new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swip_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorPrimaryDark,
                R.color.colorPrimaryDark, R.color.colorPrimaryDark, R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setEnabled(false);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                findViewById(R.id.spin_kit).setVisibility(View.GONE);
                findViewById(R.id.no_data).setVisibility(View.GONE);
                viewStorsList();

            }
        });
        findViewById(R.id.bacl_btn).setOnClickListener(this);

        viewStorsList();
        RecyclerViewSwipeHelper swipeHelper = new RecyclerViewSwipeHelper(this, mRecyclerView) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons, int mPos) {
                underlayButtons.add(new UnderlayButton(
                        "Delete",
                        0,
                        Color.parseColor("#FF3C30"),
                        new UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                // TODO: onDelete
                                getDeleteDiloag(bankListModelList.get(pos).getBank_id());
                            }
                        }
                ));

                /*underlayButtons.add(new UnderlayButton(
                        "Edit",
                        0,
                        Color.parseColor("#FF9502"),
                        new UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                // TODO: OnTransfer

                               // startActivity(edit_store);
                                overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);

                            }
                        }
                ));
                String sts = bankListModelList.get(mPos).getStatus();
                if (sts.equalsIgnoreCase("0")) {
                    text_stats = "In-Active";
                } else {
                    text_stats = "Active";
                }
                underlayButtons.add(new UnderlayButton(
                        text_stats,
                        0,
                        Color.parseColor("#C7C7CB"),
                        new UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                // TODO: OnUnshare
                                getInactiveDiloag(bankListModelList.get(pos).getBank_id(), bankListModelList.get(pos).getStatus());
                            }
                        }
                ));*/
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeHelper);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);




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
            viewStorsList();
        }
    };

    private void viewStorsList() {
        try {
            String session_data = AppPreferences.getInstance(this).getFromStore("userData");
            JSONArray ayArray = new JSONArray(session_data);
            JSONObject object = new JSONObject();
            object.put("customer_id", ayArray.getJSONObject(0).optString("customer_id"));
            MethodResquest req = new MethodResquest(this, this, Constants.PATH + "view_banks", object.toString(), 101);
            req.dismissProgress(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.bacl_btn:
                overridePendingTransition(R.anim.fade_out_anim, R.anim.fade_in_anim);
                finish();
                break;
            case R.id.id_add_store:

                Intent store = new Intent(this, AddBankAccount.class);
                startActivity(store);
                overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
                break;
                case R.id.trs_wallet:


                break;


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

                    JSONObject object1 = new JSONObject(response.toString());
                    if (object1.optString("statuscode").equalsIgnoreCase("200")) {
                        bankListModelList = new ArrayList<>();
                        mSwipeRefreshLayout.setRefreshing(false);
                        mSwipeRefreshLayout.setEnabled(true);
                        findViewById(R.id.spin_kit).setVisibility(View.GONE);
                        findViewById(R.id.no_data).setVisibility(View.GONE);
                        JSONArray jsonarray2 = object1.getJSONArray("bank_details");
                        if (jsonarray2.length() > 0) {
                            for (int i = 0; i < jsonarray2.length(); i++) {
                                JSONObject jsonobject = jsonarray2.getJSONObject(i);
                                AccountModel accountModel = new AccountModel();
                                accountModel.setBank_acc_name(jsonobject.optString("bank_acc_name"));
                                accountModel.setBank_id(jsonobject.optString("bank_id"));
                                accountModel.setBank_acc_no(jsonobject.optString("bank_acc_no"));
                                accountModel.setBank_address(jsonobject.optString("bank_address"));
                                accountModel.setStatus(jsonobject.optString("status"));
                                accountModel.setIfsc_code(jsonobject.optString("ifsc_code"));
                                bankListModelList.add(accountModel);
                            }
                            adapter = new BankAccountAdapter(bankListModelList, this, this);
                            mRecyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } else {
                            findViewById(R.id.spin_kit).setVisibility(View.GONE);
                            findViewById(R.id.id_add_store).setVisibility(View.GONE);
                            findViewById(R.id.no_data).setVisibility(View.VISIBLE);
                            Intent store = new Intent(this, AddBankAccount.class);
                            startActivity(store);
                            overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
                            finish();
                        }
                    } else {
                        mSwipeRefreshLayout.setRefreshing(false);
                        mSwipeRefreshLayout.setEnabled(true);
                        findViewById(R.id.spin_kit).setVisibility(View.GONE);
                        findViewById(R.id.no_data).setVisibility(View.VISIBLE);
                        findViewById(R.id.id_add_store).setVisibility(View.GONE);
                        ((TextView) findViewById(R.id.no_data)).setText(object1.optString("statusdescription"));
                        Intent store = new Intent(this, AddBankAccount.class);
                        startActivity(store);
                        overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
                        finish();
                    }
                    break;
                case 102:
                    JSONObject deletObj = new JSONObject(response.toString());
                    if (deletObj.optString("statuscode").equalsIgnoreCase("200")) {
                      redirectClass();
                        Toast.makeText(this, deletObj.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, deletObj.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    }
                    break;

                case 103:
                    JSONObject object = new JSONObject(response.toString());
                    if (object.optString("statuscode").equalsIgnoreCase("200")) {
                        dialog.dismiss();
                        redirectClass();
                        Toast.makeText(this, object.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, object.optString("statusdescription"), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onRowClicked(List<AccountModel> matchesList, int pos) {
    }

    private void getInactiveDiloag(final String store_id, final String status) {

        if (status.equalsIgnoreCase("0")) {
            message = "Are you Sure You Want To Inactivate Bank Details?";
        } else {
            message = "Are you Sure You Want To Activate Bank Details?";
        }
        InactiveDiloag = new Dialog(this, R.style.Theme_MaterialComponents_BottomSheetDialog);
        InactiveDiloag.setContentView(R.layout.custom_alert_show);
        ((TextView) InactiveDiloag.findViewById(R.id.text_message)).setText(message);
        InactiveDiloag.show();
        InactiveDiloag.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InactiveDiloag.dismiss();
            }
        });
        InactiveDiloag.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getinactiveStore(store_id, status);
                InactiveDiloag.dismiss();
            }
        });
    }

    private void getDeleteDiloag(final String bank_id) {

        mDialog = new Dialog(this, R.style.Theme_MaterialComponents_BottomSheetDialog);
        mDialog.setContentView(R.layout.custom_alert_show);
        mDialog.show();
        ((TextView) mDialog.findViewById(R.id.text_message)).setText("Are you Sure You Want To Delete Bank Details?");
        mDialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
        mDialog.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getDeleteStore(bank_id);
                mDialog.dismiss();
            }
        });
    }

    private void getDeleteStore(String bank_id) {

        try {
            JSONObject object = new JSONObject();
            object.put("bank_id", bank_id);
            new MethodResquest(this, this, Constants.PATH + "delete_bank", object.toString(), 102);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getinactiveStore(String bank_id, String k_status) {

        String myStatus;
        try {
            if (k_status.equalsIgnoreCase("0")) {
                myStatus = "1";
            } else {
                myStatus = "0";
            }
            JSONObject object = new JSONObject();
            object.put("bank_id", bank_id);
            object.put("status", myStatus);
            new MethodResquest(this, this, Constants.PATH + "set_bank_status", object.toString(), 102);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public  void
    redirectClass(){
        Intent in = new Intent(this, ViewBankList.class);
        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(in);
        overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
    }
}
