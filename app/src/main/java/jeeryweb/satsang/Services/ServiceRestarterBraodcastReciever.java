package jeeryweb.satsang.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ServiceRestarterBraodcastReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("LocationUpdaterService", "Service Stops! Oooooooooooooppppssssss!!!!");
        context.startService(new Intent(context, LocationUpdaterService.class));;
    }
}