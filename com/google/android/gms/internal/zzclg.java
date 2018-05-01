package com.google.android.gms.internal;

import android.os.Bundle;
import com.google.android.gms.common.util.zzd;

final class zzclg
  extends zzcgs
{
  zzclg(zzclf paramzzclf, zzcim paramzzcim)
  {
    super(paramzzcim);
  }
  
  public final void run()
  {
    zzclf localzzclf = this.zzjjf;
    localzzclf.zzve();
    long l = localzzclf.zzws().elapsedRealtime();
    localzzclf.zzawy().zzazj().zzj("Session started, time", Long.valueOf(l));
    localzzclf.zzawz().zzjdg.set(false);
    localzzclf.zzawm().zzc("auto", "_s", new Bundle());
    localzzclf.zzawz().zzjdh.set(localzzclf.zzws().currentTimeMillis());
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzclg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */