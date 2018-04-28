package jeeryweb.satsang.Actvities;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jeeryweb.satsang.BuildConfig;
import jeeryweb.satsang.Data.FileReader;
import jeeryweb.satsang.R;
import jeeryweb.satsang.Services.FetchAddressIntentService;
import jeeryweb.satsang.Services.LocationUpdaterService;
import jeeryweb.satsang.Utilities.AlarmSetter;
import jeeryweb.satsang.Utilities.ConstantsForGeocoding;
import jeeryweb.satsang.Utilities.SharedPreferenceManager;

public class MainActivity extends AppCompatActivity {

    //attributes****************************************************************************************
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    private static final String LOCATION_ADDRESS_KEY = "location-address";
    public FileReader fileReader;
    public AlarmSetter alarmSetter;
    public SharedPreferenceManager sharedPref;
    private mRecievrfromService mrecievrfromService;
    private Context c;

    //Location objects
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLastLocation;
    /**
     * Tracks whether the user has requested an address. Becomes true when the user requests an
     * address and false when the address (or an error message) is delivered.
     */
    private boolean mAddressRequested= true;
    private String mCountryName;
    private String mAddressOutput;
    private String mAddressState;
    private String mPrayingTime;
    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private AddressResultReceiver mResultReceiver;
    //widgets
    private TextView mLocationAddressTextView, mStateNameView, mPrayerTimeView;
    private ProgressBar mProgressBar;
    private Switch disableSwitch;
    private TextView alarmSetConfirmer;

    //Methods*******************************************************************************************
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mResultReceiver = new AddressResultReceiver(new Handler());

        //widgets initialization
        mLocationAddressTextView = (TextView) findViewById(R.id.location_address_view);
        mStateNameView = (TextView) findViewById(R.id.location_state);
        mPrayerTimeView = (TextView) findViewById(R.id.prayer_time_view);
        disableSwitch = (Switch) findViewById(R.id.simpleSwitch);
        alarmSetConfirmer = (TextView) findViewById(R.id.alarm_Set_confirmer);

        /*  Initilaize classed */
        fileReader = new FileReader();
        sharedPref = new SharedPreferenceManager(this);
        mResultReceiver = new AddressResultReceiver(new Handler());
        mrecievrfromService = new mRecievrfromService();
        alarmSetter = new AlarmSetter(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //set listener for swicth button

        disableSwitch.setChecked(sharedPref.isALarmDisabled());

        updateAlarmDisplaViews();
        disableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {

                    //alarm disable yes
                    Log.e("AlarmDebug","alarm disablled by switch");
                    sharedPref.setAlarmDisabled();

                      // continious is false //will be false alays on first trogger

                    //calling functions to cancel existing larms
                    try {
                        alarmSetter.setAlarm(false);
                        alarmSetter.setAlarm15(false);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                } else {
                    sharedPref.unsetAlarmDisabled();
                    try {
                        alarmSetter.setAlarm(false);   // continious is false //will be false alays on first trogger
                        alarmSetter.setAlarm15(false);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
                updateAlarmDisplaViews();
            }
        });


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LocationUpdaterService.MY_ACTION);
        registerReceiver(mrecievrfromService, intentFilter);

        if (!isServiceRunning(LocationUpdaterService.class))
            startService(new Intent(getBaseContext(), LocationUpdaterService.class));

        c = this;
        // Set defaults, then update using values stored in the Bundle.

        mAddressOutput = "";

        updateAlarmDisplaViews();
        updateValuesFromBundle(savedInstanceState);


    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        //  preparation code here
        return super.onPrepareOptionsMenu(menu);
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void onStart() {
        super.onStart();

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            getAddress();
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        unregisterReceiver(mrecievrfromService);
        super.onDestroy();
    }






    private void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(ConstantsForGeocoding.RECEIVER, mResultReceiver);
        intent.putExtra(ConstantsForGeocoding.LOCATION_DATA_EXTRA, mLastLocation);

        startService(intent);
    }


    @SuppressWarnings("MissingPermission")
    private void getAddress() {
        Log.e(TAG, "in GetAddress");

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location == null) {
                            Log.w(TAG, "onSuccess:null");

                            showSnackbar("Cannot get location! Please try again later");

                            return;
                        }

                        mLastLocation = location;

                        // Determine whether a Geocoder is available.
                        if (!Geocoder.isPresent()) {
                            showSnackbar(getString(R.string.no_geocoder_available));
                            return;
                        }

                        // If the user pressed the fetch address button before we had the location,
                        // this will be set to true indicating that we should kick off the intent
                        // service after fetching the location.
                        if(mLastLocation!=null)
                            startIntentService();
                        else
                            showSnackbar("Cannot get location! Please try again later");

                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "getLastLocation:onFailure", e);

                    }
                });
    }

    /**
     * Updates the address in the UI.
     */
    private void displayAddressOutput() {


        if(mAddressOutput==null || mCountryName==null){
            mLocationAddressTextView.setText("Cannot get your location!");
            mStateNameView.setText("Cannot get your location!");
            mPrayerTimeView.setText("Cannot get your location!");
            return;
        }


        mLocationAddressTextView.setText(mAddressOutput);

        if(mAddressState==null || mAddressState.equals("NA")){
            mStateNameView.setText("No Satasang mandir in your area");
            mPrayerTimeView.setText("");
            return;
        }
        else{
            mStateNameView.setText(mAddressState + ",\n"+ mCountryName);
        }

        String format[] = mPrayingTime.split(",");
        String op = "Morning  " + format[0] + "\n" + "Evening " + format[1];
        mPrayerTimeView.setText(op);
    }

    //update alarm display views
    public void updateAlarmDisplaViews(){

        alarmSetConfirmer.setText("");

        if (sharedPref.isALarmDisabled()) {
            alarmSetConfirmer.setText("Alarms Disabled");

            return;
        }
        if(sharedPref.getflagA()==0 && sharedPref.getflagA15()==0){
            alarmSetConfirmer.setText("Alarms not set");
        }


        if(sharedPref.getflagA()==1 &&  sharedPref.getflagA15()==1)
            alarmSetConfirmer.setText("2 Alarms set at Morning prayer time and 15 minutes prior Morning prayer time");

        if(sharedPref.getflagA()==2 &&  sharedPref.getflagA15()==2)
            alarmSetConfirmer.setText("2 Alarms set at Evening prayer time and 15 minutes prior Evening prayer time");

        if(sharedPref.getflagA()==2 &&  sharedPref.getflagA15()==1)
            alarmSetConfirmer.setText("2 Alarms set at Evening prayer time and 15 minutes prior to Morning prayer time");

        if(sharedPref.getflagA()==1 &&  sharedPref.getflagA15()==2)
            alarmSetConfirmer.setText("2 Alarms set at Morning prayer time and 15 minutes prior to Evening prayer time");


    }


    /**
     * Shows a toast with the given text.
     */
    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save whether the address has been requested.
        savedInstanceState.putBoolean(ADDRESS_REQUESTED_KEY, mAddressRequested);

        // Save the address string.
        savedInstanceState.putString(LOCATION_ADDRESS_KEY, mAddressOutput);
        super.onSaveInstanceState(savedInstanceState);
    }


    private void showSnackbar(final String text) {
        View container = findViewById(android.R.id.content);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }


    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState1 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int per2 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        int per3 = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.MANAGE_DOCUMENTS);

        return permissionState1 == PackageManager.PERMISSION_GRANTED &&
                per2 == PackageManager.PERMISSION_GRANTED &&
                per3 ==PackageManager.PERMISSION_GRANTED;
    }


//Class inside a class******************************************************************************

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) &&
                        ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.READ_EXTERNAL_STORAGE) &&
                        ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.MANAGE_DOCUMENTS);


        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "val " + "Displaying permission rationale to provide additional context.");

            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE , Manifest.permission.MEDIA_CONTENT_CONTROL,
                                    Manifest.permission.MANAGE_DOCUMENTS},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });

        } else {
            Log.i(TAG, "val " + "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE , Manifest.permission.MEDIA_CONTENT_CONTROL,
                            Manifest.permission.MANAGE_DOCUMENTS}, REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }


//Permission Methods********************************************************************************

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "val " + "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "val " + "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getAddress();
            } else {

                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }

    }

    public void loadGoogleMap(View view) {
        String co_or = "geo:" + mLastLocation.getLatitude() + ',' + mLastLocation.getLongitude() + "?q=Satsang";
        Uri gmmIntentUri = Uri.parse(co_or);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);

        }
    }
    public void startActivityforTunePicker(View view){
        Intent intent = new Intent(this, alarmTunePicker.class);

        startActivity(intent);
    }
    public void startActivityforSearch(View view){


       Intent intent = new Intent(this, SearchActivity.class);
       startActivity(intent);
    }

    public void startActivityforXXXX(View view){


        Intent intent = new Intent(this, AlarmActivity.class);
        startActivity(intent);
    }

    /* class for recieving data from Location updater Service

    */
    public class mRecievrfromService extends BroadcastReceiver {


        @Override
        public void onReceive(Context arg0, Intent arg1) {

            try {
                mAddressOutput = arg1.getStringExtra("District");
                mPrayingTime = arg1.getStringExtra("PrayingTime");
                mAddressState = arg1.getStringExtra("State");
                Log.e("LocationUpdaterService", "Broadcast recievr worked");

                //location changed
                alarmSetter.setAlarm(false);
                alarmSetter.setAlarm15(false);

                //update ui
                displayAddressOutput();
                //update alram ui
                updateAlarmDisplaViews();
            } catch (Exception e) {
                mAddressOutput = null;
                mPrayingTime = null;
                mAddressState = null;
                Log.e("LocationUpdaterService", "Broadcast reciever did not work");
            }
        }

    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    private class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {


            mAddressOutput = resultData.getString(ConstantsForGeocoding.RESULT_DATA_KEY);
            mCountryName = resultData.getString(ConstantsForGeocoding.RESULT_DATA_KEY_COUNTRY);

            Log.e(TAG + ":::", "Output from geocoder " + mAddressOutput + " "+ mCountryName);

            if(mCountryName==null){
                displayAddressOutput();
                //return;
            }
            if (mAddressOutput == null || mAddressOutput.equals("NA")) {
                displayAddressOutput();
                //resetAlarms();

                return;
            }

            fileReader.read1(c);
            fileReader.read2(c);

            mAddressState = fileReader.queryWithDistrict(mAddressOutput);
            Log.e(TAG, "StateName  " + mAddressState);

            if (mAddressState == null || mAddressState.equals("NA")) {
                displayAddressOutput();
                //resetAlarms();
                return;
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = simpleDateFormat.format(new Date());
            int month = Integer.parseInt(currentDate.split("-")[1]);
            Log.e(TAG, "val " + currentDate + " " + month);

            mPrayingTime = fileReader.queryWithState(mAddressState, month);
            if (mPrayingTime == null || mPrayingTime.equals("NA")) {
                mPrayingTime = "Error in Datase , We are fixing it!";
                displayAddressOutput();
                //resetAlarms();

                return;
            }

            if (resultCode == ConstantsForGeocoding.SUCCESS_RESULT) {
                if (!mAddressOutput.equals(sharedPref.getDistName())) {
                    Log.e(TAG, "District changed 2");
                    Log.e(TAG + "::::ss", "val " + sharedPref.getDistName() + " " + sharedPref.getPrayTime() + " " + sharedPref.getStateName());
                    sharedPref.SaveStateName(mAddressState);
                    sharedPref.SaveDistName(mAddressOutput);
                    sharedPref.SavePrayTime(mPrayingTime);

                    try {
                        alarmSetter.setAlarm(false);
                        alarmSetter.setAlarm15(false);
                    } catch (ParseException e) {
                        Log.e(TAG, "error in alarmservice");
                        e.printStackTrace();
                    }

                    //update alarm view

                    Log.e(TAG, "val " + "prference saved in mainactivity");
                }
                Log.e(TAG, "val " + "Address Found In mainactivity");
            }
            displayAddressOutput();
            updateAlarmDisplaViews();
            // Reset. Enable the Fetch Address button and stop showing the progress bar.


        }
    }

    private void resetAlarms() {
        sharedPref.setAlarmDisabled();
        try {
            alarmSetter.setAlarm(false);
            alarmSetter.setAlarm15(false);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        updateAlarmDisplaViews();
    }

    /**
     * Updates fields based on data stored in the bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Check savedInstanceState to see if the address was previously requested.
            if (savedInstanceState.keySet().contains(ADDRESS_REQUESTED_KEY)) {
                mAddressRequested = savedInstanceState.getBoolean(ADDRESS_REQUESTED_KEY);
            }
            // Check savedInstanceState to see if the location address string was previously found
            // and stored in the Bundle. If it was found, display the address string in the UI.
            if (savedInstanceState.keySet().contains(LOCATION_ADDRESS_KEY)) {
                mAddressOutput = savedInstanceState.getString(LOCATION_ADDRESS_KEY);
                displayAddressOutput();
            }
        }
    }

}
