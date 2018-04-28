package jeeryweb.satsang.Utilities;


import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceManager {

    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    SharedPreferences.Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;
    // Sharedpref file name
    private static final String DIST_NAME = "District";
    private static final String Pray_Time = "PrayTime";
    private static final String STATE_NAME = "STATE";
    private static final String Morning = "Morning_pray_time";
    private static final String AlarmDisabled = "AlarmDisabled";

    private static final String Morning15 = "Morning_pray_time15";
    private static  final  String AlarmTuneNameMorning = "AlarmTuneNameMorning";
    private static  final  String AlarmTuneNameEvening = "AlarmTuneNameEvening";






    // Constructor
    public SharedPreferenceManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(DIST_NAME, PRIVATE_MODE);
        editor = pref.edit();

    }


    public void SaveStateName(String state)
    {
        editor.putString(STATE_NAME, state);
        editor.commit();
    }
    public String getStateName()
    {
        return pref.getString(STATE_NAME, "NA");
    }

    public void SaveDistName(String dist)
    {
        editor.putString(DIST_NAME, dist);
        editor.commit();
    }
    public String getDistName()
    {
        return pref.getString(DIST_NAME, "NA");
    }

    public void SavePrayTime(String time)
    {
        editor.putString(Pray_Time, time);
        editor.commit();
    }
    public String getPrayTime()
    {
        return pref.getString(Pray_Time, "NA");
    }


    //alarm tune purposes

    public void SaveMorningTune(String path)
    {
        editor.putString(AlarmTuneNameMorning, path);
        editor.commit();
    }
    public String getMorningAlarmTune()
    {
        return pref.getString(AlarmTuneNameMorning, "NA");
    }

    public void SaveEveningTune(String path)
    {
        editor.putString(AlarmTuneNameEvening, path);
        editor.commit();
    }
    public String getEveningAlarmTune()
    {
        return pref.getString(AlarmTuneNameEvening, "NA");
    }

    public void deleteMorningAlarmTune(){
        editor.remove(AlarmTuneNameMorning).commit();
    }
    public void deleteEveningAlarmTune(){
        editor.remove(AlarmTuneNameEvening).commit();
    }

    //For alarm purposes

    //1 = morning
    //2 = evening
    // 0 = not set
    public void setflagA(int val){
        editor.putInt(Morning , val);
        editor.commit();
    }

    public int getflagA(){
        return pref.getInt(Morning, 0);
    }

    public void setAlarmDisabled(){
        editor.putBoolean( AlarmDisabled, true);
        editor.commit();
    }
    public void unsetAlarmDisabled(){
        editor.putBoolean( AlarmDisabled, false);
        editor.commit();
    }

    public Boolean isALarmDisabled(){
        return pref.getBoolean(AlarmDisabled, false);
    }


    //------------------------------------
    public void setflagA15(int val){
        editor.putInt(Morning15 , val);
        editor.commit();
    }

    public int getflagA15(){
        return pref.getInt(Morning15, 0);
    }


}