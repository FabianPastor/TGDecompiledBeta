package com.google.android.gms.common.util;

import com.google.android.gms.common.internal.zzbg;
import java.lang.reflect.Array;
import java.util.Arrays;

public final class zza
{
  public static <T> T[] zza(T[] paramArrayOfT1, T... paramVarArgs)
  {
    if (paramArrayOfT1 == null)
    {
      paramArrayOfT1 = null;
      return paramArrayOfT1;
    }
    if (paramVarArgs.length == 0) {
      return Arrays.copyOf(paramArrayOfT1, paramArrayOfT1.length);
    }
    Object[] arrayOfObject = (Object[])Array.newInstance(paramVarArgs.getClass().getComponentType(), paramArrayOfT1.length);
    int n;
    int j;
    int i;
    label51:
    int m;
    T ?;
    int k;
    if (paramVarArgs.length == 1)
    {
      n = paramArrayOfT1.length;
      j = 0;
      i = 0;
      m = i;
      if (j >= n) {
        break label193;
      }
      ? = paramArrayOfT1[j];
      if (zzbg.equal(paramVarArgs[0], ?)) {
        break label222;
      }
      k = i + 1;
      arrayOfObject[i] = ?;
      i = k;
    }
    label106:
    label128:
    label145:
    label151:
    label188:
    label193:
    label219:
    label222:
    for (;;)
    {
      j += 1;
      break label51;
      n = paramArrayOfT1.length;
      k = 0;
      i = 0;
      m = i;
      if (k < n)
      {
        ? = paramArrayOfT1[k];
        m = paramVarArgs.length;
        j = 0;
        if (j < m) {
          if (zzbg.equal(paramVarArgs[j], ?))
          {
            if (j < 0) {
              break label188;
            }
            j = 1;
            if (j != 0) {
              break label219;
            }
            j = i + 1;
            arrayOfObject[i] = ?;
            i = j;
          }
        }
      }
      for (;;)
      {
        k += 1;
        break label106;
        j += 1;
        break label128;
        j = -1;
        break label145;
        j = 0;
        break label151;
        if (arrayOfObject == null) {
          return null;
        }
        paramArrayOfT1 = arrayOfObject;
        if (m == arrayOfObject.length) {
          break;
        }
        return Arrays.copyOf(arrayOfObject, m);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/util/zza.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */