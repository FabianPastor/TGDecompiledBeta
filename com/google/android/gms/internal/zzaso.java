package com.google.android.gms.internal;

import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.measurement.AppMeasurement.zzf;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class zzaso
  extends zzatr
{
  private final Map<String, Long> zzbpF = new ArrayMap();
  private final Map<String, Integer> zzbpG = new ArrayMap();
  private long zzbpH;
  
  public zzaso(zzatp paramzzatp)
  {
    super(paramzzatp);
  }
  
  @WorkerThread
  private void zzW(long paramLong)
  {
    Iterator localIterator = this.zzbpF.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      this.zzbpF.put(str, Long.valueOf(paramLong));
    }
    if (!this.zzbpF.isEmpty()) {
      this.zzbpH = paramLong;
    }
  }
  
  @WorkerThread
  private void zza(long paramLong, AppMeasurement.zzf paramzzf)
  {
    if (paramzzf == null)
    {
      zzJt().zzLg().log("Not logging ad exposure. No active activity");
      return;
    }
    if (paramLong < 1000L)
    {
      zzJt().zzLg().zzj("Not logging ad exposure. Less than 1000 ms. exposure", Long.valueOf(paramLong));
      return;
    }
    Bundle localBundle = new Bundle();
    localBundle.putLong("_xt", paramLong);
    zzatv.zza(paramzzf, localBundle);
    zzJi().zze("am", "_xa", localBundle);
  }
  
  @WorkerThread
  private void zza(String paramString, long paramLong, AppMeasurement.zzf paramzzf)
  {
    if (paramzzf == null)
    {
      zzJt().zzLg().log("Not logging ad unit exposure. No active activity");
      return;
    }
    if (paramLong < 1000L)
    {
      zzJt().zzLg().zzj("Not logging ad unit exposure. Less than 1000 ms. exposure", Long.valueOf(paramLong));
      return;
    }
    Bundle localBundle = new Bundle();
    localBundle.putString("_ai", paramString);
    localBundle.putLong("_xt", paramLong);
    zzatv.zza(paramzzf, localBundle);
    zzJi().zze("am", "_xu", localBundle);
  }
  
  @WorkerThread
  private void zzf(String paramString, long paramLong)
  {
    zzJe();
    zzmq();
    zzac.zzdv(paramString);
    if (this.zzbpG.isEmpty()) {
      this.zzbpH = paramLong;
    }
    Integer localInteger = (Integer)this.zzbpG.get(paramString);
    if (localInteger != null)
    {
      this.zzbpG.put(paramString, Integer.valueOf(localInteger.intValue() + 1));
      return;
    }
    if (this.zzbpG.size() >= 100)
    {
      zzJt().zzLc().log("Too many ads visible");
      return;
    }
    this.zzbpG.put(paramString, Integer.valueOf(1));
    this.zzbpF.put(paramString, Long.valueOf(paramLong));
  }
  
  @WorkerThread
  private void zzg(String paramString, long paramLong)
  {
    zzJe();
    zzmq();
    zzac.zzdv(paramString);
    Object localObject = (Integer)this.zzbpG.get(paramString);
    if (localObject != null)
    {
      zzatv.zza localzza = zzJm().zzLU();
      int i = ((Integer)localObject).intValue() - 1;
      if (i == 0)
      {
        this.zzbpG.remove(paramString);
        localObject = (Long)this.zzbpF.get(paramString);
        if (localObject == null) {
          zzJt().zzLa().log("First ad unit exposure time was never set");
        }
        for (;;)
        {
          if (this.zzbpG.isEmpty())
          {
            if (this.zzbpH != 0L) {
              break;
            }
            zzJt().zzLa().log("First ad exposure time was never set");
          }
          return;
          long l = ((Long)localObject).longValue();
          this.zzbpF.remove(paramString);
          zza(paramString, paramLong - l, localzza);
        }
        zza(paramLong - this.zzbpH, localzza);
        this.zzbpH = 0L;
        return;
      }
      this.zzbpG.put(paramString, Integer.valueOf(i));
      return;
    }
    zzJt().zzLa().zzj("Call to endAdUnitExposure for unknown ad unit id", paramString);
  }
  
  public void beginAdUnitExposure(final String paramString)
  {
    if (Build.VERSION.SDK_INT < 14) {
      return;
    }
    if ((paramString == null) || (paramString.length() == 0))
    {
      zzJt().zzLa().log("Ad unit id must be a non-empty string");
      return;
    }
    final long l = zznq().elapsedRealtime();
    zzJs().zzm(new Runnable()
    {
      public void run()
      {
        zzaso.zza(zzaso.this, paramString, l);
      }
    });
  }
  
  public void endAdUnitExposure(final String paramString)
  {
    if (Build.VERSION.SDK_INT < 14) {
      return;
    }
    if ((paramString == null) || (paramString.length() == 0))
    {
      zzJt().zzLa().log("Ad unit id must be a non-empty string");
      return;
    }
    final long l = zznq().elapsedRealtime();
    zzJs().zzm(new Runnable()
    {
      public void run()
      {
        zzaso.zzb(zzaso.this, paramString, l);
      }
    });
  }
  
  public void zzJc()
  {
    final long l = zznq().elapsedRealtime();
    zzJs().zzm(new Runnable()
    {
      public void run()
      {
        zzaso.zza(zzaso.this, l);
      }
    });
  }
  
  @WorkerThread
  public void zzV(long paramLong)
  {
    zzatv.zza localzza = zzJm().zzLU();
    Iterator localIterator = this.zzbpF.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      zza(str, paramLong - ((Long)this.zzbpF.get(str)).longValue(), localzza);
    }
    if (!this.zzbpF.isEmpty()) {
      zza(paramLong - this.zzbpH, localzza);
    }
    zzW(paramLong);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaso.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */