package com.bsecure.getlucky.wallet;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
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
import com.bsecure.getlucky.services.AddressService;
import com.bsecure.getlucky.store.AddCategoryKeysSearch;
import com.bsecure.getlucky.store.AddStoreKeysSearch;
import com.bsecure.getlucky.utils.Utils;
import com.bsecure.getlucky.volleyhttp.AttachmentUpload;
import com.bsecure.getlucky.volleyhttp.Constants;
import com.bsecure.getlucky.volleyhttp.MethodResquest;
import com.bsecure.getlucky.volleyhttp.MethodResquest_GET;
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
import com.google.android.material.textfield.TextInputEditText;
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

public class AddBankAccount extends AppCompatActivity implements View.OnClickListener, RequestHandler {

    private TextInputEditText et_acname, et_acnum, et_accnnum, et_ifsc, et_address;
    private String bank_name,ifse_id;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_bank);
        findViewById(R.id.bacl_btn).setOnClickListener(this);
        findViewById(R.id.submit_bank).setOnClickListener(this);
        et_acname = findViewById(R.id.tv_name);
        et_acnum = findViewById(R.id.tv_acno);
        et_acnum.setLongClickable(false);

        et_acnum.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });

        et_accnnum = findViewById(R.id.tv_accno);
        et_accnnum.setLongClickable(false);

        et_accnnum.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });

        et_ifsc = findViewById(R.id.tv_ifsc);
        et_ifsc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence text, int i, int i1, int i2) {

                if (text.length() > 0) {
                    if (text.length() == 11) {
                        serachIFSC(text.toString());
                    }
                }else{
                    findViewById(R.id.spy_et).setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        et_address = findViewById(R.id.tv_address);
        et_address.setVisibility(View.GONE);
    }

    private void serachIFSC(String ifsccode) {
        try {
            JSONObject object = new JSONObject();
            object.put("ifsc_code", ifsccode);
            new MethodResquest(this, this, Constants.PATH + "get_bank_address", object.toString(), 101);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bacl_btn:
                Utils.hideKeyboard(this);
                finish();
                break;
            case R.id.submit_bank:
                addbankReq();
                break;
        }

    }

    private void addbankReq() {

        String st_name = et_acname.getText().toString().trim();
        if (st_name.length() == 0) {
            Toast.makeText(this, "Please Fill Required Fields", Toast.LENGTH_SHORT).show();
            et_acname.requestFocus();
            return;
        }
        String et_acnumer = et_acnum.getText().toString().trim();
        if (et_acnumer.length() == 0) {
            Toast.makeText(this, "Please Fill Required Fields", Toast.LENGTH_SHORT).show();
            et_acnum.requestFocus();
            return;
        }
        String et_accnnume = et_accnnum.getText().toString().trim();
        if (et_accnnume.length() == 0) {
            Toast.makeText(this, "Please Fill Required Fields", Toast.LENGTH_SHORT).show();
            et_accnnum.requestFocus();
            return;
        }
        if (!et_acnumer.matches(et_accnnume)) {
            Toast.makeText(this, "Please Fill Required Fields", Toast.LENGTH_SHORT).show();
            et_accnnum.requestFocus();
            return;
        }
        String et_ifscs = et_ifsc.getText().toString().trim();
        if (et_ifscs.length() == 0) {
            Toast.makeText(this, "Please Fill Required Fields", Toast.LENGTH_SHORT).show();
            et_ifsc.requestFocus();
            return;
        }
        String address = et_address.getText().toString().trim();
        if (address.length() == 0) {
            Toast.makeText(this, "Please Fill Required Fields", Toast.LENGTH_SHORT).show();
            et_address.requestFocus();
            return;
        }
        String session_data = AppPreferences.getInstance(this).getFromStore("userData");
        try {
            JSONArray ayArray = new JSONArray(session_data);
            // customer_id,bank_acc_name,bank_address,bank_acc_no,ifsc_code
            JSONObject object = new JSONObject();
            object.put("customer_id", ayArray.getJSONObject(0).optString("customer_id"));
            object.put("bank_acc_name", st_name);
            object.put("bank_name", bank_name);
            object.put("bank_address", address);
            object.put("bank_acc_no", et_acnumer);
            object.put("bank_ifsc_code_id", ifse_id);
            new MethodResquest(this, this, Constants.PATH + "add_bank", object.toString(), 100);
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
            //  {"statuscode":"201","statusdescription":"No Bank Found - Please Check IFSC Code"}
//            {"statuscode":"200","bank_address":
//                [{"bank_ifsc_code_id":"1","bank_name":"ABU DHABI COMMERCIAL BANK","address":"75, REHMAT MANZIL, V. N. ROAD, CURCHGATE, MUMBAI - 400020"}]}
            switch (requestType) {
                case 100:
                    JSONObject myObj = new JSONObject(response.toString());
                    if (myObj.optString("statuscode").equalsIgnoreCase("200")) {
                        Toast.makeText(this, myObj.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                        sendBroadcast(new Intent("com.addbank_refrsh"));
                        startActivity(new Intent(this,ViewBankList.class));
                        //this.finish();
                    }else{
                        Toast.makeText(this, myObj.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 101:
                    JSONObject myObj1 = new JSONObject(response.toString());
                    if (myObj1.optString("statuscode").equalsIgnoreCase("200")) {
                        findViewById(R.id.spy_et).setVisibility(View.VISIBLE);
                        JSONArray arry = myObj1.getJSONArray("bank_address");
                        bank_name=arry.getJSONObject(0).optString("bank_name");
                        ifse_id=arry.getJSONObject(0).optString("bank_ifsc_code_id");
                        et_address.setVisibility(View.VISIBLE);
                        et_address.setEnabled(false);
                        et_address.setTextColor(Color.BLACK);
                        et_address.setText(arry.getJSONObject(0).optString("address"));
                    } else {
                        Toast.makeText(this, myObj1.optString("statusdescription"), Toast.LENGTH_SHORT).show();
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
