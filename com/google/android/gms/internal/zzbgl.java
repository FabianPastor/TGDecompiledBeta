package com.google.android.gms.internal;

import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public abstract class zzbgl
  extends zzbgi
  implements SafeParcelable
{
  public final int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!getClass().isInstance(paramObject)) {
      return false;
    }
    paramObject = (zzbgi)paramObject;
    Iterator localIterator = zzrL().values().iterator();
    while (localIterator.hasNext())
    {
      zzbgj localzzbgj = (zzbgj)localIterator.next();
      if (zza(localzzbgj))
      {
        if (((zzbgi)paramObject).zza(localzzbgj))
        {
          if (!zzb(localzzbgj).equals(((zzbgi)paramObject).zzb(localzzbgj))) {
            return false;
          }
        }
        else {
          return false;
        }
      }
      else if (((zzbgi)paramObject).zza(localzzbgj)) {
        return false;
      }
    }
    return true;
  }
  
  public int hashCode()
  {
    Iterator localIterator = zzrL().values().iterator();
    int i = 0;
    if (localIterator.hasNext())
    {
      zzbgj localzzbgj = (zzbgj)localIterator.next();
      if (!zza(localzzbgj)) {
        break label63;
      }
      i = zzb(localzzbgj).hashCode() + i * 31;
    }
    label63:
    for (;;)
    {
      break;
      return i;
    }
  }
  
  public Object zzcH(String paramString)
  {
    return null;
  }
  
  public boolean zzcI(String paramString)
  {
    return false;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzbgl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */