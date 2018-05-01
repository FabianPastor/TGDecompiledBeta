package com.google.android.gms.internal.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

final class zzbe
  implements Cloneable
{
  private Object value;
  private zzbc<?, ?> zzco;
  private List<zzbj> zzcp = new ArrayList();
  
  private final byte[] toByteArray()
    throws IOException
  {
    byte[] arrayOfByte = new byte[zzu()];
    zza(zzaz.zza(arrayOfByte));
    return arrayOfByte;
  }
  
  private final zzbe zzaf()
  {
    zzbe localzzbe = new zzbe();
    for (;;)
    {
      try
      {
        localzzbe.zzco = this.zzco;
        if (this.zzcp == null)
        {
          localzzbe.zzcp = null;
          if (this.value != null)
          {
            if ((this.value instanceof zzbh)) {
              localzzbe.value = ((zzbh)((zzbh)this.value).clone());
            }
          }
          else {
            return localzzbe;
          }
        }
        else
        {
          localzzbe.zzcp.addAll(this.zzcp);
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
      else if ((this.value instanceof zzbh[]))
      {
        localObject2 = (zzbh[])this.value;
        localObject1 = new zzbh[localObject2.length];
        localCloneNotSupportedException.value = localObject1;
        for (i = 0; i < localObject2.length; i++) {
          localObject1[i] = ((zzbh)localObject2[i].clone());
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
      if (!(paramObject instanceof zzbe)) {
        continue;
      }
      paramObject = (zzbe)paramObject;
      if ((this.value != null) && (((zzbe)paramObject).value != null))
      {
        bool2 = bool1;
        if (this.zzco != ((zzbe)paramObject).zzco) {
          continue;
        }
        if (!this.zzco.zzcj.isArray())
        {
          bool2 = this.value.equals(((zzbe)paramObject).value);
          continue;
        }
        if ((this.value instanceof byte[]))
        {
          bool2 = Arrays.equals((byte[])this.value, (byte[])((zzbe)paramObject).value);
          continue;
        }
        if ((this.value instanceof int[]))
        {
          bool2 = Arrays.equals((int[])this.value, (int[])((zzbe)paramObject).value);
          continue;
        }
        if ((this.value instanceof long[]))
        {
          bool2 = Arrays.equals((long[])this.value, (long[])((zzbe)paramObject).value);
          continue;
        }
        if ((this.value instanceof float[]))
        {
          bool2 = Arrays.equals((float[])this.value, (float[])((zzbe)paramObject).value);
          continue;
        }
        if ((this.value instanceof double[]))
        {
          bool2 = Arrays.equals((double[])this.value, (double[])((zzbe)paramObject).value);
          continue;
        }
        if ((this.value instanceof boolean[]))
        {
          bool2 = Arrays.equals((boolean[])this.value, (boolean[])((zzbe)paramObject).value);
          continue;
        }
        bool2 = Arrays.deepEquals((Object[])this.value, (Object[])((zzbe)paramObject).value);
        continue;
      }
      if ((this.zzcp != null) && (((zzbe)paramObject).zzcp != null))
      {
        bool2 = this.zzcp.equals(((zzbe)paramObject).zzcp);
        continue;
      }
      try
      {
        bool2 = Arrays.equals(toByteArray(), ((zzbe)paramObject).toByteArray());
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
  
  final void zza(zzaz paramzzaz)
    throws IOException
  {
    if (this.value != null) {
      throw new NoSuchMethodError();
    }
    Iterator localIterator = this.zzcp.iterator();
    while (localIterator.hasNext())
    {
      zzbj localzzbj = (zzbj)localIterator.next();
      paramzzaz.zzm(localzzbj.tag);
      paramzzaz.zzc(localzzbj.zzcs);
    }
  }
  
  final void zza(zzbj paramzzbj)
    throws IOException
  {
    if (this.zzcp != null) {
      this.zzcp.add(paramzzbj);
    }
    for (;;)
    {
      return;
      if (!(this.value instanceof zzbh)) {
        break;
      }
      paramzzbj = paramzzbj.zzcs;
      zzay localzzay = zzay.zza(paramzzbj, 0, paramzzbj.length);
      int i = localzzay.zzz();
      if (i != paramzzbj.length - zzaz.zzj(i)) {
        throw zzbg.zzag();
      }
      paramzzbj = ((zzbh)this.value).zza(localzzay);
      this.zzco = this.zzco;
      this.value = paramzzbj;
      this.zzcp = null;
    }
    if ((this.value instanceof zzbh[]))
    {
      Collections.singletonList(paramzzbj);
      throw new NoSuchMethodError();
    }
    Collections.singletonList(paramzzbj);
    throw new NoSuchMethodError();
  }
  
  final int zzu()
  {
    if (this.value != null) {
      throw new NoSuchMethodError();
    }
    Iterator localIterator = this.zzcp.iterator();
    zzbj localzzbj;
    int j;
    for (int i = 0; localIterator.hasNext(); i = localzzbj.zzcs.length + (j + 0) + i)
    {
      localzzbj = (zzbj)localIterator.next();
      j = zzaz.zzn(localzzbj.tag);
    }
    return i;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/config/zzbe.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */