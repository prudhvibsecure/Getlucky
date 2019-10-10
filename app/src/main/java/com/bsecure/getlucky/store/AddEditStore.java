package com.bsecure.getlucky.store;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bsecure.getlucky.R;
import com.bsecure.getlucky.Register;
import com.bsecure.getlucky.services.AddressService;
import com.bsecure.getlucky.volleyhttp.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class AddEditStore extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private AddressResultReceiver mResultReceiver;
    String mAddressOutput;
    Location mLastLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private String pin_code, area, city, country, phone, otpone, state;
    private EditText et_storenm, et_location, et_keywords, et_mobile, et_cat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_store);
        findViewById(R.id.bacl_btn).setOnClickListener(this);
        findViewById(R.id.submit).setOnClickListener(this);
        et_location = findViewById(R.id.location);
        et_storenm = findViewById(R.id.st_name);
        et_cat = findViewById(R.id.st_category);
        et_keywords = findViewById(R.id.st_keywords);
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
                            Toast.makeText(AddEditStore.this,
                                    R.string.no_geocoder_available,
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Start service and update UI to reflect new location
                        startIntentService();
                        // updateUI();
                    }
                });
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
        }

    }

    private void addStoreReq() {

        String st_name=et_storenm.getText().toString().trim();
        if (st_name.length()==0){
            Toast.makeText(this, "Please Enter Store Name", Toast.LENGTH_SHORT).show();
            return;
        }
        String st_cat=et_cat.getText().toString().trim();
        if (st_cat.length()==0){
            Toast.makeText(this, "Please Enter Category", Toast.LENGTH_SHORT).show();
            return;
        }
        String keyword=et_keywords.getText().toString().trim();
        if (keyword.length()==0){
            Toast.makeText(this, "Please Enter Category", Toast.LENGTH_SHORT).show();
            return;
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
            et_location.setText(area + "," + city + "," + pin_code + "," + state + "," + state);
            //((TextView) findViewById(R.id.user_location_add)).setText(mAddressOutput);
            // displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT_1) {
                //  showToast(getString(R.string.address_found));
            }

        }
    }
}
