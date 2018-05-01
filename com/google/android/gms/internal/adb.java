package com.google.android.gms.internal;

import java.io.IOException;
import java.util.List;

public final class adb<M extends ada<M>, T>
{
  public final int tag;
  private int type = 11;
  protected final Class<T> zzcjC;
  protected final boolean zzcsa;
  
  private adb(int paramInt1, Class<T> paramClass, int paramInt2, boolean paramBoolean)
  {
    this.zzcjC = paramClass;
    this.tag = paramInt2;
    this.zzcsa = false;
  }
  
  public static <M extends ada<M>, T extends adg> adb<M, T> zza(int paramInt, Class<T> paramClass, long paramLong)
  {
    return new adb(11, paramClass, (int)paramLong, false);
  }
  
  private final Object zzb(acx paramacx)
  {
    Object localObject = this.zzcjC;
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
      catch (InstantiationException paramacx)
      {
        localObject = String.valueOf(localObject);
        throw new IllegalArgumentException(String.valueOf(localObject).length() + 33 + "Error creating instance of class " + (String)localObject, paramacx);
        adg localadg = (adg)((Class)localObject).newInstance();
        paramacx.zza(localadg, this.tag >>> 3);
        return localadg;
        localadg = (adg)((Class)localObject).newInstance();
        paramacx.zza(localadg);
        return localadg;
      }
      catch (IllegalAccessException paramacx)
      {
        localObject = String.valueOf(localObject);
        throw new IllegalArgumentException(String.valueOf(localObject).length() + 33 + "Error creating instance of class " + (String)localObject, paramacx);
      }
      catch (IOException paramacx)
      {
        throw new IllegalArgumentException("Error reading extension field", paramacx);
      }
    }
  }
  
  public final boolean equals(Object paramObject)
  {
    if (this == paramObject) {}
    do
    {
      return true;
      if (!(paramObject instanceof adb)) {
        return false;
      }
      paramObject = (adb)paramObject;
    } while ((this.type == ((adb)paramObject).type) && (this.zzcjC == ((adb)paramObject).zzcjC) && (this.tag == ((adb)paramObject).tag));
    return false;
  }
  
  public final int hashCode()
  {
    return (((this.type + 1147) * 31 + this.zzcjC.hashCode()) * 31 + this.tag) * 31;
  }
  
  final T zzX(List<adi> paramList)
  {
    if (paramList == null) {}
    while (paramList.isEmpty()) {
      return null;
    }
    paramList = (adi)paramList.get(paramList.size() - 1);
    return (T)this.zzcjC.cast(zzb(acx.zzH(paramList.zzbws)));
  }
  
  protected final void zza(Object paramObject, acy paramacy)
  {
    for (;;)
    {
      try
      {
        paramacy.zzcu(this.tag);
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
      paramObject = (adg)paramObject;
      int i = this.tag;
      ((adg)paramObject).zza(paramacy);
      paramacy.zzt(i >>> 3, 4);
      return;
      paramacy.zzb((adg)paramObject);
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
      paramObject = (adg)paramObject;
      return (acy.zzct(i) << 1) + ((adg)paramObject).zzLT();
    }
    return acy.zzb(i, (adg)paramObject);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/adb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */