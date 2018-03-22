package com.example.kpraveen.android5;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    private WebView mWebView;

    String userName;

    private void postLocation(Location location) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = "http://iot.ajithv.in/api/Locations";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("latitude", location.getLatitude());
            jsonBody.put("longitude", location.getLongitude());
            String temp = userName;
            if((temp == null) || (temp.isEmpty())) {
                temp = "test23";
            }
            jsonBody.put("name", userName);
            long time = System.currentTimeMillis()/1000;
            jsonBody.put("time", time);
            jsonBody.put("type", "mobile");
            jsonBody.put("accuracy", location.getAccuracy());
            final String mRequestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("LOG_VOLLEY", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("LOG_VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        Log.d("app", "Unsupported Encoding ");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {

                        responseString = String.valueOf(response.statusCode);

                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mWebView.loadUrl("http://iot.ajithv.in");
                    return true;
                case R.id.navigation_dashboard:
                    mWebView.loadUrl("http://iot.ajithv.in/map.html");
                    return true;
                case R.id.navigation_settings:
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivityForResult(intent, 1);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        userName = sharedPref.getString("userName", "");
        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        Log.d("mainactivityOnCreate", "userName " + userName);
        mWebView.loadUrl("http://iot.ajithv.in");
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener ll = new mylocationlistener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
           Log.d("pkg", "permission not given for location");
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Toast.makeText(MainActivity.this, "Requesting Location Update", Toast.LENGTH_LONG).show();
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 0, ll);
    }

    private class mylocationlistener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                Log.d("LOCATION CHANGED", location.getLatitude() + "");
                Log.d("LOCATION CHANGED", location.getLongitude() + "");
                Toast.makeText(MainActivity.this,
                        location.getLatitude() + "" + location.getLongitude(),
                        Toast.LENGTH_LONG).show();
                postLocation(location);
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

}
