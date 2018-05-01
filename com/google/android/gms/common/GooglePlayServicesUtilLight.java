package com.google.android.gms.common;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageInstaller.SessionInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Log;
import com.google.android.gms.common.internal.MetadataValueReader;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.util.DeviceProperties;
import com.google.android.gms.common.util.GmsVersionParser;
import com.google.android.gms.common.util.PlatformVersion;
import com.google.android.gms.common.util.UidVerifier;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class GooglePlayServicesUtilLight
{
  @Deprecated
  public static final int GOOGLE_PLAY_SERVICES_VERSION_CODE = 12451000;
  public static boolean sIsTestMode = false;
  public static boolean sTestIsUserBuild = false;
  private static boolean zzbr = false;
  private static boolean zzbs = false;
  static final AtomicBoolean zzbt = new AtomicBoolean();
  private static final AtomicBoolean zzbu = new AtomicBoolean();
  
  @Deprecated
  public static void cancelAvailabilityErrorNotifications(Context paramContext)
  {
    if (zzbt.getAndSet(true)) {}
    for (;;)
    {
      return;
      try
      {
        paramContext = (NotificationManager)paramContext.getSystemService("notification");
        if (paramContext != null) {
          paramContext.cancel(10436);
        }
      }
      catch (SecurityException paramContext) {}
    }
  }
  
  @Deprecated
  public static int getApkVersion(Context paramContext)
  {
    int i = 0;
    try
    {
      paramContext = paramContext.getPackageManager().getPackageInfo("com.google.android.gms", 0);
      i = paramContext.versionCode;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      for (;;)
      {
        Log.w("GooglePlayServicesUtil", "Google Play services is missing.");
      }
    }
    return i;
  }
  
  @Deprecated
  public static String getErrorString(int paramInt)
  {
    return ConnectionResult.zza(paramInt);
  }
  
  public static Context getRemoteContext(Context paramContext)
  {
    try
    {
      paramContext = paramContext.createPackageContext("com.google.android.gms", 3);
      return paramContext;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      for (;;)
      {
        paramContext = null;
      }
    }
  }
  
  public static Resources getRemoteResource(Context paramContext)
  {
    try
    {
      paramContext = paramContext.getPackageManager().getResourcesForApplication("com.google.android.gms");
      return paramContext;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      for (;;)
      {
        paramContext = null;
      }
    }
  }
  
  public static boolean honorsDebugCertificates(Context paramContext)
  {
    return true;
  }
  
  @Deprecated
  public static int isGooglePlayServicesAvailable(Context paramContext)
  {
    return isGooglePlayServicesAvailable(paramContext, GOOGLE_PLAY_SERVICES_VERSION_CODE);
  }
  
  @Deprecated
  public static int isGooglePlayServicesAvailable(Context paramContext, int paramInt)
  {
    try
    {
      paramContext.getResources().getString(R.string.common_google_play_services_unknown_issue);
      if ((!"com.google.android.gms".equals(paramContext.getPackageName())) && (!zzbu.get()))
      {
        i = MetadataValueReader.getGooglePlayServicesVersion(paramContext);
        if (i == 0) {
          throw new IllegalStateException("A required meta-data tag in your app's AndroidManifest.xml does not exist.  You must have the following declaration within the <application> element:     <meta-data android:name=\"com.google.android.gms.version\" android:value=\"@integer/google_play_services_version\" />");
        }
      }
    }
    catch (Throwable localThrowable)
    {
      int i;
      for (;;)
      {
        Log.e("GooglePlayServicesUtil", "The Google Play services resources were not found. Check your project configuration to ensure that the resources are included.");
      }
      if (i != GOOGLE_PLAY_SERVICES_VERSION_CODE)
      {
        paramInt = GOOGLE_PLAY_SERVICES_VERSION_CODE;
        throw new IllegalStateException(320 + "The meta-data tag in your app's AndroidManifest.xml does not have the right value.  Expected " + paramInt + " but found " + i + ".  You must have the following declaration within the <application> element:     <meta-data android:name=\"com.google.android.gms.version\" android:value=\"@integer/google_play_services_version\" />");
      }
      if (DeviceProperties.isWearableWithoutPlayStore(paramContext)) {
        break label143;
      }
    }
    if (!DeviceProperties.isIoT(paramContext)) {}
    label143:
    for (boolean bool = true;; bool = false) {
      return zza(paramContext, bool, paramInt);
    }
  }
  
  @Deprecated
  public static boolean isGooglePlayServicesUid(Context paramContext, int paramInt)
  {
    return UidVerifier.isGooglePlayServicesUid(paramContext, paramInt);
  }
  
  @Deprecated
  public static boolean isPlayServicesPossiblyUpdating(Context paramContext, int paramInt)
  {
    boolean bool = true;
    if (paramInt == 18) {}
    for (;;)
    {
      return bool;
      if (paramInt == 1) {
        bool = isUninstalledAppPossiblyUpdating(paramContext, "com.google.android.gms");
      } else {
        bool = false;
      }
    }
  }
  
  @TargetApi(18)
  public static boolean isRestrictedUserProfile(Context paramContext)
  {
    if (PlatformVersion.isAtLeastJellyBeanMR2())
    {
      paramContext = ((UserManager)paramContext.getSystemService("user")).getApplicationRestrictions(paramContext.getPackageName());
      if ((paramContext == null) || (!"true".equals(paramContext.getString("restricted_profile")))) {}
    }
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  @TargetApi(21)
  static boolean isUninstalledAppPossiblyUpdating(Context paramContext, String paramString)
  {
    boolean bool = paramString.equals("com.google.android.gms");
    if (PlatformVersion.isAtLeastLollipop()) {}
    for (;;)
    {
      try
      {
        localObject = paramContext.getPackageManager().getPackageInstaller().getAllSessions();
        localObject = ((List)localObject).iterator();
        if (!((Iterator)localObject).hasNext()) {
          break label69;
        }
        if (!paramString.equals(((PackageInstaller.SessionInfo)((Iterator)localObject).next()).getAppPackageName())) {
          continue;
        }
        bool = true;
      }
      catch (Exception paramContext)
      {
        bool = false;
        continue;
      }
      return bool;
      label69:
      Object localObject = paramContext.getPackageManager();
      try
      {
        paramString = ((PackageManager)localObject).getApplicationInfo(paramString, 8192);
        if (bool)
        {
          bool = paramString.enabled;
        }
        else
        {
          if (paramString.enabled)
          {
            bool = isRestrictedUserProfile(paramContext);
            if (!bool)
            {
              bool = true;
              continue;
            }
          }
          bool = false;
        }
      }
      catch (PackageManager.NameNotFoundException paramContext)
      {
        bool = false;
      }
    }
  }
  
  @Deprecated
  public static boolean isUserRecoverableError(int paramInt)
  {
    switch (paramInt)
    {
    }
    for (boolean bool = false;; bool = true) {
      return bool;
    }
  }
  
  private static int zza(Context paramContext, boolean paramBoolean, int paramInt)
  {
    i = 1;
    if (paramInt >= 0) {}
    for (boolean bool = true;; bool = false)
    {
      Preconditions.checkArgument(bool);
      localPackageManager = paramContext.getPackageManager();
      localObject = null;
      if (paramBoolean) {}
      try
      {
        localObject = localPackageManager.getPackageInfo("com.android.vending", 8256);
      }
      catch (PackageManager.NameNotFoundException paramContext)
      {
        for (;;)
        {
          label78:
          Log.w("GooglePlayServicesUtil", "Google Play Store is missing.");
          paramInt = 9;
        }
      }
      try
      {
        localPackageInfo = localPackageManager.getPackageInfo("com.google.android.gms", 64);
        paramContext = GoogleSignatureVerifier.getInstance(paramContext);
        if (paramContext.isGooglePublicSignedPackage(localPackageInfo, true)) {
          break label116;
        }
        Log.w("GooglePlayServicesUtil", "Google Play services signature invalid.");
        paramInt = 9;
      }
      catch (PackageManager.NameNotFoundException paramContext)
      {
        Log.w("GooglePlayServicesUtil", "Google Play services is missing.");
        paramInt = i;
        break label78;
        if ((!paramBoolean) || ((paramContext.isGooglePublicSignedPackage((PackageInfo)localObject, true)) && (localObject.signatures[0].equals(localPackageInfo.signatures[0])))) {
          break label165;
        }
        Log.w("GooglePlayServicesUtil", "Google Play Store signature invalid.");
        paramInt = 9;
        break label78;
        if (GmsVersionParser.parseBuildVersion(localPackageInfo.versionCode) >= GmsVersionParser.parseBuildVersion(paramInt)) {
          break label228;
        }
        i = localPackageInfo.versionCode;
        Log.w("GooglePlayServicesUtil", 77 + "Google Play services out of date.  Requires " + paramInt + " but found " + i);
        paramInt = 2;
        break label78;
        localObject = localPackageInfo.applicationInfo;
        paramContext = (Context)localObject;
        if (localObject != null) {
          break label252;
        }
        try
        {
          paramContext = localPackageManager.getApplicationInfo("com.google.android.gms", 0);
          if (paramContext.enabled) {
            break label280;
          }
          paramInt = 3;
        }
        catch (PackageManager.NameNotFoundException paramContext)
        {
          Log.wtf("GooglePlayServicesUtil", "Google Play services missing when getting application info.", paramContext);
          paramInt = i;
        }
        break label78;
        paramInt = 0;
        break label78;
      }
      return paramInt;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/GooglePlayServicesUtilLight.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */