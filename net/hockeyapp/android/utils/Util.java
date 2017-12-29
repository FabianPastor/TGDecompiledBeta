package net.hockeyapp.android.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Patterns;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.hockeyapp.android.R;
import org.telegram.messenger.exoplayer2.C;

public class Util {
    private static final ThreadLocal<DateFormat> DATE_FORMAT_THREAD_LOCAL = new ThreadLocal<DateFormat>() {
        protected DateFormat initialValue() {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            return dateFormat;
        }
    };
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    private static final Pattern appIdentifierPattern = Pattern.compile("[0-9a-f]+", 2);

    public static String encodeParam(String param) {
        try {
            return URLEncoder.encode(param, C.UTF8_NAME);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
    }

    public static final boolean isValidEmail(String value) {
        return !TextUtils.isEmpty(value) && Patterns.EMAIL_ADDRESS.matcher(value).matches();
    }

    @SuppressLint({"NewApi"})
    public static Boolean fragmentsSupported() {
        try {
            boolean z;
            if (VERSION.SDK_INT < 11 || !classExists("android.app.Fragment")) {
                z = false;
            } else {
                z = true;
            }
            return Boolean.valueOf(z);
        } catch (NoClassDefFoundError e) {
            return Boolean.valueOf(false);
        }
    }

    public static Boolean runsOnTablet(WeakReference<Activity> weakActivity) {
        boolean z = false;
        if (weakActivity != null) {
            Activity activity = (Activity) weakActivity.get();
            if (activity != null) {
                Configuration configuration = activity.getResources().getConfiguration();
                if ((configuration.screenLayout & 15) == 3 || (configuration.screenLayout & 15) == 4) {
                    z = true;
                }
                return Boolean.valueOf(z);
            }
        }
        return Boolean.valueOf(false);
    }

    public static String sanitizeAppIdentifier(String appIdentifier) throws IllegalArgumentException {
        if (appIdentifier == null) {
            throw new IllegalArgumentException("App ID must not be null.");
        }
        String sAppIdentifier = appIdentifier.trim();
        Matcher matcher = appIdentifierPattern.matcher(sAppIdentifier);
        if (sAppIdentifier.length() != 32) {
            throw new IllegalArgumentException("App ID length must be 32 characters.");
        } else if (matcher.matches()) {
            return sAppIdentifier;
        } else {
            throw new IllegalArgumentException("App ID must match regex pattern /[0-9a-f]+/i");
        }
    }

    public static boolean classExists(String className) {
        try {
            return Class.forName(className) != null;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean isNotificationBuilderSupported() {
        return VERSION.SDK_INT >= 11 && classExists("android.app.Notification.Builder");
    }

    public static Notification createNotification(Context context, PendingIntent pendingIntent, String title, String text, int iconId) {
        if (isNotificationBuilderSupported()) {
            return buildNotificationWithBuilder(context, pendingIntent, title, text, iconId);
        }
        return buildNotificationPreHoneycomb(context, pendingIntent, title, text, iconId);
    }

    private static Notification buildNotificationPreHoneycomb(Context context, PendingIntent pendingIntent, String title, String text, int iconId) {
        Notification notification = new Notification(iconId, TtmlNode.ANONYMOUS_REGION_ID, System.currentTimeMillis());
        try {
            notification.getClass().getMethod("setLatestEventInfo", new Class[]{Context.class, CharSequence.class, CharSequence.class, PendingIntent.class}).invoke(notification, new Object[]{context, title, text, pendingIntent});
        } catch (Exception e) {
        }
        return notification;
    }

    @TargetApi(11)
    private static Notification buildNotificationWithBuilder(Context context, PendingIntent pendingIntent, String title, String text, int iconId) {
        Builder builder = new Builder(context).setContentTitle(title).setContentText(text).setContentIntent(pendingIntent).setSmallIcon(iconId);
        if (VERSION.SDK_INT < 16) {
            return builder.getNotification();
        }
        return builder.build();
    }

    public static boolean isConnectedToNetwork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService("connectivity");
        if (connectivityManager == null) {
            return false;
        }
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork == null || !activeNetwork.isConnected()) {
            return false;
        }
        return true;
    }

    public static String getAppName(Context context) {
        if (context == null) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
        String appTitle;
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(context.getApplicationInfo().packageName, 0);
        } catch (NameNotFoundException e) {
        }
        if (applicationInfo != null) {
            appTitle = (String) packageManager.getApplicationLabel(applicationInfo);
        } else {
            appTitle = context.getString(R.string.hockeyapp_crash_dialog_app_name_fallback);
        }
        return appTitle;
    }
}
