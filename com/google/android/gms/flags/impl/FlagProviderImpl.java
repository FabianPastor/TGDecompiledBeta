package com.google.android.gms.flags.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import com.google.android.gms.common.util.DynamiteApi;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.ObjectWrapper;
import com.google.android.gms.flags.IFlagProvider.Stub;

@DynamiteApi
public class FlagProviderImpl
  extends IFlagProvider.Stub
{
  private boolean zzacf = false;
  private SharedPreferences zzacu;
  
  public boolean getBooleanFlagValue(String paramString, boolean paramBoolean, int paramInt)
  {
    if (!this.zzacf) {}
    for (;;)
    {
      return paramBoolean;
      paramBoolean = DataUtils.BooleanUtils.getFromSharedPreferencesNoStrict(this.zzacu, paramString, Boolean.valueOf(paramBoolean)).booleanValue();
    }
  }
  
  public int getIntFlagValue(String paramString, int paramInt1, int paramInt2)
  {
    if (!this.zzacf) {}
    for (;;)
    {
      return paramInt1;
      paramInt1 = DataUtils.IntegerUtils.getFromSharedPreferencesNoStrict(this.zzacu, paramString, Integer.valueOf(paramInt1)).intValue();
    }
  }
  
  public long getLongFlagValue(String paramString, long paramLong, int paramInt)
  {
    if (!this.zzacf) {}
    for (;;)
    {
      return paramLong;
      paramLong = DataUtils.LongUtils.getFromSharedPreferencesNoStrict(this.zzacu, paramString, Long.valueOf(paramLong)).longValue();
    }
  }
  
  public String getStringFlagValue(String paramString1, String paramString2, int paramInt)
  {
    if (!this.zzacf) {}
    for (;;)
    {
      return paramString2;
      paramString2 = DataUtils.StringUtils.getFromSharedPreferencesNoStrict(this.zzacu, paramString1, paramString2);
    }
  }
  
  public void init(IObjectWrapper paramIObjectWrapper)
  {
    paramIObjectWrapper = (Context)ObjectWrapper.unwrap(paramIObjectWrapper);
    if (this.zzacf) {}
    for (;;)
    {
      return;
      try
      {
        this.zzacu = SharedPreferencesFactory.getSharedPreferences(paramIObjectWrapper.createPackageContext("com.google.android.gms", 0));
        this.zzacf = true;
      }
      catch (PackageManager.NameNotFoundException paramIObjectWrapper) {}catch (Exception paramIObjectWrapper)
      {
        paramIObjectWrapper = String.valueOf(paramIObjectWrapper.getMessage());
        if (paramIObjectWrapper.length() == 0) {}
      }
    }
    for (paramIObjectWrapper = "Could not retrieve sdk flags, continuing with defaults: ".concat(paramIObjectWrapper);; paramIObjectWrapper = new String("Could not retrieve sdk flags, continuing with defaults: "))
    {
      Log.w("FlagProviderImpl", paramIObjectWrapper);
      break;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/flags/impl/FlagProviderImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */