package com.google.android.gms.internal.measurement;

final class zzjr
  extends zzem
{
  zzjr(zzjq paramzzjq, zzgl paramzzgl1, zzgl paramzzgl2)
  {
    super(paramzzgl1);
  }
  
  public final void run()
  {
    this.zzaqt.cancel();
    this.zzaqt.zzgg().zzir().log("Starting upload from DelayedRunnable");
    this.zzafi.zzjw();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzjr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */