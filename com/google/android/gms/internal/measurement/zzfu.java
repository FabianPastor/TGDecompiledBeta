package com.google.android.gms.internal.measurement;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.google.android.gms.common.internal.Preconditions;

public final class zzfu
{
  private long value;
  private boolean zzako;
  private final long zzakq;
  private final String zznt;
  
  public zzfu(zzfr paramzzfr, String paramString, long paramLong)
  {
    Preconditions.checkNotEmpty(paramString);
    this.zznt = paramString;
    this.zzakq = paramLong;
  }
  
  public final long get()
  {
    if (!this.zzako)
    {
      this.zzako = true;
      this.value = zzfr.zza(this.zzakp).getLong(this.zznt, this.zzakq);
    }
    return this.value;
  }
  
  public final void set(long paramLong)
  {
    SharedPreferences.Editor localEditor = zzfr.zza(this.zzakp).edit();
    localEditor.putLong(this.zznt, paramLong);
    localEditor.apply();
    this.value = paramLong;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzfu.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */