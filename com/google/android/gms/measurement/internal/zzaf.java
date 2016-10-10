package com.google.android.gms.measurement.internal;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.util.zze;

public class zzaf
  extends zzaa
{
  private long asg;
  private final Runnable ash = new Runnable()
  {
    @MainThread
    public void run()
    {
      zzaf.this.zzbvf().zzm(new Runnable()
      {
        public void run()
        {
          zzaf.this.zzbyi();
        }
      });
    }
  };
  private final zzf asi = new zzf(this.anq)
  {
    @WorkerThread
    public void run()
    {
      zzaf.zza(zzaf.this);
    }
  };
  private final zzf asj = new zzf(this.anq)
  {
    @WorkerThread
    public void run()
    {
      zzaf.zzb(zzaf.this);
    }
  };
  private Handler mHandler;
  
  zzaf(zzx paramzzx)
  {
    super(paramzzx);
  }
  
  @WorkerThread
  private void zzbp(long paramLong)
  {
    zzyl();
    zzbyg();
    this.asi.cancel();
    this.asj.cancel();
    zzbvg().zzbwj().zzj("Activity resumed, time", Long.valueOf(paramLong));
    this.asg = paramLong;
    if (zzaan().currentTimeMillis() - zzbvh().aqa.get() > zzbvh().aqc.get())
    {
      zzbvh().aqb.set(true);
      zzbvh().aqd.set(0L);
    }
    if (zzbvh().aqb.get())
    {
      this.asi.zzx(Math.max(0L, zzbvh().apZ.get() - zzbvh().aqd.get()));
      return;
    }
    this.asj.zzx(Math.max(0L, 3600000L - zzbvh().aqd.get()));
  }
  
  @WorkerThread
  private void zzbq(long paramLong)
  {
    zzyl();
    zzbyg();
    this.asi.cancel();
    this.asj.cancel();
    zzbvg().zzbwj().zzj("Activity paused, time", Long.valueOf(paramLong));
    if (this.asg != 0L) {
      zzbvh().aqd.set(zzbvh().aqd.get() + (paramLong - this.asg));
    }
    zzbvh().aqc.set(zzaan().currentTimeMillis());
    try
    {
      if (!zzbvh().aqb.get()) {
        this.mHandler.postDelayed(this.ash, 1000L);
      }
      return;
    }
    finally {}
  }
  
  private void zzbyg()
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
  private void zzbyj()
  {
    zzyl();
    long l = zzaan().elapsedRealtime();
    zzbvg().zzbwj().zzj("Session started, time", Long.valueOf(l));
    zzbvh().aqb.set(false);
    zzbux().zzf("auto", "_s", new Bundle());
  }
  
  @WorkerThread
  private void zzbyk()
  {
    zzyl();
    long l1 = zzaan().elapsedRealtime();
    if (this.asg == 0L) {
      this.asg = (l1 - 3600000L);
    }
    long l2 = zzbvh().aqd.get() + (l1 - this.asg);
    zzbvh().aqd.set(l2);
    zzbvg().zzbwj().zzj("Recording user engagement, ms", Long.valueOf(l2));
    Bundle localBundle = new Bundle();
    localBundle.putLong("_et", l2);
    zzbux().zzf("auto", "_e", localBundle);
    zzbvh().aqd.set(0L);
    this.asg = l1;
    this.asj.zzx(Math.max(0L, 3600000L - zzbvh().aqd.get()));
  }
  
  @MainThread
  protected void zzbyf()
  {
    try
    {
      zzbyg();
      this.mHandler.removeCallbacks(this.ash);
      final long l = zzaan().elapsedRealtime();
      zzbvf().zzm(new Runnable()
      {
        public void run()
        {
          zzaf.zza(zzaf.this, l);
        }
      });
      return;
    }
    finally {}
  }
  
  @MainThread
  protected void zzbyh()
  {
    final long l = zzaan().elapsedRealtime();
    zzbvf().zzm(new Runnable()
    {
      public void run()
      {
        zzaf.zzb(zzaf.this, l);
      }
    });
  }
  
  @WorkerThread
  public void zzbyi()
  {
    zzyl();
    zzbvg().zzbwi().log("Application backgrounded. Logging engagement");
    long l = zzbvh().aqd.get();
    if (l > 0L)
    {
      Bundle localBundle = new Bundle();
      localBundle.putLong("_et", l);
      zzbux().zzf("auto", "_e", localBundle);
      zzbvh().aqd.set(0L);
      return;
    }
    zzbvg().zzbwe().zzj("Not logging non-positive engagement time", Long.valueOf(l));
  }
  
  protected void zzym() {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzaf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */