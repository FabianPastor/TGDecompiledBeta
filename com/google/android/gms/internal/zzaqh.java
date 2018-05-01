package com.google.android.gms.internal;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class zzaqh
  implements zzapl
{
  private final zzaps bod;
  private final boolean bpS;
  
  public zzaqh(zzaps paramzzaps, boolean paramBoolean)
  {
    this.bod = paramzzaps;
    this.bpS = paramBoolean;
  }
  
  private zzapk<?> zza(zzaos paramzzaos, Type paramType)
  {
    if ((paramType == Boolean.TYPE) || (paramType == Boolean.class)) {
      return zzaqn.bqo;
    }
    return paramzzaos.zza(zzaqo.zzl(paramType));
  }
  
  public <T> zzapk<T> zza(zzaos paramzzaos, zzaqo<T> paramzzaqo)
  {
    Object localObject = paramzzaqo.bC();
    if (!Map.class.isAssignableFrom(paramzzaqo.bB())) {
      return null;
    }
    localObject = zzapr.zzb((Type)localObject, zzapr.zzf((Type)localObject));
    zzapk localzzapk1 = zza(paramzzaos, localObject[0]);
    zzapk localzzapk2 = paramzzaos.zza(zzaqo.zzl(localObject[1]));
    paramzzaqo = this.bod.zzb(paramzzaqo);
    return new zza(paramzzaos, localObject[0], localzzapk1, localObject[1], localzzapk2, paramzzaqo);
  }
  
  private final class zza<K, V>
    extends zzapk<Map<K, V>>
  {
    private final zzapx<? extends Map<K, V>> bpK;
    private final zzapk<K> bpT;
    private final zzapk<V> bpU;
    
    public zza(Type paramType1, zzapk<K> paramzzapk, Type paramType2, zzapk<V> paramzzapk1, zzapx<? extends Map<K, V>> paramzzapx)
    {
      this.bpT = new zzaqm(paramType1, paramType2, paramzzapk);
      this.bpU = new zzaqm(paramType1, paramzzapx, paramzzapk1);
      zzapx localzzapx;
      this.bpK = localzzapx;
    }
    
    private String zze(zzaoy paramzzaoy)
    {
      if (paramzzaoy.aX())
      {
        paramzzaoy = paramzzaoy.bb();
        if (paramzzaoy.be()) {
          return String.valueOf(paramzzaoy.aT());
        }
        if (paramzzaoy.bd()) {
          return Boolean.toString(paramzzaoy.getAsBoolean());
        }
        if (paramzzaoy.bf()) {
          return paramzzaoy.aU();
        }
        throw new AssertionError();
      }
      if (paramzzaoy.aY()) {
        return "null";
      }
      throw new AssertionError();
    }
    
    public void zza(zzaqr paramzzaqr, Map<K, V> paramMap)
      throws IOException
    {
      int m = 0;
      int k = 0;
      if (paramMap == null)
      {
        paramzzaqr.bA();
        return;
      }
      if (!zzaqh.zza(zzaqh.this))
      {
        paramzzaqr.by();
        paramMap = paramMap.entrySet().iterator();
        while (paramMap.hasNext())
        {
          localObject = (Map.Entry)paramMap.next();
          paramzzaqr.zzus(String.valueOf(((Map.Entry)localObject).getKey()));
          this.bpU.zza(paramzzaqr, ((Map.Entry)localObject).getValue());
        }
        paramzzaqr.bz();
        return;
      }
      Object localObject = new ArrayList(paramMap.size());
      ArrayList localArrayList = new ArrayList(paramMap.size());
      paramMap = paramMap.entrySet().iterator();
      int i = 0;
      if (paramMap.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)paramMap.next();
        zzaoy localzzaoy = this.bpT.zzcn(localEntry.getKey());
        ((List)localObject).add(localzzaoy);
        localArrayList.add(localEntry.getValue());
        if ((localzzaoy.aV()) || (localzzaoy.aW())) {}
        for (int j = 1;; j = 0)
        {
          i = j | i;
          break;
        }
      }
      if (i != 0)
      {
        paramzzaqr.bw();
        i = k;
        while (i < ((List)localObject).size())
        {
          paramzzaqr.bw();
          zzapz.zzb((zzaoy)((List)localObject).get(i), paramzzaqr);
          this.bpU.zza(paramzzaqr, localArrayList.get(i));
          paramzzaqr.bx();
          i += 1;
        }
        paramzzaqr.bx();
        return;
      }
      paramzzaqr.by();
      i = m;
      while (i < ((List)localObject).size())
      {
        paramzzaqr.zzus(zze((zzaoy)((List)localObject).get(i)));
        this.bpU.zza(paramzzaqr, localArrayList.get(i));
        i += 1;
      }
      paramzzaqr.bz();
    }
    
    public Map<K, V> zzl(zzaqp paramzzaqp)
      throws IOException
    {
      Object localObject = paramzzaqp.bq();
      if (localObject == zzaqq.brJ)
      {
        paramzzaqp.nextNull();
        return null;
      }
      Map localMap = (Map)this.bpK.bj();
      if (localObject == zzaqq.brB)
      {
        paramzzaqp.beginArray();
        while (paramzzaqp.hasNext())
        {
          paramzzaqp.beginArray();
          localObject = this.bpT.zzb(paramzzaqp);
          if (localMap.put(localObject, this.bpU.zzb(paramzzaqp)) != null)
          {
            paramzzaqp = String.valueOf(localObject);
            throw new zzaph(String.valueOf(paramzzaqp).length() + 15 + "duplicate key: " + paramzzaqp);
          }
          paramzzaqp.endArray();
        }
        paramzzaqp.endArray();
        return localMap;
      }
      paramzzaqp.beginObject();
      while (paramzzaqp.hasNext())
      {
        zzapu.bph.zzi(paramzzaqp);
        localObject = this.bpT.zzb(paramzzaqp);
        if (localMap.put(localObject, this.bpU.zzb(paramzzaqp)) != null)
        {
          paramzzaqp = String.valueOf(localObject);
          throw new zzaph(String.valueOf(paramzzaqp).length() + 15 + "duplicate key: " + paramzzaqp);
        }
      }
      paramzzaqp.endObject();
      return localMap;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaqh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */