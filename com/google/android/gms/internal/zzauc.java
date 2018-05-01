package com.google.android.gms.internal;

import android.support.annotation.WorkerThread;
import android.support.v4.util.ArrayMap;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.measurement.AppMeasurement.zza;
import java.io.IOException;
import java.util.Map;

public class zzauc
  extends zzauh
{
  private final Map<String, Map<String, String>> zzbtF = new ArrayMap();
  private final Map<String, Map<String, Boolean>> zzbtG = new ArrayMap();
  private final Map<String, Map<String, Boolean>> zzbtH = new ArrayMap();
  private final Map<String, zzauv.zzb> zzbtI = new ArrayMap();
  private final Map<String, String> zzbtJ = new ArrayMap();
  
  zzauc(zzaue paramzzaue)
  {
    super(paramzzaue);
  }
  
  private Map<String, String> zza(zzauv.zzb paramzzb)
  {
    ArrayMap localArrayMap = new ArrayMap();
    if ((paramzzb != null) && (paramzzb.zzbwS != null))
    {
      paramzzb = paramzzb.zzbwS;
      int j = paramzzb.length;
      int i = 0;
      while (i < j)
      {
        Object localObject = paramzzb[i];
        if (localObject != null) {
          localArrayMap.put(((zzauv.zzc)localObject).zzaB, ((zzauv.zzc)localObject).value);
        }
        i += 1;
      }
    }
    return localArrayMap;
  }
  
  private void zza(String paramString, zzauv.zzb paramzzb)
  {
    ArrayMap localArrayMap1 = new ArrayMap();
    ArrayMap localArrayMap2 = new ArrayMap();
    if ((paramzzb != null) && (paramzzb.zzbwT != null))
    {
      paramzzb = paramzzb.zzbwT;
      int j = paramzzb.length;
      int i = 0;
      while (i < j)
      {
        Object localObject = paramzzb[i];
        if (localObject != null)
        {
          String str = (String)AppMeasurement.zza.zzbqc.get(((zzauv.zza)localObject).name);
          if (str != null) {
            ((zzauv.zza)localObject).name = str;
          }
          localArrayMap1.put(((zzauv.zza)localObject).name, ((zzauv.zza)localObject).zzbwO);
          localArrayMap2.put(((zzauv.zza)localObject).name, ((zzauv.zza)localObject).zzbwP);
        }
        i += 1;
      }
    }
    this.zzbtG.put(paramString, localArrayMap1);
    this.zzbtH.put(paramString, localArrayMap2);
  }
  
  @WorkerThread
  private zzauv.zzb zze(String paramString, byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      return new zzauv.zzb();
    }
    paramArrayOfByte = zzbyb.zzag(paramArrayOfByte);
    zzauv.zzb localzzb = new zzauv.zzb();
    try
    {
      localzzb.zzb(paramArrayOfByte);
      zzKl().zzMf().zze("Parsed config. version, gmp_app_id", localzzb.zzbwQ, localzzb.zzbqK);
      return localzzb;
    }
    catch (IOException paramArrayOfByte)
    {
      zzKl().zzMb().zze("Unable to merge remote config. appId", zzatx.zzfE(paramString), paramArrayOfByte);
    }
    return null;
  }
  
  @WorkerThread
  private void zzfK(String paramString)
  {
    zzob();
    zzmR();
    zzac.zzdr(paramString);
    if (this.zzbtI.get(paramString) == null)
    {
      localObject = zzKg().zzfw(paramString);
      if (localObject == null)
      {
        this.zzbtF.put(paramString, null);
        this.zzbtG.put(paramString, null);
        this.zzbtH.put(paramString, null);
        this.zzbtI.put(paramString, null);
        this.zzbtJ.put(paramString, null);
      }
    }
    else
    {
      return;
    }
    Object localObject = zze(paramString, (byte[])localObject);
    this.zzbtF.put(paramString, zza((zzauv.zzb)localObject));
    zza(paramString, (zzauv.zzb)localObject);
    this.zzbtI.put(paramString, localObject);
    this.zzbtJ.put(paramString, null);
  }
  
  @WorkerThread
  String zzZ(String paramString1, String paramString2)
  {
    zzmR();
    zzfK(paramString1);
    paramString1 = (Map)this.zzbtF.get(paramString1);
    if (paramString1 != null) {
      return (String)paramString1.get(paramString2);
    }
    return null;
  }
  
  @WorkerThread
  boolean zzaa(String paramString1, String paramString2)
  {
    zzmR();
    zzfK(paramString1);
    if ((zzKh().zzgg(paramString1)) && (zzaut.zzgd(paramString2))) {}
    while ((zzKh().zzgh(paramString1)) && (zzaut.zzfT(paramString2))) {
      return true;
    }
    paramString1 = (Map)this.zzbtG.get(paramString1);
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
  boolean zzab(String paramString1, String paramString2)
  {
    zzmR();
    zzfK(paramString1);
    paramString1 = (Map)this.zzbtH.get(paramString1);
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
    zzob();
    zzmR();
    zzac.zzdr(paramString1);
    zzauv.zzb localzzb = zze(paramString1, paramArrayOfByte);
    if (localzzb == null) {
      return false;
    }
    zza(paramString1, localzzb);
    this.zzbtI.put(paramString1, localzzb);
    this.zzbtJ.put(paramString1, paramString2);
    this.zzbtF.put(paramString1, zza(localzzb));
    zzJZ().zza(paramString1, localzzb.zzbwU);
    try
    {
      localzzb.zzbwU = null;
      paramString2 = new byte[localzzb.zzafB()];
      localzzb.zza(zzbyc.zzah(paramString2));
      paramArrayOfByte = paramString2;
    }
    catch (IOException paramString2)
    {
      for (;;)
      {
        zzKl().zzMb().zze("Unable to serialize reduced-size config. Storing full config instead. appId", zzatx.zzfE(paramString1), paramString2);
      }
    }
    zzKg().zzd(paramString1, paramArrayOfByte);
    return true;
  }
  
  @WorkerThread
  protected zzauv.zzb zzfL(String paramString)
  {
    zzob();
    zzmR();
    zzac.zzdr(paramString);
    zzfK(paramString);
    return (zzauv.zzb)this.zzbtI.get(paramString);
  }
  
  @WorkerThread
  protected String zzfM(String paramString)
  {
    zzmR();
    return (String)this.zzbtJ.get(paramString);
  }
  
  @WorkerThread
  protected void zzfN(String paramString)
  {
    zzmR();
    this.zzbtJ.put(paramString, null);
  }
  
  protected void zzmS() {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzauc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */