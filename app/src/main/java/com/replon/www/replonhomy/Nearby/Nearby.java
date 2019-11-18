package com.replon.www.replonhomy.Nearby;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.replon.www.replonhomy.Accounting.MMT.AccountingMainActivity;
import com.replon.www.replonhomy.Complaints.ComplaintsActivity;
import com.replon.www.replonhomy.Emergency.Emergency;
import com.replon.www.replonhomy.Home.QuickActionsGridAdapter;
import com.replon.www.replonhomy.Messaging.MessagesMainActivity;

import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Services.Services;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class Nearby extends AppCompatActivity
        implements LocationListener, NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private static final String TAG = "Nearby";
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LocationRequest locationRequest;

    double latitude, longitude;
    NearbyAdapter adapter;
    private final static int REQUEST_CHECK_SETTINGS = 69;
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION};

    private boolean locationPermissionGranted;
    private boolean requestLocation;


    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    public boolean canGetLocation = false;
    Location location1;

    getNearbyPlacesData
            getNearbyPlacesDataObj;


    GridView gridNearby;
    ImageView back, ham_icon, user_icon;
    String soc_name, flat_no, name, username,unique_id;
    TextView tv_society_name, tv_flatno, tv_username, tv_name, nearby_obj;
    String admin;
    String category_home="";

    ProgressBar progressBar;
    GoogleApiClient googleApiClient;
    Boolean dummy=FALSE;


    String nearbyGrid[] = {"Restaurants", "Grocery Stores", "Shopping Centers", "Transport", "Pharmacies", "Hospitals & Clinics", "ATM", "More"};

    int nearbyGridImg[] = {R.drawable.nearby_restaurants, R.drawable.nearby_groceries, R.drawable.nearby_shopping,
            R.drawable.nearby_trans, R.drawable.nearby_pharmacy, R.drawable.nearby_hospitals, R.drawable.nearby_atm,
            R.drawable.nearby_more};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), Nearby.this);

        setContentView(R.layout.activity_nearby);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ham_icon = (ImageView) findViewById(R.id.ham_out);
        user_icon = (ImageView) findViewById(R.id.user_profile);

        ham_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.START);
            }
        });
        admin = getIntent().getExtras().getString("admin");

        Boolean internet_ans = isNetworkAvailable();

        if (!internet_ans){

            final Dialog dialog = new Dialog(Nearby.this);
            dialog.setContentView(R.layout.dialog_new);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            Log.i(TAG,"NEW DIALOG");

            Button btn_positive = dialog.findViewById(R.id.btn_positive);
            Button btn_negative = dialog.findViewById(R.id.btn_negative);
            TextView dialog_title = dialog.findViewById(R.id.dialog_title);
            TextView dialog_message = dialog.findViewById(R.id.dialog_message);
            ImageView dialog_icon = dialog.findViewById(R.id.dialog_img);

            dialog_title.setText("Internet Unavailable");
            dialog_message.setText("Poor network connectivity detected! Please check your internet connection");
//        btn_negative.setVisibility(View.GONE);
//        btn_positive.setVisibility(View.GONE);

            btn_positive.setText("OK");
            btn_negative.setText("Go to Settings");
            btn_positive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            btn_negative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    startActivity(myIntent);
                }
            });
            dialog_icon.setImageResource(R.drawable.ic_no_internet);
            dialog.show();
        }

        user_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent settings_intent = new Intent(Nearby.this, com.replon.www.replonhomy.Settings.Settings.class);
                settings_intent.putExtra("society_name", soc_name);
                settings_intent.putExtra("flat_no", flat_no);
                settings_intent.putExtra("username", username);
                settings_intent.putExtra("name", name);
                settings_intent.putExtra("admin", admin);
                settings_intent.putExtra("unique_id",unique_id);
                finish();
                startActivity(settings_intent);
                overridePendingTransition(0, 0);

            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        back = (ImageView) findViewById(R.id.back_nearby);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_home);
        tv_name = headerLayout.findViewById(R.id.name);
        tv_username = headerLayout.findViewById(R.id.username);

        tv_society_name = (TextView) findViewById(R.id.society_name);
        tv_flatno = (TextView) findViewById(R.id.flat_no);


        soc_name = getIntent().getExtras().getString("society_name");
        flat_no = getIntent().getExtras().getString("flat_no");
        name = getIntent().getExtras().getString("name");
        username = getIntent().getExtras().getString("username");
        unique_id=getIntent().getStringExtra("unique_id");
        category_home = getIntent().getStringExtra("category_home");


        tv_society_name.setText(soc_name);
        tv_flatno.setText(flat_no);
        tv_username.setText(username);
        tv_name.setText(name);

        if(soc_name.equals(getString(R.string.DUMMY_SOC))){
            dummy=TRUE;
        }


        gridNearby = (GridView) findViewById(R.id.gridViewNearby);
        QuickActionsGridAdapter adapter = new QuickActionsGridAdapter(getApplicationContext(), nearbyGridImg, nearbyGrid);
        nearby_obj = findViewById(R.id.nearby_obj);
        nearby_obj.setText("Please Select An Item");
        gridNearby.setAdapter(adapter);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        if (!category_home.equals("")){

            getLocation();
            progressBar.setVisibility(View.VISIBLE);
            showPlaces(category_home.replace(" ", "+"));
            nearby_obj.setText(category_home);
            getNearbyPlacesDataObj = new getNearbyPlacesData();
            Toast.makeText(getApplicationContext(), "Showing Nearby " + category_home, Toast.LENGTH_SHORT).show();
            category_home="";
        }

        gridNearby.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (!dummy) {

                    gridNearby.setEnabled(false);
                    gridNearby.setClickable(false);
                    gridNearby.setFocusable(false);


                    if (!isNetworkAvailable()) {

                        final Dialog dialog = new Dialog(Nearby.this);
                        dialog.setContentView(R.layout.dialog_new);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        Log.i(TAG, "NEW DIALOG");

                        Button btn_positive = dialog.findViewById(R.id.btn_positive);
                        Button btn_negative = dialog.findViewById(R.id.btn_negative);
                        TextView dialog_title = dialog.findViewById(R.id.dialog_title);
                        TextView dialog_message = dialog.findViewById(R.id.dialog_message);
                        ImageView dialog_icon = dialog.findViewById(R.id.dialog_img);

                        dialog_title.setText("Internet Unavailable");
                        dialog_message.setText("Poor network connectivity detected! Please check your internet connection");
//        btn_negative.setVisibility(View.GONE);
//        btn_positive.setVisibility(View.GONE);

                        btn_positive.setText("OK");
                        btn_negative.setText("Go to Settings");
                        btn_positive.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        btn_negative.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent myIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                                startActivity(myIntent);
                            }
                        });
                        dialog_icon.setImageResource(R.drawable.ic_no_internet);
                        dialog.show();

                        gridNearby.setEnabled(true);
                        gridNearby.setClickable(true);
                    } else if (isLocationServiceEnabled()) {

                        getLocation();
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }

                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, Nearby.this);
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, Nearby.this);

                        if (!nearby_obj.getText().toString().equals(nearbyGrid[position])) {
                            switch (position) {
                                case 0:
                                case 1:
                                case 2:
                                case 3:
                                case 4:
                                case 5:
                                case 6:

                                    progressBar.setVisibility(View.VISIBLE);
                                    showPlaces(nearbyGrid[position].replace(" ", "+"));
                                    nearby_obj.setText(nearbyGrid[position]);
                                    getNearbyPlacesDataObj = new getNearbyPlacesData();
                                    Toast.makeText(getApplicationContext(), "Showing Nearby " + nearbyGrid[position], Toast.LENGTH_SHORT).show();
                                    break;
                                case 7:
                                    final String[] more_items = {"➤  Services",
                                            "           Hotels",
                                            "           ATMs",
                                            "           Beauty Salons",
                                            "           Car Hire",
                                            "           Car Repair",
                                            "           Car Wash",
                                            "           Dry Cleaning",
                                            "           Electric Vehicle Charging",
                                            "           Chemists",
                                            "           Petrol",
                                            "           Post",
                                            "           Hospitals & Clinics",
                                            "           Parking",
                                            "➤  Food & Drink",
                                            "           Restaurants",
                                            "           Bars",
                                            "           Coffee",
                                            "           Delivery",
                                            "           Takeaway",
                                            "➤  Things To Do",
                                            "           Parks",
                                            "           Gyms",
                                            "           Films",
                                            "           Nightlife",
                                            "           Live Music",
                                            "           Art",
                                            "           Museums",
                                            "           Libraries",
                                            "➤  Shopping",
                                            "           Groceries",
                                            "           Beauty Supplies",
                                            "           Clothing",
                                            "           Shopping Centres",
                                            "           Electronics",
                                            "           Sporting Goods",
                                            "           Home & Garden",
                                            "           Car Dealers",
                                            "           Convenience Shops"

                                    };


                                    final AlertDialog.Builder builder = new AlertDialog.Builder(Nearby.this);
                                    builder.setTitle("Pick an Item");
                                    builder.setItems(more_items, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // the user clicked on colors[which]
                                            switch (which) {
                                                case 0:
                                                case 14:
                                                case 20:
                                                case 29:
                                                    builder.show();
                                                    break;
                                                default:
                                                    progressBar.setVisibility(View.VISIBLE);
                                                    more_items[which] = more_items[which].trim();
                                                    showPlaces(more_items[which].replace(" ", "+"));
                                                    nearby_obj.setText(more_items[which]);
                                                    getNearbyPlacesDataObj = new getNearbyPlacesData();
                                                    Toast.makeText(getApplicationContext(), "Showing Nearby " + more_items[which], Toast.LENGTH_SHORT).show();
                                                    break;

                                            }
                                        }
                                    });
                                    builder.show();
                                    break;

                            }//end switch
                        }else{ //if(Pressed again)
                            gridNearby.setEnabled(true);
                            gridNearby.setClickable(true);
                            gridNearby.setFocusable(true);
                        }
                        } else {
                            showMessage("Error !", "Please enable location services to continue.", R.drawable.ic_location_diabled);

                            gridNearby.setEnabled(true);
                            gridNearby.setClickable(true);
                            gridNearby.setFocusable(true);

                        }
                    } else {
                        showMessageOptions("Oops!", "You are not able to access the nearby services!\n Contact us for more information", R.drawable.ic_error_dialog);

                    }
                }

        });

        back = (ImageView) findViewById(R.id.back_nearby);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
            }
        });


        checkPermissions();

        getLocation();


    }


    public void showMessage(String title, String message,int image){

        final Dialog dialog = new Dialog(Nearby.this);
        dialog.setContentView(R.layout.dialog_new);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Log.i(TAG,"NEW DIALOG");

        Button btn_positive = dialog.findViewById(R.id.btn_positive);
        Button btn_negative = dialog.findViewById(R.id.btn_negative);
        TextView dialog_title = dialog.findViewById(R.id.dialog_title);
        TextView dialog_message = dialog.findViewById(R.id.dialog_message);
        ImageView dialog_icon = dialog.findViewById(R.id.dialog_img);

        dialog_title.setText(title);
        dialog_message.setText(message);
//        btn_negative.setVisibility(View.GONE);
//        btn_positive.setVisibility(View.GONE);

        btn_positive.setText("ALLOW");
        btn_negative.setText("CANCEL");
        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (googleApiClient == null) {
                    googleApiClient = new GoogleApiClient.Builder(Nearby.this)
                            .addApi(LocationServices.API)
                            .addConnectionCallbacks(Nearby.this)
                            .addOnConnectionFailedListener(Nearby.this).build();
                    googleApiClient.connect();

                    LocationRequest locationRequest = LocationRequest.create();
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    locationRequest.setInterval(30 * 1000);
                    locationRequest.setFastestInterval(5 * 1000);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);

                    //**************************
                    builder.setAlwaysShow(true); //this is the key ingredient
                    //**************************

                    PendingResult<LocationSettingsResult> result =
                            LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            final LocationSettingsStates state = result.getLocationSettingsStates();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    // All location settings are satisfied. The client can initialize location
                                    // requests here.
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied. But could be fixed by showing the user
                                    // a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        status.startResolutionForResult(
                                                Nearby.this, 1000);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied. However, we have no way to fix the
                                    // settings so we won't show the dialog.
                                    break;
                            }
                        }
                    });
                }
                dialog.dismiss();
            }
        });
        btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog_icon.setImageResource(image);
        dialog.show();

    }


    public Location getLocation(){

        try{

            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                showMessage("Error","Could not connect to Network.",R.drawable.ic_error_dialog);
            }else{
                canGetLocation = true;
                if (isNetworkEnabled) {

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                    }
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, Nearby.this);

                    if (locationManager != null) {
                        location1 = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        if (location1 != null) {

                            latitude = location1.getLatitude();
                            longitude = location1.getLongitude();
                            Log.i(TAG,"LOCATION MILA HAI MUJE FROM NEW METHOD: NETWORK " + latitude);
                        }
                    }
                }

                if (isGPSEnabled){
                    if(location1 == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                        if(locationManager != null) {
                            location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            if(location1 != null) {
                                latitude = location1.getLatitude();
                                longitude = location1.getLongitude();
                                Log.i(TAG,"LOCATION MILA HAI MUJE FROM NEW METHOD: GPS " + latitude);
                            }
                        }
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }



        return location1;
    }





    public boolean isLocationServiceEnabled(){
        boolean gps_enabled= false,network_enabled = false;

        if(locationManager ==null)
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        try{
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch(Exception ex){
            //do nothing...
        }

        try{
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch(Exception ex){
            //do nothing...
        }

        return gps_enabled || network_enabled;

    }


    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        Log.i(TAG,"Location is: "+latitude +" : " + longitude);

    }

    @Override
    public void onProviderDisabled(String provider)
    {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG,"New status");
    }

    protected void createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, "Please allow us to use your location services to give you better experience!", Toast.LENGTH_LONG).show();

//                        showMessage("Required Permission","Please allow us to use your location services to give you better experience!",R.drawable.ic_location_diabled);
                        finish();
                        return;
                    }
                }
                // all permissions were granted
                break;
        }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private String getUrl(double latitude, double longitude, String nearbyPlace){

        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location=" + latitude + "," + longitude);
        googlePlaceUrl.append("&rankby=distance");
        googlePlaceUrl.append("&keyword=" + nearbyPlace);
        googlePlaceUrl.append("&sensor=true");
//        googlePlaceUrl.append("&key=" + "AIzaSyBLzXqAVj2WUlBw5u1k1ujl2QqjRyqLauE");
        googlePlaceUrl.append("&key=" + "AIzaSyCdbPZ0gQb-IvBuXCm2txQpxhV0wAHoVj0");


        Log.d("MapsActivity", "url = "+googlePlaceUrl.toString());

        return googlePlaceUrl.toString();
    }



    public void showPlaces(final String placeName){



        if (!isNetworkAvailable()){

            final Dialog dialog = new Dialog(Nearby.this);
            dialog.setContentView(R.layout.dialog_new);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            Log.i(TAG,"NEW DIALOG");

            Button btn_positive = dialog.findViewById(R.id.btn_positive);
            Button btn_negative = dialog.findViewById(R.id.btn_negative);
            TextView dialog_title = dialog.findViewById(R.id.dialog_title);
            TextView dialog_message = dialog.findViewById(R.id.dialog_message);
            ImageView dialog_icon = dialog.findViewById(R.id.dialog_img);

            dialog_title.setText("Internet Unavailable");
            dialog_message.setText("Poor network connectivity detected! Please check your internet connection");
//        btn_negative.setVisibility(View.GONE);
//        btn_positive.setVisibility(View.GONE);

            btn_positive.setText("OK");
            btn_negative.setText("Go to Settings");
            btn_positive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            btn_negative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    startActivity(myIntent);
                }
            });
            dialog_icon.setImageResource(R.drawable.ic_no_internet);
            dialog.show();

            gridNearby.setEnabled(true);
            gridNearby.setClickable(true);
        }

        else {


            Object dataTransfer[] = new Object[1];
            String url = getUrl(latitude, longitude, placeName);

            dataTransfer[0] = url;

            getNearbyPlacesDataObj = new getNearbyPlacesData() {

                @Override
                protected void onPostExecute(String s) {
                    DataParser parser = new DataParser();
                    nearbyplaceList = parser.parse(s);
                    Log.d("nearbyplacesdata", "called parse method");
                    final RecyclerView recyclerView = findViewById(R.id.recycler_view);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    placesName.clear();
                    showNearbyPlaces1();


                    //adapter.setClickListener(getApplicationContext());
                    adapter = new NearbyAdapter(getApplicationContext(), placesName) {

                        @Override
                        public void onBindViewHolder(@NonNull NearbyViewHolder holder, int i) {

                            final ContentsNearbyPlaces contentsNearbyPlaces = nearbyList.get(i);

                            holder.place_name.setText(String.valueOf(contentsNearbyPlaces.getPlace_name()));
                            holder.place_address.setText(String.valueOf(contentsNearbyPlaces.getPlace_address()));
                            holder.place_rating.setText(String.valueOf(contentsNearbyPlaces.getPlace_rating()));

                            holder.place_phone.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String tel = contentsNearbyPlaces.getPlace_phno();
                                    tel = "tel:" + tel;
                                    //Intent acCall = new Intent(Intent.ACTION_DIAL, Uri.parse(tel));

                                    Intent acCall = new Intent(Intent.ACTION_DIAL);
                                    acCall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    acCall.setData(Uri.parse(tel));
                                    // acCall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                Intent chooserIntent = Intent.createChooser(acCall, "Open With");
//                chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    v.getContext().startActivity(acCall);
                                }
                            });

                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + Uri.encode(contentsNearbyPlaces.getPlace_name()));
//                                Uri gmmIntentUri = Uri.parse("?q=place_id:" + Uri.encode(contentsNearbyPlaces.getPlace_id()));
                                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                    mapIntent.setPackage("com.google.android.apps.maps");
                                    mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    v.getContext().startActivity(mapIntent);

                                }
                            });

                        }

                    };
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    showNearbyPlaces2();
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);


                }
            };

            getNearbyPlacesDataObj.execute(dataTransfer);


            gridNearby.setEnabled(true);
            gridNearby.setClickable(true);
            gridNearby.setFocusable(true);
        }

    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
            overridePendingTransition(0, 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        switch (id){
            case R.id.home_menu:
                finish();
                overridePendingTransition(0, 0);
                break;
            case R.id.messages_menu:
                if(!dummy) {
                    Intent messages_intent = new Intent(Nearby.this, MessagesMainActivity.class);
                    finish();
                    messages_intent.putExtra("society_name", soc_name);
                    messages_intent.putExtra("flat_no", flat_no);
                    messages_intent.putExtra("username", username);
                    messages_intent.putExtra("name", name);
                    messages_intent.putExtra("admin", admin);
                    messages_intent.putExtra("unique_id", unique_id);
                    startActivity(messages_intent);
                    overridePendingTransition(0, 0);
                }else{
                    showMessageOptions("Oops!","You are not able to access Messaging!\n Contact us for more information",R.drawable.ic_error_dialog);
                }
                break;
            case R.id.complaints_menu:
                Intent complaints_intent = new Intent(Nearby.this, ComplaintsActivity.class);
                finish();
                complaints_intent.putExtra("society_name",soc_name);
                complaints_intent.putExtra("flat_no",flat_no);
                complaints_intent.putExtra("username",username);
                complaints_intent.putExtra("name",name);
                complaints_intent.putExtra("admin",admin);
                complaints_intent.putExtra("unique_id",unique_id);
                startActivity(complaints_intent);
                overridePendingTransition(0, 0);
                break;
//            case R.id.payments_menu:
//                Intent payments_intent = new Intent(Nearby.this, Payments.class);
//                finish();
//                payments_intent.putExtra("society_name",soc_name);
//                payments_intent.putExtra("flat_no",flat_no);
//                payments_intent.putExtra("username",username);
//                payments_intent.putExtra("name",name);
//                payments_intent.putExtra("admin",admin);
//                payments_intent.putExtra("unique_id",unique_id);
//                startActivity(payments_intent);
//                overridePendingTransition(0, 0);
//                break;
            case R.id.accounting_menu:
                Intent acc_intent = new Intent(getApplicationContext(), AccountingMainActivity.class);
                finish();
                acc_intent.putExtra("society_name",soc_name);
                acc_intent.putExtra("flat_no",flat_no);
                acc_intent.putExtra("username",username);
                acc_intent.putExtra("name",name);
                acc_intent.putExtra("admin",  admin);
                acc_intent.putExtra("unique_id", unique_id);
                startActivity(acc_intent);
                overridePendingTransition(0,0);
                break;
            case R.id.services_menu:
                Intent services_intent = new Intent(Nearby.this, Services.class);
                finish();
                services_intent.putExtra("society_name",soc_name);
                services_intent.putExtra("flat_no",flat_no);
                services_intent.putExtra("username",username);
                services_intent.putExtra("name",name);
                services_intent.putExtra("admin",admin);
                services_intent.putExtra("unique_id",unique_id);
                startActivity(services_intent);
                overridePendingTransition(0, 0);
                break;
            case R.id.settings_menu:
                Intent settings_intent = new Intent(Nearby.this, com.replon.www.replonhomy.Settings.Settings.class);
                settings_intent.putExtra("society_name", soc_name);
                settings_intent.putExtra("flat_no", flat_no);
                settings_intent.putExtra("username", username);
                settings_intent.putExtra("name", name);
                settings_intent.putExtra("admin", admin);
                settings_intent.putExtra("unique_id",unique_id);
                finish();
                startActivity(settings_intent);
                overridePendingTransition(0, 0);
                break;

            case R.id.emergency_menu:
                Intent emergency_intent = new Intent(getApplicationContext(), Emergency.class);
                finish();
                emergency_intent.putExtra("society_name",soc_name);
                emergency_intent.putExtra("flat_no",flat_no);
                emergency_intent.putExtra("username",username);
                emergency_intent.putExtra("name",name);
                emergency_intent.putExtra("admin",admin);
                emergency_intent.putExtra("unique_id",unique_id);
                startActivity(emergency_intent);
                overridePendingTransition(0, 0);
                break;
        }


        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.i(TAG,"CONNECT HUA HAI");

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void showMessageOptions(String title, String message,int image){

//        ,R.drawable.ic_error_dialog

        final Dialog dialog = new Dialog(Nearby.this);
        dialog.setContentView(R.layout.dialog_new);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Log.i(TAG,"NEW DIALOG");

        Button btn_positive = dialog.findViewById(R.id.btn_positive);
        Button btn_negative = dialog.findViewById(R.id.btn_negative);
        TextView dialog_title = dialog.findViewById(R.id.dialog_title);
        TextView dialog_message = dialog.findViewById(R.id.dialog_message);
        ImageView dialog_icon = dialog.findViewById(R.id.dialog_img);

        dialog_title.setText(title);
        dialog_message.setText(message);
        btn_negative.setVisibility(View.VISIBLE);
        btn_positive.setVisibility(View.VISIBLE);

        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settings_intent=new Intent(getApplicationContext(), Services.class);
                settings_intent.putExtra("society_name",soc_name);
                settings_intent.putExtra("flat_no",flat_no);
                settings_intent.putExtra("username",username);
                settings_intent.putExtra("name",name);
                settings_intent.putExtra("admin",admin);
                startActivity(settings_intent);
                overridePendingTransition(0, 0);

            }
        });
        btn_positive.setText("Contact Us");

        btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        btn_negative.setText("OK");
        dialog_icon.setImageResource(image);
        dialog.show();

    }
}
