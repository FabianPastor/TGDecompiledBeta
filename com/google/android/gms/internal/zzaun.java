package com.google.android.gms.internal;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;
import com.google.android.gms.common.util.zze;

public class zzaun
  extends zzauh
{
  private Handler mHandler;
  protected long zzbvZ;
  private final zzatk zzbwa = new zzatk(this.zzbqb)
  {
    @WorkerThread
    public void run()
    {
      zzaun.this.zzNh();
    }
  };
  private final zzatk zzbwb = new zzatk(this.zzbqb)
  {
    @WorkerThread
    public void run()
    {
      zzaun.zza(zzaun.this);
    }
  };
  
  zzaun(zzaue paramzzaue)
  {
    super(paramzzaue);
  }
  
  private void zzNf()
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
  private void zzNi()
  {
    zzmR();
    zzaN(false);
    zzJY().zzW(zznR().elapsedRealtime());
  }
  
  @WorkerThread
  private void zzat(long paramLong)
  {
    zzmR();
    zzNf();
    this.zzbwa.cancel();
    this.zzbwb.cancel();
    zzKl().zzMf().zzj("Activity resumed, time", Long.valueOf(paramLong));
    this.zzbvZ = paramLong;
    if (zznR().currentTimeMillis() - zzKm().zzbto.get() > zzKm().zzbtq.get())
    {
      zzKm().zzbtp.set(true);
      zzKm().zzbtr.set(0L);
    }
    if (zzKm().zzbtp.get())
    {
      this.zzbwa.zzy(Math.max(0L, zzKm().zzbtn.get() - zzKm().zzbtr.get()));
      return;
    }
    this.zzbwb.zzy(Math.max(0L, 3600000L - zzKm().zzbtr.get()));
  }
  
  @WorkerThread
  private void zzau(long paramLong)
  {
    zzmR();
    zzNf();
    this.zzbwa.cancel();
    this.zzbwb.cancel();
    zzKl().zzMf().zzj("Activity paused, time", Long.valueOf(paramLong));
    if (this.zzbvZ != 0L) {
      zzKm().zzbtr.set(zzKm().zzbtr.get() + (paramLong - this.zzbvZ));
    }
    zzKm().zzbtq.set(zznR().currentTimeMillis());
  }
  
  @MainThread
  protected void zzNe()
  {
    final long l = zznR().elapsedRealtime();
    zzKk().zzm(new Runnable()
    {
      public void run()
      {
        zzaun.zza(zzaun.this, l);
      }
    });
  }
  
  @MainThread
  protected void zzNg()
  {
    final long l = zznR().elapsedRealtime();
    zzKk().zzm(new Runnable()
    {
      public void run()
      {
        zzaun.zzb(zzaun.this, l);
      }
    });
  }
  
  @WorkerThread
  protected void zzNh()
  {
    zzmR();
    long l = zznR().elapsedRealtime();
    zzKl().zzMf().zzj("Session started, time", Long.valueOf(l));
    zzKm().zzbtp.set(false);
    zzKa().zze("auto", "_s", new Bundle());
  }
  
  @WorkerThread
  public boolean zzaN(boolean paramBoolean)
  {
    zzmR();
    zzob();
    long l1 = zznR().elapsedRealtime();
    if (this.zzbvZ == 0L) {
      this.zzbvZ = (l1 - 3600000L);
    }
    long l2 = l1 - this.zzbvZ;
    if ((!paramBoolean) && (l2 < 1000L))
    {
      zzKl().zzMf().zzj("Screen exposed for less than 1000 ms. Event not sent. time", Long.valueOf(l2));
      return false;
    }
    zzKm().zzbtr.set(l2);
    zzKl().zzMf().zzj("Recording user engagement, ms", Long.valueOf(l2));
    Bundle localBundle = new Bundle();
    localBundle.putLong("_et", l2);
    zzauk.zza(zzKe().zzMW(), localBundle);
    zzKa().zze("auto", "_e", localBundle);
    this.zzbvZ = l1;
    this.zzbwb.cancel();
    this.zzbwb.zzy(Math.max(0L, 3600000L - zzKm().zzbtr.get()));
    return true;
  }
  
  protected void zzmS() {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaun.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */