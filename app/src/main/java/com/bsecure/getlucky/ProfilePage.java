package com.bsecure.getlucky;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bsecure.getlucky.common.AppPreferences;
import com.bsecure.getlucky.interfaces.RequestHandler;
import com.bsecure.getlucky.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProfilePage extends AppCompatActivity implements View.OnClickListener , RequestHandler {

    private String session_data = null;
    private EditText name,dob,gender,location;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        try {
            session_data = AppPreferences.getInstance(this).getFromStore("userData");
            Log.e("data",session_data);
            JSONArray ayArray = new JSONArray(session_data);
            name=findViewById(R.id.name);
            name.setText(ayArray.getJSONObject(0).optString("name"));
            dob=findViewById(R.id.dateofbirth);
            dob.setText(ayArray.getJSONObject(0).optString("date_of_birth"));
            gender=findViewById(R.id.gender);
            gender.setOnClickListener(this);
            gender.setText(ayArray.getJSONObject(0).optString("gender"));
            location=findViewById(R.id.location);
            location .setText(ayArray.getJSONObject(0).optString("area")+","+ayArray.getJSONObject(0).optString("city")+","+ayArray.getJSONObject(0).optString("state")+","+ayArray.getJSONObject(0).optString("country")+","+ayArray.getJSONObject(0).optString("pin_code"));
        }catch (Exception e){
            e.printStackTrace();
        }

        findViewById(R.id.bacl_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bacl_btn:
                finish();
                break;
            case R.id.gender:

                ArrayList<String> gender_sp=new ArrayList<>();
                gender_sp.add("Male");
                gender_sp.add("Female");
                gender_sp.add("Other");
                Utils.openListDialogView((TextView) view,"Select Gender", gender_sp,this);
                break;
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
}
