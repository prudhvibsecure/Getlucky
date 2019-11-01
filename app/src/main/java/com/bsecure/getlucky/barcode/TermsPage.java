package com.bsecure.getlucky.barcode;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bsecure.getlucky.R;

public class TermsPage extends AppCompatActivity {
    private WebView wv_content = null;
    private WebSettings webSettings = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terms_page);


        wv_content =  findViewById(R.id.web_vv);
        wv_content.getSettings().setAllowFileAccess(true);
        wv_content.getSettings().setSupportZoom(true);
        wv_content.setVerticalScrollBarEnabled(true);
        wv_content.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wv_content.getSettings().setLoadWithOverviewMode(true);
        wv_content.getSettings().setUseWideViewPort(true);
        wv_content.getSettings().setJavaScriptEnabled(true);

        wv_content.getSettings().setSaveFormData(false);
        wv_content.getSettings().setSavePassword(false);

        wv_content.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        wv_content.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        wv_content.setWebViewClient(new MyWebViewClient());
        wv_content.setWebChromeClient(new MyWebChromeClient());

        wv_content.getSettings().setJavaScriptEnabled(true);
        wv_content.getSettings().setLoadWithOverviewMode(true);
        wv_content.getSettings().setUseWideViewPort(true);

        webSettings = wv_content.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //wv_content.addJavascriptInterface(jsInterface, "jsInterface");
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDefaultFontSize(50);

        wv_content.loadUrl("http://bsecuresoftechsolutions.com/get-lucky/termsandconditions");

    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {


            if (newProgress == 5)
                findViewById(R.id.spin_kit).setVisibility(View.VISIBLE);

            if (newProgress >= 95) {
                findViewById(R.id.spin_kit).setVisibility(View.GONE);
            }
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            return super.onJsAlert(view, url, message, result);
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            findViewById(R.id.spin_kit).setVisibility(View.GONE);
        }
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }
    }
}
