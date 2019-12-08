package com.bsecure.getlucky;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bsecure.getlucky.adpters.OfferAdapter;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ViewStoreDetails extends AppCompatActivity implements View.OnClickListener, RequestHandler, SpecialOfferAdapter.SpecialOfferListListener, OfferAdapter.OfferListListener {

    ImageView store_img;
    TextView tv_store_name, store_address, tv_spoffer, tv_offers;
    RecyclerView sp_offer_vv, offer_vv;
    private SpecialOfferAdapter specialOfferAdapter;
    private OfferAdapter offerAdapter;
    private List<OfferModel> spList, offerModelList;
    private String text_stats, message, msg;
    private Dialog InactiveDiloag, mDialog, editDiloag, nmDiloag;
    double temp_percent = 0, refer_percent = 0, store_refer_percent = 0, admin_percent = 0, total_percent = 0, offer_percent = 0;
    private static DecimalFormat df = new DecimalFormat("0.00");
    double min = 0, max = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_store_details);

        store_img = findViewById(R.id.post_image);
        tv_store_name = findViewById(R.id.store_name);

        tv_spoffer = findViewById(R.id.tv_spoffer);
        tv_spoffer.setOnClickListener(this);
        tv_offers = findViewById(R.id.tv_offers);
        tv_offers.setOnClickListener(this);

        store_address = findViewById(R.id.store_address);
        sp_offer_vv = findViewById(R.id.sp_recyler);
        offer_vv = findViewById(R.id.offer_recyler);

        findViewById(R.id.bacl_btn).setOnClickListener(this);

        tv_store_name.setText(getIntent().getStringExtra("store_name"));
        store_address.setText(getIntent().getStringExtra("store_add"));
        if (!TextUtils.isEmpty(getIntent().getStringExtra("store_image"))) {
            Glide.with(this).load(Constants.PATH + "assets/upload/avatar/" + getIntent().getStringExtra("store_image")).into(store_img);
        }

        if (getIntent().getStringExtra("type").equalsIgnoreCase("1")) {
            getSpecialOffers();
        } else {
            tv_spoffer.setBackground(getResources().getDrawable(R.drawable.button_bg_cancel_gray));
            tv_offers.setBackground(getResources().getDrawable(R.drawable.button_bg_submit_blue));
            tv_spoffer.setTextColor(getResources().getColor(R.color.black));
            tv_offers.setTextColor(getResources().getColor(R.color.black));
            sp_offer_vv.setVisibility(View.GONE);
            offer_vv.setVisibility(View.VISIBLE);
            getOffers();
        }

        // Special offer swipping code

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
                                editOfferDialog(spList.get(pos).getOffer_sp_id(), spList.get(pos).getOffer_description());
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

        //offer swiping code
        RecyclerViewSwipeHelper swipeHelper1 = new RecyclerViewSwipeHelper(this, offer_vv) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons, final int mPos) {
                underlayButtons.add(new RecyclerViewSwipeHelper.UnderlayButton(
                        "Delete",
                        0,
                        Color.parseColor("#FF3C30"),
                        new RecyclerViewSwipeHelper.UnderlayButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                // TODO: onDelete
                                getDeleteDiloag_offer(offerModelList.get(pos).getOffer_id());
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
                                editOfferDialog_New(offerModelList, mPos);
                            }
                        }
                ));
                String sts = offerModelList.get(mPos).getStatus();
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
                                getInactiveDiloagOffer(offerModelList.get(pos).getOffer_id(), offerModelList.get(pos).getStatus());
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
        ((TextView) editDiloag.findViewById(R.id.tv_title)).setText("Edit Special Offer");
        ((TextView) editDiloag.findViewById(R.id.bt_add)).setText("Update Offer");
        ((EditText) editDiloag.findViewById(R.id.sp_offer)).setText(offer_description);
        editDiloag.findViewById(R.id.bt_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String offer = ((EditText) editDiloag.findViewById(R.id.sp_offer)).getText().toString();
                if (offer.length() == 0) {
                    Toast.makeText(ViewStoreDetails.this, "Please Fill Required Field", Toast.LENGTH_SHORT).show();
                    return;
                }
                editSpecailOffer(offer_sp_id, offer);
            }
        });
        editDiloag.findViewById(R.id.bt_preview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String offer = ((EditText) editDiloag.findViewById(R.id.sp_offer)).getText().toString();
                if (offer.length() == 0) {
                    Toast.makeText(ViewStoreDetails.this, "Please Fill Required Field", Toast.LENGTH_SHORT).show();
                    return;
                }
                previewOffer(offer);
            }
        });
    }

    private void editOfferDialog_New(final List<OfferModel> offerList, final int pos) {

        editDiloag = new Dialog(this, R.style.Theme_MaterialComponents_DialogWhenLarge);
        editDiloag.setContentView(R.layout.add_m_offer);
        editDiloag.show();
        editDiloag.findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editDiloag.dismiss();
            }
        });
        ((TextView) editDiloag.findViewById(R.id.tv_title1)).setText("Edit Offer");
        ((TextView) editDiloag.findViewById(R.id.bt_add)).setText("Update Offer");
        offer_percent= Double.parseDouble(offerList.get(pos).getOffer_percent());
        refer_percent=Double.parseDouble(offerList.get(pos).getRefer_percent());
        store_refer_percent= Double.parseDouble(offerList.get(pos).getStore_refer_percent());
        admin_percent=Double.parseDouble(offerList.get(pos).getAdmin_percent());
        total_percent= Double.parseDouble(offerList.get(pos).getTotal_percent());

        ((EditText) editDiloag.findViewById(R.id.add_co)).setText(offerList.get(pos).getOffer_percent());
        ((EditText) editDiloag.findViewById(R.id.add_cr)).setText(df.format(refer_percent));
        ((EditText) editDiloag.findViewById(R.id.add_sr)).setText(df.format(store_refer_percent));
        ((EditText) editDiloag.findViewById(R.id.add_admin)).setText(df.format(admin_percent));
        ((EditText) editDiloag.findViewById(R.id.add_to)).setText(df.format(total_percent));
        ((EditText) editDiloag.findViewById(R.id.add_min)).setText(offerList.get(pos).getMin_amount());
        if (TextUtils.isEmpty(offerList.get(pos).getMax_amount())||offerList.get(pos).getMax_amount().equalsIgnoreCase("0")) {
            ((EditText) editDiloag.findViewById(R.id.add_max)).setText("");
        }else{
            ((EditText) editDiloag.findViewById(R.id.add_max)).setText(offerList.get(pos).getMax_amount());
        }

        ((EditText) editDiloag.findViewById(R.id.add_co)).addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence offer, int start,
                                      int before, int count) {
                if (offer.length() != 0) {

                    offer_percent = Double.parseDouble(String.valueOf(offer));

                    temp_percent = offer_percent * 0.4;

                    refer_percent = temp_percent * 0.5;

                    store_refer_percent = temp_percent * 0.25;

                    admin_percent = temp_percent * 0.25;

                    total_percent = offer_percent + refer_percent + store_refer_percent + admin_percent;
                    ((EditText) editDiloag.findViewById(R.id.add_cr)).setText(df.format(refer_percent));
                    ((EditText) editDiloag.findViewById(R.id.add_sr)).setText(df.format(store_refer_percent));
                    ((EditText) editDiloag.findViewById(R.id.add_admin)).setText(df.format(admin_percent));
                    ((EditText) editDiloag.findViewById(R.id.add_to)).setText(df.format(total_percent));
                }
            }
        });
        editDiloag.findViewById(R.id.bt_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String offer = ((EditText) editDiloag.findViewById(R.id.add_co)).getText().toString().trim();
                if (offer.length() == 0 || offer.equalsIgnoreCase("0")) {
                    Toast.makeText(ViewStoreDetails.this, "Please Fill Required Field", Toast.LENGTH_SHORT).show();
                    return;
                }

                String add_min = ((EditText) editDiloag.findViewById(R.id.add_min)).getText().toString();
                if (add_min.length() == 0 || add_min.equalsIgnoreCase("0")||add_min.startsWith("0")) {
                    ((EditText) editDiloag.findViewById(R.id.add_min)).setText("1");
                    return;
                }
                String add_max = ((EditText) editDiloag.findViewById(R.id.add_max)).getText().toString();
                if (add_max.equalsIgnoreCase("∞")){
                    add_max="";
                }
                if (add_max.length() > 0) {
                    min = Double.parseDouble(add_min);
                    max = Double.parseDouble(add_max);
                    if (Double.compare(min, max) > 0) {
                        Toast.makeText(ViewStoreDetails.this, "Maximum Amount Must Be Greater Than Minimum Amount", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                editOffer(offerList.get(pos).getOffer_id(), add_min, add_max);
            }
        });
        editDiloag.findViewById(R.id.bt_preview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String offer = ((EditText) editDiloag.findViewById(R.id.add_co)).getText().toString().trim();
                if (offer.length() == 0 || offer.equalsIgnoreCase("0")) {
                    Toast.makeText(ViewStoreDetails.this, "Please Fill Required Field", Toast.LENGTH_SHORT).show();
                    return;
                }
                String offer_l = ((EditText) editDiloag.findViewById(R.id.add_to)).getText().toString();
                previewOffer(offer + "% Cashback On All Purchase");
            }
        });
    }

    private void previewOffer(String offer) {

        final Dialog previewDiloag = new Dialog(this, R.style.Theme_MaterialComponents_BottomSheetDialog);
        previewDiloag.setContentView(R.layout.preview_sp_offer);

        String store_name = getIntent().getStringExtra("store_name");
        String area = getIntent().getStringExtra("store_add1");
        ((TextView) previewDiloag.findViewById(R.id.tv_offer_1)).setVisibility(View.GONE);
        ((TextView) previewDiloag.findViewById(R.id.store_name_1)).setText(store_name);
        ((TextView) previewDiloag.findViewById(R.id.tv_offer_12)).setVisibility(View.VISIBLE);
        ((TextView) previewDiloag.findViewById(R.id.tv_address_1)).setText(area);

        ((TextView) previewDiloag.findViewById(R.id.tv_offer_12)).setText(offer);
        String image = getIntent().getStringExtra("store_image");
        ImageView iv_image = (ImageView) previewDiloag.findViewById(R.id.store_image_1);

        if (!TextUtils.isEmpty(image) && image != null) {
            Glide.with(this).load(Constants.PATH + "assets/upload/avatar/" + image).into(iv_image);
        }
        previewDiloag.findViewById(R.id.kk_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previewDiloag.dismiss();
            }
        });
        previewDiloag.show();
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

    private void editOffer(String offer_sp_id, String add_min, String add_max) {

        try {

            String session_data = AppPreferences.getInstance(this).getFromStore("userData");
            JSONArray ayArray = new JSONArray(session_data);


            JSONObject object = new JSONObject();
            object.put("offer_id", offer_sp_id);
            object.put("offer_percent", offer_percent);
            object.put("min_amount", add_min);
            object.put("max_amount", add_max);
            object.put("refer_percent", refer_percent);
            object.put("store_refer_percent", store_refer_percent);
            object.put("admin_percent", admin_percent);
            object.put("total_percent", total_percent);
            object.put("store_id", getIntent().getStringExtra("store_id"));
            object.put("customer_id", ayArray.getJSONObject(0).optString("customer_id"));
            new MethodResquest(this, this, Constants.PATH + "edit_offers", object.toString(), 107);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDeleteDiloag(final String _id) {

        mDialog = new Dialog(this, R.style.Theme_MaterialComponents_BottomSheetDialog);
        mDialog.setContentView(R.layout.custom_alert_show);
        mDialog.show();
        ((TextView) mDialog.findViewById(R.id.text_message)).setText("Are you Sure You Want To Delete Special Offer?");
        mDialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
        mDialog.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                getDeletesp(_id);

            }
        });
    }

    private void getDeleteDiloag_offer(final String _id) {

        mDialog = new Dialog(this, R.style.Theme_MaterialComponents_BottomSheetDialog);
        mDialog.setContentView(R.layout.custom_alert_show);
        mDialog.show();
        ((TextView) mDialog.findViewById(R.id.text_message)).setText("Are you Sure You Want To Delete Offer?");
        mDialog.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
        mDialog.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                getDeleteOffer(_id);

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

    private void getInactiveDiloagOffer(final String store_id, final String status) {

        if (status.equalsIgnoreCase("0")) {
            message = "Are you Sure You Want To Inactivate Offer?";
        } else {
            message = "Are you Sure You Want To Activate Offer?";
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

                getinactiveStoreOffer(store_id, status);
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

    private void getinactiveStoreOffer(String _id, String k_status) {

        String myStatus;
        try {
            if (k_status.equalsIgnoreCase("0")) {
                myStatus = "1";
            } else {
                myStatus = "0";
            }
            JSONObject object = new JSONObject();
            object.put("offer_id", _id);
            object.put("status", myStatus);
            new MethodResquest(this, this, Constants.PATH + "set_offers_status", object.toString(), 106);
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

    private void getDeleteOffer(String offer_sp_id) {

        try {
            JSONObject object = new JSONObject();
            object.put("offer_id", offer_sp_id);
            new MethodResquest(this, this, Constants.PATH + "delete_offers", object.toString(), 105);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getSpecialOffers() {
        try {
            String session_data = AppPreferences.getInstance(this).getFromStore("userData");
            JSONArray ayArray = new JSONArray(session_data);
            JSONObject object = new JSONObject();
            object.put("customer_id", ayArray.getJSONObject(0).optString("customer_id"));
            object.put("store_id", getIntent().getStringExtra("store_id"));
            new MethodResquest(this, this, Constants.PATH + "view_special_offers", object.toString(), 100);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getOffers() {
        try {
            String session_data = AppPreferences.getInstance(this).getFromStore("userData");
            JSONArray ayArray = new JSONArray(session_data);
            JSONObject object = new JSONObject();
            object.put("customer_id", ayArray.getJSONObject(0).optString("customer_id"));
            object.put("store_id", getIntent().getStringExtra("store_id"));
            new MethodResquest(this, this, Constants.PATH + "view_offers", object.toString(), 104);
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

            case R.id.tv_spoffer:
                tv_spoffer.setBackground(getResources().getDrawable(R.drawable.button_bg_submit_blue));
                tv_offers.setBackground(getResources().getDrawable(R.drawable.button_bg_cancel_gray));
                tv_spoffer.setTextColor(getResources().getColor(R.color.black));
                tv_offers.setTextColor(getResources().getColor(R.color.black));
                tv_offers.setBackground(getResources().getDrawable(R.drawable.button_bg_cancel_gray));
                sp_offer_vv.setVisibility(View.VISIBLE);
                offer_vv.setVisibility(View.GONE);
                getSpecialOffers();
                break;
            case R.id.tv_offers:
                tv_spoffer.setBackground(getResources().getDrawable(R.drawable.button_bg_cancel_gray));
                tv_offers.setBackground(getResources().getDrawable(R.drawable.button_bg_submit_blue));
                tv_spoffer.setTextColor(getResources().getColor(R.color.black));
                tv_offers.setTextColor(getResources().getColor(R.color.black));
                sp_offer_vv.setVisibility(View.GONE);
                offer_vv.setVisibility(View.VISIBLE);
                getOffers();
                break;
        }

    }

    @Override
    public void onRowClicked(List<OfferModel> matchesList, int pos) {

    }

    @Override
    public void onRowClickedPos(final List<OfferModel> matchesList, final int position, final RadioButton offer_select) {

//        nmDiloag = new Dialog(this, R.style.Theme_MaterialComponents_BottomSheetDialog);
//        nmDiloag.setContentView(R.layout.custom_alert_show);
//        message = "Selected Special offer will show on your Store.";
//        ((TextView) nmDiloag.findViewById(R.id.text_message)).setText(message);
//        nmDiloag.show();
//        nmDiloag.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                offer_select.setChecked(false);
//                nmDiloag.dismiss();
//            }
//        });
//        nmDiloag.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {

                getapperOffersc(matchesList.get(position).getOffer_sp_id());
//                nmDiloag.dismiss();
//            }
//        });


    }

    private void getapperOffersc(String special_offer_id) {
        try {

            JSONObject object = new JSONObject();
            object.put("special_offer_id", special_offer_id);
            new MethodResquest(this, this, Constants.PATH + "change_special_offers_default_status", object.toString(), 109);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRowClickedPos2(final List<OfferModel> matchesList, final int position, final RadioButton selct_rd) {

//        nmDiloag = new Dialog(this, R.style.Theme_MaterialComponents_BottomSheetDialog);
//        nmDiloag.setContentView(R.layout.custom_alert_show);
//        message = "Selected  offer will show on your Store.";
//        ((TextView) nmDiloag.findViewById(R.id.text_message)).setText(message);
//        nmDiloag.show();
//        nmDiloag.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                selct_rd.setChecked(false);
//                nmDiloag.dismiss();
//            }
//        });
//        nmDiloag.findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {

                getapperOffersc2(matchesList.get(position).getOffer_id());
//                nmDiloag.dismiss();
//            }
//        });


    }

    private void getapperOffersc2(String offer_id) {
        try {

            JSONObject object = new JSONObject();
            object.put("offer_id", offer_id);
            new MethodResquest(this, this, Constants.PATH + "change_offers_default_status", object.toString(), 108);
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
                case 100:
                    spList = new ArrayList<>();
                    sp_offer_vv.setVisibility(View.VISIBLE);
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
                                storeListModel.setDefault_status(jsonobject.optString("default_status"));
                                spList.add(storeListModel);
                            }
                            specialOfferAdapter = new SpecialOfferAdapter(spList, this, this,"2");
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                            sp_offer_vv.setLayoutManager(linearLayoutManager);
                            sp_offer_vv.setAdapter(specialOfferAdapter);
                        }
                    } else {
                        sp_offer_vv.removeAllViews();
                        sp_offer_vv.setVisibility(View.GONE);
                    }
                    break;
                case 101:
                    JSONObject deletObj = new JSONObject(response.toString());
                    if (deletObj.optString("statuscode").equalsIgnoreCase("200")) {
                        redirectClass("1");
                        Toast.makeText(this, deletObj.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, deletObj.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 102:
                    JSONObject object1 = new JSONObject(response.toString());
                    if (object1.optString("statuscode").equalsIgnoreCase("200")) {
                        redirectClass("1");
                        Toast.makeText(this, object1.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, object1.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 103:
                    JSONObject object2 = new JSONObject(response.toString());
                    if (object2.optString("statuscode").equalsIgnoreCase("200")) {
                        editDiloag.dismiss();
                        redirectClass("1");
                        Toast.makeText(this, object2.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, object2.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 104:
                    // store_name,offer_percent,min_amount,max_amount,refer_percent,store_refer_percent,admin_percent,total_percent]
                    offerModelList = new ArrayList<>();
                    offer_vv.setVisibility(View.VISIBLE);
                    JSONObject object21 = new JSONObject(response.toString());
                    if (object21.optString("statuscode").equalsIgnoreCase("200")) {
                        JSONArray jsonarray2 = object21.getJSONArray("offer_details");
                        if (jsonarray2.length() > 0) {
                            for (int i = 0; i < jsonarray2.length(); i++) {
                                JSONObject jsonobject = jsonarray2.getJSONObject(i);
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
                            offerAdapter = new OfferAdapter(offerModelList, this, this,"2");
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                            offer_vv.setLayoutManager(linearLayoutManager);
                            offer_vv.setAdapter(offerAdapter);
                        }
                    } else {
                        offer_vv.removeAllViews();
                        offer_vv.setVisibility(View.GONE);
                    }
                    break;

                case 105:
                    JSONObject deletObj1 = new JSONObject(response.toString());
                    if (deletObj1.optString("statuscode").equalsIgnoreCase("200")) {
                        redirectClass("2");

                        Toast.makeText(this, deletObj1.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, deletObj1.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 106:
                    JSONObject object11 = new JSONObject(response.toString());
                    if (object11.optString("statuscode").equalsIgnoreCase("200")) {
                        redirectClass("2");
                        Toast.makeText(this, object11.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, object11.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 107:
                    JSONObject object211 = new JSONObject(response.toString());
                    if (object211.optString("statuscode").equalsIgnoreCase("200")) {
                        editDiloag.dismiss();
                        redirectClass("2");
                        Toast.makeText(this, object211.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, object211.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    }
                    break;
                    case 108:
                    JSONObject g = new JSONObject(response.toString());
                    if (g.optString("statuscode").equalsIgnoreCase("200")) {
                        redirectClass("2");
                        Toast.makeText(this, g.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, g.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    }
                    break; case 109:
                    JSONObject g1 = new JSONObject(response.toString());
                    if (g1.optString("statuscode").equalsIgnoreCase("200")) {
                        redirectClass("1");
                        Toast.makeText(this, g1.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, g1.optString("statusdescription"), Toast.LENGTH_SHORT).show();
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

    public void redirectClass(String type) {
        Intent in = new Intent(this, ViewStoreDetails.class);
        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        in.putExtra("type", type);
        in.putExtra("store_id", getIntent().getStringExtra("store_id"));
        in.putExtra("store_name", getIntent().getStringExtra("store_name"));
        in.putExtra("store_add", getIntent().getStringExtra("store_add"));
        in.putExtra("store_add1", getIntent().getStringExtra("store_add1"));
        in.putExtra("store_image", getIntent().getStringExtra("store_image"));
        startActivity(in);
        overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
    }
}
