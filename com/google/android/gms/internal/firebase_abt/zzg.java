package com.google.android.gms.internal.firebase_abt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

final class zzg
  implements Cloneable
{
  private Object value;
  private zze<?, ?> zzy;
  private List<zzl> zzz = new ArrayList();
  
  private final byte[] toByteArray()
    throws IOException
  {
    if (this.value != null) {
      throw new NoSuchMethodError();
    }
    Object localObject1 = this.zzz.iterator();
    int j;
    for (int i = 0; ((Iterator)localObject1).hasNext(); i = ((zzl)localObject2).zzac.length + (j + 0) + i)
    {
      localObject2 = (zzl)((Iterator)localObject1).next();
      j = zzb.zzf(((zzl)localObject2).tag);
    }
    byte[] arrayOfByte = new byte[i];
    zzb localzzb = zzb.zzb(arrayOfByte);
    if (this.value != null) {
      throw new NoSuchMethodError();
    }
    Object localObject2 = this.zzz.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      localObject1 = (zzl)((Iterator)localObject2).next();
      localzzb.zze(((zzl)localObject1).tag);
      localzzb.zzc(((zzl)localObject1).zzac);
    }
    return arrayOfByte;
  }
  
  private final zzg zzk()
  {
    zzg localzzg = new zzg();
    for (;;)
    {
      try
      {
        localzzg.zzy = this.zzy;
        if (this.zzz == null)
        {
          localzzg.zzz = null;
          if (this.value != null)
          {
            if ((this.value instanceof zzj)) {
              localzzg.value = ((zzj)((zzj)this.value).clone());
            }
          }
          else {
            return localzzg;
          }
        }
        else
        {
          localzzg.zzz.addAll(this.zzz);
          continue;
        }
        if (!(this.value instanceof byte[])) {
          break label118;
        }
      }
      catch (CloneNotSupportedException localCloneNotSupportedException)
      {
        throw new AssertionError(localCloneNotSupportedException);
      }
      localCloneNotSupportedException.value = ((byte[])this.value).clone();
      continue;
      label118:
      Object localObject1;
      Object localObject2;
      int i;
      if ((this.value instanceof byte[][]))
      {
        localObject1 = (byte[][])this.value;
        localObject2 = new byte[localObject1.length][];
        localCloneNotSupportedException.value = localObject2;
        for (i = 0; i < localObject1.length; i++) {
          localObject2[i] = ((byte[])localObject1[i].clone());
        }
      }
      else if ((this.value instanceof boolean[]))
      {
        localCloneNotSupportedException.value = ((boolean[])this.value).clone();
      }
      else if ((this.value instanceof int[]))
      {
        localCloneNotSupportedException.value = ((int[])this.value).clone();
      }
      else if ((this.value instanceof long[]))
      {
        localCloneNotSupportedException.value = ((long[])this.value).clone();
      }
      else if ((this.value instanceof float[]))
      {
        localCloneNotSupportedException.value = ((float[])this.value).clone();
      }
      else if ((this.value instanceof double[]))
      {
        localCloneNotSupportedException.value = ((double[])this.value).clone();
      }
      else if ((this.value instanceof zzj[]))
      {
        localObject2 = (zzj[])this.value;
        localObject1 = new zzj[localObject2.length];
        localCloneNotSupportedException.value = localObject1;
        for (i = 0; i < localObject2.length; i++) {
          localObject1[i] = ((zzj)localObject2[i].clone());
        }
      }
    }
  }
  
  public final boolean equals(Object paramObject)
  {
    boolean bool1 = false;
    boolean bool2;
    if (paramObject == this) {
      bool2 = true;
    }
    for (;;)
    {
      return bool2;
      bool2 = bool1;
      if (!(paramObject instanceof zzg)) {
        continue;
      }
      paramObject = (zzg)paramObject;
      if ((this.value != null) && (((zzg)paramObject).value != null))
      {
        bool2 = bool1;
        if (this.zzy != ((zzg)paramObject).zzy) {
          continue;
        }
        if (!this.zzy.zzt.isArray())
        {
          bool2 = this.value.equals(((zzg)paramObject).value);
          continue;
        }
        if ((this.value instanceof byte[]))
        {
          bool2 = Arrays.equals((byte[])this.value, (byte[])((zzg)paramObject).value);
          continue;
        }
        if ((this.value instanceof int[]))
        {
          bool2 = Arrays.equals((int[])this.value, (int[])((zzg)paramObject).value);
          continue;
        }
        if ((this.value instanceof long[]))
        {
          bool2 = Arrays.equals((long[])this.value, (long[])((zzg)paramObject).value);
          continue;
        }
        if ((this.value instanceof float[]))
        {
          bool2 = Arrays.equals((float[])this.value, (float[])((zzg)paramObject).value);
          continue;
        }
        if ((this.value instanceof double[]))
        {
          bool2 = Arrays.equals((double[])this.value, (double[])((zzg)paramObject).value);
          continue;
        }
        if ((this.value instanceof boolean[]))
        {
          bool2 = Arrays.equals((boolean[])this.value, (boolean[])((zzg)paramObject).value);
          continue;
        }
        bool2 = Arrays.deepEquals((Object[])this.value, (Object[])((zzg)paramObject).value);
        continue;
      }
      if ((this.zzz != null) && (((zzg)paramObject).zzz != null))
      {
        bool2 = this.zzz.equals(((zzg)paramObject).zzz);
        continue;
      }
      try
      {
        bool2 = Arrays.equals(toByteArray(), ((zzg)paramObject).toByteArray());
      }
      catch (IOException paramObject)
      {
        throw new IllegalStateException((Throwable)paramObject);
      }
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
  
  final void zza(zzl paramzzl)
    throws IOException
  {
    if (this.zzz != null) {
      this.zzz.add(paramzzl);
    }
    for (;;)
    {
      return;
      if (!(this.value instanceof zzj)) {
        break;
      }
      byte[] arrayOfByte = paramzzl.zzac;
      paramzzl = zza.zza(arrayOfByte, 0, arrayOfByte.length);
      int i = paramzzl.zzg();
      int j = arrayOfByte.length;
      if (i >= 0) {}
      for (int k = zzb.zzf(i); i != j - k; k = 10) {
        throw zzi.zzl();
      }
      paramzzl = ((zzj)this.value).zza(paramzzl);
      this.zzy = this.zzy;
      this.value = paramzzl;
      this.zzz = null;
    }
    if ((this.value instanceof zzj[]))
    {
      Collections.singletonList(paramzzl);
      throw new NoSuchMethodError();
    }
    Collections.singletonList(paramzzl);
    throw new NoSuchMethodError();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/firebase_abt/zzg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */