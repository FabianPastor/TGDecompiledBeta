package com.google.android.gms.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

class zzarh
  implements Cloneable
{
  private zzarf<?, ?> bqB;
  private List<zzarm> bqC = new ArrayList();
  private Object value;
  
  private byte[] toByteArray()
    throws IOException
  {
    byte[] arrayOfByte = new byte[zzx()];
    zza(zzard.zzbe(arrayOfByte));
    return arrayOfByte;
  }
  
  public final zzarh cS()
  {
    zzarh localzzarh = new zzarh();
    try
    {
      localzzarh.bqB = this.bqB;
      if (this.bqC == null) {
        localzzarh.bqC = null;
      }
      while (this.value == null)
      {
        return localzzarh;
        localzzarh.bqC.addAll(this.bqC);
      }
      if (!(this.value instanceof zzark)) {
        break label93;
      }
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new AssertionError(localCloneNotSupportedException);
    }
    localCloneNotSupportedException.value = ((zzark)((zzark)this.value).clone());
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
    if ((this.value instanceof zzark[]))
    {
      localObject1 = (zzark[])this.value;
      localObject2 = new zzark[localObject1.length];
      localCloneNotSupportedException.value = localObject2;
      i = 0;
      while (i < localObject1.length)
      {
        localObject2[i] = ((zzark)localObject1[i].clone());
        i += 1;
      }
    }
    return localCloneNotSupportedException;
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
      } while (!(paramObject instanceof zzarh));
      paramObject = (zzarh)paramObject;
      if ((this.value == null) || (((zzarh)paramObject).value == null)) {
        break;
      }
      bool1 = bool2;
    } while (this.bqB != ((zzarh)paramObject).bqB);
    if (!this.bqB.bhd.isArray()) {
      return this.value.equals(((zzarh)paramObject).value);
    }
    if ((this.value instanceof byte[])) {
      return Arrays.equals((byte[])this.value, (byte[])((zzarh)paramObject).value);
    }
    if ((this.value instanceof int[])) {
      return Arrays.equals((int[])this.value, (int[])((zzarh)paramObject).value);
    }
    if ((this.value instanceof long[])) {
      return Arrays.equals((long[])this.value, (long[])((zzarh)paramObject).value);
    }
    if ((this.value instanceof float[])) {
      return Arrays.equals((float[])this.value, (float[])((zzarh)paramObject).value);
    }
    if ((this.value instanceof double[])) {
      return Arrays.equals((double[])this.value, (double[])((zzarh)paramObject).value);
    }
    if ((this.value instanceof boolean[])) {
      return Arrays.equals((boolean[])this.value, (boolean[])((zzarh)paramObject).value);
    }
    return Arrays.deepEquals((Object[])this.value, (Object[])((zzarh)paramObject).value);
    if ((this.bqC != null) && (((zzarh)paramObject).bqC != null)) {
      return this.bqC.equals(((zzarh)paramObject).bqC);
    }
    try
    {
      bool1 = Arrays.equals(toByteArray(), ((zzarh)paramObject).toByteArray());
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
  
  void zza(zzard paramzzard)
    throws IOException
  {
    if (this.value != null) {
      this.bqB.zza(this.value, paramzzard);
    }
    for (;;)
    {
      return;
      Iterator localIterator = this.bqC.iterator();
      while (localIterator.hasNext()) {
        ((zzarm)localIterator.next()).zza(paramzzard);
      }
    }
  }
  
  void zza(zzarm paramzzarm)
  {
    this.bqC.add(paramzzarm);
  }
  
  <T> T zzb(zzarf<?, T> paramzzarf)
  {
    if (this.value != null)
    {
      if (!this.bqB.equals(paramzzarf)) {
        throw new IllegalStateException("Tried to getExtension with a different Extension.");
      }
    }
    else
    {
      this.bqB = paramzzarf;
      this.value = paramzzarf.zzay(this.bqC);
      this.bqC = null;
    }
    return (T)this.value;
  }
  
  int zzx()
  {
    int j;
    if (this.value != null)
    {
      j = this.bqB.zzcu(this.value);
      return j;
    }
    Iterator localIterator = this.bqC.iterator();
    for (int i = 0;; i = ((zzarm)localIterator.next()).zzx() + i)
    {
      j = i;
      if (!localIterator.hasNext()) {
        break;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzarh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */