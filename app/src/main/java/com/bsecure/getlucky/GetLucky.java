package com.bsecure.getlucky;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.bsecure.getlucky.store.AddEditStore;
import com.bsecure.getlucky.utils.TraceUtils;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Stack;

public class GetLucky extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ParentFragment.OnFragmentInteractionListener, View.OnClickListener {

    private Toolbar toolbar;

    private ActionBarDrawerToggle toggle;

    private DrawerLayout drawer;

    private FragmentManager manager = null;

    private Stack<ParentFragment> fragStack = null;

    private ParentFragment tempFrag = null;

    private String session_data = null;

    private NavigationView navigationView;

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
        session_data = AppPreferences.getInstance(this).getFromStore("userData");
        swiftFragments(HomeFragment.newInstance(), "home");

        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onKeyDown(4, null);

            }

        });

        navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        if (session_data != null && !TextUtils.isEmpty(session_data)) {
            try {
                JSONArray ayArray = new JSONArray(session_data);
                ImageView profile = (ImageView) header.findViewById(R.id.tv_profileicon);
                Glide.with(this).load(ayArray.getJSONObject(0).optString("profile_image")).into(profile);
                ((TextView) header.findViewById(R.id.mobile_no)).setText(ayArray.getJSONObject(0).optString("name"));
                ((TextView) header.findViewById(R.id.refer_code)).setVisibility(View.VISIBLE);
                ((TextView) header.findViewById(R.id.refer_code)).setText("Referral Code - " + AppPreferences.getInstance(this).getFromStore("customer_referral_code"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            ((TextView) header.findViewById(R.id.mobile_no)).setText("Unknown");
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
        getMenuInflater().inflate(R.menu.activity_getlucky_drawer, menu);
        Menu nav_Menu = navigationView.getMenu();
        if (session_data != null && !TextUtils.isEmpty(session_data)) {
            nav_Menu.findItem(R.id.nav_profile).setVisible(true);
            nav_Menu.findItem(R.id.nav_login).setVisible(false);
            nav_Menu.findItem(R.id.nav_store).setVisible(true);
            nav_Menu.findItem(R.id.nav_view_store).setVisible(true);
            nav_Menu.findItem(R.id.nav_bar_code).setVisible(true);
        } else {
            nav_Menu.findItem(R.id.nav_profile).setVisible(false);
            nav_Menu.findItem(R.id.nav_login).setVisible(true);
            nav_Menu.findItem(R.id.nav_store).setVisible(false);
            nav_Menu.findItem(R.id.nav_view_store).setVisible(false);
            nav_Menu.findItem(R.id.nav_bar_code).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.phoneno) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                break;
            case R.id.nav_profile:

                Intent prof = new Intent(this, ProfilePage.class);
                startActivity(prof);
                break;
            case R.id.nav_store:

                Intent store = new Intent(this, AddEditStore.class);
                startActivity(store);
                break;
            case R.id.nav_bar_code:

                Intent code = new Intent(this, Mybarcode.class);
                startActivity(code);
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
