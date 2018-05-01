package com.google.android.gms.internal;

import java.io.IOException;

public final class ady
  extends ada<ady>
  implements Cloneable
{
  private String version = "";
  private int zzbpb = 0;
  private String zzctw = "";
  
  public ady()
  {
    this.zzcrZ = null;
    this.zzcsi = -1;
  }
  
  private ady zzLZ()
  {
    try
    {
      ady localady = (ady)super.zzLL();
      return localady;
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
      if (!(paramObject instanceof ady)) {
        return false;
      }
      paramObject = (ady)paramObject;
      if (this.zzbpb != ((ady)paramObject).zzbpb) {
        return false;
      }
      if (this.zzctw == null)
      {
        if (((ady)paramObject).zzctw != null) {
          return false;
        }
      }
      else if (!this.zzctw.equals(((ady)paramObject).zzctw)) {
        return false;
      }
      if (this.version == null)
      {
        if (((ady)paramObject).version != null) {
          return false;
        }
      }
      else if (!this.version.equals(((ady)paramObject).version)) {
        return false;
      }
      if ((this.zzcrZ != null) && (!this.zzcrZ.isEmpty())) {
        break;
      }
    } while ((((ady)paramObject).zzcrZ == null) || (((ady)paramObject).zzcrZ.isEmpty()));
    return false;
    return this.zzcrZ.equals(((ady)paramObject).zzcrZ);
  }
  
  public final int hashCode()
  {
    int m = 0;
    int n = getClass().getName().hashCode();
    int i1 = this.zzbpb;
    int i;
    int j;
    if (this.zzctw == null)
    {
      i = 0;
      if (this.version != null) {
        break label101;
      }
      j = 0;
      label39:
      k = m;
      if (this.zzcrZ != null) {
        if (!this.zzcrZ.isEmpty()) {
          break label112;
        }
      }
    }
    label101:
    label112:
    for (int k = m;; k = this.zzcrZ.hashCode())
    {
      return (j + (i + ((n + 527) * 31 + i1) * 31) * 31) * 31 + k;
      i = this.zzctw.hashCode();
      break;
      j = this.version.hashCode();
      break label39;
    }
  }
  
  public final void zza(acy paramacy)
    throws IOException
  {
    if (this.zzbpb != 0) {
      paramacy.zzr(1, this.zzbpb);
    }
    if ((this.zzctw != null) && (!this.zzctw.equals(""))) {
      paramacy.zzl(2, this.zzctw);
    }
    if ((this.version != null) && (!this.version.equals(""))) {
      paramacy.zzl(3, this.version);
    }
    super.zza(paramacy);
  }
  
  protected final int zzn()
  {
    int j = super.zzn();
    int i = j;
    if (this.zzbpb != 0) {
      i = j + acy.zzs(1, this.zzbpb);
    }
    j = i;
    if (this.zzctw != null)
    {
      j = i;
      if (!this.zzctw.equals("")) {
        j = i + acy.zzm(2, this.zzctw);
      }
    }
    i = j;
    if (this.version != null)
    {
      i = j;
      if (!this.version.equals("")) {
        i = j + acy.zzm(3, this.version);
      }
    }
    return i;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/ady.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */