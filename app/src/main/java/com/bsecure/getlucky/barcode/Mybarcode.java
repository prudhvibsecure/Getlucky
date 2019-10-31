package com.bsecure.getlucky.barcode;

import android.os.Bundle;
import android.os.Environment;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bsecure.getlucky.R;

public class Mybarcode extends AppCompatActivity {

    private ImageView post_image;
    String savePath = Environment.getExternalStorageDirectory().getPath() + "/GetLucky/QRCode/";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_code_layout);

        post_image=findViewById(R.id.post_image);


    }
}
