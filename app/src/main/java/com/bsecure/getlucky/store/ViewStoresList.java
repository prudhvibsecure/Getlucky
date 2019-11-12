package com.bsecure.getlucky.store;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bsecure.getlucky.Login;
import com.bsecure.getlucky.R;
import com.bsecure.getlucky.ViewStoreDetails;
import com.bsecure.getlucky.adpters.StoreListAdapter;
import com.bsecure.getlucky.common.AppPreferences;
import com.bsecure.getlucky.interfaces.RequestHandler;
import com.bsecure.getlucky.models.StoreListModel;
import com.bsecure.getlucky.volleyhttp.Constants;
import com.bsecure.getlucky.volleyhttp.MethodResquest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewStoresList extends AppCompatActivity implements View.OnClickListener , RequestHandler,StoreListAdapter.StoreAdapterListener {
    private SwipeRefreshLayout mSwipeRefreshLayout = null;
    private List<StoreListModel> storeListModelList;
    private StoreListAdapter adapter;
    private RecyclerView mRecyclerView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_store_list);

        mRecyclerView=findViewById(R.id.view_store_rec);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swip_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorPrimaryDark,
                R.color.colorPrimaryDark, R.color.colorPrimaryDark, R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setEnabled(false);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
              viewStorsList();

            }
        });
        findViewById(R.id.bacl_btn).setOnClickListener(this);
        viewStorsList();
    }

    private void viewStorsList() {
            try {
                String session_data = AppPreferences.getInstance(this).getFromStore("userData");
                JSONArray ayArray = new JSONArray(session_data);
                JSONObject object = new JSONObject();
                object.put("customer_id", ayArray.getJSONObject(0).optString("customer_id"));
                MethodResquest req=new MethodResquest(this, this, Constants.PATH + "view_stores", object.toString(), 101);
            } catch (Exception e) {
                e.printStackTrace();
            }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.bacl_btn:
                overridePendingTransition(R.anim.fade_out_anim,R.anim.fade_in_anim);
                finish();
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
                        storeListModelList=new ArrayList<>();
                        mSwipeRefreshLayout.setRefreshing(false);
                        mSwipeRefreshLayout.setEnabled(true);
                        findViewById(R.id.spin_kit).setVisibility(View.GONE);
                        findViewById(R.id.no_data).setVisibility(View.GONE);
                        JSONArray jsonarray2 = object1.getJSONArray("store_details");
                        if (jsonarray2.length() > 0) {
                            for (int i = 0; i < jsonarray2.length(); i++) {
                                JSONObject jsonobject = jsonarray2.getJSONObject(i);
                                StoreListModel storeListModel = new StoreListModel();
                                storeListModel.setStore_name(jsonobject.optString("store_name"));
                                storeListModel.setAddress(jsonobject.optString("address"));
                                storeListModel.setArea(jsonobject.optString("area"));
                                storeListModel.setCity(jsonobject.optString("city"));
                                storeListModel.setCountry(jsonobject.optString("country"));
                                storeListModel.setPin_code(jsonobject.optString("pin_code"));
                                storeListModel.setState(jsonobject.optString("state"));
                                storeListModel.setOffer(jsonobject.optString("offer"));
                                storeListModel.setSpecial_offer(jsonobject.optString("special_offer"));
                                storeListModel.setStore_image(jsonobject.optString("store_image"));
                                storeListModel.setStore_phone_number(jsonobject.optString("store_phone_number"));
                                storeListModelList.add(storeListModel);
                            }
                            adapter = new StoreListAdapter(storeListModelList, this, this);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                            mRecyclerView.setLayoutManager(linearLayoutManager);
                            mRecyclerView.setAdapter(adapter);
                        }else{
                            findViewById(R.id.spin_kit).setVisibility(View.GONE);
                            findViewById(R.id.no_data).setVisibility(View.VISIBLE);
                        }
                    }else{
                        mSwipeRefreshLayout.setRefreshing(false);
                        mSwipeRefreshLayout.setEnabled(true);
                        findViewById(R.id.spin_kit).setVisibility(View.GONE);
                       findViewById(R.id.no_data).setVisibility(View.VISIBLE);
                        ((TextView)findViewById(R.id.no_data)).setText(object1.optString("statusdescription"));
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
        String data= AppPreferences.getInstance(this).getFromStore("userData");
        if (data!=null &&!TextUtils.isEmpty(data)){
            Intent login=new Intent(this, ViewStoreDetails.class);
            login.putExtra("store_name",matchesList.get(pos).getStore_name());
            login.putExtra("store_image",matchesList.get(pos).getStore_image());
            login.putExtra("store_add",matchesList.get(pos).getArea()+","+matchesList.get(pos).getCity()+","+matchesList.get(pos).getState()+","+matchesList.get(pos).getState()+","+matchesList.get(pos).getPin_code());
            login.putExtra("store_offer",matchesList.get(pos).getOffer());
            login.putExtra("store_spofer",matchesList.get(pos).getSpecial_offer());
            login.putExtra("store_ph",matchesList.get(pos).getStore_phone_number());
            //login.putExtra("store_ph",matchesList.get(pos).getp());
            startActivity(login);
           overridePendingTransition(R.anim.fade_in_anim,R.anim.fade_out_anim);
        }else{
            Intent login=new Intent(this, Login.class);
            startActivity(login);
            overridePendingTransition(R.anim.fade_in_anim,R.anim.fade_out_anim);
        }
    }
}
