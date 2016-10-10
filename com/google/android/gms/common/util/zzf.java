package com.google.android.gms.common.util;

import android.support.v4.util.ArrayMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class zzf
{
  public static <K, V> Map<K, V> zza(K paramK1, V paramV1, K paramK2, V paramV2, K paramK3, V paramV3, K paramK4, V paramV4, K paramK5, V paramV5, K paramK6, V paramV6)
  {
    ArrayMap localArrayMap = new ArrayMap(6);
    localArrayMap.put(paramK1, paramV1);
    localArrayMap.put(paramK2, paramV2);
    localArrayMap.put(paramK3, paramV3);
    localArrayMap.put(paramK4, paramV4);
    localArrayMap.put(paramK5, paramV5);
    localArrayMap.put(paramK6, paramV6);
    return Collections.unmodifiableMap(localArrayMap);
  }
  
  public static <T> Set<T> zza(T paramT1, T paramT2, T paramT3)
  {
    zza localzza = new zza(3);
    localzza.add(paramT1);
    localzza.add(paramT2);
    localzza.add(paramT3);
    return Collections.unmodifiableSet(localzza);
  }
  
  public static <T> Set<T> zza(T paramT1, T paramT2, T paramT3, T paramT4)
  {
    zza localzza = new zza(4);
    localzza.add(paramT1);
    localzza.add(paramT2);
    localzza.add(paramT3);
    localzza.add(paramT4);
    return Collections.unmodifiableSet(localzza);
  }
  
  private static <K, V> void zza(K[] paramArrayOfK, V[] paramArrayOfV)
  {
    if (paramArrayOfK.length != paramArrayOfV.length)
    {
      int i = paramArrayOfK.length;
      int j = paramArrayOfV.length;
      throw new IllegalArgumentException(66 + "Key and values array lengths not equal: " + i + " != " + j);
    }
  }
  
  public static <T> Set<T> zzaa(T paramT)
  {
    return Collections.singleton(paramT);
  }
  
  public static <T> Set<T> zzaxh()
  {
    return Collections.emptySet();
  }
  
  public static <K, V> Map<K, V> zzaxi()
  {
    return Collections.emptyMap();
  }
  
  public static <K, V> Map<K, V> zzb(K[] paramArrayOfK, V[] paramArrayOfV)
  {
    int i = 0;
    zza(paramArrayOfK, paramArrayOfV);
    int j = paramArrayOfK.length;
    Object localObject;
    switch (j)
    {
    default: 
      if (j <= 32) {
        localObject = new ArrayMap(j);
      }
      break;
    }
    while (i < j)
    {
      ((Map)localObject).put(paramArrayOfK[i], paramArrayOfV[i]);
      i += 1;
      continue;
      return zzaxi();
      return zze(paramArrayOfK[0], paramArrayOfV[0]);
      localObject = new HashMap(j, 1.0F);
    }
    return Collections.unmodifiableMap((Map)localObject);
  }
  
  public static <T> List<T> zzc(T paramT1, T paramT2)
  {
    ArrayList localArrayList = new ArrayList(2);
    localArrayList.add(paramT1);
    localArrayList.add(paramT2);
    return Collections.unmodifiableList(localArrayList);
  }
  
  public static <T> Set<T> zzc(T... paramVarArgs)
  {
    switch (paramVarArgs.length)
    {
    default: 
      if (paramVarArgs.length > 32) {
        break;
      }
    }
    for (paramVarArgs = new zza(Arrays.asList(paramVarArgs));; paramVarArgs = new HashSet(Arrays.asList(paramVarArgs)))
    {
      return Collections.unmodifiableSet(paramVarArgs);
      return zzaxh();
      return zzaa(paramVarArgs[0]);
      return zzd(paramVarArgs[0], paramVarArgs[1]);
      return zza(paramVarArgs[0], paramVarArgs[1], paramVarArgs[2]);
      return zza(paramVarArgs[0], paramVarArgs[1], paramVarArgs[2], paramVarArgs[3]);
    }
  }
  
  public static <T> Set<T> zzd(T paramT1, T paramT2)
  {
    zza localzza = new zza(2);
    localzza.add(paramT1);
    localzza.add(paramT2);
    return Collections.unmodifiableSet(localzza);
  }
  
  public static <K, V> Map<K, V> zze(K paramK, V paramV)
  {
    return Collections.singletonMap(paramK, paramV);
  }
  
  public static <T> List<T> zzz(T paramT)
  {
    return Collections.singletonList(paramT);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/util/zzf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */