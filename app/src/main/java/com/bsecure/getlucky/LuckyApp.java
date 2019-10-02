package com.bsecure.getlucky;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

public class LuckyApp extends MultiDexApplication implements Application.ActivityLifecycleCallbacks {

    private static boolean isInterestingActivityVisible;

    private static LuckyApp instance;

    public LuckyApp() {
        super();
        instance = this;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

            instance = this;

    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (activity instanceof GetLucky) {
            isInterestingActivityVisible = true;
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (activity instanceof GetLucky) {
            isInterestingActivityVisible = false;
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    public static boolean isInterestingActivityVisible() {
        return isInterestingActivityVisible;
    }

}
