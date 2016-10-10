package com.google.android.gms.internal;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class zzapq
  implements zzaou
{
  private final zzapb bkM;
  private final boolean bmB;
  
  public zzapq(zzapb paramzzapb, boolean paramBoolean)
  {
    this.bkM = paramzzapb;
    this.bmB = paramBoolean;
  }
  
  private zzaot<?> zza(zzaob paramzzaob, Type paramType)
  {
    if ((paramType == Boolean.TYPE) || (paramType == Boolean.class)) {
      return zzapw.bmX;
    }
    return paramzzaob.zza(zzapx.zzl(paramType));
  }
  
  public <T> zzaot<T> zza(zzaob paramzzaob, zzapx<T> paramzzapx)
  {
    Object localObject = paramzzapx.bz();
    if (!Map.class.isAssignableFrom(paramzzapx.by())) {
      return null;
    }
    localObject = zzapa.zzb((Type)localObject, zzapa.zzf((Type)localObject));
    zzaot localzzaot1 = zza(paramzzaob, localObject[0]);
    zzaot localzzaot2 = paramzzaob.zza(zzapx.zzl(localObject[1]));
    paramzzapx = this.bkM.zzb(paramzzapx);
    return new zza(paramzzaob, localObject[0], localzzaot1, localObject[1], localzzaot2, paramzzapx);
  }
  
  private final class zza<K, V>
    extends zzaot<Map<K, V>>
  {
    private final zzaot<K> bmC;
    private final zzaot<V> bmD;
    private final zzapg<? extends Map<K, V>> bmt;
    
    public zza(Type paramType1, zzaot<K> paramzzaot, Type paramType2, zzaot<V> paramzzaot1, zzapg<? extends Map<K, V>> paramzzapg)
    {
      this.bmC = new zzapv(paramType1, paramType2, paramzzaot);
      this.bmD = new zzapv(paramType1, paramzzapg, paramzzaot1);
      zzapg localzzapg;
      this.bmt = localzzapg;
    }
    
    private String zze(zzaoh paramzzaoh)
    {
      if (paramzzaoh.aU())
      {
        paramzzaoh = paramzzaoh.aY();
        if (paramzzaoh.bb()) {
          return String.valueOf(paramzzaoh.aQ());
        }
        if (paramzzaoh.ba()) {
          return Boolean.toString(paramzzaoh.getAsBoolean());
        }
        if (paramzzaoh.bc()) {
          return paramzzaoh.aR();
        }
        throw new AssertionError();
      }
      if (paramzzaoh.aV()) {
        return "null";
      }
      throw new AssertionError();
    }
    
    public void zza(zzaqa paramzzaqa, Map<K, V> paramMap)
      throws IOException
    {
      int m = 0;
      int k = 0;
      if (paramMap == null)
      {
        paramzzaqa.bx();
        return;
      }
      if (!zzapq.zza(zzapq.this))
      {
        paramzzaqa.bv();
        paramMap = paramMap.entrySet().iterator();
        while (paramMap.hasNext())
        {
          localObject = (Map.Entry)paramMap.next();
          paramzzaqa.zzus(String.valueOf(((Map.Entry)localObject).getKey()));
          this.bmD.zza(paramzzaqa, ((Map.Entry)localObject).getValue());
        }
        paramzzaqa.bw();
        return;
      }
      Object localObject = new ArrayList(paramMap.size());
      ArrayList localArrayList = new ArrayList(paramMap.size());
      paramMap = paramMap.entrySet().iterator();
      int i = 0;
      if (paramMap.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)paramMap.next();
        zzaoh localzzaoh = this.bmC.zzco(localEntry.getKey());
        ((List)localObject).add(localzzaoh);
        localArrayList.add(localEntry.getValue());
        if ((localzzaoh.aS()) || (localzzaoh.aT())) {}
        for (int j = 1;; j = 0)
        {
          i = j | i;
          break;
        }
      }
      if (i != 0)
      {
        paramzzaqa.bt();
        i = k;
        while (i < ((List)localObject).size())
        {
          paramzzaqa.bt();
          zzapi.zzb((zzaoh)((List)localObject).get(i), paramzzaqa);
          this.bmD.zza(paramzzaqa, localArrayList.get(i));
          paramzzaqa.bu();
          i += 1;
        }
        paramzzaqa.bu();
        return;
      }
      paramzzaqa.bv();
      i = m;
      while (i < ((List)localObject).size())
      {
        paramzzaqa.zzus(zze((zzaoh)((List)localObject).get(i)));
        this.bmD.zza(paramzzaqa, localArrayList.get(i));
        i += 1;
      }
      paramzzaqa.bw();
    }
    
    public Map<K, V> zzl(zzapy paramzzapy)
      throws IOException
    {
      Object localObject = paramzzapy.bn();
      if (localObject == zzapz.bos)
      {
        paramzzapy.nextNull();
        return null;
      }
      Map localMap = (Map)this.bmt.bg();
      if (localObject == zzapz.bok)
      {
        paramzzapy.beginArray();
        while (paramzzapy.hasNext())
        {
          paramzzapy.beginArray();
          localObject = this.bmC.zzb(paramzzapy);
          if (localMap.put(localObject, this.bmD.zzb(paramzzapy)) != null)
          {
            paramzzapy = String.valueOf(localObject);
            throw new zzaoq(String.valueOf(paramzzapy).length() + 15 + "duplicate key: " + paramzzapy);
          }
          paramzzapy.endArray();
        }
        paramzzapy.endArray();
        return localMap;
      }
      paramzzapy.beginObject();
      while (paramzzapy.hasNext())
      {
        zzapd.blQ.zzi(paramzzapy);
        localObject = this.bmC.zzb(paramzzapy);
        if (localMap.put(localObject, this.bmD.zzb(paramzzapy)) != null)
        {
          paramzzapy = String.valueOf(localObject);
          throw new zzaoq(String.valueOf(paramzzapy).length() + 15 + "duplicate key: " + paramzzapy);
        }
      }
      paramzzapy.endObject();
      return localMap;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzapq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */