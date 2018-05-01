package com.google.android.gms.common.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class CollectionUtils
{
  @Deprecated
  public static <T> List<T> listOf()
  {
    return Collections.emptyList();
  }
  
  @Deprecated
  public static <T> List<T> listOf(T paramT)
  {
    return Collections.singletonList(paramT);
  }
  
  @Deprecated
  public static <T> List<T> listOf(T... paramVarArgs)
  {
    switch (paramVarArgs.length)
    {
    default: 
      paramVarArgs = Collections.unmodifiableList(Arrays.asList(paramVarArgs));
    }
    for (;;)
    {
      return paramVarArgs;
      paramVarArgs = listOf();
      continue;
      paramVarArgs = listOf(paramVarArgs[0]);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/util/CollectionUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */