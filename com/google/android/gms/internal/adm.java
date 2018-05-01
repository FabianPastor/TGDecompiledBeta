package com.google.android.gms.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

final class adm
  implements Cloneable
{
  private Object value;
  private adk<?, ?> zzcsu;
  private List<adr> zzcsv = new ArrayList();
  
  private final byte[] toByteArray()
    throws IOException
  {
    byte[] arrayOfByte = new byte[zzn()];
    zza(adh.zzI(arrayOfByte));
    return arrayOfByte;
  }
  
  private adm zzLP()
  {
    adm localadm = new adm();
    try
    {
      localadm.zzcsu = this.zzcsu;
      if (this.zzcsv == null) {
        localadm.zzcsv = null;
      }
      for (;;)
      {
        if (this.value == null) {
          return localadm;
        }
        if (!(this.value instanceof adp)) {
          break;
        }
        localadm.value = ((adp)((adp)this.value).clone());
        return localadm;
        localadm.zzcsv.addAll(this.zzcsv);
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
    if ((this.value instanceof adp[]))
    {
      localObject1 = (adp[])this.value;
      localObject2 = new adp[localObject1.length];
      localCloneNotSupportedException.value = localObject2;
      i = 0;
      while (i < localObject1.length)
      {
        localObject2[i] = ((adp)localObject1[i].clone());
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
      } while (!(paramObject instanceof adm));
      paramObject = (adm)paramObject;
      if ((this.value == null) || (((adm)paramObject).value == null)) {
        break;
      }
      bool1 = bool2;
    } while (this.zzcsu != ((adm)paramObject).zzcsu);
    if (!this.zzcsu.zzcjG.isArray()) {
      return this.value.equals(((adm)paramObject).value);
    }
    if ((this.value instanceof byte[])) {
      return Arrays.equals((byte[])this.value, (byte[])((adm)paramObject).value);
    }
    if ((this.value instanceof int[])) {
      return Arrays.equals((int[])this.value, (int[])((adm)paramObject).value);
    }
    if ((this.value instanceof long[])) {
      return Arrays.equals((long[])this.value, (long[])((adm)paramObject).value);
    }
    if ((this.value instanceof float[])) {
      return Arrays.equals((float[])this.value, (float[])((adm)paramObject).value);
    }
    if ((this.value instanceof double[])) {
      return Arrays.equals((double[])this.value, (double[])((adm)paramObject).value);
    }
    if ((this.value instanceof boolean[])) {
      return Arrays.equals((boolean[])this.value, (boolean[])((adm)paramObject).value);
    }
    return Arrays.deepEquals((Object[])this.value, (Object[])((adm)paramObject).value);
    if ((this.zzcsv != null) && (((adm)paramObject).zzcsv != null)) {
      return this.zzcsv.equals(((adm)paramObject).zzcsv);
    }
    try
    {
      bool1 = Arrays.equals(toByteArray(), ((adm)paramObject).toByteArray());
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
  
  final void zza(adh paramadh)
    throws IOException
  {
    if (this.value != null) {
      this.zzcsu.zza(this.value, paramadh);
    }
    for (;;)
    {
      return;
      Iterator localIterator = this.zzcsv.iterator();
      while (localIterator.hasNext())
      {
        adr localadr = (adr)localIterator.next();
        paramadh.zzcu(localadr.tag);
        paramadh.zzK(localadr.zzbws);
      }
    }
  }
  
  final void zza(adr paramadr)
  {
    this.zzcsv.add(paramadr);
  }
  
  final <T> T zzb(adk<?, T> paramadk)
  {
    if (this.value != null)
    {
      if (!this.zzcsu.equals(paramadk)) {
        throw new IllegalStateException("Tried to getExtension with a different Extension.");
      }
    }
    else
    {
      this.zzcsu = paramadk;
      this.value = paramadk.zzX(this.zzcsv);
      this.zzcsv = null;
    }
    return (T)this.value;
  }
  
  final int zzn()
  {
    int j;
    if (this.value != null)
    {
      j = this.zzcsu.zzav(this.value);
      return j;
    }
    Iterator localIterator = this.zzcsv.iterator();
    adr localadr;
    for (int i = 0;; i = localadr.zzbws.length + (j + 0) + i)
    {
      j = i;
      if (!localIterator.hasNext()) {
        break;
      }
      localadr = (adr)localIterator.next();
      j = adh.zzcv(localadr.tag);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/adm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */