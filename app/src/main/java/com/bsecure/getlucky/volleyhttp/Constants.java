package com.bsecure.getlucky.volleyhttp;

/**
 * Created by prudhvi on 2018-06-19.
 */

public class Constants {
    public static final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public static final int SUCCESS_RESULT = 0;
    public static final int SUCCESS_RESULT_1 = 2;
    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME =
            "com.bsecure.getlucky.locationaddress";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME +
            ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
            ".LOCATION_DATA_EXTRA";
    public String SHA1="9A:54:8A:6C:B9:1C:19:1E:0D:DF:66:42:B9:3D:D9:05:91:40:F9:EA";
    public static String key="AIzaSyCSgCTTAPef2MpDVXjvzmXj37Db97WJmjs";
    //public static String key="AIzaSyCvdgdoCZc4bkufNsTKmaKGRw3egMIn_cs";

    /*
    * @ Rest api call
    *
    * */
    public static final String PATH = "https://getlucky.in/api/";
    public static final String g_location="https://maps.googleapis.com/maps/api/place/autocomplete/json?input=(INPUT)&types(TYPE)&sensor=(SENSOR)&key=(KEY)";
    public static final String g_location_two="https://maps.googleapis.com/maps/api/place/photo?maxwidth=800&photoreference=%s&sensor=false&key=AIzaSyCvdgdoCZc4bkufNsTKmaKGRw3egMIn_cs";
}
