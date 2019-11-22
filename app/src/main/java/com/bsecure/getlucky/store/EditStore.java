package com.bsecure.getlucky.store;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bsecure.getlucky.R;
import com.bsecure.getlucky.bubble.ContactsCompletionView;
import com.bsecure.getlucky.common.AppPreferences;
import com.bsecure.getlucky.interfaces.IFileUploadCallback;
import com.bsecure.getlucky.interfaces.RequestHandler;
import com.bsecure.getlucky.models.ChipsItem;
import com.bsecure.getlucky.models.KeyWords;
import com.bsecure.getlucky.services.AddressService;
import com.bsecure.getlucky.volleyhttp.AttachmentUpload;
import com.bsecure.getlucky.volleyhttp.Constants;
import com.bsecure.getlucky.volleyhttp.MethodResquest;
import com.bsecure.getlucky.volleyhttp.MethodResquest_GET;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class EditStore extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, RequestHandler, IFileUploadCallback {
    private String pin_code, area, city, country, phone, poaste_img, state;
    private EditText et_storenm, et_location, et_keywords, et_mobile, et_cat;
    int AUTOCOMPLETE_REQUEST_CODE = 1;
    private ImageView poster, i_keys, i_category;
    private AutoCompleteTextView autoCompleteView;
    private Uri mImageUri;
    private ArrayList<String> list = null;
    private String cat_lstwords,ids="",my_cat_array="",my_key_array="",m_select_list="", store_id;
    private ContactsCompletionView cust_key;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_store);
        findViewById(R.id.bacl_btn).setOnClickListener(this);
        findViewById(R.id.submit).setVisibility(View.GONE);
        findViewById(R.id.submit_edit).setVisibility(View.VISIBLE);
        findViewById(R.id.submit_edit).setOnClickListener(this);
        autoCompleteView = findViewById(R.id.location1);
        et_location = findViewById(R.id.location);
        et_location.setOnClickListener(this);
        findViewById(R.id.banner).setOnClickListener(this);
        poster = findViewById(R.id.post_image);
        et_storenm = findViewById(R.id.st_name);
        et_mobile = findViewById(R.id.mobile_no);
        et_cat = findViewById(R.id.st_category);
        i_keys = findViewById(R.id.i_keys);
        i_keys.setOnClickListener(this);
        cust_key = findViewById(R.id.cust_key);
        i_category = findViewById(R.id.i_category);
        i_category.setOnClickListener(this);
        et_cat.setOnClickListener(this);
        et_keywords = findViewById(R.id.st_keywords);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), Constants.key);
        }
        autoCompleteView.setThreshold(1);
        autoCompleteView.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });
        Places.initialize(getApplicationContext(), "AIzaSyCvdgdoCZc4bkufNsTKmaKGRw3egMIn_cs");

        Intent storeDataEdit = getIntent();
        if (storeDataEdit != null) {
            store_id= storeDataEdit.getStringExtra("store_id");
            et_storenm.setText(storeDataEdit.getStringExtra("store_name"));
            et_mobile.setText(storeDataEdit.getStringExtra("store_phone_number"));
            et_cat.setText(storeDataEdit.getStringExtra("categories"));
            et_keywords.setText(storeDataEdit.getStringExtra("keywords"));
            cust_key.handleDone();
            cust_key.setText(storeDataEdit.getStringExtra("custom_keywords"));
            poaste_img= storeDataEdit.getStringExtra("store_image");
            if (poaste_img.length()>0){
                poaste_img="";
            }
            et_location.setText(storeDataEdit.getStringExtra("area")+","+storeDataEdit.getStringExtra("city")+","+storeDataEdit.getStringExtra("pin_code")+","+storeDataEdit.getStringExtra("state")+","+storeDataEdit.getStringExtra("country"));
            String path = Constants.PATH + "assets/upload/avatar/" + storeDataEdit.getStringExtra("store_image");
            Glide.with(this).load(path).into(poster);

             my_cat_array=storeDataEdit.getStringExtra("categories_array");
             my_key_array=storeDataEdit.getStringExtra("keywords_array");

            try {
                if (my_cat_array.length()!=0) {
                    JSONArray catarry = new JSONArray(my_cat_array);
                    if (catarry.length() > 0) {
                        for (int k = 0; k < catarry.length(); k++) {
                            JSONObject oob = catarry.getJSONObject(k);
                            m_select_list = m_select_list + "," + oob.optString("category_id");
                        }
                        ids = m_select_list.replaceFirst(",", "");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
             area=storeDataEdit.getStringExtra("area");
             city=storeDataEdit.getStringExtra("city");
             pin_code=storeDataEdit.getStringExtra("pin_code");
            state=storeDataEdit.getStringExtra("state");
            country=storeDataEdit.getStringExtra("country");
        }


    }


    private void getStoreData(String text) {
        try {

            JSONObject object = new JSONObject();
            object.put("category_ids", text);
            MethodResquest ms = new MethodResquest(this, this, Constants.PATH + "get_keywords", object.toString(), 102);
            ms.dismissProgress(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bacl_btn:
                finish();
                break;
            case R.id.submit_edit:
                addStoreReq();
                break;
            case R.id.location:
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.FULLSCREEN, fields)
                        .setTypeFilter(TypeFilter.ADDRESS)
                        .setCountry("IN")
                        .build(this);
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
                break;
            case R.id.banner:
                CropImage.activity()
                        .setAspectRatio(1, 1)
                        .start(EditStore.this);
                break;
            case R.id.i_keys:

                if (et_cat.getText().toString().length() == 0) {
                    Toast.makeText(this, "Please Choose Category", Toast.LENGTH_SHORT).show();
                } else {
                    Intent search_keys = new Intent(this, AddStoreKeysSearch.class);
                    search_keys.putExtra("key_selected_keys", my_key_array);
                    startActivityForResult(search_keys, 200);
                }
                break;
            case R.id.i_category:
                Intent cat_keys = new Intent(this, AddCategoryKeysSearch.class);
                cat_keys.putExtra("cat_selected_keys", my_cat_array);
                startActivityForResult(cat_keys, 201);
                break;
        }

    }

    private void addStoreReq() {

        String st_name = et_storenm.getText().toString().trim();
        if (st_name.length() == 0) {
            Toast.makeText(this, "Please Fill Required Fields", Toast.LENGTH_SHORT).show();
            return;
        }
        String st_cat = et_cat.getText().toString().trim();
        if (st_cat.length() == 0) {
            Toast.makeText(this, "Please Fill Required Fields", Toast.LENGTH_SHORT).show();
            return;
        }
        String keyword = et_keywords.getText().toString().trim();
        if (keyword.length() == 0) {
            Toast.makeText(this, "Please Fill Required Fields", Toast.LENGTH_SHORT).show();
            return;
        }
        cust_key.handleDone();
        String keyword_cust = cust_key.getObjects().toString();
        if (keyword_cust.startsWith("[") & keyword_cust.endsWith("]")) {
            keyword_cust = keyword_cust.replace("[", "");
            keyword_cust = keyword_cust.replace("]", "");
            keyword_cust.replaceAll(", ",",");
        }
        String location = et_location.getText().toString().trim();
        if (location.length() == 0) {
            Toast.makeText(this, "Please Fill Required Fields", Toast.LENGTH_SHORT).show();
            return;
        }
        String mobile = et_mobile.getText().toString().trim();
        if (mobile.length() == 0) {
            Toast.makeText(this, "Please Fill Required Fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mobile.length() < 10) {
            Toast.makeText(this, "Mobile Number Must Be 10 Digits", Toast.LENGTH_SHORT).show();
            return;
        }
        if (poaste_img == null) {
            Toast.makeText(this, "Please Select Store Image", Toast.LENGTH_SHORT).show();
            return;
        }
        String session_data = AppPreferences.getInstance(this).getFromStore("userData");
        Log.e("data", session_data);
        try {


            JSONArray ayArray = new JSONArray(session_data);
            // customer_id,store_name,area,city,state,country,pin_code,store_phone_number,store_image,categories,keywords
            JSONObject object = new JSONObject();
            object.put("store_id",  store_id);
            object.put("customer_id", ayArray.getJSONObject(0).optString("customer_id"));
            object.put("store_name", st_name);
            if (area == null)
                area = "unknown road";
            object.put("area", area);
            object.put("city", city);
            object.put("state", state);
            object.put("country", country);
            object.put("pin_code", pin_code);
            object.put("store_phone_number", mobile);
            object.put("categories", st_cat);
            object.put("categories_id", ids);
            object.put("custom_keywords", keyword_cust);
            object.put("keywords", keyword);
            object.put("store_image", poaste_img);
            new MethodResquest(this, this, Constants.PATH + "edit_store", object.toString(), 100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionRcesult) {

    }

    @Override
    public void onStateChange(int what, int arg1, int arg2, Object obj, int reqID) {
        try {

            switch (what) {

                case -1: // failed
                    Toast.makeText(this, "Failed To Send", Toast.LENGTH_SHORT).show();

                    break;

                case 1: // progressBar

                    break;

                case 0: // success
                    JSONObject object = new JSONObject(obj.toString());
                    //     {"status":"0","status_description":"File Uploaded Successfully","attachname":"1552318451_Screenshot_20181203-194010_20190311_090349.png"}
//                    if (mime_type == null) {
                    //if (reply_Id.isEmpty()) {
                    poaste_img = object.optString("attachname");
                    //sendImage();
                    break;

                default:
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Toast.makeText(this, "Place: " + place.getName() + ", " + place.getId(), Toast.LENGTH_SHORT).show();
                Log.e("Location", "Place: " + place.getName() + ", " + place.getId());
                et_location.getText().clear();
                et_location.setText(place.getAddress());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);

                Toast.makeText(this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                Log.i("Location", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();
            poster.setImageURI(mImageUri);
            uploadImage(mImageUri);
        }
        if (requestCode == 200) {
            if (resultCode == RESULT_OK) {
                String ket_text = data.getStringExtra("keys_data");
                String kes = et_keywords.getText().toString();
                if (kes.length() == 0) {
                    et_keywords.setText(ket_text);
                } else {
                    et_keywords.setText(kes + "," + ket_text);
                }

            }
        }
        if (requestCode == 201) {
            if (resultCode == RESULT_OK) {
                cat_lstwords = data.getStringExtra("keys_data");
                ids = data.getStringExtra("keys_ids");
                et_cat.setText(cat_lstwords);
                getStoreData(ids);
            }
        }
    }

    private void uploadImage(Uri mImageUri) {
        try {

            File myfilename = new File(mImageUri.getPath());
            String displayName = myfilename.getName();
            String url = Constants.PATH + "upload_photo";
            AttachmentUpload uploader = new AttachmentUpload(this, this);
            uploader.setFileName(displayName, displayName);
            uploader.userRequest("", 11, url, mImageUri.getPath());
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
                    JSONObject myObj = new JSONObject(response.toString());
                    if (myObj.optString("statuscode").equalsIgnoreCase("200")) {
                        Toast.makeText(this, myObj.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                        sendBroadcast(new Intent("com.store_refrsh"));
                        this.finish();
                    }
                    break;
                case 102:

                    JSONObject object = new JSONObject(response.toString());
                    if (object.optString("statuscode").equalsIgnoreCase("200")) {
                        JSONArray array1 = object.getJSONArray("keywords_ios");
                        AppPreferences.getInstance(this).addToStore("keywords", array1.toString(), true);
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

}
