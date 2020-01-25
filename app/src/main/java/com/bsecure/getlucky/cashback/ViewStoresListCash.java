package com.bsecure.getlucky.cashback;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bsecure.getlucky.R;
import com.bsecure.getlucky.adpters.StoreListCashbackAdapter;
import com.bsecure.getlucky.common.AppPreferences;
import com.bsecure.getlucky.interfaces.RequestHandler;
import com.bsecure.getlucky.models.StoreListModel;
import com.bsecure.getlucky.operator.OperatorsListCashBack;
import com.bsecure.getlucky.store.AddStore;
import com.bsecure.getlucky.utils.Utils;
import com.bsecure.getlucky.volleyhttp.Constants;
import com.bsecure.getlucky.volleyhttp.MethodResquest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewStoresListCash extends AppCompatActivity implements View.OnClickListener, RequestHandler, StoreListCashbackAdapter.StoreAdapterListener {
    private SwipeRefreshLayout mSwipeRefreshLayout = null;
    private List<StoreListModel> storeListModelList;
    private StoreListCashbackAdapter adapter;
    private RecyclerView mRecyclerView;

    private IntentFilter filter;
    LinearLayoutManager linearLayoutManager;
    String session_data;
    JSONArray ayArray;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_store_list);

        filter = new IntentFilter("com.store_refrsh");
        filter.setPriority(1);
        findViewById(R.id.id_add_store).setOnClickListener(this);
        findViewById(R.id.id_add_store).setVisibility(View.GONE);

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
                viewStorsList();

            }
        });
        findViewById(R.id.bacl_btn).setOnClickListener(this);

        viewStorsList();

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
            session_data= AppPreferences.getInstance(this).getFromStore("userData");
            ayArray = new JSONArray(session_data);
            AppPreferences.getInstance(this).addToStore("operator_name", ayArray.getJSONObject(0).optString("name"), true);
            JSONObject object = new JSONObject();
            object.put("customer_id", ayArray.getJSONObject(0).optString("customer_id"));
            MethodResquest req = new MethodResquest(this, this, Constants.PATH + "view_stores", object.toString(), 101);
            req.dismissProgress(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.bacl_btn:
                Utils.hideKeyboard(this);
                overridePendingTransition(R.anim.fade_out_anim, R.anim.fade_in_anim);
                finish();
                break;
            case R.id.id_add_store:

                Intent store = new Intent(this, AddStore.class);
                startActivity(store);
                overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
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
                    Utils.hideKeyboard(this);
                    JSONObject object1 = new JSONObject(response.toString());
                    if (object1.optString("statuscode").equalsIgnoreCase("200")) {
                        storeListModelList = new ArrayList<>();
                        mSwipeRefreshLayout.setRefreshing(false);
                        mSwipeRefreshLayout.setEnabled(true);
                        findViewById(R.id.spin_kit).setVisibility(View.GONE);
                        findViewById(R.id.no_data).setVisibility(View.GONE);
                        JSONArray jsonarray2 = object1.getJSONArray("store_details");
                        if (jsonarray2.length() > 0) {
                            for (int i = 0; i < jsonarray2.length(); i++) {
                                JSONObject jsonobject = jsonarray2.getJSONObject(i);
                                StoreListModel storeListModel = new StoreListModel();
                                storeListModel.setStore_id(jsonobject.optString("store_id"));
                                storeListModel.setStore_name(jsonobject.optString("store_name"));
                                storeListModel.setAddress(jsonobject.optString("address"));
                                storeListModel.setArea(jsonobject.optString("area"));
                                storeListModel.setCity(jsonobject.optString("city"));
                                storeListModel.setCountry(jsonobject.optString("country"));
                                storeListModel.setPin_code(jsonobject.optString("pin_code"));
                                storeListModel.setState(jsonobject.optString("state"));
                                storeListModel.setStore_image(jsonobject.optString("store_image"));
                                storeListModelList.add(storeListModel);
                            }
                            adapter = new StoreListCashbackAdapter(storeListModelList, this, this);
                            mRecyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } else {
                            Utils.hideKeyboard(this);
                            findViewById(R.id.spin_kit).setVisibility(View.GONE);
                            findViewById(R.id.id_add_store).setVisibility(View.GONE);
                            findViewById(R.id.no_data).setVisibility(View.VISIBLE);
                            Intent store = new Intent(this, AddStore.class);
                            startActivity(store);
                            overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
                            finish();
                        }
                    } else {
                        Utils.hideKeyboard(this);
                        mSwipeRefreshLayout.setRefreshing(false);
                        mSwipeRefreshLayout.setEnabled(true);
                        findViewById(R.id.spin_kit).setVisibility(View.GONE);
                        findViewById(R.id.no_data).setVisibility(View.VISIBLE);
                        findViewById(R.id.id_add_store).setVisibility(View.GONE);
                        ((TextView) findViewById(R.id.no_data)).setText(object1.optString("statusdescription"));
                        Intent store = new Intent(this, AddStore.class);
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
    public void onRowClicked(List<StoreListModel> matchesList, int pos) {

       /* Intent login = new Intent(this, OperatorsListCashBack.class);
        login.putExtra("store_id", matchesList.get(pos).getStore_id());
        login.putExtra("store_name", matchesList.get(pos).getStore_name());
        startActivity(login);
        overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);*/
        AppPreferences.getInstance(this).addToStore("store_name", matchesList.get(pos).getStore_name(), true);

        AppPreferences.getInstance(this).addToStore("username", "", true);
        AppPreferences.getInstance(this).addToStore("store_id", matchesList.get(pos).getStore_id(), true);

        Intent edit_store = new Intent(this, AddCashback.class);
        startActivity(edit_store);
        overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);

    }

}
