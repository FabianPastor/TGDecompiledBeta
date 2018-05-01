package com.google.android.gms.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

class zzarx
  implements Cloneable
{
  private zzarv<?, ?> btM;
  private List<zzasc> btN = new ArrayList();
  private Object value;
  
  private byte[] toByteArray()
    throws IOException
  {
    byte[] arrayOfByte = new byte[zzx()];
    zza(zzart.zzbe(arrayOfByte));
    return arrayOfByte;
  }
  
  public final zzarx cq()
  {
    zzarx localzzarx = new zzarx();
    try
    {
      localzzarx.btM = this.btM;
      if (this.btN == null) {
        localzzarx.btN = null;
      }
      while (this.value == null)
      {
        return localzzarx;
        localzzarx.btN.addAll(this.btN);
      }
      if (!(this.value instanceof zzasa)) {
        break label93;
      }
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new AssertionError(localCloneNotSupportedException);
    }
    localCloneNotSupportedException.value = ((zzasa)((zzasa)this.value).clone());
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
    if ((this.value instanceof zzasa[]))
    {
      localObject1 = (zzasa[])this.value;
      localObject2 = new zzasa[localObject1.length];
      localCloneNotSupportedException.value = localObject2;
      i = 0;
      while (i < localObject1.length)
      {
        localObject2[i] = ((zzasa)localObject1[i].clone());
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
      } while (!(paramObject instanceof zzarx));
      paramObject = (zzarx)paramObject;
      if ((this.value == null) || (((zzarx)paramObject).value == null)) {
        break;
      }
      bool1 = bool2;
    } while (this.btM != ((zzarx)paramObject).btM);
    if (!this.btM.bkp.isArray()) {
      return this.value.equals(((zzarx)paramObject).value);
    }
    if ((this.value instanceof byte[])) {
      return Arrays.equals((byte[])this.value, (byte[])((zzarx)paramObject).value);
    }
    if ((this.value instanceof int[])) {
      return Arrays.equals((int[])this.value, (int[])((zzarx)paramObject).value);
    }
    if ((this.value instanceof long[])) {
      return Arrays.equals((long[])this.value, (long[])((zzarx)paramObject).value);
    }
    if ((this.value instanceof float[])) {
      return Arrays.equals((float[])this.value, (float[])((zzarx)paramObject).value);
    }
    if ((this.value instanceof double[])) {
      return Arrays.equals((double[])this.value, (double[])((zzarx)paramObject).value);
    }
    if ((this.value instanceof boolean[])) {
      return Arrays.equals((boolean[])this.value, (boolean[])((zzarx)paramObject).value);
    }
    return Arrays.deepEquals((Object[])this.value, (Object[])((zzarx)paramObject).value);
    if ((this.btN != null) && (((zzarx)paramObject).btN != null)) {
      return this.btN.equals(((zzarx)paramObject).btN);
    }
    try
    {
      bool1 = Arrays.equals(toByteArray(), ((zzarx)paramObject).toByteArray());
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
  
  void zza(zzart paramzzart)
    throws IOException
  {
    if (this.value != null) {
      this.btM.zza(this.value, paramzzart);
    }
    for (;;)
    {
      return;
      Iterator localIterator = this.btN.iterator();
      while (localIterator.hasNext()) {
        ((zzasc)localIterator.next()).zza(paramzzart);
      }
    }
  }
  
  void zza(zzasc paramzzasc)
  {
    this.btN.add(paramzzasc);
  }
  
  <T> T zzb(zzarv<?, T> paramzzarv)
  {
    if (this.value != null)
    {
      if (!this.btM.equals(paramzzarv)) {
        throw new IllegalStateException("Tried to getExtension with a different Extension.");
      }
    }
    else
    {
      this.btM = paramzzarv;
      this.value = paramzzarv.zzay(this.btN);
      this.btN = null;
    }
    return (T)this.value;
  }
  
  int zzx()
  {
    int j;
    if (this.value != null)
    {
      j = this.btM.zzct(this.value);
      return j;
    }
    Iterator localIterator = this.btN.iterator();
    for (int i = 0;; i = ((zzasc)localIterator.next()).zzx() + i)
    {
      j = i;
      if (!localIterator.hasNext()) {
        break;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzarx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */