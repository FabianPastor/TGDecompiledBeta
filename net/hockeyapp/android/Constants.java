package net.hockeyapp.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import java.io.File;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import net.hockeyapp.android.utils.AsyncTaskUtils;
import net.hockeyapp.android.utils.CompletedFuture;
import net.hockeyapp.android.utils.HockeyLog;

public class Constants {
    public static String ANDROID_BUILD = null;
    public static String ANDROID_VERSION = null;
    public static String APP_PACKAGE = null;
    public static String APP_VERSION = null;
    public static String APP_VERSION_NAME = null;
    static String DEVICE_IDENTIFIER = null;
    static CountDownLatch LOADING_LATCH = new CountDownLatch(1);
    public static String PHONE_MANUFACTURER = null;
    public static String PHONE_MODEL = null;

    public static Future<String> getDeviceIdentifier() {
        if (LOADING_LATCH.getCount() == 0) {
            return new CompletedFuture(DEVICE_IDENTIFIER);
        }
        return AsyncTaskUtils.execute(new Callable<String>() {
            public String call() throws Exception {
                Constants.LOADING_LATCH.await();
                return Constants.DEVICE_IDENTIFIER;
            }
        });
    }

    public static void loadFromContext(Context context) {
        ANDROID_VERSION = VERSION.RELEASE;
        ANDROID_BUILD = Build.DISPLAY;
        PHONE_MODEL = Build.MODEL;
        PHONE_MANUFACTURER = Build.MANUFACTURER;
        loadPackageData(context);
        loadIdentifiers(context);
    }

    public static File getHockeyAppStorageDir(Context context) {
        File dir = new File(context.getExternalFilesDir(null), "HockeyApp");
        boolean success = dir.exists() || dir.mkdirs();
        if (!success) {
            HockeyLog.warn("Couldn't create HockeyApp Storage dir");
        }
        return dir;
    }

    private static void loadPackageData(Context context) {
        if (context != null) {
            try {
                PackageManager packageManager = context.getPackageManager();
                PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
                APP_PACKAGE = packageInfo.packageName;
                APP_VERSION = TtmlNode.ANONYMOUS_REGION_ID + packageInfo.versionCode;
                APP_VERSION_NAME = packageInfo.versionName;
                int buildNumber = loadBuildNumber(context, packageManager);
                if (buildNumber != 0 && buildNumber > packageInfo.versionCode) {
                    APP_VERSION = TtmlNode.ANONYMOUS_REGION_ID + buildNumber;
                }
            } catch (Throwable e) {
                HockeyLog.error("Exception thrown when accessing the package info", e);
            }
        }
    }

    private static int loadBuildNumber(Context context, PackageManager packageManager) {
        int i = 0;
        try {
            Bundle metaData = packageManager.getApplicationInfo(context.getPackageName(), 128).metaData;
            if (metaData != null) {
                i = metaData.getInt("buildNumber", 0);
            }
        } catch (Throwable e) {
            HockeyLog.error("Exception thrown when accessing the application info", e);
        }
        return i;
    }

    @SuppressLint({"StaticFieldLeak"})
    private static void loadIdentifiers(final Context context) {
        if (DEVICE_IDENTIFIER == null) {
            AsyncTaskUtils.execute(new AsyncTask<Void, Object, String>() {
                protected String doInBackground(Void... voids) {
                    SharedPreferences preferences = context.getSharedPreferences("HockeyApp", 0);
                    String deviceIdentifier = preferences.getString("deviceIdentifier", null);
                    if (deviceIdentifier != null) {
                        return deviceIdentifier;
                    }
                    deviceIdentifier = UUID.randomUUID().toString();
                    preferences.edit().putString("deviceIdentifier", deviceIdentifier).apply();
                    return deviceIdentifier;
                }

                protected void onPostExecute(String deviceIdentifier) {
                    Constants.DEVICE_IDENTIFIER = deviceIdentifier;
                    Constants.LOADING_LATCH.countDown();
                }
            });
        }
    }
}
