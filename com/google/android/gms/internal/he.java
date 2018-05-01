package com.google.android.gms.internal;

import java.io.IOException;

public final class he
  extends adj<he>
{
  public hf[] zzbTH = hf.zzEa();
  
  public he()
  {
    this.zzcso = null;
    this.zzcsx = -1;
  }
  
  public static he zzy(byte[] paramArrayOfByte)
    throws ado
  {
    return (he)adp.zza(new he(), paramArrayOfByte);
  }
  
  public final boolean equals(Object paramObject)
  {
    if (paramObject == this) {}
    do
    {
      return true;
      if (!(paramObject instanceof he)) {
        return false;
      }
      paramObject = (he)paramObject;
      if (!adn.equals(this.zzbTH, ((he)paramObject).zzbTH)) {
        return false;
      }
      if ((this.zzcso != null) && (!this.zzcso.isEmpty())) {
        break;
      }
    } while ((((he)paramObject).zzcso == null) || (((he)paramObject).zzcso.isEmpty()));
    return false;
    return this.zzcso.equals(((he)paramObject).zzcso);
  }
  
  public final int hashCode()
  {
    int j = getClass().getName().hashCode();
    int k = adn.hashCode(this.zzbTH);
    if ((this.zzcso == null) || (this.zzcso.isEmpty())) {}
    for (int i = 0;; i = this.zzcso.hashCode()) {
      return i + ((j + 527) * 31 + k) * 31;
    }
  }
  
  public final void zza(adh paramadh)
    throws IOException
  {
    if ((this.zzbTH != null) && (this.zzbTH.length > 0))
    {
      int i = 0;
      while (i < this.zzbTH.length)
      {
        hf localhf = this.zzbTH[i];
        if (localhf != null) {
          paramadh.zza(1, localhf);
        }
        i += 1;
      }
    }
    super.zza(paramadh);
  }
  
  protected final int zzn()
  {
    int i = super.zzn();
    int k = i;
    if (this.zzbTH != null)
    {
      k = i;
      if (this.zzbTH.length > 0)
      {
        int j = 0;
        for (;;)
        {
          k = i;
          if (j >= this.zzbTH.length) {
            break;
          }
          hf localhf = this.zzbTH[j];
          k = i;
          if (localhf != null) {
            k = i + adh.zzb(1, localhf);
          }
          j += 1;
          i = k;
        }
      }
    }
    return k;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/he.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */