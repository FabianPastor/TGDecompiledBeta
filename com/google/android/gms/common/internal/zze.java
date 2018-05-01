package com.google.android.gms.common.internal;

import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;

abstract class zze
  extends zzi<Boolean>
{
  private int statusCode;
  private Bundle zzfyz;
  
  protected zze(zzd paramzzd, int paramInt, Bundle paramBundle)
  {
    super(paramzzd, Boolean.valueOf(true));
    this.statusCode = paramInt;
    this.zzfyz = paramBundle;
  }
  
  protected abstract boolean zzakr();
  
  protected abstract void zzj(ConnectionResult paramConnectionResult);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zze.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */