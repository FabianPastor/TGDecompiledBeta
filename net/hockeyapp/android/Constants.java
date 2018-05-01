package net.hockeyapp.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
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

public class Constants
{
  public static String ANDROID_BUILD;
  public static String ANDROID_VERSION;
  public static String APP_PACKAGE;
  public static String APP_VERSION = null;
  public static String APP_VERSION_NAME = null;
  static String DEVICE_IDENTIFIER = null;
  static CountDownLatch LOADING_LATCH = new CountDownLatch(1);
  public static String PHONE_MANUFACTURER;
  public static String PHONE_MODEL;
  
  static
  {
    APP_PACKAGE = null;
    ANDROID_VERSION = null;
    ANDROID_BUILD = null;
    PHONE_MODEL = null;
    PHONE_MANUFACTURER = null;
  }
  
  public static Future<String> getDeviceIdentifier()
  {
    if (LOADING_LATCH.getCount() == 0L) {}
    for (Object localObject = new CompletedFuture(DEVICE_IDENTIFIER);; localObject = AsyncTaskUtils.execute(new Callable()
        {
          public String call()
            throws Exception
          {
            Constants.LOADING_LATCH.await();
            return Constants.DEVICE_IDENTIFIER;
          }
        })) {
      return (Future<String>)localObject;
    }
  }
  
  public static File getHockeyAppStorageDir(Context paramContext)
  {
    paramContext = new File(paramContext.getExternalFilesDir(null), "HockeyApp");
    if ((paramContext.exists()) || (paramContext.mkdirs())) {}
    for (int i = 1;; i = 0)
    {
      if (i == 0) {
        HockeyLog.warn("Couldn't create HockeyApp Storage dir");
      }
      return paramContext;
    }
  }
  
  private static int loadBuildNumber(Context paramContext, PackageManager paramPackageManager)
  {
    i = 0;
    try
    {
      paramContext = paramPackageManager.getApplicationInfo(paramContext.getPackageName(), 128).metaData;
      j = i;
      if (paramContext != null) {
        j = paramContext.getInt("buildNumber", 0);
      }
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      for (;;)
      {
        HockeyLog.error("Exception thrown when accessing the application info", paramContext);
        int j = i;
      }
    }
    return j;
  }
  
  public static void loadFromContext(Context paramContext)
  {
    ANDROID_VERSION = Build.VERSION.RELEASE;
    ANDROID_BUILD = Build.DISPLAY;
    PHONE_MODEL = Build.MODEL;
    PHONE_MANUFACTURER = Build.MANUFACTURER;
    loadPackageData(paramContext);
    loadIdentifiers(paramContext);
  }
  
  @SuppressLint({"StaticFieldLeak"})
  private static void loadIdentifiers(Context paramContext)
  {
    if (DEVICE_IDENTIFIER != null) {}
    for (;;)
    {
      return;
      AsyncTaskUtils.execute(new AsyncTask()
      {
        protected String doInBackground(Void... paramAnonymousVarArgs)
        {
          SharedPreferences localSharedPreferences = this.val$context.getSharedPreferences("HockeyApp", 0);
          String str = localSharedPreferences.getString("deviceIdentifier", null);
          paramAnonymousVarArgs = str;
          if (str == null)
          {
            paramAnonymousVarArgs = UUID.randomUUID().toString();
            localSharedPreferences.edit().putString("deviceIdentifier", paramAnonymousVarArgs).apply();
          }
          return paramAnonymousVarArgs;
        }
        
        protected void onPostExecute(String paramAnonymousString)
        {
          Constants.DEVICE_IDENTIFIER = paramAnonymousString;
          Constants.LOADING_LATCH.countDown();
        }
      });
    }
  }
  
  private static void loadPackageData(Context paramContext)
  {
    if (paramContext != null) {}
    try
    {
      PackageManager localPackageManager = paramContext.getPackageManager();
      PackageInfo localPackageInfo = localPackageManager.getPackageInfo(paramContext.getPackageName(), 0);
      APP_PACKAGE = localPackageInfo.packageName;
      StringBuilder localStringBuilder = new java/lang/StringBuilder;
      localStringBuilder.<init>();
      APP_VERSION = "" + localPackageInfo.versionCode;
      APP_VERSION_NAME = localPackageInfo.versionName;
      int i = loadBuildNumber(paramContext, localPackageManager);
      if ((i != 0) && (i > localPackageInfo.versionCode))
      {
        paramContext = new java/lang/StringBuilder;
        paramContext.<init>();
        APP_VERSION = "" + i;
      }
      return;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      for (;;)
      {
        HockeyLog.error("Exception thrown when accessing the package info", paramContext);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/Constants.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */