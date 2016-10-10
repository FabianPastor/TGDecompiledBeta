package net.hockeyapp.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import java.io.File;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import net.hockeyapp.android.utils.HockeyLog;

public class Constants
{
  public static String ANDROID_BUILD;
  public static String ANDROID_VERSION;
  public static String APP_PACKAGE;
  public static String APP_VERSION;
  public static String APP_VERSION_NAME;
  public static final String BASE_URL = "https://sdk.hockeyapp.net/";
  private static final String BUNDLE_BUILD_NUMBER = "buildNumber";
  public static String CRASH_IDENTIFIER = null;
  public static String DEVICE_IDENTIFIER = null;
  public static final String FILES_DIRECTORY_NAME = "HockeyApp";
  public static String FILES_PATH = null;
  public static String PHONE_MANUFACTURER;
  public static String PHONE_MODEL;
  public static final String SDK_NAME = "HockeySDK";
  public static final int UPDATE_PERMISSIONS_REQUEST = 1;
  
  static
  {
    APP_VERSION = null;
    APP_VERSION_NAME = null;
    APP_PACKAGE = null;
    ANDROID_VERSION = null;
    ANDROID_BUILD = null;
    PHONE_MODEL = null;
    PHONE_MANUFACTURER = null;
  }
  
  private static String bytesToHex(byte[] paramArrayOfByte)
  {
    char[] arrayOfChar1 = "0123456789ABCDEF".toCharArray();
    char[] arrayOfChar2 = new char[paramArrayOfByte.length * 2];
    int i = 0;
    while (i < paramArrayOfByte.length)
    {
      int j = paramArrayOfByte[i] & 0xFF;
      arrayOfChar2[(i * 2)] = arrayOfChar1[(j >>> 4)];
      arrayOfChar2[(i * 2 + 1)] = arrayOfChar1[(j & 0xF)];
      i += 1;
    }
    return new String(arrayOfChar2).replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");
  }
  
  @SuppressLint({"InlinedApi"})
  private static String createSalt(Context paramContext)
  {
    if (Build.VERSION.SDK_INT >= 21) {}
    for (paramContext = Build.SUPPORTED_ABIS[0];; paramContext = Build.CPU_ABI)
    {
      String str2 = "HA" + Build.BOARD.length() % 10 + Build.BRAND.length() % 10 + paramContext.length() % 10 + Build.PRODUCT.length() % 10;
      paramContext = "";
      try
      {
        String str1 = Build.class.getField("SERIAL").get(null).toString();
        paramContext = str1;
      }
      catch (Throwable localThrowable)
      {
        for (;;) {}
      }
      return str2 + ":" + paramContext;
    }
  }
  
  public static File getHockeyAppStorageDir()
  {
    File localFile = Environment.getExternalStorageDirectory();
    localFile = new File(localFile.getAbsolutePath() + "/" + "HockeyApp");
    if ((localFile.exists()) || (localFile.mkdirs())) {}
    for (int i = 1;; i = 0)
    {
      if (i == 0) {
        HockeyLog.warn("Couldn't create HockeyApp Storage dir");
      }
      return localFile;
    }
  }
  
  private static int loadBuildNumber(Context paramContext, PackageManager paramPackageManager)
  {
    int i = 0;
    try
    {
      paramContext = paramPackageManager.getApplicationInfo(paramContext.getPackageName(), 128).metaData;
      if (paramContext != null) {
        i = paramContext.getInt("buildNumber", 0);
      }
      return i;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      HockeyLog.error("Exception thrown when accessing the application info:");
      paramContext.printStackTrace();
    }
    return 0;
  }
  
  private static void loadCrashIdentifier(Context paramContext)
  {
    Object localObject = Settings.Secure.getString(paramContext.getContentResolver(), "android_id");
    if ((!TextUtils.isEmpty(APP_PACKAGE)) && (!TextUtils.isEmpty((CharSequence)localObject))) {
      localObject = APP_PACKAGE + ":" + (String)localObject + ":" + createSalt(paramContext);
    }
    try
    {
      paramContext = MessageDigest.getInstance("SHA-1");
      localObject = ((String)localObject).getBytes("UTF-8");
      paramContext.update((byte[])localObject, 0, localObject.length);
      CRASH_IDENTIFIER = bytesToHex(paramContext.digest());
      return;
    }
    catch (Throwable paramContext)
    {
      HockeyLog.error("Couldn't create CrashIdentifier with Exception:" + paramContext.toString());
    }
  }
  
  private static void loadDeviceIdentifier(Context paramContext)
  {
    String str = Settings.Secure.getString(paramContext.getContentResolver(), "android_id");
    if (str != null)
    {
      paramContext = tryHashStringSha256(paramContext, str);
      if (paramContext == null) {
        break label29;
      }
    }
    for (;;)
    {
      DEVICE_IDENTIFIER = paramContext;
      return;
      label29:
      paramContext = UUID.randomUUID().toString();
    }
  }
  
  private static void loadFilesPath(Context paramContext)
  {
    if (paramContext != null) {}
    try
    {
      paramContext = paramContext.getFilesDir();
      if (paramContext != null) {
        FILES_PATH = paramContext.getAbsolutePath();
      }
      return;
    }
    catch (Exception paramContext)
    {
      HockeyLog.error("Exception thrown when accessing the files dir:");
      paramContext.printStackTrace();
    }
  }
  
  public static void loadFromContext(Context paramContext)
  {
    ANDROID_VERSION = Build.VERSION.RELEASE;
    ANDROID_BUILD = Build.DISPLAY;
    PHONE_MODEL = Build.MODEL;
    PHONE_MANUFACTURER = Build.MANUFACTURER;
    loadFilesPath(paramContext);
    loadPackageData(paramContext);
    loadCrashIdentifier(paramContext);
    loadDeviceIdentifier(paramContext);
  }
  
  private static void loadPackageData(Context paramContext)
  {
    if (paramContext != null) {}
    try
    {
      PackageManager localPackageManager = paramContext.getPackageManager();
      PackageInfo localPackageInfo = localPackageManager.getPackageInfo(paramContext.getPackageName(), 0);
      APP_PACKAGE = localPackageInfo.packageName;
      APP_VERSION = "" + localPackageInfo.versionCode;
      APP_VERSION_NAME = localPackageInfo.versionName;
      int i = loadBuildNumber(paramContext, localPackageManager);
      if ((i != 0) && (i > localPackageInfo.versionCode)) {
        APP_VERSION = "" + i;
      }
      return;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      HockeyLog.error("Exception thrown when accessing the package info:");
      paramContext.printStackTrace();
    }
  }
  
  private static String tryHashStringSha256(Context paramContext, String paramString)
  {
    paramContext = createSalt(paramContext);
    try
    {
      MessageDigest localMessageDigest = MessageDigest.getInstance("SHA-256");
      localMessageDigest.reset();
      localMessageDigest.update(paramString.getBytes());
      localMessageDigest.update(paramContext.getBytes());
      paramContext = bytesToHex(localMessageDigest.digest());
      return paramContext;
    }
    catch (NoSuchAlgorithmException paramContext) {}
    return null;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/Constants.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */