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
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class AddStore extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, RequestHandler, IFileUploadCallback {
    private GoogleApiClient mGoogleApiClient;
    private AddressResultReceiver mResultReceiver;
    String mAddressOutput;
    Location mLastLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private String pin_code, area, city, country, phone, poaste_img, state;
    private EditText et_storenm, et_location, et_keywords, et_mobile, et_cat;
    int AUTOCOMPLETE_REQUEST_CODE = 1;
    private ImageView poster, i_keys, i_category;
    private AutoCompleteTextView autoCompleteView;
    private Uri mImageUri;
    private ArrayList<String> list = null;
    private String cat_lstwords, ids = "";
    private String status = "1";
    private ContactsCompletionView cust_key;
    private ArrayList<String> custom_keys_list = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_store);
        findViewById(R.id.bacl_btn).setOnClickListener(this);
        findViewById(R.id.submit).setOnClickListener(this);
        autoCompleteView = findViewById(R.id.location1);
        et_location = findViewById(R.id.location);
        et_location.setOnClickListener(this);
        findViewById(R.id.banner).setOnClickListener(this);
        poster = findViewById(R.id.post_image);
        et_storenm = findViewById(R.id.st_name);
        et_mobile = findViewById(R.id.mobile_no);
        et_cat = findViewById(R.id.st_category);
        cust_key = findViewById(R.id.cust_key);
        i_keys = findViewById(R.id.i_keys);
        i_keys.setOnClickListener(this);

        i_category = findViewById(R.id.i_category);
        i_category.setOnClickListener(this);
        et_cat.setOnClickListener(this);
        et_keywords = findViewById(R.id.st_keywords);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), Constants.key);
        }
        mResultReceiver = new AddressResultReceiver(new Handler());
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {
                            ACCESS_FINE_LOCATION,
                            ACCESS_COARSE_LOCATION

                    }, 7);
            return;
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        mLastLocation = location;

                        // In some rare cases the location returned can be null
                        if (mLastLocation == null) {
                            return;
                        }

                        if (!Geocoder.isPresent()) {
                            Toast.makeText(AddStore.this,
                                    R.string.no_geocoder_available,
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Start service and update UI to reflect new location
                        startIntentService();
                        // updateUI();
                    }
                });

        autoCompleteView.setThreshold(1);
        autoCompleteView.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                getLocationData(s.toString());
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
        cust_key.setThreshold(1);
        Places.initialize(getApplicationContext(), "AIzaSyCvdgdoCZc4bkufNsTKmaKGRw3egMIn_cs");

    }

    private void getLocationData(String toString) {
        String url = Constants.g_location_two;
        MethodResquest_GET resquest_get = new MethodResquest_GET(this, this, url, 101);
        resquest_get.dismissProgress(this);
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
            case R.id.submit:
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
            case R.id.st_category:
//                try {
////                    String category = AppPreferences.getInstance(this).getFromStore("category");
////                    JSONArray catarry = new JSONArray(category);
////                    ArrayList<String> list_data = new ArrayList<>();
////                    if (catarry.length() > 0) {
////                        for (int f = 0; f < catarry.length(); f++) {
////                            JSONObject oob = catarry.getJSONObject(f);
////                            list_data.add(oob.optString("category_name"));
////                        }
////                       // openListDialogView((TextView) view, "Select Category", list_data, this);
////                    }
////                } catch (Exception e) {
////                    e.printStackTrace();
////                }
                break;
            case R.id.banner:
                CropImage.activity()
                        .setAspectRatio(1, 1)
                        .start(AddStore.this);
                break;
            case R.id.i_keys:

                if (et_cat.getText().toString().length() == 0) {
                    Toast.makeText(this, "Please Choose Category", Toast.LENGTH_SHORT).show();
                } else {
                    Intent search_keys = new Intent(this, AddStoreKeysSearch.class);
                    search_keys.putExtra("key_selected_keys","");
                    search_keys.putExtra("key_selected_keys_tx", et_keywords.getText().toString());
                    startActivityForResult(search_keys, 200);
                }
                break;
            case R.id.i_category:
                Intent cat_keys = new Intent(this, AddCategoryKeysSearch.class);
                cat_keys.putExtra("cat_selected_keys", "");
                cat_keys.putExtra("cat_selected_keys_tx", ids);
                cat_keys.putExtra("cat_selected_keys_tx1", et_cat.getText().toString());
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
//        if (keyword_cust.length() == 0) {
//            Toast.makeText(this, "Please Fill Required Fields", Toast.LENGTH_SHORT).show();
//            return;
//        }
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
              new MethodResquest(this, this, Constants.PATH + "add_store", object.toString(), 100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void startIntentService() {
        Intent intent = new Intent(this, AddressService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        startService(intent);
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

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultData == null) {
                return;
            }

            // Display the address string
            // or an error message sent from the intent service.
            area = resultData.getString("area");
            city = resultData.getString("city");
            pin_code = resultData.getString("postalcode");
            country = resultData.getString("country");
            state = resultData.getString("state");

            if (area == null) {
                area = "unknown area";
            }
            et_location.setText(area + "," + city + "," + pin_code + "," + state + "," + country);
            //((TextView) findViewById(R.id.user_location_add)).setText(mAddressOutput);
            // displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT_1) {
                //  showToast(getString(R.string.address_found));
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
                    et_keywords.setText(/*kes + "," + */ket_text);
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
                        AppPreferences.getInstance(this).addToStore("customer_number",myObj.optString("customer_number"),true);
                        this.finish();
                    }
                    break;
                case 101:
                    JSONObject jsonOb = new JSONObject(response.toString());
                    JSONArray array = jsonOb.getJSONArray("predictions");
                    if (array != null && array.length() > 0) {
                        list = new ArrayList<String>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jObject = array.getJSONObject(i);
                            String description = jObject.getString("description");
                            String place_id = jObject.getString("place_id");
                            String reference = jObject.getString("reference");
                            list.add(description);

                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                                android.R.layout.select_dialog_item, list);
                        autoCompleteView.setThreshold(1);
                        autoCompleteView.setAdapter(adapter);
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
//    public  void openListDialogView(final TextView tv_location, String tittle,
//                                    final ArrayList<String> list_data, Context context) {
//
//        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
//        builderSingle.setTitle(tittle);
//        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, list_data);
//        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                String strName = arrayAdapter.getItem(which);
//                tv_location.setText(strName);
//                getStoreData(et_cat.getText().toString());
//
//            }
//        });
//        builderSingle.show();
//    }

}
