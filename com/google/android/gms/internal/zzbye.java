package com.google.android.gms.internal;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class zzbye<M extends zzbyd<M>, T>
{
  public final int tag;
  protected final int type;
  protected final Class<T> zzckL;
  protected final boolean zzcwD;
  
  private zzbye(int paramInt1, Class<T> paramClass, int paramInt2, boolean paramBoolean)
  {
    this.type = paramInt1;
    this.zzckL = paramClass;
    this.tag = paramInt2;
    this.zzcwD = paramBoolean;
  }
  
  public static <M extends zzbyd<M>, T extends zzbyj> zzbye<M, T> zza(int paramInt, Class<T> paramClass, long paramLong)
  {
    return new zzbye(paramInt, paramClass, (int)paramLong, false);
  }
  
  private T zzae(List<zzbyl> paramList)
  {
    int j = 0;
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    while (i < paramList.size())
    {
      localObject = (zzbyl)paramList.get(i);
      if (((zzbyl)localObject).zzbyc.length != 0) {
        zza((zzbyl)localObject, localArrayList);
      }
      i += 1;
    }
    int k = localArrayList.size();
    if (k == 0)
    {
      paramList = null;
      return paramList;
    }
    Object localObject = this.zzckL.cast(Array.newInstance(this.zzckL.getComponentType(), k));
    i = j;
    for (;;)
    {
      paramList = (List<zzbyl>)localObject;
      if (i >= k) {
        break;
      }
      Array.set(localObject, i, localArrayList.get(i));
      i += 1;
    }
  }
  
  private T zzaf(List<zzbyl> paramList)
  {
    if (paramList.isEmpty()) {
      return null;
    }
    paramList = (zzbyl)paramList.get(paramList.size() - 1);
    return (T)this.zzckL.cast(zzaU(zzbyb.zzag(paramList.zzbyc)));
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {}
    do
    {
      return true;
      if (!(paramObject instanceof zzbye)) {
        return false;
      }
      paramObject = (zzbye)paramObject;
    } while ((this.type == ((zzbye)paramObject).type) && (this.zzckL == ((zzbye)paramObject).zzckL) && (this.tag == ((zzbye)paramObject).tag) && (this.zzcwD == ((zzbye)paramObject).zzcwD));
    return false;
  }
  
  public int hashCode()
  {
    int j = this.type;
    int k = this.zzckL.hashCode();
    int m = this.tag;
    if (this.zzcwD) {}
    for (int i = 1;; i = 0) {
      return i + (((j + 1147) * 31 + k) * 31 + m) * 31;
    }
  }
  
  protected void zza(zzbyl paramzzbyl, List<Object> paramList)
  {
    paramList.add(zzaU(zzbyb.zzag(paramzzbyl.zzbyc)));
  }
  
  void zza(Object paramObject, zzbyc paramzzbyc)
    throws IOException
  {
    if (this.zzcwD)
    {
      zzc(paramObject, paramzzbyc);
      return;
    }
    zzb(paramObject, paramzzbyc);
  }
  
  protected Object zzaU(zzbyb paramzzbyb)
  {
    Object localObject;
    if (this.zzcwD) {
      localObject = this.zzckL.getComponentType();
    }
    for (;;)
    {
      try
      {
        switch (this.type)
        {
        case 10: 
          int i = this.type;
          throw new IllegalArgumentException(24 + "Unknown type " + i);
        }
      }
      catch (InstantiationException paramzzbyb)
      {
        localObject = String.valueOf(localObject);
        throw new IllegalArgumentException(String.valueOf(localObject).length() + 33 + "Error creating instance of class " + (String)localObject, paramzzbyb);
        localObject = this.zzckL;
        continue;
        zzbyj localzzbyj = (zzbyj)((Class)localObject).newInstance();
        paramzzbyb.zza(localzzbyj, zzbym.zzrx(this.tag));
        return localzzbyj;
        localzzbyj = (zzbyj)((Class)localObject).newInstance();
        paramzzbyb.zza(localzzbyj);
        return localzzbyj;
      }
      catch (IllegalAccessException paramzzbyb)
      {
        localObject = String.valueOf(localObject);
        throw new IllegalArgumentException(String.valueOf(localObject).length() + 33 + "Error creating instance of class " + (String)localObject, paramzzbyb);
      }
      catch (IOException paramzzbyb)
      {
        throw new IllegalArgumentException("Error reading extension field", paramzzbyb);
      }
    }
  }
  
  int zzaV(Object paramObject)
  {
    if (this.zzcwD) {
      return zzaW(paramObject);
    }
    return zzaX(paramObject);
  }
  
  protected int zzaW(Object paramObject)
  {
    int j = 0;
    int m = Array.getLength(paramObject);
    int i = 0;
    while (i < m)
    {
      int k = j;
      if (Array.get(paramObject, i) != null) {
        k = j + zzaX(Array.get(paramObject, i));
      }
      i += 1;
      j = k;
    }
    return j;
  }
  
  protected int zzaX(Object paramObject)
  {
    int i = zzbym.zzrx(this.tag);
    switch (this.type)
    {
    default: 
      i = this.type;
      throw new IllegalArgumentException(24 + "Unknown type " + i);
    case 10: 
      return zzbyc.zzb(i, (zzbyj)paramObject);
    }
    return zzbyc.zzc(i, (zzbyj)paramObject);
  }
  
  final T zzad(List<zzbyl> paramList)
  {
    if (paramList == null) {
      return null;
    }
    if (this.zzcwD) {
      return (T)zzae(paramList);
    }
    return (T)zzaf(paramList);
  }
  
  protected void zzb(Object paramObject, zzbyc paramzzbyc)
  {
    for (;;)
    {
      try
      {
        paramzzbyc.zzrp(this.tag);
        switch (this.type)
        {
        case 10: 
          i = this.type;
          throw new IllegalArgumentException(24 + "Unknown type " + i);
        }
      }
      catch (IOException paramObject)
      {
        throw new IllegalStateException((Throwable)paramObject);
      }
      paramObject = (zzbyj)paramObject;
      int i = zzbym.zzrx(this.tag);
      paramzzbyc.zzb((zzbyj)paramObject);
      paramzzbyc.zzN(i, 4);
      return;
      paramzzbyc.zzc((zzbyj)paramObject);
      return;
    }
  }
  
  protected void zzc(Object paramObject, zzbyc paramzzbyc)
  {
    int j = Array.getLength(paramObject);
    int i = 0;
    while (i < j)
    {
      Object localObject = Array.get(paramObject, i);
      if (localObject != null) {
        zzb(localObject, paramzzbyc);
      }
      i += 1;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbye.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */