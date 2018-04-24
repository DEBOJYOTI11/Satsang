package jeeryweb.satsang.Utilities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import jeeryweb.satsang.Services.AlarmBroadcastReciever;

/**
 * Created by Debo#Paul on 4/1/2018.
 */

public class AlarmSetter {
    final private String TAG = "AlarmDebug";
    public SharedPreferenceManager sharedPref;
    Context _c;
    SimpleDateFormat sdfDate, sdfDatePlusTime;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private int requestCode, requestCode15;
    public String AlarmFormat = "alarmformat";
    private String currentDate;


    public AlarmSetter(Context c) {
        this._c = c;
        sharedPref = new SharedPreferenceManager(_c);
        requestCode = 234324243;
        requestCode15 = 230000000;
        sdfDate = new SimpleDateFormat("yyyy:MM:dd"); //for date
        sdfDatePlusTime = new SimpleDateFormat("yyyy:MM:dd HH:mm"); //for time

    }

    public void setAlarm(Boolean cont) throws ParseException {

        if(sharedPref.getPrayTime().equals("NA")){
            sharedPref.setflagA(0);
            return;
        }
        Log.e(TAG, "SerAlarm() called");

        //if alarm disabbled
        if (sharedPref.isALarmDisabled()) {
            if (sharedPref.getflagA() != 0) {   //flag a says morning evening or not set 120
                cancelAlarm();
                sharedPref.setflagA(0);
            }
            Log.e(TAG, " Alarm not set as setting is disallowed");
            return;
        }

        //if alarm not disaabled
        if (cont) {

            Log.e(TAG, "Contiious flag is set");
            //morning alarm was set
            if (sharedPref.getflagA() == 1) { //morning alreadyset
                Log.e(TAG, "Morning prayer ALarm was called");

                String s = sharedPref.getPrayTime().split(",")[1];
                int h = Integer.parseInt(s.split(":")[0]);
                int m = Integer.parseInt(s.split(":")[1]);

                long setTime;
                Date d = new Date();
                currentDate = sdfDate.format(d);

                String temp = currentDate + " " + h + ":" + m;
                Date date = sdfDatePlusTime.parse(temp);
                setTime = date.getTime();

                alarmMgr = (AlarmManager) _c.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(_c, AlarmBroadcastReciever.class);
                intent.putExtra(AlarmFormat, 0);  //0 = current, 15 = 15 mins before alarm
                alarmIntent = PendingIntent.getBroadcast(_c, requestCode, intent, 0);
                alarmMgr.set(AlarmManager.RTC_WAKEUP,
                        setTime, alarmIntent);

                sharedPref.setflagA(2); //now setting evening
                //Toast.makeText(_c, "Alarm set for your next Evening time prayer", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Evening prayer alarm is being set!" + setTime);

            } else { //evening was set, now we will set morning
                Log.e(TAG, "Evening prayer ALarm was called");


                String s = sharedPref.getPrayTime().split(",")[0];
                int h = Integer.parseInt(s.split(":")[0]);
                int m = Integer.parseInt(s.split(":")[1]);

                long setTime;


                Date d = new Date();
                currentDate = parseDate(d);
                String temp = currentDate + " " + h + ":" + m;
                Date date = sdfDatePlusTime.parse(temp);
                setTime = date.getTime();

                alarmMgr = (AlarmManager) _c.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(_c, AlarmBroadcastReciever.class);
                intent.putExtra(AlarmFormat, 0);  //0 = current, 15 = 15 mins before alarm
                alarmIntent = PendingIntent.getBroadcast(_c, requestCode, intent, 0);

                alarmMgr.set(AlarmManager.RTC_WAKEUP,
                        setTime, alarmIntent);

                sharedPref.setflagA(1);  //now setting in morning

               // Toast.makeText(_c, "Alarm set for your next Morning time prayer", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Morning prayer alarm is being set!" + setTime);
            }
            Log.e(TAG, "_______________________________");
            return;
        }

        if (sharedPref.getflagA() != 0) {
            cancelAlarm();
            sharedPref.setflagA(0);
        }

        Log.e(TAG, "In not continious part, i.e location changed or main activity opened!");


        Boolean isCloserToMorning = IsCloserToMorning();
        Log.e(TAG, "isclosertoMornign is  "+isCloserToMorning);
        int h, m;


        if (isCloserToMorning) {
            String s = sharedPref.getPrayTime().split(",")[0];
            h = Integer.parseInt(s.split(":")[0]);
            m = Integer.parseInt(s.split(":")[1]);

            s = sharedPref.getPrayTime().split(",")[1];
            int h_temp = Integer.parseInt(s.split(":")[0]);
            int m_temp = Integer.parseInt(s.split(":")[1]);

            Date d = new Date();
            SimpleDateFormat s_temp = new SimpleDateFormat("HH");
            int temp1 = Integer.parseInt(s_temp.format(d));  //get current hour

            if(temp1 >= h_temp){   //check if current hour is next day or presetn day
                Date d_temp  = new Date();
                currentDate = parseDate(d_temp);
            }else{
                Date d_temp  = new Date();
                currentDate = sdfDate.format(d_temp);
            }



            sharedPref.setflagA(1);
            //Toast.makeText(_c, "Alarm set for your next Morning time Prayer",Toast.LENGTH_SHORT).show();
            Log.e(TAG, "ALarm set for morning time");
        } else {
            String s = sharedPref.getPrayTime().split(",")[1];
            h = Integer.parseInt(s.split(":")[0]);
            m = Integer.parseInt(s.split(":")[1]);
            Date d = new Date();
            currentDate = sdfDate.format(d);
            sharedPref.setflagA(2);

            Log.e(TAG, "ALarm set for Evening time");
        }

        long setTime;


        String temp = currentDate+" "+h+":"+m;

        Date date = sdfDatePlusTime.parse(temp);
        setTime = date.getTime();

        alarmMgr = (AlarmManager) _c.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(_c, AlarmBroadcastReciever.class);
        intent.putExtra(AlarmFormat, 0);  //0 = current, 15 = 15 mins before alarm
        alarmIntent = PendingIntent.getBroadcast(_c, requestCode, intent, 0);

        alarmMgr.set(AlarmManager.RTC_WAKEUP,
                setTime, alarmIntent);

        Log.e(TAG, "ALarm set at time " + setTime);
        Log.e(TAG, "-----------------------------------------------------------------------------------------------");

    }

    //checking for the need for a date increment
    private String parseDate(Date d) {


        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(Calendar.DATE, 1);
        d = c.getTime();
        return sdfDate.format(d);
    }

    public void setAlarm15(Boolean cont) throws ParseException {

        if(sharedPref.getPrayTime().equals("NA")){
            sharedPref.setflagA15(0);
            return;
        }

        Log.e(TAG, "SetAlarm15() function called");
        if (sharedPref.isALarmDisabled()) {
            if (sharedPref.getflagA15() != 0) {
                cancelAlarm15();
                sharedPref.setflagA15(0);
            }
            Log.e(TAG, " 15) Alarm not set as setting is disallowed");
            return;
        }

        if (cont) {
            Log.e(TAG, "Contniuos is "+cont);

            //mornng laram was set
            if (sharedPref.getflagA15() == 1) {
                String s = sharedPref.getPrayTime().split(",")[1];
                int h = Integer.parseInt(s.split(":")[0]);
                int m = Integer.parseInt(s.split(":")[1]);

                if (m >= 15) {
                    m = m - 15;
                } else {
                    int temp = 15 - m;
                    m = 60 - temp;
                    if (h == 0) {
                        h = 23;

                    } else {
                        h = h - 1;
                    }


                }
                long setTime;

                Date d = new Date();
                currentDate = sdfDate.format(d);
                String temp = currentDate + " " + h + ":" + m;

                Date date = sdfDatePlusTime.parse(temp);
                setTime = date.getTime();

                alarmMgr = (AlarmManager) _c.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(_c, AlarmBroadcastReciever.class);
                intent.putExtra(AlarmFormat, 15);  //0 = current, 15 = 15 mins before alarm
                alarmIntent = PendingIntent.getBroadcast(_c, requestCode15, intent, 0);
                 alarmMgr.set(AlarmManager.RTC_WAKEUP,
                        setTime, alarmIntent);

                sharedPref.setflagA15(2);
               // Toast.makeText(_c, "ALarm set for reminding you 15 mins early to your next Evening prayer",Toast.LENGTH_SHORT).show();
                Log.e(TAG,  "15) Evening alarm is set at " + setTime);
            } else {


                String s = sharedPref.getPrayTime().split(",")[0];
                int h = Integer.parseInt(s.split(":")[0]);
                int m = Integer.parseInt(s.split(":")[1]);

                if (m >= 15) {
                    m = m - 15;
                } else {
                    int temp = 15 - m;
                    m = 60 - temp;
                    if (h == 0) {
                        h = 23;

                    } else {
                        h = h - 1;
                    }
                }

                long setTime;


                Date d = new Date();
                currentDate = parseDate(d);
                String temp = currentDate + " " + h + ":" + m;
                Date date = sdfDatePlusTime.parse(temp);
                setTime = date.getTime();

                alarmMgr = (AlarmManager) _c.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(_c, AlarmBroadcastReciever.class);
                intent.putExtra(AlarmFormat, 15);  //0 = current, 15 = 15 mins before alarm
                alarmIntent = PendingIntent.getBroadcast(_c, requestCode15, intent, 0);
                alarmMgr.set(AlarmManager.RTC_WAKEUP,
                        setTime, alarmIntent);

                sharedPref.setflagA15(1);
                //Toast.makeText(_c, "ALarm set for reminding you 15 mins early to your next Morning prayer",Toast.LENGTH_SHORT).show();
                Log.e(TAG,  "15) Morning alarm is set at " + setTime);
            }
            Log.e(TAG, " _______________________________________");
            return;
        }

        if (sharedPref.getflagA15() != 0) {
            cancelAlarm15();
            sharedPref.setflagA15(0);
        }
        Log.e(TAG, "15) Continious variable is False, i.e lcoation hasc changed or method has launched");


        Boolean isCloserToMorning = IsCloserToMorning15();
        Log.e(TAG, "15). isCLoserToMorning variable is "+isCloserToMorning);
        int h, m;


        if (isCloserToMorning) {
            String s = sharedPref.getPrayTime().split(",")[0];
            h = Integer.parseInt(s.split(":")[0]);
            m = Integer.parseInt(s.split(":")[1]);

            if (m >= 15) {
                m = m - 15;
            } else {
                int temp = 15 - m;
                m = 60 - temp;
                if (h == 0) {
                    h = 23;

                } else {
                    h = h - 1;
                }
            }

            s = sharedPref.getPrayTime().split(",")[1];
            int h_temp = Integer.parseInt(s.split(":")[0]);
            int m_temp = Integer.parseInt(s.split(":")[1]);

            if (m_temp >= 15) {
                m_temp = m_temp - 15;
            } else {
                int temp = 15 - m_temp;
                m_temp = 60 - temp;
                if (h_temp == 0) {
                    h_temp = 23;

                } else {
                    h_temp = h_temp - 1;
                }
            }

            Date d = new Date();

            SimpleDateFormat s_temp = new SimpleDateFormat("HH:mm");

            String temp1 = s_temp.format(d);  //get current hour
            String temp2[] =  temp1.split(":");
            int curr_hour = Integer.parseInt(temp2[0]);
            int curr_min = Integer.parseInt(temp2[1]);

            if(curr_hour==0 && curr_min<=15){
                Date d_temp  = new Date();
                currentDate = sdfDate.format(d_temp);

            sharedPref.setflagA15(1);
            // Toast.makeText(_c, "ALarm set for reminding you 15 mins early to your next Morning prayer",Toast.LENGTH_SHORT).show();
            Log.e(TAG,  "15) Morning alarm is set");

            }else{



            if (curr_min >= 15) {
                curr_min = curr_min - 15;
            } else {
                int temp = 15 - curr_min;
                curr_min = 60 - temp;
                if (curr_hour == 0) {
                    curr_hour = 23;

                } else {
                    curr_hour = curr_hour - 1;
                }
            }



            if(curr_hour >= h_temp){   //check if current hour is next day or present day
                Date d_temp= new Date();
                currentDate =  parseDate(d_temp);
            }
            else{

                Date d_temp  = new Date();
                currentDate = sdfDate.format(d_temp);
            }
            sharedPref.setflagA15(1);
           // Toast.makeText(_c, "ALarm set for reminding you 15 mins early to your next Morning prayer",Toast.LENGTH_SHORT).show();
            Log.e(TAG,  "15) Morning alarm is set");
        }
        }else {
            String s = sharedPref.getPrayTime().split(",")[1];
            h = Integer.parseInt(s.split(":")[0]);
            m = Integer.parseInt(s.split(":")[1]);
            if (m >= 15) {
                m = m - 15;
            } else {
                int temp = 15 - m;
                m = 60 - temp;
                if (h == 0) {
                    h = 23;

                } else {
                    h = h - 1;
                }
            }
            Date d = new Date();
            currentDate = sdfDate.format(d);
            sharedPref.setflagA15(2);
            //Toast.makeText(_c, "ALarm set for reminding you 15 mins early to your next Evening prayer",Toast.LENGTH_SHORT).show();
            Log.e(TAG,  "15) Evening alarm is set at ");
        }

        long setTime;


        String temp = currentDate+" "+h+":"+m;
        Date date = sdfDatePlusTime.parse(temp);
        setTime = date.getTime();

        alarmMgr = (AlarmManager) _c.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(_c, AlarmBroadcastReciever.class);
        intent.putExtra(AlarmFormat, 15);  //0 = current, 15 = 15 mins before alarm
        alarmIntent = PendingIntent.getBroadcast(_c, requestCode15, intent, 0);
        alarmMgr.set(AlarmManager.RTC_WAKEUP,
                setTime, alarmIntent);

        Log.e(TAG, "15). ALarm set at time " + setTime);
        Log.e(TAG, "-----------------------------------------------------------------------------------------------");

    }


    public boolean IsCloserToMorning() {

        SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");
        String currentTime = hourFormat.format(new Date());

        //current time
        String[] s = currentTime.split(":");
        int h = Integer.parseInt(s[0]);
        int m = Integer.parseInt(s[1]);

        //morning time
        String s1 = sharedPref.getPrayTime().split(",")[0];
        int h1 = Integer.parseInt(s1.split(":")[0]);
        int m1 = Integer.parseInt(s1.split(":")[1]);

        //evening time
        String s2 = sharedPref.getPrayTime().split(",")[1];
        int h2 = Integer.parseInt(s2.split(":")[0]);
        int m2 = Integer.parseInt(s2.split(":")[1]);

        Log.e(TAG, "Ismorning called with para "+h+" "+h1+" "+h2);
        //compare with evening times
        if (h > h2)
            return true;
        if (h == h2) {
            if (m > m2)
                return true;
            else
                return false;
        }

        if (h < h1) {
            return true;
        }
        if (h > h1) {
            return false;
        }
        if (h == h1) {
            if (m > m1) {
                return false;
            }
            return true;
        }

        return true;
    }

    public boolean IsCloserToMorning15() {

        SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");
        String currentTime = hourFormat.format(new Date());

        String[] s = currentTime.split(":");
        int h = Integer.parseInt(s[0]);
        int m = Integer.parseInt(s[1]);

        String s1 = sharedPref.getPrayTime().split(",")[0];
        int h1 = Integer.parseInt(s1.split(":")[0]);
        int m1 = Integer.parseInt(s1.split(":")[1]);

        if (m1 >= 15) {
            m1 = m1 - 15;
        } else {
            int temp = 15 - m1;
            m1 = 60 - temp;
            if (h1 == 0) {
                h1 = 23;

            } else {
                h1 = h1 - 1;
            }
        }

        String s2 = sharedPref.getPrayTime().split(",")[1];
        int h2 = Integer.parseInt(s2.split(":")[0]);
        int m2 = Integer.parseInt(s2.split(":")[1]);

        if (m2 >= 15) {
            m2 = m2 - 15;
        } else {
            int temp = 15 - m2;
            m2 = 60 - temp;
            if (h2 == 0) {
                h2 = 23;

            } else {
                h2 = h2 - 1;
            }
        }
        Log.e(TAG, "Ismorning called with para "+h+" "+h1+" "+h2);
        //compare with evening times
        if (h > h2)
            return true;
        if (h == h2) {
            if (m > m2)
                return true;
            else
                return false;
        }

        if (h < h1) {
            return true;
        }
        if (h > h1) {
            return false;
        }
        if (h == h1) {
            if (m > m1) {
                return false;
            }
            return true;
        }

        return true;
    }


    public void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) _c.getSystemService(_c.ALARM_SERVICE);
        Intent myIntent = new Intent(_c, AlarmBroadcastReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                _c, requestCode, myIntent,
                0);

        alarmManager.cancel(pendingIntent);
        Log.e(TAG, "Alarm is cancelled");
    }


    public void cancelAlarm15() {
        AlarmManager alarmManager = (AlarmManager) _c.getSystemService(_c.ALARM_SERVICE);
        Intent myIntent = new Intent(_c, AlarmBroadcastReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                _c, requestCode15, myIntent,
                0);

        alarmManager.cancel(pendingIntent);
        Log.e(TAG, "15). Alarm is cancelled");
    }

}
