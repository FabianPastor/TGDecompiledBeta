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
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.hockeyapp.android.R.string;

public class Util
{
  private static final ThreadLocal<DateFormat> DATE_FORMAT_THREAD_LOCAL = new ThreadLocal()
  {
    protected DateFormat initialValue()
    {
      SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
      localSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
      return localSimpleDateFormat;
    }
  };
  private static final Pattern appIdentifierPattern = Pattern.compile("[0-9a-f]+", 2);
  
  public static void announceForAccessibility(View paramView, CharSequence paramCharSequence)
  {
    AccessibilityManager localAccessibilityManager = (AccessibilityManager)paramView.getContext().getSystemService("accessibility");
    if (!localAccessibilityManager.isEnabled()) {
      return;
    }
    if (Build.VERSION.SDK_INT < 16) {}
    for (int i = 8;; i = 16384)
    {
      AccessibilityEvent localAccessibilityEvent = AccessibilityEvent.obtain(i);
      localAccessibilityEvent.getText().add(paramCharSequence);
      localAccessibilityEvent.setSource(paramView);
      localAccessibilityEvent.setEnabled(paramView.isEnabled());
      localAccessibilityEvent.setClassName(paramView.getClass().getName());
      localAccessibilityEvent.setPackageName(paramView.getContext().getPackageName());
      localAccessibilityManager.sendAccessibilityEvent(localAccessibilityEvent);
      break;
    }
  }
  
  public static String bytesToHex(byte[] paramArrayOfByte)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = paramArrayOfByte.length;
    for (int j = 0; j < i; j++)
    {
      for (String str = Integer.toHexString(paramArrayOfByte[j] & 0xFF); str.length() < 2; str = "0" + str) {}
      localStringBuilder.append(str);
    }
    return localStringBuilder.toString();
  }
  
  public static void cancelNotification(Context paramContext, int paramInt)
  {
    ((NotificationManager)paramContext.getSystemService("notification")).cancel(paramInt);
  }
  
  /* Error */
  public static String convertStreamToString(java.io.InputStream paramInputStream)
  {
    // Byte code:
    //   0: new 148	java/io/BufferedReader
    //   3: dup
    //   4: new 150	java/io/InputStreamReader
    //   7: dup
    //   8: aload_0
    //   9: invokespecial 153	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;)V
    //   12: sipush 1024
    //   15: invokespecial 156	java/io/BufferedReader:<init>	(Ljava/io/Reader;I)V
    //   18: astore_1
    //   19: new 110	java/lang/StringBuilder
    //   22: dup
    //   23: invokespecial 111	java/lang/StringBuilder:<init>	()V
    //   26: astore_2
    //   27: aload_1
    //   28: invokevirtual 159	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   31: astore_3
    //   32: aload_3
    //   33: ifnull +33 -> 66
    //   36: aload_2
    //   37: aload_3
    //   38: invokevirtual 129	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   41: bipush 10
    //   43: invokevirtual 162	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
    //   46: pop
    //   47: goto -20 -> 27
    //   50: astore_1
    //   51: ldc -92
    //   53: aload_1
    //   54: invokestatic 170	net/hockeyapp/android/utils/HockeyLog:error	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   57: aload_0
    //   58: invokevirtual 175	java/io/InputStream:close	()V
    //   61: aload_2
    //   62: invokevirtual 132	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   65: areturn
    //   66: aload_0
    //   67: invokevirtual 175	java/io/InputStream:close	()V
    //   70: goto -9 -> 61
    //   73: astore_0
    //   74: goto -13 -> 61
    //   77: astore_2
    //   78: aload_0
    //   79: invokevirtual 175	java/io/InputStream:close	()V
    //   82: aload_2
    //   83: athrow
    //   84: astore_0
    //   85: goto -24 -> 61
    //   88: astore_0
    //   89: goto -7 -> 82
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	92	0	paramInputStream	java.io.InputStream
    //   18	10	1	localBufferedReader	java.io.BufferedReader
    //   50	4	1	localIOException	java.io.IOException
    //   26	36	2	localStringBuilder	StringBuilder
    //   77	6	2	localObject	Object
    //   31	7	3	str	String
    // Exception table:
    //   from	to	target	type
    //   27	32	50	java/io/IOException
    //   36	47	50	java/io/IOException
    //   66	70	73	java/io/IOException
    //   27	32	77	finally
    //   36	47	77	finally
    //   51	57	77	finally
    //   57	61	84	java/io/IOException
    //   78	82	88	java/io/IOException
  }
  
  public static Notification createNotification(Context paramContext, PendingIntent paramPendingIntent, String paramString1, String paramString2, int paramInt, String paramString3)
  {
    if (Build.VERSION.SDK_INT >= 26)
    {
      paramContext = new Notification.Builder(paramContext, paramString3);
      paramContext.setContentTitle(paramString1).setContentText(paramString2).setContentIntent(paramPendingIntent).setSmallIcon(paramInt);
      if (Build.VERSION.SDK_INT < 16) {
        break label65;
      }
    }
    label65:
    for (paramContext = paramContext.build();; paramContext = paramContext.getNotification())
    {
      return paramContext;
      paramContext = new Notification.Builder(paramContext);
      break;
    }
  }
  
  public static String encodeParam(String paramString)
  {
    try
    {
      String str = URLEncoder.encode(paramString, "UTF-8");
      paramString = str;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      for (;;)
      {
        HockeyLog.error("Failed to encode param " + paramString, localUnsupportedEncodingException);
        paramString = "";
      }
    }
    return paramString;
  }
  
  public static String getAppName(Context paramContext)
  {
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
    if (localObject != null) {}
    for (paramContext = (String)localPackageManager.getApplicationLabel((ApplicationInfo)localObject);; paramContext = paramContext.getString(R.string.hockeyapp_crash_dialog_app_name_fallback)) {
      return paramContext;
    }
  }
  
  public static byte[] hash(byte[] paramArrayOfByte, String paramString)
    throws NoSuchAlgorithmException
  {
    paramString = MessageDigest.getInstance(paramString);
    paramString.update(paramArrayOfByte);
    return paramString.digest();
  }
  
  public static boolean isConnectedToNetwork(Context paramContext)
  {
    bool1 = false;
    try
    {
      paramContext = (ConnectivityManager)paramContext.getApplicationContext().getSystemService("connectivity");
      bool2 = bool1;
      if (paramContext != null)
      {
        paramContext = paramContext.getActiveNetworkInfo();
        bool2 = bool1;
        if (paramContext != null)
        {
          boolean bool3 = paramContext.isConnected();
          bool2 = bool1;
          if (bool3) {
            bool2 = true;
          }
        }
      }
    }
    catch (Exception paramContext)
    {
      for (;;)
      {
        HockeyLog.error("Exception thrown when check network is connected", paramContext);
        boolean bool2 = bool1;
      }
    }
    return bool2;
  }
  
  public static boolean isValidEmail(String paramString)
  {
    if ((!TextUtils.isEmpty(paramString)) && (Patterns.EMAIL_ADDRESS.matcher(paramString).matches())) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static Boolean runsOnTablet(Context paramContext)
  {
    boolean bool = false;
    if (paramContext != null)
    {
      paramContext = paramContext.getResources().getConfiguration();
      if (((paramContext.screenLayout & 0xF) == 3) || ((paramContext.screenLayout & 0xF) == 4)) {
        bool = true;
      }
    }
    for (paramContext = Boolean.valueOf(bool);; paramContext = Boolean.valueOf(false)) {
      return paramContext;
    }
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
  
  public static void sendNotification(Context paramContext, int paramInt, Notification paramNotification, String paramString, CharSequence paramCharSequence)
  {
    paramContext = (NotificationManager)paramContext.getSystemService("notification");
    if (Build.VERSION.SDK_INT >= 26) {
      paramContext.createNotificationChannel(new NotificationChannel(paramString, paramCharSequence, 3));
    }
    paramContext.notify(paramInt, paramNotification);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/utils/Util.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */