package com.example.go4lunch.utils.notification;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import com.example.go4lunch.R;
import android.content.ContextWrapper;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationHelper extends ContextWrapper {

    public static final String channelID = "channelID";
    public static final String channelName = "Channel Name";

    private NotificationManager mManager;

    public NotificationHelper(Context base) {
        super(base);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();

        }
    }

    /**
     * For api 26 and up
     */
    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);

        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    public NotificationCompat.Builder getChannelNotification(String notifMessage) {

        return new NotificationCompat.Builder(getApplicationContext(), channelID)

                .setContentTitle(getString(R.string.title_alarm))
                .setContentText(notifMessage)
                .setSmallIcon(R.drawable.white_bowl_lowsize)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notifMessage));
    }
}

