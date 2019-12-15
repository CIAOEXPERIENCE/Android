package com.aCrmNet.CIAOexperience;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.List;

/**
 * Created by Andrea on 29/11/2017.
 */

public class CiaoApplication extends Application implements BootstrapNotifier {
    private static final String TAG = "CiaoApplication";
    private RegionBootstrap regionBootstrap;
    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "APPLICATION START");
        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        Region region = new Region("test.acrmnet.testbeacon.boostrapRegion", null, null, null);
        regionBootstrap = new RegionBootstrap(this, region);
//        beaconManager.setBackgroundMode(true);
//        beaconManager.setBackgroundBetweenScanPeriod(5000);
    }

    @Override
    public void didEnterRegion(final Region region) {

        Log.d(TAG, "ENTER");
        // This call to disable will make it so the activity below only gets launched the first time a beacon is seen (until the next time the app is launched)
        // if you want the Activity to launch every single time beacons come into view, remove this call.
        //regionBootstrap.disable();


        final MonitorBeacon monitorBeacon = new MonitorBeacon(this);
        monitorBeacon.setBeaconInfoListener(new MonitorBeacon.BeaconInfoListener() {
            @Override
            public void beaconInfo(List<Beacon> beacons) {
                for(Beacon b:beacons){
                    Log.i(TAG, b.getId1() + "");
                    Log.i(TAG, b.getId2() + "");
                    Log.i(TAG, b.getId3() + "");
                }
                int major;
                try {
                    major = Integer.parseInt(beacons.get(0).getId2().toString());
                }catch (Exception e){
                    major = (int) System.currentTimeMillis();
                }
                monitorBeacon.stopService(region);
                notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(major,getNotification(beacons.get(0).getId2()+" ::major",false));
            }
        });



        //Intent intent = new Intent(this, MainActivity.class);
        // IMPORTANT: in the AndroidManifest.xml definition of this activity, you must set android:launchMode="singleInstance" or you will get two instances
        // created when a user launches the activity manually and it gets launched from here.
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//       this.startActivity(intent);
    }

    @Override
    public void didExitRegion(Region region) {
        Log.d(TAG, "EXIT");
        //Toast.makeText(this, "EXIT", Toast.LENGTH_LONG).show();
    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {
        Log.d(TAG, "STATE");
        if(i== MonitorNotifier.INSIDE){
            Log.i(TAG,"STATE:: inside");
        }else if(i==MonitorNotifier.OUTSIDE){
            Log.i(TAG,"STATE:: ouside");
        }

       /* Log.d(TAG, "STATE:: "+i);
        Log.d(TAG, "STATE:: "+region.getId1());
        Log.d(TAG, "STATE:: "+region.getId2());
        Log.d(TAG, "STATE:: "+region.getId3());*/
        //Toast.makeText(this, "STATE:: "+region.getIdentifier(0), Toast.LENGTH_LONG).show();
    }
    private Notification getNotification(String msg, boolean progress) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.attractions)
                .setContentTitle("beacon")
                .setContentText(msg);
        //.addAction(R.drawable.cancel_icon,"nascondi",pHideIntent)
        return notificationBuilder.build();
    }
}
