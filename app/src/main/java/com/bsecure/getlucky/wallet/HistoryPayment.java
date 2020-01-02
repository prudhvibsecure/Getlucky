package com.bsecure.getlucky.wallet;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bsecure.getlucky.R;
import com.bsecure.getlucky.common.AppPreferences;
import com.bsecure.getlucky.volleyhttp.Constants;
import com.github.ybq.android.spinkit.SpinKitView;

import org.json.JSONArray;
import org.json.JSONException;

public class HistoryPayment extends AppCompatActivity {
    private WebView wv_history;
    private SpinKitView spin_kit;
    JSONArray ayArray;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_web);
        wv_history = findViewById(R.id.wv_web);
        spin_kit = findViewById(R.id.spin_kit);
        findViewById(R.id.bacl_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overridePendingTransition(R.anim.fade_out_anim, R.anim.fade_in_anim);
                finish();
            }
        });
        String session_data = AppPreferences.getInstance(this).getFromStore("userData");
        if (session_data != null && !TextUtils.isEmpty(session_data)) {
            try {
                ayArray = new JSONArray(session_data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        wv_history.setVerticalScrollBarEnabled(true);

        wv_history.getSettings().setJavaScriptEnabled(true);

        wv_history.getSettings().setSupportZoom(false);

        wv_history.setWebViewClient(new MyWebViewClient());

        wv_history.setWebChromeClient(new WebChromeClient());

        wv_history.getSettings().setLoadWithOverviewMode(true);

        wv_history.getSettings().setUseWideViewPort(false);

        wv_history.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

        wv_history.getSettings().setSupportZoom(true);

        wv_history.getSettings().setBuiltInZoomControls(true);

        wv_history.getSettings().setDisplayZoomControls(false);

        try {
            wv_history.loadUrl(Constants.PATH + "history?customer_id=" + ayArray.getJSONObject(0).optString("customer_id"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        wv_history.clearFormData();

        wv_history.clearCache(true);

        wv_history.clearHistory();
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {

            spin_kit.setVisibility(View.GONE);

        }


        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);

            spin_kit.setVisibility(View.GONE);

        }

    }

    private class WebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {

            if (newProgress >= 1)
                spin_kit.setVisibility(View.VISIBLE);

            if (newProgress >= 95) {
                spin_kit.setVisibility(View.GONE);
            }

            super.onProgressChanged(view, newProgress);
        }
    }

}
