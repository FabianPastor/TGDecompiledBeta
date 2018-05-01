package com.google.android.gms.internal.measurement;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import com.google.android.gms.common.util.Clock;

public final class zzjk
  extends zzhk
{
  private Handler handler;
  private long zzaqo = zzbt().elapsedRealtime();
  private final zzem zzaqp = new zzjl(this, this.zzacr);
  private final zzem zzaqq = new zzjm(this, this.zzacr);
  
  zzjk(zzgl paramzzgl)
  {
    super(paramzzgl);
  }
  
  private final void zzaf(long paramLong)
  {
    zzab();
    zzkq();
    this.zzaqp.cancel();
    this.zzaqq.cancel();
    zzgg().zzir().zzg("Activity resumed, time", Long.valueOf(paramLong));
    this.zzaqo = paramLong;
    if (zzbt().currentTimeMillis() - zzgh().zzaki.get() > zzgh().zzakk.get())
    {
      zzgh().zzakj.set(true);
      zzgh().zzakl.set(0L);
    }
    if (zzgh().zzakj.get()) {
      this.zzaqp.zzh(Math.max(0L, zzgh().zzakh.get() - zzgh().zzakl.get()));
    }
    for (;;)
    {
      return;
      this.zzaqq.zzh(Math.max(0L, 3600000L - zzgh().zzakl.get()));
    }
  }
  
  private final void zzag(long paramLong)
  {
    zzab();
    zzkq();
    this.zzaqp.cancel();
    this.zzaqq.cancel();
    zzgg().zzir().zzg("Activity paused, time", Long.valueOf(paramLong));
    if (this.zzaqo != 0L) {
      zzgh().zzakl.set(zzgh().zzakl.get() + (paramLong - this.zzaqo));
    }
  }
  
  private final void zzkq()
  {
    try
    {
      if (this.handler == null)
      {
        Handler localHandler = new android/os/Handler;
        localHandler.<init>(Looper.getMainLooper());
        this.handler = localHandler;
      }
      return;
    }
    finally {}
  }
  
  private final void zzkr()
  {
    zzab();
    zzm(false);
    zzfs().zzk(zzbt().elapsedRealtime());
  }
  
  protected final boolean zzhh()
  {
    return false;
  }
  
  public final boolean zzm(boolean paramBoolean)
  {
    boolean bool = true;
    zzab();
    zzch();
    long l1 = zzbt().elapsedRealtime();
    zzgh().zzakk.set(zzbt().currentTimeMillis());
    long l2 = l1 - this.zzaqo;
    if ((!paramBoolean) && (l2 < 1000L)) {
      zzgg().zzir().zzg("Screen exposed for less than 1000 ms. Event not sent. time", Long.valueOf(l2));
    }
    for (paramBoolean = false;; paramBoolean = bool)
    {
      return paramBoolean;
      zzgh().zzakl.set(l2);
      zzgg().zzir().zzg("Recording user engagement, ms", Long.valueOf(l2));
      Bundle localBundle = new Bundle();
      localBundle.putLong("_et", l2);
      zzih.zza(zzfy().zzkk(), localBundle, true);
      zzfu().zza("auto", "_e", localBundle);
      this.zzaqo = l1;
      this.zzaqq.cancel();
      this.zzaqq.zzh(Math.max(0L, 3600000L - zzgh().zzakl.get()));
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzjk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */