package com.bsecure.getlucky.operator;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bsecure.getlucky.R;
import com.bsecure.getlucky.adpters.OperatorListAdapter;
import com.bsecure.getlucky.adpters.StoreListOwnerAdapter;
import com.bsecure.getlucky.common.AppPreferences;
import com.bsecure.getlucky.helper.RecyclerViewSwipeHelper;
import com.bsecure.getlucky.interfaces.RequestHandler;
import com.bsecure.getlucky.models.OperatorModel;
import com.bsecure.getlucky.models.StoreListModel;
import com.bsecure.getlucky.store.AddStore;
import com.bsecure.getlucky.store.EditStore;
import com.bsecure.getlucky.store.ViewStoresList;
import com.bsecure.getlucky.volleyhttp.Constants;
import com.bsecure.getlucky.volleyhttp.MethodResquest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OperatorsList extends AppCompatActivity implements RequestHandler, OperatorListAdapter.OperatorListAdapterListener {

    Button add_operator;
    String storeId;
    private RecyclerView mRecyclerView;
    Dialog mDialog, InactiveDiloag;
    private SwipeRefreshLayout mSwipeRefreshLayout = null;
    private String text_stats, message;
    LinearLayoutManager linearLayoutManager;
    private List<OperatorModel> operatorModelList;
    private OperatorListAdapter adapter;
    private IntentFilter filter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_store_list);
        findViewById(R.id.bacl_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        filter = new IntentFilter("com.operator_refrsh");
        filter.setPriority(1);
        storeId = getIntent().getStringExtra("store_id");
        add_operator = findViewById(R.id.id_add_store);
        add_operator.setText("+Add Operator");
        add_operator.setTextColor(Color.WHITE);
        mRecyclerView = findViewById(R.id.view_store_rec);
        mRecyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
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
                getOperators();

            }
        });
        getOperators();

        RecyclerViewSwipeHelper swipeHelper = new RecyclerViewSwipeHelper(this, mRecyclerView) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons, int mPos) {
                underlayButtons.add(new RecyclerViewSwipeHelper.UnderlayButton(
                        "Delete",
                        0,
                        Color.parseColor("#FF3C30"),
                        new RecyclerViewSwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                // TODO: onDelete
                                getDeleteDiloag(operatorModelList.get(pos).getStore_operator_id());
                            }
                        }
                ));

                underlayButtons.add(new RecyclerViewSwipeHelper.UnderlayButton(
                        "Edit",
                        0,
                        Color.parseColor("#FF9502"),
                        new RecyclerViewSwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                // TODO: OnTransfer
                                Intent edit_store = new Intent(OperatorsList.this, AddOperator.class);
                                edit_store.putExtra("store_operator_id", operatorModelList.get(pos).getStore_operator_id());
                                edit_store.putExtra("operator_name", operatorModelList.get(pos).getOperator_name());
                                edit_store.putExtra("username", operatorModelList.get(pos).getUsername());
                                edit_store.putExtra("password", operatorModelList.get(pos).getPassword());
                                edit_store.putExtra("code", "2");
                                edit_store.putExtra("store_id", storeId);
                                startActivity(edit_store);
                                overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);

                            }
                        }
                ));
                String sts = operatorModelList.get(mPos).getStatus();
                if (sts.equalsIgnoreCase("0")) {
                    text_stats = "In-Active";
                } else {
                    text_stats = "Active";
                }
                underlayButtons.add(new RecyclerViewSwipeHelper.UnderlayButton(
                        text_stats,
                        0,
                        Color.parseColor("#C7C7CB"),
                        new RecyclerViewSwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                // TODO: OnUnshare
                                getInactiveDiloag(operatorModelList.get(pos).getStore_operator_id(), operatorModelList.get(pos).getStatus());
                            }
                        }
                ));
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeHelper);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        findViewById(R.id.id_add_store).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent store = new Intent(getApplicationContext(), AddOperator.class);
                store.putExtra("store_id", storeId);
                store.putExtra("code", "1");
                startActivity(store);
                overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
            }
        });
    }

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                storeId = intent.getStringExtra("store_id");
                getOperators();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

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

    private void getOperators() {

        try {
            String session_data = AppPreferences.getInstance(this).getFromStore("userData");
            JSONArray ayArray = new JSONArray(session_data);
            JSONObject object = new JSONObject();
            object.put("customer_id", ayArray.getJSONObject(0).optString("customer_id"));
            object.put("store_id", storeId);
            MethodResquest req = new MethodResquest(this, this, Constants.PATH + "view_store_operators", object.toString(), 101);
            req.dismissProgress(this);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void redirectClass() {
        Intent ii = new Intent(this, OperatorsList.class);
        ii.putExtra("store_id", getIntent().getStringExtra("store_id"));
        startActivity(ii);
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
                        operatorModelList = new ArrayList<>();
                        mSwipeRefreshLayout.setRefreshing(false);
                        mSwipeRefreshLayout.setEnabled(true);
                        findViewById(R.id.spin_kit).setVisibility(View.GONE);
                        findViewById(R.id.no_data).setVisibility(View.GONE);
                        JSONArray jsonarray2 = object1.getJSONArray("store_operator_details");
                        if (jsonarray2.length() > 0) {
                            for (int i = 0; i < jsonarray2.length(); i++) {
                                JSONObject jsonobject = jsonarray2.getJSONObject(i);
                                OperatorModel storeListModel = new OperatorModel();
                                storeListModel.setPassword(jsonobject.optString("password"));
                                storeListModel.setOperator_name(jsonobject.optString("operator_name"));
                                storeListModel.setUsername(jsonobject.optString("username"));
                                storeListModel.setStatus(jsonobject.optString("status"));
                                storeListModel.setStore_operator_id(jsonobject.optString("store_operator_id"));
                                operatorModelList.add(storeListModel);
                            }
                            adapter = new OperatorListAdapter(operatorModelList, this, this);
                            mRecyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } else {
                            findViewById(R.id.spin_kit).setVisibility(View.GONE);
                            findViewById(R.id.id_add_store).setVisibility(View.GONE);
                            findViewById(R.id.no_data).setVisibility(View.VISIBLE);
                            Intent store = new Intent(this, AddOperator.class);
                            store.putExtra("store_id", storeId);
                            store.putExtra("code", "1");
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
                        Intent store = new Intent(this, AddOperator.class);
                        store.putExtra("store_id", storeId);
                        store.putExtra("code", "1");
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


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void requestEndedWithError(String error, int errorcode) {

    }

    @Override
    public void onRowClicked(List<StoreListModel> matchesList, int pos) {

    }

    private void getDeleteDiloag(final String opetator_id) {

        mDialog = new Dialog(this, R.style.Theme_MaterialComponents_BottomSheetDialog);
        mDialog.setContentView(R.layout.custom_alert_show);
        mDialog.show();
        ((TextView) mDialog.findViewById(R.id.text_message)).setText("Are You Sure You Want To Delete Store Operator?");
        mDialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
        mDialog.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getDeleteStore(opetator_id);
                mDialog.dismiss();
            }
        });
    }

    private void getDeleteStore(String opetator_id) {

        try {
            JSONObject object = new JSONObject();
            object.put("store_operator_id", opetator_id);
            new MethodResquest(this, this, Constants.PATH + "delete_store_operator", object.toString(), 102);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void getInactiveDiloag(final String store_id, final String status) {

        if (status.equalsIgnoreCase("0")) {
            message = "Are you Sure You Want To Inactivate Store Operator?";
        } else {
            message = "Are you Sure You Want To Activate Store Operator?";
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
    private void getinactiveStore(String store_id, String k_status) {

        String myStatus;
        try {
            if (k_status.equalsIgnoreCase("0")) {
                myStatus = "1";
            } else {
                myStatus = "0";
            }
            JSONObject object = new JSONObject();
            object.put("store_operator_id", store_id);
            object.put("status", myStatus);
            new MethodResquest(this, this, Constants.PATH + "set_store_operator_status", object.toString(), 102);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
