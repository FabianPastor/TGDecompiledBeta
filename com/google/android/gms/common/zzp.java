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
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Log;
import com.google.android.gms.R.string;
import com.google.android.gms.common.internal.zzbf;
import com.google.android.gms.common.util.zzi;
import com.google.android.gms.common.util.zzx;
import com.google.android.gms.internal.zzbhe;
import com.google.android.gms.internal.zzbhf;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class zzp
{
  @Deprecated
  public static final int GOOGLE_PLAY_SERVICES_VERSION_CODE = 11910000;
  private static boolean zzflj = false;
  private static boolean zzflk = false;
  private static boolean zzfll = false;
  private static boolean zzflm = false;
  static final AtomicBoolean zzfln = new AtomicBoolean();
  private static final AtomicBoolean zzflo = new AtomicBoolean();
  
  @Deprecated
  public static String getErrorString(int paramInt)
  {
    return ConnectionResult.getStatusString(paramInt);
  }
  
  public static Context getRemoteContext(Context paramContext)
  {
    try
    {
      paramContext = paramContext.createPackageContext("com.google.android.gms", 3);
      return paramContext;
    }
    catch (PackageManager.NameNotFoundException paramContext) {}
    return null;
  }
  
  public static Resources getRemoteResource(Context paramContext)
  {
    try
    {
      paramContext = paramContext.getPackageManager().getResourcesForApplication("com.google.android.gms");
      return paramContext;
    }
    catch (PackageManager.NameNotFoundException paramContext) {}
    return null;
  }
  
  @Deprecated
  public static int isGooglePlayServicesAvailable(Context paramContext)
  {
    PackageManager localPackageManager = paramContext.getPackageManager();
    try
    {
      paramContext.getResources().getString(R.string.common_google_play_services_unknown_issue);
      if ((!"com.google.android.gms".equals(paramContext.getPackageName())) && (!zzflo.get()))
      {
        i = zzbf.zzcq(paramContext);
        if (i == 0) {
          throw new IllegalStateException("A required meta-data tag in your app's AndroidManifest.xml does not exist.  You must have the following declaration within the <application> element:     <meta-data android:name=\"com.google.android.gms.version\" android:value=\"@integer/google_play_services_version\" />");
        }
      }
    }
    catch (Throwable localThrowable)
    {
      for (;;)
      {
        Log.e("GooglePlayServicesUtil", "The Google Play services resources were not found. Check your project configuration to ensure that the resources are included.");
      }
      int j;
      if (i != GOOGLE_PLAY_SERVICES_VERSION_CODE)
      {
        j = GOOGLE_PLAY_SERVICES_VERSION_CODE;
        throw new IllegalStateException(String.valueOf("com.google.android.gms.version").length() + 290 + "The meta-data tag in your app's AndroidManifest.xml does not have the right value.  Expected " + j + " but found " + i + ".  You must have the following declaration within the <application> element:     <meta-data android:name=\"" + "com.google.android.gms.version" + "\" android:value=\"@integer/google_play_services_version\" />");
      }
      if ((!zzi.zzct(paramContext)) && (!zzi.zzcv(paramContext))) {}
      PackageInfo localPackageInfo;
      for (int i = 1;; i = 0)
      {
        localObject = null;
        if (i != 0) {}
        try
        {
          localObject = localPackageManager.getPackageInfo("com.android.vending", 8256);
        }
        catch (PackageManager.NameNotFoundException paramContext)
        {
          Log.w("GooglePlayServicesUtil", "Google Play Store is missing.");
          return 9;
        }
        try
        {
          localPackageInfo = localPackageManager.getPackageInfo("com.google.android.gms", 64);
          zzq.zzci(paramContext);
          if (i == 0) {
            break label274;
          }
          paramContext = zzq.zza((PackageInfo)localObject, zzk.zzflf);
          if (paramContext != null) {
            break label247;
          }
          Log.w("GooglePlayServicesUtil", "Google Play Store signature invalid.");
          return 9;
        }
        catch (PackageManager.NameNotFoundException paramContext)
        {
          Log.w("GooglePlayServicesUtil", "Google Play services is missing.");
          return 1;
        }
      }
      label247:
      if (zzq.zza(localPackageInfo, new zzh[] { paramContext }) == null)
      {
        Log.w("GooglePlayServicesUtil", "Google Play services signature invalid.");
        return 9;
        label274:
        if (zzq.zza(localPackageInfo, zzk.zzflf) == null)
        {
          Log.w("GooglePlayServicesUtil", "Google Play services signature invalid.");
          return 9;
        }
      }
      i = GOOGLE_PLAY_SERVICES_VERSION_CODE / 1000;
      if (localPackageInfo.versionCode / 1000 < i)
      {
        i = GOOGLE_PLAY_SERVICES_VERSION_CODE;
        j = localPackageInfo.versionCode;
        Log.w("GooglePlayServicesUtil", 77 + "Google Play services out of date.  Requires " + i + " but found " + j);
        return 2;
      }
      Object localObject = localPackageInfo.applicationInfo;
      paramContext = (Context)localObject;
      if (localObject == null) {}
      try
      {
        paramContext = localPackageManager.getApplicationInfo("com.google.android.gms", 0);
        if (!paramContext.enabled) {
          return 3;
        }
      }
      catch (PackageManager.NameNotFoundException paramContext)
      {
        Log.wtf("GooglePlayServicesUtil", "Google Play services missing when getting application info.", paramContext);
        return 1;
      }
    }
    return 0;
  }
  
  @Deprecated
  public static boolean isUserRecoverableError(int paramInt)
  {
    switch (paramInt)
    {
    case 4: 
    case 5: 
    case 6: 
    case 7: 
    case 8: 
    default: 
      return false;
    }
    return true;
  }
  
  @Deprecated
  @TargetApi(19)
  public static boolean zzb(Context paramContext, int paramInt, String paramString)
  {
    return zzx.zzb(paramContext, paramInt, paramString);
  }
  
  @Deprecated
  public static void zzce(Context paramContext)
  {
    if (zzfln.getAndSet(true)) {}
    for (;;)
    {
      return;
      try
      {
        paramContext = (NotificationManager)paramContext.getSystemService("notification");
        if (paramContext != null)
        {
          paramContext.cancel(10436);
          return;
        }
      }
      catch (SecurityException paramContext) {}
    }
  }
  
  @Deprecated
  public static int zzcf(Context paramContext)
  {
    try
    {
      paramContext = paramContext.getPackageManager().getPackageInfo("com.google.android.gms", 0);
      return paramContext.versionCode;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      Log.w("GooglePlayServicesUtil", "Google Play services is missing.");
    }
    return 0;
  }
  
  public static boolean zzch(Context paramContext)
  {
    boolean bool = false;
    if (!zzflm) {}
    for (;;)
    {
      try
      {
        PackageInfo localPackageInfo = zzbhf.zzdb(paramContext).getPackageInfo("com.google.android.gms", 64);
        if (localPackageInfo == null) {
          continue;
        }
        zzq.zzci(paramContext);
        if (zzq.zza(localPackageInfo, new zzh[] { zzk.zzflf[1] }) == null) {
          continue;
        }
        zzfll = true;
      }
      catch (PackageManager.NameNotFoundException paramContext)
      {
        Log.w("GooglePlayServicesUtil", "Cannot find Google Play services package name.", paramContext);
        zzflm = true;
        continue;
      }
      finally
      {
        zzflm = true;
      }
      if ((zzfll) || (!"user".equals(Build.TYPE))) {
        bool = true;
      }
      return bool;
      zzfll = false;
    }
  }
  
  @Deprecated
  public static boolean zze(Context paramContext, int paramInt)
  {
    if (paramInt == 18) {
      return true;
    }
    if (paramInt == 1) {
      return zzv(paramContext, "com.google.android.gms");
    }
    return false;
  }
  
  @TargetApi(21)
  static boolean zzv(Context paramContext, String paramString)
  {
    boolean bool = paramString.equals("com.google.android.gms");
    if (com.google.android.gms.common.util.zzq.zzamn()) {
      try
      {
        localObject = paramContext.getPackageManager().getPackageInstaller().getAllSessions();
        localObject = ((List)localObject).iterator();
        while (((Iterator)localObject).hasNext()) {
          if (paramString.equals(((PackageInstaller.SessionInfo)((Iterator)localObject).next()).getAppPackageName())) {
            return true;
          }
        }
      }
      catch (Exception paramContext)
      {
        return false;
      }
    }
    Object localObject = paramContext.getPackageManager();
    try
    {
      paramString = ((PackageManager)localObject).getApplicationInfo(paramString, 8192);
      if (bool) {
        return paramString.enabled;
      }
      if (paramString.enabled)
      {
        if (com.google.android.gms.common.util.zzq.zzamk())
        {
          paramContext = ((UserManager)paramContext.getSystemService("user")).getApplicationRestrictions(paramContext.getPackageName());
          if (paramContext != null)
          {
            bool = "true".equals(paramContext.getString("restricted_profile"));
            if (!bool) {}
          }
        }
        for (int i = 1; i == 0; i = 0) {
          return true;
        }
      }
      return false;
    }
    catch (PackageManager.NameNotFoundException paramContext) {}
    return false;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/zzp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */