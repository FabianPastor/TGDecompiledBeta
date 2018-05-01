package com.google.android.gms.common.util;

import android.support.v4.util.ArrayMap;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class zzf
{
  public static <K, V> Map<K, V> zza(K paramK1, V paramV1, K paramK2, V paramV2)
  {
    Map localMap = zzg(2, false);
    localMap.put(paramK1, paramV1);
    localMap.put(paramK2, paramV2);
    return Collections.unmodifiableMap(localMap);
  }
  
  public static <K, V> Map<K, V> zza(K paramK1, V paramV1, K paramK2, V paramV2, K paramK3, V paramV3, K paramK4, V paramV4, K paramK5, V paramV5, K paramK6, V paramV6)
  {
    Map localMap = zzg(6, false);
    localMap.put(paramK1, paramV1);
    localMap.put(paramK2, paramV2);
    localMap.put(paramK3, paramV3);
    localMap.put(paramK4, paramV4);
    localMap.put(paramK5, paramV5);
    localMap.put(paramK6, paramV6);
    return Collections.unmodifiableMap(localMap);
  }
  
  public static <T> Set<T> zza(T paramT1, T paramT2, T paramT3)
  {
    Set localSet = zzf(3, false);
    localSet.add(paramT1);
    localSet.add(paramT2);
    localSet.add(paramT3);
    return Collections.unmodifiableSet(localSet);
  }
  
  public static <T> Set<T> zzb(T... paramVarArgs)
  {
    switch (paramVarArgs.length)
    {
    default: 
      localObject1 = zzf(paramVarArgs.length, false);
      Collections.addAll((Collection)localObject1, paramVarArgs);
      return Collections.unmodifiableSet((Set)localObject1);
    case 0: 
      return Collections.emptySet();
    case 1: 
      return Collections.singleton(paramVarArgs[0]);
    case 2: 
      localObject1 = paramVarArgs[0];
      paramVarArgs = paramVarArgs[1];
      localObject2 = zzf(2, false);
      ((Set)localObject2).add(localObject1);
      ((Set)localObject2).add(paramVarArgs);
      return Collections.unmodifiableSet((Set)localObject2);
    case 3: 
      return zza(paramVarArgs[0], paramVarArgs[1], paramVarArgs[2]);
    }
    Object localObject1 = paramVarArgs[0];
    Object localObject2 = paramVarArgs[1];
    T ? = paramVarArgs[2];
    paramVarArgs = paramVarArgs[3];
    Set localSet = zzf(4, false);
    localSet.add(localObject1);
    localSet.add(localObject2);
    localSet.add(?);
    localSet.add(paramVarArgs);
    return Collections.unmodifiableSet(localSet);
  }
  
  private static <T> Set<T> zzf(int paramInt, boolean paramBoolean)
  {
    if (paramInt <= 256) {
      return new zza(paramInt);
    }
    return new HashSet(paramInt, 1.0F);
  }
  
  private static <K, V> Map<K, V> zzg(int paramInt, boolean paramBoolean)
  {
    if (paramInt <= 256) {
      return new ArrayMap(paramInt);
    }
    return new HashMap(paramInt, 1.0F);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/util/zzf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */