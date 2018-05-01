package com.google.android.gms.internal;

import android.os.Process;
import java.util.concurrent.BlockingQueue;

public final class zzd
  extends Thread
{
  private static final boolean DEBUG = zzab.DEBUG;
  private final BlockingQueue<zzp<?>> zzg;
  private final BlockingQueue<zzp<?>> zzh;
  private final zzb zzi;
  private final zzw zzj;
  private volatile boolean zzk = false;
  
  public zzd(BlockingQueue<zzp<?>> paramBlockingQueue1, BlockingQueue<zzp<?>> paramBlockingQueue2, zzb paramzzb, zzw paramzzw)
  {
    this.zzg = paramBlockingQueue1;
    this.zzh = paramBlockingQueue2;
    this.zzi = paramzzb;
    this.zzj = paramzzw;
  }
  
  public final void quit()
  {
    this.zzk = true;
    interrupt();
  }
  
  public final void run()
  {
    if (DEBUG) {
      zzab.zza("start new dispatcher", new Object[0]);
    }
    Process.setThreadPriority(10);
    this.zzi.initialize();
    zzc localzzc;
    for (;;)
    {
      try
      {
        zzp localzzp = (zzp)this.zzg.take();
        localzzp.zzb("cache-queue-take");
        localzzc = this.zzi.zza(localzzp.getUrl());
        if (localzzc != null) {
          break label94;
        }
        localzzp.zzb("cache-miss");
        this.zzh.put(localzzp);
        continue;
        if (!this.zzk) {
          continue;
        }
      }
      catch (InterruptedException localInterruptedException) {}
      return;
      label94:
      if (localzzc.zzd >= System.currentTimeMillis()) {
        break label243;
      }
      i = 1;
      label107:
      if (i == 0) {
        break;
      }
      localInterruptedException.zzb("cache-hit-expired");
      localInterruptedException.zza(localzzc);
      this.zzh.put(localInterruptedException);
    }
    localInterruptedException.zzb("cache-hit");
    zzt localzzt = localInterruptedException.zza(new zzn(localzzc.data, localzzc.zzf));
    localInterruptedException.zzb("cache-hit-parsed");
    if (localzzc.zze < System.currentTimeMillis()) {}
    for (int i = 1;; i = 0)
    {
      if (i == 0)
      {
        this.zzj.zza(localInterruptedException, localzzt);
        break;
      }
      localInterruptedException.zzb("cache-hit-refresh-needed");
      localInterruptedException.zza(localzzc);
      localzzt.zzag = true;
      this.zzj.zza(localInterruptedException, localzzt, new zze(this, localInterruptedException));
      break;
      label243:
      i = 0;
      break label107;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */