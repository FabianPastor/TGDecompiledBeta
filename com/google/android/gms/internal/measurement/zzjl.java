package com.google.android.gms.internal.measurement;

import android.os.Bundle;
import com.google.android.gms.common.util.Clock;

final class zzjl
  extends zzem
{
  zzjl(zzjk paramzzjk, zzgl paramzzgl)
  {
    super(paramzzgl);
  }
  
  public final void run()
  {
    zzjk localzzjk = this.zzaqr;
    localzzjk.zzab();
    long l = localzzjk.zzbt().elapsedRealtime();
    localzzjk.zzgg().zzir().zzg("Session started, time", Long.valueOf(l));
    localzzjk.zzgh().zzakj.set(false);
    localzzjk.zzfu().zza("auto", "_s", new Bundle());
    localzzjk.zzgh().zzakk.set(localzzjk.zzbt().currentTimeMillis());
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzjl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */