package com.bsecure.getlucky;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bsecure.getlucky.fragments.HomeFragment;
import com.bsecure.getlucky.fragments.ParentFragment;
import com.bsecure.getlucky.utils.TraceUtils;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONObject;

import java.util.Stack;

public class GetLucky extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ParentFragment.OnFragmentInteractionListener, View.OnClickListener {

    private Toolbar toolbar;

    private ActionBarDrawerToggle toggle;

    private DrawerLayout drawer;

    private FragmentManager manager = null;

    private Stack<ParentFragment> fragStack = null;

    ParentFragment tempFrag = null;


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

                onKeyDown(4, null);

            }

        });

        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        ((TextView) header.findViewById(R.id.mobile_no)).setText("9100239922");
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
        int id = item.getItemId();

        if (id == R.id.nav_login) {
            Intent login = new Intent(this, Login.class);
            startActivity(login);

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
                return false;

            }
        } catch (Exception e) {
            TraceUtils.logException(e);
        }

        return super.onKeyDown(keyCode, event);
    }

}
