package com.bsecure.getlucky.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bsecure.getlucky.GetLucky;
import com.bsecure.getlucky.R;

import org.json.JSONObject;

public class HomeFragment extends ParentFragment {

    private GetLucky getLucky;
    private OnListFragmentInteractionListener mListener;
    private OnFragmentInteractionListener mFragListener;
    public HomeFragment() {

    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_dashboard, container, false);

        return view;

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mListener = (OnListFragmentInteractionListener) context;

        mFragListener = (GetLucky) context;

        getLucky = (GetLucky) context;

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mFragListener = null;
    }

    public interface OnListFragmentInteractionListener {

        void onListFragmentInteraction(JSONObject item);

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        mFragListener.onFragmentInteraction(R.string.app_name, true);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mFragListener.onFragmentInteraction(R.string.app_name, true);

    }

}