package com.google.android.gms.ads.identifier;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.android.gms.common.zzp;

public final class zzb
{
  private SharedPreferences zzani;
  
  public zzb(Context paramContext)
  {
    try
    {
      paramContext = zzp.getRemoteContext(paramContext);
      if (paramContext == null) {}
      for (paramContext = null;; paramContext = paramContext.getSharedPreferences("google_ads_flags", 0))
      {
        this.zzani = paramContext;
        return;
      }
      return;
    }
    catch (Throwable paramContext)
    {
      Log.w("GmscoreFlag", "Error while getting SharedPreferences ", paramContext);
      this.zzani = null;
    }
  }
  
  public final boolean getBoolean(String paramString, boolean paramBoolean)
  {
    try
    {
      if (this.zzani == null) {
        return false;
      }
      paramBoolean = this.zzani.getBoolean(paramString, false);
      return paramBoolean;
    }
    catch (Throwable paramString)
    {
      Log.w("GmscoreFlag", "Error while reading from SharedPreferences ", paramString);
    }
    return false;
  }
  
  final float getFloat(String paramString, float paramFloat)
  {
    try
    {
      if (this.zzani == null) {
        return 0.0F;
      }
      paramFloat = this.zzani.getFloat(paramString, 0.0F);
      return paramFloat;
    }
    catch (Throwable paramString)
    {
      Log.w("GmscoreFlag", "Error while reading from SharedPreferences ", paramString);
    }
    return 0.0F;
  }
  
  final String getString(String paramString1, String paramString2)
  {
    try
    {
      if (this.zzani == null) {
        return paramString2;
      }
      paramString1 = this.zzani.getString(paramString1, paramString2);
      return paramString1;
    }
    catch (Throwable paramString1)
    {
      Log.w("GmscoreFlag", "Error while reading from SharedPreferences ", paramString1);
    }
    return paramString2;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/ads/identifier/zzb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */