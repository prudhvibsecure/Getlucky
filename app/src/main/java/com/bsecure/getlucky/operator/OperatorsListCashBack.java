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
import com.bsecure.getlucky.adpters.OperatorListCashbackAdapter;
import com.bsecure.getlucky.cashback.AddCashback;
import com.bsecure.getlucky.common.AppPreferences;
import com.bsecure.getlucky.helper.RecyclerViewSwipeHelper;
import com.bsecure.getlucky.interfaces.RequestHandler;
import com.bsecure.getlucky.models.OperatorModel;
import com.bsecure.getlucky.models.StoreListModel;
import com.bsecure.getlucky.volleyhttp.Constants;
import com.bsecure.getlucky.volleyhttp.MethodResquest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OperatorsListCashBack extends AppCompatActivity implements RequestHandler, OperatorListCashbackAdapter.OperatorListAdapterListener {

    Button add_operator;
    String storeId;
    private RecyclerView mRecyclerView;
    Dialog mDialog, InactiveDiloag;
    private SwipeRefreshLayout mSwipeRefreshLayout = null;
    private String text_stats, message;
    LinearLayoutManager linearLayoutManager;
    private List<OperatorModel> operatorModelList;
    private OperatorListCashbackAdapter adapter;
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
        getOperators();

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
                            adapter = new OperatorListCashbackAdapter(operatorModelList, this, this);
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

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void requestEndedWithError(String error, int errorcode) {

    }

    @Override
    public void onRowClicked(List<OperatorModel> matchesList, int pos) {
        AppPreferences.getInstance(this).addToStore("store_name",getIntent().getStringExtra("store_name"), true);
        AppPreferences.getInstance(this).addToStore("operator_name",matchesList.get(pos).getOperator_name(), true);
        AppPreferences.getInstance(this).addToStore("username",matchesList.get(pos).getUsername(), true);
        AppPreferences.getInstance(this).addToStore("store_id",storeId, true);

        Intent edit_store = new Intent(this, AddCashback.class);
        startActivity(edit_store);
        overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);


    }

}
