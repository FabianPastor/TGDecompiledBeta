package com.google.android.gms.internal;

import java.io.IOException;

public final class aec
  extends ada<aec>
  implements Cloneable
{
  private int zzctV = -1;
  private int zzctW = 0;
  
  public aec()
  {
    this.zzcrZ = null;
    this.zzcsi = -1;
  }
  
  private aec zzMe()
  {
    try
    {
      aec localaec = (aec)super.zzLL();
      return localaec;
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
      if (!(paramObject instanceof aec)) {
        return false;
      }
      paramObject = (aec)paramObject;
      if (this.zzctV != ((aec)paramObject).zzctV) {
        return false;
      }
      if (this.zzctW != ((aec)paramObject).zzctW) {
        return false;
      }
      if ((this.zzcrZ != null) && (!this.zzcrZ.isEmpty())) {
        break;
      }
    } while ((((aec)paramObject).zzcrZ == null) || (((aec)paramObject).zzcrZ.isEmpty()));
    return false;
    return this.zzcrZ.equals(((aec)paramObject).zzcrZ);
  }
  
  public final int hashCode()
  {
    int j = getClass().getName().hashCode();
    int k = this.zzctV;
    int m = this.zzctW;
    if ((this.zzcrZ == null) || (this.zzcrZ.isEmpty())) {}
    for (int i = 0;; i = this.zzcrZ.hashCode()) {
      return i + (((j + 527) * 31 + k) * 31 + m) * 31;
    }
  }
  
  public final void zza(acy paramacy)
    throws IOException
  {
    if (this.zzctV != -1) {
      paramacy.zzr(1, this.zzctV);
    }
    if (this.zzctW != 0) {
      paramacy.zzr(2, this.zzctW);
    }
    super.zza(paramacy);
  }
  
  protected final int zzn()
  {
    int j = super.zzn();
    int i = j;
    if (this.zzctV != -1) {
      i = j + acy.zzs(1, this.zzctV);
    }
    j = i;
    if (this.zzctW != 0) {
      j = i + acy.zzs(2, this.zzctW);
    }
    return j;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/aec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */