package com.bsecure.getlucky.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.bsecure.getlucky.ViewStoreDetails_Home;
import com.bsecure.getlucky.adpters.StoreListAdapter;
import com.bsecure.getlucky.common.AppPreferences;
import com.bsecure.getlucky.helper.RecyclerOnScrollListener;
import com.bsecure.getlucky.interfaces.IItemHandler;
import com.bsecure.getlucky.interfaces.RequestHandler;
import com.bsecure.getlucky.models.StoreListModel;
import com.bsecure.getlucky.network.CheckNetwork;
import com.bsecure.getlucky.services.GetAddressIntentService;
import com.bsecure.getlucky.volleyhttp.Constants;
import com.bsecure.getlucky.volleyhttp.HTTPPostTask;
import com.bsecure.getlucky.volleyhttp.MethodResquest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.tooltip.Tooltip;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;


public class HomeFragment extends ParentFragment implements  IItemHandler,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, RequestHandler, StoreListAdapter.StoreAdapterListener {

    private GetLucky getLucky;
    private String pin_code, area = "", city = "", country = "", phone, category_ids = "", state = "", loacal_area;
    private OnListFragmentInteractionListener mListener;
    private OnFragmentInteractionListener mFragListener;
    private View laView;
    private FusedLocationProviderClient mFusedLocationClient;
    private EditText serach;
    private RecyclerView mRecyclerView;
    private StoreListAdapter adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout = null;

    private FusedLocationProviderClient fusedLocationClient;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;

    private LocationAddressResultReceiver addressResultReceiver;

    private Location currentLocation;

    private LocationCallback locationCallback;
    private RecyclerOnScrollListener recycScollListener = null;
    private String networkType = "mobile";
    private int count_page = 0;
    List<StoreListModel> storeListModelList = new ArrayList<>();
    LinearLayout loading_btm;

    public HomeFragment() {

    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
            HTTPPostTask task=new HTTPPostTask(getActivity(),this);
            task.userRequest("",100,Constants.PATH + "get_keywords",object.toString());
//            MethodResquest ms = new MethodResquest(getActivity(), this, Constants.PATH + "get_keywords", object.toString(), 100);
//            ms.dismissProgress(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        laView = inflater.inflate(R.layout.activity_home_pg, container, false);
        serach = laView.findViewById(R.id.keyword);
        loading_btm = laView.findViewById(R.id.loading_btm);
        mRecyclerView = laView.findViewById(R.id.mrecycler);
        laView.findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable()) {
                    storeListModelList = new ArrayList<>();
                    count_page = 0;
                    searchStore(count_page);
                }
            }
        });
        mSwipeRefreshLayout = laView.findViewById(R.id.swip_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorPrimaryDark,
                R.color.colorPrimaryDark, R.color.colorPrimaryDark, R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setEnabled(false);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                recycScollListener.resetValue();
                count_page = 0;
                if (isNetworkAvailable()) {
                    storeListModelList = new ArrayList<>();
                    searchStore(count_page);
                    adapter.notifyDataSetChanged();
                } else {
                    mSwipeRefreshLayout.setRefreshing(false);
                    mSwipeRefreshLayout.setEnabled(true);
                    Toast.makeText(getActivity(), "Sorry-No Network Found", Toast.LENGTH_SHORT).show();
                }
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        recycScollListener = new RecyclerOnScrollListener(layoutManager) {

            @Override
            public void onLoadMoreData(int currentPage) {
                try {
                    if (isNetworkAvailable()) {
                        count_page = currentPage;
                        loading_btm.setVisibility(View.VISIBLE);
                        searchStore(currentPage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };

        mRecyclerView.addOnScrollListener(recycScollListener);

        addressResultReceiver = new LocationAddressResultReceiver(new Handler());


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                currentLocation = locationResult.getLocations().get(0);
                if (isNetworkAvailable()) {
                    getAddress();
                }
            }
        };
        startLocationUpdates();
        serach.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence data, int i, int i1, int i2) {

                if (data.length() == 0) {
                    if (isNetworkAvailable()) {
                        recycScollListener.resetValue();
                        count_page = 0;
                        storeListModelList = new ArrayList<>();
                        adapter.clear();
                        searchStore(count_page);
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getActivity(), "Sorry-No Network Found", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return laView;

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

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

                case 101:

                    JSONObject object1 = new JSONObject(response.toString());
                    if (object1.optString("statuscode").equalsIgnoreCase("200")) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        mSwipeRefreshLayout.setEnabled(true);
                        laView.findViewById(R.id.spin_kit).setVisibility(View.GONE);
                        laView.findViewById(R.id.no_data).setVisibility(View.GONE);
                        JSONArray jsonarray2 = object1.getJSONArray("store_details");
                        if (jsonarray2.length() > 0) {
                            for (int i = 0; i < jsonarray2.length(); i++) {
                                JSONObject jsonobject = jsonarray2.getJSONObject(i);
                                StoreListModel storeListModel = new StoreListModel();
                                storeListModel.setStore_id(jsonobject.optString("store_id"));
                                storeListModel.setStore_name(jsonobject.optString("store_name"));
                                storeListModel.setArea(jsonobject.optString("area"));
                                storeListModel.setState(jsonobject.optString("state"));
                                storeListModel.setCity(jsonobject.optString("city"));
                                storeListModel.setCountry(jsonobject.optString("country"));
                                storeListModel.setPin_code(jsonobject.optString("pin_code"));
                                storeListModel.setOffer(jsonobject.optString("offer_description"));
                                storeListModel.setOffer_date(jsonobject.optString("offer_date"));
//                                storeListModel.setCategories(jsonobject.optString("categories"));
//                                storeListModel.setKeywords(jsonobject.optString("keywords"));
                                storeListModel.setLucky_offer_description(jsonobject.optString("lucky_offer_description"));
                                storeListModel.setStore_image(jsonobject.optString("store_image"));
                                storeListModel.setStore_phone_number(jsonobject.optString("store_phone_number"));
                                storeListModelList.add(storeListModel);
                            }
                            storeListModelList = new ArrayList<>(new LinkedHashSet<StoreListModel>(storeListModelList));
                            loading_btm.setVisibility(View.GONE);
                            adapter = new StoreListAdapter(storeListModelList, getActivity(), this);
                            mRecyclerView.setAdapter(adapter);
                        } else {
                            if (count_page == 0) {
                                mRecyclerView.removeAllViews();
                                adapter.clear();
                                mSwipeRefreshLayout.setRefreshing(false);
                                mSwipeRefreshLayout.setEnabled(true);
                                laView.findViewById(R.id.spin_kit).setVisibility(View.GONE);
                                laView.findViewById(R.id.no_data).setVisibility(View.VISIBLE);
                            } else {
                                loading_btm.setVisibility(View.GONE);
                            }

                        }
                    } else {
                        if (count_page == 0) {
                            mRecyclerView.removeAllViews();
                            if (adapter!=null) {
                                adapter.clear();
                            }
                            mSwipeRefreshLayout.setRefreshing(false);
                            mSwipeRefreshLayout.setEnabled(true);
                            laView.findViewById(R.id.spin_kit).setVisibility(View.GONE);
                            laView.findViewById(R.id.no_data).setVisibility(View.VISIBLE);
                            ((TextView) laView.findViewById(R.id.no_data)).setText(object1.optString("statusdescription"));
                        } else {
                            loading_btm.setVisibility(View.GONE);
                        }
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
            if (area == null)
                area = "";
            String key = serach.getText().toString();
            if (key.length() == 0) {
                key = "";
            }
            object.put("area", area);
            object.put("city", city);
            object.put("state", state);
            object.put("country", country);
            object.put("search_key", key);
            object.put("pageno", pageno);
            MethodResquest req = new MethodResquest(getActivity(), this, Constants.PATH + "search_store", object.toString(), 101);
            req.dismissProgress(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void requestEndedWithError(String error, int errorcode) {
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setEnabled(true);
        laView.findViewById(R.id.spin_kit).setVisibility(View.GONE);
        laView.findViewById(R.id.no_data).setVisibility(View.VISIBLE);
        //((TextView)laView.findViewById(R.id.no_data)).setText(error);
    }

    @Override
    public void onRowClicked(List<StoreListModel> matchesList, int pos) {
        String data = AppPreferences.getInstance(getActivity()).getFromStore("userData");
        if (data != null && !TextUtils.isEmpty(data)) {
            Intent login = new Intent(getActivity(), ViewStoreDetails_Home.class);
            login.putExtra("store_name", matchesList.get(pos).getStore_name());
            login.putExtra("store_id", matchesList.get(pos).getStore_id());
            login.putExtra("store_image", matchesList.get(pos).getStore_image());
            login.putExtra("store_add", matchesList.get(pos).getArea() + "," + matchesList.get(pos).getCity() + "," + matchesList.get(pos).getState() + "," + matchesList.get(pos).getPin_code());
            login.putExtra("store_add1", matchesList.get(pos).getArea() + "," + matchesList.get(pos).getCity() + "," + matchesList.get(pos).getState());
            login.putExtra("store_offer", matchesList.get(pos).getOffer());
            login.putExtra("store_ph", matchesList.get(pos).getStore_phone_number());
            login.putExtra("type", "0");
            //login.putExtra("store_ph",matchesList.get(pos).getp());
            startActivity(login);
            getActivity().overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
        } else {
            Intent login = new Intent(getActivity(), Login.class);
            startActivity(login);
            getActivity().overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
        }

    }

    @Override
    public void onFinish(Object results, int requestId) {
        try {
            switch (requestId){
                case 100:

                    JSONObject object = new JSONObject(results.toString());
                    if (object.optString("statuscode").equalsIgnoreCase("200")) {
                        JSONArray array = object.getJSONArray("keywords_ios");
                        AppPreferences.getInstance(getActivity()).addToStore("keywords", array.toString(), true);
                        AppPreferences.getInstance(getActivity()).addToStore("keywords_new", array.toString(), true);

                    }
                    searchStore(count_page);
                    break;
                    default:
                        break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String errorCode, int requestId) {

    }

    @Override
    public void onProgressChange(int requestId, Long... values) {

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == 101
//                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//           locationFind();
//        }
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

    @Override
    public void onResume() {

        startLocationUpdates();
        super.onResume();
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
            locationRequest.setInterval(20000);
            locationRequest.setFastestInterval(10000);
            // locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);// up to 10 km in city radius

            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    null);
        }
    }


    @SuppressWarnings("MissingPermission")
    public void getAddress() {

        if (!Geocoder.isPresent()) {
            Toast.makeText(getActivity(),
                    "Can't find current address, ",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Intent intent = new Intent(getActivity(), GetAddressIntentService.class);
            intent.putExtra("add_receiver", addressResultReceiver);
            intent.putExtra("add_location", currentLocation);
            getContext().startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class LocationAddressResultReceiver extends ResultReceiver {
        LocationAddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultCode == 0) {
                Log.d("Address", "Location null retrying");
                getAddress();
            }

            if (resultCode == 1) {
                Toast.makeText(getActivity(),
                        "Address not found",
                        Toast.LENGTH_SHORT).show();
            }

            String currentAdd = resultData.getString("address_data");
            area = resultData.getString("area");
            loacal_area = resultData.getString("area");
            city = resultData.getString("city");
            pin_code = resultData.getString("postalcode");
            country = resultData.getString("country");
            state = resultData.getString("state");
            showResults(currentAdd);
        }
    }

    private void showResults(String currentAdd) {

        ((TextView) laView.findViewById(R.id.location)).setText(currentAdd);
        String vl = AppPreferences.getInstance(getActivity()).getFromStore("first_time");
        if (vl.equalsIgnoreCase("0")) {
            AppPreferences.getInstance(getActivity()).addToStore("larea", loacal_area, true);
            laView.findViewById(R.id.no_data).setVisibility(View.GONE);
            if (isNetworkAvailable()) {
                AppPreferences.getInstance(getActivity()).addToStore("first_time", "Yes", true);
                count_page = 0;
                storeListModelList = new ArrayList<>();
                getStoreData();
            }
        }
        if (!TextUtils.isEmpty(area)) {
            String k_area = AppPreferences.getInstance(getActivity()).getFromStore("larea");
            if (k_area.startsWith(area)) {
                return;
            } else {
                count_page = 0;
                AppPreferences.getInstance(getActivity()).addToStore("first_time", "0", true);
                new Tooltip.Builder(laView.findViewById(R.id.location), R.style.Tooltip)
                        .setDismissOnClick(true)
                        .setGravity(Gravity.RIGHT)
                        .setText(currentAdd)
                        .setCancelable(true)
                        .show();
            }
        }
    }

    private boolean isNetworkAvailable() {

        try {
            ConnectivityManager manager = (ConnectivityManager) getContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            if (manager == null) {

                return false;

            }

            NetworkInfo net = manager.getActiveNetworkInfo();

            if (net != null) {

                networkType = net.getTypeName();

                return net.isConnected();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }

}