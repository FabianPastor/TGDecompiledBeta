package com.google.android.gms.internal.config;

import java.io.IOException;

public final class zzav
  extends zzbb<zzav>
{
  private static volatile zzav[] zzbo;
  public String namespace = "";
  public zzat[] zzbp = zzat.zzv();
  
  public zzav()
  {
    this.zzci = null;
    this.zzcr = -1;
  }
  
  public static zzav[] zzw()
  {
    if (zzbo == null) {}
    synchronized (zzbf.zzcq)
    {
      if (zzbo == null) {
        zzbo = new zzav[0];
      }
      return zzbo;
    }
  }
  
  public final boolean equals(Object paramObject)
  {
    boolean bool1 = true;
    boolean bool2;
    if (paramObject == this) {
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      if (!(paramObject instanceof zzav))
      {
        bool2 = false;
      }
      else
      {
        paramObject = (zzav)paramObject;
        if (this.namespace == null)
        {
          if (((zzav)paramObject).namespace != null) {
            bool2 = false;
          }
        }
        else if (!this.namespace.equals(((zzav)paramObject).namespace))
        {
          bool2 = false;
          continue;
        }
        if (!zzbf.equals(this.zzbp, ((zzav)paramObject).zzbp))
        {
          bool2 = false;
        }
        else if ((this.zzci == null) || (this.zzci.isEmpty()))
        {
          bool2 = bool1;
          if (((zzav)paramObject).zzci != null)
          {
            bool2 = bool1;
            if (!((zzav)paramObject).zzci.isEmpty()) {
              bool2 = false;
            }
          }
        }
        else
        {
          bool2 = this.zzci.equals(((zzav)paramObject).zzci);
        }
      }
    }
  }
  
  public final int hashCode()
  {
    int i = 0;
    int j = getClass().getName().hashCode();
    int k;
    int m;
    if (this.namespace == null)
    {
      k = 0;
      m = zzbf.hashCode(this.zzbp);
      n = i;
      if (this.zzci != null) {
        if (!this.zzci.isEmpty()) {
          break label88;
        }
      }
    }
    label88:
    for (int n = i;; n = this.zzci.hashCode())
    {
      return ((k + (j + 527) * 31) * 31 + m) * 31 + n;
      k = this.namespace.hashCode();
      break;
    }
  }
  
  public final void zza(zzaz paramzzaz)
    throws IOException
  {
    if ((this.namespace != null) && (!this.namespace.equals(""))) {
      paramzzaz.zza(1, this.namespace);
    }
    if ((this.zzbp != null) && (this.zzbp.length > 0)) {
      for (int i = 0; i < this.zzbp.length; i++)
      {
        zzat localzzat = this.zzbp[i];
        if (localzzat != null) {
          paramzzaz.zza(2, localzzat);
        }
      }
    }
    super.zza(paramzzaz);
  }
  
  protected final int zzu()
  {
    int i = super.zzu();
    int j = i;
    if (this.namespace != null)
    {
      j = i;
      if (!this.namespace.equals("")) {
        j = i + zzaz.zzb(1, this.namespace);
      }
    }
    i = j;
    if (this.zzbp != null)
    {
      i = j;
      if (this.zzbp.length > 0)
      {
        i = 0;
        while (i < this.zzbp.length)
        {
          zzat localzzat = this.zzbp[i];
          int k = j;
          if (localzzat != null) {
            k = j + zzaz.zzb(2, localzzat);
          }
          i++;
          j = k;
        }
        i = j;
      }
    }
    return i;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/config/zzav.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */