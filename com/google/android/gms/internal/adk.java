package com.google.android.gms.internal;

import java.io.IOException;
import java.util.List;

public final class adk<M extends adj<M>, T>
{
  public final int tag;
  private int type = 11;
  protected final Class<T> zzcjG;
  protected final boolean zzcsp;
  
  private adk(int paramInt1, Class<T> paramClass, int paramInt2, boolean paramBoolean)
  {
    this.zzcjG = paramClass;
    this.tag = paramInt2;
    this.zzcsp = false;
  }
  
  public static <M extends adj<M>, T extends adp> adk<M, T> zza(int paramInt, Class<T> paramClass, long paramLong)
  {
    return new adk(11, paramClass, (int)paramLong, false);
  }
  
  private final Object zzb(adg paramadg)
  {
    Object localObject = this.zzcjG;
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
      catch (InstantiationException paramadg)
      {
        localObject = String.valueOf(localObject);
        throw new IllegalArgumentException(String.valueOf(localObject).length() + 33 + "Error creating instance of class " + (String)localObject, paramadg);
        adp localadp = (adp)((Class)localObject).newInstance();
        paramadg.zza(localadp, this.tag >>> 3);
        return localadp;
        localadp = (adp)((Class)localObject).newInstance();
        paramadg.zza(localadp);
        return localadp;
      }
      catch (IllegalAccessException paramadg)
      {
        localObject = String.valueOf(localObject);
        throw new IllegalArgumentException(String.valueOf(localObject).length() + 33 + "Error creating instance of class " + (String)localObject, paramadg);
      }
      catch (IOException paramadg)
      {
        throw new IllegalArgumentException("Error reading extension field", paramadg);
      }
    }
  }
  
  public final boolean equals(Object paramObject)
  {
    if (this == paramObject) {}
    do
    {
      return true;
      if (!(paramObject instanceof adk)) {
        return false;
      }
      paramObject = (adk)paramObject;
    } while ((this.type == ((adk)paramObject).type) && (this.zzcjG == ((adk)paramObject).zzcjG) && (this.tag == ((adk)paramObject).tag));
    return false;
  }
  
  public final int hashCode()
  {
    return (((this.type + 1147) * 31 + this.zzcjG.hashCode()) * 31 + this.tag) * 31;
  }
  
  final T zzX(List<adr> paramList)
  {
    if (paramList == null) {}
    while (paramList.isEmpty()) {
      return null;
    }
    paramList = (adr)paramList.get(paramList.size() - 1);
    return (T)this.zzcjG.cast(zzb(adg.zzH(paramList.zzbws)));
  }
  
  protected final void zza(Object paramObject, adh paramadh)
  {
    for (;;)
    {
      try
      {
        paramadh.zzcu(this.tag);
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
      paramObject = (adp)paramObject;
      int i = this.tag;
      ((adp)paramObject).zza(paramadh);
      paramadh.zzt(i >>> 3, 4);
      return;
      paramadh.zzb((adp)paramObject);
      return;
    }
  }
  
  protected final int zzav(Object paramObject)
  {
    int i = this.tag >>> 3;
    switch (this.type)
    {
    default: 
      i = this.type;
      throw new IllegalArgumentException(24 + "Unknown type " + i);
    case 10: 
      paramObject = (adp)paramObject;
      return (adh.zzct(i) << 1) + ((adp)paramObject).zzLV();
    }
    return adh.zzb(i, (adp)paramObject);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/adk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */