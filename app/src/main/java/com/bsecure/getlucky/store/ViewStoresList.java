package com.bsecure.getlucky.store;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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
import com.bsecure.getlucky.adpters.StoreListAdapter;
import com.bsecure.getlucky.common.AppPreferences;
import com.bsecure.getlucky.helper.RecyclerViewSwipeHelper;
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
    Dialog mDialog,InactiveDiloag;
    private String text_stats,message;
    private IntentFilter filter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_store_list);

        filter=new IntentFilter("com.store_refrsh");
        filter.setPriority(1);
        findViewById(R.id.id_add_store).setOnClickListener(this);

        mRecyclerView=findViewById(R.id.view_store_rec);
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

        RecyclerViewSwipeHelper swipeHelper = new RecyclerViewSwipeHelper(this, mRecyclerView) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons,int mPos) {
                underlayButtons.add(new RecyclerViewSwipeHelper.UnderlayButton(
                        "Delete",
                        0,
                        Color.parseColor("#FF3C30"),
                        new RecyclerViewSwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                // TODO: onDelete
                               getDeleteDiloag(storeListModelList.get(pos).getStore_id());
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

                                Intent edit_store=new Intent(ViewStoresList.this, EditStore.class);
                                edit_store.putExtra("store_id",storeListModelList.get(pos).getStore_id());
                                edit_store.putExtra("store_name",storeListModelList.get(pos).getStore_name());
                                edit_store.putExtra("store_image",storeListModelList.get(pos).getStore_image());
                                edit_store.putExtra("area",storeListModelList.get(pos).getArea());
                                edit_store.putExtra("city",storeListModelList.get(pos).getCity());
                                edit_store.putExtra("state",storeListModelList.get(pos).getState());
                                edit_store.putExtra("country",storeListModelList.get(pos).getCountry());
                                edit_store.putExtra("pin_code",storeListModelList.get(pos).getPin_code());
                                edit_store.putExtra("store_phone_number",storeListModelList.get(pos).getStore_phone_number());
                                edit_store.putExtra("categories",storeListModelList.get(pos).getCategories());
                                edit_store.putExtra("keywords",storeListModelList.get(pos).getKeywords());
                                edit_store.putExtra("categories_array",storeListModelList.get(pos).getCategories_array());
                                edit_store.putExtra("keywords_array",storeListModelList.get(pos).getKeywords_array());
                                edit_store.putExtra("custom_keywords",storeListModelList.get(pos).getCustom_keywords());
                                startActivity(edit_store);
                                overridePendingTransition(R.anim.fade_in_anim,R.anim.fade_out_anim);
                            }
                        }
                ));
                String sts=storeListModelList.get(mPos).getStatus();
                if (sts.equalsIgnoreCase("0")){
                    text_stats="In-Active";
                }else{
                    text_stats="Active";
                }
                underlayButtons.add(new RecyclerViewSwipeHelper.UnderlayButton(
                        text_stats,
                        0,
                        Color.parseColor("#C7C7CB"),
                        new RecyclerViewSwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                // TODO: OnUnshare
                                getInactiveDiloag(storeListModelList.get(pos).getStore_id(),storeListModelList.get(pos).getStatus());
                            }
                        }
                ));
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeHelper);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        viewStorsList();
    }


    @Override
    protected void onResume() {
        registerReceiver(mBroadcastReceiver,filter);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    BroadcastReceiver mBroadcastReceiver=new BroadcastReceiver() {
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
                MethodResquest req=new MethodResquest(this, this, Constants.PATH + "view_stores", object.toString(), 101);
                req.dismissProgress(this);
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
            case R.id.id_add_store:

                Intent store=new Intent(this, AddStore.class);
                startActivity(store);
                overridePendingTransition(R.anim.fade_in_anim,R.anim.fade_out_anim);
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
                                storeListModel.setStore_id(jsonobject.optString("store_id"));
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
                                storeListModel.setKeywords(jsonobject.optString("keywords"));
                                storeListModel.setCategories(jsonobject.optString("categories"));
                                storeListModel.setCategories_array(jsonobject.optString("categories_array"));
                                storeListModel.setKeywords_array(jsonobject.optString("keywords_array"));
                                storeListModel.setStatus(jsonobject.optString("status"));
                                storeListModel.setCustom_keywords(jsonobject.optString("custom_keywords"));
                                storeListModelList.add(storeListModel);
                            }
                            adapter = new StoreListAdapter(storeListModelList, this, this);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                            mRecyclerView.setLayoutManager(linearLayoutManager);
                            mRecyclerView.setAdapter(adapter);
                        }else{
                            findViewById(R.id.spin_kit).setVisibility(View.GONE);
                            findViewById(R.id.id_add_store).setVisibility(View.GONE);
                            findViewById(R.id.no_data).setVisibility(View.VISIBLE);
                            Intent store=new Intent(this, AddStore.class);
                            startActivity(store);
                            overridePendingTransition(R.anim.fade_in_anim,R.anim.fade_out_anim);
                            finish();
                        }
                    }else{
                        mSwipeRefreshLayout.setRefreshing(false);
                        mSwipeRefreshLayout.setEnabled(true);
                        findViewById(R.id.spin_kit).setVisibility(View.GONE);
                       findViewById(R.id.no_data).setVisibility(View.VISIBLE);
                        findViewById(R.id.id_add_store).setVisibility(View.GONE);
                        ((TextView)findViewById(R.id.no_data)).setText(object1.optString("statusdescription"));
                        Intent store=new Intent(this, AddStore.class);
                        startActivity(store);
                        overridePendingTransition(R.anim.fade_in_anim,R.anim.fade_out_anim);
                        finish();
                    }
                    break;
                case 102:
                    JSONObject deletObj=new JSONObject(response.toString());
                    if (deletObj.optString("statuscode").equalsIgnoreCase("200")){
                        viewStorsList();
                        Toast.makeText(this, deletObj.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    }else{
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
    private void getInactiveDiloag(final String store_id,final String status) {

        if (status.equalsIgnoreCase("0")){
            message="Are you Sure You Want To Inactivate Store?";
        }else{
            message="Are you Sure You Want To Activate Store?";
        }
        InactiveDiloag=new Dialog(this,R.style.Theme_MaterialComponents_BottomSheetDialog);
        InactiveDiloag.setContentView(R.layout.custom_alert_show);
        ((TextView)InactiveDiloag.findViewById(R.id.text_message)).setText(message);
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

                getinactiveStore(store_id,status);
                InactiveDiloag.dismiss();
            }
        });
    }
    private void getDeleteDiloag(final String store_id) {

        mDialog=new Dialog(this,R.style.Theme_MaterialComponents_BottomSheetDialog);
        mDialog.setContentView(R.layout.custom_alert_show);
        mDialog.show();
        mDialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
        mDialog.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getDeleteStore(store_id);
                mDialog.dismiss();
            }
        });
    }

    private void getDeleteStore(String store_id) {

        try {
            JSONObject object = new JSONObject();
            object.put("store_id", store_id);
           new MethodResquest(this, this, Constants.PATH + "delete_store", object.toString(), 102);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void getinactiveStore(String store_id,String k_status) {

        String myStatus;
        try {
            if (k_status.equalsIgnoreCase("0")){
                myStatus="1";
            }else{
                myStatus="0";
            }
            JSONObject object = new JSONObject();
            object.put("store_id", store_id);
            object.put("status", myStatus);
           new MethodResquest(this, this, Constants.PATH + "set_store_status", object.toString(), 102);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
