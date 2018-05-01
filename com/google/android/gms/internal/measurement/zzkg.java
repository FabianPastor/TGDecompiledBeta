package com.google.android.gms.internal.measurement;

import java.io.IOException;

public final class zzkg
  extends zzabd<zzkg>
{
  private static volatile zzkg[] zzasp;
  public String value = null;
  public String zznt = null;
  
  public zzkg()
  {
    this.zzbzh = null;
    this.zzbzs = -1;
  }
  
  public static zzkg[] zzlb()
  {
    if (zzasp == null) {}
    synchronized (zzabh.zzbzr)
    {
      if (zzasp == null) {
        zzasp = new zzkg[0];
      }
      return zzasp;
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
      if (!(paramObject instanceof zzkg))
      {
        bool2 = false;
      }
      else
      {
        paramObject = (zzkg)paramObject;
        if (this.zznt == null)
        {
          if (((zzkg)paramObject).zznt != null) {
            bool2 = false;
          }
        }
        else if (!this.zznt.equals(((zzkg)paramObject).zznt))
        {
          bool2 = false;
          continue;
        }
        if (this.value == null)
        {
          if (((zzkg)paramObject).value != null) {
            bool2 = false;
          }
        }
        else if (!this.value.equals(((zzkg)paramObject).value))
        {
          bool2 = false;
          continue;
        }
        if ((this.zzbzh == null) || (this.zzbzh.isEmpty()))
        {
          bool2 = bool1;
          if (((zzkg)paramObject).zzbzh != null)
          {
            bool2 = bool1;
            if (!((zzkg)paramObject).zzbzh.isEmpty()) {
              bool2 = false;
            }
          }
        }
        else
        {
          bool2 = this.zzbzh.equals(((zzkg)paramObject).zzbzh);
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
    if (this.zznt == null)
    {
      k = 0;
      if (this.value != null) {
        break label89;
      }
      m = 0;
      label32:
      n = i;
      if (this.zzbzh != null) {
        if (!this.zzbzh.isEmpty()) {
          break label101;
        }
      }
    }
    label89:
    label101:
    for (int n = i;; n = this.zzbzh.hashCode())
    {
      return (m + (k + (j + 527) * 31) * 31) * 31 + n;
      k = this.zznt.hashCode();
      break;
      m = this.value.hashCode();
      break label32;
    }
  }
  
  protected final int zza()
  {
    int i = super.zza();
    int j = i;
    if (this.zznt != null) {
      j = i + zzabb.zzd(1, this.zznt);
    }
    i = j;
    if (this.value != null) {
      i = j + zzabb.zzd(2, this.value);
    }
    return i;
  }
  
  public final void zza(zzabb paramzzabb)
    throws IOException
  {
    if (this.zznt != null) {
      paramzzabb.zzc(1, this.zznt);
    }
    if (this.value != null) {
      paramzzabb.zzc(2, this.value);
    }
    super.zza(paramzzabb);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzkg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */