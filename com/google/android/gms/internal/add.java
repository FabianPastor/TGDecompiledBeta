package com.google.android.gms.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

final class add
  implements Cloneable
{
  private Object value;
  private adb<?, ?> zzcsf;
  private List<adi> zzcsg = new ArrayList();
  
  private final byte[] toByteArray()
    throws IOException
  {
    byte[] arrayOfByte = new byte[zzn()];
    zza(acy.zzI(arrayOfByte));
    return arrayOfByte;
  }
  
  private add zzLN()
  {
    add localadd = new add();
    try
    {
      localadd.zzcsf = this.zzcsf;
      if (this.zzcsg == null) {
        localadd.zzcsg = null;
      }
      for (;;)
      {
        if (this.value == null) {
          return localadd;
        }
        if (!(this.value instanceof adg)) {
          break;
        }
        localadd.value = ((adg)((adg)this.value).clone());
        return localadd;
        localadd.zzcsg.addAll(this.zzcsg);
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
    if ((this.value instanceof adg[]))
    {
      localObject1 = (adg[])this.value;
      localObject2 = new adg[localObject1.length];
      localCloneNotSupportedException.value = localObject2;
      i = 0;
      while (i < localObject1.length)
      {
        localObject2[i] = ((adg)localObject1[i].clone());
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
      } while (!(paramObject instanceof add));
      paramObject = (add)paramObject;
      if ((this.value == null) || (((add)paramObject).value == null)) {
        break;
      }
      bool1 = bool2;
    } while (this.zzcsf != ((add)paramObject).zzcsf);
    if (!this.zzcsf.zzcjC.isArray()) {
      return this.value.equals(((add)paramObject).value);
    }
    if ((this.value instanceof byte[])) {
      return Arrays.equals((byte[])this.value, (byte[])((add)paramObject).value);
    }
    if ((this.value instanceof int[])) {
      return Arrays.equals((int[])this.value, (int[])((add)paramObject).value);
    }
    if ((this.value instanceof long[])) {
      return Arrays.equals((long[])this.value, (long[])((add)paramObject).value);
    }
    if ((this.value instanceof float[])) {
      return Arrays.equals((float[])this.value, (float[])((add)paramObject).value);
    }
    if ((this.value instanceof double[])) {
      return Arrays.equals((double[])this.value, (double[])((add)paramObject).value);
    }
    if ((this.value instanceof boolean[])) {
      return Arrays.equals((boolean[])this.value, (boolean[])((add)paramObject).value);
    }
    return Arrays.deepEquals((Object[])this.value, (Object[])((add)paramObject).value);
    if ((this.zzcsg != null) && (((add)paramObject).zzcsg != null)) {
      return this.zzcsg.equals(((add)paramObject).zzcsg);
    }
    try
    {
      bool1 = Arrays.equals(toByteArray(), ((add)paramObject).toByteArray());
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
  
  final void zza(acy paramacy)
    throws IOException
  {
    if (this.value != null) {
      this.zzcsf.zza(this.value, paramacy);
    }
    for (;;)
    {
      return;
      Iterator localIterator = this.zzcsg.iterator();
      while (localIterator.hasNext())
      {
        adi localadi = (adi)localIterator.next();
        paramacy.zzcu(localadi.tag);
        paramacy.zzK(localadi.zzbws);
      }
    }
  }
  
  final void zza(adi paramadi)
  {
    this.zzcsg.add(paramadi);
  }
  
  final <T> T zzb(adb<?, T> paramadb)
  {
    if (this.value != null)
    {
      if (!this.zzcsf.equals(paramadb)) {
        throw new IllegalStateException("Tried to getExtension with a different Extension.");
      }
    }
    else
    {
      this.zzcsf = paramadb;
      this.value = paramadb.zzX(this.zzcsg);
      this.zzcsg = null;
    }
    return (T)this.value;
  }
  
  final int zzn()
  {
    int j;
    if (this.value != null)
    {
      j = this.zzcsf.zzav(this.value);
      return j;
    }
    Iterator localIterator = this.zzcsg.iterator();
    adi localadi;
    for (int i = 0;; i = localadi.zzbws.length + (j + 0) + i)
    {
      j = i;
      if (!localIterator.hasNext()) {
        break;
      }
      localadi = (adi)localIterator.next();
      j = acy.zzcv(localadi.tag);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/add.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */