package com.bsecure.getlucky.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bsecure.getlucky.GetLucky;
import com.bsecure.getlucky.Login;
import com.bsecure.getlucky.R;
import com.bsecure.getlucky.ViewStoreDetails;
import com.bsecure.getlucky.adpters.StoreListAdapter;
import com.bsecure.getlucky.common.AppPreferences;
import com.bsecure.getlucky.interfaces.RequestHandler;
import com.bsecure.getlucky.models.StoreListModel;
import com.bsecure.getlucky.services.AddressService;
import com.bsecure.getlucky.services.FetchAddressIntentService;
import com.bsecure.getlucky.volleyhttp.Constants;
import com.bsecure.getlucky.volleyhttp.MethodResquest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class HomeFragment extends ParentFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, RequestHandler,StoreListAdapter.StoreAdapterListener {

    private GetLucky getLucky;
    private String pin_code, area, city, country, phone, category_ids = "", state;
    private OnListFragmentInteractionListener mListener;
    private OnFragmentInteractionListener mFragListener;
    private View laView;
    private static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    private static final String LOCATION_ADDRESS_KEY = "location-address";
    private AddressResultReceiver mResultReceiver;
    String mAddressOutput;
    Location mLastLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private EditText serach;
    private RecyclerView mRecyclerView;
    private List<StoreListModel> storeListModelList=new ArrayList<>();
    private StoreListAdapter adapter;
    private int count=0;
    public HomeFragment() {

    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mResultReceiver = new AddressResultReceiver(new Handler());
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]
                    {
                            ACCESS_FINE_LOCATION,
                            ACCESS_COARSE_LOCATION

                    }, 7);
            return;
        }
      locationFind();
    }

    private void locationFind() {

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        mLastLocation = location;

                        // In some rare cases the location returned can be null
                        if (mLastLocation == null) {
                            return;
                        }

                        if (!Geocoder.isPresent()) {
                            Toast.makeText(getActivity(),
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

    private void getStoreData() {
        try {
            String serch_word = serach.getText().toString();
            String category = AppPreferences.getInstance(getActivity()).getFromStore("category");
            JSONArray ayArray = new JSONArray(category);
            if (ayArray.length() > 0) {
                for (int f = 0; f < ayArray.length(); f++) {
                    JSONObject oob = ayArray.getJSONObject(f);
                    category_ids = category_ids + "," + oob.optString("category_id");
                }
            }

            category_ids = category_ids.replaceFirst(",", "");
            JSONObject object = new JSONObject();
            object.put("category_ids", category_ids.trim());
            MethodResquest ms = new MethodResquest(getActivity(), this, Constants.PATH + "get_keywords", object.toString(), 100);
            ms.dismissProgress(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        laView = inflater.inflate(R.layout.activity_home_pg, container, false);
        serach = laView.findViewById(R.id.keyword);
        mRecyclerView=laView.findViewById(R.id.mrecycler);
        return laView;

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // mListener = (OnListFragmentInteractionListener) context;

        mFragListener = (GetLucky) context;

        getLucky = (GetLucky) context;

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mFragListener = null;
    }

    @Override
    public void requestStarted() {

    }

    @Override
    public void requestCompleted(JSONObject response, int requestType) {

        try {

            switch (requestType) {
                case 100:

                    JSONObject object = new JSONObject(response.toString());
                    if (object.optString("statuscode").equalsIgnoreCase("200")) {
                        String keywords = object.optString("keywords");
                    }

                    searchStore();
                    break;
                case 101:

                    JSONObject object1 = new JSONObject(response.toString());
                    if (object1.optString("statuscode").equalsIgnoreCase("200")) {
                        laView.findViewById(R.id.spin_kit).setVisibility(View.GONE);
                        laView.findViewById(R.id.no_data).setVisibility(View.GONE);
                        JSONArray jsonarray2 = object1.getJSONArray("store_details");
                        if (jsonarray2.length() > 0) {
                            for (int i = 0; i < jsonarray2.length(); i++) {
                                JSONObject jsonobject = jsonarray2.getJSONObject(i);
                                StoreListModel storeListModel = new StoreListModel();
                                storeListModel.setStore_name(jsonobject.optString("store_name"));
                                storeListModel.setAddress(jsonobject.optString("address"));
                                storeListModel.setArea(jsonobject.optString("area"));
                                storeListModel.setCity(jsonobject.optString("city"));
                                storeListModel.setOffer(jsonobject.optString("offer"));
                                storeListModel.setSpecial_offer(jsonobject.optString("special_offer"));
                                storeListModel.setStore_image(jsonobject.optString("store_image"));
                                storeListModelList.add(storeListModel);
                            }
                            adapter = new StoreListAdapter(storeListModelList, getActivity(), this);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                            mRecyclerView.setLayoutManager(linearLayoutManager);
                            mRecyclerView.setAdapter(adapter);
                        }else{
                            laView.findViewById(R.id.spin_kit).setVisibility(View.GONE);
                            laView.findViewById(R.id.no_data).setVisibility(View.VISIBLE);
                        }
                    }else{
                        laView.findViewById(R.id.spin_kit).setVisibility(View.GONE);
                        laView.findViewById(R.id.no_data).setVisibility(View.VISIBLE);
                        ((TextView)laView.findViewById(R.id.no_data)).setText(object1.optString("statusdescription"));
                    }
                    break;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void searchStore() {
        try {
            JSONObject object = new JSONObject();
            object.put("area", area);
            object.put("city", city);
            object.put("state", state);
            object.put("country", country);
            object.put("search_key", "");
            object.put("pageno", count);
            MethodResquest req=new MethodResquest(getActivity(), this, Constants.PATH + "search_store", object.toString(), 101);
            req.dismissProgress(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void requestEndedWithError(String error, int errorcode) {

    }

    @Override
    public void onRowClicked(List<StoreListModel> matchesList, int pos) {
        String data= AppPreferences.getInstance(getActivity()).getFromStore("userData");
        if (data!=null &&!TextUtils.isEmpty(data)){
            Intent login=new Intent(getActivity(), ViewStoreDetails.class);
            login.putExtra("store_name",matchesList.get(pos).getStore_name());
            login.putExtra("store_image",matchesList.get(pos).getStore_image());
            login.putExtra("store_add",matchesList.get(pos).getCity()+","+matchesList.get(pos).getState());
            login.putExtra("store_offer",matchesList.get(pos).getOffer());
            login.putExtra("store_spofer",matchesList.get(pos).getSpecial_offer());
            login.putExtra("store_ph",matchesList.get(pos).getStore_phone_number());
            //login.putExtra("store_ph",matchesList.get(pos).getp());
            startActivity(login);
        }else{
            Intent login=new Intent(getActivity(), Login.class);
            startActivity(login);
        }

    }

    public interface OnListFragmentInteractionListener {

        void onListFragmentInteraction(JSONObject item);

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        mFragListener.onFragmentInteraction(R.string.app_name, true);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mFragListener.onFragmentInteraction(R.string.app_name, true);

    }

    protected void startIntentService() {
        Intent intent = new Intent(getActivity(), AddressService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, mLastLocation);
        getActivity().startService(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 7:

                if (grantResults.length > 0) {

                    boolean secondPermissionResult = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean thirdPermissionResult = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (secondPermissionResult && thirdPermissionResult) {
                        locationFind();
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

            area = resultData.getString("area");
            city = resultData.getString("city");
            pin_code = resultData.getString("postalcode");
            country = resultData.getString("country");
            state = resultData.getString("state");
            mAddressOutput = area + "," + city + "," + state + "," + country;
            ((EditText) laView.findViewById(R.id.location)).setText(mAddressOutput);
            // displayAddressOutput();
            getStoreData();
            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                //  showToast(getString(R.string.address_found));

            }

        }
    }
}