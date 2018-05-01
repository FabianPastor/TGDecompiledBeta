package com.google.android.gms.internal;

import java.io.IOException;
import java.util.Arrays;

public final class adz
  extends ada<adz>
  implements Cloneable
{
  private boolean zzctA = false;
  private byte[] zzctx = adj.zzcst;
  private String zzcty = "";
  private byte[][] zzctz = adj.zzcss;
  
  public adz()
  {
    this.zzcrZ = null;
    this.zzcsi = -1;
  }
  
  private adz zzMa()
  {
    try
    {
      adz localadz = (adz)super.zzLL();
      if ((this.zzctz != null) && (this.zzctz.length > 0)) {
        localadz.zzctz = ((byte[][])this.zzctz.clone());
      }
      return localadz;
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
      if (!(paramObject instanceof adz)) {
        return false;
      }
      paramObject = (adz)paramObject;
      if (!Arrays.equals(this.zzctx, ((adz)paramObject).zzctx)) {
        return false;
      }
      if (this.zzcty == null)
      {
        if (((adz)paramObject).zzcty != null) {
          return false;
        }
      }
      else if (!this.zzcty.equals(((adz)paramObject).zzcty)) {
        return false;
      }
      if (!ade.zza(this.zzctz, ((adz)paramObject).zzctz)) {
        return false;
      }
      if (this.zzctA != ((adz)paramObject).zzctA) {
        return false;
      }
      if ((this.zzcrZ != null) && (!this.zzcrZ.isEmpty())) {
        break;
      }
    } while ((((adz)paramObject).zzcrZ == null) || (((adz)paramObject).zzcrZ.isEmpty()));
    return false;
    return this.zzcrZ.equals(((adz)paramObject).zzcrZ);
  }
  
  public final int hashCode()
  {
    int m = 0;
    int n = getClass().getName().hashCode();
    int i1 = Arrays.hashCode(this.zzctx);
    int i;
    int i2;
    int j;
    if (this.zzcty == null)
    {
      i = 0;
      i2 = ade.zzc(this.zzctz);
      if (!this.zzctA) {
        break label121;
      }
      j = 1231;
      label53:
      k = m;
      if (this.zzcrZ != null) {
        if (!this.zzcrZ.isEmpty()) {
          break label128;
        }
      }
    }
    label121:
    label128:
    for (int k = m;; k = this.zzcrZ.hashCode())
    {
      return (j + ((i + ((n + 527) * 31 + i1) * 31) * 31 + i2) * 31) * 31 + k;
      i = this.zzcty.hashCode();
      break;
      j = 1237;
      break label53;
    }
  }
  
  public final void zza(acy paramacy)
    throws IOException
  {
    if (!Arrays.equals(this.zzctx, adj.zzcst)) {
      paramacy.zzb(1, this.zzctx);
    }
    if ((this.zzctz != null) && (this.zzctz.length > 0))
    {
      int i = 0;
      while (i < this.zzctz.length)
      {
        byte[] arrayOfByte = this.zzctz[i];
        if (arrayOfByte != null) {
          paramacy.zzb(2, arrayOfByte);
        }
        i += 1;
      }
    }
    if (this.zzctA) {
      paramacy.zzk(3, this.zzctA);
    }
    if ((this.zzcty != null) && (!this.zzcty.equals(""))) {
      paramacy.zzl(4, this.zzcty);
    }
    super.zza(paramacy);
  }
  
  protected final int zzn()
  {
    int n = 0;
    int j = super.zzn();
    int i = j;
    if (!Arrays.equals(this.zzctx, adj.zzcst)) {
      i = j + acy.zzc(1, this.zzctx);
    }
    j = i;
    if (this.zzctz != null)
    {
      j = i;
      if (this.zzctz.length > 0)
      {
        int k = 0;
        int m = 0;
        j = n;
        while (j < this.zzctz.length)
        {
          byte[] arrayOfByte = this.zzctz[j];
          int i1 = k;
          n = m;
          if (arrayOfByte != null)
          {
            n = m + 1;
            i1 = k + acy.zzJ(arrayOfByte);
          }
          j += 1;
          k = i1;
          m = n;
        }
        j = i + k + m * 1;
      }
    }
    i = j;
    if (this.zzctA) {
      i = j + (acy.zzct(3) + 1);
    }
    j = i;
    if (this.zzcty != null)
    {
      j = i;
      if (!this.zzcty.equals("")) {
        j = i + acy.zzm(4, this.zzcty);
      }
    }
    return j;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/adz.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */