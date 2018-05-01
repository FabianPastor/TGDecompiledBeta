package com.google.android.gms.internal.measurement;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

final class zzabg
  implements Cloneable
{
  private Object value;
  private zzabe<?, ?> zzbzp;
  private List<zzabl> zzbzq = new ArrayList();
  
  private final byte[] toByteArray()
    throws IOException
  {
    byte[] arrayOfByte = new byte[zza()];
    zza(zzabb.zzk(arrayOfByte));
    return arrayOfByte;
  }
  
  private final zzabg zzwa()
  {
    zzabg localzzabg = new zzabg();
    for (;;)
    {
      try
      {
        localzzabg.zzbzp = this.zzbzp;
        if (this.zzbzq == null)
        {
          localzzabg.zzbzq = null;
          if (this.value != null)
          {
            if ((this.value instanceof zzabj)) {
              localzzabg.value = ((zzabj)((zzabj)this.value).clone());
            }
          }
          else {
            return localzzabg;
          }
        }
        else
        {
          localzzabg.zzbzq.addAll(this.zzbzq);
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
      else if ((this.value instanceof zzabj[]))
      {
        localObject2 = (zzabj[])this.value;
        localObject1 = new zzabj[localObject2.length];
        localCloneNotSupportedException.value = localObject1;
        for (i = 0; i < localObject2.length; i++) {
          localObject1[i] = ((zzabj)localObject2[i].clone());
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
      if (!(paramObject instanceof zzabg)) {
        continue;
      }
      paramObject = (zzabg)paramObject;
      if ((this.value != null) && (((zzabg)paramObject).value != null))
      {
        bool2 = bool1;
        if (this.zzbzp != ((zzabg)paramObject).zzbzp) {
          continue;
        }
        if (!this.zzbzp.zzbzi.isArray())
        {
          bool2 = this.value.equals(((zzabg)paramObject).value);
          continue;
        }
        if ((this.value instanceof byte[]))
        {
          bool2 = Arrays.equals((byte[])this.value, (byte[])((zzabg)paramObject).value);
          continue;
        }
        if ((this.value instanceof int[]))
        {
          bool2 = Arrays.equals((int[])this.value, (int[])((zzabg)paramObject).value);
          continue;
        }
        if ((this.value instanceof long[]))
        {
          bool2 = Arrays.equals((long[])this.value, (long[])((zzabg)paramObject).value);
          continue;
        }
        if ((this.value instanceof float[]))
        {
          bool2 = Arrays.equals((float[])this.value, (float[])((zzabg)paramObject).value);
          continue;
        }
        if ((this.value instanceof double[]))
        {
          bool2 = Arrays.equals((double[])this.value, (double[])((zzabg)paramObject).value);
          continue;
        }
        if ((this.value instanceof boolean[]))
        {
          bool2 = Arrays.equals((boolean[])this.value, (boolean[])((zzabg)paramObject).value);
          continue;
        }
        bool2 = Arrays.deepEquals((Object[])this.value, (Object[])((zzabg)paramObject).value);
        continue;
      }
      if ((this.zzbzq != null) && (((zzabg)paramObject).zzbzq != null))
      {
        bool2 = this.zzbzq.equals(((zzabg)paramObject).zzbzq);
        continue;
      }
      try
      {
        bool2 = Arrays.equals(toByteArray(), ((zzabg)paramObject).toByteArray());
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
  
  final int zza()
  {
    int i = 0;
    Object localObject1;
    Object localObject2;
    if (this.value != null)
    {
      localObject1 = this.zzbzp;
      localObject2 = this.value;
      if (((zzabe)localObject1).zzbzj)
      {
        int j = Array.getLength(localObject2);
        int k = 0;
        for (i = 0;; i = m)
        {
          m = i;
          if (k >= j) {
            break;
          }
          m = i;
          if (Array.get(localObject2, k) != null) {
            m = i + ((zzabe)localObject1).zzx(Array.get(localObject2, k));
          }
          k++;
        }
      }
    }
    for (int m = ((zzabe)localObject1).zzx(localObject2);; m = i)
    {
      return m;
      localObject1 = this.zzbzq.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (zzabl)((Iterator)localObject1).next();
        m = zzabb.zzau(((zzabl)localObject2).tag);
        i = ((zzabl)localObject2).zzbto.length + (m + 0) + i;
      }
    }
  }
  
  final void zza(zzabb paramzzabb)
    throws IOException
  {
    Object localObject1;
    Object localObject2;
    if (this.value != null)
    {
      zzabe localzzabe = this.zzbzp;
      localObject1 = this.value;
      if (localzzabe.zzbzj)
      {
        int i = Array.getLength(localObject1);
        for (int j = 0; j < i; j++)
        {
          localObject2 = Array.get(localObject1, j);
          if (localObject2 != null) {
            localzzabe.zza(localObject2, paramzzabb);
          }
        }
      }
      localzzabe.zza(localObject1, paramzzabb);
    }
    for (;;)
    {
      return;
      localObject1 = this.zzbzq.iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (zzabl)((Iterator)localObject1).next();
        paramzzabb.zzat(((zzabl)localObject2).tag);
        paramzzabb.zzl(((zzabl)localObject2).zzbto);
      }
    }
  }
  
  final void zza(zzabl paramzzabl)
    throws IOException
  {
    if (this.zzbzq != null)
    {
      this.zzbzq.add(paramzzabl);
      return;
    }
    Object localObject;
    if ((this.value instanceof zzabj))
    {
      paramzzabl = paramzzabl.zzbto;
      localObject = zzaba.zza(paramzzabl, 0, paramzzabl.length);
      int i = ((zzaba)localObject).zzvs();
      if (i != paramzzabl.length - zzabb.zzaq(i)) {
        throw zzabi.zzwb();
      }
      paramzzabl = ((zzabj)this.value).zzb((zzaba)localObject);
    }
    for (;;)
    {
      this.zzbzp = this.zzbzp;
      this.value = paramzzabl;
      this.zzbzq = null;
      break;
      if ((this.value instanceof zzabj[]))
      {
        localObject = (zzabj[])this.zzbzp.zzi(Collections.singletonList(paramzzabl));
        zzabj[] arrayOfzzabj = (zzabj[])this.value;
        paramzzabl = (zzabj[])Arrays.copyOf(arrayOfzzabj, arrayOfzzabj.length + localObject.length);
        System.arraycopy(localObject, 0, paramzzabl, arrayOfzzabj.length, localObject.length);
      }
      else
      {
        paramzzabl = this.zzbzp.zzi(Collections.singletonList(paramzzabl));
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzabg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */