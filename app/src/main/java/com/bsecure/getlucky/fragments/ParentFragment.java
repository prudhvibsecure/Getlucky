package com.bsecure.getlucky.fragments;


import androidx.fragment.app.Fragment;

import com.bsecure.getlucky.R;

import org.json.JSONObject;


public class ParentFragment extends Fragment {

    public boolean back() {
        return false;
    }

    public String getFragmentName() {
        return getString(R.string.app_name);
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(int stringid, boolean blockMenu);

        void onFragmentInteraction(String title, boolean blockMenu);

        void onFragmentInteraction(JSONObject jsonObject);

        void onListFragmentInteraction(JSONObject jsonObject);
    }

    public void onRequestPermissionsResult(int requestCode, boolean permissionState) {

    }

}
