package com.google.android.gms.internal;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.google.android.gms.common.internal.zzbq;

public final class zzcia
{
  private final String zzbhb;
  private long zzdrr;
  private boolean zzjdl;
  private final long zzjdn;
  
  public zzcia(zzchx paramzzchx, String paramString, long paramLong)
  {
    zzbq.zzgm(paramString);
    this.zzbhb = paramString;
    this.zzjdn = paramLong;
  }
  
  public final long get()
  {
    if (!this.zzjdl)
    {
      this.zzjdl = true;
      this.zzdrr = zzchx.zza(this.zzjdm).getLong(this.zzbhb, this.zzjdn);
    }
    return this.zzdrr;
  }
  
  public final void set(long paramLong)
  {
    SharedPreferences.Editor localEditor = zzchx.zza(this.zzjdm).edit();
    localEditor.putLong(this.zzbhb, paramLong);
    localEditor.apply();
    this.zzdrr = paramLong;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcia.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */