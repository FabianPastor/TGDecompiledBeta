package com.google.android.gms.internal.measurement;

final class zzhs
  implements Runnable
{
  zzhs(zzhm paramzzhm) {}
  
  public final void run()
  {
    boolean bool1 = true;
    zzhm localzzhm = this.zzaop;
    localzzhm.zzab();
    localzzhm.zzch();
    localzzhm.zzgg().zziq().log("Resetting analytics data (FE)");
    boolean bool2 = localzzhm.zzacr.isEnabled();
    if (!localzzhm.zzgi().zzhi())
    {
      zzfr localzzfr = localzzhm.zzgh();
      if (!bool2)
      {
        bool3 = true;
        localzzfr.zzh(bool3);
      }
    }
    else
    {
      localzzhm.zzfx().resetAnalyticsData();
      if (bool2) {
        break label92;
      }
    }
    label92:
    for (boolean bool3 = bool1;; bool3 = false)
    {
      localzzhm.zzaon = bool3;
      return;
      bool3 = false;
      break;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzhs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */