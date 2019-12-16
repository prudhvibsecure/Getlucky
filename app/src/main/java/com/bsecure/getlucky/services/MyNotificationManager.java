package com.bsecure.getlucky.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.Html;


import androidx.core.app.NotificationCompat;

import com.bsecure.getlucky.R;
import com.bsecure.getlucky.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;


/**
 * Created by Admin on 2018-09-27.
 */

public class MyNotificationManager {
    public static final int ID_BIG_NOTIFICATION = 234;
    public static final int ID_SMALL_NOTIFICATION = 235;
    private static final String CHANNEL_NAME = "Majic Chat";
    private Context mCtx;
    private static int value = 0;
    String GROUP_KEY_WORK_EMAIL = "com.bsecure.majicchat.WORK_EMAIL";
    int SUMMARY_ID = 0;
    Bitmap my_url;

    public MyNotificationManager(Context mCtx) {
        this.mCtx = mCtx;
    }

    //the method will show a big notification with an image
    //parameters are title for message title, message for message text, url of the big image and an intent that will open
    //when you will tap on the notification
    public void showBigNotification(String title, String message, String url, Intent intent) {
        if (url != null) {
            SUMMARY_ID++;
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            mCtx,
                            ID_BIG_NOTIFICATION,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
            bigPictureStyle.setBigContentTitle(title);
            bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());

            String kk_url=new File(url).getName();
            String mType= Utils.getMimeType(kk_url);
            if (mType.startsWith("video/")||mType.startsWith("audio/")) {
                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(url, new HashMap<String, String>());
                my_url = mediaMetadataRetriever.getFrameAtTime(1000);
            } else {
                my_url = getBitmapFromURL(url);
            }
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mCtx, mCtx.getString(R.string.notification_channel_id));
            Notification notification;
            notification = mBuilder.setSmallIcon(R.mipmap.ic_launcher).setTicker(title)
                    .setAutoCancel(true)
                    .setContentIntent(resultPendingIntent)
                    .setContentTitle(title)
                    .setStyle(bigPictureStyle.bigPicture(my_url))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(mCtx.getResources(), R.mipmap.ic_launcher))
                    .setContentText(message)
                    .setGroup(GROUP_KEY_WORK_EMAIL)
                    .setGroupSummary(true)
                    .setSound(uri)
                    .build();

            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                NotificationChannel channel = new NotificationChannel(
                        mCtx.getString(R.string.notification_channel_id), CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
                );
                channel.setDescription(title);
                channel.setShowBadge(true);
                channel.canShowBadge();
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});

                assert notificationManager != null;
                notificationManager.createNotificationChannel(channel);

                // notificationManager.notify(ID_BIG_NOTIFICATION, notification);
            }
            assert notificationManager != null;
            notificationManager.notify(SUMMARY_ID, notification);

            //notificationManager.notify(ID_BIG_NOTIFICATION, notification);
        }
    }

    //the method will show a small notification
    //parameters are title for message title, message for message text and an intent that will open
    //when you will tap on the notification
    public void showSmallNotification(String title, String message, Intent intent) {
        SUMMARY_ID++;
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mCtx,
                        ID_SMALL_NOTIFICATION,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );


        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mCtx, mCtx.getString(R.string.notification_channel_id));
        Notification notification;
       /* if (type_action.equalsIgnoreCase("SAM")) {
            PendingIntent my_act=PendingIntent.getBroadcast(mCtx,0,intent, PendingIntent.FLAG_UPDATE_CURRENT);
            notification = mBuilder.setSmallIcon(R.mipmap.ic_launcher).setTicker(title)
                    .setAutoCancel(true)
                    .setContentIntent(resultPendingIntent)
                    .setContentTitle(title)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(mCtx.getResources(), R.mipmap.ic_launcher))
                    .setContentText(message)
                    .addAction(R.drawable.accept,"Accept",my_act)
                    .addAction(R.mipmap.inactive,"Decline",my_act)
                    .addAction(R.drawable.pending,"Pending", my_act)
                    .setSound(uri)
                    .build();
        }else{*/
        notification = mBuilder.setSmallIcon(R.mipmap.ic_launcher).setTicker(title)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(mCtx.getResources(), R.mipmap.ic_launcher))
                .setContentText(message)
                .setStyle(new NotificationCompat.InboxStyle()
                        .addLine(message)
                        .setSummaryText(String.valueOf(SUMMARY_ID)))
                .setGroup(GROUP_KEY_WORK_EMAIL)
                .setGroupSummary(true)
                .setSound(uri)
                .build();
        // }

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(ID_SMALL_NOTIFICATION, notification);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(
                    mCtx.getString(R.string.notification_channel_id), CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(title);
            channel.setShowBadge(true);
            channel.canShowBadge();
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});

            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);

            // notificationManager.notify(ID_BIG_NOTIFICATION, notification);
        }
        assert notificationManager != null;
        //notificationManager.notify(ID_BIG_NOTIFICATION, notification);
        notificationManager.notify(SUMMARY_ID, notification);
    }

    //The method will return Bitmap from an image URL
    private Bitmap getBitmapFromURL(String strURL) {
        Bitmap myBitmap = null;
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            myBitmap = BitmapFactory.decodeStream(input);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return myBitmap;
    }
}