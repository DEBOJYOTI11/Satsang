package jeeryweb.satsang.Services;


import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Network;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jeeryweb.satsang.Data.FileReader;
import jeeryweb.satsang.Utilities.AlarmSetter;
import jeeryweb.satsang.Utilities.ConstantsForGeocoding;
import jeeryweb.satsang.Utilities.SharedPreferenceManager;


public class LocationUpdaterService extends Service {
    private final String TAG=getClass().getSimpleName();

    private LocationManager mLocationManager = null;
    Network network;
    private Intent resultIntent;
    private PendingIntent mPendingIntent;
    Context c;
    private AddressResultReceiver mResultReceiver;
    public AlarmSetter alarmSetter;
    private String mAddressOutput;
    public FileReader  fileReader;
    public SharedPreferenceManager sharedPref;
    private static final int LOCATION_INTERVAL = 10000;
    private static final float LOCATION_DISTANCE = 10f;

    public String latt, longi;
    public static String MY_ACTION = "MY_ACTION";



    private class LocationListener implements android.location.LocationListener
    {
        Location mLastLocation;

        public LocationListener(String provider)
        {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }
        @Override

        public void onLocationChanged(Location location)
        {
//            Log.e(TAG, "onLocationChanged: " + location);

            mLastLocation.set(location);
            latt=Double.toString(location.getLatitude());
            longi=Double.toString(location.getLongitude());
            Log.e(TAG+" onloc-changed", "val "+latt+" "+longi);
            startIntentService(mLastLocation);



        }
        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }
        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }


    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    private void startIntentService(Location mLastLocation) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(ConstantsForGeocoding.RECEIVER, mResultReceiver);
        intent.putExtra(ConstantsForGeocoding.LOCATION_DATA_EXTRA, mLastLocation);
        startService(intent);
    }

    @Override
    public void onCreate()
    {
        Log.e(TAG, "onCreate");
        c=this;
        mResultReceiver = new AddressResultReceiver(new Handler());
        sharedPref = new SharedPreferenceManager(this);
        alarmSetter = new AlarmSetter(this);
        requestLocationUpdates();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        fileReader = new FileReader();
        fileReader.read1(c);
        fileReader.read2(c);

        Log.e(TAG, "onStartCommand");

        Toast.makeText(this, "Location service starting", Toast.LENGTH_SHORT).show();
        //requestLocationUpdates();
        return START_STICKY;
    }


    @Override
    public void onDestroy()
    {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                    Log.e(TAG, "location manager removed");
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listeners, ignore", ex);
                }
            }
        }

        Intent broadcastIntent = new Intent("com.jerryweb.restartService");
        sendBroadcast(broadcastIntent);

    }

    //for service
    public IBinder onBind(Intent arg0){
        return null;
    }

    //request location updates regularly
    void requestLocationUpdates()
    {
        //initialize location manager
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }

        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.e(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.e(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.e(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.e(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }


    public void notifyDistrictChanged(){

    }


    //Action Reciever -----------------------------------------------
    private class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            mAddressOutput = resultData.getString(ConstantsForGeocoding.RESULT_DATA_KEY);

            Log.e(TAG, "District is "+mAddressOutput);
            if(mAddressOutput ==null){
                sharedPref.SaveDistName("Cannot get your location");
                return;
            }
            String res = fileReader.queryWithDistrict(mAddressOutput);
            Log.e(TAG,"State is "+res);

            if(res==null || res=="NA")
                return;

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = simpleDateFormat.format(new Date());
            int month = Integer.parseInt(currentDate.split("-")[1]);
            String prayingTime = fileReader.queryWithState(res,month);
            Log.e(TAG, "Praying time "+prayingTime);
            if(prayingTime == null || prayingTime =="NA")
                return;

            // Show a toast message if an address was found.
            if (resultCode == ConstantsForGeocoding.SUCCESS_RESULT) {


                if(!mAddressOutput.equals(sharedPref.getDistName())){
                    Log.e("AlarmFuck", "District changed");
                    sharedPref.SaveStateName(res);
                    sharedPref.SaveDistName(mAddressOutput);
                    sharedPref.SavePrayTime(prayingTime);

                    Intent intent = new Intent();
                    intent.setAction(MY_ACTION);

                    intent.putExtra("District", mAddressOutput);
                    intent.putExtra("State", res);
                    intent.putExtra("PrayingTime", prayingTime);
                    try {
                        alarmSetter.setAlarm(false);   //comtinious
                        alarmSetter.setAlarm15(false);
                    } catch (ParseException e) {
                        Log.e("AlarmFuck", "error in alarmservice 2");
                        e.printStackTrace();
                    }
                    sendBroadcast(intent);

                    Log.e(TAG, "val "+"Preference saved in Service");
                }

                Log.e(TAG, "val "+"address found");

            }

        }
    }

}
