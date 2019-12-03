package com.bsecure.getlucky.volleyhttp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bsecure.getlucky.R;
import com.bsecure.getlucky.interfaces.MethodHandler;
import com.bsecure.getlucky.interfaces.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * Created by prudhvi on 2018-05-02.
 */

public class MethodResquest_GET implements MethodHandler {

    private Context context;

    private RequestHandler requestHandler;

    private String message;

    private int reqId = -1;

    private int typeError = -1;

    private static Dialog dialog = null;

    private JSONObject json = null;

    private String networkType = "mobile";

    private String request_url = null;

    public MethodResquest_GET(final Context context, final RequestHandler requestHandler, String url,int request) {
        this.context = context;
        this.requestHandler = requestHandler;
        this.reqId = request;
        this.request_url = url;

        requestHandler.requestStarted();
        if (isNetworkAvailable()) {
            showProgress(message, context);

        } else {
            message = "Cannot connect to Internet...Please check your connection!";
            typeError = 1;
        }
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dismissProgress(context);
                        requestHandler.requestCompleted(response, reqId);

                        Log.e("RequestURL:::",request_url);
                        Log.e("Response:::",response.toString());
                        setLogsFiles(request_url, json, response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NetworkError) {

                    message = "Cannot connect to Internet...Please check your connection!";
                   // showProgress(message, context);
                    typeError = 1;
                    dismissProgress(context);
                } else if (error instanceof ServerError) {
                    typeError = 2;
                    message = "The server could not be found. Please try again after some time!!";
                   // showProgress(message, context);
                    dismissProgress(context);
                } else if (error instanceof AuthFailureError) {
                    typeError = 3;
                    message = "Cannot connect to Internet...Please check your connection!";
                    dismissProgress(context);
                   // showProgress(message, context);
                } else if (error instanceof ParseError) {
                    typeError = 4;
                    message = "No Data Found";
                    dismissProgress(context);
                    //showProgress(message, context);
                } else if (error instanceof NoConnectionError) {
                    typeError = 5;
                    message = "Cannot connect to Internet...Please check your connection!";
                    dismissProgress(context);
                   // showProgress(message, context);
                } else if (error instanceof TimeoutError) {
                    typeError = 6;
                    message = "TimeOut! Please check your internet connection.";
                    dismissProgress(context);
                    //showProgress(message, context);
                }
                requestHandler.requestEndedWithError(message, typeError);
            }

        }) {

            /** Passing some request headers* */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap();
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };

        queue.add(jsonObjectRequest).setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 12000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 0; //retry turn off
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
    }

    public static void showProgress(String title, final Context context) {
        try {

            dialog = new Dialog(context);

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            if (dialog.getWindow() != null) {

                dialog.getWindow().setBackgroundDrawable(
                        new ColorDrawable(Color.TRANSPARENT));

            }

            dialog.setCancelable(false);

            View view = View.inflate(context, R.layout.loading, null);

            dialog.setContentView(view);

            dialog.show();

        } catch (Exception e) {
           e.printStackTrace();
        }

    }

    public static void dismissProgress(Context context) {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
        dialog = null;

    }

    private boolean isNetworkAvailable() {

        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (manager == null) {

            return false;

        }

        NetworkInfo net = manager.getActiveNetworkInfo();

        if (net != null) {

            networkType = net.getTypeName();

            return net.isConnected();

        }

        return false;

    }

    private void setLogsFiles(String request, JSONObject postdata, JSONObject response) {

        try {

            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(context.getExternalFilesDir(null).getAbsolutePath() + "/getLucky.log", true)));
            pw.append(getCustomSystemTime());

            pw.append("Reqest URL:");
            pw.append(request);

            pw.append("\n");

            pw.append("Req Body:");
            pw.append(postdata.toString());

            pw.append("\n");

            pw.append("Response:");
            pw.append(response.toString());

            pw.append("\n");

            pw.append("------------------------------\n");

            pw.flush();

            pw.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private static String getCustomSystemTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd:MMM:yyy hh:mm:ss", Locale.ENGLISH);
        java.util.Date date = new java.util.Date();
        return "-----" + dateFormat.format(date) + "-----\n";
    }
}
