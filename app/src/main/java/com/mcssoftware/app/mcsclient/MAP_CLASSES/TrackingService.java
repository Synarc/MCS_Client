package com.mcssoftware.app.mcsclient.MAP_CLASSES;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mcssoftware.app.mcsclient.R;

public class TrackingService extends Service {

    SharedPreferences sharedPreferences;
    public TrackingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

sharedPreferences = getSharedPreferences("CarPass", MODE_PRIVATE);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PickUpRead/"+user.getUid());



        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

             if (!dataSnapshot.child("Car").getValue().toString().equals("NA")) {

                // sharedPreferences.edit().putString("cartotrack", dataSnapshot.child("Car").getValue().toString()).apply();
                 buildNotification("clientService", "client_background_service", "Chauffeur is coming to Pick you. Car: " + dataSnapshot.child("Car").getValue().toString());
             }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // startForeground(101, getNotification());

    }




    private void buildNotification(String channelID, String cahannelName, String message) {

        String chanID;


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            chanID = createNotificationChannelId(channelID, cahannelName);

        }
        else{
            chanID = "";
        }

        String stop = "stop";
        registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);
        // Create the persistent notification


        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder
                //   if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                builder = new NotificationCompat.Builder(this, chanID)//Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(message)


                //Make this notification ongoing so it can’t be dismissed by the user//

                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setSound(alarmSound)
                .setOngoing(true)
                .setContentIntent(broadcastIntent)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark);



        startForeground(1025, builder.build());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String createNotificationChannelId(String channelid, String channelName) {

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationChannel chan = new NotificationChannel(channelid, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.YELLOW);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        chan.canShowBadge();
        chan.enableVibration(true);
        chan.setSound(alarmSound,
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setFlags(AudioAttributes.USAGE_NOTIFICATION)
                        .build());
        chan.setImportance(NotificationManager.IMPORTANCE_HIGH);

        NotificationManager service =  getSystemService(NotificationManager.class);

        service.createNotificationChannel(chan);

        return channelid;


    }

    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

//Unregister the BroadcastReceiver when the notification is tapped//

            unregisterReceiver(stopReceiver);

//Stop the Service//

            //
            stopSelf();
        }
    };



}
