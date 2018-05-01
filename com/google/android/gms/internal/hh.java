package com.google.android.gms.internal;

import java.io.IOException;
import java.util.Arrays;

public final class hh
  extends adj<hh>
{
  public byte[] zzbTM = ads.zzcsI;
  public String zzbTN = "";
  public double zzbTO = 0.0D;
  public float zzbTP = 0.0F;
  public long zzbTQ = 0L;
  public int zzbTR = 0;
  public int zzbTS = 0;
  public boolean zzbTT = false;
  public hf[] zzbTU = hf.zzEa();
  public hg[] zzbTV = hg.zzEb();
  public String[] zzbTW = ads.EMPTY_STRING_ARRAY;
  public long[] zzbTX = ads.zzcsD;
  public float[] zzbTY = ads.zzcsE;
  public long zzbTZ = 0L;
  
  public hh()
  {
    this.zzcso = null;
    this.zzcsx = -1;
  }
  
  public final boolean equals(Object paramObject)
  {
    if (paramObject == this) {}
    do
    {
      return true;
      if (!(paramObject instanceof hh)) {
        return false;
      }
      paramObject = (hh)paramObject;
      if (!Arrays.equals(this.zzbTM, ((hh)paramObject).zzbTM)) {
        return false;
      }
      if (this.zzbTN == null)
      {
        if (((hh)paramObject).zzbTN != null) {
          return false;
        }
      }
      else if (!this.zzbTN.equals(((hh)paramObject).zzbTN)) {
        return false;
      }
      if (Double.doubleToLongBits(this.zzbTO) != Double.doubleToLongBits(((hh)paramObject).zzbTO)) {
        return false;
      }
      if (Float.floatToIntBits(this.zzbTP) != Float.floatToIntBits(((hh)paramObject).zzbTP)) {
        return false;
      }
      if (this.zzbTQ != ((hh)paramObject).zzbTQ) {
        return false;
      }
      if (this.zzbTR != ((hh)paramObject).zzbTR) {
        return false;
      }
      if (this.zzbTS != ((hh)paramObject).zzbTS) {
        return false;
      }
      if (this.zzbTT != ((hh)paramObject).zzbTT) {
        return false;
      }
      if (!adn.equals(this.zzbTU, ((hh)paramObject).zzbTU)) {
        return false;
      }
      if (!adn.equals(this.zzbTV, ((hh)paramObject).zzbTV)) {
        return false;
      }
      if (!adn.equals(this.zzbTW, ((hh)paramObject).zzbTW)) {
        return false;
      }
      if (!adn.equals(this.zzbTX, ((hh)paramObject).zzbTX)) {
        return false;
      }
      if (!adn.equals(this.zzbTY, ((hh)paramObject).zzbTY)) {
        return false;
      }
      if (this.zzbTZ != ((hh)paramObject).zzbTZ) {
        return false;
      }
      if ((this.zzcso != null) && (!this.zzcso.isEmpty())) {
        break;
      }
    } while ((((hh)paramObject).zzcso == null) || (((hh)paramObject).zzcso.isEmpty()));
    return false;
    return this.zzcso.equals(((hh)paramObject).zzcso);
  }
  
  public final int hashCode()
  {
    int m = 0;
    int n = getClass().getName().hashCode();
    int i1 = Arrays.hashCode(this.zzbTM);
    int i;
    int i2;
    int i3;
    int i4;
    int i5;
    int i6;
    int j;
    label100:
    int i7;
    int i8;
    int i9;
    int i10;
    int i11;
    int i12;
    if (this.zzbTN == null)
    {
      i = 0;
      long l = Double.doubleToLongBits(this.zzbTO);
      i2 = (int)(l ^ l >>> 32);
      i3 = Float.floatToIntBits(this.zzbTP);
      i4 = (int)(this.zzbTQ ^ this.zzbTQ >>> 32);
      i5 = this.zzbTR;
      i6 = this.zzbTS;
      if (!this.zzbTT) {
        break label288;
      }
      j = 1231;
      i7 = adn.hashCode(this.zzbTU);
      i8 = adn.hashCode(this.zzbTV);
      i9 = adn.hashCode(this.zzbTW);
      i10 = adn.hashCode(this.zzbTX);
      i11 = adn.hashCode(this.zzbTY);
      i12 = (int)(this.zzbTZ ^ this.zzbTZ >>> 32);
      k = m;
      if (this.zzcso != null) {
        if (!this.zzcso.isEmpty()) {
          break label295;
        }
      }
    }
    label288:
    label295:
    for (int k = m;; k = this.zzcso.hashCode())
    {
      return (((((((j + ((((((i + ((n + 527) * 31 + i1) * 31) * 31 + i2) * 31 + i3) * 31 + i4) * 31 + i5) * 31 + i6) * 31) * 31 + i7) * 31 + i8) * 31 + i9) * 31 + i10) * 31 + i11) * 31 + i12) * 31 + k;
      i = this.zzbTN.hashCode();
      break;
      j = 1237;
      break label100;
    }
  }
  
  public final void zza(adh paramadh)
    throws IOException
  {
    int j = 0;
    if (!Arrays.equals(this.zzbTM, ads.zzcsI)) {
      paramadh.zzb(1, this.zzbTM);
    }
    if ((this.zzbTN != null) && (!this.zzbTN.equals(""))) {
      paramadh.zzl(2, this.zzbTN);
    }
    if (Double.doubleToLongBits(this.zzbTO) != Double.doubleToLongBits(0.0D)) {
      paramadh.zza(3, this.zzbTO);
    }
    if (Float.floatToIntBits(this.zzbTP) != Float.floatToIntBits(0.0F)) {
      paramadh.zzc(4, this.zzbTP);
    }
    if (this.zzbTQ != 0L) {
      paramadh.zzb(5, this.zzbTQ);
    }
    if (this.zzbTR != 0) {
      paramadh.zzr(6, this.zzbTR);
    }
    int i;
    if (this.zzbTS != 0)
    {
      i = this.zzbTS;
      paramadh.zzt(7, 0);
      paramadh.zzcu(adh.zzcw(i));
    }
    if (this.zzbTT) {
      paramadh.zzk(8, this.zzbTT);
    }
    Object localObject;
    if ((this.zzbTU != null) && (this.zzbTU.length > 0))
    {
      i = 0;
      while (i < this.zzbTU.length)
      {
        localObject = this.zzbTU[i];
        if (localObject != null) {
          paramadh.zza(9, (adp)localObject);
        }
        i += 1;
      }
    }
    if ((this.zzbTV != null) && (this.zzbTV.length > 0))
    {
      i = 0;
      while (i < this.zzbTV.length)
      {
        localObject = this.zzbTV[i];
        if (localObject != null) {
          paramadh.zza(10, (adp)localObject);
        }
        i += 1;
      }
    }
    if ((this.zzbTW != null) && (this.zzbTW.length > 0))
    {
      i = 0;
      while (i < this.zzbTW.length)
      {
        localObject = this.zzbTW[i];
        if (localObject != null) {
          paramadh.zzl(11, (String)localObject);
        }
        i += 1;
      }
    }
    if ((this.zzbTX != null) && (this.zzbTX.length > 0))
    {
      i = 0;
      while (i < this.zzbTX.length)
      {
        paramadh.zzb(12, this.zzbTX[i]);
        i += 1;
      }
    }
    if (this.zzbTZ != 0L) {
      paramadh.zzb(13, this.zzbTZ);
    }
    if ((this.zzbTY != null) && (this.zzbTY.length > 0))
    {
      i = j;
      while (i < this.zzbTY.length)
      {
        paramadh.zzc(14, this.zzbTY[i]);
        i += 1;
      }
    }
    super.zza(paramadh);
  }
  
  protected final int zzn()
  {
    int i2 = 0;
    int j = super.zzn();
    int i = j;
    if (!Arrays.equals(this.zzbTM, ads.zzcsI)) {
      i = j + adh.zzc(1, this.zzbTM);
    }
    j = i;
    if (this.zzbTN != null)
    {
      j = i;
      if (!this.zzbTN.equals("")) {
        j = i + adh.zzm(2, this.zzbTN);
      }
    }
    i = j;
    if (Double.doubleToLongBits(this.zzbTO) != Double.doubleToLongBits(0.0D)) {
      i = j + (adh.zzct(3) + 8);
    }
    j = i;
    if (Float.floatToIntBits(this.zzbTP) != Float.floatToIntBits(0.0F)) {
      j = i + (adh.zzct(4) + 4);
    }
    int k = j;
    if (this.zzbTQ != 0L) {
      k = j + adh.zze(5, this.zzbTQ);
    }
    i = k;
    if (this.zzbTR != 0) {
      i = k + adh.zzs(6, this.zzbTR);
    }
    j = i;
    if (this.zzbTS != 0)
    {
      j = this.zzbTS;
      k = adh.zzct(7);
      j = i + (adh.zzcv(adh.zzcw(j)) + k);
    }
    i = j;
    if (this.zzbTT) {
      i = j + (adh.zzct(8) + 1);
    }
    j = i;
    Object localObject;
    if (this.zzbTU != null)
    {
      j = i;
      if (this.zzbTU.length > 0)
      {
        j = 0;
        while (j < this.zzbTU.length)
        {
          localObject = this.zzbTU[j];
          k = i;
          if (localObject != null) {
            k = i + adh.zzb(9, (adp)localObject);
          }
          j += 1;
          i = k;
        }
        j = i;
      }
    }
    i = j;
    if (this.zzbTV != null)
    {
      i = j;
      if (this.zzbTV.length > 0)
      {
        i = j;
        j = 0;
        while (j < this.zzbTV.length)
        {
          localObject = this.zzbTV[j];
          k = i;
          if (localObject != null) {
            k = i + adh.zzb(10, (adp)localObject);
          }
          j += 1;
          i = k;
        }
      }
    }
    j = i;
    if (this.zzbTW != null)
    {
      j = i;
      if (this.zzbTW.length > 0)
      {
        j = 0;
        k = 0;
        int n;
        for (int m = 0; j < this.zzbTW.length; m = n)
        {
          localObject = this.zzbTW[j];
          int i1 = k;
          n = m;
          if (localObject != null)
          {
            n = m + 1;
            i1 = k + adh.zzhQ((String)localObject);
          }
          j += 1;
          k = i1;
        }
        j = i + k + m * 1;
      }
    }
    i = j;
    if (this.zzbTX != null)
    {
      i = j;
      if (this.zzbTX.length > 0)
      {
        k = 0;
        i = i2;
        while (i < this.zzbTX.length)
        {
          k += adh.zzaP(this.zzbTX[i]);
          i += 1;
        }
        i = j + k + this.zzbTX.length * 1;
      }
    }
    j = i;
    if (this.zzbTZ != 0L) {
      j = i + adh.zze(13, this.zzbTZ);
    }
    i = j;
    if (this.zzbTY != null)
    {
      i = j;
      if (this.zzbTY.length > 0) {
        i = j + this.zzbTY.length * 4 + this.zzbTY.length * 1;
      }
    }
    return i;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/hh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */