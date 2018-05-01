package com.google.android.gms.internal.config;

import java.io.IOException;

public final class zzas
  extends zzbb<zzas>
{
  public long timestamp = 0L;
  public zzav[] zzbg = zzav.zzw();
  public byte[][] zzbh = zzbk.zzdd;
  
  public zzas()
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
      if (!(paramObject instanceof zzas))
      {
        bool2 = false;
      }
      else
      {
        paramObject = (zzas)paramObject;
        if (!zzbf.equals(this.zzbg, ((zzas)paramObject).zzbg))
        {
          bool2 = false;
        }
        else if (this.timestamp != ((zzas)paramObject).timestamp)
        {
          bool2 = false;
        }
        else if (!zzbf.zza(this.zzbh, ((zzas)paramObject).zzbh))
        {
          bool2 = false;
        }
        else if ((this.zzci == null) || (this.zzci.isEmpty()))
        {
          bool2 = bool1;
          if (((zzas)paramObject).zzci != null)
          {
            bool2 = bool1;
            if (!((zzas)paramObject).zzci.isEmpty()) {
              bool2 = false;
            }
          }
        }
        else
        {
          bool2 = this.zzci.equals(((zzas)paramObject).zzci);
        }
      }
    }
  }
  
  public final int hashCode()
  {
    int i = getClass().getName().hashCode();
    int j = zzbf.hashCode(this.zzbg);
    int k = (int)(this.timestamp ^ this.timestamp >>> 32);
    int m = zzbf.zza(this.zzbh);
    if ((this.zzci == null) || (this.zzci.isEmpty())) {}
    for (int n = 0;; n = this.zzci.hashCode()) {
      return n + ((((i + 527) * 31 + j) * 31 + k) * 31 + m) * 31;
    }
  }
  
  public final void zza(zzaz paramzzaz)
    throws IOException
  {
    int i = 0;
    int j;
    Object localObject;
    if ((this.zzbg != null) && (this.zzbg.length > 0)) {
      for (j = 0; j < this.zzbg.length; j++)
      {
        localObject = this.zzbg[j];
        if (localObject != null) {
          paramzzaz.zza(1, (zzbh)localObject);
        }
      }
    }
    if (this.timestamp != 0L) {
      paramzzaz.zza(2, this.timestamp);
    }
    if ((this.zzbh != null) && (this.zzbh.length > 0)) {
      for (j = i; j < this.zzbh.length; j++)
      {
        localObject = this.zzbh[j];
        if (localObject != null) {
          paramzzaz.zza(3, (byte[])localObject);
        }
      }
    }
    super.zza(paramzzaz);
  }
  
  protected final int zzu()
  {
    int i = super.zzu();
    int j = i;
    int k;
    Object localObject;
    if (this.zzbg != null)
    {
      j = i;
      if (this.zzbg.length > 0)
      {
        k = 0;
        for (;;)
        {
          j = i;
          if (k >= this.zzbg.length) {
            break;
          }
          localObject = this.zzbg[k];
          j = i;
          if (localObject != null) {
            j = i + zzaz.zzb(1, (zzbh)localObject);
          }
          k++;
          i = j;
        }
      }
    }
    i = j;
    if (this.timestamp != 0L) {
      i = j + (zzaz.zzl(2) + 8);
    }
    j = i;
    if (this.zzbh != null)
    {
      j = i;
      if (this.zzbh.length > 0)
      {
        k = 0;
        j = 0;
        int i1;
        for (int m = 0; k < this.zzbh.length; m = i1)
        {
          localObject = this.zzbh[k];
          int n = j;
          i1 = m;
          if (localObject != null)
          {
            i1 = m + 1;
            n = j + zzaz.zzb((byte[])localObject);
          }
          k++;
          j = n;
        }
        j = i + j + m * 1;
      }
    }
    return j;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/config/zzas.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */