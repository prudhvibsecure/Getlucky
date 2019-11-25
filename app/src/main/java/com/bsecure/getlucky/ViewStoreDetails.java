package com.bsecure.getlucky;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bsecure.getlucky.adpters.SpecialOfferAdapter;
import com.bsecure.getlucky.common.AppPreferences;
import com.bsecure.getlucky.helper.RecyclerViewSwipeHelper;
import com.bsecure.getlucky.interfaces.RequestHandler;
import com.bsecure.getlucky.models.OfferModel;
import com.bsecure.getlucky.models.StoreListModel;
import com.bsecure.getlucky.store.EditStore;
import com.bsecure.getlucky.store.ViewStoresList;
import com.bsecure.getlucky.volleyhttp.Constants;
import com.bsecure.getlucky.volleyhttp.MethodResquest;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViewStoreDetails extends AppCompatActivity implements View.OnClickListener, RequestHandler, SpecialOfferAdapter.SpecialOfferListListener {

    ImageView store_img;
    TextView tv_store_name,store_address;
    RecyclerView sp_offer_vv;
    private SpecialOfferAdapter specialOfferAdapter;
    private List<OfferModel> spList;
    private String text_stats,message,msg;
    private Dialog InactiveDiloag,mDialog,editDiloag;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_store_details);

        store_img=findViewById(R.id.post_image);
        tv_store_name=findViewById(R.id.store_name);
        store_address=findViewById(R.id.store_address);
        sp_offer_vv=findViewById(R.id.sp_recyler);
        findViewById(R.id.bacl_btn).setOnClickListener(this);

        tv_store_name.setText(getIntent().getStringExtra("store_name"));
        store_address.setText(getIntent().getStringExtra("store_add"));
        if (!TextUtils.isEmpty(getIntent().getStringExtra("store_image"))) {
            Glide.with(this).load(Constants.PATH + "assets/upload/avatar/" +getIntent().getStringExtra("store_image")).into(store_img);
        }
        getSpecialOffers();

        RecyclerViewSwipeHelper swipeHelper = new RecyclerViewSwipeHelper(this, sp_offer_vv) {
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
                                getDeleteDiloag(spList.get(pos).getOffer_sp_id());
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
                                    editOfferDialog(spList.get(pos).getOffer_sp_id(),spList.get(pos).getOffer_description());
                            }
                        }
                ));
                String sts = spList.get(mPos).getStatus();
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
                                getInactiveDiloag(spList.get(pos).getOffer_sp_id(), spList.get(pos).getStatus());
                            }
                        }
                ));
            }
        };
    }

    private void editOfferDialog(final String offer_sp_id, String offer_description) {

        editDiloag = new Dialog(this, R.style.Theme_MaterialComponents_BottomSheetDialog);
        editDiloag.setContentView(R.layout.add_sp_offer);
        editDiloag.show();
        ((EditText) editDiloag.findViewById(R.id.sp_offer)).setText(offer_description);
        editDiloag.findViewById(R.id.bt_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String offer = ((EditText) editDiloag.findViewById(R.id.sp_offer)).getText().toString();
                if (offer.length() == 0) {
                    Toast.makeText(ViewStoreDetails.this, "Enter Your Special Offer", Toast.LENGTH_SHORT).show();
                    return;
                }
                editSpecailOffer(offer_sp_id, offer);
            }
        });
    }

    private void editSpecailOffer(String offer_sp_id, String offer) {

        try {

            JSONObject object = new JSONObject();
            object.put("special_offer_id", offer_sp_id);
            object.put("offer_description", offer);
            new MethodResquest(this, this, Constants.PATH + "edit_special_offers", object.toString(), 103);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDeleteDiloag(final String _id) {

        mDialog = new Dialog(this, R.style.Theme_MaterialComponents_BottomSheetDialog);
        mDialog.setContentView(R.layout.custom_alert_show);
        mDialog.show();
        ((TextView)mDialog.findViewById(R.id.text_message)).setText("Are you Sure You Want To Delete Special Offer?");
        mDialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
        mDialog.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getDeletesp(_id);
                mDialog.dismiss();
            }
        });
    }
    private void getInactiveDiloag(final String store_id, final String status) {

        if (status.equalsIgnoreCase("0")) {
            message = "Are you Sure You Want To Inactivate Special Offer?";
        } else {
            message = "Are you Sure You Want To Activate Special Offer?";
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
    private void getinactiveStore(String _id, String k_status) {

        String myStatus;
        try {
            if (k_status.equalsIgnoreCase("0")) {
                myStatus = "1";
            } else {
                myStatus = "0";
            }
            JSONObject object = new JSONObject();
            object.put("special_offer_id", _id);
            object.put("status", myStatus);
            new MethodResquest(this, this, Constants.PATH + "set_special_offers_status", object.toString(), 102);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void getDeletesp(String offer_sp_id) {

        try {
            JSONObject object = new JSONObject();
            object.put("special_offer_id", offer_sp_id);
            new MethodResquest(this, this, Constants.PATH + "delete_special_offers", object.toString(), 101);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getSpecialOffers() {
        try{
            String session_data = AppPreferences.getInstance(this).getFromStore("userData");
            JSONArray ayArray = new JSONArray(session_data);
            JSONObject object = new JSONObject();
            object.put("customer_id", ayArray.getJSONObject(0).optString("customer_id"));
            object.put("store_id", getIntent().getStringExtra("store_id"));
            new MethodResquest(this, this, Constants.PATH + "view_special_offers", object.toString(), 100);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.bacl_btn:
                overridePendingTransition(R.anim.fade_out_anim,R.anim.fade_in_anim);
                finish();
                break;
        }

    }

    @Override
    public void onRowClicked(List<OfferModel> matchesList, int pos) {
        
    }

    @Override
    public void requestStarted() {

    }

    @Override
    public void requestCompleted(JSONObject response, int requestType) {
        try {
            switch (requestType) {
                case 100:
                    spList = new ArrayList<>();
                    JSONObject object = new JSONObject(response.toString());
                    if (object.optString("statuscode").equalsIgnoreCase("200")) {
                        JSONArray jsonarray2 = object.getJSONArray("special_offer_details");
                        if (jsonarray2.length() > 0) {
                            for (int i = 0; i < jsonarray2.length(); i++) {
                                JSONObject jsonobject = jsonarray2.getJSONObject(i);
                                OfferModel storeListModel = new OfferModel();
                                storeListModel.setOffer_description(jsonobject.optString("offer_description"));
                                storeListModel.setOffer_sp_id(jsonobject.optString("special_offer_id"));
                                storeListModel.setStatus(jsonobject.optString("status"));
                                spList.add(storeListModel);
                            }
                            specialOfferAdapter = new SpecialOfferAdapter(spList, this, this);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                            sp_offer_vv.setLayoutManager(linearLayoutManager);
                            sp_offer_vv.setAdapter(specialOfferAdapter);
                        }
                    }
                    break;
                case 101:
                    JSONObject deletObj = new JSONObject(response.toString());
                    if (deletObj.optString("statuscode").equalsIgnoreCase("200")) {
                        getSpecialOffers();
                        Toast.makeText(this, deletObj.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, deletObj.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 102:
                    JSONObject object1 = new JSONObject(response.toString());
                    if (object1.optString("statuscode").equalsIgnoreCase("200")) {
                        getSpecialOffers();
                        Toast.makeText(this, object1.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, object1.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 103:
                    JSONObject object2 = new JSONObject(response.toString());
                    if (object2.optString("statuscode").equalsIgnoreCase("200")) {
                        editDiloag.dismiss();
                       getSpecialOffers();
                        Toast.makeText(this, object2.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, object2.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void requestEndedWithError(String error, int errorcode) {

    }
}
