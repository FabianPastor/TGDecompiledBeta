package com.google.android.gms.common.util;

import com.google.android.gms.common.internal.Objects;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public final class ArrayUtils
{
  public static <T> boolean contains(T[] paramArrayOfT, T paramT)
  {
    if (indexOf(paramArrayOfT, paramT) >= 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static <T> int indexOf(T[] paramArrayOfT, T paramT)
  {
    int i = 0;
    int j;
    if (paramArrayOfT != null)
    {
      j = paramArrayOfT.length;
      if (i >= j) {
        break label37;
      }
      if (!Objects.equal(paramArrayOfT[i], paramT)) {
        break label31;
      }
    }
    for (;;)
    {
      return i;
      j = 0;
      break;
      label31:
      i++;
      break;
      label37:
      i = -1;
    }
  }
  
  public static <T> ArrayList<T> newArrayList()
  {
    return new ArrayList();
  }
  
  public static <T> T[] removeAll(T[] paramArrayOfT1, T... paramVarArgs)
  {
    if (paramArrayOfT1 == null) {}
    for (paramArrayOfT1 = null;; paramArrayOfT1 = Arrays.copyOf(paramArrayOfT1, paramArrayOfT1.length))
    {
      return paramArrayOfT1;
      if ((paramVarArgs != null) && (paramVarArgs.length != 0)) {
        break;
      }
    }
    Object[] arrayOfObject = (Object[])Array.newInstance(paramVarArgs.getClass().getComponentType(), paramArrayOfT1.length);
    int i;
    int j;
    int k;
    label58:
    int m;
    T ?;
    if (paramVarArgs.length == 1)
    {
      i = paramArrayOfT1.length;
      j = 0;
      k = 0;
      m = k;
      if (j >= i) {
        break label163;
      }
      ? = paramArrayOfT1[j];
      if (Objects.equal(paramVarArgs[0], ?)) {
        break label176;
      }
      m = k + 1;
      arrayOfObject[k] = ?;
      k = m;
    }
    label116:
    label163:
    label173:
    label176:
    for (;;)
    {
      j++;
      break label58;
      i = paramArrayOfT1.length;
      j = 0;
      k = 0;
      m = k;
      if (j < i)
      {
        ? = paramArrayOfT1[j];
        if (contains(paramVarArgs, ?)) {
          break label173;
        }
        m = k + 1;
        arrayOfObject[k] = ?;
        k = m;
      }
      for (;;)
      {
        j++;
        break label116;
        paramArrayOfT1 = resize(arrayOfObject, m);
        break;
      }
    }
  }
  
  public static <T> T[] resize(T[] paramArrayOfT, int paramInt)
  {
    Object localObject;
    if (paramArrayOfT == null) {
      localObject = null;
    }
    for (;;)
    {
      return (T[])localObject;
      localObject = paramArrayOfT;
      if (paramInt != paramArrayOfT.length) {
        localObject = Arrays.copyOf(paramArrayOfT, paramInt);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/util/ArrayUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */