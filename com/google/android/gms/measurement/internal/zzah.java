package com.google.android.gms.measurement.internal;

import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.util.zze;

class zzah
{
  private final zze zzaql;
  private long zzbwt;
  
  public zzah(zze paramzze)
  {
    zzaa.zzy(paramzze);
    this.zzaql = paramzze;
  }
  
  public void clear()
  {
    this.zzbwt = 0L;
  }
  
  public void start()
  {
    this.zzbwt = this.zzaql.elapsedRealtime();
  }
  
  public boolean zzz(long paramLong)
  {
    if (this.zzbwt == 0L) {}
    while (this.zzaql.elapsedRealtime() - this.zzbwt >= paramLong) {
      return true;
    }
    return false;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzah.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */