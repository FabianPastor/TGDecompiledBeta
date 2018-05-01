package com.google.android.gms.internal.config;

import java.io.IOException;

public final class zzax
  extends zzbb<zzax>
{
  private static volatile zzax[] zzbv;
  public String namespace = "";
  public int resourceId = 0;
  public long zzbw = 0L;
  
  public zzax()
  {
    this.zzci = null;
    this.zzcr = -1;
  }
  
  public static zzax[] zzx()
  {
    if (zzbv == null) {}
    synchronized (zzbf.zzcq)
    {
      if (zzbv == null) {
        zzbv = new zzax[0];
      }
      return zzbv;
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
      if (!(paramObject instanceof zzax))
      {
        bool2 = false;
      }
      else
      {
        paramObject = (zzax)paramObject;
        if (this.resourceId != ((zzax)paramObject).resourceId)
        {
          bool2 = false;
        }
        else if (this.zzbw != ((zzax)paramObject).zzbw)
        {
          bool2 = false;
        }
        else
        {
          if (this.namespace == null)
          {
            if (((zzax)paramObject).namespace != null) {
              bool2 = false;
            }
          }
          else if (!this.namespace.equals(((zzax)paramObject).namespace))
          {
            bool2 = false;
            continue;
          }
          if ((this.zzci == null) || (this.zzci.isEmpty()))
          {
            bool2 = bool1;
            if (((zzax)paramObject).zzci != null)
            {
              bool2 = bool1;
              if (!((zzax)paramObject).zzci.isEmpty()) {
                bool2 = false;
              }
            }
          }
          else
          {
            bool2 = this.zzci.equals(((zzax)paramObject).zzci);
          }
        }
      }
    }
  }
  
  public final int hashCode()
  {
    int i = 0;
    int j = getClass().getName().hashCode();
    int k = this.resourceId;
    int m = (int)(this.zzbw ^ this.zzbw >>> 32);
    int n;
    if (this.namespace == null)
    {
      n = 0;
      i1 = i;
      if (this.zzci != null) {
        if (!this.zzci.isEmpty()) {
          break label107;
        }
      }
    }
    label107:
    for (int i1 = i;; i1 = this.zzci.hashCode())
    {
      return (n + (((j + 527) * 31 + k) * 31 + m) * 31) * 31 + i1;
      n = this.namespace.hashCode();
      break;
    }
  }
  
  public final void zza(zzaz paramzzaz)
    throws IOException
  {
    if (this.resourceId != 0) {
      paramzzaz.zzc(1, this.resourceId);
    }
    if (this.zzbw != 0L) {
      paramzzaz.zza(2, this.zzbw);
    }
    if ((this.namespace != null) && (!this.namespace.equals(""))) {
      paramzzaz.zza(3, this.namespace);
    }
    super.zza(paramzzaz);
  }
  
  protected final int zzu()
  {
    int i = super.zzu();
    int j = i;
    if (this.resourceId != 0) {
      j = i + zzaz.zzd(1, this.resourceId);
    }
    i = j;
    if (this.zzbw != 0L) {
      i = j + (zzaz.zzl(2) + 8);
    }
    j = i;
    if (this.namespace != null)
    {
      j = i;
      if (!this.namespace.equals("")) {
        j = i + zzaz.zzb(3, this.namespace);
      }
    }
    return j;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/config/zzax.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */