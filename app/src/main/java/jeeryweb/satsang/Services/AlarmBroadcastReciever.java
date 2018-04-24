package jeeryweb.satsang.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jeeryweb.satsang.Actvities.AlarmActivity;
import jeeryweb.satsang.Utilities.AlarmSetter;
import jeeryweb.satsang.Utilities.SharedPreferenceManager;


public class AlarmBroadcastReciever extends BroadcastReceiver {
   MediaPlayer mp;
   AlarmSetter alarmSetter;
   SharedPreferenceManager sh;
   final private String TAG = "AlarmDebug";
   public String AlarmFormat = "alarmformat";

   @Override
   public void onReceive(Context context, Intent intent) {
       int formatOfALarm=0;


       if (intent.hasExtra(AlarmFormat)) {
           formatOfALarm = intent.getIntExtra(AlarmFormat,0);
       } else {
           throw new IllegalArgumentException("Activity cannot find  extras " + AlarmFormat);
       }
       alarmSetter = new AlarmSetter(context);
       sh = new SharedPreferenceManager(context);

       SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss a");
       String currentTime = simpleDateFormat.format(new Date());

       Log.e(TAG, "!!!!!!!!!!!!!!!!!!!! Alarm called at time " + currentTime);
       Log.e(TAG, "d "+sh.getflagA()+" "+sh.getflagA15());
       Log.e(TAG, "alarmformat = "+formatOfALarm);
//
//       NotificationCompat.Builder builder =
//               new NotificationCompat.Builder(context)
//                       .setSmallIcon(R.drawable.ic_satsang)
//                       .setContentTitle("Satsang")
//                       .setContentText("The time is " + currentTime)
//                       .setStyle(new NotificationCompat.BigTextStyle())
//                       .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                        .setAutoCancel(true);
//       builder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
//
//       Intent notificationIntent = new Intent(context, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
//               Intent.FLAG_ACTIVITY_CLEAR_TASK);
//       PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
//               PendingIntent.FLAG_UPDATE_CURRENT);
//       builder.setContentIntent(contentIntent);
//
//       // Add as notification
//       NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//       manager.notify(R.string.FM_NOTIFICATION_ID, builder.build());
//
//       mp=MediaPlayer.create(context, R.raw.alarm );
//  //     mp.start();
//
//       Toast.makeText(context, "Alarm....", Toast.LENGTH_LONG).show();

       Intent intentForAlramActivity = new Intent(context, AlarmActivity.class);
       intentForAlramActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

       try {

           if(formatOfALarm==0)
           {
               if(sh.getflagA()==1)
                    intentForAlramActivity.putExtra("AlarmTime", 1); //mornign alaeram
               if(sh.getflagA()==2)
                   intentForAlramActivity.putExtra("AlarmTime", 2); //evening alarm
               alarmSetter.setAlarm(true); //mane continious is true
               context.startActivity(intentForAlramActivity);
           }

           if(formatOfALarm==15){
               if(sh.getflagA15()==1)
                   intentForAlramActivity.putExtra("AlarmTime", 1);
               if(sh.getflagA15()==2)
                   intentForAlramActivity.putExtra("AlarmTime", 2);
               alarmSetter.setAlarm15(true);
               context.startActivity(intentForAlramActivity);

           }

       } catch (ParseException e) {
           Log.e(TAG, "error in alarmservice");
           e.printStackTrace();
       }
       Log.e(TAG, "Next alarm set by setting cont= true by ALarmBroadcastrecieverclass!!");
       Log.e(TAG, "_____________________________________");

   }


}
