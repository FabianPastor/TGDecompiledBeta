package com.google.android.gms.internal;

import java.io.IOException;

public final class aeh
  extends adj<aeh>
  implements Cloneable
{
  private String version = "";
  private int zzbpb = 0;
  private String zzctL = "";
  
  public aeh()
  {
    this.zzcso = null;
    this.zzcsx = -1;
  }
  
  private aeh zzMb()
  {
    try
    {
      aeh localaeh = (aeh)super.zzLN();
      return localaeh;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new AssertionError(localCloneNotSupportedException);
    }
  }
  
  public final boolean equals(Object paramObject)
  {
    if (paramObject == this) {}
    do
    {
      return true;
      if (!(paramObject instanceof aeh)) {
        return false;
      }
      paramObject = (aeh)paramObject;
      if (this.zzbpb != ((aeh)paramObject).zzbpb) {
        return false;
      }
      if (this.zzctL == null)
      {
        if (((aeh)paramObject).zzctL != null) {
          return false;
        }
      }
      else if (!this.zzctL.equals(((aeh)paramObject).zzctL)) {
        return false;
      }
      if (this.version == null)
      {
        if (((aeh)paramObject).version != null) {
          return false;
        }
      }
      else if (!this.version.equals(((aeh)paramObject).version)) {
        return false;
      }
      if ((this.zzcso != null) && (!this.zzcso.isEmpty())) {
        break;
      }
    } while ((((aeh)paramObject).zzcso == null) || (((aeh)paramObject).zzcso.isEmpty()));
    return false;
    return this.zzcso.equals(((aeh)paramObject).zzcso);
  }
  
  public final int hashCode()
  {
    int m = 0;
    int n = getClass().getName().hashCode();
    int i1 = this.zzbpb;
    int i;
    int j;
    if (this.zzctL == null)
    {
      i = 0;
      if (this.version != null) {
        break label101;
      }
      j = 0;
      label39:
      k = m;
      if (this.zzcso != null) {
        if (!this.zzcso.isEmpty()) {
          break label112;
        }
      }
    }
    label101:
    label112:
    for (int k = m;; k = this.zzcso.hashCode())
    {
      return (j + (i + ((n + 527) * 31 + i1) * 31) * 31) * 31 + k;
      i = this.zzctL.hashCode();
      break;
      j = this.version.hashCode();
      break label39;
    }
  }
  
  public final void zza(adh paramadh)
    throws IOException
  {
    if (this.zzbpb != 0) {
      paramadh.zzr(1, this.zzbpb);
    }
    if ((this.zzctL != null) && (!this.zzctL.equals(""))) {
      paramadh.zzl(2, this.zzctL);
    }
    if ((this.version != null) && (!this.version.equals(""))) {
      paramadh.zzl(3, this.version);
    }
    super.zza(paramadh);
  }
  
  protected final int zzn()
  {
    int j = super.zzn();
    int i = j;
    if (this.zzbpb != 0) {
      i = j + adh.zzs(1, this.zzbpb);
    }
    j = i;
    if (this.zzctL != null)
    {
      j = i;
      if (!this.zzctL.equals("")) {
        j = i + adh.zzm(2, this.zzctL);
      }
    }
    i = j;
    if (this.version != null)
    {
      i = j;
      if (!this.version.equals("")) {
        i = j + adh.zzm(3, this.version);
      }
    }
    return i;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/aeh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */