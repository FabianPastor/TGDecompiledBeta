package com.google.android.gms.flags.impl;

import android.content.SharedPreferences;
import android.util.Log;
import com.google.android.gms.flags.impl.util.StrictModeUtil;

public abstract class DataUtils<T>
{
  public static class BooleanUtils
    extends DataUtils<Boolean>
  {
    public static Boolean getFromSharedPreferencesNoStrict(SharedPreferences paramSharedPreferences, String paramString, Boolean paramBoolean)
    {
      try
      {
        zza localzza = new com/google/android/gms/flags/impl/zza;
        localzza.<init>(paramSharedPreferences, paramString, paramBoolean);
        paramSharedPreferences = (Boolean)StrictModeUtil.runWithLaxStrictMode(localzza);
        paramBoolean = paramSharedPreferences;
        return paramBoolean;
      }
      catch (Exception paramSharedPreferences)
      {
        paramSharedPreferences = String.valueOf(paramSharedPreferences.getMessage());
        if (paramSharedPreferences.length() == 0) {}
      }
      for (paramSharedPreferences = "Flag value not available, returning default: ".concat(paramSharedPreferences);; paramSharedPreferences = new String("Flag value not available, returning default: "))
      {
        Log.w("FlagDataUtils", paramSharedPreferences);
        break;
      }
    }
  }
  
  public static class IntegerUtils
    extends DataUtils<Integer>
  {
    public static Integer getFromSharedPreferencesNoStrict(SharedPreferences paramSharedPreferences, String paramString, Integer paramInteger)
    {
      try
      {
        zzb localzzb = new com/google/android/gms/flags/impl/zzb;
        localzzb.<init>(paramSharedPreferences, paramString, paramInteger);
        paramSharedPreferences = (Integer)StrictModeUtil.runWithLaxStrictMode(localzzb);
        paramInteger = paramSharedPreferences;
        return paramInteger;
      }
      catch (Exception paramSharedPreferences)
      {
        paramSharedPreferences = String.valueOf(paramSharedPreferences.getMessage());
        if (paramSharedPreferences.length() == 0) {}
      }
      for (paramSharedPreferences = "Flag value not available, returning default: ".concat(paramSharedPreferences);; paramSharedPreferences = new String("Flag value not available, returning default: "))
      {
        Log.w("FlagDataUtils", paramSharedPreferences);
        break;
      }
    }
  }
  
  public static class LongUtils
    extends DataUtils<Long>
  {
    public static Long getFromSharedPreferencesNoStrict(SharedPreferences paramSharedPreferences, String paramString, Long paramLong)
    {
      try
      {
        zzc localzzc = new com/google/android/gms/flags/impl/zzc;
        localzzc.<init>(paramSharedPreferences, paramString, paramLong);
        paramSharedPreferences = (Long)StrictModeUtil.runWithLaxStrictMode(localzzc);
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
        paramSharedPreferences = paramLong;
        break;
      }
    }
  }
  
  public static class StringUtils
    extends DataUtils<String>
  {
    public static String getFromSharedPreferencesNoStrict(SharedPreferences paramSharedPreferences, String paramString1, String paramString2)
    {
      try
      {
        zzd localzzd = new com/google/android/gms/flags/impl/zzd;
        localzzd.<init>(paramSharedPreferences, paramString1, paramString2);
        paramSharedPreferences = (String)StrictModeUtil.runWithLaxStrictMode(localzzd);
        paramString2 = paramSharedPreferences;
        return paramString2;
      }
      catch (Exception paramSharedPreferences)
      {
        paramSharedPreferences = String.valueOf(paramSharedPreferences.getMessage());
        if (paramSharedPreferences.length() == 0) {}
      }
      for (paramSharedPreferences = "Flag value not available, returning default: ".concat(paramSharedPreferences);; paramSharedPreferences = new String("Flag value not available, returning default: "))
      {
        Log.w("FlagDataUtils", paramSharedPreferences);
        break;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/flags/impl/DataUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */