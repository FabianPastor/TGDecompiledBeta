package com.google.android.gms.measurement.internal;

import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzarc;
import com.google.android.gms.internal.zzard;
import com.google.android.gms.internal.zzvl.zza;
import com.google.android.gms.internal.zzvl.zzb;
import com.google.android.gms.internal.zzvl.zzc;
import com.google.android.gms.measurement.AppMeasurement.zza;
import java.io.IOException;
import java.util.Map;

public class zzv
  extends zzaa
{
  private final Map<String, Map<String, String>> aqu = new ArrayMap();
  private final Map<String, Map<String, Boolean>> aqv = new ArrayMap();
  private final Map<String, Map<String, Boolean>> aqw = new ArrayMap();
  private final Map<String, zzvl.zzb> aqx = new ArrayMap();
  private final Map<String, String> aqy = new ArrayMap();
  
  zzv(zzx paramzzx)
  {
    super(paramzzx);
  }
  
  private Map<String, String> zza(zzvl.zzb paramzzb)
  {
    ArrayMap localArrayMap = new ArrayMap();
    if ((paramzzb != null) && (paramzzb.atf != null))
    {
      paramzzb = paramzzb.atf;
      int j = paramzzb.length;
      int i = 0;
      while (i < j)
      {
        Object localObject = paramzzb[i];
        if (localObject != null) {
          localArrayMap.put(((zzvl.zzc)localObject).zzcb, ((zzvl.zzc)localObject).value);
        }
        i += 1;
      }
    }
    return localArrayMap;
  }
  
  private void zza(String paramString, zzvl.zzb paramzzb)
  {
    ArrayMap localArrayMap1 = new ArrayMap();
    ArrayMap localArrayMap2 = new ArrayMap();
    if ((paramzzb != null) && (paramzzb.atg != null))
    {
      paramzzb = paramzzb.atg;
      int j = paramzzb.length;
      int i = 0;
      while (i < j)
      {
        Object localObject = paramzzb[i];
        if (localObject != null)
        {
          String str = (String)AppMeasurement.zza.anr.get(((zzvl.zza)localObject).name);
          if (str != null) {
            ((zzvl.zza)localObject).name = str;
          }
          localArrayMap1.put(((zzvl.zza)localObject).name, ((zzvl.zza)localObject).atb);
          localArrayMap2.put(((zzvl.zza)localObject).name, ((zzvl.zza)localObject).atc);
        }
        i += 1;
      }
    }
    this.aqv.put(paramString, localArrayMap1);
    this.aqw.put(paramString, localArrayMap2);
  }
  
  @WorkerThread
  private zzvl.zzb zze(String paramString, byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      return new zzvl.zzb();
    }
    Object localObject = zzarc.zzbd(paramArrayOfByte);
    paramArrayOfByte = new zzvl.zzb();
    try
    {
      localObject = (zzvl.zzb)paramArrayOfByte.zzb((zzarc)localObject);
      zzbvg().zzbwj().zze("Parsed config. version, gmp_app_id", paramArrayOfByte.atd, paramArrayOfByte.anQ);
      return paramArrayOfByte;
    }
    catch (IOException paramArrayOfByte)
    {
      zzbvg().zzbwe().zze("Unable to merge remote config", paramString, paramArrayOfByte);
    }
    return null;
  }
  
  @WorkerThread
  private void zzmo(String paramString)
  {
    zzaax();
    zzyl();
    zzac.zzhz(paramString);
    if (!this.aqx.containsKey(paramString))
    {
      localObject = zzbvb().zzmb(paramString);
      if (localObject == null)
      {
        this.aqu.put(paramString, null);
        this.aqv.put(paramString, null);
        this.aqw.put(paramString, null);
        this.aqx.put(paramString, null);
        this.aqy.put(paramString, null);
      }
    }
    else
    {
      return;
    }
    Object localObject = zze(paramString, (byte[])localObject);
    this.aqu.put(paramString, zza((zzvl.zzb)localObject));
    zza(paramString, (zzvl.zzb)localObject);
    this.aqx.put(paramString, localObject);
    this.aqy.put(paramString, null);
  }
  
  @WorkerThread
  String zzaw(String paramString1, String paramString2)
  {
    zzyl();
    zzmo(paramString1);
    paramString1 = (Map)this.aqu.get(paramString1);
    if (paramString1 != null) {
      return (String)paramString1.get(paramString2);
    }
    return null;
  }
  
  @WorkerThread
  boolean zzax(String paramString1, String paramString2)
  {
    zzyl();
    zzmo(paramString1);
    paramString1 = (Map)this.aqv.get(paramString1);
    if (paramString1 != null)
    {
      paramString1 = (Boolean)paramString1.get(paramString2);
      if (paramString1 == null) {
        return false;
      }
      return paramString1.booleanValue();
    }
    return false;
  }
  
  @WorkerThread
  boolean zzay(String paramString1, String paramString2)
  {
    zzyl();
    zzmo(paramString1);
    paramString1 = (Map)this.aqw.get(paramString1);
    if (paramString1 != null)
    {
      paramString1 = (Boolean)paramString1.get(paramString2);
      if (paramString1 == null) {
        return false;
      }
      return paramString1.booleanValue();
    }
    return false;
  }
  
  @WorkerThread
  protected boolean zzb(String paramString1, byte[] paramArrayOfByte, String paramString2)
  {
    zzaax();
    zzyl();
    zzac.zzhz(paramString1);
    zzvl.zzb localzzb = zze(paramString1, paramArrayOfByte);
    if (localzzb == null) {
      return false;
    }
    zza(paramString1, localzzb);
    this.aqx.put(paramString1, localzzb);
    this.aqy.put(paramString1, paramString2);
    this.aqu.put(paramString1, zza(localzzb));
    zzbuw().zza(paramString1, localzzb.ath);
    try
    {
      localzzb.ath = null;
      paramString2 = new byte[localzzb.db()];
      localzzb.zza(zzard.zzbe(paramString2));
      paramArrayOfByte = paramString2;
    }
    catch (IOException paramString2)
    {
      for (;;)
      {
        zzbvg().zzbwe().zzj("Unable to serialize reduced-size config.  Storing full config instead.", paramString2);
      }
    }
    zzbvb().zzd(paramString1, paramArrayOfByte);
    return true;
  }
  
  @WorkerThread
  protected zzvl.zzb zzmp(String paramString)
  {
    zzaax();
    zzyl();
    zzac.zzhz(paramString);
    zzmo(paramString);
    return (zzvl.zzb)this.aqx.get(paramString);
  }
  
  @WorkerThread
  protected String zzmq(String paramString)
  {
    zzyl();
    return (String)this.aqy.get(paramString);
  }
  
  @WorkerThread
  protected void zzmr(String paramString)
  {
    zzyl();
    this.aqy.put(paramString, null);
  }
  
  protected void zzym() {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */