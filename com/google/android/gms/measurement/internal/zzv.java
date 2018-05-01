package com.google.android.gms.measurement.internal;

import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import com.google.android.gms.internal.zzars;
import com.google.android.gms.internal.zzart;
import com.google.android.gms.internal.zzwb.zza;
import com.google.android.gms.internal.zzwb.zzb;
import com.google.android.gms.internal.zzwb.zzc;
import com.google.android.gms.measurement.AppMeasurement.zza;
import java.io.IOException;
import java.util.Map;

public class zzv
  extends zzaa
{
  private final Map<String, Map<String, String>> atB = new ArrayMap();
  private final Map<String, Map<String, Boolean>> atC = new ArrayMap();
  private final Map<String, Map<String, Boolean>> atD = new ArrayMap();
  private final Map<String, zzwb.zzb> atE = new ArrayMap();
  private final Map<String, String> atF = new ArrayMap();
  
  zzv(zzx paramzzx)
  {
    super(paramzzx);
  }
  
  private Map<String, String> zza(zzwb.zzb paramzzb)
  {
    ArrayMap localArrayMap = new ArrayMap();
    if ((paramzzb != null) && (paramzzb.awE != null))
    {
      paramzzb = paramzzb.awE;
      int j = paramzzb.length;
      int i = 0;
      while (i < j)
      {
        Object localObject = paramzzb[i];
        if (localObject != null) {
          localArrayMap.put(((zzwb.zzc)localObject).zzcb, ((zzwb.zzc)localObject).value);
        }
        i += 1;
      }
    }
    return localArrayMap;
  }
  
  private void zza(String paramString, zzwb.zzb paramzzb)
  {
    ArrayMap localArrayMap1 = new ArrayMap();
    ArrayMap localArrayMap2 = new ArrayMap();
    if ((paramzzb != null) && (paramzzb.awF != null))
    {
      paramzzb = paramzzb.awF;
      int j = paramzzb.length;
      int i = 0;
      while (i < j)
      {
        Object localObject = paramzzb[i];
        if (localObject != null)
        {
          String str = (String)AppMeasurement.zza.aqx.get(((zzwb.zza)localObject).name);
          if (str != null) {
            ((zzwb.zza)localObject).name = str;
          }
          localArrayMap1.put(((zzwb.zza)localObject).name, ((zzwb.zza)localObject).awA);
          localArrayMap2.put(((zzwb.zza)localObject).name, ((zzwb.zza)localObject).awB);
        }
        i += 1;
      }
    }
    this.atC.put(paramString, localArrayMap1);
    this.atD.put(paramString, localArrayMap2);
  }
  
  @WorkerThread
  private zzwb.zzb zze(String paramString, byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      return new zzwb.zzb();
    }
    Object localObject = zzars.zzbd(paramArrayOfByte);
    paramArrayOfByte = new zzwb.zzb();
    try
    {
      localObject = (zzwb.zzb)paramArrayOfByte.zzb((zzars)localObject);
      zzbwb().zzbxe().zze("Parsed config. version, gmp_app_id", paramArrayOfByte.awC, paramArrayOfByte.aqZ);
      return paramArrayOfByte;
    }
    catch (IOException paramArrayOfByte)
    {
      zzbwb().zzbxa().zze("Unable to merge remote config", paramString, paramArrayOfByte);
    }
    return null;
  }
  
  @WorkerThread
  private void zzmn(String paramString)
  {
    zzacj();
    zzzx();
    com.google.android.gms.common.internal.zzaa.zzib(paramString);
    if (!this.atE.containsKey(paramString))
    {
      localObject = zzbvw().zzmb(paramString);
      if (localObject == null)
      {
        this.atB.put(paramString, null);
        this.atC.put(paramString, null);
        this.atD.put(paramString, null);
        this.atE.put(paramString, null);
        this.atF.put(paramString, null);
      }
    }
    else
    {
      return;
    }
    Object localObject = zze(paramString, (byte[])localObject);
    this.atB.put(paramString, zza((zzwb.zzb)localObject));
    zza(paramString, (zzwb.zzb)localObject);
    this.atE.put(paramString, localObject);
    this.atF.put(paramString, null);
  }
  
  @WorkerThread
  String zzaw(String paramString1, String paramString2)
  {
    zzzx();
    zzmn(paramString1);
    paramString1 = (Map)this.atB.get(paramString1);
    if (paramString1 != null) {
      return (String)paramString1.get(paramString2);
    }
    return null;
  }
  
  @WorkerThread
  boolean zzax(String paramString1, String paramString2)
  {
    zzzx();
    zzmn(paramString1);
    if ((zzbvx().zznh(paramString1)) && (zzal.zzne(paramString2))) {}
    while ((zzbvx().zzni(paramString1)) && (zzal.zzmu(paramString2))) {
      return true;
    }
    paramString1 = (Map)this.atC.get(paramString1);
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
    zzzx();
    zzmn(paramString1);
    paramString1 = (Map)this.atD.get(paramString1);
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
    zzacj();
    zzzx();
    com.google.android.gms.common.internal.zzaa.zzib(paramString1);
    zzwb.zzb localzzb = zze(paramString1, paramArrayOfByte);
    if (localzzb == null) {
      return false;
    }
    zza(paramString1, localzzb);
    this.atE.put(paramString1, localzzb);
    this.atF.put(paramString1, paramString2);
    this.atB.put(paramString1, zza(localzzb));
    zzbvp().zza(paramString1, localzzb.awG);
    try
    {
      localzzb.awG = null;
      paramString2 = new byte[localzzb.cz()];
      localzzb.zza(zzart.zzbe(paramString2));
      paramArrayOfByte = paramString2;
    }
    catch (IOException paramString2)
    {
      for (;;)
      {
        zzbwb().zzbxa().zzj("Unable to serialize reduced-size config.  Storing full config instead.", paramString2);
      }
    }
    zzbvw().zzd(paramString1, paramArrayOfByte);
    return true;
  }
  
  @WorkerThread
  protected zzwb.zzb zzmo(String paramString)
  {
    zzacj();
    zzzx();
    com.google.android.gms.common.internal.zzaa.zzib(paramString);
    zzmn(paramString);
    return (zzwb.zzb)this.atE.get(paramString);
  }
  
  @WorkerThread
  protected String zzmp(String paramString)
  {
    zzzx();
    return (String)this.atF.get(paramString);
  }
  
  @WorkerThread
  protected void zzmq(String paramString)
  {
    zzzx();
    this.atF.put(paramString, null);
  }
  
  protected void zzzy() {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */