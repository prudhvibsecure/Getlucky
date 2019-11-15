package com.bsecure.getlucky.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bsecure.getlucky.GetLucky;
import com.bsecure.getlucky.Login;
import com.bsecure.getlucky.R;
import com.bsecure.getlucky.ViewStoreDetails;
import com.bsecure.getlucky.adpters.StoreListAdapter;
import com.bsecure.getlucky.common.AppPreferences;
import com.bsecure.getlucky.helper.RecyclerOnScrollListener;
import com.bsecure.getlucky.interfaces.RequestHandler;
import com.bsecure.getlucky.models.StoreListModel;
import com.bsecure.getlucky.services.AddressService;
import com.bsecure.getlucky.services.GetAddressIntentService;
import com.bsecure.getlucky.volleyhttp.Constants;
import com.bsecure.getlucky.volleyhttp.MethodResquest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends ParentFragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, RequestHandler,StoreListAdapter.StoreAdapterListener {

    private GetLucky getLucky;
    private String pin_code, area, city, country, phone, category_ids = "", state;
    private OnListFragmentInteractionListener mListener;
    private OnFragmentInteractionListener mFragListener;
    private View laView;
    private AddressResultReceiver mResultReceiver;
    String mAddressOutput;
    Location mLastLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private EditText serach;
    private RecyclerView mRecyclerView;
    private List<StoreListModel> storeListModelList;
    private StoreListAdapter adapter;
    private int count=0;
    private SwipeRefreshLayout mSwipeRefreshLayout = null;
    private String total_pages = "0";

    private boolean isRefresh = false;

    private FusedLocationProviderClient fusedLocationClient;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;

    private LocationAddressResultReceiver addressResultReceiver;

    private TextView currentAddTv;

    private Location currentLocation;

    private LocationCallback locationCallback;
    private RecyclerOnScrollListener recycScollListener = null;

    public HomeFragment() {

    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            locationFind();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        101);
            }
        }

    }

    private void locationFind() {
        mResultReceiver = new AddressResultReceiver(new Handler());
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
        laView.findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchStore(0);
            }
        });
        mSwipeRefreshLayout = (SwipeRefreshLayout) laView.findViewById(R.id.swip_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorPrimaryDark,
                R.color.colorPrimaryDark, R.color.colorPrimaryDark, R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setEnabled(false);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                recycScollListener.resetValue();
                searchStore(0);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        recycScollListener = new RecyclerOnScrollListener(layoutManager) {

            @Override
            public void onLoadMoreData(int currentPage) {
                if (total_pages.length() > 0)
                    if (currentPage <= Integer.parseInt(total_pages) - 1) {
                        searchStore(currentPage);
                       // getData(2, currentPage);
                       // getView().findViewById(R.id.catgr_pbar).setVisibility(View.VISIBLE);


                    }
            }
        };

        mRecyclerView.addOnScrollListener(recycScollListener);


        addressResultReceiver = new LocationAddressResultReceiver(new Handler());

        currentAddTv = laView.findViewById(R.id.current_address);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                currentLocation = locationResult.getLocations().get(0);
                getAddress();
            };
        };
        startLocationUpdates();
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
                        JSONArray array=object.getJSONArray("keywords_ios");
                        AppPreferences.getInstance(getActivity()).addToStore("keywords",array.toString(),true);
                    }

                    searchStore(0);
                    break;
                case 101:

                    JSONObject object1 = new JSONObject(response.toString());
                    if (object1.optString("statuscode").equalsIgnoreCase("200")) {
                        storeListModelList=new ArrayList<>();
                        mSwipeRefreshLayout.setRefreshing(false);
                        mSwipeRefreshLayout.setEnabled(true);
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
                                storeListModel.setState(jsonobject.optString("state"));
                                storeListModel.setCity(jsonobject.optString("city"));
                                storeListModel.setCountry(jsonobject.optString("country"));
                                storeListModel.setPin_code(jsonobject.optString("pin_code"));
                                storeListModel.setOffer(jsonobject.optString("offer"));
                                storeListModel.setSpecial_offer(jsonobject.optString("special_offer"));
                                storeListModel.setStore_image(jsonobject.optString("store_image"));
                                storeListModel.setStore_phone_number(jsonobject.optString("store_phone_number"));
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
                        mSwipeRefreshLayout.setRefreshing(false);
                        mSwipeRefreshLayout.setEnabled(true);
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

    private void searchStore(int pageno) {
        try {
            JSONObject object = new JSONObject();
            object.put("area", area);
            object.put("city", city);
            object.put("state", state);
            object.put("country", country);
            object.put("search_key", "");
            object.put("pageno", pageno);
            MethodResquest req=new MethodResquest(getActivity(), this, Constants.PATH + "search_store", object.toString(), 101);
            req.dismissProgress(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void requestEndedWithError(String error, int errorcode) {
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setEnabled(true);
    }

    @Override
    public void onRowClicked(List<StoreListModel> matchesList, int pos) {
        String data= AppPreferences.getInstance(getActivity()).getFromStore("userData");
        if (data!=null &&!TextUtils.isEmpty(data)){
            Intent login=new Intent(getActivity(), ViewStoreDetails.class);
            login.putExtra("store_name",matchesList.get(pos).getStore_name());
            login.putExtra("store_image",matchesList.get(pos).getStore_image());
            login.putExtra("store_add",matchesList.get(pos).getArea()+","+matchesList.get(pos).getCity()+","+matchesList.get(pos).getState()+","+matchesList.get(pos).getState()+","+matchesList.get(pos).getPin_code());
            login.putExtra("store_offer",matchesList.get(pos).getOffer());
            login.putExtra("store_spofer",matchesList.get(pos).getSpecial_offer());
            login.putExtra("store_ph",matchesList.get(pos).getStore_phone_number());
            //login.putExtra("store_ph",matchesList.get(pos).getp());
            startActivity(login);
            getActivity().overridePendingTransition(R.anim.fade_in_anim,R.anim.fade_out_anim);
        }else{
            Intent login=new Intent(getActivity(), Login.class);
            startActivity(login);
            getActivity().overridePendingTransition(R.anim.fade_in_anim,R.anim.fade_out_anim);
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
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 101
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
           locationFind();
        }
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationUpdates();
                } else {
                    Toast.makeText(getActivity(), "Location permission not granted, " +
                                    "restart the app if you want the feature",
                            Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @SuppressWarnings("MissingPermission")
    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getLucky,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(2000);
            locationRequest.setFastestInterval(1000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    null);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void getAddress() {

        if (!Geocoder.isPresent()) {
            Toast.makeText(getActivity(),
                    "Can't find current address, ",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(getActivity(), GetAddressIntentService.class);
        intent.putExtra("add_receiver", addressResultReceiver);
        intent.putExtra("add_location", currentLocation);
        getActivity().startService(intent);
    }


    private class LocationAddressResultReceiver extends ResultReceiver {
        LocationAddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultCode == 0) {
                //Last Location can be null for various reasons
                //for example the api is called first time
                //so retry till location is set
                //since intent service runs on background thread, it doesn't block main thread
                Log.d("Address", "Location null retrying");
                getAddress();
            }

            if (resultCode == 1) {
                Toast.makeText(getActivity(),
                        "Address not found, " ,
                        Toast.LENGTH_SHORT).show();
            }

            String currentAdd = resultData.getString("address_result");

            showResults(currentAdd);
        }
    }

    private void showResults(String currentAdd){
        currentAddTv.setText(currentAdd);
    }
}