package com.google.android.gms.flags.impl;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.android.gms.flags.impl.util.StrictModeUtil;

public class SharedPreferencesFactory
{
  private static SharedPreferences zzacv = null;
  
  public static SharedPreferences getSharedPreferences(Context paramContext)
    throws Exception
  {
    try
    {
      if (zzacv == null)
      {
        zze localzze = new com/google/android/gms/flags/impl/zze;
        localzze.<init>(paramContext);
        zzacv = (SharedPreferences)StrictModeUtil.runWithLaxStrictMode(localzze);
      }
      paramContext = zzacv;
      return paramContext;
    }
    finally {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/flags/impl/SharedPreferencesFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */