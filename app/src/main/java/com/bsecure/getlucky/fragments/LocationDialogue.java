package com.bsecure.getlucky.fragments;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ResultReceiver;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bsecure.getlucky.GetLucky;
import com.bsecure.getlucky.IDownloadCallback;
import com.bsecure.getlucky.R;
import com.bsecure.getlucky.adpters.LocationAdapter;
import com.bsecure.getlucky.interfaces.Click;
import com.bsecure.getlucky.interfaces.ClickListener;
import com.bsecure.getlucky.interfaces.RequestHandler;
import com.bsecure.getlucky.volleyhttp.Constants;
import com.bsecure.getlucky.volleyhttp.MethodResquest;
import com.google.zxing.WriterException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

import static android.content.Context.DOWNLOAD_SERVICE;
import static android.content.Context.WINDOW_SERVICE;

public class LocationDialogue extends DialogFragment implements IDownloadCallback, RequestHandler {
    private String TAG = LocationDialogue.class.getSimpleName();
    ImageView close;
    AutoCompleteTextView loc;
    TextView hint;
    LocationAdapter adapter;
    RecyclerView list;
    JSONArray array;
    public static Context contex;
    private ResultReceiver receiver;


    public static LocationDialogue newInstance() {
        LocationDialogue f = new LocationDialogue();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_preview, container, false);

        receiver = getArguments().getParcelable("rec");
        loc = v.findViewById(R.id.location);
        close = v.findViewById(R.id.close);
        hint = v.findViewById(R.id.hint);
        list = v.findViewById(R.id.list);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        loc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length() < 3)
                {
                    hint.setText("Minimum 3 Characters Required to Search");
                    getLoc("");
                }
                else
                {
                    hint.setText("");
                    //adapter.notifyDataSetChanged();
                }
                if(s.toString().length()%3 == 0)
                {
                    getLoc(s.toString().trim());
                    //adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return v;


    }

    private void getLoc(String loc) {

        try {
            JSONObject object = new JSONObject();
            object.put("search_key", loc);
            object.put("pageno", 0);
            MethodResquest req = new MethodResquest(getActivity(), this, Constants.PATH + "search_location", object.toString(), 100);
            req.dismissProgress(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Override
    public void onStateChange(int what, int arg1, int arg2, Object obj, int requestId) {

    }




    @Override
    public void onPause() {
        super.onPause();
        //images.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        //images = simages;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //images.clear();
    }

    @Override
    public void requestStarted() {

    }

    @Override
    public void requestCompleted(JSONObject response, int requestType) {

        if(requestType == 100)
        {
            try {

                JSONObject object = new JSONObject(response.toString());
                if (object.optString("statuscode").equalsIgnoreCase("200")) {

                    array = object.getJSONArray("locations");
                    adapter = new LocationAdapter(getActivity(), array, new ClickListener() {
                        @Override
                        public void onClick(int pos) {
                            try {
                                //String location = array.getJSONObject(pos).getString("area") + "," +array.getJSONObject(pos).getString("city")+ "," +array.getJSONObject(pos).getString("state");

                                sendResult(array.getJSONObject(pos).getString("area"),array.getJSONObject(pos).getString("city"), array.getJSONObject(pos).getString("state") );
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    list.setLayoutManager(layoutManager);
                    list.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }
                else
                {
                    final JSONArray array = new JSONArray();
                    adapter = new LocationAdapter(getActivity(), array, new ClickListener() {
                        @Override
                        public void onClick(int pos) {
                            try {
                                //String location = array.getJSONObject(pos).getString("area") + "," +array.getJSONObject(pos).getString("city")+ "," +array.getJSONObject(pos).getString("state");
                                sendResult(array.getJSONObject(pos).getString("area"),array.getJSONObject(pos).getString("city"), array.getJSONObject(pos).getString("state") );
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    list.setLayoutManager(layoutManager);
                    list.setAdapter(adapter);
                    Toast.makeText(getActivity(), object.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void requestEndedWithError(String error, int errorcode) {

    }

    public void sendResult(String area, String city, String state)
    {
        Bundle bundle = new Bundle();
        bundle.putString("city", city);
        bundle.putString("area", area);
        bundle.putString("state", state);
        receiver.send(1, bundle);
        dismiss();
    }

}
