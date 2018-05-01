package com.google.android.gms.internal.measurement;

import java.io.IOException;

public final class zzke
  extends zzabd<zzke>
{
  private static volatile zzke[] zzasg;
  public String name = null;
  public Boolean zzash = null;
  public Boolean zzasi = null;
  public Integer zzasj = null;
  
  public zzke()
  {
    this.zzbzh = null;
    this.zzbzs = -1;
  }
  
  public static zzke[] zzla()
  {
    if (zzasg == null) {}
    synchronized (zzabh.zzbzr)
    {
      if (zzasg == null) {
        zzasg = new zzke[0];
      }
      return zzasg;
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
      if (!(paramObject instanceof zzke))
      {
        bool2 = false;
      }
      else
      {
        paramObject = (zzke)paramObject;
        if (this.name == null)
        {
          if (((zzke)paramObject).name != null) {
            bool2 = false;
          }
        }
        else if (!this.name.equals(((zzke)paramObject).name))
        {
          bool2 = false;
          continue;
        }
        if (this.zzash == null)
        {
          if (((zzke)paramObject).zzash != null) {
            bool2 = false;
          }
        }
        else if (!this.zzash.equals(((zzke)paramObject).zzash))
        {
          bool2 = false;
          continue;
        }
        if (this.zzasi == null)
        {
          if (((zzke)paramObject).zzasi != null) {
            bool2 = false;
          }
        }
        else if (!this.zzasi.equals(((zzke)paramObject).zzasi))
        {
          bool2 = false;
          continue;
        }
        if (this.zzasj == null)
        {
          if (((zzke)paramObject).zzasj != null) {
            bool2 = false;
          }
        }
        else if (!this.zzasj.equals(((zzke)paramObject).zzasj))
        {
          bool2 = false;
          continue;
        }
        if ((this.zzbzh == null) || (this.zzbzh.isEmpty()))
        {
          bool2 = bool1;
          if (((zzke)paramObject).zzbzh != null)
          {
            bool2 = bool1;
            if (!((zzke)paramObject).zzbzh.isEmpty()) {
              bool2 = false;
            }
          }
        }
        else
        {
          bool2 = this.zzbzh.equals(((zzke)paramObject).zzbzh);
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
    if (this.name == null)
    {
      k = 0;
      if (this.zzash != null) {
        break label121;
      }
      m = 0;
      if (this.zzasi != null) {
        break label133;
      }
      n = 0;
      if (this.zzasj != null) {
        break label145;
      }
      i1 = 0;
      label52:
      i2 = i;
      if (this.zzbzh != null) {
        if (!this.zzbzh.isEmpty()) {
          break label157;
        }
      }
    }
    label121:
    label133:
    label145:
    label157:
    for (int i2 = i;; i2 = this.zzbzh.hashCode())
    {
      return (i1 + (n + (m + (k + (j + 527) * 31) * 31) * 31) * 31) * 31 + i2;
      k = this.name.hashCode();
      break;
      m = this.zzash.hashCode();
      break label32;
      n = this.zzasi.hashCode();
      break label42;
      i1 = this.zzasj.hashCode();
      break label52;
    }
  }
  
  protected final int zza()
  {
    int i = super.zza();
    int j = i;
    if (this.name != null) {
      j = i + zzabb.zzd(1, this.name);
    }
    i = j;
    if (this.zzash != null)
    {
      this.zzash.booleanValue();
      i = j + (zzabb.zzas(2) + 1);
    }
    j = i;
    if (this.zzasi != null)
    {
      this.zzasi.booleanValue();
      j = i + (zzabb.zzas(3) + 1);
    }
    i = j;
    if (this.zzasj != null) {
      i = j + zzabb.zzf(4, this.zzasj.intValue());
    }
    return i;
  }
  
  public final void zza(zzabb paramzzabb)
    throws IOException
  {
    if (this.name != null) {
      paramzzabb.zzc(1, this.name);
    }
    if (this.zzash != null) {
      paramzzabb.zza(2, this.zzash.booleanValue());
    }
    if (this.zzasi != null) {
      paramzzabb.zza(3, this.zzasi.booleanValue());
    }
    if (this.zzasj != null) {
      paramzzabb.zze(4, this.zzasj.intValue());
    }
    super.zza(paramzzabb);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzke.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */