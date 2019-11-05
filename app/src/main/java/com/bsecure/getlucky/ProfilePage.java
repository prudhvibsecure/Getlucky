package com.bsecure.getlucky;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bsecure.getlucky.common.AppPreferences;
import com.bsecure.getlucky.interfaces.IFileUploadCallback;
import com.bsecure.getlucky.interfaces.RequestHandler;
import com.bsecure.getlucky.store.AddEditStore;
import com.bsecure.getlucky.utils.Utils;
import com.bsecure.getlucky.volleyhttp.AttachmentUpload;
import com.bsecure.getlucky.volleyhttp.Constants;
import com.bsecure.getlucky.volleyhttp.MethodResquest;
import com.bsecure.getlucky.volleyhttp.MethodResquest_GET;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ProfilePage extends AppCompatActivity implements View.OnClickListener, RequestHandler, IFileUploadCallback {

    private String session_data = null, user_img;
    private EditText name, dob, gender;
    private AutoCompleteTextView  location;
    private Uri mImageUri;
    private ImageView userImage;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;
    private ArrayList<String> list = null;

    String place_id;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        try {
            dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            userImage = findViewById(R.id.pf_image);
            session_data = AppPreferences.getInstance(this).getFromStore("userData");
            Log.e("data", session_data);
            JSONArray ayArray = new JSONArray(session_data);
            name = findViewById(R.id.name);
            name.setText(ayArray.getJSONObject(0).optString("name"));
            dob = findViewById(R.id.dateofbirth);
            dob.setOnClickListener(this);
            dob.setText(ayArray.getJSONObject(0).optString("date_of_birth"));
            gender = findViewById(R.id.gender);
            gender.setOnClickListener(this);
            gender.setText(ayArray.getJSONObject(0).optString("gender"));
            location = findViewById(R.id.location);
            location.setThreshold(1);
            location.setText(ayArray.getJSONObject(0).optString("area") + "," + ayArray.getJSONObject(0).optString("city") + "," + ayArray.getJSONObject(0).optString("state") + "," + ayArray.getJSONObject(0).optString("country") + "," + ayArray.getJSONObject(0).optString("pin_code"));
            location.addTextChangedListener(new TextWatcher() {

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
            findViewById(R.id.pf_image).setOnClickListener(this);
            findViewById(R.id.submit_p).setOnClickListener(this);
            Glide.with(this).load(ayArray.getJSONObject(0).optString("profile_image")).into(userImage);

        } catch (Exception e) {
            e.printStackTrace();
        }

        findViewById(R.id.bacl_btn).setOnClickListener(this);
    }
    private void getLocationData(String place) {

        try {

            String url =Constants.g_location;
            url = url.replace("(INPUT)", place);
            url = url.replace("(SENSOR)", "false");
            url = url.replace("(TYPE)", "geocode");
            url = url.replace("(KEY)", getString(R.string.my_key));

            MethodResquest_GET resquest_get=new MethodResquest_GET(this,this,url,101);
            resquest_get.dismissProgress(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pf_image:
                CropImage.activity()
                        .setAspectRatio(1, 1)
                        .start(ProfilePage.this);
                break;
            case R.id.bacl_btn:
                finish();
                break;
            case R.id.gender:

                ArrayList<String> gender_sp = new ArrayList<>();
                gender_sp.add("Male");
                gender_sp.add("Female");
                gender_sp.add("Other");
                Utils.openListDialogView((TextView) view, "Select Gender", gender_sp, this);
                break;
            case R.id.submit_p:
                updateProfile();
                break;
            case R.id.dateofbirth:
                showDate();
                break;
        }

    }

    private void showDate() {

        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                dob.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void updateProfile() {
        try {
            String session_data = AppPreferences.getInstance(this).getFromStore("userData");
            JSONArray ayArray = new JSONArray(session_data);
            // customer_id,name,area,city,state,country,pin_code,date_of_birth,gender,profile_image

            String u_name = name.getText().toString();
            if (u_name.length() == 0) {
                Toast.makeText(this, "Please Enter Name", Toast.LENGTH_SHORT).show();
                return;
            }
            String dobs = dob.getText().toString();
            if (dobs.length() == 0) {
                Toast.makeText(this, "Please Select Date Of Birth", Toast.LENGTH_SHORT).show();
                return;
            }String genders = gender.getText().toString();
            if (genders.length() == 0) {
                Toast.makeText(this, "Please Select Gender", Toast.LENGTH_SHORT).show();
                return;
            }
            JSONObject object = new JSONObject();

            object.put("customer_id", ayArray.getJSONObject(0).optString("customer_id"));
            object.put("name", u_name);
            object.put("area", ayArray.getJSONObject(0).optString("area"));
            object.put("city", ayArray.getJSONObject(0).optString("city"));
            object.put("state", ayArray.getJSONObject(0).optString("state"));
            object.put("country", ayArray.getJSONObject(0).optString("country"));
            object.put("pin_code", ayArray.getJSONObject(0).optString("pin_code"));
            object.put("date_of_birth", dobs);
            object.put("gender", genders);
            object.put("profile_image", user_img);
            new MethodResquest(this, this, Constants.PATH + "update_profile", object.toString(), 100);
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
                        JSONArray array = myObj.optJSONArray("customer_details");
                        AppPreferences.getInstance(this).addToStore("userData",array.toString(),true);
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
                            place_id = jObject.getString("place_id");
                            String reference = jObject.getString("reference");
                            list.add(description);

                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                                android.R.layout.simple_expandable_list_item_1, list);
                        location.setThreshold(1);
                        location.setAdapter(adapter);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
//        if (requestCode == 1001 && resultCode == RESULT_OK) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mImageUri = result.getUri();
            userImage.setImageURI(mImageUri);
            uploadImage(mImageUri);
        } else {
            Toast.makeText(this, "Something gone wrong!", Toast.LENGTH_SHORT).show();
            this.finish();
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
                    user_img = object.optString("attachname");
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
}
