package com.google.android.gms.internal.config;

import java.io.IOException;

public final class zzaw
  extends zzbb<zzaw>
{
  public zzas zzbq = null;
  public zzas zzbr = null;
  public zzas zzbs = null;
  public zzau zzbt = null;
  public zzax[] zzbu = zzax.zzx();
  
  public zzaw()
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
      if (!(paramObject instanceof zzaw))
      {
        bool2 = false;
      }
      else
      {
        paramObject = (zzaw)paramObject;
        if (this.zzbq == null)
        {
          if (((zzaw)paramObject).zzbq != null) {
            bool2 = false;
          }
        }
        else if (!this.zzbq.equals(((zzaw)paramObject).zzbq))
        {
          bool2 = false;
          continue;
        }
        if (this.zzbr == null)
        {
          if (((zzaw)paramObject).zzbr != null) {
            bool2 = false;
          }
        }
        else if (!this.zzbr.equals(((zzaw)paramObject).zzbr))
        {
          bool2 = false;
          continue;
        }
        if (this.zzbs == null)
        {
          if (((zzaw)paramObject).zzbs != null) {
            bool2 = false;
          }
        }
        else if (!this.zzbs.equals(((zzaw)paramObject).zzbs))
        {
          bool2 = false;
          continue;
        }
        if (this.zzbt == null)
        {
          if (((zzaw)paramObject).zzbt != null) {
            bool2 = false;
          }
        }
        else if (!this.zzbt.equals(((zzaw)paramObject).zzbt))
        {
          bool2 = false;
          continue;
        }
        if (!zzbf.equals(this.zzbu, ((zzaw)paramObject).zzbu))
        {
          bool2 = false;
        }
        else if ((this.zzci == null) || (this.zzci.isEmpty()))
        {
          bool2 = bool1;
          if (((zzaw)paramObject).zzci != null)
          {
            bool2 = bool1;
            if (!((zzaw)paramObject).zzci.isEmpty()) {
              bool2 = false;
            }
          }
        }
        else
        {
          bool2 = this.zzci.equals(((zzaw)paramObject).zzci);
        }
      }
    }
  }
  
  public final int hashCode()
  {
    int i = 0;
    int j = getClass().getName().hashCode();
    Object localObject = this.zzbq;
    int k;
    int m;
    label37:
    int n;
    label49:
    int i1;
    label61:
    int i2;
    if (localObject == null)
    {
      k = 0;
      localObject = this.zzbr;
      if (localObject != null) {
        break label144;
      }
      m = 0;
      localObject = this.zzbs;
      if (localObject != null) {
        break label153;
      }
      n = 0;
      localObject = this.zzbt;
      if (localObject != null) {
        break label162;
      }
      i1 = 0;
      i2 = zzbf.hashCode(this.zzbu);
      i3 = i;
      if (this.zzci != null) {
        if (!this.zzci.isEmpty()) {
          break label171;
        }
      }
    }
    label144:
    label153:
    label162:
    label171:
    for (int i3 = i;; i3 = this.zzci.hashCode())
    {
      return ((i1 + (n + (m + (k + (j + 527) * 31) * 31) * 31) * 31) * 31 + i2) * 31 + i3;
      k = ((zzas)localObject).hashCode();
      break;
      m = ((zzas)localObject).hashCode();
      break label37;
      n = ((zzas)localObject).hashCode();
      break label49;
      i1 = ((zzau)localObject).hashCode();
      break label61;
    }
  }
  
  public final void zza(zzaz paramzzaz)
    throws IOException
  {
    if (this.zzbq != null) {
      paramzzaz.zza(1, this.zzbq);
    }
    if (this.zzbr != null) {
      paramzzaz.zza(2, this.zzbr);
    }
    if (this.zzbs != null) {
      paramzzaz.zza(3, this.zzbs);
    }
    if (this.zzbt != null) {
      paramzzaz.zza(4, this.zzbt);
    }
    if ((this.zzbu != null) && (this.zzbu.length > 0)) {
      for (int i = 0; i < this.zzbu.length; i++)
      {
        zzax localzzax = this.zzbu[i];
        if (localzzax != null) {
          paramzzaz.zza(5, localzzax);
        }
      }
    }
    super.zza(paramzzaz);
  }
  
  protected final int zzu()
  {
    int i = super.zzu();
    int j = i;
    if (this.zzbq != null) {
      j = i + zzaz.zzb(1, this.zzbq);
    }
    i = j;
    if (this.zzbr != null) {
      i = j + zzaz.zzb(2, this.zzbr);
    }
    j = i;
    if (this.zzbs != null) {
      j = i + zzaz.zzb(3, this.zzbs);
    }
    i = j;
    if (this.zzbt != null) {
      i = j + zzaz.zzb(4, this.zzbt);
    }
    j = i;
    if (this.zzbu != null)
    {
      j = i;
      if (this.zzbu.length > 0)
      {
        int k = 0;
        while (k < this.zzbu.length)
        {
          zzax localzzax = this.zzbu[k];
          j = i;
          if (localzzax != null) {
            j = i + zzaz.zzb(5, localzzax);
          }
          k++;
          i = j;
        }
        j = i;
      }
    }
    return j;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/config/zzaw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */