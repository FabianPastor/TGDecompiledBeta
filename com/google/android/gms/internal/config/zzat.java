package com.google.android.gms.internal.config;

import java.io.IOException;
import java.util.Arrays;

public final class zzat
  extends zzbb<zzat>
{
  private static volatile zzat[] zzbi;
  public String zzbj = "";
  public byte[] zzbk = zzbk.zzde;
  
  public zzat()
  {
    this.zzci = null;
    this.zzcr = -1;
  }
  
  public static zzat[] zzv()
  {
    if (zzbi == null) {}
    synchronized (zzbf.zzcq)
    {
      if (zzbi == null) {
        zzbi = new zzat[0];
      }
      return zzbi;
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
      if (!(paramObject instanceof zzat))
      {
        bool2 = false;
      }
      else
      {
        paramObject = (zzat)paramObject;
        if (this.zzbj == null)
        {
          if (((zzat)paramObject).zzbj != null) {
            bool2 = false;
          }
        }
        else if (!this.zzbj.equals(((zzat)paramObject).zzbj))
        {
          bool2 = false;
          continue;
        }
        if (!Arrays.equals(this.zzbk, ((zzat)paramObject).zzbk))
        {
          bool2 = false;
        }
        else if ((this.zzci == null) || (this.zzci.isEmpty()))
        {
          bool2 = bool1;
          if (((zzat)paramObject).zzci != null)
          {
            bool2 = bool1;
            if (!((zzat)paramObject).zzci.isEmpty()) {
              bool2 = false;
            }
          }
        }
        else
        {
          bool2 = this.zzci.equals(((zzat)paramObject).zzci);
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
    if (this.zzbj == null)
    {
      k = 0;
      m = Arrays.hashCode(this.zzbk);
      n = i;
      if (this.zzci != null) {
        if (!this.zzci.isEmpty()) {
          break label88;
        }
      }
    }
    label88:
    for (int n = i;; n = this.zzci.hashCode())
    {
      return ((k + (j + 527) * 31) * 31 + m) * 31 + n;
      k = this.zzbj.hashCode();
      break;
    }
  }
  
  public final void zza(zzaz paramzzaz)
    throws IOException
  {
    if ((this.zzbj != null) && (!this.zzbj.equals(""))) {
      paramzzaz.zza(1, this.zzbj);
    }
    if (!Arrays.equals(this.zzbk, zzbk.zzde)) {
      paramzzaz.zza(2, this.zzbk);
    }
    super.zza(paramzzaz);
  }
  
  protected final int zzu()
  {
    int i = super.zzu();
    int j = i;
    if (this.zzbj != null)
    {
      j = i;
      if (!this.zzbj.equals("")) {
        j = i + zzaz.zzb(1, this.zzbj);
      }
    }
    i = j;
    if (!Arrays.equals(this.zzbk, zzbk.zzde))
    {
      byte[] arrayOfByte = this.zzbk;
      i = zzaz.zzl(2);
      i = j + (zzaz.zzb(arrayOfByte) + i);
    }
    return i;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/config/zzat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */