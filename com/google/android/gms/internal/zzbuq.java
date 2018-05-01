package com.google.android.gms.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

class zzbuq
  implements Cloneable
{
  private Object value;
  private zzbuo<?, ?> zzcsd;
  private List<zzbuv> zzcse = new ArrayList();
  
  private byte[] toByteArray()
    throws IOException
  {
    byte[] arrayOfByte = new byte[zzv()];
    zza(zzbum.zzae(arrayOfByte));
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
      } while (!(paramObject instanceof zzbuq));
      paramObject = (zzbuq)paramObject;
      if ((this.value == null) || (((zzbuq)paramObject).value == null)) {
        break;
      }
      bool1 = bool2;
    } while (this.zzcsd != ((zzbuq)paramObject).zzcsd);
    if (!this.zzcsd.zzciF.isArray()) {
      return this.value.equals(((zzbuq)paramObject).value);
    }
    if ((this.value instanceof byte[])) {
      return Arrays.equals((byte[])this.value, (byte[])((zzbuq)paramObject).value);
    }
    if ((this.value instanceof int[])) {
      return Arrays.equals((int[])this.value, (int[])((zzbuq)paramObject).value);
    }
    if ((this.value instanceof long[])) {
      return Arrays.equals((long[])this.value, (long[])((zzbuq)paramObject).value);
    }
    if ((this.value instanceof float[])) {
      return Arrays.equals((float[])this.value, (float[])((zzbuq)paramObject).value);
    }
    if ((this.value instanceof double[])) {
      return Arrays.equals((double[])this.value, (double[])((zzbuq)paramObject).value);
    }
    if ((this.value instanceof boolean[])) {
      return Arrays.equals((boolean[])this.value, (boolean[])((zzbuq)paramObject).value);
    }
    return Arrays.deepEquals((Object[])this.value, (Object[])((zzbuq)paramObject).value);
    if ((this.zzcse != null) && (((zzbuq)paramObject).zzcse != null)) {
      return this.zzcse.equals(((zzbuq)paramObject).zzcse);
    }
    try
    {
      bool1 = Arrays.equals(toByteArray(), ((zzbuq)paramObject).toByteArray());
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
  
  void zza(zzbum paramzzbum)
    throws IOException
  {
    if (this.value != null) {
      this.zzcsd.zza(this.value, paramzzbum);
    }
    for (;;)
    {
      return;
      Iterator localIterator = this.zzcse.iterator();
      while (localIterator.hasNext()) {
        ((zzbuv)localIterator.next()).zza(paramzzbum);
      }
    }
  }
  
  void zza(zzbuv paramzzbuv)
  {
    this.zzcse.add(paramzzbuv);
  }
  
  public final zzbuq zzacQ()
  {
    zzbuq localzzbuq = new zzbuq();
    try
    {
      localzzbuq.zzcsd = this.zzcsd;
      if (this.zzcse == null) {
        localzzbuq.zzcse = null;
      }
      while (this.value == null)
      {
        return localzzbuq;
        localzzbuq.zzcse.addAll(this.zzcse);
      }
      if (!(this.value instanceof zzbut)) {
        break label93;
      }
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new AssertionError(localCloneNotSupportedException);
    }
    localCloneNotSupportedException.value = ((zzbut)((zzbut)this.value).clone());
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
    if ((this.value instanceof zzbut[]))
    {
      localObject1 = (zzbut[])this.value;
      localObject2 = new zzbut[localObject1.length];
      localCloneNotSupportedException.value = localObject2;
      i = 0;
      while (i < localObject1.length)
      {
        localObject2[i] = ((zzbut)localObject1[i].clone());
        i += 1;
      }
    }
    return localCloneNotSupportedException;
  }
  
  <T> T zzb(zzbuo<?, T> paramzzbuo)
  {
    if (this.value != null)
    {
      if (!this.zzcsd.equals(paramzzbuo)) {
        throw new IllegalStateException("Tried to getExtension with a different Extension.");
      }
    }
    else
    {
      this.zzcsd = paramzzbuo;
      this.value = paramzzbuo.zzZ(this.zzcse);
      this.zzcse = null;
    }
    return (T)this.value;
  }
  
  int zzv()
  {
    int j;
    if (this.value != null)
    {
      j = this.zzcsd.zzaR(this.value);
      return j;
    }
    Iterator localIterator = this.zzcse.iterator();
    for (int i = 0;; i = ((zzbuv)localIterator.next()).zzv() + i)
    {
      j = i;
      if (!localIterator.hasNext()) {
        break;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbuq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */