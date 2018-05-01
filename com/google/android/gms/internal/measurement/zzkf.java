package com.google.android.gms.internal.measurement;

import java.io.IOException;

public final class zzkf
  extends zzabd<zzkf>
{
  public String zzadh = null;
  public Long zzask = null;
  private Integer zzasl = null;
  public zzkg[] zzasm = zzkg.zzlb();
  public zzke[] zzasn = zzke.zzla();
  public zzjy[] zzaso = zzjy.zzkw();
  
  public zzkf()
  {
    this.zzbzh = null;
    this.zzbzs = -1;
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
      if (!(paramObject instanceof zzkf))
      {
        bool2 = false;
      }
      else
      {
        paramObject = (zzkf)paramObject;
        if (this.zzask == null)
        {
          if (((zzkf)paramObject).zzask != null) {
            bool2 = false;
          }
        }
        else if (!this.zzask.equals(((zzkf)paramObject).zzask))
        {
          bool2 = false;
          continue;
        }
        if (this.zzadh == null)
        {
          if (((zzkf)paramObject).zzadh != null) {
            bool2 = false;
          }
        }
        else if (!this.zzadh.equals(((zzkf)paramObject).zzadh))
        {
          bool2 = false;
          continue;
        }
        if (this.zzasl == null)
        {
          if (((zzkf)paramObject).zzasl != null) {
            bool2 = false;
          }
        }
        else if (!this.zzasl.equals(((zzkf)paramObject).zzasl))
        {
          bool2 = false;
          continue;
        }
        if (!zzabh.equals(this.zzasm, ((zzkf)paramObject).zzasm))
        {
          bool2 = false;
        }
        else if (!zzabh.equals(this.zzasn, ((zzkf)paramObject).zzasn))
        {
          bool2 = false;
        }
        else if (!zzabh.equals(this.zzaso, ((zzkf)paramObject).zzaso))
        {
          bool2 = false;
        }
        else if ((this.zzbzh == null) || (this.zzbzh.isEmpty()))
        {
          bool2 = bool1;
          if (((zzkf)paramObject).zzbzh != null)
          {
            bool2 = bool1;
            if (!((zzkf)paramObject).zzbzh.isEmpty()) {
              bool2 = false;
            }
          }
        }
        else
        {
          bool2 = this.zzbzh.equals(((zzkf)paramObject).zzbzh);
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
    int n;
    label42:
    int i1;
    int i2;
    int i3;
    if (this.zzask == null)
    {
      k = 0;
      if (this.zzadh != null) {
        break label150;
      }
      m = 0;
      if (this.zzasl != null) {
        break label162;
      }
      n = 0;
      i1 = zzabh.hashCode(this.zzasm);
      i2 = zzabh.hashCode(this.zzasn);
      i3 = zzabh.hashCode(this.zzaso);
      i4 = i;
      if (this.zzbzh != null) {
        if (!this.zzbzh.isEmpty()) {
          break label174;
        }
      }
    }
    label150:
    label162:
    label174:
    for (int i4 = i;; i4 = this.zzbzh.hashCode())
    {
      return ((((n + (m + (k + (j + 527) * 31) * 31) * 31) * 31 + i1) * 31 + i2) * 31 + i3) * 31 + i4;
      k = this.zzask.hashCode();
      break;
      m = this.zzadh.hashCode();
      break label32;
      n = this.zzasl.hashCode();
      break label42;
    }
  }
  
  protected final int zza()
  {
    int i = 0;
    int j = super.zza();
    int k = j;
    if (this.zzask != null) {
      k = j + zzabb.zzc(1, this.zzask.longValue());
    }
    j = k;
    if (this.zzadh != null) {
      j = k + zzabb.zzd(2, this.zzadh);
    }
    k = j;
    if (this.zzasl != null) {
      k = j + zzabb.zzf(3, this.zzasl.intValue());
    }
    j = k;
    Object localObject;
    if (this.zzasm != null)
    {
      j = k;
      if (this.zzasm.length > 0)
      {
        j = 0;
        while (j < this.zzasm.length)
        {
          localObject = this.zzasm[j];
          m = k;
          if (localObject != null) {
            m = k + zzabb.zzb(4, (zzabj)localObject);
          }
          j++;
          k = m;
        }
        j = k;
      }
    }
    k = j;
    if (this.zzasn != null)
    {
      k = j;
      if (this.zzasn.length > 0)
      {
        m = 0;
        k = j;
        j = m;
        while (j < this.zzasn.length)
        {
          localObject = this.zzasn[j];
          m = k;
          if (localObject != null) {
            m = k + zzabb.zzb(5, (zzabj)localObject);
          }
          j++;
          k = m;
        }
      }
    }
    int m = k;
    if (this.zzaso != null)
    {
      m = k;
      if (this.zzaso.length > 0)
      {
        j = i;
        for (;;)
        {
          m = k;
          if (j >= this.zzaso.length) {
            break;
          }
          localObject = this.zzaso[j];
          m = k;
          if (localObject != null) {
            m = k + zzabb.zzb(6, (zzabj)localObject);
          }
          j++;
          k = m;
        }
      }
    }
    return m;
  }
  
  public final void zza(zzabb paramzzabb)
    throws IOException
  {
    int i = 0;
    if (this.zzask != null) {
      paramzzabb.zzb(1, this.zzask.longValue());
    }
    if (this.zzadh != null) {
      paramzzabb.zzc(2, this.zzadh);
    }
    if (this.zzasl != null) {
      paramzzabb.zze(3, this.zzasl.intValue());
    }
    int j;
    Object localObject;
    if ((this.zzasm != null) && (this.zzasm.length > 0)) {
      for (j = 0; j < this.zzasm.length; j++)
      {
        localObject = this.zzasm[j];
        if (localObject != null) {
          paramzzabb.zza(4, (zzabj)localObject);
        }
      }
    }
    if ((this.zzasn != null) && (this.zzasn.length > 0)) {
      for (j = 0; j < this.zzasn.length; j++)
      {
        localObject = this.zzasn[j];
        if (localObject != null) {
          paramzzabb.zza(5, (zzabj)localObject);
        }
      }
    }
    if ((this.zzaso != null) && (this.zzaso.length > 0)) {
      for (j = i; j < this.zzaso.length; j++)
      {
        localObject = this.zzaso[j];
        if (localObject != null) {
          paramzzabb.zza(6, (zzabj)localObject);
        }
      }
    }
    super.zza(paramzzabb);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzkf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */