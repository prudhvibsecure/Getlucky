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

import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bsecure.getlucky.IDownloadCallback;
import com.bsecure.getlucky.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.zxing.WriterException;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

import static android.content.Context.DOWNLOAD_SERVICE;
import static android.content.Context.WINDOW_SERVICE;

public class QRDialogFragment extends DialogFragment implements IDownloadCallback {
    private String TAG = QRDialogFragment.class.getSimpleName();
    ImageView download, image_preview;
    DownloadManager downloadManager;
    String path, code;
    Bitmap bitmap;

    public static QRDialogFragment newInstance() {
        QRDialogFragment f = new QRDialogFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_slider, container, false);

        download = v.findViewById(R.id.download);
        image_preview = v.findViewById(R.id.image_preview);
        path = getArguments().getString("image");
        code = getArguments().getString("code");
        getCode();
        //image_preview.setImageURI(Uri.parse(path));
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                } else {
                    storeImage(bitmap);
                    //DownloadData(Uri.parse(Paths.up_load+images.get(selectedPosition).getEname()),images.get(selectedPosition).getEname());
                    //new DownloadImage().execute(Paths.up_load+images.get(selectedPosition).getEname());
                }

                //getActivity().startService(DownloadService.getDownloadService(getActivity(), Paths.up_load, Environment.DIRECTORY_DOWNLOADS, images.get(selectedPosition).getEname()));
            }
        });

        return v;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Override
    public void onStateChange(int what, int arg1, int arg2, Object obj, int requestId) {

    }


    private long DownloadData(Uri uri, String ename) {

        long downloadReference;

        // Create request for android download manager
        downloadManager = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        //Setting title of request
        request.setTitle(ename);

        //Setting description of request
        request.setDescription("File downloaded...");

        //Set the local destination for the downloaded file to a path
        //within the application's external files directory

        request.setDestinationInExternalFilesDir(getActivity(),
                Environment.DIRECTORY_DOWNLOADS, ename);
        Toast.makeText(getActivity(), "Image Downloaded Successfully", Toast.LENGTH_SHORT).show();

        //Enqueue download and save into referenceId
        downloadReference = downloadManager.enqueue(request);
        return downloadReference;
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

   /* private class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        private String TAG = "DownloadImage";
        private Bitmap downloadImageBitmap(String sUrl) {
            Bitmap bitmap = null;
            try {
                InputStream inputStream = new URL(sUrl).openStream();   // Download Image from URL
                bitmap = BitmapFactory.decodeStream(inputStream);       // Decode Bitmap
                inputStream.close();
            } catch (Exception e) {
                Log.d(TAG, "Exception 1, Something went wrong!");
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return downloadImageBitmap(params[0]);
        }

        protected void onPostExecute(Bitmap result) {
            saveImage(getActivity(), result, "my_image.png");
        }
    }

    public void saveImage(Context context, Bitmap b, String imageName) {
        FileOutputStream foStream;
        try {
            foStream = context.openFileOutput(imageName, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.PNG, 100, foStream);
            foStream.close();
        } catch (Exception e) {
            Log.d("saveImage", "Exception 2, Something went wrong!");
            e.printStackTrace();
        }
    }*/

    private void getCode() {
        try {
            boolean save;
            WindowManager manager = (WindowManager) getActivity().getSystemService(WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            int width = point.x;
            int height = point.y;
            int smallerDimension = width < height ? width : height;
            smallerDimension = smallerDimension * 3 / 4;
            String inputValue = code;
            QRGEncoder qrgEncoder = new QRGEncoder(
                    inputValue, null,
                    QRGContents.Type.TEXT,
                    smallerDimension);
            try {

                String result;
                bitmap = qrgEncoder.encodeAsBitmap();
                image_preview.setImageBitmap(bitmap);
                save = QRGSaver.save(path, inputValue, bitmap, QRGContents.ImageType.IMAGE_JPEG);
                result = save ? "Image Saved" : "Image Not Saved";
                // Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            } catch (WriterException e) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d(TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
            Toast.makeText(getActivity(), "Downloaded Successfully", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }

    private File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getActivity().getPackageName()
                + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        //String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName = "GL_" + code + ".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }
}
