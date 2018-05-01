package com.google.android.gms.internal;

import java.io.IOException;

public final class ael
  extends adj<ael>
  implements Cloneable
{
  private int zzcuk = -1;
  private int zzcul = 0;
  
  public ael()
  {
    this.zzcso = null;
    this.zzcsx = -1;
  }
  
  private ael zzMg()
  {
    try
    {
      ael localael = (ael)super.zzLN();
      return localael;
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
      if (!(paramObject instanceof ael)) {
        return false;
      }
      paramObject = (ael)paramObject;
      if (this.zzcuk != ((ael)paramObject).zzcuk) {
        return false;
      }
      if (this.zzcul != ((ael)paramObject).zzcul) {
        return false;
      }
      if ((this.zzcso != null) && (!this.zzcso.isEmpty())) {
        break;
      }
    } while ((((ael)paramObject).zzcso == null) || (((ael)paramObject).zzcso.isEmpty()));
    return false;
    return this.zzcso.equals(((ael)paramObject).zzcso);
  }
  
  public final int hashCode()
  {
    int j = getClass().getName().hashCode();
    int k = this.zzcuk;
    int m = this.zzcul;
    if ((this.zzcso == null) || (this.zzcso.isEmpty())) {}
    for (int i = 0;; i = this.zzcso.hashCode()) {
      return i + (((j + 527) * 31 + k) * 31 + m) * 31;
    }
  }
  
  public final void zza(adh paramadh)
    throws IOException
  {
    if (this.zzcuk != -1) {
      paramadh.zzr(1, this.zzcuk);
    }
    if (this.zzcul != 0) {
      paramadh.zzr(2, this.zzcul);
    }
    super.zza(paramadh);
  }
  
  protected final int zzn()
  {
    int j = super.zzn();
    int i = j;
    if (this.zzcuk != -1) {
      i = j + adh.zzs(1, this.zzcuk);
    }
    j = i;
    if (this.zzcul != 0) {
      j = i + adh.zzs(2, this.zzcul);
    }
    return j;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/ael.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */