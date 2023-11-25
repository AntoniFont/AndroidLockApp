package com.example.applock;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.concurrent.locks.Lock;

public class MyService extends Service implements PhpRequestHandler.OnResultListener {

    private boolean permisoOverlaysPedido = false;
    private boolean permisoStatsPedido = false;
    private Handler handler;
    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "your_channel_id";
    private static final CharSequence CHANNEL_NAME = "Your Channel Name";

    private boolean shouldBlock = false;

    private MyService esto;
    public MyService(){

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        esto = this;
        createNotificationChannel();

        Notification notification = createNotification();

        // Start the service in the foreground
        startForeground(NOTIFICATION_ID, notification);

        //Intent epicIntent = new Intent(Settings.)

        // Check if the stats permission is not granted yet
        if(permisoStatsPedido == false){
            permisoStatsPedido = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Intent intentillo = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                intentillo.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentillo);
            }
        }

        // Check if the overlays permission is not granted yet
        if ((!(Settings.canDrawOverlays(this)))) {
            if(!permisoOverlaysPedido){
                System.out.println("PIDIENDO PERMISOS :)");
                permisoOverlaysPedido = true;
                // If not, request the permission
                Intent nuevoIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + this.getPackageName()));
                nuevoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(nuevoIntent);
            }

        }
        this.handler = new Handler();
        handler.postDelayed(runnable, 1000);
        return START_STICKY;
    }

    private Notification createNotification() {
        // Create and return a Notification object
        // Customize it according to your app's requirements
        // You can use NotificationCompat.Builder for compatibility across different Android versions
        // Example:
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Your Service")
                .setContentText("Service is running in the foreground");
                //.setSmallIcon(R.drawable.my_ic_notification);

        return builder.build();
    }

    private void createNotificationChannel() {
        // Create a notification channel for Android 8.0 (Oreo) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            // Customize additional channel settings if needed
            // For example, you can set the notification sound, vibration pattern, etc.

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            String currentApp = ForegroundAppSearcher.getCurrentForegroundApp(esto);
            System.out.println(currentApp);
            if(currentApp.equals("com.spotify.music")){
                String phpUrl = "http://dashboarduib.mooo.com/myownlifedashboard/dashboard/view/academic/androidAppAPI/shouldBlock.php";
                PhpRequestHandler.getResponse(phpUrl, esto);
                if(shouldBlock){
                    Intent lockScreenActivityIntent = new Intent(esto, LockScreenActivity.class);
                    lockScreenActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    esto.startActivity(lockScreenActivityIntent);
                }
            }
            handler.postDelayed(this, 1000);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onResult(boolean isYes) {
        // Handle the boolean result as needed
        if (isYes) {
            shouldBlock = true;
        } else {
            shouldBlock = false;
        }
    }
}
