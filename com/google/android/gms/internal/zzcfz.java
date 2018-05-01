package com.google.android.gms.internal;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.internal.zzbo;

public final class zzcfz
{
  private final String zzBN;
  private long zzaeZ;
  private boolean zzbrE;
  private final long zzbrG;
  
  public zzcfz(zzcfw paramzzcfw, String paramString, long paramLong)
  {
    zzbo.zzcF(paramString);
    this.zzBN = paramString;
    this.zzbrG = paramLong;
  }
  
  @WorkerThread
  public final long get()
  {
    if (!this.zzbrE)
    {
      this.zzbrE = true;
      this.zzaeZ = zzcfw.zza(this.zzbrF).getLong(this.zzBN, this.zzbrG);
    }
    return this.zzaeZ;
  }
  
  @WorkerThread
  public final void set(long paramLong)
  {
    SharedPreferences.Editor localEditor = zzcfw.zza(this.zzbrF).edit();
    localEditor.putLong(this.zzBN, paramLong);
    localEditor.apply();
    this.zzaeZ = paramLong;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcfz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */