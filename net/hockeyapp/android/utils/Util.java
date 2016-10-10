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
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.hockeyapp.android.R.string;

public class Util
{
  public static final String APP_IDENTIFIER_KEY = "net.hockeyapp.android.appIdentifier";
  public static final int APP_IDENTIFIER_LENGTH = 32;
  public static final String APP_IDENTIFIER_PATTERN = "[0-9a-f]+";
  private static final String APP_SECRET_KEY = "net.hockeyapp.android.appSecret";
  private static final ThreadLocal<DateFormat> DATE_FORMAT_THREAD_LOCAL = new ThreadLocal()
  {
    protected DateFormat initialValue()
    {
      SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
      localSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
      return localSimpleDateFormat;
    }
  };
  private static final char[] HEX_ARRAY;
  public static final String LOG_IDENTIFIER = "HockeyApp";
  public static final String PREFS_FEEDBACK_TOKEN = "net.hockeyapp.android.prefs_feedback_token";
  public static final String PREFS_KEY_FEEDBACK_TOKEN = "net.hockeyapp.android.prefs_key_feedback_token";
  public static final String PREFS_KEY_NAME_EMAIL_SUBJECT = "net.hockeyapp.android.prefs_key_name_email";
  public static final String PREFS_NAME_EMAIL_SUBJECT = "net.hockeyapp.android.prefs_name_email";
  private static final String SDK_VERSION_KEY = "net.hockeyapp.android.sdkVersion";
  private static final Pattern appIdentifierPattern = Pattern.compile("[0-9a-f]+", 2);
  
  static
  {
    HEX_ARRAY = "0123456789ABCDEF".toCharArray();
  }
  
  private static Notification buildNotificationPreHoneycomb(Context paramContext, PendingIntent paramPendingIntent, String paramString1, String paramString2, int paramInt)
  {
    Notification localNotification = new Notification(paramInt, "", System.currentTimeMillis());
    try
    {
      localNotification.getClass().getMethod("setLatestEventInfo", new Class[] { Context.class, CharSequence.class, CharSequence.class, PendingIntent.class }).invoke(localNotification, new Object[] { paramContext, paramString1, paramString2, paramPendingIntent });
      return localNotification;
    }
    catch (Exception paramContext) {}
    return localNotification;
  }
  
  @TargetApi(11)
  private static Notification buildNotificationWithBuilder(Context paramContext, PendingIntent paramPendingIntent, String paramString1, String paramString2, int paramInt)
  {
    paramContext = new Notification.Builder(paramContext).setContentTitle(paramString1).setContentText(paramString2).setContentIntent(paramPendingIntent).setSmallIcon(paramInt);
    if (Build.VERSION.SDK_INT < 16) {
      return paramContext.getNotification();
    }
    return paramContext.build();
  }
  
  public static boolean classExists(String paramString)
  {
    boolean bool = false;
    try
    {
      paramString = Class.forName(paramString);
      if (paramString != null) {
        bool = true;
      }
      return bool;
    }
    catch (ClassNotFoundException paramString) {}
    return false;
  }
  
  public static String convertAppIdentifierToGuid(String paramString)
    throws IllegalArgumentException
  {
    Object localObject = null;
    try
    {
      String str = sanitizeAppIdentifier(paramString);
      paramString = (String)localObject;
      if (str != null)
      {
        paramString = new StringBuffer(str);
        paramString.insert(20, '-');
        paramString.insert(16, '-');
        paramString.insert(12, '-');
        paramString.insert(8, '-');
        paramString = paramString.toString();
      }
      return paramString;
    }
    catch (IllegalArgumentException paramString)
    {
      throw paramString;
    }
  }
  
  public static Notification createNotification(Context paramContext, PendingIntent paramPendingIntent, String paramString1, String paramString2, int paramInt)
  {
    if (isNotificationBuilderSupported()) {
      return buildNotificationWithBuilder(paramContext, paramPendingIntent, paramString1, paramString2, paramInt);
    }
    return buildNotificationPreHoneycomb(paramContext, paramPendingIntent, paramString1, paramString2, paramInt);
  }
  
  public static String dateToISO8601(Date paramDate)
  {
    Date localDate = paramDate;
    if (paramDate == null) {
      localDate = new Date();
    }
    return ((DateFormat)DATE_FORMAT_THREAD_LOCAL.get()).format(localDate);
  }
  
  public static String encodeParam(String paramString)
  {
    try
    {
      paramString = URLEncoder.encode(paramString, "UTF-8");
      return paramString;
    }
    catch (UnsupportedEncodingException paramString)
    {
      paramString.printStackTrace();
    }
    return "";
  }
  
  @SuppressLint({"NewApi"})
  public static Boolean fragmentsSupported()
  {
    try
    {
      if ((Build.VERSION.SDK_INT >= 11) && (classExists("android.app.Fragment"))) {}
      for (boolean bool = true;; bool = false) {
        return Boolean.valueOf(bool);
      }
      return Boolean.valueOf(false);
    }
    catch (NoClassDefFoundError localNoClassDefFoundError) {}
  }
  
  public static String getAppIdentifier(Context paramContext)
  {
    return getManifestString(paramContext, "net.hockeyapp.android.appIdentifier");
  }
  
  public static String getAppName(Context paramContext)
  {
    if (paramContext == null) {
      return "";
    }
    PackageManager localPackageManager = paramContext.getPackageManager();
    Object localObject = null;
    try
    {
      ApplicationInfo localApplicationInfo = localPackageManager.getApplicationInfo(paramContext.getApplicationInfo().packageName, 0);
      localObject = localApplicationInfo;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      for (;;) {}
    }
    if (localObject != null) {
      return (String)localPackageManager.getApplicationLabel((ApplicationInfo)localObject);
    }
    return paramContext.getString(R.string.hockeyapp_crash_dialog_app_name_fallback);
  }
  
  public static String getAppSecret(Context paramContext)
  {
    return getManifestString(paramContext, "net.hockeyapp.android.appSecret");
  }
  
  private static Bundle getBundle(Context paramContext)
  {
    try
    {
      paramContext = paramContext.getPackageManager().getApplicationInfo(paramContext.getPackageName(), 128).metaData;
      return paramContext;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      throw new RuntimeException(paramContext);
    }
  }
  
  public static String getFormString(Map<String, String> paramMap)
    throws UnsupportedEncodingException
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = paramMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str2 = (String)localIterator.next();
      String str1 = (String)paramMap.get(str2);
      str2 = URLEncoder.encode(str2, "UTF-8");
      str1 = URLEncoder.encode(str1, "UTF-8");
      localArrayList.add(str2 + "=" + str1);
    }
    return TextUtils.join("&", localArrayList);
  }
  
  public static String getManifestString(Context paramContext, String paramString)
  {
    return getBundle(paramContext).getString(paramString);
  }
  
  public static String getSdkVersionFromManifest(Context paramContext)
  {
    return getManifestString(paramContext, "net.hockeyapp.android.sdkVersion");
  }
  
  public static boolean isConnectedToNetwork(Context paramContext)
  {
    boolean bool2 = false;
    paramContext = (ConnectivityManager)paramContext.getSystemService("connectivity");
    boolean bool1 = bool2;
    if (paramContext != null)
    {
      paramContext = paramContext.getActiveNetworkInfo();
      bool1 = bool2;
      if (paramContext != null)
      {
        bool1 = bool2;
        if (paramContext.isConnected()) {
          bool1 = true;
        }
      }
    }
    return bool1;
  }
  
  public static boolean isEmulator()
  {
    return Build.BRAND.equalsIgnoreCase("generic");
  }
  
  public static boolean isNotificationBuilderSupported()
  {
    return (Build.VERSION.SDK_INT >= 11) && (classExists("android.app.Notification.Builder"));
  }
  
  public static final boolean isValidEmail(String paramString)
  {
    return (!TextUtils.isEmpty(paramString)) && (Patterns.EMAIL_ADDRESS.matcher(paramString).matches());
  }
  
  public static Boolean runsOnTablet(WeakReference<Activity> paramWeakReference)
  {
    boolean bool = false;
    if (paramWeakReference != null)
    {
      paramWeakReference = (Activity)paramWeakReference.get();
      if (paramWeakReference != null)
      {
        paramWeakReference = paramWeakReference.getResources().getConfiguration();
        if (((paramWeakReference.screenLayout & 0xF) == 3) || ((paramWeakReference.screenLayout & 0xF) == 4)) {
          bool = true;
        }
        return Boolean.valueOf(bool);
      }
    }
    return Boolean.valueOf(false);
  }
  
  public static String sanitizeAppIdentifier(String paramString)
    throws IllegalArgumentException
  {
    if (paramString == null) {
      throw new IllegalArgumentException("App ID must not be null.");
    }
    paramString = paramString.trim();
    Matcher localMatcher = appIdentifierPattern.matcher(paramString);
    if (paramString.length() != 32) {
      throw new IllegalArgumentException("App ID length must be 32 characters.");
    }
    if (!localMatcher.matches()) {
      throw new IllegalArgumentException("App ID must match regex pattern /[0-9a-f]+/i");
    }
    return paramString;
  }
  
  public static boolean sessionTrackingSupported()
  {
    return Build.VERSION.SDK_INT >= 14;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/utils/Util.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */