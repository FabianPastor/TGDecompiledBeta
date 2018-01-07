package net.hockeyapp.android.utils;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.hockeyapp.android.R;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.exoplayer2.C;

public class Util {
    private static final ThreadLocal<DateFormat> DATE_FORMAT_THREAD_LOCAL = new ThreadLocal<DateFormat>() {
        protected DateFormat initialValue() {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            return dateFormat;
        }
    };
    private static final Pattern appIdentifierPattern = Pattern.compile("[0-9a-f]+", 2);

    public static String encodeParam(String param) {
        try {
            return URLEncoder.encode(param, C.UTF8_NAME);
        } catch (Throwable e) {
            HockeyLog.error("Failed to encode param " + param, e);
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
    }

    public static boolean isValidEmail(String value) {
        return !TextUtils.isEmpty(value) && Patterns.EMAIL_ADDRESS.matcher(value).matches();
    }

    public static Boolean runsOnTablet(Context context) {
        boolean z = false;
        if (context == null) {
            return Boolean.valueOf(false);
        }
        Configuration configuration = context.getResources().getConfiguration();
        if ((configuration.screenLayout & 15) == 3 || (configuration.screenLayout & 15) == 4) {
            z = true;
        }
        return Boolean.valueOf(z);
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

    public static Notification createNotification(Context context, PendingIntent pendingIntent, String title, String text, int iconId, String channelId) {
        Builder builder;
        if (VERSION.SDK_INT >= 26) {
            builder = new Builder(context, channelId);
        } else {
            builder = new Builder(context);
        }
        builder.setContentTitle(title).setContentText(text).setContentIntent(pendingIntent).setSmallIcon(iconId);
        if (VERSION.SDK_INT >= 16) {
            return builder.build();
        }
        return builder.getNotification();
    }

    public static void sendNotification(Context context, int id, Notification notification, String channelId, CharSequence channelName) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
        if (VERSION.SDK_INT >= 26) {
            notificationManager.createNotificationChannel(new NotificationChannel(channelId, channelName, 3));
        }
        notificationManager.notify(id, notification);
    }

    public static void cancelNotification(Context context, int id) {
        ((NotificationManager) context.getSystemService("notification")).cancel(id);
    }

    public static void announceForAccessibility(View view, CharSequence text) {
        AccessibilityManager manager = (AccessibilityManager) view.getContext().getSystemService("accessibility");
        if (manager.isEnabled()) {
            int eventType;
            if (VERSION.SDK_INT < 16) {
                eventType = 8;
            } else {
                eventType = MessagesController.UPDATE_MASK_CHAT_ADMINS;
            }
            AccessibilityEvent event = AccessibilityEvent.obtain(eventType);
            event.getText().add(text);
            event.setSource(view);
            event.setEnabled(view.isEnabled());
            event.setClassName(view.getClass().getName());
            event.setPackageName(view.getContext().getPackageName());
            manager.sendAccessibilityEvent(event);
        }
    }

    public static boolean isConnectedToNetwork(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().getSystemService("connectivity");
            if (connectivityManager == null) {
                return false;
            }
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            if (activeNetwork == null || !activeNetwork.isConnected()) {
                return false;
            }
            return true;
        } catch (Throwable e) {
            HockeyLog.error("Exception thrown when check network is connected", e);
            return false;
        }
    }

    public static String getAppName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(context.getApplicationInfo().packageName, 0);
        } catch (NameNotFoundException e) {
        }
        if (applicationInfo != null) {
            return (String) packageManager.getApplicationLabel(applicationInfo);
        }
        return context.getString(R.string.hockeyapp_crash_dialog_app_name_fallback);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static String convertStreamToString(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream), 1024);
        StringBuilder stringBuilder = new StringBuilder();
        while (true) {
            try {
                String line = reader.readLine();
                if (line != null) {
                    stringBuilder.append(line).append('\n');
                } else {
                    try {
                        break;
                    } catch (IOException e) {
                    }
                }
            } catch (Throwable e2) {
                HockeyLog.error("Failed to convert stream to string", e2);
            } catch (Throwable th) {
                try {
                    inputStream.close();
                } catch (IOException e3) {
                }
            }
        }
        inputStream.close();
        return stringBuilder.toString();
    }

    public static byte[] hash(byte[] bytes, String algorithm) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        digest.update(bytes);
        return digest.digest();
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte aMessageDigest : bytes) {
            String h = Integer.toHexString(aMessageDigest & 255);
            while (h.length() < 2) {
                h = "0" + h;
            }
            hexString.append(h);
        }
        return hexString.toString();
    }
}
