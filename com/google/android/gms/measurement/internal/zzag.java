package com.google.android.gms.measurement.internal;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.util.zze;

public class zzag
  extends zzaa
{
  protected long avM;
  private final zzf avN = new zzf(this.aqw)
  {
    @WorkerThread
    public void run()
    {
      zzag.this.zzbze();
    }
  };
  private final zzf avO = new zzf(this.aqw)
  {
    @WorkerThread
    public void run()
    {
      zzag.zza(zzag.this);
    }
  };
  private Handler mHandler;
  
  zzag(zzx paramzzx)
  {
    super(paramzzx);
  }
  
  @WorkerThread
  private void zzbn(long paramLong)
  {
    zzzx();
    zzbzc();
    this.avN.cancel();
    this.avO.cancel();
    zzbwb().zzbxe().zzj("Activity resumed, time", Long.valueOf(paramLong));
    this.avM = paramLong;
    if (zzabz().currentTimeMillis() - zzbwc().atj.get() > zzbwc().atl.get())
    {
      zzbwc().atk.set(true);
      zzbwc().atm.set(0L);
    }
    if (zzbwc().atk.get())
    {
      this.avN.zzx(Math.max(0L, zzbwc().ati.get() - zzbwc().atm.get()));
      return;
    }
    this.avO.zzx(Math.max(0L, 3600000L - zzbwc().atm.get()));
  }
  
  @WorkerThread
  private void zzbo(long paramLong)
  {
    zzzx();
    zzbzc();
    this.avN.cancel();
    this.avO.cancel();
    zzbwb().zzbxe().zzj("Activity paused, time", Long.valueOf(paramLong));
    if (this.avM != 0L) {
      zzbwc().atm.set(zzbwc().atm.get() + (paramLong - this.avM));
    }
    zzbwc().atl.set(zzabz().currentTimeMillis());
  }
  
  private void zzbzc()
  {
    try
    {
      if (this.mHandler == null) {
        this.mHandler = new Handler(Looper.getMainLooper());
      }
      return;
    }
    finally {}
  }
  
  @WorkerThread
  private void zzbzf()
  {
    zzzx();
    zzck(false);
  }
  
  @MainThread
  protected void zzbzb()
  {
    final long l = zzabz().elapsedRealtime();
    zzbwa().zzm(new Runnable()
    {
      public void run()
      {
        zzag.zza(zzag.this, l);
      }
    });
  }
  
  @MainThread
  protected void zzbzd()
  {
    final long l = zzabz().elapsedRealtime();
    zzbwa().zzm(new Runnable()
    {
      public void run()
      {
        zzag.zzb(zzag.this, l);
      }
    });
  }
  
  @WorkerThread
  protected void zzbze()
  {
    zzzx();
    long l = zzabz().elapsedRealtime();
    zzbwb().zzbxe().zzj("Session started, time", Long.valueOf(l));
    zzbwc().atk.set(false);
    zzbvq().zzf("auto", "_s", new Bundle());
  }
  
  @WorkerThread
  public boolean zzck(boolean paramBoolean)
  {
    zzzx();
    zzacj();
    long l1 = zzabz().elapsedRealtime();
    if (this.avM == 0L) {
      this.avM = (l1 - 3600000L);
    }
    long l2 = l1 - this.avM;
    if ((!paramBoolean) && (l2 < 1000L))
    {
      zzbwb().zzbxe().zzj("Screen exposed for less than 1000 ms. Event not sent. time", Long.valueOf(l2));
      return false;
    }
    zzbwc().atm.set(l2);
    zzbwb().zzbxe().zzj("Recording user engagement, ms", Long.valueOf(l2));
    Bundle localBundle = new Bundle();
    localBundle.putLong("_et", l2);
    zzad.zza(zzbvu().zzbyt(), localBundle);
    zzbvq().zzf("auto", "_e", localBundle);
    this.avM = l1;
    this.avO.cancel();
    this.avO.zzx(Math.max(0L, 3600000L - zzbwc().atm.get()));
    return true;
  }
  
  protected void zzzy() {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzag.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */