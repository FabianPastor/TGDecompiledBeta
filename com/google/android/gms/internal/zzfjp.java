package com.google.android.gms.internal;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

final class zzfjp
  implements Cloneable
{
  private Object value;
  private zzfjn<?, ?> zzpni;
  private List<zzfju> zzpnj = new ArrayList();
  
  private final byte[] toByteArray()
    throws IOException
  {
    byte[] arrayOfByte = new byte[zzq()];
    zza(zzfjk.zzbf(arrayOfByte));
    return arrayOfByte;
  }
  
  private zzfjp zzdah()
  {
    zzfjp localzzfjp = new zzfjp();
    try
    {
      localzzfjp.zzpni = this.zzpni;
      if (this.zzpnj == null) {
        localzzfjp.zzpnj = null;
      }
      for (;;)
      {
        if (this.value == null) {
          return localzzfjp;
        }
        if (!(this.value instanceof zzfjs)) {
          break;
        }
        localzzfjp.value = ((zzfjs)((zzfjs)this.value).clone());
        return localzzfjp;
        localzzfjp.zzpnj.addAll(this.zzpnj);
      }
      if (!(this.value instanceof byte[])) {
        break label117;
      }
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new AssertionError(localCloneNotSupportedException);
    }
    localCloneNotSupportedException.value = ((byte[])this.value).clone();
    return localCloneNotSupportedException;
    label117:
    Object localObject1;
    Object localObject2;
    int i;
    if ((this.value instanceof byte[][]))
    {
      localObject1 = (byte[][])this.value;
      localObject2 = new byte[localObject1.length][];
      localCloneNotSupportedException.value = localObject2;
      i = 0;
      while (i < localObject1.length)
      {
        localObject2[i] = ((byte[])localObject1[i].clone());
        i += 1;
      }
    }
    if ((this.value instanceof boolean[]))
    {
      localCloneNotSupportedException.value = ((boolean[])this.value).clone();
      return localCloneNotSupportedException;
    }
    if ((this.value instanceof int[]))
    {
      localCloneNotSupportedException.value = ((int[])this.value).clone();
      return localCloneNotSupportedException;
    }
    if ((this.value instanceof long[]))
    {
      localCloneNotSupportedException.value = ((long[])this.value).clone();
      return localCloneNotSupportedException;
    }
    if ((this.value instanceof float[]))
    {
      localCloneNotSupportedException.value = ((float[])this.value).clone();
      return localCloneNotSupportedException;
    }
    if ((this.value instanceof double[]))
    {
      localCloneNotSupportedException.value = ((double[])this.value).clone();
      return localCloneNotSupportedException;
    }
    if ((this.value instanceof zzfjs[]))
    {
      localObject1 = (zzfjs[])this.value;
      localObject2 = new zzfjs[localObject1.length];
      localCloneNotSupportedException.value = localObject2;
      i = 0;
      while (i < localObject1.length)
      {
        localObject2[i] = ((zzfjs)localObject1[i].clone());
        i += 1;
      }
    }
    return localCloneNotSupportedException;
  }
  
  public final boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    boolean bool1;
    if (paramObject == this) {
      bool1 = true;
    }
    do
    {
      do
      {
        return bool1;
        bool1 = bool2;
      } while (!(paramObject instanceof zzfjp));
      paramObject = (zzfjp)paramObject;
      if ((this.value == null) || (((zzfjp)paramObject).value == null)) {
        break;
      }
      bool1 = bool2;
    } while (this.zzpni != ((zzfjp)paramObject).zzpni);
    if (!this.zzpni.zznfk.isArray()) {
      return this.value.equals(((zzfjp)paramObject).value);
    }
    if ((this.value instanceof byte[])) {
      return Arrays.equals((byte[])this.value, (byte[])((zzfjp)paramObject).value);
    }
    if ((this.value instanceof int[])) {
      return Arrays.equals((int[])this.value, (int[])((zzfjp)paramObject).value);
    }
    if ((this.value instanceof long[])) {
      return Arrays.equals((long[])this.value, (long[])((zzfjp)paramObject).value);
    }
    if ((this.value instanceof float[])) {
      return Arrays.equals((float[])this.value, (float[])((zzfjp)paramObject).value);
    }
    if ((this.value instanceof double[])) {
      return Arrays.equals((double[])this.value, (double[])((zzfjp)paramObject).value);
    }
    if ((this.value instanceof boolean[])) {
      return Arrays.equals((boolean[])this.value, (boolean[])((zzfjp)paramObject).value);
    }
    return Arrays.deepEquals((Object[])this.value, (Object[])((zzfjp)paramObject).value);
    if ((this.zzpnj != null) && (((zzfjp)paramObject).zzpnj != null)) {
      return this.zzpnj.equals(((zzfjp)paramObject).zzpnj);
    }
    try
    {
      bool1 = Arrays.equals(toByteArray(), ((zzfjp)paramObject).toByteArray());
      return bool1;
    }
    catch (IOException paramObject)
    {
      throw new IllegalStateException((Throwable)paramObject);
    }
  }
  
  public final int hashCode()
  {
    try
    {
      int i = Arrays.hashCode(toByteArray());
      return i + 527;
    }
    catch (IOException localIOException)
    {
      throw new IllegalStateException(localIOException);
    }
  }
  
  final void zza(zzfjk paramzzfjk)
    throws IOException
  {
    Object localObject1;
    Object localObject2;
    if (this.value != null)
    {
      localObject1 = this.zzpni;
      localObject2 = this.value;
      if (((zzfjn)localObject1).zzpnd)
      {
        int j = Array.getLength(localObject2);
        int i = 0;
        while (i < j)
        {
          Object localObject3 = Array.get(localObject2, i);
          if (localObject3 != null) {
            ((zzfjn)localObject1).zza(localObject3, paramzzfjk);
          }
          i += 1;
        }
      }
      ((zzfjn)localObject1).zza(localObject2, paramzzfjk);
    }
    for (;;)
    {
      return;
      localObject1 = this.zzpnj.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (zzfju)((Iterator)localObject1).next();
        paramzzfjk.zzmi(((zzfju)localObject2).tag);
        paramzzfjk.zzbh(((zzfju)localObject2).zzjng);
      }
    }
  }
  
  final void zza(zzfju paramzzfju)
  {
    this.zzpnj.add(paramzzfju);
  }
  
  final int zzq()
  {
    int i = 0;
    Object localObject2;
    int j;
    if (this.value != null)
    {
      localObject1 = this.zzpni;
      localObject2 = this.value;
      if (((zzfjn)localObject1).zzpnd)
      {
        int m = Array.getLength(localObject2);
        j = 0;
        for (;;)
        {
          k = i;
          if (j >= m) {
            break;
          }
          k = i;
          if (Array.get(localObject2, j) != null) {
            k = i + ((zzfjn)localObject1).zzcs(Array.get(localObject2, j));
          }
          j += 1;
          i = k;
        }
      }
      int k = ((zzfjn)localObject1).zzcs(localObject2);
      return k;
    }
    Object localObject1 = this.zzpnj.iterator();
    for (i = 0; ((Iterator)localObject1).hasNext(); i = ((zzfju)localObject2).zzjng.length + (j + 0) + i)
    {
      localObject2 = (zzfju)((Iterator)localObject1).next();
      j = zzfjk.zzlp(((zzfju)localObject2).tag);
    }
    return i;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzfjp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */