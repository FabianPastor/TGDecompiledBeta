package com.google.android.gms.internal.measurement;

import java.io.IOException;

public final class zzkc
  extends zzabd<zzkc>
{
  private static volatile zzkc[] zzarz;
  public Integer zzark = null;
  public String zzasa = null;
  public zzka zzasb = null;
  
  public zzkc()
  {
    this.zzbzh = null;
    this.zzbzs = -1;
  }
  
  public static zzkc[] zzkz()
  {
    if (zzarz == null) {}
    synchronized (zzabh.zzbzr)
    {
      if (zzarz == null) {
        zzarz = new zzkc[0];
      }
      return zzarz;
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
      if (!(paramObject instanceof zzkc))
      {
        bool2 = false;
      }
      else
      {
        paramObject = (zzkc)paramObject;
        if (this.zzark == null)
        {
          if (((zzkc)paramObject).zzark != null) {
            bool2 = false;
          }
        }
        else if (!this.zzark.equals(((zzkc)paramObject).zzark))
        {
          bool2 = false;
          continue;
        }
        if (this.zzasa == null)
        {
          if (((zzkc)paramObject).zzasa != null) {
            bool2 = false;
          }
        }
        else if (!this.zzasa.equals(((zzkc)paramObject).zzasa))
        {
          bool2 = false;
          continue;
        }
        if (this.zzasb == null)
        {
          if (((zzkc)paramObject).zzasb != null) {
            bool2 = false;
          }
        }
        else if (!this.zzasb.equals(((zzkc)paramObject).zzasb))
        {
          bool2 = false;
          continue;
        }
        if ((this.zzbzh == null) || (this.zzbzh.isEmpty()))
        {
          bool2 = bool1;
          if (((zzkc)paramObject).zzbzh != null)
          {
            bool2 = bool1;
            if (!((zzkc)paramObject).zzbzh.isEmpty()) {
              bool2 = false;
            }
          }
        }
        else
        {
          bool2 = this.zzbzh.equals(((zzkc)paramObject).zzbzh);
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
    label32:
    zzka localzzka;
    int n;
    if (this.zzark == null)
    {
      k = 0;
      if (this.zzasa != null) {
        break label109;
      }
      m = 0;
      localzzka = this.zzasb;
      if (localzzka != null) {
        break label121;
      }
      n = 0;
      label46:
      i1 = i;
      if (this.zzbzh != null) {
        if (!this.zzbzh.isEmpty()) {
          break label131;
        }
      }
    }
    label109:
    label121:
    label131:
    for (int i1 = i;; i1 = this.zzbzh.hashCode())
    {
      return (n + (m + (k + (j + 527) * 31) * 31) * 31) * 31 + i1;
      k = this.zzark.hashCode();
      break;
      m = this.zzasa.hashCode();
      break label32;
      n = localzzka.hashCode();
      break label46;
    }
  }
  
  protected final int zza()
  {
    int i = super.zza();
    int j = i;
    if (this.zzark != null) {
      j = i + zzabb.zzf(1, this.zzark.intValue());
    }
    i = j;
    if (this.zzasa != null) {
      i = j + zzabb.zzd(2, this.zzasa);
    }
    j = i;
    if (this.zzasb != null) {
      j = i + zzabb.zzb(3, this.zzasb);
    }
    return j;
  }
  
  public final void zza(zzabb paramzzabb)
    throws IOException
  {
    if (this.zzark != null) {
      paramzzabb.zze(1, this.zzark.intValue());
    }
    if (this.zzasa != null) {
      paramzzabb.zzc(2, this.zzasa);
    }
    if (this.zzasb != null) {
      paramzzabb.zza(3, this.zzasb);
    }
    super.zza(paramzzabb);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzkc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */