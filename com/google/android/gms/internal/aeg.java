package com.google.android.gms.internal;

import java.io.IOException;

public final class aeg
  extends adj<aeg>
  implements Cloneable
{
  private String[] zzctG = ads.EMPTY_STRING_ARRAY;
  private String[] zzctH = ads.EMPTY_STRING_ARRAY;
  private int[] zzctI = ads.zzcsC;
  private long[] zzctJ = ads.zzcsD;
  private long[] zzctK = ads.zzcsD;
  
  public aeg()
  {
    this.zzcso = null;
    this.zzcsx = -1;
  }
  
  private aeg zzMa()
  {
    try
    {
      aeg localaeg = (aeg)super.zzLN();
      if ((this.zzctG != null) && (this.zzctG.length > 0)) {
        localaeg.zzctG = ((String[])this.zzctG.clone());
      }
      if ((this.zzctH != null) && (this.zzctH.length > 0)) {
        localaeg.zzctH = ((String[])this.zzctH.clone());
      }
      if ((this.zzctI != null) && (this.zzctI.length > 0)) {
        localaeg.zzctI = ((int[])this.zzctI.clone());
      }
      if ((this.zzctJ != null) && (this.zzctJ.length > 0)) {
        localaeg.zzctJ = ((long[])this.zzctJ.clone());
      }
      if ((this.zzctK != null) && (this.zzctK.length > 0)) {
        localaeg.zzctK = ((long[])this.zzctK.clone());
      }
      return localaeg;
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
      if (!(paramObject instanceof aeg)) {
        return false;
      }
      paramObject = (aeg)paramObject;
      if (!adn.equals(this.zzctG, ((aeg)paramObject).zzctG)) {
        return false;
      }
      if (!adn.equals(this.zzctH, ((aeg)paramObject).zzctH)) {
        return false;
      }
      if (!adn.equals(this.zzctI, ((aeg)paramObject).zzctI)) {
        return false;
      }
      if (!adn.equals(this.zzctJ, ((aeg)paramObject).zzctJ)) {
        return false;
      }
      if (!adn.equals(this.zzctK, ((aeg)paramObject).zzctK)) {
        return false;
      }
      if ((this.zzcso != null) && (!this.zzcso.isEmpty())) {
        break;
      }
    } while ((((aeg)paramObject).zzcso == null) || (((aeg)paramObject).zzcso.isEmpty()));
    return false;
    return this.zzcso.equals(((aeg)paramObject).zzcso);
  }
  
  public final int hashCode()
  {
    int j = getClass().getName().hashCode();
    int k = adn.hashCode(this.zzctG);
    int m = adn.hashCode(this.zzctH);
    int n = adn.hashCode(this.zzctI);
    int i1 = adn.hashCode(this.zzctJ);
    int i2 = adn.hashCode(this.zzctK);
    if ((this.zzcso == null) || (this.zzcso.isEmpty())) {}
    for (int i = 0;; i = this.zzcso.hashCode()) {
      return i + ((((((j + 527) * 31 + k) * 31 + m) * 31 + n) * 31 + i1) * 31 + i2) * 31;
    }
  }
  
  public final void zza(adh paramadh)
    throws IOException
  {
    int j = 0;
    int i;
    String str;
    if ((this.zzctG != null) && (this.zzctG.length > 0))
    {
      i = 0;
      while (i < this.zzctG.length)
      {
        str = this.zzctG[i];
        if (str != null) {
          paramadh.zzl(1, str);
        }
        i += 1;
      }
    }
    if ((this.zzctH != null) && (this.zzctH.length > 0))
    {
      i = 0;
      while (i < this.zzctH.length)
      {
        str = this.zzctH[i];
        if (str != null) {
          paramadh.zzl(2, str);
        }
        i += 1;
      }
    }
    if ((this.zzctI != null) && (this.zzctI.length > 0))
    {
      i = 0;
      while (i < this.zzctI.length)
      {
        paramadh.zzr(3, this.zzctI[i]);
        i += 1;
      }
    }
    if ((this.zzctJ != null) && (this.zzctJ.length > 0))
    {
      i = 0;
      while (i < this.zzctJ.length)
      {
        paramadh.zzb(4, this.zzctJ[i]);
        i += 1;
      }
    }
    if ((this.zzctK != null) && (this.zzctK.length > 0))
    {
      i = j;
      while (i < this.zzctK.length)
      {
        paramadh.zzb(5, this.zzctK[i]);
        i += 1;
      }
    }
    super.zza(paramadh);
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
    if ((this.zzctG != null) && (this.zzctG.length > 0))
    {
      i = 0;
      j = 0;
      for (k = 0; i < this.zzctG.length; k = m)
      {
        str = this.zzctG[i];
        n = j;
        m = k;
        if (str != null)
        {
          m = k + 1;
          n = j + adh.zzhQ(str);
        }
        i += 1;
        j = n;
      }
    }
    for (int i = i1 + j + k * 1;; i = i1)
    {
      j = i;
      if (this.zzctH != null)
      {
        j = i;
        if (this.zzctH.length > 0)
        {
          j = 0;
          k = 0;
          for (m = 0; j < this.zzctH.length; m = n)
          {
            str = this.zzctH[j];
            i1 = k;
            n = m;
            if (str != null)
            {
              n = m + 1;
              i1 = k + adh.zzhQ(str);
            }
            j += 1;
            k = i1;
          }
          j = i + k + m * 1;
        }
      }
      i = j;
      if (this.zzctI != null)
      {
        i = j;
        if (this.zzctI.length > 0)
        {
          i = 0;
          k = 0;
          while (i < this.zzctI.length)
          {
            k += adh.zzcr(this.zzctI[i]);
            i += 1;
          }
          i = j + k + this.zzctI.length * 1;
        }
      }
      j = i;
      if (this.zzctJ != null)
      {
        j = i;
        if (this.zzctJ.length > 0)
        {
          j = 0;
          k = 0;
          while (j < this.zzctJ.length)
          {
            k += adh.zzaP(this.zzctJ[j]);
            j += 1;
          }
          j = i + k + this.zzctJ.length * 1;
        }
      }
      i = j;
      if (this.zzctK != null)
      {
        i = j;
        if (this.zzctK.length > 0)
        {
          k = 0;
          i = i2;
          while (i < this.zzctK.length)
          {
            k += adh.zzaP(this.zzctK[i]);
            i += 1;
          }
          i = j + k + this.zzctK.length * 1;
        }
      }
      return i;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/aeg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */