package jeeryweb.satsang.Actvities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import jeeryweb.satsang.R;
import jeeryweb.satsang.Utilities.SharedPreferenceManager;

public class alarmTunePicker extends Activity {

    public final static String Tag = alarmTunePicker.class.getSimpleName();
    public MediaPlayer mp;
    private SharedPreferenceManager sh;
    private TextView textView;
    private String TimeZoneOfDay  ="TimeZoneOfDay"; //Key value for morning or evening
    public int timezoneofday=0;    //global variable set in oncreate from savedInstanceState

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_tune_picker);
        textView = (TextView)findViewById(R.id.tune_picker_information);
        sh  = new SharedPreferenceManager(this);

        if(savedInstanceState!=null){
            timezoneofday  = savedInstanceState.getInt(TimeZoneOfDay);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        Log.v(Tag, "Inside of onSaveInstanceState");
        state.putInt(TimeZoneOfDay, timezoneofday);
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.v(Tag, "Inside of onRestoreInstanceState");
        timezoneofday = savedInstanceState.getInt(TimeZoneOfDay);
    }
    //1 = morning
    //2 = evening
    public void loadAlarmTuneForMorning(View view){
        Intent intent_upload = new Intent();
        intent_upload.setType("audio/*");
        //intent_upload.putExtra(TimeZoneOfDay,1);
        //Bundle b = new Bundle();
        //b.putInt(TimeZoneOfDay,1);
        timezoneofday = 1;
        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent_upload,1);

    }

    public void loadAlarmTuneForEvening(View view){
        Intent intent_upload = new Intent();
        intent_upload.setType("audio/*");
        intent_upload.putExtra(TimeZoneOfDay,2);
       // Bundle b = new Bundle();
        //b.putInt(TimeZoneOfDay,2);
        timezoneofday = 2;
        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent_upload,1);

    }

    public void deleteMorningAlarmTune(View view){
        Log.e(Tag, "Deleted");
        sh.deleteMorningAlarmTune();
        Toast.makeText(this, "Succesfully deleted", Toast.LENGTH_SHORT).show();
    }
    public void deleteEveningAlarmTune(View view){
        Log.e(Tag, "Deleted");
        sh.deleteEveningAlarmTune();
        Toast.makeText(this, "Succesfully deleted", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){

        Log.e(Tag, "Onactivity result called");
        if(requestCode == 1){
            Toast.makeText(this, "file choosen",Toast.LENGTH_SHORT).show();
            if(resultCode == RESULT_OK){
                Uri uri = data.getData();
                Bundle b = data.getExtras();


                grantUriPermission(this.getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                Log.e(Tag, "dcs "+data.hasExtra(TimeZoneOfDay)+ String.valueOf(b!=null));
                Log.e(Tag, uri.toString());
                Log.e(Tag, uri.getPath());

                Cursor returnCursor =
                        getContentResolver().query(uri, null, null, null, null);
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                Log.e(Tag, "size "+String.valueOf(sizeIndex));

       //         mp =new MediaPlayer();

//              mp.setAudioStreamType(AudioManager.STREAM_MUSIC);

                mp = MediaPlayer.create(this, uri);
                int duration   = mp.getDuration()/1000;
                
                Log.e(Tag, "Duration "+String.valueOf(duration));
                if(duration>240){
                    Toast.makeText(this, "The length of the tune should be less then four minutes", Toast.LENGTH_SHORT).show();
                    restoreTextWidgets();
                    return;
                }


                Log.e(Tag, "has extra");
                if(timezoneofday==1){
                    sh.SaveMorningTune(uri.toString());
                    textView.setText("Succesfully added Alarm Tune for Morning");
                    Toast.makeText(this, "Succesfully added alarm tune", Toast.LENGTH_SHORT).show();
                }
                else if(timezoneofday==2){
                    sh.SaveEveningTune(uri.toString());
                    textView.setText("Succesfully added Alarm Tune for Evening");
                    Toast.makeText(this, "Succesfully added alarm tune", Toast.LENGTH_SHORT).show();
                }
                else{
                    textView.setText("Some error occurred");
                }


               // mp.start();

//                try {
//                    mp.setDataSource(this,uri);
//
//                  } catch (IllegalArgumentException e) {
//                    Toast.makeText(getApplicationContext(), "You might not set the URI correctly! argument", Toast.LENGTH_LONG).show();
//                } catch (SecurityException e) {
//                    Toast.makeText(getApplicationContext(), "You might not set the URI correctly! security", Toast.LENGTH_LONG).show();
//                } catch (IllegalStateException e) {
//                    Toast.makeText(getApplicationContext(), "You might not set the URI correctly! state", Toast.LENGTH_LONG).show();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                    public void onPrepared(MediaPlayer mp) {
//                        int duration   = mp.getDuration()/1000;
//                        Log.e(Tag, "Duration "+String.valueOf(duration));
//
//                        if(duration>240)
//                            Toast.makeText(getApplicationContext(),"The alarm tune length should be less then 4 mins",Toast.LENGTH_SHORT).show();
//                        else{
//
//                            mp.start();
//                        }
//
//
//                    }
//                });
//                mp.prepareAsync();

//                mp.setOnPreparedListener(new OnPreparedListener() {
//                    @Override
//                    public void onPrepared(MediaPlayer mp) {
//                        mp.prepareAsync();
//                        int duration   = mp.getDuration();
//
//                        Log.e(Tag, "Duration "+String.valueOf(duration));
//
//                        try {
//                            mp.prepare();
//                        } catch (IllegalStateException e) {
//                            Toast.makeText(getApplicationContext(), "You might not set the URI correctly!!", Toast.LENGTH_LONG).show();
//                        } catch (IOException e) {
//                            Toast.makeText(getApplicationContext(), "You might not set the URI correctly!!!", Toast.LENGTH_LONG).show();
//                        }
//                        mp.start();
//                    }
//
//                });

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void restoreTextWidgets(){
        textView.setText("");
    }
    }