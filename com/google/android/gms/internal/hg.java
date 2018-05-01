package com.google.android.gms.internal;

import java.io.IOException;

public final class hg
  extends adj<hg>
{
  private static volatile hg[] zzbTK;
  public int type = 1;
  public hh zzbTL = null;
  
  public hg()
  {
    this.zzcso = null;
    this.zzcsx = -1;
  }
  
  public static hg[] zzEb()
  {
    if (zzbTK == null) {}
    synchronized (adn.zzcsw)
    {
      if (zzbTK == null) {
        zzbTK = new hg[0];
      }
      return zzbTK;
    }
  }
  
  public final boolean equals(Object paramObject)
  {
    if (paramObject == this) {}
    do
    {
      return true;
      if (!(paramObject instanceof hg)) {
        return false;
      }
      paramObject = (hg)paramObject;
      if (this.type != ((hg)paramObject).type) {
        return false;
      }
      if (this.zzbTL == null)
      {
        if (((hg)paramObject).zzbTL != null) {
          return false;
        }
      }
      else if (!this.zzbTL.equals(((hg)paramObject).zzbTL)) {
        return false;
      }
      if ((this.zzcso != null) && (!this.zzcso.isEmpty())) {
        break;
      }
    } while ((((hg)paramObject).zzcso == null) || (((hg)paramObject).zzcso.isEmpty()));
    return false;
    return this.zzcso.equals(((hg)paramObject).zzcso);
  }
  
  public final int hashCode()
  {
    int k = 0;
    int m = getClass().getName().hashCode();
    int n = this.type;
    int i;
    if (this.zzbTL == null)
    {
      i = 0;
      j = k;
      if (this.zzcso != null) {
        if (!this.zzcso.isEmpty()) {
          break label84;
        }
      }
    }
    label84:
    for (int j = k;; j = this.zzcso.hashCode())
    {
      return (i + ((m + 527) * 31 + n) * 31) * 31 + j;
      i = this.zzbTL.hashCode();
      break;
    }
  }
  
  public final void zza(adh paramadh)
    throws IOException
  {
    paramadh.zzr(1, this.type);
    if (this.zzbTL != null) {
      paramadh.zza(2, this.zzbTL);
    }
    super.zza(paramadh);
  }
  
  protected final int zzn()
  {
    int j = super.zzn() + adh.zzs(1, this.type);
    int i = j;
    if (this.zzbTL != null) {
      i = j + adh.zzb(2, this.zzbTL);
    }
    return i;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/hg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */