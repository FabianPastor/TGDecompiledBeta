package com.google.android.gms.internal.measurement;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public final class zzabe<M extends zzabd<M>, T>
{
  public final int tag;
  private final int type;
  protected final Class<T> zzbzi;
  protected final boolean zzbzj;
  
  private final Object zzf(zzaba paramzzaba)
  {
    Object localObject1;
    if (this.zzbzj) {
      localObject1 = this.zzbzi.getComponentType();
    }
    try
    {
      switch (this.type)
      {
      default: 
        localObject2 = new java/lang/IllegalArgumentException;
        int i = this.type;
        paramzzaba = new java/lang/StringBuilder;
        paramzzaba.<init>(24);
        ((IllegalArgumentException)localObject2).<init>("Unknown type " + i);
        throw ((Throwable)localObject2);
      }
    }
    catch (InstantiationException paramzzaba)
    {
      for (;;)
      {
        localObject1 = String.valueOf(localObject1);
        throw new IllegalArgumentException(String.valueOf(localObject1).length() + 33 + "Error creating instance of class " + (String)localObject1, paramzzaba);
        localObject1 = this.zzbzi;
      }
      Object localObject2 = (zzabj)((Class)localObject1).newInstance();
      paramzzaba.zza((zzabj)localObject2, this.tag >>> 3);
      for (paramzzaba = (zzaba)localObject2;; paramzzaba = (zzaba)localObject2)
      {
        return paramzzaba;
        localObject2 = (zzabj)((Class)localObject1).newInstance();
        paramzzaba.zza((zzabj)localObject2);
      }
    }
    catch (IllegalAccessException paramzzaba)
    {
      localObject1 = String.valueOf(localObject1);
      throw new IllegalArgumentException(String.valueOf(localObject1).length() + 33 + "Error creating instance of class " + (String)localObject1, paramzzaba);
    }
    catch (IOException paramzzaba)
    {
      throw new IllegalArgumentException("Error reading extension field", paramzzaba);
    }
  }
  
  public final boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (this == paramObject) {}
    for (;;)
    {
      return bool;
      if (!(paramObject instanceof zzabe))
      {
        bool = false;
      }
      else
      {
        paramObject = (zzabe)paramObject;
        if ((this.type != ((zzabe)paramObject).type) || (this.zzbzi != ((zzabe)paramObject).zzbzi) || (this.tag != ((zzabe)paramObject).tag) || (this.zzbzj != ((zzabe)paramObject).zzbzj)) {
          bool = false;
        }
      }
    }
  }
  
  public final int hashCode()
  {
    int i = this.type;
    int j = this.zzbzi.hashCode();
    int k = this.tag;
    if (this.zzbzj) {}
    for (int m = 1;; m = 0) {
      return m + (((i + 1147) * 31 + j) * 31 + k) * 31;
    }
  }
  
  protected final void zza(Object paramObject, zzabb paramzzabb)
  {
    try
    {
      paramzzabb.zzat(this.tag);
      switch (this.type)
      {
      default: 
        paramzzabb = new java/lang/IllegalArgumentException;
        i = this.type;
        paramObject = new java/lang/StringBuilder;
        ((StringBuilder)paramObject).<init>(24);
        paramzzabb.<init>("Unknown type " + i);
        throw paramzzabb;
      }
    }
    catch (IOException paramObject)
    {
      throw new IllegalStateException((Throwable)paramObject);
    }
    int i = this.tag;
    ((zzabj)paramObject).zza(paramzzabb);
    paramzzabb.zzg(i >>> 3, 4);
    for (;;)
    {
      return;
      paramzzabb.zzb((zzabj)paramObject);
    }
  }
  
  final T zzi(List<zzabl> paramList)
  {
    int i = 0;
    if (paramList == null) {
      paramList = null;
    }
    for (;;)
    {
      return paramList;
      if (this.zzbzj)
      {
        ArrayList localArrayList = new ArrayList();
        Object localObject;
        for (int j = 0; j < paramList.size(); j++)
        {
          localObject = (zzabl)paramList.get(j);
          if (((zzabl)localObject).zzbto.length != 0) {
            localArrayList.add(zzf(zzaba.zzj(((zzabl)localObject).zzbto)));
          }
        }
        int k = localArrayList.size();
        if (k == 0)
        {
          paramList = null;
        }
        else
        {
          localObject = this.zzbzi.cast(Array.newInstance(this.zzbzi.getComponentType(), k));
          for (j = i;; j++)
          {
            paramList = (List<zzabl>)localObject;
            if (j >= k) {
              break;
            }
            Array.set(localObject, j, localArrayList.get(j));
          }
        }
      }
      else if (paramList.isEmpty())
      {
        paramList = null;
      }
      else
      {
        paramList = (zzabl)paramList.get(paramList.size() - 1);
        paramList = this.zzbzi.cast(zzf(zzaba.zzj(paramList.zzbto)));
      }
    }
  }
  
  protected final int zzx(Object paramObject)
  {
    int i = this.tag >>> 3;
    switch (this.type)
    {
    default: 
      i = this.type;
      throw new IllegalArgumentException(24 + "Unknown type " + i);
    case 10: 
      paramObject = (zzabj)paramObject;
    }
    for (i = (zzabb.zzas(i) << 1) + ((zzabj)paramObject).zzwg();; i = zzabb.zzb(i, (zzabj)paramObject)) {
      return i;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzabe.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */