package com.bsecure.getlucky;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bsecure.getlucky.barcode.TermsPage;
import com.bsecure.getlucky.common.AppPreferences;
import com.bsecure.getlucky.interfaces.RequestHandler;
import com.bsecure.getlucky.pinstore.AddPin;
import com.bsecure.getlucky.services.AddressService;
import com.bsecure.getlucky.services.FetchAddressIntentService;
import com.bsecure.getlucky.volleyhttp.Constants;
import com.bsecure.getlucky.volleyhttp.MethodResquest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.zxing.WriterException;
import com.tooltip.OnClickListener;
import com.tooltip.Tooltip;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class Register extends AppCompatActivity implements RequestHandler, View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    private static final String LOCATION_ADDRESS_KEY = "location-address";
    private AddressResultReceiver mResultReceiver;
    String mAddressOutput;
    Location mLastLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private EditText et_name, et_refer;
    private Button sub_register;
    private String pin_code,area,city,country,phone,otpone,state;
    String savePath = Environment.getExternalStorageDirectory().toString() + "/GetLucky/QRCode/";
    private CheckBox ch_terms;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Intent in = getIntent();

        Bundle bd = in.getExtras();

        phone = bd.getString("phone");
        otpone = bd.getString("otpone");

        sub_register = findViewById(R.id.submit);
        sub_register.setOnClickListener(this);
        et_name = findViewById(R.id.name);
        et_refer = findViewById(R.id.referral);
        ch_terms = findViewById(R.id.ch_terms);
        findViewById(R.id.terms).setOnClickListener(this);
        findViewById(R.id.help_icon).setOnClickListener(this);
        findViewById(R.id.bacl_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overridePendingTransition(R.anim.fade_out_anim,R.anim.fade_in_anim);
                finish();
            }
        });
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
                            Toast.makeText(Register.this,
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
    public void requestStarted() {

    }

    @Override
    public void requestCompleted(JSONObject response, int requestType) {

        try {
            switch (requestType){
                case 100:
                    JSONObject result = new JSONObject(response.toString());

                    if (result.optString("statuscode").equalsIgnoreCase("200")) {
                        JSONArray array = result.getJSONArray("customer_details");
                        AppPreferences.getInstance(this).addToStore("userData",array.toString(),true);
                        getCode();
                        Intent in = new Intent(this, AddPin.class);
                        in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(in);
                        finish();
                    } else {
                        Toast.makeText(this, result.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    }

                    break;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void getCode() {
        try {
            String session_data = AppPreferences.getInstance(this).getFromStore("userData");
            JSONArray ayArray = new JSONArray(session_data);
           // AppPreferences.getInstance(this).addToStore("customer_referral_code",ayArray.getJSONObject(0).optString("customer_referral_code"),true);
            WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            int width = point.x;
            int height = point.y;
            int smallerDimension = width < height ? width : height;
            smallerDimension = smallerDimension * 3 / 4;
            String inputValue = ayArray.getJSONObject(0).optString("name") + "," +
                    ayArray.getJSONObject(0).optString("customer_referral_code");
            QRGEncoder qrgEncoder = new QRGEncoder(
                    inputValue, null,
                    QRGContents.Type.TEXT,
                    smallerDimension);
            try {
                Bitmap bitmap = qrgEncoder.encodeAsBitmap();
                QRGSaver.save(savePath, inputValue, bitmap, QRGContents.ImageType.IMAGE_JPEG);
            } catch (WriterException e) {
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void requestEndedWithError(String error, int errorcode) {

    }

    @Override
    public void onClick(View view) {
        try {
            switch (view.getId()) {

                case R.id.submit:
                    getRegister();
                    break;
                    case R.id.help_icon:
                        SpannableString text = new SpannableString("Referral code must be the code of the customer that has referred you to Get Lucky");
                        Tooltip mTooltip = new Tooltip.Builder(view, R.style.Tooltip)
                                .setDismissOnClick(true)
                                .setGravity(Gravity.BOTTOM)
                                .setText(text.toString())
                                .setCancelable(true)
                                .show();
                    break;
                    case R.id.terms:
                    Intent terms=new Intent(this, TermsPage.class);
                    startActivity(terms);
                    overridePendingTransition(R.anim.fade_in_anim,R.anim.fade_out_anim);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getRegister() {
        try{
            String name = et_name.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(this, "Please Enter Name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (name.length()<=2){
                Toast.makeText(this, "Name Must Be Of Minimum Three Characters", Toast.LENGTH_SHORT).show();
                return;

            }
            if (!ch_terms.isChecked()){
                Toast.makeText(this, "Please Select Terms & Conditions", Toast.LENGTH_SHORT).show();
                return;
            }
            et_refer.setText(AppPreferences.getInstance(this).getFromStore("refer"));
            String refercode=et_refer.getText().toString();
            if (refercode.length()==0||refercode==null){
                refercode="";

            }
            JSONObject object = new JSONObject();

            object.put("name", name);
            object.put("phone_number", phone);
            object.put("address", city);
            object.put("referral_code", refercode);
            object.put("otp", otpone);
            object.put("regidand", AppPreferences.getInstance(this).getFromStore("token"));

            new MethodResquest(this, this, Constants.PATH + "customer/customer_registration", object.toString(), 100);
        }catch (Exception e)
        {
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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 7:

                if (grantResults.length > 0) {

                    boolean secondPermissionResult = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean thirdPermissionResult = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (secondPermissionResult && thirdPermissionResult) {

                    } else {

                    }
                }

                break;
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

            //((TextView) findViewById(R.id.user_location_add)).setText(mAddressOutput);
            // displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT_1) {
                //  showToast(getString(R.string.address_found));
            }

        }
    }
}
