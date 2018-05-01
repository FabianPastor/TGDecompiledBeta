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

public class zzatb
  extends zzaug
{
  private final Map<String, Long> zzbqk = new ArrayMap();
  private final Map<String, Integer> zzbql = new ArrayMap();
  private long zzbqm;
  
  public zzatb(zzaue paramzzaue)
  {
    super(paramzzaue);
  }
  
  @WorkerThread
  private void zzX(long paramLong)
  {
    Iterator localIterator = this.zzbqk.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      this.zzbqk.put(str, Long.valueOf(paramLong));
    }
    if (!this.zzbqk.isEmpty()) {
      this.zzbqm = paramLong;
    }
  }
  
  @WorkerThread
  private void zza(long paramLong, AppMeasurement.zzf paramzzf)
  {
    if (paramzzf == null)
    {
      zzKl().zzMf().log("Not logging ad exposure. No active activity");
      return;
    }
    if (paramLong < 1000L)
    {
      zzKl().zzMf().zzj("Not logging ad exposure. Less than 1000 ms. exposure", Long.valueOf(paramLong));
      return;
    }
    Bundle localBundle = new Bundle();
    localBundle.putLong("_xt", paramLong);
    zzauk.zza(paramzzf, localBundle);
    zzKa().zze("am", "_xa", localBundle);
  }
  
  @WorkerThread
  private void zza(String paramString, long paramLong, AppMeasurement.zzf paramzzf)
  {
    if (paramzzf == null)
    {
      zzKl().zzMf().log("Not logging ad unit exposure. No active activity");
      return;
    }
    if (paramLong < 1000L)
    {
      zzKl().zzMf().zzj("Not logging ad unit exposure. Less than 1000 ms. exposure", Long.valueOf(paramLong));
      return;
    }
    Bundle localBundle = new Bundle();
    localBundle.putString("_ai", paramString);
    localBundle.putLong("_xt", paramLong);
    zzauk.zza(paramzzf, localBundle);
    zzKa().zze("am", "_xu", localBundle);
  }
  
  @WorkerThread
  private void zzf(String paramString, long paramLong)
  {
    zzJW();
    zzmR();
    zzac.zzdr(paramString);
    if (this.zzbql.isEmpty()) {
      this.zzbqm = paramLong;
    }
    Integer localInteger = (Integer)this.zzbql.get(paramString);
    if (localInteger != null)
    {
      this.zzbql.put(paramString, Integer.valueOf(localInteger.intValue() + 1));
      return;
    }
    if (this.zzbql.size() >= 100)
    {
      zzKl().zzMb().log("Too many ads visible");
      return;
    }
    this.zzbql.put(paramString, Integer.valueOf(1));
    this.zzbqk.put(paramString, Long.valueOf(paramLong));
  }
  
  @WorkerThread
  private void zzg(String paramString, long paramLong)
  {
    zzJW();
    zzmR();
    zzac.zzdr(paramString);
    Object localObject = (Integer)this.zzbql.get(paramString);
    if (localObject != null)
    {
      zzauk.zza localzza = zzKe().zzMW();
      int i = ((Integer)localObject).intValue() - 1;
      if (i == 0)
      {
        this.zzbql.remove(paramString);
        localObject = (Long)this.zzbqk.get(paramString);
        if (localObject == null) {
          zzKl().zzLZ().log("First ad unit exposure time was never set");
        }
        for (;;)
        {
          if (this.zzbql.isEmpty())
          {
            if (this.zzbqm != 0L) {
              break;
            }
            zzKl().zzLZ().log("First ad exposure time was never set");
          }
          return;
          long l = ((Long)localObject).longValue();
          this.zzbqk.remove(paramString);
          zza(paramString, paramLong - l, localzza);
        }
        zza(paramLong - this.zzbqm, localzza);
        this.zzbqm = 0L;
        return;
      }
      this.zzbql.put(paramString, Integer.valueOf(i));
      return;
    }
    zzKl().zzLZ().zzj("Call to endAdUnitExposure for unknown ad unit id", paramString);
  }
  
  public void beginAdUnitExposure(final String paramString)
  {
    int i = Build.VERSION.SDK_INT;
    if ((paramString == null) || (paramString.length() == 0))
    {
      zzKl().zzLZ().log("Ad unit id must be a non-empty string");
      return;
    }
    final long l = zznR().elapsedRealtime();
    zzKk().zzm(new Runnable()
    {
      public void run()
      {
        zzatb.zza(zzatb.this, paramString, l);
      }
    });
  }
  
  public void endAdUnitExposure(final String paramString)
  {
    int i = Build.VERSION.SDK_INT;
    if ((paramString == null) || (paramString.length() == 0))
    {
      zzKl().zzLZ().log("Ad unit id must be a non-empty string");
      return;
    }
    final long l = zznR().elapsedRealtime();
    zzKk().zzm(new Runnable()
    {
      public void run()
      {
        zzatb.zzb(zzatb.this, paramString, l);
      }
    });
  }
  
  public void zzJU()
  {
    final long l = zznR().elapsedRealtime();
    zzKk().zzm(new Runnable()
    {
      public void run()
      {
        zzatb.zza(zzatb.this, l);
      }
    });
  }
  
  @WorkerThread
  public void zzW(long paramLong)
  {
    zzauk.zza localzza = zzKe().zzMW();
    Iterator localIterator = this.zzbqk.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      zza(str, paramLong - ((Long)this.zzbqk.get(str)).longValue(), localzza);
    }
    if (!this.zzbqk.isEmpty()) {
      zza(paramLong - this.zzbqm, localzza);
    }
    zzX(paramLong);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzatb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */