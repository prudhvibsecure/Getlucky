package com.bsecure.getlucky;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class ProfilePage extends AppCompatActivity implements View.OnClickListener, RequestHandler, IFileUploadCallback {

    private String session_data = null,user_img;
    private EditText name, dob, gender, location;
    private Uri mImageUri;
    private ImageView userImage;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        try {
            userImage = findViewById(R.id.pf_image);
            session_data = AppPreferences.getInstance(this).getFromStore("userData");
            Log.e("data", session_data);
            JSONArray ayArray = new JSONArray(session_data);
            name = findViewById(R.id.name);
            name.setText(ayArray.getJSONObject(0).optString("name"));
            dob = findViewById(R.id.dateofbirth);
            dob.setText(ayArray.getJSONObject(0).optString("date_of_birth"));
            gender = findViewById(R.id.gender);
            gender.setOnClickListener(this);
            gender.setText(ayArray.getJSONObject(0).optString("gender"));
            location = findViewById(R.id.location);
            location.setText(ayArray.getJSONObject(0).optString("area") + "," + ayArray.getJSONObject(0).optString("city") + "," + ayArray.getJSONObject(0).optString("state") + "," + ayArray.getJSONObject(0).optString("country") + "," + ayArray.getJSONObject(0).optString("pin_code"));
            findViewById(R.id.pf_image).setOnClickListener(this);
            findViewById(R.id.submit_p).setOnClickListener(this);

        } catch (Exception e) {
            e.printStackTrace();
        }

        findViewById(R.id.bacl_btn).setOnClickListener(this);
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
        }

    }

    private void updateProfile() {
        try{

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void requestStarted() {

    }

    @Override
    public void requestCompleted(JSONObject response, int requestType) {

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
            String url = Constants.PATH + "assets/upload/avatar";
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
                    user_img=object.optString("attachname");
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
