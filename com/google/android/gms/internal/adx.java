package com.google.android.gms.internal;

import java.io.IOException;

public final class adx
  extends ada<adx>
  implements Cloneable
{
  private String[] zzctr = adj.EMPTY_STRING_ARRAY;
  private String[] zzcts = adj.EMPTY_STRING_ARRAY;
  private int[] zzctt = adj.zzcsn;
  private long[] zzctu = adj.zzcso;
  private long[] zzctv = adj.zzcso;
  
  public adx()
  {
    this.zzcrZ = null;
    this.zzcsi = -1;
  }
  
  private adx zzLY()
  {
    try
    {
      adx localadx = (adx)super.zzLL();
      if ((this.zzctr != null) && (this.zzctr.length > 0)) {
        localadx.zzctr = ((String[])this.zzctr.clone());
      }
      if ((this.zzcts != null) && (this.zzcts.length > 0)) {
        localadx.zzcts = ((String[])this.zzcts.clone());
      }
      if ((this.zzctt != null) && (this.zzctt.length > 0)) {
        localadx.zzctt = ((int[])this.zzctt.clone());
      }
      if ((this.zzctu != null) && (this.zzctu.length > 0)) {
        localadx.zzctu = ((long[])this.zzctu.clone());
      }
      if ((this.zzctv != null) && (this.zzctv.length > 0)) {
        localadx.zzctv = ((long[])this.zzctv.clone());
      }
      return localadx;
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
      if (!(paramObject instanceof adx)) {
        return false;
      }
      paramObject = (adx)paramObject;
      if (!ade.equals(this.zzctr, ((adx)paramObject).zzctr)) {
        return false;
      }
      if (!ade.equals(this.zzcts, ((adx)paramObject).zzcts)) {
        return false;
      }
      if (!ade.equals(this.zzctt, ((adx)paramObject).zzctt)) {
        return false;
      }
      if (!ade.equals(this.zzctu, ((adx)paramObject).zzctu)) {
        return false;
      }
      if (!ade.equals(this.zzctv, ((adx)paramObject).zzctv)) {
        return false;
      }
      if ((this.zzcrZ != null) && (!this.zzcrZ.isEmpty())) {
        break;
      }
    } while ((((adx)paramObject).zzcrZ == null) || (((adx)paramObject).zzcrZ.isEmpty()));
    return false;
    return this.zzcrZ.equals(((adx)paramObject).zzcrZ);
  }
  
  public final int hashCode()
  {
    int j = getClass().getName().hashCode();
    int k = ade.hashCode(this.zzctr);
    int m = ade.hashCode(this.zzcts);
    int n = ade.hashCode(this.zzctt);
    int i1 = ade.hashCode(this.zzctu);
    int i2 = ade.hashCode(this.zzctv);
    if ((this.zzcrZ == null) || (this.zzcrZ.isEmpty())) {}
    for (int i = 0;; i = this.zzcrZ.hashCode()) {
      return i + ((((((j + 527) * 31 + k) * 31 + m) * 31 + n) * 31 + i1) * 31 + i2) * 31;
    }
  }
  
  public final void zza(acy paramacy)
    throws IOException
  {
    int j = 0;
    int i;
    String str;
    if ((this.zzctr != null) && (this.zzctr.length > 0))
    {
      i = 0;
      while (i < this.zzctr.length)
      {
        str = this.zzctr[i];
        if (str != null) {
          paramacy.zzl(1, str);
        }
        i += 1;
      }
    }
    if ((this.zzcts != null) && (this.zzcts.length > 0))
    {
      i = 0;
      while (i < this.zzcts.length)
      {
        str = this.zzcts[i];
        if (str != null) {
          paramacy.zzl(2, str);
        }
        i += 1;
      }
    }
    if ((this.zzctt != null) && (this.zzctt.length > 0))
    {
      i = 0;
      while (i < this.zzctt.length)
      {
        paramacy.zzr(3, this.zzctt[i]);
        i += 1;
      }
    }
    if ((this.zzctu != null) && (this.zzctu.length > 0))
    {
      i = 0;
      while (i < this.zzctu.length)
      {
        paramacy.zzb(4, this.zzctu[i]);
        i += 1;
      }
    }
    if ((this.zzctv != null) && (this.zzctv.length > 0))
    {
      i = j;
      while (i < this.zzctv.length)
      {
        paramacy.zzb(5, this.zzctv[i]);
        i += 1;
      }
    }
    super.zza(paramacy);
  }
  
  protected final int zzn()
  {
    int i2 = 0;
    int i1 = super.zzn();
    int j;
    int k;
    String str;
    int n;
    int m;
    if ((this.zzctr != null) && (this.zzctr.length > 0))
    {
      i = 0;
      j = 0;
      for (k = 0; i < this.zzctr.length; k = m)
      {
        str = this.zzctr[i];
        n = j;
        m = k;
        if (str != null)
        {
          m = k + 1;
          n = j + acy.zzhQ(str);
        }
        i += 1;
        j = n;
      }
    }
    for (int i = i1 + j + k * 1;; i = i1)
    {
      j = i;
      if (this.zzcts != null)
      {
        j = i;
        if (this.zzcts.length > 0)
        {
          j = 0;
          k = 0;
          for (m = 0; j < this.zzcts.length; m = n)
          {
            str = this.zzcts[j];
            i1 = k;
            n = m;
            if (str != null)
            {
              n = m + 1;
              i1 = k + acy.zzhQ(str);
            }
            j += 1;
            k = i1;
          }
          j = i + k + m * 1;
        }
      }
      i = j;
      if (this.zzctt != null)
      {
        i = j;
        if (this.zzctt.length > 0)
        {
          i = 0;
          k = 0;
          while (i < this.zzctt.length)
          {
            k += acy.zzcr(this.zzctt[i]);
            i += 1;
          }
          i = j + k + this.zzctt.length * 1;
        }
      }
      j = i;
      if (this.zzctu != null)
      {
        j = i;
        if (this.zzctu.length > 0)
        {
          j = 0;
          k = 0;
          while (j < this.zzctu.length)
          {
            k += acy.zzaP(this.zzctu[j]);
            j += 1;
          }
          j = i + k + this.zzctu.length * 1;
        }
      }
      i = j;
      if (this.zzctv != null)
      {
        i = j;
        if (this.zzctv.length > 0)
        {
          k = 0;
          i = i2;
          while (i < this.zzctv.length)
          {
            k += acy.zzaP(this.zzctv[i]);
            i += 1;
          }
          i = j + k + this.zzctv.length * 1;
        }
      }
      return i;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/adx.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */