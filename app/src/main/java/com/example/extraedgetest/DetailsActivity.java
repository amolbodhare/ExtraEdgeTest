package com.example.extraedgetest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailsActivity extends AppCompatActivity {


    private View fragMentView;
    private static RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    DetailsAdapter apiListAdapter;
    Context context;
    JSONArray jsonArray;
    String id;
    TextView nameTv,activeStatusTv,costPerLaunch,successRateTv,descTv,wikipediaLinkTv,heightTv,diameterTv;

    // json object response url
    private String urlJsonObj = "https://api.spacexdata.com/v4/rockets";

    private static String TAG = DetailsActivity.class.getSimpleName();
    // private Button btnMakeObjectRequest, btnMakeArrayRequest;

    // Progress dialog
    private ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        context= DetailsActivity.this;
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        jsonArray=new JSONArray();
        Intent i=getIntent();
        id=i.getStringExtra("id");
        nameTv=findViewById(R.id.nameTv);
        activeStatusTv=findViewById(R.id.activeStatusTv);
        costPerLaunch=findViewById(R.id.costPerLaunchTv);
        successRateTv=findViewById(R.id.successRateTv);
        descTv=findViewById(R.id.descriptionTv);
        wikipediaLinkTv=findViewById(R.id.wikipediaLinkTv);
        heightTv=findViewById(R.id.heightTv);
        diameterTv=findViewById(R.id.diameterTv);
        setTitle("Rocket Details");

        recyclerView = findViewById(R.id.eventDetailsRecyclerView);

        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        GridLayoutManager  mLayoutManager = new GridLayoutManager(this,
                2, GridLayoutManager.HORIZONTAL,true);

        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(
                        2, //The number of Columns in the grid
                        LinearLayoutManager.HORIZONTAL);
        StaggeredGridLayoutManager staggeredGridLayoutManagerr = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManagerr);
        //recyclerView.setLayoutManager(layoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        if (isInternetAvailable(context))
            makeJsonObjectRequest();
        else
            Toast.makeText(context, "Internet is not available", Toast.LENGTH_SHORT).show();
    }
    private void makeJsonObjectRequest() {

        showpDialog();

        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj+"/"+id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());


                // Parsing json object response
                // response will be a json object
                // jsonArray=response.getJSONArray("feed");

                try {
                    descTv.setSelected(true);
                    wikipediaLinkTv.setSelected(true);
                    nameTv.setText("Name : "+response.getString("name"));
                    activeStatusTv.setText("Active Status :  "+response.getString("active"));
                    costPerLaunch.setText("Cost Per Launch : "+response.getString("cost_per_launch"));
                    successRateTv.setText("Success Rate : "+response.getString("success_rate_pct")+"%");
                    descTv.setText("Description : "+response.getString("description"));
                    wikipediaLinkTv.setText("Wikipedia Link : "+response.getString("wikipedia"));
                    heightTv.setText("Height : "+response.getJSONObject("height").getString("feet")+"feet");
                    diameterTv.setText("Diameter : "+response.getJSONObject("diameter").getString("feet")+"feet");

                    JSONArray imageArray= response.getJSONArray("flickr_images");
                    apiListAdapter=null;
                    apiListAdapter = new DetailsAdapter(context,imageArray);
                    recyclerView.setAdapter(apiListAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }



                hidepDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                /*Toast.makeText(context,
                        error.getMessage(), Toast.LENGTH_SHORT).show();*/
                // hide the progress dialog
                hidepDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonArrayRequest);
    }
    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
    public static boolean isInternetAvailable(Context context) {
        boolean connected = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            // connected to the internet
            switch (activeNetwork.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    // connected to wifi
                    connected = true;
                    break;

                case ConnectivityManager.TYPE_MOBILE:
                    // connected to mobile data
                    connected = true;
                    break;

                default:
                    connected = false;
            }
        } else {
            // not connected to the internet
            connected = false;
        }

        return connected;
    }
}