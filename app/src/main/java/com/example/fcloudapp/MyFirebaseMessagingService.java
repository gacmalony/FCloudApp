package com.example.fcloudapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.view.ContentInfoCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        Log.v(TAG, "From: "+ message.getFrom());


        if(message.getData().size() >0){
            Log.v(TAG, "Message Data payload: "+ message.getData());
            if(true){
                scheduleJob();
            }else{
                handleNow();
            }
        }


        // Check for notification payload in the message
        if(message.getNotification() != null){
            Log.v(TAG, "Message notification Body: "+ message.getNotification().getBody());

        }
    }

    private void handleNow() {
        Log.v(TAG,"Short Lived Task is done!");
    }

    private void sendRegistrationToServer(String token){

    }

    private void sendNotification(String messageBody){
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , i, PendingIntent.FLAG_MUTABLE);


        String channelId = getString(R.string.default_notification);
        Uri defaultSounduri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                this, channelId
        ).setSmallIcon(R.drawable.belicon)
                .setContentTitle(getString(R.string.fcm_message))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSounduri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(channelId ,"Channel Human readable Title", NotificationManager.IMPORTANCE_DEFAULT);

            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0, notificationBuilder.build());


    }

    private void scheduleJob() {
        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(MyWorker.class).build();
        WorkManager.getInstance(this).beginWith(oneTimeWorkRequest).enqueue();
        
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: "+token);
        sendRegistrationToServer(token);

    }
}
