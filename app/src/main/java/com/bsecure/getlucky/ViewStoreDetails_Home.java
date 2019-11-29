package com.bsecure.getlucky;

import android.app.Dialog;
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
import com.bsecure.getlucky.volleyhttp.Constants;
import com.bsecure.getlucky.volleyhttp.MethodResquest;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewStoreDetails_Home extends AppCompatActivity implements View.OnClickListener, RequestHandler, SpecialOfferAdapter.SpecialOfferListListener {

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
        setContentView(R.layout.view_store_details_home);

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

           // getSpecialOffers();


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
