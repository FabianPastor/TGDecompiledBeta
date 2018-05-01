package com.google.android.gms.flags.impl;

import android.content.SharedPreferences;
import android.util.Log;
import com.google.android.gms.internal.zzcbc;

public final class zzf
  extends zza<Long>
{
  public static Long zza(SharedPreferences paramSharedPreferences, String paramString, Long paramLong)
  {
    try
    {
      paramSharedPreferences = (Long)zzcbc.zzb(new zzg(paramSharedPreferences, paramString, paramLong));
      return paramSharedPreferences;
    }
    catch (Exception paramSharedPreferences)
    {
      paramSharedPreferences = String.valueOf(paramSharedPreferences.getMessage());
      if (paramSharedPreferences.length() == 0) {}
    }
    for (paramSharedPreferences = "Flag value not available, returning default: ".concat(paramSharedPreferences);; paramSharedPreferences = new String("Flag value not available, returning default: "))
    {
      Log.w("FlagDataUtils", paramSharedPreferences);
      return paramLong;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/flags/impl/zzf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */