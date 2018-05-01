package com.google.android.gms.internal;

import java.io.IOException;
import java.util.Arrays;

public final class aei
  extends adj<aei>
  implements Cloneable
{
  private byte[] zzctM = ads.zzcsI;
  private String zzctN = "";
  private byte[][] zzctO = ads.zzcsH;
  private boolean zzctP = false;
  
  public aei()
  {
    this.zzcso = null;
    this.zzcsx = -1;
  }
  
  private aei zzMc()
  {
    try
    {
      aei localaei = (aei)super.zzLN();
      if ((this.zzctO != null) && (this.zzctO.length > 0)) {
        localaei.zzctO = ((byte[][])this.zzctO.clone());
      }
      return localaei;
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
      if (!(paramObject instanceof aei)) {
        return false;
      }
      paramObject = (aei)paramObject;
      if (!Arrays.equals(this.zzctM, ((aei)paramObject).zzctM)) {
        return false;
      }
      if (this.zzctN == null)
      {
        if (((aei)paramObject).zzctN != null) {
          return false;
        }
      }
      else if (!this.zzctN.equals(((aei)paramObject).zzctN)) {
        return false;
      }
      if (!adn.zza(this.zzctO, ((aei)paramObject).zzctO)) {
        return false;
      }
      if (this.zzctP != ((aei)paramObject).zzctP) {
        return false;
      }
      if ((this.zzcso != null) && (!this.zzcso.isEmpty())) {
        break;
      }
    } while ((((aei)paramObject).zzcso == null) || (((aei)paramObject).zzcso.isEmpty()));
    return false;
    return this.zzcso.equals(((aei)paramObject).zzcso);
  }
  
  public final int hashCode()
  {
    int m = 0;
    int n = getClass().getName().hashCode();
    int i1 = Arrays.hashCode(this.zzctM);
    int i;
    int i2;
    int j;
    if (this.zzctN == null)
    {
      i = 0;
      i2 = adn.zzc(this.zzctO);
      if (!this.zzctP) {
        break label121;
      }
      j = 1231;
      label53:
      k = m;
      if (this.zzcso != null) {
        if (!this.zzcso.isEmpty()) {
          break label128;
        }
      }
    }
    label121:
    label128:
    for (int k = m;; k = this.zzcso.hashCode())
    {
      return (j + ((i + ((n + 527) * 31 + i1) * 31) * 31 + i2) * 31) * 31 + k;
      i = this.zzctN.hashCode();
      break;
      j = 1237;
      break label53;
    }
  }
  
  public final void zza(adh paramadh)
    throws IOException
  {
    if (!Arrays.equals(this.zzctM, ads.zzcsI)) {
      paramadh.zzb(1, this.zzctM);
    }
    if ((this.zzctO != null) && (this.zzctO.length > 0))
    {
      int i = 0;
      while (i < this.zzctO.length)
      {
        byte[] arrayOfByte = this.zzctO[i];
        if (arrayOfByte != null) {
          paramadh.zzb(2, arrayOfByte);
        }
        i += 1;
      }
    }
    if (this.zzctP) {
      paramadh.zzk(3, this.zzctP);
    }
    if ((this.zzctN != null) && (!this.zzctN.equals(""))) {
      paramadh.zzl(4, this.zzctN);
    }
    super.zza(paramadh);
  }
  
  protected final int zzn()
  {
    int n = 0;
    int j = super.zzn();
    int i = j;
    if (!Arrays.equals(this.zzctM, ads.zzcsI)) {
      i = j + adh.zzc(1, this.zzctM);
    }
    j = i;
    if (this.zzctO != null)
    {
      j = i;
      if (this.zzctO.length > 0)
      {
        int k = 0;
        int m = 0;
        j = n;
        while (j < this.zzctO.length)
        {
          byte[] arrayOfByte = this.zzctO[j];
          int i1 = k;
          n = m;
          if (arrayOfByte != null)
          {
            n = m + 1;
            i1 = k + adh.zzJ(arrayOfByte);
          }
          j += 1;
          k = i1;
          m = n;
        }
        j = i + k + m * 1;
      }
    }
    i = j;
    if (this.zzctP) {
      i = j + (adh.zzct(3) + 1);
    }
    j = i;
    if (this.zzctN != null)
    {
      j = i;
      if (!this.zzctN.equals("")) {
        j = i + adh.zzm(4, this.zzctN);
      }
    }
    return j;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/aei.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */