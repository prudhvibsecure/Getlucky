package com.bsecure.getlucky.barcode;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bsecure.getlucky.BuildConfig;
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
    Button share;
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
            ((TextView) findViewById(R.id.code)).setText(ayArray.getJSONObject(0).optString("customer_referral_code"));
            Glide.with(this).load(ayArray.getJSONObject(0).optString("profile_image")).into(pf_image);
            String inputValue = ayArray.getJSONObject(0).optString("name") + "," +
                    ayArray.getJSONObject(0).optString("customer_referral_code");
            getCode();
           // Glide.with(this).load("file://" + savePath+inputValue).into(post_image);

        }catch (Exception e){
            e.printStackTrace();
        }

        share = findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "GetLucky");
                    String shareMessage = "\nLet me recommend you this application\n\n";
                    shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch (Exception e) {
                    //e.toString();
                }

            }
        });

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
