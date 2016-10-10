package com.google.android.gms.common;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzp;
import com.google.android.gms.common.util.zzi;
import com.google.android.gms.internal.zzsh;
import com.google.android.gms.internal.zzsi;

public class zzc
{
  public static final String GOOGLE_PLAY_SERVICES_PACKAGE = "com.google.android.gms";
  public static final int GOOGLE_PLAY_SERVICES_VERSION_CODE = zze.GOOGLE_PLAY_SERVICES_VERSION_CODE;
  private static final zzc uN = new zzc();
  
  public static zzc zzapd()
  {
    return uN;
  }
  
  private String zzt(@Nullable Context paramContext, @Nullable String paramString)
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
      localStringBuilder.append(zzsi.zzcr(paramContext).getPackageInfo(paramContext.getPackageName(), 0).versionCode);
      return localStringBuilder.toString();
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      for (;;) {}
    }
  }
  
  @Nullable
  public PendingIntent getErrorResolutionPendingIntent(Context paramContext, int paramInt1, int paramInt2)
  {
    return zza(paramContext, paramInt1, paramInt2, null);
  }
  
  public String getErrorString(int paramInt)
  {
    return zze.getErrorString(paramInt);
  }
  
  @Nullable
  public String getOpenSourceSoftwareLicenseInfo(Context paramContext)
  {
    return zze.getOpenSourceSoftwareLicenseInfo(paramContext);
  }
  
  public int isGooglePlayServicesAvailable(Context paramContext)
  {
    int j = zze.isGooglePlayServicesAvailable(paramContext);
    int i = j;
    if (zze.zzd(paramContext, j)) {
      i = 18;
    }
    return i;
  }
  
  public boolean isUserResolvableError(int paramInt)
  {
    return zze.isUserRecoverableError(paramInt);
  }
  
  @Nullable
  public PendingIntent zza(Context paramContext, int paramInt1, int paramInt2, @Nullable String paramString)
  {
    int i = paramInt1;
    if (zzi.zzcl(paramContext))
    {
      i = paramInt1;
      if (paramInt1 == 2) {
        i = 42;
      }
    }
    paramString = zza(paramContext, i, paramString);
    if (paramString == null) {
      return null;
    }
    return PendingIntent.getActivity(paramContext, paramInt2, paramString, 268435456);
  }
  
  @Nullable
  public Intent zza(Context paramContext, int paramInt, @Nullable String paramString)
  {
    switch (paramInt)
    {
    default: 
      return null;
    case 1: 
    case 2: 
      return zzp.zzad("com.google.android.gms", zzt(paramContext, paramString));
    case 42: 
      return zzp.zzaux();
    }
    return zzp.zzhw("com.google.android.gms");
  }
  
  public int zzbo(Context paramContext)
  {
    return zze.zzbo(paramContext);
  }
  
  public void zzbp(Context paramContext)
    throws GooglePlayServicesRepairableException, GooglePlayServicesNotAvailableException
  {
    zze.zzbc(paramContext);
  }
  
  public void zzbq(Context paramContext)
  {
    zze.zzbq(paramContext);
  }
  
  public boolean zzd(Context paramContext, int paramInt)
  {
    return zze.zzd(paramContext, paramInt);
  }
  
  @Deprecated
  @Nullable
  public Intent zzfl(int paramInt)
  {
    return zza(null, paramInt, null);
  }
  
  public boolean zzs(Context paramContext, String paramString)
  {
    return zze.zzs(paramContext, paramString);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/zzc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */