package com.s92064476.samsungnote2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "clash_alarm_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String description = intent.getStringExtra("description");
        String accountName = intent.getStringExtra("accountName"); // NEW
        boolean isPreAlert = intent.getBooleanExtra("isPreAlert", false);

        if (description == null) description = "Upgrade Finished";
        if (accountName == null) accountName = "Clash of Clans";

        if (isPreAlert) {
            // Pre-alert notification
            showStandardNotification(context, "Ready in 5m: " + description + " (" + accountName + ")");
        } else {
            // Pop-up Activity
            Intent i = new Intent(context, AlarmTriggerActivity.class);
            i.putExtra("description", description);
            i.putExtra("accountName", accountName); // PASS IT ON
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);
        }
    }

    private void showStandardNotification(Context context, String msg) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Clash Timer", NotificationManager.IMPORTANCE_HIGH);
            nm.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle("Upgrade Alert")
                .setContentText(msg)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true);

        nm.notify((int)System.currentTimeMillis(), builder.build());
    }
}