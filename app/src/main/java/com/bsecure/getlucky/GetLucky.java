package com.bsecure.getlucky;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bsecure.getlucky.barcode.Mybarcode;
import com.bsecure.getlucky.common.AppPreferences;
import com.bsecure.getlucky.fragments.HomeFragment;
import com.bsecure.getlucky.fragments.ParentFragment;
import com.bsecure.getlucky.operator.OperatorLogin;
import com.bsecure.getlucky.store.AddStore;
import com.bsecure.getlucky.store.ViewStoresList;
import com.bsecure.getlucky.utils.TraceUtils;
import com.bsecure.getlucky.wallet.ViewWallet;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Stack;

import afu.org.checkerframework.checker.nullness.qual.NonNull;

public class GetLucky extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ParentFragment.OnFragmentInteractionListener, View.OnClickListener {

    private Toolbar toolbar;

    private ActionBarDrawerToggle toggle;

    private DrawerLayout drawer;

    private FragmentManager manager = null;

    private Stack<ParentFragment> fragStack = null;

    private ParentFragment tempFrag = null;

    private String session_data = null;

    private NavigationView navigationView;

    private Dialog referDialog;

    private JSONArray ayArray;

    private View header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("");

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }


        drawer = findViewById(R.id.drawer_layout);

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);

        toggle.syncState();

        fragStack = new Stack<>();

        manager = getSupportFragmentManager();

        if (savedInstanceState != null) {

            removeAllFragments();

        }

        swiftFragments(HomeFragment.newInstance(), "home");

        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportInvalidateOptionsMenu();
                onKeyDown(4, null);

            }

        });

        navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        header = navigationView.getHeaderView(0);

        getprofileData();

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }
                        if (deepLink != null
                                && deepLink.getBooleanQueryParameter("invitedby", false)) {
                            String referrerUid = deepLink.getQueryParameter("invitedby");
                            Log.e("invite", referrerUid);
                            AppPreferences.getInstance(GetLucky.this).addToStore("refer", referrerUid, true);
                        } else {
                            AppPreferences.getInstance(GetLucky.this).addToStore("refer", "", true);
                        }
                    }
                });
    }

    private void getprofileData() {
        session_data = AppPreferences.getInstance(this).getFromStore("userData");
        if (session_data != null && !TextUtils.isEmpty(session_data)) {
            try {
                ayArray = new JSONArray(session_data);
                ImageView profile = (ImageView) header.findViewById(R.id.tv_profileicon);
                Glide.with(this).load(ayArray.getJSONObject(0).optString("profile_image")).into(profile);
                ((TextView) header.findViewById(R.id.mobile_no)).setText(ayArray.getJSONObject(0).optString("name"));
                ((TextView) header.findViewById(R.id.refer_code)).setVisibility(View.VISIBLE);
                ((TextView) header.findViewById(R.id.refer_code)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getShareRefer();
                    }
                });
                AppPreferences.getInstance(this).addToStore("customer_id", ayArray.getJSONObject(0).optString("customer_id"), true);
                ((TextView) header.findViewById(R.id.refer_code)).setText("Referral Code - " + ayArray.getJSONObject(0).optString("customer_referral_code"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            ((TextView) header.findViewById(R.id.mobile_no)).setText("Unknown");
        }
    }

    private void getShareRefer() {

        try {
            referDialog = new Dialog(this, R.style.Theme_MaterialComponents_Light_BottomSheetDialog);
            referDialog.setContentView(R.layout.share_dialog);
            referDialog.setCancelable(true);
            referDialog.setCanceledOnTouchOutside(true);
            referDialog.show();
            ((TextView) referDialog.findViewById(R.id.share_code)).setText(ayArray.getJSONObject(0).optString("customer_referral_code"));
            referDialog.findViewById(R.id.share_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getShareLink();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        try {
            getprofileData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    private void getShareLink() {

        try {

            DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setLink(Uri.parse("https://bsecuresoftechsolutions.com/"))
                    .setDomainUriPrefix("https://bsecure.page.link")
                    // Open links with this app on Android
                    .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                    // Open links with com.example.ios on iOS
                    .setIosParameters(new DynamicLink.IosParameters.Builder("com.aaria.farmer")
                            .setAppStoreId("1482252408")
                            .setMinimumVersion("1.0")
                            .build())
                    .buildDynamicLink();
            //click -- link -- google play store -- inistalled/ or not  ----
            Uri dynamicLinkUri = dynamicLink.getUri();
            String sharelinktext = "https://bsecure.page.link/?" +
                    "link=https://bsecuresoftechsolutions.com/?invitedby=" + ayArray.getJSONObject(0).optString("customer_referral_code") +
                    "&apn=" + getPackageName() +
                    "&isi=" + "1482252408" +
                    "&ibi=" + "ios package" +
                    "&st=" + getString(R.string.app_name) +
                    "&sd=" + "Referral Code : " + ayArray.getJSONObject(0).optString("customer_referral_code") +
                    "&si=" + "";
            // invitation?referrer="+ UserSession.getFarmerId()

            FirebaseDynamicLinks.getInstance().createDynamicLink()
                    //.setLongLink(dynamicLink.getUri())
                    .setLongLink(Uri.parse(sharelinktext))  // manually
                    .buildShortDynamicLink()
                    .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                        @Override
                        public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                            if (task.isSuccessful()) {
                                referDialog.dismiss();
                                // Short link created
                                Uri shortLink = task.getResult().getShortLink();
                                Uri flowchartLink = task.getResult().getPreviewLink();
                                Log.e("main ", "short link " + shortLink.toString());
                                // share app dialog

                                // String msgHtml = String.format("Hi,I'm using Kheti buddy - Farm Use my referrer link : ", );
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_SEND);
                                intent.putExtra(Intent.EXTRA_TEXT, shortLink.toString());
                                intent.setType("text/plain");
                                startActivity(intent);
                            } else {
                                // Error
                                // ...
                                Log.e("main", " error " + task.getException());
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.activity_getlucky_drawer, menu);
        Menu nav_Menu = navigationView.getMenu();
        if (session_data != null && !TextUtils.isEmpty(session_data)) {
            nav_Menu.findItem(R.id.nav_profile).setVisible(true);
            nav_Menu.findItem(R.id.nav_login).setVisible(false);
            nav_Menu.findItem(R.id.nav_store).setVisible(false);
            nav_Menu.findItem(R.id.nav_view_store).setVisible(true);
            nav_Menu.findItem(R.id.nav_bar_code).setVisible(true);
            nav_Menu.findItem(R.id.nav_operator).setVisible(false);
            nav_Menu.findItem(R.id.nav_wallet).setVisible(true);
        } else {
            nav_Menu.findItem(R.id.nav_profile).setVisible(false);
            nav_Menu.findItem(R.id.nav_login).setVisible(true);
            nav_Menu.findItem(R.id.nav_operator).setVisible(true);
            nav_Menu.findItem(R.id.nav_store).setVisible(false);
            nav_Menu.findItem(R.id.nav_view_store).setVisible(false);
            nav_Menu.findItem(R.id.nav_bar_code).setVisible(false);
            nav_Menu.findItem(R.id.nav_wallet).setVisible(false);
        }
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        //int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.nav_login:
                Intent login = new Intent(this, Login.class);
                startActivity(login);
                overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
                break;
            case R.id.nav_profile:

                Intent prof = new Intent(this, ProfilePage.class);
                startActivity(prof);
                overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
                break;
            case R.id.nav_store:

                Intent store = new Intent(this, AddStore.class);
                startActivity(store);
                overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
                break;

            case R.id.nav_view_store:

                Intent nav_view_store = new Intent(this, ViewStoresList.class);
                startActivity(nav_view_store);
                overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
                break;
            case R.id.nav_bar_code:

                Intent code = new Intent(this, Mybarcode.class);
                startActivity(code);
                overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
                break;

            case R.id.nav_operator:

                Intent op_log = new Intent(this, OperatorLogin.class);
                startActivity(op_log);
                overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
                break;

            case R.id.nav_wallet:

                Intent nav_wallet = new Intent(this, ViewWallet.class);
                startActivity(nav_wallet);
                overridePendingTransition(R.anim.fade_in_anim, R.anim.fade_out_anim);
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        drawer.closeDrawer(GravityCompat.START);

        return true;

    }

    @Override
    public void onFragmentInteraction(int stringid, boolean blockMenu) {

        toolbar.setTitle(stringid);

        lockUnLockMenu(blockMenu);

    }

    @Override
    public void onFragmentInteraction(String title, boolean blockMenu) {

        toolbar.setTitle(title);

        lockUnLockMenu(blockMenu);

    }

    @Override
    public void onFragmentInteraction(JSONObject jsonObject) {

    }

    public void lockUnLockMenu(boolean mode) {

        toggle.setDrawerIndicatorEnabled(mode);

        drawer.setDrawerLockMode(mode ? DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

    }

    @Override
    public void onListFragmentInteraction(JSONObject jsonObject) {

        try {

            int position = jsonObject.getInt("position");

            switch (position) {

                case 1:
                    break;

            }

        } catch (Exception e) {

            TraceUtils.logException(e);

        }

    }

    @Override
    public void onClick(View v) {

    }


    public void swiftFragments(ParentFragment frag, String tag) {

        FragmentTransaction trans = manager.beginTransaction();

        tempFrag = (ParentFragment) manager.findFragmentByTag(tag);
        if (tempFrag != null) {

            if (tempFrag.isAdded() && tempFrag.isVisible())
                return;
            else if (tempFrag.isAdded() && tempFrag.isHidden()) {

                trans.hide(fragStack.get(fragStack.size() - 1));
                trans.show(tempFrag);

                fragStack.remove(tempFrag);
                fragStack.add(tempFrag);

            }

        } else if (!frag.isAdded()) {

            if (fragStack.size() > 0) {
                ParentFragment pf = fragStack.get(fragStack.size() - 1);
                trans.hide(pf);
            }

            trans.add(R.id.container, frag, tag);
            trans.show(frag);

            fragStack.push(frag);
        }

        try {

            if (LuckyApp.isInterestingActivityVisible()) {
                trans.commit();
            } else {
                trans.commitAllowingStateLoss();
            }

        } catch (IllegalStateException e) {
            TraceUtils.logException(e);
        }
    }

    private void removeAllFragments() {

        FragmentTransaction trans = manager.beginTransaction();

        for (int i = 0; i < manager.getFragments().size(); i++) {

            trans.remove(manager.getFragments().get(i));

        }

        trans.commitAllowingStateLoss();

        trans = null;

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            if (keyCode == 4) {

                if (fragStack.size() > 1) {

                    ParentFragment pf = fragStack.peek();

                    if (pf.back())
                        return true;

                    fragStack.pop();

                    FragmentTransaction trans = manager.beginTransaction();
                    trans.remove(pf);

                    if (fragStack.size() > 0) {
                        ParentFragment pf1 = fragStack.get(fragStack.size() - 1);
                        trans.show(pf1);
                    }

                    trans.commit();

                    return true;
                }
                //return false;

            }
        } catch (Exception e) {
            TraceUtils.logException(e);
        }

        return super.onKeyDown(keyCode, event);
    }

}
