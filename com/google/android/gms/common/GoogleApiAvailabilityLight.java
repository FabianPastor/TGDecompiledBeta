package com.google.android.gms.common;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import com.google.android.gms.common.internal.GmsIntents;
import com.google.android.gms.common.util.DeviceProperties;
import com.google.android.gms.common.wrappers.PackageManagerWrapper;
import com.google.android.gms.common.wrappers.Wrappers;

public class GoogleApiAvailabilityLight
{
  public static final int GOOGLE_PLAY_SERVICES_VERSION_CODE = GooglePlayServicesUtilLight.GOOGLE_PLAY_SERVICES_VERSION_CODE;
  private static final GoogleApiAvailabilityLight zzaw = new GoogleApiAvailabilityLight();
  
  public static GoogleApiAvailabilityLight getInstance()
  {
    return zzaw;
  }
  
  private static String zza(Context paramContext, String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("gcore_");
    localStringBuilder.append(GOOGLE_PLAY_SERVICES_VERSION_CODE);
    localStringBuilder.append("-");
    if (!TextUtils.isEmpty(paramString)) {
      localStringBuilder.append(paramString);
    }
    localStringBuilder.append("-");
    if (paramContext != null) {
      localStringBuilder.append(paramContext.getPackageName());
    }
    localStringBuilder.append("-");
    if (paramContext != null) {}
    try
    {
      localStringBuilder.append(Wrappers.packageManager(paramContext).getPackageInfo(paramContext.getPackageName(), 0).versionCode);
      return localStringBuilder.toString();
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      for (;;) {}
    }
  }
  
  public void cancelAvailabilityErrorNotifications(Context paramContext)
  {
    GooglePlayServicesUtilLight.cancelAvailabilityErrorNotifications(paramContext);
  }
  
  public int getApkVersion(Context paramContext)
  {
    return GooglePlayServicesUtilLight.getApkVersion(paramContext);
  }
  
  @Deprecated
  public Intent getErrorResolutionIntent(int paramInt)
  {
    return getErrorResolutionIntent(null, paramInt, null);
  }
  
  public Intent getErrorResolutionIntent(Context paramContext, int paramInt, String paramString)
  {
    switch (paramInt)
    {
    default: 
      paramContext = null;
    }
    for (;;)
    {
      return paramContext;
      if ((paramContext != null) && (DeviceProperties.isWearableWithoutPlayStore(paramContext)))
      {
        paramContext = GmsIntents.createAndroidWearUpdateIntent();
      }
      else
      {
        paramContext = GmsIntents.createPlayStoreIntent("com.google.android.gms", zza(paramContext, paramString));
        continue;
        paramContext = GmsIntents.createSettingsIntent("com.google.android.gms");
      }
    }
  }
  
  public PendingIntent getErrorResolutionPendingIntent(Context paramContext, int paramInt1, int paramInt2)
  {
    return getErrorResolutionPendingIntent(paramContext, paramInt1, paramInt2, null);
  }
  
  public PendingIntent getErrorResolutionPendingIntent(Context paramContext, int paramInt1, int paramInt2, String paramString)
  {
    paramString = getErrorResolutionIntent(paramContext, paramInt1, paramString);
    if (paramString == null) {}
    for (paramContext = null;; paramContext = PendingIntent.getActivity(paramContext, paramInt2, paramString, 134217728)) {
      return paramContext;
    }
  }
  
  public String getErrorString(int paramInt)
  {
    return GooglePlayServicesUtilLight.getErrorString(paramInt);
  }
  
  public int isGooglePlayServicesAvailable(Context paramContext)
  {
    return isGooglePlayServicesAvailable(paramContext, GOOGLE_PLAY_SERVICES_VERSION_CODE);
  }
  
  public int isGooglePlayServicesAvailable(Context paramContext, int paramInt)
  {
    int i = GooglePlayServicesUtilLight.isGooglePlayServicesAvailable(paramContext, paramInt);
    paramInt = i;
    if (GooglePlayServicesUtilLight.isPlayServicesPossiblyUpdating(paramContext, i)) {
      paramInt = 18;
    }
    return paramInt;
  }
  
  public boolean isPlayServicesPossiblyUpdating(Context paramContext, int paramInt)
  {
    return GooglePlayServicesUtilLight.isPlayServicesPossiblyUpdating(paramContext, paramInt);
  }
  
  public boolean isUninstalledAppPossiblyUpdating(Context paramContext, String paramString)
  {
    return GooglePlayServicesUtilLight.isUninstalledAppPossiblyUpdating(paramContext, paramString);
  }
  
  public boolean isUserResolvableError(int paramInt)
  {
    return GooglePlayServicesUtilLight.isUserRecoverableError(paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/GoogleApiAvailabilityLight.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */