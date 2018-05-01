package com.google.android.gms.internal;

import android.os.Bundle;
import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.measurement.AppMeasurement.zzb;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class zzceb
  extends zzchh
{
  private final Map<String, Long> zzboq = new ArrayMap();
  private final Map<String, Integer> zzbor = new ArrayMap();
  private long zzbos;
  
  public zzceb(zzcgk paramzzcgk)
  {
    super(paramzzcgk);
  }
  
  @WorkerThread
  private final void zzK(long paramLong)
  {
    Iterator localIterator = this.zzboq.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      this.zzboq.put(str, Long.valueOf(paramLong));
    }
    if (!this.zzboq.isEmpty()) {
      this.zzbos = paramLong;
    }
  }
  
  @WorkerThread
  private final void zza(long paramLong, AppMeasurement.zzb paramzzb)
  {
    if (paramzzb == null)
    {
      super.zzwF().zzyD().log("Not logging ad exposure. No active activity");
      return;
    }
    if (paramLong < 1000L)
    {
      super.zzwF().zzyD().zzj("Not logging ad exposure. Less than 1000 ms. exposure", Long.valueOf(paramLong));
      return;
    }
    Bundle localBundle = new Bundle();
    localBundle.putLong("_xt", paramLong);
    zzchy.zza(paramzzb, localBundle);
    super.zzwt().zzd("am", "_xa", localBundle);
  }
  
  @WorkerThread
  private final void zza(String paramString, long paramLong, AppMeasurement.zzb paramzzb)
  {
    if (paramzzb == null)
    {
      super.zzwF().zzyD().log("Not logging ad unit exposure. No active activity");
      return;
    }
    if (paramLong < 1000L)
    {
      super.zzwF().zzyD().zzj("Not logging ad unit exposure. Less than 1000 ms. exposure", Long.valueOf(paramLong));
      return;
    }
    Bundle localBundle = new Bundle();
    localBundle.putString("_ai", paramString);
    localBundle.putLong("_xt", paramLong);
    zzchy.zza(paramzzb, localBundle);
    super.zzwt().zzd("am", "_xu", localBundle);
  }
  
  @WorkerThread
  private final void zzd(String paramString, long paramLong)
  {
    super.zzwp();
    super.zzjC();
    zzbo.zzcF(paramString);
    if (this.zzbor.isEmpty()) {
      this.zzbos = paramLong;
    }
    Integer localInteger = (Integer)this.zzbor.get(paramString);
    if (localInteger != null)
    {
      this.zzbor.put(paramString, Integer.valueOf(localInteger.intValue() + 1));
      return;
    }
    if (this.zzbor.size() >= 100)
    {
      super.zzwF().zzyz().log("Too many ads visible");
      return;
    }
    this.zzbor.put(paramString, Integer.valueOf(1));
    this.zzboq.put(paramString, Long.valueOf(paramLong));
  }
  
  @WorkerThread
  private final void zze(String paramString, long paramLong)
  {
    super.zzwp();
    super.zzjC();
    zzbo.zzcF(paramString);
    Object localObject = (Integer)this.zzbor.get(paramString);
    if (localObject != null)
    {
      zzcib localzzcib = super.zzwx().zzzh();
      int i = ((Integer)localObject).intValue() - 1;
      if (i == 0)
      {
        this.zzbor.remove(paramString);
        localObject = (Long)this.zzboq.get(paramString);
        if (localObject == null) {
          super.zzwF().zzyx().log("First ad unit exposure time was never set");
        }
        for (;;)
        {
          if (this.zzbor.isEmpty())
          {
            if (this.zzbos != 0L) {
              break;
            }
            super.zzwF().zzyx().log("First ad exposure time was never set");
          }
          return;
          long l = ((Long)localObject).longValue();
          this.zzboq.remove(paramString);
          zza(paramString, paramLong - l, localzzcib);
        }
        zza(paramLong - this.zzbos, localzzcib);
        this.zzbos = 0L;
        return;
      }
      this.zzbor.put(paramString, Integer.valueOf(i));
      return;
    }
    super.zzwF().zzyx().zzj("Call to endAdUnitExposure for unknown ad unit id", paramString);
  }
  
  public final void beginAdUnitExposure(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0))
    {
      super.zzwF().zzyx().log("Ad unit id must be a non-empty string");
      return;
    }
    long l = super.zzkq().elapsedRealtime();
    super.zzwE().zzj(new zzcec(this, paramString, l));
  }
  
  public final void endAdUnitExposure(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0))
    {
      super.zzwF().zzyx().log("Ad unit id must be a non-empty string");
      return;
    }
    long l = super.zzkq().elapsedRealtime();
    super.zzwE().zzj(new zzced(this, paramString, l));
  }
  
  @WorkerThread
  public final void zzJ(long paramLong)
  {
    zzcib localzzcib = super.zzwx().zzzh();
    Iterator localIterator = this.zzboq.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      zza(str, paramLong - ((Long)this.zzboq.get(str)).longValue(), localzzcib);
    }
    if (!this.zzboq.isEmpty()) {
      zza(paramLong - this.zzbos, localzzcib);
    }
    zzK(paramLong);
  }
  
  public final void zzwn()
  {
    long l = super.zzkq().elapsedRealtime();
    super.zzwE().zzj(new zzcee(this, l));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzceb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */