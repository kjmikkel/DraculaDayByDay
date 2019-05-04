package com.jensen.draculadaybyday.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;

public class NotificationUtils extends ContextWrapper {

    private NotificationManager mManager;

    public static final String DRACULA_CHANNEL_ID = "com.jensen.draculadaybyday";
    public static final String DRACULA_CHANNEL_NAME = "DraculaDayByDay Channel";

    public NotificationUtils(Context base) {
        super(base);
        createChannels();
    }

    public void createChannels() {
        // Create the channel
        NotificationChannel draculaChannel = new NotificationChannel(DRACULA_CHANNEL_ID, DRACULA_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);



        // Set the lights
        setLights(false, Color.BLACK);
        // Set the vibration
        setVibration(false);
        // Set the lock screen visibility
        setLockScreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(draculaChannel);
    }

    public void setLights(boolean setLights, int color) {
        // Enable lights
        NotificationChannel draculaChannel = getManager().getNotificationChannel(DRACULA_CHANNEL_ID);
        draculaChannel.enableLights(setLights);
        draculaChannel.setLightColor(color);
    }

    public void setVibration(boolean vibration) {
        NotificationChannel draculaChannel = getManager().getNotificationChannel(DRACULA_CHANNEL_ID);
        draculaChannel.enableVibration(vibration);
    }

    public void setLockScreenVisibility(int screenVisibility) {
        NotificationChannel draculaChannel = getManager().getNotificationChannel(DRACULA_CHANNEL_ID);
        draculaChannel.setLockscreenVisibility(screenVisibility);
    }

    private NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return mManager;
    }
}