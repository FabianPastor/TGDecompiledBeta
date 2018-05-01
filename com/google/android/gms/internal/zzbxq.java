package com.google.android.gms.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

class zzbxq
  implements Cloneable
{
  private Object value;
  private zzbxo<?, ?> zzcuO;
  private List<zzbxv> zzcuP = new ArrayList();
  
  private byte[] toByteArray()
    throws IOException
  {
    byte[] arrayOfByte = new byte[zzu()];
    zza(zzbxm.zzag(arrayOfByte));
    return arrayOfByte;
  }
  
  public boolean equals(Object paramObject)
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
      } while (!(paramObject instanceof zzbxq));
      paramObject = (zzbxq)paramObject;
      if ((this.value == null) || (((zzbxq)paramObject).value == null)) {
        break;
      }
      bool1 = bool2;
    } while (this.zzcuO != ((zzbxq)paramObject).zzcuO);
    if (!this.zzcuO.zzckM.isArray()) {
      return this.value.equals(((zzbxq)paramObject).value);
    }
    if ((this.value instanceof byte[])) {
      return Arrays.equals((byte[])this.value, (byte[])((zzbxq)paramObject).value);
    }
    if ((this.value instanceof int[])) {
      return Arrays.equals((int[])this.value, (int[])((zzbxq)paramObject).value);
    }
    if ((this.value instanceof long[])) {
      return Arrays.equals((long[])this.value, (long[])((zzbxq)paramObject).value);
    }
    if ((this.value instanceof float[])) {
      return Arrays.equals((float[])this.value, (float[])((zzbxq)paramObject).value);
    }
    if ((this.value instanceof double[])) {
      return Arrays.equals((double[])this.value, (double[])((zzbxq)paramObject).value);
    }
    if ((this.value instanceof boolean[])) {
      return Arrays.equals((boolean[])this.value, (boolean[])((zzbxq)paramObject).value);
    }
    return Arrays.deepEquals((Object[])this.value, (Object[])((zzbxq)paramObject).value);
    if ((this.zzcuP != null) && (((zzbxq)paramObject).zzcuP != null)) {
      return this.zzcuP.equals(((zzbxq)paramObject).zzcuP);
    }
    try
    {
      bool1 = Arrays.equals(toByteArray(), ((zzbxq)paramObject).toByteArray());
      return bool1;
    }
    catch (IOException paramObject)
    {
      throw new IllegalStateException((Throwable)paramObject);
    }
  }
  
  public int hashCode()
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
  
  void zza(zzbxm paramzzbxm)
    throws IOException
  {
    if (this.value != null) {
      this.zzcuO.zza(this.value, paramzzbxm);
    }
    for (;;)
    {
      return;
      Iterator localIterator = this.zzcuP.iterator();
      while (localIterator.hasNext()) {
        ((zzbxv)localIterator.next()).zza(paramzzbxm);
      }
    }
  }
  
  void zza(zzbxv paramzzbxv)
  {
    this.zzcuP.add(paramzzbxv);
  }
  
  public final zzbxq zzaeK()
  {
    zzbxq localzzbxq = new zzbxq();
    try
    {
      localzzbxq.zzcuO = this.zzcuO;
      if (this.zzcuP == null) {
        localzzbxq.zzcuP = null;
      }
      while (this.value == null)
      {
        return localzzbxq;
        localzzbxq.zzcuP.addAll(this.zzcuP);
      }
      if (!(this.value instanceof zzbxt)) {
        break label93;
      }
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new AssertionError(localCloneNotSupportedException);
    }
    localCloneNotSupportedException.value = ((zzbxt)((zzbxt)this.value).clone());
    return localCloneNotSupportedException;
    label93:
    if ((this.value instanceof byte[]))
    {
      localCloneNotSupportedException.value = ((byte[])this.value).clone();
      return localCloneNotSupportedException;
    }
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
    if ((this.value instanceof zzbxt[]))
    {
      localObject1 = (zzbxt[])this.value;
      localObject2 = new zzbxt[localObject1.length];
      localCloneNotSupportedException.value = localObject2;
      i = 0;
      while (i < localObject1.length)
      {
        localObject2[i] = ((zzbxt)localObject1[i].clone());
        i += 1;
      }
    }
    return localCloneNotSupportedException;
  }
  
  <T> T zzb(zzbxo<?, T> paramzzbxo)
  {
    if (this.value != null)
    {
      if (!this.zzcuO.equals(paramzzbxo)) {
        throw new IllegalStateException("Tried to getExtension with a different Extension.");
      }
    }
    else
    {
      this.zzcuO = paramzzbxo;
      this.value = paramzzbxo.zzac(this.zzcuP);
      this.zzcuP = null;
    }
    return (T)this.value;
  }
  
  int zzu()
  {
    int j;
    if (this.value != null)
    {
      j = this.zzcuO.zzaU(this.value);
      return j;
    }
    Iterator localIterator = this.zzcuP.iterator();
    for (int i = 0;; i = ((zzbxv)localIterator.next()).zzu() + i)
    {
      j = i;
      if (!localIterator.hasNext()) {
        break;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbxq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */