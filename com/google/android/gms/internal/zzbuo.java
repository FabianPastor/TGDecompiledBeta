package com.google.android.gms.internal;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class zzbuo<M extends zzbun<M>, T>
{
  public final int tag;
  protected final int type;
  protected final Class<T> zzciF;
  protected final boolean zzcrY;
  
  private zzbuo(int paramInt1, Class<T> paramClass, int paramInt2, boolean paramBoolean)
  {
    this.type = paramInt1;
    this.zzciF = paramClass;
    this.tag = paramInt2;
    this.zzcrY = paramBoolean;
  }
  
  public static <M extends zzbun<M>, T extends zzbut> zzbuo<M, T> zza(int paramInt, Class<T> paramClass, long paramLong)
  {
    return new zzbuo(paramInt, paramClass, (int)paramLong, false);
  }
  
  private T zzaa(List<zzbuv> paramList)
  {
    int j = 0;
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    while (i < paramList.size())
    {
      localObject = (zzbuv)paramList.get(i);
      if (((zzbuv)localObject).zzcsh.length != 0) {
        zza((zzbuv)localObject, localArrayList);
      }
      i += 1;
    }
    int k = localArrayList.size();
    if (k == 0)
    {
      paramList = null;
      return paramList;
    }
    Object localObject = this.zzciF.cast(Array.newInstance(this.zzciF.getComponentType(), k));
    i = j;
    for (;;)
    {
      paramList = (List<zzbuv>)localObject;
      if (i >= k) {
        break;
      }
      Array.set(localObject, i, localArrayList.get(i));
      i += 1;
    }
  }
  
  private T zzab(List<zzbuv> paramList)
  {
    if (paramList.isEmpty()) {
      return null;
    }
    paramList = (zzbuv)paramList.get(paramList.size() - 1);
    return (T)this.zzciF.cast(zzaM(zzbul.zzad(paramList.zzcsh)));
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {}
    do
    {
      return true;
      if (!(paramObject instanceof zzbuo)) {
        return false;
      }
      paramObject = (zzbuo)paramObject;
    } while ((this.type == ((zzbuo)paramObject).type) && (this.zzciF == ((zzbuo)paramObject).zzciF) && (this.tag == ((zzbuo)paramObject).tag) && (this.zzcrY == ((zzbuo)paramObject).zzcrY));
    return false;
  }
  
  public int hashCode()
  {
    int j = this.type;
    int k = this.zzciF.hashCode();
    int m = this.tag;
    if (this.zzcrY) {}
    for (int i = 1;; i = 0) {
      return i + (((j + 1147) * 31 + k) * 31 + m) * 31;
    }
  }
  
  final T zzZ(List<zzbuv> paramList)
  {
    if (paramList == null) {
      return null;
    }
    if (this.zzcrY) {
      return (T)zzaa(paramList);
    }
    return (T)zzab(paramList);
  }
  
  protected void zza(zzbuv paramzzbuv, List<Object> paramList)
  {
    paramList.add(zzaM(zzbul.zzad(paramzzbuv.zzcsh)));
  }
  
  void zza(Object paramObject, zzbum paramzzbum)
    throws IOException
  {
    if (this.zzcrY)
    {
      zzc(paramObject, paramzzbum);
      return;
    }
    zzb(paramObject, paramzzbum);
  }
  
  protected Object zzaM(zzbul paramzzbul)
  {
    Object localObject;
    if (this.zzcrY) {
      localObject = this.zzciF.getComponentType();
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
      catch (InstantiationException paramzzbul)
      {
        localObject = String.valueOf(localObject);
        throw new IllegalArgumentException(String.valueOf(localObject).length() + 33 + "Error creating instance of class " + (String)localObject, paramzzbul);
        localObject = this.zzciF;
        continue;
        zzbut localzzbut = (zzbut)((Class)localObject).newInstance();
        paramzzbul.zza(localzzbut, zzbuw.zzqB(this.tag));
        return localzzbut;
        localzzbut = (zzbut)((Class)localObject).newInstance();
        paramzzbul.zza(localzzbut);
        return localzzbut;
      }
      catch (IllegalAccessException paramzzbul)
      {
        localObject = String.valueOf(localObject);
        throw new IllegalArgumentException(String.valueOf(localObject).length() + 33 + "Error creating instance of class " + (String)localObject, paramzzbul);
      }
      catch (IOException paramzzbul)
      {
        throw new IllegalArgumentException("Error reading extension field", paramzzbul);
      }
    }
  }
  
  int zzaR(Object paramObject)
  {
    if (this.zzcrY) {
      return zzaS(paramObject);
    }
    return zzaT(paramObject);
  }
  
  protected int zzaS(Object paramObject)
  {
    int j = 0;
    int m = Array.getLength(paramObject);
    int i = 0;
    while (i < m)
    {
      int k = j;
      if (Array.get(paramObject, i) != null) {
        k = j + zzaT(Array.get(paramObject, i));
      }
      i += 1;
      j = k;
    }
    return j;
  }
  
  protected int zzaT(Object paramObject)
  {
    int i = zzbuw.zzqB(this.tag);
    switch (this.type)
    {
    default: 
      i = this.type;
      throw new IllegalArgumentException(24 + "Unknown type " + i);
    case 10: 
      return zzbum.zzb(i, (zzbut)paramObject);
    }
    return zzbum.zzc(i, (zzbut)paramObject);
  }
  
  protected void zzb(Object paramObject, zzbum paramzzbum)
  {
    for (;;)
    {
      try
      {
        paramzzbum.zzqt(this.tag);
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
      paramObject = (zzbut)paramObject;
      int i = zzbuw.zzqB(this.tag);
      paramzzbum.zzb((zzbut)paramObject);
      paramzzbum.zzJ(i, 4);
      return;
      paramzzbum.zzc((zzbut)paramObject);
      return;
    }
  }
  
  protected void zzc(Object paramObject, zzbum paramzzbum)
  {
    int j = Array.getLength(paramObject);
    int i = 0;
    while (i < j)
    {
      Object localObject = Array.get(paramObject, i);
      if (localObject != null) {
        zzb(localObject, paramzzbum);
      }
      i += 1;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbuo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */