package com.google.android.gms.flags.impl;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.android.gms.internal.zzvb;
import java.util.concurrent.Callable;

public class zzb
{
  private static SharedPreferences UF = null;
  
  public static SharedPreferences zzn(Context paramContext)
  {
    try
    {
      if (UF == null) {
        UF = (SharedPreferences)zzvb.zzb(new Callable()
        {
          public SharedPreferences zzbhq()
          {
            return zzb.this.getSharedPreferences("google_sdk_flags", 1);
          }
        });
      }
      paramContext = UF;
      return paramContext;
    }
    finally {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/flags/impl/zzb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */