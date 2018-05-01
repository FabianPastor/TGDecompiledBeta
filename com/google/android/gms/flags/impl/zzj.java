package com.google.android.gms.flags.impl;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.android.gms.internal.zzcbc;

public final class zzj
{
  private static SharedPreferences zzhje = null;
  
  public static SharedPreferences zzdi(Context paramContext)
    throws Exception
  {
    try
    {
      if (zzhje == null) {
        zzhje = (SharedPreferences)zzcbc.zzb(new zzk(paramContext));
      }
      paramContext = zzhje;
      return paramContext;
    }
    finally {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/flags/impl/zzj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */