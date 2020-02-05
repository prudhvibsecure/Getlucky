package com.bsecure.getlucky;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bsecure.getlucky.adpters.OfferAdapter;
import com.bsecure.getlucky.adpters.SpecialOfferAdapter;
import com.bsecure.getlucky.common.AppPreferences;
import com.bsecure.getlucky.fragments.QRDialogFragment;
import com.bsecure.getlucky.interfaces.RequestHandler;
import com.bsecure.getlucky.models.OfferModel;
import com.bsecure.getlucky.volleyhttp.Constants;
import com.bsecure.getlucky.volleyhttp.MethodResquest;
import com.bumptech.glide.Glide;
import com.google.zxing.WriterException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

public class ViewStoreDetails_Home extends AppCompatActivity implements View.OnClickListener, RequestHandler, SpecialOfferAdapter.SpecialOfferListListener, OfferAdapter.OfferListListener {

    ImageView store_img, view_on_map, sqr;
    TextView tv_store_name, store_address, tv_spoffer, tv_offers, store_code;
    RecyclerView sp_offer_vv, offer_vv;
    private SpecialOfferAdapter specialOfferAdapter;
    private OfferAdapter offerAdapter;
    private List<OfferModel> spList, offerModelList;
    private String text_stats, message, msg;
    private Dialog InactiveDiloag, mDialog, editDiloag;
    String savePath = Environment.getExternalStorageDirectory().toString() + "/GetLucky/QRCode/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_store_details_home);

