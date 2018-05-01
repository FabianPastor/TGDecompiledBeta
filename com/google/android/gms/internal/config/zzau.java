package com.google.android.gms.internal.config;

import java.io.IOException;

public final class zzau
  extends zzbb<zzau>
{
  public int zzbl = 0;
  public boolean zzbm = false;
  public long zzbn = 0L;
  
  public zzau()
  {
    this.zzci = null;
    this.zzcr = -1;
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
      if (!(paramObject instanceof zzau))
      {
        bool2 = false;
      }
      else
      {
        paramObject = (zzau)paramObject;
        if (this.zzbl != ((zzau)paramObject).zzbl)
        {
          bool2 = false;
        }
        else if (this.zzbm != ((zzau)paramObject).zzbm)
        {
          bool2 = false;
        }
        else if (this.zzbn != ((zzau)paramObject).zzbn)
        {
          bool2 = false;
        }
        else if ((this.zzci == null) || (this.zzci.isEmpty()))
        {
          bool2 = bool1;
          if (((zzau)paramObject).zzci != null)
          {
            bool2 = bool1;
            if (!((zzau)paramObject).zzci.isEmpty()) {
              bool2 = false;
            }
          }
        }
        else
        {
          bool2 = this.zzci.equals(((zzau)paramObject).zzci);
        }
      }
    }
  }
  
  public final int hashCode()
  {
    int i = getClass().getName().hashCode();
    int j = this.zzbl;
    int k;
    int m;
    if (this.zzbm)
    {
      k = 1231;
      m = (int)(this.zzbn ^ this.zzbn >>> 32);
      if ((this.zzci != null) && (!this.zzci.isEmpty())) {
        break label97;
      }
    }
    label97:
    for (int n = 0;; n = this.zzci.hashCode())
    {
      return n + ((k + ((i + 527) * 31 + j) * 31) * 31 + m) * 31;
      k = 1237;
      break;
    }
  }
  
  public final void zza(zzaz paramzzaz)
    throws IOException
  {
    int i = 1;
    if (this.zzbl != 0) {
      paramzzaz.zzc(1, this.zzbl);
    }
    if (this.zzbm)
    {
      boolean bool = this.zzbm;
      paramzzaz.zze(2, 0);
      if (!bool) {
        break label70;
      }
    }
    for (;;)
    {
      paramzzaz.zza((byte)i);
      if (this.zzbn != 0L) {
        paramzzaz.zza(3, this.zzbn);
      }
      super.zza(paramzzaz);
      return;
      label70:
      i = 0;
    }
  }
  
  protected final int zzu()
  {
    int i = super.zzu();
    int j = i;
    if (this.zzbl != 0) {
      j = i + zzaz.zzd(1, this.zzbl);
    }
    i = j;
    if (this.zzbm) {
      i = j + (zzaz.zzl(2) + 1);
    }
    j = i;
    if (this.zzbn != 0L) {
      j = i + (zzaz.zzl(3) + 8);
    }
    return j;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/config/zzau.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */