package com.example.applock;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;

import java.util.List;

public class ForegroundAppSearcher {

    private static final int USAGE_STATS_PERMISSION_REQUEST = 1001;

    public static String getCurrentForegroundApp(Context context) {
        if (hasUsageStatsPermission(context)) {
            return retrieveForegroundApp(context);
        } else {
            return "You may want to handle this case appropriately in your app"; // You may want to handle this case appropriately in your app
        }
    }

    private static boolean hasUsageStatsPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
            return mode == AppOpsManager.MODE_ALLOWED;
        }
        return true;
    }

    @SuppressWarnings("unused")
    private static void requestUsageStatsPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }
    }

    @SuppressWarnings("unused")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static String retrieveForegroundApp(Context context) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        long currentTime = System.currentTimeMillis();
        List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, currentTime - 1000 * 3600, currentTime);

        if (stats != null) {
            long lastUsedTime = 0;
            String currentForegroundApp = "";

            for (UsageStats usageStats : stats) {
                if (usageStats.getLastTimeUsed() > lastUsedTime) {
                    lastUsedTime = usageStats.getLastTimeUsed();
                    currentForegroundApp = usageStats.getPackageName();
                }
            }

            return currentForegroundApp;
        }

        return "";
    }
}
