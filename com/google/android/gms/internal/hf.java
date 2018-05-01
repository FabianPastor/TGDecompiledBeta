package com.google.android.gms.internal;

import java.io.IOException;

public final class hf
  extends adj<hf>
{
  private static volatile hf[] zzbTI;
  public String name = "";
  public hg zzbTJ = null;
  
  public hf()
  {
    this.zzcso = null;
    this.zzcsx = -1;
  }
  
  public static hf[] zzEa()
  {
    if (zzbTI == null) {}
    synchronized (adn.zzcsw)
    {
      if (zzbTI == null) {
        zzbTI = new hf[0];
      }
      return zzbTI;
    }
  }
  
  public final boolean equals(Object paramObject)
  {
    if (paramObject == this) {}
    do
    {
      return true;
      if (!(paramObject instanceof hf)) {
        return false;
      }
      paramObject = (hf)paramObject;
      if (this.name == null)
      {
        if (((hf)paramObject).name != null) {
          return false;
        }
      }
      else if (!this.name.equals(((hf)paramObject).name)) {
        return false;
      }
      if (this.zzbTJ == null)
      {
        if (((hf)paramObject).zzbTJ != null) {
          return false;
        }
      }
      else if (!this.zzbTJ.equals(((hf)paramObject).zzbTJ)) {
        return false;
      }
      if ((this.zzcso != null) && (!this.zzcso.isEmpty())) {
        break;
      }
    } while ((((hf)paramObject).zzcso == null) || (((hf)paramObject).zzcso.isEmpty()));
    return false;
    return this.zzcso.equals(((hf)paramObject).zzcso);
  }
  
  public final int hashCode()
  {
    int m = 0;
    int n = getClass().getName().hashCode();
    int i;
    int j;
    if (this.name == null)
    {
      i = 0;
      if (this.zzbTJ != null) {
        break label89;
      }
      j = 0;
      label33:
      k = m;
      if (this.zzcso != null) {
        if (!this.zzcso.isEmpty()) {
          break label100;
        }
      }
    }
    label89:
    label100:
    for (int k = m;; k = this.zzcso.hashCode())
    {
      return (j + (i + (n + 527) * 31) * 31) * 31 + k;
      i = this.name.hashCode();
      break;
      j = this.zzbTJ.hashCode();
      break label33;
    }
  }
  
  public final void zza(adh paramadh)
    throws IOException
  {
    paramadh.zzl(1, this.name);
    if (this.zzbTJ != null) {
      paramadh.zza(2, this.zzbTJ);
    }
    super.zza(paramadh);
  }
  
  protected final int zzn()
  {
    int j = super.zzn() + adh.zzm(1, this.name);
    int i = j;
    if (this.zzbTJ != null) {
      i = j + adh.zzb(2, this.zzbTJ);
    }
    return i;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/hf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */