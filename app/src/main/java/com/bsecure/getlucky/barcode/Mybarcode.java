package com.bsecure.getlucky.barcode;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bsecure.getlucky.R;
import com.bsecure.getlucky.common.AppPreferences;
import com.bumptech.glide.Glide;
import com.google.zxing.WriterException;

import org.json.JSONArray;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

public class Mybarcode extends AppCompatActivity {

    private ImageView post_image,pf_image;
    String savePath = Environment.getExternalStorageDirectory().toString() + "/GetLucky/QRCode/";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_code_layout);

        post_image=findViewById(R.id.post_image);
        pf_image=findViewById(R.id.pf_image);
        findViewById(R.id.bacl_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        String session_data = AppPreferences.getInstance(this).getFromStore("userData");
        try {
            JSONArray ayArray = new JSONArray(session_data);
            ((TextView) findViewById(R.id.name)).setText(ayArray.getJSONObject(0).optString("name"));
            Glide.with(this).load(ayArray.getJSONObject(0).optString("profile_image")).into(pf_image);
            String inputValue = ayArray.getJSONObject(0).optString("name") + "," +
                    ayArray.getJSONObject(0).optString("customer_referral_code");
            getCode();
           // Glide.with(this).load("file://" + savePath+inputValue).into(post_image);

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void getCode() {
        try {
            boolean save ;
            String session_data = AppPreferences.getInstance(this).getFromStore("userData");
            JSONArray ayArray = new JSONArray(session_data);
            AppPreferences.getInstance(this).addToStore("customer_referral_code",ayArray.getJSONObject(0).optString("customer_referral_code"),true);
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

                String result;
                Bitmap bitmap = qrgEncoder.encodeAsBitmap();
                post_image.setImageBitmap(bitmap);
                save = QRGSaver.save(savePath, inputValue, bitmap, QRGContents.ImageType.IMAGE_JPEG);
                result = save ? "Image Saved" : "Image Not Saved";
               // Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            } catch (WriterException e) {
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
