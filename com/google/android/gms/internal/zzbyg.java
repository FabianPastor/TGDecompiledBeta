package com.google.android.gms.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

class zzbyg
  implements Cloneable
{
  private Object value;
  private zzbye<?, ?> zzcwI;
  private List<zzbyl> zzcwJ = new ArrayList();
  
  private byte[] toByteArray()
    throws IOException
  {
    byte[] arrayOfByte = new byte[zzu()];
    zza(zzbyc.zzah(arrayOfByte));
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
      } while (!(paramObject instanceof zzbyg));
      paramObject = (zzbyg)paramObject;
      if ((this.value == null) || (((zzbyg)paramObject).value == null)) {
        break;
      }
      bool1 = bool2;
    } while (this.zzcwI != ((zzbyg)paramObject).zzcwI);
    if (!this.zzcwI.zzckL.isArray()) {
      return this.value.equals(((zzbyg)paramObject).value);
    }
    if ((this.value instanceof byte[])) {
      return Arrays.equals((byte[])this.value, (byte[])((zzbyg)paramObject).value);
    }
    if ((this.value instanceof int[])) {
      return Arrays.equals((int[])this.value, (int[])((zzbyg)paramObject).value);
    }
    if ((this.value instanceof long[])) {
      return Arrays.equals((long[])this.value, (long[])((zzbyg)paramObject).value);
    }
    if ((this.value instanceof float[])) {
      return Arrays.equals((float[])this.value, (float[])((zzbyg)paramObject).value);
    }
    if ((this.value instanceof double[])) {
      return Arrays.equals((double[])this.value, (double[])((zzbyg)paramObject).value);
    }
    if ((this.value instanceof boolean[])) {
      return Arrays.equals((boolean[])this.value, (boolean[])((zzbyg)paramObject).value);
    }
    return Arrays.deepEquals((Object[])this.value, (Object[])((zzbyg)paramObject).value);
    if ((this.zzcwJ != null) && (((zzbyg)paramObject).zzcwJ != null)) {
      return this.zzcwJ.equals(((zzbyg)paramObject).zzcwJ);
    }
    try
    {
      bool1 = Arrays.equals(toByteArray(), ((zzbyg)paramObject).toByteArray());
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
  
  void zza(zzbyc paramzzbyc)
    throws IOException
  {
    if (this.value != null) {
      this.zzcwI.zza(this.value, paramzzbyc);
    }
    for (;;)
    {
      return;
      Iterator localIterator = this.zzcwJ.iterator();
      while (localIterator.hasNext()) {
        ((zzbyl)localIterator.next()).zza(paramzzbyc);
      }
    }
  }
  
  void zza(zzbyl paramzzbyl)
  {
    this.zzcwJ.add(paramzzbyl);
  }
  
  public final zzbyg zzafs()
  {
    zzbyg localzzbyg = new zzbyg();
    try
    {
      localzzbyg.zzcwI = this.zzcwI;
      if (this.zzcwJ == null) {
        localzzbyg.zzcwJ = null;
      }
      while (this.value == null)
      {
        return localzzbyg;
        localzzbyg.zzcwJ.addAll(this.zzcwJ);
      }
      if (!(this.value instanceof zzbyj)) {
        break label93;
      }
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new AssertionError(localCloneNotSupportedException);
    }
    localCloneNotSupportedException.value = ((zzbyj)((zzbyj)this.value).clone());
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
    if ((this.value instanceof zzbyj[]))
    {
      localObject1 = (zzbyj[])this.value;
      localObject2 = new zzbyj[localObject1.length];
      localCloneNotSupportedException.value = localObject2;
      i = 0;
      while (i < localObject1.length)
      {
        localObject2[i] = ((zzbyj)localObject1[i].clone());
        i += 1;
      }
    }
    return localCloneNotSupportedException;
  }
  
  <T> T zzb(zzbye<?, T> paramzzbye)
  {
    if (this.value != null)
    {
      if (!this.zzcwI.equals(paramzzbye)) {
        throw new IllegalStateException("Tried to getExtension with a different Extension.");
      }
    }
    else
    {
      this.zzcwI = paramzzbye;
      this.value = paramzzbye.zzad(this.zzcwJ);
      this.zzcwJ = null;
    }
    return (T)this.value;
  }
  
  int zzu()
  {
    int j;
    if (this.value != null)
    {
      j = this.zzcwI.zzaV(this.value);
      return j;
    }
    Iterator localIterator = this.zzcwJ.iterator();
    for (int i = 0;; i = ((zzbyl)localIterator.next()).zzu() + i)
    {
      j = i;
      if (!localIterator.hasNext()) {
        break;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbyg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */