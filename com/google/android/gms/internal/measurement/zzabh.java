package com.google.android.gms.internal.measurement;

import java.nio.charset.Charset;
import java.util.Arrays;

public final class zzabh
{
  private static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
  protected static final Charset UTF_8 = Charset.forName("UTF-8");
  public static final Object zzbzr = new Object();
  
  public static boolean equals(long[] paramArrayOfLong1, long[] paramArrayOfLong2)
  {
    boolean bool;
    if ((paramArrayOfLong1 == null) || (paramArrayOfLong1.length == 0)) {
      if ((paramArrayOfLong2 == null) || (paramArrayOfLong2.length == 0)) {
        bool = true;
      }
    }
    for (;;)
    {
      return bool;
      bool = false;
      continue;
      bool = Arrays.equals(paramArrayOfLong1, paramArrayOfLong2);
    }
  }
  
  public static boolean equals(Object[] paramArrayOfObject1, Object[] paramArrayOfObject2)
  {
    boolean bool1 = false;
    int i;
    if (paramArrayOfObject1 == null)
    {
      i = 0;
      if (paramArrayOfObject2 != null) {
        break label46;
      }
    }
    int k;
    int m;
    label46:
    for (int j = 0;; j = paramArrayOfObject2.length)
    {
      k = 0;
      for (m = 0; (m < i) && (paramArrayOfObject1[m] == null); m++) {}
      i = paramArrayOfObject1.length;
      break;
    }
    for (;;)
    {
      if ((k < j) && (paramArrayOfObject2[k] == null))
      {
        k++;
      }
      else
      {
        int n;
        int i1;
        label92:
        boolean bool2;
        if (m >= i)
        {
          n = 1;
          if (k < j) {
            break label114;
          }
          i1 = 1;
          if ((n == 0) || (i1 == 0)) {
            break label120;
          }
          bool2 = true;
        }
        label114:
        label120:
        do
        {
          do
          {
            return bool2;
            n = 0;
            break;
            i1 = 0;
            break label92;
            bool2 = bool1;
          } while (n != i1);
          bool2 = bool1;
        } while (!paramArrayOfObject1[m].equals(paramArrayOfObject2[k]));
        k++;
        m++;
        break;
      }
    }
  }
  
  public static int hashCode(long[] paramArrayOfLong)
  {
    if ((paramArrayOfLong == null) || (paramArrayOfLong.length == 0)) {}
    for (int i = 0;; i = Arrays.hashCode(paramArrayOfLong)) {
      return i;
    }
  }
  
  public static int hashCode(Object[] paramArrayOfObject)
  {
    int i;
    int j;
    int k;
    if (paramArrayOfObject == null)
    {
      i = 0;
      j = 0;
      k = 0;
      label10:
      if (j >= i) {
        break label48;
      }
      Object localObject = paramArrayOfObject[j];
      if (localObject == null) {
        break label50;
      }
      k = localObject.hashCode() + k * 31;
    }
    label48:
    label50:
    for (;;)
    {
      j++;
      break label10;
      i = paramArrayOfObject.length;
      break;
      return k;
    }
  }
  
  public static void zza(zzabd paramzzabd1, zzabd paramzzabd2)
  {
    if (paramzzabd1.zzbzh != null) {
      paramzzabd2.zzbzh = ((zzabf)paramzzabd1.zzbzh.clone());
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzabh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */