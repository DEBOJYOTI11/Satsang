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
    private String TimeZoneOfDay  ="Time"; //morning or evening

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_tune_picker);
        textView = (TextView)findViewById(R.id.tune_picker_information);

        sh  = new SharedPreferenceManager(this);
        }
    //1 = morning
    //2 = evening
    public void loadAlarmTuneForMorning(View view){
        Intent intent_upload = new Intent();
        intent_upload.setType("audio/*");
        intent_upload.putExtra(TimeZoneOfDay,1);
        //Bundle b = new Bundle();
        //b.putInt(TimeZoneOfDay,1);
        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent_upload,1);

    }

    public void loadAlarmTuneForEvening(View view){
        Intent intent_upload = new Intent();
        intent_upload.setType("audio/*");
        intent_upload.putExtra(TimeZoneOfDay,2);
       // Bundle b = new Bundle();
        //b.putInt(TimeZoneOfDay,2);

        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent_upload,1);

    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){

        Log.e(Tag, "Onactivity result called");
        if(requestCode == 1){
            Toast.makeText(this, "file choosen",Toast.LENGTH_SHORT).show();
            if(resultCode == RESULT_OK){
                Uri uri = data.getData();
                Bundle b = data.getExtras();

                Log.e(Tag, "dcs "+data.hasExtra(TimeZoneOfDay)+ String.valueOf(b!=null));
                Log.e(Tag, uri.toString());
                Log.e(Tag, uri.getPath());

                Cursor returnCursor =
                        getContentResolver().query(uri, null, null, null, null);
                int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                Log.e(Tag, "size "+String.valueOf(sizeIndex));

                mp =new MediaPlayer();

//              mp.setAudioStreamType(AudioManager.STREAM_MUSIC);

                mp = MediaPlayer.create(this, uri);
                int duration   = mp.getDuration()/1000;
                
                Log.e(Tag, "Duration "+String.valueOf(duration));
                if(duration>240){
                    Toast.makeText(this, "The length of the tune should be less then four minutes", Toast.LENGTH_SHORT).show();
                    return;
                }



                if(b!=null){
                    Log.e(Tag, "has extra");
                    if((int)b.get(TimeZoneOfDay)==1){
                        sh.SaveMorningTune(uri.toString());
                        textView.setText("Succesfully added Alarm Tune for Morning");
                    }
                    else if((int)b.get(TimeZoneOfDay)==2){
                        sh.SaveEveningTune(uri.toString());
                        textView.setText("Succesfully added Alarm Tune for Evening");
                    }
                    else{
                        textView.setText("Some error occurred");
                    }
                    Toast.makeText(this, "Succesfully added alarm tune", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "There was some error", Toast.LENGTH_SHORT).show();
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
    }