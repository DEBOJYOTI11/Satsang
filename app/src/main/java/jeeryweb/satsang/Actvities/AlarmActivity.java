package jeeryweb.satsang.Actvities;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import jeeryweb.satsang.R;
import jeeryweb.satsang.Utilities.AlarmSetter;
import jeeryweb.satsang.Utilities.SharedPreferenceManager;

/**
 * Created by Debo#Paul on 4/8/2018.
 */

public class AlarmActivity extends Activity {

    private MediaPlayer mp;
    TextView textView;
    Button buttonStop;
    final String Tag = "AlarmDebug";
    //only for debug vlaue set to 2********
    int morningOrEveningAlarm=2;
    private SharedPreferenceManager sh;
    public AlarmSetter alarmSetter;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        sh = new SharedPreferenceManager(this);
        textView = (TextView)findViewById(R.id.Information);
        buttonStop = (Button)findViewById(R.id.stopButton);

        Log.e(Tag, "AlackActivity called");


        if(getIntent().hasExtra("AlarmTime")){
            morningOrEveningAlarm  = getIntent().getIntExtra("AlarmTime", 0);
        }



        Log.e(Tag, "eorM "+String.valueOf(morningOrEveningAlarm));

        if(morningOrEveningAlarm==1){
            if(sh.getMorningAlarmTune().equals("NA"))
                mp=MediaPlayer.create(this, R.raw.satsangmorning);
            else{
                grantUriPermission(this.getPackageName(), Uri.parse(sh.getEveningAlarmTune()), Intent.FLAG_GRANT_READ_URI_PERMISSION);
                try{
                    mp=MediaPlayer.create(this, Uri.parse(sh.getMorningAlarmTune()));
                }catch (Exception e){
                    mp=MediaPlayer.create(this, R.raw.satsangmorning);
                }

            }
            textView.setText("Morning Prayer Alarm Ringing");
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    mp.stop();
                    mp.release();
                    finish();
                }
            });
            mp.start();
        }

        else if(morningOrEveningAlarm==2){

            if(sh.getEveningAlarmTune().equals("NA"))
                mp=MediaPlayer.create(this, R.raw.satsangevening);
            else{
                grantUriPermission(this.getPackageName(), Uri.parse(sh.getEveningAlarmTune()), Intent.FLAG_GRANT_READ_URI_PERMISSION);
                try{
                    mp=MediaPlayer.create(this, Uri.parse(sh.getEveningAlarmTune()));
                }catch (Exception e){
                    mp=MediaPlayer.create(this, R.raw.satsangevening);
                }

            }
            textView.setText("Evening Prayer Alarm Ringing");
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

                    mp.stop();
                    mp.release();
                    finish();
                }
            });
            mp.start();
        }

        else{
            textView.setText("Some error occurred!");
            buttonStop.setEnabled(false);
        }
    }

    private void playAlarmUtil(Uri uri, String s){

    }
    public void stopAlarm(View view){
        Log.e(Tag, "stopped alarm");
        textView.setText("Alarm Stopped");
        if (mp.isPlaying()){
            mp.pause();
            mp.seekTo(0);
        }
        finish();
        System.exit(0);
    }
    @Override
    protected void onDestroy() {
// TODO Auto-generated method stub
        super.onDestroy();
        if(mp!=null){
            mp.stop();
            mp.release();
        }

    }
}
