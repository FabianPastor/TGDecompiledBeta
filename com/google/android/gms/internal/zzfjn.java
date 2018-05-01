package com.google.android.gms.internal;

import java.io.IOException;

public final class zzfjn<M extends zzfjm<M>, T>
{
  public final int tag;
  private int type;
  protected final Class<T> zznfk;
  protected final boolean zzpnd;
  
  public final boolean equals(Object paramObject)
  {
    if (this == paramObject) {}
    do
    {
      return true;
      if (!(paramObject instanceof zzfjn)) {
        return false;
      }
      paramObject = (zzfjn)paramObject;
    } while ((this.type == ((zzfjn)paramObject).type) && (this.zznfk == ((zzfjn)paramObject).zznfk) && (this.tag == ((zzfjn)paramObject).tag) && (this.zzpnd == ((zzfjn)paramObject).zzpnd));
    return false;
  }
  
  public final int hashCode()
  {
    int j = this.type;
    int k = this.zznfk.hashCode();
    int m = this.tag;
    if (this.zzpnd) {}
    for (int i = 1;; i = 0) {
      return i + (((j + 1147) * 31 + k) * 31 + m) * 31;
    }
  }
  
  protected final void zza(Object paramObject, zzfjk paramzzfjk)
  {
    for (;;)
    {
      try
      {
        paramzzfjk.zzmi(this.tag);
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
      int i = this.tag;
      ((zzfjs)paramObject).zza(paramzzfjk);
      paramzzfjk.zzz(i >>> 3, 4);
      return;
      paramzzfjk.zzb((zzfjs)paramObject);
      return;
    }
  }
  
  protected final int zzcs(Object paramObject)
  {
    int i = this.tag >>> 3;
    switch (this.type)
    {
    default: 
      i = this.type;
      throw new IllegalArgumentException(24 + "Unknown type " + i);
    case 10: 
      paramObject = (zzfjs)paramObject;
      return (zzfjk.zzlg(i) << 1) + ((zzfjs)paramObject).zzho();
    }
    return zzfjk.zzb(i, (zzfjs)paramObject);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzfjn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */