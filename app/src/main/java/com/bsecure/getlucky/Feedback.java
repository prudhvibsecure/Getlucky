package com.bsecure.getlucky;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bsecure.getlucky.common.AppPreferences;
import com.bsecure.getlucky.interfaces.RequestHandler;
import com.bsecure.getlucky.volleyhttp.Constants;
import com.bsecure.getlucky.volleyhttp.MethodResquest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Feedback extends AppCompatActivity implements RequestHandler {

    ImageView back;
    Button submit;
    EditText feed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        back = findViewById(R.id.back);
        feed = findViewById(R.id.feed);
        submit = findViewById(R.id.proceed);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Feedback.this.finish();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(feed.getText().toString().length() == 0)
                {
                    Toast.makeText(Feedback.this, "Please add some feedback", Toast.LENGTH_SHORT).show();
                    feed.requestFocus();
                    return;
                }
                sendFeedback();
            }
        });
    }

    private void sendFeedback() {

        try {

            String session_data = AppPreferences.getInstance(this).getFromStore("userData");
            JSONArray ayArray = new JSONArray(session_data);

            JSONObject object = new JSONObject();
            object.put("store_id", getIntent().getStringExtra("store_id"));
            object.put("customer_id", ayArray.getJSONObject(0).optString("customer_id"));
            new MethodResquest(this, this, Constants.PATH + "edit_offers", object.toString(), 100);

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
            JSONObject deletObj = new JSONObject(response.toString());
            if (deletObj.optString("statuscode").equalsIgnoreCase("200")) {
                Toast.makeText(this, deletObj.optString("statusdescription"), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, deletObj.optString("statusdescription"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void requestEndedWithError(String error, int errorcode) {

    }
}