        sqr = findViewById(R.id.sqr);
        sqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString("image", savePath);
                String test = getIntent().getStringExtra("store_code");
                bundle.putString("code", getIntent().getStringExtra("store_code"));

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                QRDialogFragment newFragment = QRDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
            }
        });

        store_img = findViewById(R.id.post_image);
        tv_store_name = findViewById(R.id.store_name);
        store_address = findViewById(R.id.store_address);
        store_code = findViewById(R.id.store_code);

        tv_spoffer = findViewById(R.id.tv_spoffer);
        tv_spoffer.setOnClickListener(this);

        tv_offers = findViewById(R.id.tv_offers);
        tv_offers.setOnClickListener(this);

        //view_on_map = findViewById(R.id.view_on_map);
        //view_on_map.setOnClickListener(this);

        sp_offer_vv = findViewById(R.id.sp_recyler);
        offer_vv = findViewById(R.id.offer_recyler);

        findViewById(R.id.bacl_btn).setOnClickListener(this);

        tv_store_name.setText(getIntent().getStringExtra("store_name"));
        store_address.setText(getIntent().getStringExtra("store_add"));
        store_code.setText("Store Code:\t"+getIntent().getStringExtra("store_code"));
        if (!TextUtils.isEmpty(getIntent().getStringExtra("store_image"))) {
            Glide.with(this).load(Constants.PATH + "assets/upload/avatar/" + getIntent().getStringExtra("store_image")).into(store_img);
        }
        getStoreData();

        getCode();
    }

    private void getStoreData() {
        try {
            String session_data = AppPreferences.getInstance(this).getFromStore("userData");
            JSONArray ayArray = new JSONArray(session_data);
            JSONObject object = new JSONObject();
            // object.put("customer_id", ayArray.getJSONObject(0).optString("customer_id"));
            object.put("store_id", getIntent().getStringExtra("store_id"));
            new MethodResquest(this, this, Constants.PATH + "get_store", object.toString(), 100);
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

           /* case R.id.view_on_map:
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("google.navigation:q="+getIntent().getStringExtra("store_add")));
                startActivity(intent);
                break;*/
            case R.id.tv_spoffer:
                tv_spoffer.setBackground(getResources().getDrawable(R.drawable.button_bg_submit_blue));
                tv_offers.setBackground(getResources().getDrawable(R.drawable.button_bg_cancel_gray));
                tv_spoffer.setTextColor(getResources().getColor(R.color.black));
                tv_offers.setTextColor(getResources().getColor(R.color.black));
                tv_offers.setBackground(getResources().getDrawable(R.drawable.button_bg_cancel_gray));
                sp_offer_vv.setVisibility(View.VISIBLE);
                offer_vv.setVisibility(View.GONE);

                break;
            case R.id.tv_offers:
                tv_spoffer.setBackground(getResources().getDrawable(R.drawable.button_bg_cancel_gray));
                tv_offers.setBackground(getResources().getDrawable(R.drawable.button_bg_submit_blue));
                tv_spoffer.setTextColor(getResources().getColor(R.color.black));
                tv_offers.setTextColor(getResources().getColor(R.color.black));
                sp_offer_vv.setVisibility(View.GONE);
                offer_vv.setVisibility(View.VISIBLE);

                break;
        }

    }

    @Override
    public void onRowClicked(List<OfferModel> matchesList, int pos) {

    }

    @Override
    public void onRowClickedPos(List<OfferModel> matchesList, int position, RadioButton offer_select) {

    }

    @Override
    public void requestStarted() {

    }

    @Override
    public void requestCompleted(JSONObject response, int requestType) {
        try {
            switch (requestType) {
                case 100:
                    offerModelList = new ArrayList<>();
                    spList = new ArrayList<>();
                    JSONObject object = new JSONObject(response.toString());
                    if (object.optString("statuscode").equalsIgnoreCase("200")) {
                        JSONArray jsonarray2 = object.getJSONArray("store_details");
                        //JSONObject sobj = jsonarray2.getJSONObject(0);
                        store_code.setText("Store Code:\t" + jsonarray2.getJSONObject(0).optString("store_referral_code"));
                        JSONArray jsonarray3 = object.getJSONArray("special_offers_details");
                        if (jsonarray3.length() > 0) {
                            for (int i = 0; i < jsonarray3.length(); i++) {
                                JSONObject jsonobject = jsonarray3.getJSONObject(i);
                                OfferModel storeListModel = new OfferModel();
                                storeListModel.setOffer_description(jsonobject.optString("offer_description"));
                                storeListModel.setOffer_sp_id(jsonobject.optString("special_offer_id"));
                                storeListModel.setStatus(jsonobject.optString("status"));
                                storeListModel.setDefault_status(jsonobject.optString("default_status"));
                                spList.add(storeListModel);
                            }
                            specialOfferAdapter = new SpecialOfferAdapter(spList, this, this, "1");
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                            sp_offer_vv.setLayoutManager(linearLayoutManager);
                            sp_offer_vv.setAdapter(specialOfferAdapter);
                        } else {
                            sp_offer_vv.removeAllViews();
                            sp_offer_vv.setVisibility(View.GONE);
                        }
                        JSONArray jsonarray4 = object.getJSONArray("offer_details");
                        if (jsonarray4.length() > 0) {
                            for (int i = 0; i < jsonarray4.length(); i++) {
                                JSONObject jsonobject = jsonarray4.getJSONObject(i);
                                OfferModel storeListModel = new OfferModel();
                                storeListModel.setOffer_id(jsonobject.optString("offer_id"));
                                storeListModel.setOffer_percent_description(jsonobject.optString("offer_percent_description"));
                                storeListModel.setOffer_percent(jsonobject.optString("offer_percent"));
                                storeListModel.setOffer_sp_id(jsonobject.optString("min_amount"));
                                storeListModel.setStatus(jsonobject.optString("status"));
                                storeListModel.setMax_amount(jsonobject.optString("max_amount"));
                                storeListModel.setMin_amount(jsonobject.optString("min_amount"));
                                storeListModel.setRefer_percent(jsonobject.optString("refer_percent"));
                                storeListModel.setStore_refer_percent(jsonobject.optString("store_refer_percent"));
                                storeListModel.setAdmin_percent(jsonobject.optString("admin_percent"));
                                storeListModel.setTotal_percent(jsonobject.optString("total_percent"));
                                storeListModel.setDefault_status(jsonobject.optString("default_status"));
                                offerModelList.add(storeListModel);
                            }
                            offerAdapter = new OfferAdapter(offerModelList, this, this, "1");
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                            offer_vv.setLayoutManager(linearLayoutManager);
                            offer_vv.setAdapter(offerAdapter);
                        } else {
                            offer_vv.removeAllViews();
                            offer_vv.setVisibility(View.GONE);

                        }
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
    public void onRowClickedOffer(List<OfferModel> matchesList, int pos) {

    }

    @Override
    public void onRowClickedPos2(List<OfferModel> matchesList, int position, RadioButton selct_rd) {

    }

    private void getCode() {
        try {
            boolean save ;
            WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            int width = point.x;
            int height = point.y;
            int smallerDimension = width < height ? width : height;
            smallerDimension = smallerDimension * 3 / 4;
            String inputValue = getIntent().getStringExtra("store_code");
            QRGEncoder qrgEncoder = new QRGEncoder(
                    inputValue, null,
                    QRGContents.Type.TEXT,
                    smallerDimension);
            try {

                String result;
                Bitmap bitmap = qrgEncoder.encodeAsBitmap();
                sqr.setImageBitmap(bitmap);
                save = QRGSaver.save(savePath, inputValue, bitmap, QRGContents.ImageType.IMAGE_JPEG);
                result = save ? "Image Saved" : "Image Not Saved";
                // Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            } catch (WriterException e) {
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
