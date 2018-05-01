package com.google.android.gms.internal.measurement;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.measurement.AppMeasurement.Event;
import com.google.android.gms.measurement.AppMeasurement.Param;
import com.google.android.gms.measurement.AppMeasurement.UserProperty;
import java.io.IOException;
import java.util.Map;

public final class zzgf
  extends zzhk
{
  private static int zzald = 65535;
  private static int zzale = 2;
  private final Map<String, Map<String, String>> zzalf = new ArrayMap();
  private final Map<String, Map<String, Boolean>> zzalg = new ArrayMap();
  private final Map<String, Map<String, Boolean>> zzalh = new ArrayMap();
  private final Map<String, zzkf> zzali = new ArrayMap();
  private final Map<String, Map<String, Integer>> zzalj = new ArrayMap();
  private final Map<String, String> zzalk = new ArrayMap();
  
  zzgf(zzgl paramzzgl)
  {
    super(paramzzgl);
  }
  
  private final zzkf zza(String paramString, byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      paramString = new zzkf();
    }
    for (;;)
    {
      return paramString;
      zzaba localzzaba = zzaba.zza(paramArrayOfByte, 0, paramArrayOfByte.length);
      paramArrayOfByte = new zzkf();
      try
      {
        paramArrayOfByte.zzb(localzzaba);
        zzgg().zzir().zze("Parsed config. version, gmp_app_id", paramArrayOfByte.zzask, paramArrayOfByte.zzadh);
        paramString = paramArrayOfByte;
      }
      catch (IOException paramArrayOfByte)
      {
        zzgg().zzin().zze("Unable to merge remote config. appId", zzfg.zzbh(paramString), paramArrayOfByte);
        paramString = new zzkf();
      }
    }
  }
  
  private static Map<String, String> zza(zzkf paramzzkf)
  {
    ArrayMap localArrayMap = new ArrayMap();
    if ((paramzzkf != null) && (paramzzkf.zzasm != null)) {
      for (Object localObject : paramzzkf.zzasm) {
        if (localObject != null) {
          localArrayMap.put(((zzkg)localObject).zznt, ((zzkg)localObject).value);
        }
      }
    }
    return localArrayMap;
  }
  
  private final void zza(String paramString, zzkf paramzzkf)
  {
    ArrayMap localArrayMap1 = new ArrayMap();
    ArrayMap localArrayMap2 = new ArrayMap();
    ArrayMap localArrayMap3 = new ArrayMap();
    if ((paramzzkf != null) && (paramzzkf.zzasn != null))
    {
      zzke[] arrayOfzzke = paramzzkf.zzasn;
      int i = arrayOfzzke.length;
      int j = 0;
      if (j < i)
      {
        zzke localzzke = arrayOfzzke[j];
        if (TextUtils.isEmpty(localzzke.name)) {
          zzgg().zzin().log("EventConfig contained null event name");
        }
        for (;;)
        {
          j++;
          break;
          paramzzkf = AppMeasurement.Event.zzak(localzzke.name);
          if (!TextUtils.isEmpty(paramzzkf)) {
            localzzke.name = paramzzkf;
          }
          localArrayMap1.put(localzzke.name, localzzke.zzash);
          localArrayMap2.put(localzzke.name, localzzke.zzasi);
          if (localzzke.zzasj != null) {
            if ((localzzke.zzasj.intValue() < zzale) || (localzzke.zzasj.intValue() > zzald)) {
              zzgg().zzin().zze("Invalid sampling rate. Event name, sample rate", localzzke.name, localzzke.zzasj);
            } else {
              localArrayMap3.put(localzzke.name, localzzke.zzasj);
            }
          }
        }
      }
    }
    this.zzalg.put(paramString, localArrayMap1);
    this.zzalh.put(paramString, localArrayMap2);
    this.zzalj.put(paramString, localArrayMap3);
  }
  
  private final void zzbo(String paramString)
  {
    zzch();
    zzab();
    Preconditions.checkNotEmpty(paramString);
    Object localObject;
    if (this.zzali.get(paramString) == null)
    {
      localObject = zzga().zzaz(paramString);
      if (localObject != null) {
        break label112;
      }
      this.zzalf.put(paramString, null);
      this.zzalg.put(paramString, null);
      this.zzalh.put(paramString, null);
      this.zzali.put(paramString, null);
      this.zzalk.put(paramString, null);
      this.zzalj.put(paramString, null);
    }
    for (;;)
    {
      return;
      label112:
      localObject = zza(paramString, (byte[])localObject);
      this.zzalf.put(paramString, zza((zzkf)localObject));
      zza(paramString, (zzkf)localObject);
      this.zzali.put(paramString, localObject);
      this.zzalk.put(paramString, null);
    }
  }
  
  protected final boolean zza(String paramString1, byte[] paramArrayOfByte, String paramString2)
  {
    zzch();
    zzab();
    Preconditions.checkNotEmpty(paramString1);
    Object localObject1 = zza(paramString1, paramArrayOfByte);
    boolean bool;
    if (localObject1 == null) {
      bool = false;
    }
    for (;;)
    {
      return bool;
      zza(paramString1, (zzkf)localObject1);
      this.zzali.put(paramString1, localObject1);
      this.zzalk.put(paramString1, paramString2);
      this.zzalf.put(paramString1, zza((zzkf)localObject1));
      paramString2 = zzft();
      zzjy[] arrayOfzzjy = ((zzkf)localObject1).zzaso;
      Preconditions.checkNotNull(arrayOfzzjy);
      int i = arrayOfzzjy.length;
      for (int j = 0; j < i; j++)
      {
        zzjy localzzjy = arrayOfzzjy[j];
        zzkc[] arrayOfzzkc;
        for (arrayOfzzkc : localzzjy.zzari)
        {
          Object localObject3 = AppMeasurement.Event.zzak(arrayOfzzkc.zzarl);
          if (localObject3 != null) {
            arrayOfzzkc.zzarl = ((String)localObject3);
          }
          for (arrayOfzzkc : arrayOfzzkc.zzarm)
          {
            String str = AppMeasurement.Param.zzak(arrayOfzzkc.zzart);
            if (str != null) {
              arrayOfzzkc.zzart = str;
            }
          }
        }
        for (localzzjy : localzzjy.zzarh)
        {
          ??? = AppMeasurement.UserProperty.zzak(localzzjy.zzasa);
          if (??? != null) {
            localzzjy.zzasa = ((String)???);
          }
        }
      }
      paramString2.zzga().zza(paramString1, arrayOfzzjy);
      try
      {
        ((zzkf)localObject1).zzaso = null;
        paramString2 = new byte[((zzabj)localObject1).zzwg()];
        ((zzabj)localObject1).zza(zzabb.zzb(paramString2, 0, paramString2.length));
        paramArrayOfByte = paramString2;
        paramString2 = zzga();
        Preconditions.checkNotEmpty(paramString1);
        paramString2.zzab();
        paramString2.zzch();
        localObject1 = new ContentValues();
        ((ContentValues)localObject1).put("remote_config", paramArrayOfByte);
      }
      catch (IOException paramString2)
      {
        try
        {
          if (paramString2.getWritableDatabase().update("apps", (ContentValues)localObject1, "app_id = ?", new String[] { paramString1 }) == 0L) {
            paramString2.zzgg().zzil().zzg("Failed to update remote config (got 0). appId", zzfg.zzbh(paramString1));
          }
          bool = true;
          continue;
          paramString2 = paramString2;
          zzgg().zzin().zze("Unable to serialize reduced-size config. Storing full config instead. appId", zzfg.zzbh(paramString1), paramString2);
        }
        catch (SQLiteException paramArrayOfByte)
        {
          for (;;)
          {
            paramString2.zzgg().zzil().zze("Error storing remote config. appId", zzfg.zzbh(paramString1), paramArrayOfByte);
          }
        }
      }
    }
  }
  
  protected final zzkf zzbp(String paramString)
  {
    zzch();
    zzab();
    Preconditions.checkNotEmpty(paramString);
    zzbo(paramString);
    return (zzkf)this.zzali.get(paramString);
  }
  
  protected final String zzbq(String paramString)
  {
    zzab();
    return (String)this.zzalk.get(paramString);
  }
  
  protected final void zzbr(String paramString)
  {
    zzab();
    this.zzalk.put(paramString, null);
  }
  
  protected final boolean zzhh()
  {
    return false;
  }
  
  final String zzm(String paramString1, String paramString2)
  {
    zzab();
    zzbo(paramString1);
    paramString1 = (Map)this.zzalf.get(paramString1);
    if (paramString1 != null) {}
    for (paramString1 = (String)paramString1.get(paramString2);; paramString1 = null) {
      return paramString1;
    }
  }
  
  final boolean zzn(String paramString1, String paramString2)
  {
    boolean bool = true;
    zzab();
    zzbo(paramString1);
    if ((zzgc().zzce(paramString1)) && (zzjv.zzcb(paramString2))) {}
    for (;;)
    {
      return bool;
      if ((!zzgc().zzcf(paramString1)) || (!zzjv.zzbv(paramString2)))
      {
        paramString1 = (Map)this.zzalg.get(paramString1);
        if (paramString1 != null)
        {
          paramString1 = (Boolean)paramString1.get(paramString2);
          if (paramString1 == null) {
            bool = false;
          } else {
            bool = paramString1.booleanValue();
          }
        }
        else
        {
          bool = false;
        }
      }
    }
  }
  
  final boolean zzo(String paramString1, String paramString2)
  {
    zzab();
    zzbo(paramString1);
    boolean bool;
    if ("ecommerce_purchase".equals(paramString2)) {
      bool = true;
    }
    for (;;)
    {
      return bool;
      paramString1 = (Map)this.zzalh.get(paramString1);
      if (paramString1 != null)
      {
        paramString1 = (Boolean)paramString1.get(paramString2);
        if (paramString1 == null) {
          bool = false;
        } else {
          bool = paramString1.booleanValue();
        }
      }
      else
      {
        bool = false;
      }
    }
  }
  
  final int zzp(String paramString1, String paramString2)
  {
    zzab();
    zzbo(paramString1);
    paramString1 = (Map)this.zzalj.get(paramString1);
    int i;
    if (paramString1 != null)
    {
      paramString1 = (Integer)paramString1.get(paramString2);
      if (paramString1 == null) {
        i = 1;
      }
    }
    for (;;)
    {
      return i;
      i = paramString1.intValue();
      continue;
      i = 1;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzgf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */