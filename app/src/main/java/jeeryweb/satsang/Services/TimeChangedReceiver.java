package jeeryweb.satsang.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.ParseException;

import jeeryweb.satsang.Utilities.AlarmSetter;
import jeeryweb.satsang.Utilities.SharedPreferenceManager;

/**
 * Created by Debo#Paul on 4/29/2018.
 */

public class TimeChangedReceiver extends BroadcastReceiver {

    public AlarmSetter aa;
    public SharedPreferenceManager sh;
    private final String Tag = "AlarmDebug";
    @Override
    public void onReceive(Context context, Intent intent) {
        //Do whatever you need to

        Log.e(Tag, "In time cahnged event");
        aa = new AlarmSetter(context);
        sh = new SharedPreferenceManager(context);

        Log.e("AlarmDebug","alarm disablled by switch");
        sh.setAlarmDisabled();

        // continious is false //will be false alays on first trogger
        //cancel both the alarms
        try {
            aa.setAlarm(false);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            aa.setAlarm15(false);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.e(Tag, "alarm cancelled in TImechanged eveny");

        sh.unsetAlarmDisabled();
        try {
            aa.setAlarm(false);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            aa.setAlarm15(false);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.e(Tag, "set again");
    }

}