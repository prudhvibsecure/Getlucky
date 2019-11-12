package com.bsecure.getlucky;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bsecure.getlucky.volleyhttp.Constants;
import com.bumptech.glide.Glide;

public class ViewStoreDetails extends AppCompatActivity implements View.OnClickListener {

    ImageView store_img;
    TextView tv_store_name,store_address;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_store_details);

        store_img=findViewById(R.id.post_image);
        tv_store_name=findViewById(R.id.store_name);
        store_address=findViewById(R.id.store_address);
        findViewById(R.id.bacl_btn).setOnClickListener(this);


       getIntent().getStringExtra("store_offer");
        getIntent().getStringExtra("store_spofer");
        tv_store_name.setText(getIntent().getStringExtra("store_name"));
        store_address.setText(getIntent().getStringExtra("store_add"));
        Glide.with(this).load(Constants.PATH+"assets/upload/avatar/" +getIntent().getStringExtra("store_image")).into(store_img);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.bacl_btn:
                overridePendingTransition(R.anim.fade_out_anim,R.anim.fade_in_anim);
                finish();
                break;
        }

    }
}
