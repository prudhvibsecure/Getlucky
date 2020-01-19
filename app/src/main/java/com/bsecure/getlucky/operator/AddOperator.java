package com.bsecure.getlucky.operator;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bsecure.getlucky.R;
import com.bsecure.getlucky.common.AppPreferences;
import com.bsecure.getlucky.interfaces.RequestHandler;
import com.bsecure.getlucky.volleyhttp.Constants;
import com.bsecure.getlucky.volleyhttp.MethodResquest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddOperator extends AppCompatActivity implements RequestHandler {

    String codes = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.operator_login);
        findViewById(R.id.op_login).setVisibility(View.GONE);
        findViewById(R.id.op_add).setVisibility(View.VISIBLE);
        Button add = findViewById(R.id.add_login);
        codes = getIntent().getStringExtra("code");

        if (codes.equalsIgnoreCase("1")) {
            add.setText("Add");
            add.setTextColor(Color.WHITE);
        } else {
            ((EditText) findViewById(R.id.name_nm)).setText(getIntent().getStringExtra("operator_name"));
            ((EditText) findViewById(R.id.name_u1)).setText(getIntent().getStringExtra("username"));
            ((EditText) findViewById(R.id.password_u1)).setText(getIntent().getStringExtra("password"));
            ((EditText) findViewById(R.id.name_u1)).setEnabled(false);
            ((EditText) findViewById(R.id.name_u1)).setTextColor(Color.BLACK);
            ((TextView) findViewById(R.id.add_opetaror)).setText("EDIT OPERATOR");
            add.setText("Update");
            add.setTextColor(Color.WHITE);
        }
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addOperator(codes);
            }
        });
        findViewById(R.id.bacl_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void addOperator(String codes) {
        try {
            //operator_name,username,password,added_date,status
            JSONObject object = new JSONObject();
            String name = ((EditText) findViewById(R.id.name_nm)).getText().toString().trim();
            if (name.length() == 0) {
                Toast.makeText(this, "Please Fill Required Fields", Toast.LENGTH_SHORT).show();
                return;
            }
            String u_name = ((EditText) findViewById(R.id.name_u1)).getText().toString().trim();
            if (u_name.length() == 0) {
                Toast.makeText(this, "Please Fill Required Fields", Toast.LENGTH_SHORT).show();
                return;
            }
            String password = ((EditText) findViewById(R.id.password_u1)).getText().toString();
            if (password.length() == 0) {
                Toast.makeText(this, "Please Fill Required Fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.contains(" ")) {
                Toast.makeText(this, "Spaces Not Allowed", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.length() == 1 && password.length() >= 16) {
                Toast.makeText(this, "Please Fill Required Fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if (codes.equalsIgnoreCase("1")) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                String currentDateandTime = sdf.format(new Date());
                String session_data = AppPreferences.getInstance(this).getFromStore("userData");
                JSONArray ayArray = new JSONArray(session_data);

                object.put("customer_id", ayArray.getJSONObject(0).optString("customer_id"));
                object.put("store_id", getIntent().getStringExtra("store_id"));
                object.put("operator_name", name);
                object.put("username", u_name);
                object.put("password", password);
                object.put("added_date", currentDateandTime);
                object.put("status", "0");
                MethodResquest req = new MethodResquest(this, this, Constants.PATH + "add_store_operator", object.toString(), 101);
                req.dismissProgress(this);
            } else {

                //store_operator_id,operator_name,password
                object.put("operator_name", name);
                object.put("store_operator_id", getIntent().getStringExtra("store_operator_id"));
                object.put("password", password);
                MethodResquest req = new MethodResquest(this, this, Constants.PATH + "edit_store_operator", object.toString(), 101);
                req.dismissProgress(this);
            }

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
            switch (requestType) {
                case 101:
                    JSONObject object = new JSONObject(response.toString());
                    if (object.optString("statuscode").equalsIgnoreCase("200")) {
                        Toast.makeText(this, object.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                        Intent bb = new Intent("com.operator_refrsh");
                        bb.putExtra("store_id", getIntent().getStringExtra("store_id"));
                        sendBroadcast(bb);
                        this.finish();
                    } else {
                        Toast.makeText(this, object.optString("statusdescription"), Toast.LENGTH_LONG).show();
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
