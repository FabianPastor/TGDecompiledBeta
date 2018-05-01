package com.google.android.gms.internal;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.measurement.AppMeasurement.Event;
import com.google.android.gms.measurement.AppMeasurement.Param;
import com.google.android.gms.measurement.AppMeasurement.UserProperty;
import java.io.IOException;
import java.util.Map;

public final class zzcig
  extends zzcjl
{
  private static int zzjdx = 65535;
  private static int zzjdy = 2;
  private final Map<String, Map<String, String>> zzjdz = new ArrayMap();
  private final Map<String, Map<String, Boolean>> zzjea = new ArrayMap();
  private final Map<String, Map<String, Boolean>> zzjeb = new ArrayMap();
  private final Map<String, zzcly> zzjec = new ArrayMap();
  private final Map<String, Map<String, Integer>> zzjed = new ArrayMap();
  private final Map<String, String> zzjee = new ArrayMap();
  
  zzcig(zzcim paramzzcim)
  {
    super(paramzzcim);
  }
  
  private static Map<String, String> zza(zzcly paramzzcly)
  {
    ArrayMap localArrayMap = new ArrayMap();
    if ((paramzzcly != null) && (paramzzcly.zzjky != null))
    {
      paramzzcly = paramzzcly.zzjky;
      int j = paramzzcly.length;
      int i = 0;
      while (i < j)
      {
        Object localObject = paramzzcly[i];
        if (localObject != null) {
          localArrayMap.put(((zzclz)localObject).key, ((zzclz)localObject).value);
        }
        i += 1;
      }
    }
    return localArrayMap;
  }
  
  private final void zza(String paramString, zzcly paramzzcly)
  {
    ArrayMap localArrayMap1 = new ArrayMap();
    ArrayMap localArrayMap2 = new ArrayMap();
    ArrayMap localArrayMap3 = new ArrayMap();
    if ((paramzzcly != null) && (paramzzcly.zzjkz != null))
    {
      paramzzcly = paramzzcly.zzjkz;
      int j = paramzzcly.length;
      int i = 0;
      if (i < j)
      {
        Object localObject = paramzzcly[i];
        if (TextUtils.isEmpty(((zzclx)localObject).name)) {
          zzawy().zzazf().log("EventConfig contained null event name");
        }
        for (;;)
        {
          i += 1;
          break;
          String str = AppMeasurement.Event.zziq(((zzclx)localObject).name);
          if (!TextUtils.isEmpty(str)) {
            ((zzclx)localObject).name = str;
          }
          localArrayMap1.put(((zzclx)localObject).name, ((zzclx)localObject).zzjkt);
          localArrayMap2.put(((zzclx)localObject).name, ((zzclx)localObject).zzjku);
          if (((zzclx)localObject).zzjkv != null) {
            if ((((zzclx)localObject).zzjkv.intValue() < zzjdy) || (((zzclx)localObject).zzjkv.intValue() > zzjdx)) {
              zzawy().zzazf().zze("Invalid sampling rate. Event name, sample rate", ((zzclx)localObject).name, ((zzclx)localObject).zzjkv);
            } else {
              localArrayMap3.put(((zzclx)localObject).name, ((zzclx)localObject).zzjkv);
            }
          }
        }
      }
    }
    this.zzjea.put(paramString, localArrayMap1);
    this.zzjeb.put(paramString, localArrayMap2);
    this.zzjed.put(paramString, localArrayMap3);
  }
  
  private final zzcly zzc(String paramString, byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      return new zzcly();
    }
    paramArrayOfByte = zzfjj.zzn(paramArrayOfByte, 0, paramArrayOfByte.length);
    zzcly localzzcly = new zzcly();
    try
    {
      localzzcly.zza(paramArrayOfByte);
      zzawy().zzazj().zze("Parsed config. version, gmp_app_id", localzzcly.zzjkw, localzzcly.zzixs);
      return localzzcly;
    }
    catch (IOException paramArrayOfByte)
    {
      zzawy().zzazf().zze("Unable to merge remote config. appId", zzchm.zzjk(paramString), paramArrayOfByte);
    }
    return new zzcly();
  }
  
  private final void zzjr(String paramString)
  {
    zzxf();
    zzve();
    zzbq.zzgm(paramString);
    if (this.zzjec.get(paramString) == null)
    {
      localObject = zzaws().zzjd(paramString);
      if (localObject == null)
      {
        this.zzjdz.put(paramString, null);
        this.zzjea.put(paramString, null);
        this.zzjeb.put(paramString, null);
        this.zzjec.put(paramString, null);
        this.zzjee.put(paramString, null);
        this.zzjed.put(paramString, null);
      }
    }
    else
    {
      return;
    }
    Object localObject = zzc(paramString, (byte[])localObject);
    this.zzjdz.put(paramString, zza((zzcly)localObject));
    zza(paramString, (zzcly)localObject);
    this.zzjec.put(paramString, localObject);
    this.zzjee.put(paramString, null);
  }
  
  final String zzam(String paramString1, String paramString2)
  {
    zzve();
    zzjr(paramString1);
    paramString1 = (Map)this.zzjdz.get(paramString1);
    if (paramString1 != null) {
      return (String)paramString1.get(paramString2);
    }
    return null;
  }
  
  final boolean zzan(String paramString1, String paramString2)
  {
    zzve();
    zzjr(paramString1);
    if ((zzawu().zzkl(paramString1)) && (zzclq.zzki(paramString2))) {}
    while ((zzawu().zzkm(paramString1)) && (zzclq.zzjz(paramString2))) {
      return true;
    }
    paramString1 = (Map)this.zzjea.get(paramString1);
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
  
  final boolean zzao(String paramString1, String paramString2)
  {
    zzve();
    zzjr(paramString1);
    if ("ecommerce_purchase".equals(paramString2)) {
      return true;
    }
    paramString1 = (Map)this.zzjeb.get(paramString1);
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
  
  final int zzap(String paramString1, String paramString2)
  {
    zzve();
    zzjr(paramString1);
    paramString1 = (Map)this.zzjed.get(paramString1);
    if (paramString1 != null)
    {
      paramString1 = (Integer)paramString1.get(paramString2);
      if (paramString1 == null) {
        return 1;
      }
      return paramString1.intValue();
    }
    return 1;
  }
  
  protected final boolean zzaxz()
  {
    return false;
  }
  
  protected final boolean zzb(String paramString1, byte[] paramArrayOfByte, String paramString2)
  {
    zzxf();
    zzve();
    zzbq.zzgm(paramString1);
    localObject1 = zzc(paramString1, paramArrayOfByte);
    if (localObject1 == null) {
      return false;
    }
    zza(paramString1, (zzcly)localObject1);
    this.zzjec.put(paramString1, localObject1);
    this.zzjee.put(paramString1, paramString2);
    this.zzjdz.put(paramString1, zza((zzcly)localObject1));
    paramString2 = zzawl();
    zzclr[] arrayOfzzclr = ((zzcly)localObject1).zzjla;
    zzbq.checkNotNull(arrayOfzzclr);
    int m = arrayOfzzclr.length;
    int i = 0;
    while (i < m)
    {
      Object localObject2 = arrayOfzzclr[i];
      zzcls[] arrayOfzzcls = ((zzclr)localObject2).zzjju;
      int n = arrayOfzzcls.length;
      int j = 0;
      Object localObject3;
      while (j < n)
      {
        localObject3 = arrayOfzzcls[j];
        String str1 = AppMeasurement.Event.zziq(((zzcls)localObject3).zzjjx);
        if (str1 != null) {
          ((zzcls)localObject3).zzjjx = str1;
        }
        localObject3 = ((zzcls)localObject3).zzjjy;
        int i1 = localObject3.length;
        k = 0;
        while (k < i1)
        {
          str1 = localObject3[k];
          String str2 = AppMeasurement.Param.zziq(str1.zzjkf);
          if (str2 != null) {
            str1.zzjkf = str2;
          }
          k += 1;
        }
        j += 1;
      }
      localObject2 = ((zzclr)localObject2).zzjjt;
      int k = localObject2.length;
      j = 0;
      while (j < k)
      {
        arrayOfzzcls = localObject2[j];
        localObject3 = AppMeasurement.UserProperty.zziq(arrayOfzzcls.zzjkm);
        if (localObject3 != null) {
          arrayOfzzcls.zzjkm = ((String)localObject3);
        }
        j += 1;
      }
      i += 1;
    }
    paramString2.zzaws().zza(paramString1, arrayOfzzclr);
    try
    {
      ((zzcly)localObject1).zzjla = null;
      paramString2 = new byte[((zzfjs)localObject1).zzho()];
      ((zzfjs)localObject1).zza(zzfjk.zzo(paramString2, 0, paramString2.length));
      paramArrayOfByte = paramString2;
    }
    catch (IOException paramString2)
    {
      try
      {
        if (paramString2.getWritableDatabase().update("apps", (ContentValues)localObject1, "app_id = ?", new String[] { paramString1 }) != 0L) {
          break label425;
        }
        paramString2.zzawy().zzazd().zzj("Failed to update remote config (got 0). appId", zzchm.zzjk(paramString1));
        return true;
        paramString2 = paramString2;
        zzawy().zzazf().zze("Unable to serialize reduced-size config. Storing full config instead. appId", zzchm.zzjk(paramString1), paramString2);
      }
      catch (SQLiteException paramArrayOfByte)
      {
        for (;;)
        {
          paramString2.zzawy().zzazd().zze("Error storing remote config. appId", zzchm.zzjk(paramString1), paramArrayOfByte);
        }
      }
    }
    paramString2 = zzaws();
    zzbq.zzgm(paramString1);
    paramString2.zzve();
    paramString2.zzxf();
    localObject1 = new ContentValues();
    ((ContentValues)localObject1).put("remote_config", paramArrayOfByte);
  }
  
  protected final zzcly zzjs(String paramString)
  {
    zzxf();
    zzve();
    zzbq.zzgm(paramString);
    zzjr(paramString);
    return (zzcly)this.zzjec.get(paramString);
  }
  
  protected final String zzjt(String paramString)
  {
    zzve();
    return (String)this.zzjee.get(paramString);
  }
  
  protected final void zzju(String paramString)
  {
    zzve();
    this.zzjee.put(paramString, null);
  }
  
  final void zzjv(String paramString)
  {
    zzve();
    this.zzjec.remove(paramString);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */