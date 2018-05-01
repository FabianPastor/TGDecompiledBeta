package com.google.android.gms.internal.measurement;

import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.util.Clock;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class zzdx
  extends zzhj
{
  private final Map<String, Long> zzada = new ArrayMap();
  private final Map<String, Integer> zzadb = new ArrayMap();
  private long zzadc;
  
  public zzdx(zzgl paramzzgl)
  {
    super(paramzzgl);
  }
  
  private final void zza(long paramLong, zzig paramzzig)
  {
    if (paramzzig == null) {
      zzgg().zzir().log("Not logging ad exposure. No active activity");
    }
    for (;;)
    {
      return;
      if (paramLong < 1000L)
      {
        zzgg().zzir().zzg("Not logging ad exposure. Less than 1000 ms. exposure", Long.valueOf(paramLong));
      }
      else
      {
        Bundle localBundle = new Bundle();
        localBundle.putLong("_xt", paramLong);
        zzih.zza(paramzzig, localBundle, true);
        zzfu().zza("am", "_xa", localBundle);
      }
    }
  }
  
  private final void zza(String paramString, long paramLong)
  {
    zzab();
    Preconditions.checkNotEmpty(paramString);
    if (this.zzadb.isEmpty()) {
      this.zzadc = paramLong;
    }
    Integer localInteger = (Integer)this.zzadb.get(paramString);
    if (localInteger != null) {
      this.zzadb.put(paramString, Integer.valueOf(localInteger.intValue() + 1));
    }
    for (;;)
    {
      return;
      if (this.zzadb.size() >= 100)
      {
        zzgg().zzin().log("Too many ads visible");
      }
      else
      {
        this.zzadb.put(paramString, Integer.valueOf(1));
        this.zzada.put(paramString, Long.valueOf(paramLong));
      }
    }
  }
  
  private final void zza(String paramString, long paramLong, zzig paramzzig)
  {
    if (paramzzig == null) {
      zzgg().zzir().log("Not logging ad unit exposure. No active activity");
    }
    for (;;)
    {
      return;
      if (paramLong < 1000L)
      {
        zzgg().zzir().zzg("Not logging ad unit exposure. Less than 1000 ms. exposure", Long.valueOf(paramLong));
      }
      else
      {
        Bundle localBundle = new Bundle();
        localBundle.putString("_ai", paramString);
        localBundle.putLong("_xt", paramLong);
        zzih.zza(paramzzig, localBundle, true);
        zzfu().zza("am", "_xu", localBundle);
      }
    }
  }
  
  private final void zzb(String paramString, long paramLong)
  {
    zzab();
    Preconditions.checkNotEmpty(paramString);
    Object localObject = (Integer)this.zzadb.get(paramString);
    zzik localzzik;
    int i;
    if (localObject != null)
    {
      localzzik = zzfy().zzkk();
      i = ((Integer)localObject).intValue() - 1;
      if (i == 0)
      {
        this.zzadb.remove(paramString);
        localObject = (Long)this.zzada.get(paramString);
        if (localObject == null)
        {
          zzgg().zzil().log("First ad unit exposure time was never set");
          if (this.zzadb.isEmpty())
          {
            if (this.zzadc != 0L) {
              break label161;
            }
            zzgg().zzil().log("First ad exposure time was never set");
          }
        }
      }
    }
    for (;;)
    {
      return;
      long l = ((Long)localObject).longValue();
      this.zzada.remove(paramString);
      zza(paramString, paramLong - l, localzzik);
      break;
      label161:
      zza(paramLong - this.zzadc, localzzik);
      this.zzadc = 0L;
      continue;
      this.zzadb.put(paramString, Integer.valueOf(i));
      continue;
      zzgg().zzil().zzg("Call to endAdUnitExposure for unknown ad unit id", paramString);
    }
  }
  
  private final void zzl(long paramLong)
  {
    Iterator localIterator = this.zzada.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      this.zzada.put(str, Long.valueOf(paramLong));
    }
    if (!this.zzada.isEmpty()) {
      this.zzadc = paramLong;
    }
  }
  
  public final void beginAdUnitExposure(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      zzgg().zzil().log("Ad unit id must be a non-empty string");
    }
    for (;;)
    {
      return;
      long l = zzbt().elapsedRealtime();
      zzgf().zzc(new zzdy(this, paramString, l));
    }
  }
  
  public final void endAdUnitExposure(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      zzgg().zzil().log("Ad unit id must be a non-empty string");
    }
    for (;;)
    {
      return;
      long l = zzbt().elapsedRealtime();
      zzgf().zzc(new zzdz(this, paramString, l));
    }
  }
  
  public final void zzk(long paramLong)
  {
    zzik localzzik = zzfy().zzkk();
    Iterator localIterator = this.zzada.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      zza(str, paramLong - ((Long)this.zzada.get(str)).longValue(), localzzik);
    }
    if (!this.zzada.isEmpty()) {
      zza(paramLong - this.zzadc, localzzik);
    }
    zzl(paramLong);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzdx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */