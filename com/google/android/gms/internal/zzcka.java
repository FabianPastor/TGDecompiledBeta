package com.google.android.gms.internal;

final class zzcka
  implements Runnable
{
  zzcka(zzcjn paramzzcjn) {}
  
  public final void run()
  {
    zzcjn localzzcjn = this.zzjhc;
    localzzcjn.zzve();
    localzzcjn.zzxf();
    localzzcjn.zzawy().zzazi().log("Resetting analytics data (FE)");
    localzzcjn.zzawp().resetAnalyticsData();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcka.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */