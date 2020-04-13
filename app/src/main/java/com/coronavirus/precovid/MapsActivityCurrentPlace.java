package com.coronavirus.precovid;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import static java.lang.String.valueOf;

/**
 * An activity that displays a map showing the place at the device's current location.
 */
public class MapsActivityCurrentPlace extends FragmentActivity
        implements OnMapReadyCallback {
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
    private boolean flag = false;

    private static final String TAG = MapsActivityCurrentPlace.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    // The entry point to the Places API.
    private PlacesClient mPlacesClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private MarkerOptions marker_glob= new MarkerOptions().position(new LatLng(0,0)).title("Home Location").draggable(true);;
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    private boolean handwash_flag=false;
    private static Context context;
    private Location glob_Location=new Location("service Provider");;
    private boolean contact_flag=false;
    private boolean toggle=true;
    private boolean toggle_aqi=true;

    private boolean toggle_info=true;
    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 5;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    private List[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;
    private LatLng pt1 = new LatLng(0, 0);
    private String email;
    private boolean camera_flag=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ProgressDialog nDialog;
//        nDialog = new ProgressDialog(MapsActivityCurrentPlace.this,R.style.dialog);
//        nDialog.setMessage("Loading..");
//
//        nDialog.setTitle("Get Data");
//        nDialog.setIndeterminate(false);
//        nDialog.setCancelable(true);
//        Handler handler = new Handler();
//        nDialog.show();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                nDialog.dismiss();
//            }},3000);


    glob_Location=new Location("service Provider");;
        MapsActivityCurrentPlace.context = getApplicationContext();
        Bundle bundle = getIntent().getExtras();
        Intent i = getIntent();
        SharedPreferences shared = getSharedPreferences("Preferences", MODE_PRIVATE);
        String channel = (shared.getString("LOGIN", ""));
        String s = i.getStringExtra("Username");
        Log.d("msg", "Email is  " + s);
        email = channel;
        pt1 = new LatLng(0, 0);;
        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
//            glob_Location=mLastKnownLocation;
//            glob_Location.setLatitude(mLastKnownLocation.getLatitude());
//            glob_Location.setLongitude(mLastKnownLocation.getLongitude());
                    mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        setContentView(R.layout.activity_maps);
        MaterialButton btn_news=(MaterialButton) findViewById(R.id.button_news);
        btn_news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Coronanews.class);

                startActivity(intent);

            }
        });
        MaterialButton btn_dist=(MaterialButton) findViewById(R.id.button_dist);
        btn_dist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(toggle)
                {
                    toggle=false;
                    findViewById(R.id.map).setAlpha(0.5f);
                    findViewById(R.id.textview).bringToFront();
                    findViewById(R.id.textview).animate().alpha(1.0f);

//                    findViewById(R.id.textview).setVisibility(View.VISIBLE);
                }
                else
                {
                    toggle=true;
                    findViewById(R.id.textview).animate().alpha(0.0f);
                    if(toggle_aqi && toggle && toggle_info)
                    findViewById(R.id.map).setAlpha(1.0f);
//                    findViewById(R.id.textview).setVisibility(View.GONE);
                }

            }
            });
        MaterialButton btn_aqi=(MaterialButton) findViewById(R.id.button_aqi);
        btn_aqi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(toggle_aqi)
                {
                    toggle_aqi=false;
                    findViewById(R.id.textview1).animate().alpha(1.0f);
                    findViewById(R.id.map).setAlpha(0.5f);
//                    findViewById(R.id.textview).setVisibility(View.VISIBLE);
                }
                else
                {
                    toggle_aqi=true;
                    findViewById(R.id.textview1).animate().alpha(0.0f);
                    if(toggle_aqi && toggle && toggle_info)
                    findViewById(R.id.map).setAlpha(1.0f);
//                    findViewById(R.id.textview).setVisibility(View.GONE);
                }

            }
        });
        MaterialButton btn_info=(MaterialButton) findViewById(R.id.button_info);
        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(toggle_info)
                {
                    toggle_info=false;
                    findViewById(R.id.textview2).animate().alpha(1.0f);
                    findViewById(R.id.map).setAlpha(0.5f);
                    findViewById(R.id.textview3).animate().alpha(1.0f);
//                    findViewById(R.id.textview).setVisibility(View.VISIBLE);
                }
                else
                {
                    toggle_info=true;
                    findViewById(R.id.textview2).animate().alpha(0.0f);
                    findViewById(R.id.textview3).animate().alpha(0.0f);
                    if(toggle_aqi && toggle && toggle_info)
                    findViewById(R.id.map).setAlpha(1.0f);
//                    findViewById(R.id.textview).setVisibility(View.GONE);
                }

            }
        });
        MaterialButton btn1=(MaterialButton) findViewById(R.id.button_share);
//        marker_glob = new MarkerOptions().position(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude())).title("AQI").draggable(true);
        btn1.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View view) {
//                                       Uri contentUri = Uri.parse("android.resource://" + getPackageName() + "/drawable/" + "ic_launcher");

                                       StringBuilder msg = new StringBuilder();
                                       msg.append("Hey, Download this awesome app!");
                                       msg.append("\n");
                                       msg.append("https://play.google.com/store/apps/details?id=com.corona.precovid"); //example :com.package.name
                                       msg.append("https://play.google.com/store/apps/details?id=com.corona.precovid"); //example :com.package.name


                                           Intent shareIntent = new Intent();
                                           shareIntent.setAction(Intent.ACTION_SEND);
                                           shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
                                           shareIntent.setType("text/plain");
                                           shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, msg.toString());
//                                           shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);

                                           try {
                                               startActivity(shareIntent);
                                           } catch (ActivityNotFoundException e) {
                                               Toast.makeText(getApplicationContext(), "No App Available", Toast.LENGTH_SHORT).show();
                                           }

                                   }});
        // Retrieve the content view that renders the map.

        MaterialButton btn = (MaterialButton) findViewById(R.id.button1);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.commit();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        // Construct a PlacesClientt
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        mPlacesClient = Places.createClient(this);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


//        TextView button_hover = (TextView) findViewById(R.id.text_hover);
//        button_hover.setOnHoverListener(this);
        mapFragment.getMapAsync(this);

    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Sets up the options menu.
     * @param menu The options menu.
     * @return Boolean.
     */
    //    @Override
    //    public boolean onCreateOptionsMenu(Menu menu) {
    //        getMenuInflater().inflate(R.menu.current_place_menu, menu);
    //        return true;
    //    }

    /**
     * Handles a click on the menu option to get a place.
     * @param item The menu item to handle.
     * @return Boolean.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        glob_Location=new Location("service Provider");;
        map.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.style_json));
        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation(mMap);
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    public static Context getAppContext() {
        return MapsActivityCurrentPlace.context;
    }
    private void getDeviceLocation(final GoogleMap map) {
        glob_Location=new Location("service Provider");;
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                final RequestQueue[] queue = {
                        Volley.newRequestQueue(getApplicationContext())
                };


                Map < String, String > params = new HashMap < String, String > ();
                params.put("Username", email);
                String[] url=       {"https://6gmvz4821j.execute-api.us-east-2.amazonaws.com/testing"};
                post_location(url[0],queue[0],params,true);

                Timer t = new Timer();
                //Set the schedule function and rate
                t.scheduleAtFixedRate(new TimerTask() {

                                          @Override
                                          public void run() {
//

                                              final RequestQueue queue3 = Volley.newRequestQueue(getAppContext());
                                              Task < Location > locationResult;
                                              locationResult = mFusedLocationProviderClient.getLastLocation();
                                              Map < String, String > params3 = new HashMap < String, String > ();
                                              params3.put("Username", email + "alarm");
                                              String url3 = "https://y4ycjffei3.execute-api.us-east-2.amazonaws.com/testing";
                                              Log.d("msg", url3 + " alarm url  " + email + "alarm");


                                              locationResult.addOnCompleteListener(MapsActivityCurrentPlace.this, new OnCompleteListener < Location > () {
                                                  @Override
                                                  public void onComplete(@NonNull Task < Location > task) {
                                                      if (task.isSuccessful()) {
                                                          // Set the map's camera position to the current location of the device.
                                                          mLastKnownLocation = task.getResult();


                                                          if (mLastKnownLocation != null) {
                                                              Projection projection = mMap.getProjection();

//                                                              glob_Location.setLatitude(mLastKnownLocation.getLatitude());
//                                                              glob_Location.setLongitude(mLastKnownLocation.getLongitude());

//                                                              Log.d(email, "success" + url[0]);
                                                              final RequestQueue[] queue = {
                                                                      Volley.newRequestQueue(getApplicationContext())
                                                              };

                                                              float[] results= new float[1];;

//                                                                      pt1 = new LatLng(Float.valueOf(valueOf(latitude"))), Float.valueOf(valueOf(response.get("longitude"))));
                                                              //


                                                              final String[] url = {
                                                                      "https://6gmvz4821j.execute-api.us-east-2.amazonaws.com/testing"
                                                              };
//                                                              Log.d(TAG, "Success");
////                                                                          View myLayout = findViewById( R.layout.activity_maps );
//                                                              TextView myView = (TextView) findViewById( R.id.textview );
////                                                                      myView.setText("distance is "+results[0]);
                                                              Map < String, String >  params1 = new HashMap < String, String > ();
                                                              params1.put("Username", email+"activity");
                                                              get_activity(url[0],queue[0],params1);
//
                                                             params1 = new HashMap < String, String > ();
                                                             url[0]= "https://6gmvz4821j.execute-api.us-east-2.amazonaws.com/testing/location";
                                                              params1.put("Username", email+"activity");
                                                              params1.put("latitude", valueOf(mLastKnownLocation.getLatitude()));
                                                              params1.put("longitude", valueOf(mLastKnownLocation.getLongitude()));
                                                              Log.d("msg","inside post activity "+params1);

                                                              post_activity(url[0], queue[0],params1, false);
                                                              url[0]= "https://6gmvz4821j.execute-api.us-east-2.amazonaws.com/testing/alluser";
                                                              try {
                                                                  get_all_activity(url[0],queue[0],params1);
                                                              } catch (JSONException e) {
                                                                  e.printStackTrace();
                                                              }
                                                              double screen_height = (double) findViewById(R.id.map).getHeight();
                                                              double screen_height_30p = 30.0*screen_height/100.0;
                                                              Point screenPosition = projection.toScreenLocation(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
                                                              Point targetPoint = new Point(screenPosition.x, screenPosition.y+(int)(screen_height_30p));
                                                              LatLng targetPosition = projection.fromScreenLocation(targetPoint);
                                                              map.animateCamera(CameraUpdateFactory.newLatLng(targetPosition));
                                                              marker_glob = new MarkerOptions().position(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude())).title("Home Location").draggable(true);
                                                              Log.d("msg","val of marker glob"+marker_glob);
                                                              map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                                                                  @Override
                                                                  public void onMarkerDragStart(Marker arg0) {
                                                                  }

                                                                  @SuppressWarnings("unchecked")
                                                                  @Override
                                                                  public void onMarkerDragEnd(Marker arg0) {
                                                                      final String[] url = {
                                                                              "https://6gmvz4821j.execute-api.us-east-2.amazonaws.com/testing/location"
                                                                      };
                                                                      Log.d(email, "onMarkerDragEnd" + url[0]);
                                                                      final RequestQueue[] queue = {
                                                                              Volley.newRequestQueue(getApplicationContext())
                                                                      };

                                                                      float[] results= new float[1];;

//

                                                                      glob_Location.setLatitude(Double.valueOf(arg0.getPosition().latitude));
                                                                      glob_Location.setLongitude(Double.valueOf(arg0.getPosition().longitude));

                                                                      Location.distanceBetween(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(), arg0.getPosition().latitude, arg0.getPosition().longitude,
                                                                              results);

                                                                      Log.d(TAG, "Success");
                                                                      TextView myView = (TextView) findViewById( R.id.textview );


                                                                      Log.d("System out", "onMarkerDragEnd..."+arg0.getPosition().latitude+"..."+arg0.getPosition().longitude);
                                                                      Map < String, String > params1 = new HashMap < String, String > ();
                                                                      params1.put("Username", email);
                                                                      params1.put("latitude", valueOf(arg0.getPosition().latitude));
                                                                      params1.put("longitude", valueOf(arg0.getPosition().longitude));
                                                                      MapsActivityCurrentPlace obj = new MapsActivityCurrentPlace();

                                                                      post_request(url[0], params1, queue[0], "getcurrentlatlong", obj);



                                                                  }

                                                                  @Override
                                                                  public void onMarkerDrag(Marker arg0) {
                                                                  }
                                                              });

                                                              Timer timer = new Timer();
                                                              String email1 = email;

                                                              Map < String, String > params = new HashMap < String, String > ();
                                                              params.put("Username", email1);

                                                            queue[0] =Volley.newRequestQueue(getApplicationContext());


                                                              url[0]="https://6gmvz4821j.execute-api.us-east-2.amazonaws.com/testing";
                                                              try {
                                                                  post_location(url[0],queue[0],params,false);
                                                              } catch (JSONException e) {
                                                                  e.printStackTrace();
                                                              }

                                                                      float[] results1 = new float[1];
                                                                      if (pt1 == null)
                                                                          results1[0] = 0;
                                                                      else
                                                                          Location.distanceBetween(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(), pt1.latitude, pt1.longitude,
                                                                                  results1);
                                                                      if (results1[0] > 200 && !handwash_flag) {
                                                                          Log.d(TAG, "Success");
                                                                          handwash_flag=true;
////                                                                          View myLayout = findViewById( R.layout.activity_maps );
//                                                                                  TextView myView = (TextView) findViewById( R.id.textview );
//                                                                         myView.setText("distance is"+results1[0]);
                                                                          String who_url = "https://www.who.int/gpsc/clean_hands_protection/en/";
                                                                          String notification_title = "Wash Your Hands";
                                                                          String notification_content = "Wash Your hands as soon as you reach home";
                                                                          notification(who_url, notification_title, notification_content);





                                                                      }
                                                                      else if(results1[0]<=200)
                                                                          handwash_flag=false;
                                                                      //your code

                                                              if(!camera_flag)
                                                              mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                                                      new LatLng(mLastKnownLocation.getLatitude(),
                                                                              mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                                          }
                                                      } else {
                                                          Log.d(TAG, "Current location is null. Using defaults.");
                                                          Log.e(TAG, "Exception: %s", task.getException());
                                                          if(!camera_flag)
                                                          mMap.moveCamera(CameraUpdateFactory
                                                                  .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                                                          mMap.getUiSettings().setMyLocationButtonEnabled(false);
                                                      }
                                                      camera_flag=true;
                                                  }
                                              });

                                         }


                                      },
                        0,
                        30000);

            }
        } catch (SecurityException | JSONException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {

            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);

            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }
    public void notification(String url, String title, String content) {
        Intent notificationIntent = new Intent(Intent.ACTION_VIEW);
        notificationIntent.setData(Uri.parse(url));
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getAppContext())
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent((pi));
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "1";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }

        manager.notify(0, builder.build());
    }
    public void show_AQI(String response) {
        try {
            JSONObject js = new JSONObject(response);
            int res = js.getJSONObject("data").getJSONObject("current").getJSONObject("pollution").getInt("aqius");
            String city=js.getJSONObject("data").getString("city");

                                                        Log.d("msg", "AQI" + response);
                                                        Map<String,String>mp=new HashMap<>();
                                                        mp.put("Entity",city);
            MapsActivityCurrentPlace obj = new MapsActivityCurrentPlace();
            final RequestQueue queue1 = Volley.newRequestQueue(getAppContext());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void showPopulationDensity(JSONObject response)  {
        try {
//            Toast toast = Toast.makeText(getApplicationContext(), "Didn't Work!!", Toast.LENGTH_LONG);
         Toast.makeText(getApplicationContext(),response.getString("Population"), Toast.LENGTH_LONG).show();
        }
        catch(JSONException ex)
        {
            Log.d("msg","Thisistheway "+response);
            ex.printStackTrace();
        }
    }
    public void get_request(String url1, RequestQueue queue1) {
        StringRequest stringRequest1 = new StringRequest(Request.Method.GET, url1,
                new Response.Listener < String > () {
                    @Override
                    public void onResponse(String response) {
                        show_AQI(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("msg", "Error in get request");
            }
        });
        stringRequest1.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue1.add(stringRequest1);
    }

    public void post_request(String url1, Map < String, String > param, RequestQueue queue1, final String currentlatlong, final Object object) {
        JsonObjectRequest stringRequest1 = new JsonObjectRequest(url1, new JSONObject(param),
                new Response.Listener < JSONObject > () {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Display the first 500 characters of the response string.
                        //                                                            Log.d("succ", "response is" + email + response.toString());
                        Class[] parameterTypes = new Class[1];
                        parameterTypes[0] = JSONObject.class;

                        try {
                            if (response.has("latitude")) {
                                pt1 = new LatLng(Float.valueOf(valueOf(response.get("latitude"))), Float.valueOf(valueOf(response.get("longitude"))));
                                float[] val=new float[1];
                                Location.distanceBetween(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(), pt1.latitude, pt1.longitude,
                                        val);

                                Log.d(TAG, "Success in post "+val[0]);
//                                                                         View myLayout = findViewById( R.layout.activity_maps );
                                TextView myView = (TextView) findViewById( R.id.textview );
                                myView.setText("Distance is "+(int)val[0]+" metres");
                            }
//                            Toast toast = Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG);
//                            toast.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("succ", "successerr in post");
//                Toast toast = Toast.makeText(getApplicationContext(), "Didn't Work!!", Toast.LENGTH_LONG);
//                toast.show();

            }
        });
//        flag = false;
        stringRequest1.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue1.add(stringRequest1);
    }
    public void get_activity(String url, RequestQueue queue, Map<String,String>params)
    {
        final JsonObjectRequest stringRequest = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener < JSONObject > () {
                    @Override
                    public void onResponse(JSONObject response) {
                    if(response.has("latitude"))
                    {
                    float[] val=new float[1];
                        try {
                            LatLng point1 = new LatLng(Float.valueOf(valueOf(response.get("latitude"))), Float.valueOf(valueOf(response.get("longitude"))));

                            Location.distanceBetween(point1.latitude,point1.longitude,mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude(),val);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    Log.d("ms","speed"+val[0]);
                        if(val[0]>200)
                        {
                            String who_url = "https://www.who.int/gpsc/clean_hands_protection/en/";
                            String notification_title = "Outside Home on a vehicle";
                            String notification_content = "Please do not go far";
                            notification(who_url, notification_title, notification_content);
                        }
                    }
                    }}, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Log.d("msg", "get_activity does not work");
                        }
                });
        queue.add(stringRequest);
    }
    public void post_activity(String url, RequestQueue queue, Map<String,String>params, final boolean flag_local)
    {
        Log.d("msg","Inside post activity ");
        Log.d("msg","Inside post activity "+url);
        Log.d("msg","Inside post activity "+new JSONObject(params));
        final JsonObjectRequest stringRequest = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener < JSONObject > () {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("msg","Inside post activity "+response);
                    }}
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            error.printStackTrace();}});
        queue.add(stringRequest);
    }
public void get_all_activity(String url,RequestQueue queue,Map<String,String>params) throws JSONException {
    String email1 = email;

    Map < String, String > params1 = new HashMap < String, String > ();
    params.put("Username", email1);



        final JsonArrayRequest stringRequest = new JsonArrayRequest(Request.Method.GET,url,null,
            new Response.Listener <JSONArray> () {
                @Override
                public void onResponse(JSONArray response) {

                    JSONArray sortedJsonArray = new JSONArray();
                    List<JSONObject> jsonList = new ArrayList<JSONObject>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            jsonList.add(response.getJSONObject(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Collections.sort( jsonList, new Comparator<JSONObject>() {

                        public int compare(JSONObject a, JSONObject b) {
                            String valA = new String();
                            String valB = new String();

                            try {
                                valA = (String) a.get("Username");
                                valB = (String) b.get("Username");
                            }
                            catch (JSONException e) {
                                //do something
                            }

                            return valA.compareTo(valB);
                        }
                    });
                    Log.d("msg","Sorted Json Array: \n ");
                    for (int i = 0; i < response.length(); i++) {
                        sortedJsonArray.put(jsonList.get(i));
                        Log.d("msg", String.valueOf(jsonList.get(i)));
                    }
                    //n
                    // ow get your  json array like this

//                        JSONArray booking = response.getJSONArray("Username");
                        Log.d("msg","Inside get all activity "+response.toString());
                    int flag=0;
    for(int i=0;i<response.length()-1;i++)
    {
        Log.d("msg", String.valueOf(jsonList.get(i)));
        try {

            String first=sortedJsonArray.getJSONObject(i).get("Username").toString()+"activity";
            String second=sortedJsonArray.getJSONObject(i+1).get("Username").toString();
            Log.d("mess","first: " +first +" second: "+second +"\n"+"lat: " + sortedJsonArray.getJSONObject(i).get("latitude").toString()+" long: "+sortedJsonArray.getJSONObject(i).get("longitude").toString());
            if((first).equals(second))
            {
                float[] value=new float[1];
                float[] value1=new float[1];
                Log.d("mess","mission impossible");
                LatLng home_location=new LatLng(Double.valueOf(valueOf(sortedJsonArray.getJSONObject(i).get("latitude"))),Double.valueOf(valueOf(sortedJsonArray.getJSONObject(i).get("longitude"))));
                LatLng location=new LatLng(Double.valueOf(sortedJsonArray.getJSONObject(i+1).get("latitude").toString()),Double.valueOf(sortedJsonArray.getJSONObject(i+1).get("longitude").toString()));
                ;
                Location.distanceBetween(glob_Location.getLatitude(),glob_Location.getLongitude(),home_location.latitude,home_location.longitude,value);
                Location.distanceBetween(location.latitude,location.longitude,mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude(),value1);


//                Log.d("mess","first is:" +first+" secondis: " + second);
                Log.d("mess","val[0] is" +value[0] +" val1[0] is "+value1[0]+"\n");
//                Log.d("mess","lat1: "+location.latitude+" long1: "+location.longitude+" lat2: "+mLastKnownLocation.getLatitude()+" long2: "+mLastKnownLocation.getLongitude());
                Log.d("mess","lat1: "+ sortedJsonArray.getJSONObject(i).get("latitude").toString()+" long1: "+sortedJsonArray.getJSONObject(i).get("longitude").toString()+" lat2: "+home_location.latitude+" long2: "+home_location.longitude);

                if(value[0]>25 && value1[0]<5 )
                {
                    flag=1;
                    RequestQueue queue1 = Volley.newRequestQueue(getApplicationContext());
                    String transmit_url="https://dbt0dtctah.execute-api.us-east-2.amazonaws.com/transmit";
                    Map < String, String > transmit_params = new HashMap < String, String > ();
                    transmit_params.put("User", email);
                    transmit_params.put("Contact", sortedJsonArray.getJSONObject(i).get("Username").toString());
                    JsonObjectRequest stringRequest1 = new JsonObjectRequest(transmit_url,new JSONObject(transmit_params),
                            new Response.Listener < JSONObject> () {
                                @Override
                                public void onResponse(JSONObject response) {

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("msg", "Error in get request");
                        }
                    });

                    stringRequest1.setRetryPolicy(new DefaultRetryPolicy(
                            10000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    queue1.add(stringRequest1);


                }

                i++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    if(flag==1 && !contact_flag) {
        contact_flag = true;
        String who_url = "https://www.who.int/gpsc/clean_hands_protection/en/";
        String notification_title = "You are in contact";
        String notification_content = "You are in contact of external person";
//                    Log.d("mess","val[0] is" +value[0] +" val1[0] is "+value1[0]);
        notification(who_url, notification_title, notification_content);
    }
    else if(flag==0)
        contact_flag=false;

                }}
            , new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            error.printStackTrace();

        }});
    queue.add(stringRequest);
}
    public void post_location(String url, RequestQueue queue, Map<String,String>params, final boolean flag_local) throws JSONException {
        if(flag_local)
            mMap.clear();
        final JsonObjectRequest stringRequest = new JsonObjectRequest(url, new JSONObject(params),
                new Response.Listener < JSONObject > () {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("succ", "response is" + email + response.toString());

                        if (response.has("latitude")) {
                            try {
                                float[] v= new float[1];
                                ;

                                pt1 = new LatLng(Float.valueOf(valueOf(response.get("latitude"))), Float.valueOf(valueOf(response.get("longitude"))));
//                                glob_Location.setLatitude(pt1.latitude);
//                                glob_Location.setLongitude(pt1.longitude);
                                Location.distanceBetween(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(), pt1.latitude, pt1.longitude,
                                        v);
                                glob_Location.setLatitude(pt1.latitude);
                                glob_Location.setLongitude(pt1.longitude);

//                                Log.d(TAG, "Success"+pt1);
//                                                                          View myLayout = findViewById( R.layout.activity_maps );
                                TextView myView = (TextView) findViewById(R.id.textview);
                                myView.setText("Distance is " + (int)v[0]+"  metres");
                                Log.d("msg", "in post_location: "+mLastKnownLocation+glob_Location);


                                marker_glob = new MarkerOptions().position(new LatLng(pt1.latitude, pt1.longitude)).title("Home Location").draggable(true);
//                                marker_glob.snippet(Integer.toString(res));
                                Log.d("msg", "Added Marker");

                                //                                                        MarkerOptions marker = new MarkerOptions().position(new LatLng(pt1.latitude, pt1.longitude)).title("New Marker");
                                RequestQueue queue1 = Volley.newRequestQueue(getApplicationContext());
                                String url1 = "https://api.airvisual.com/v2/nearest_city?lat=" + pt1.latitude + "&" + "lon=" + pt1.longitude + "&key=cec21d6d-9d3a-4a11-8edf-656216d5fc98";
                                final int[] Aqi = new int[1];
                                // Request a string response from the provided URL.
//                                        get_request(url1, queue1);
                                StringRequest stringRequest1 = new StringRequest(Request.Method.GET, url1,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                // Display the first 500 characters of the response string.

                                                //                                                                JSONArray jsonArray = new JSONArray(response.get("pollution"));
                                                try {

                                                    JSONObject js = new JSONObject(response);
                                                    int res = js.getJSONObject("data").getJSONObject("current").getJSONObject("pollution").getInt("aqius");
                                                    Log.d("msg", "AQI" + res);
                                                    TextView myView = (TextView) findViewById(R.id.textview1);
                                                    myView.setText("Air Quality Index is " + res);


                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }


                                                //                                                            textView.setText("Response is: "+ response.substring(0,500));
                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        Log.d("msg", "wow||That didn't work!");
                                    }
                                });

                                // Add the request to the RequestQueue.

                                queue1.add(stringRequest1);
                                //                                                        map.addMarker(marker);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }

                        if(flag_local) {
                            Log.d("succ", "error for post request after before else");
                            mMap.clear();
                            mMap.addMarker(marker_glob).showInfoWindow();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if(flag_local) {
                    Log.d("succ", "error for post request after else");
                    mMap.clear();
                    mMap.addMarker(marker_glob).showInfoWindow();
                }
            }
        });

        queue.add(stringRequest);
    }
}





