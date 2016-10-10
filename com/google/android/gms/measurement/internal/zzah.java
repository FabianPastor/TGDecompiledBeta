package com.google.android.gms.measurement.internal;

import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zze;

class zzah
{
  private final zze zzapy;
  private long zzbtl;
  
  public zzah(zze paramzze)
  {
    zzac.zzy(paramzze);
    this.zzapy = paramzze;
  }
  
  public void clear()
  {
    this.zzbtl = 0L;
  }
  
  public void start()
  {
    this.zzbtl = this.zzapy.elapsedRealtime();
  }
  
  public boolean zzz(long paramLong)
  {
    if (this.zzbtl == 0L) {}
    while (this.zzapy.elapsedRealtime() - this.zzbtl >= paramLong) {
      return true;
    }
    return false;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzah.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */