package com.google.android.gms.internal;

import java.io.IOException;
import java.util.Arrays;

public final class aej
  extends adj<aej>
  implements Cloneable
{
  private String tag = "";
  private boolean zzccZ = false;
  private ael zzcmX = null;
  public long zzctQ = 0L;
  public long zzctR = 0L;
  private long zzctS = 0L;
  public int zzctT = 0;
  private aek[] zzctU = aek.zzMe();
  private byte[] zzctV = ads.zzcsI;
  private aeh zzctW = null;
  public byte[] zzctX = ads.zzcsI;
  private String zzctY = "";
  private String zzctZ = "";
  private aeg zzcua = null;
  private String zzcub = "";
  public long zzcuc = 180000L;
  private aei zzcud = null;
  public byte[] zzcue = ads.zzcsI;
  private String zzcuf = "";
  private int zzcug = 0;
  private int[] zzcuh = ads.zzcsC;
  private long zzcui = 0L;
  public int zzrB = 0;
  
  public aej()
  {
    this.zzcso = null;
    this.zzcsx = -1;
  }
  
  private final aej zzMd()
  {
    try
    {
      aej localaej = (aej)super.zzLN();
      if ((this.zzctU != null) && (this.zzctU.length > 0))
      {
        localaej.zzctU = new aek[this.zzctU.length];
        int i = 0;
        while (i < this.zzctU.length)
        {
          if (this.zzctU[i] != null) {
            localaej.zzctU[i] = ((aek)this.zzctU[i].clone());
          }
          i += 1;
        }
      }
      if (this.zzctW == null) {
        break label111;
      }
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new AssertionError(localCloneNotSupportedException);
    }
    localCloneNotSupportedException.zzctW = ((aeh)this.zzctW.clone());
    label111:
    if (this.zzcua != null) {
      localCloneNotSupportedException.zzcua = ((aeg)this.zzcua.clone());
    }
    if (this.zzcud != null) {
      localCloneNotSupportedException.zzcud = ((aei)this.zzcud.clone());
    }
    if ((this.zzcuh != null) && (this.zzcuh.length > 0)) {
      localCloneNotSupportedException.zzcuh = ((int[])this.zzcuh.clone());
    }
    if (this.zzcmX != null) {
      localCloneNotSupportedException.zzcmX = ((ael)this.zzcmX.clone());
    }
    return localCloneNotSupportedException;
  }
  
  public final boolean equals(Object paramObject)
  {
    if (paramObject == this) {}
    do
    {
      return true;
      if (!(paramObject instanceof aej)) {
        return false;
      }
      paramObject = (aej)paramObject;
      if (this.zzctQ != ((aej)paramObject).zzctQ) {
        return false;
      }
      if (this.zzctR != ((aej)paramObject).zzctR) {
        return false;
      }
      if (this.zzctS != ((aej)paramObject).zzctS) {
        return false;
      }
      if (this.tag == null)
      {
        if (((aej)paramObject).tag != null) {
          return false;
        }
      }
      else if (!this.tag.equals(((aej)paramObject).tag)) {
        return false;
      }
      if (this.zzctT != ((aej)paramObject).zzctT) {
        return false;
      }
      if (this.zzrB != ((aej)paramObject).zzrB) {
        return false;
      }
      if (this.zzccZ != ((aej)paramObject).zzccZ) {
        return false;
      }
      if (!adn.equals(this.zzctU, ((aej)paramObject).zzctU)) {
        return false;
      }
      if (!Arrays.equals(this.zzctV, ((aej)paramObject).zzctV)) {
        return false;
      }
      if (this.zzctW == null)
      {
        if (((aej)paramObject).zzctW != null) {
          return false;
        }
      }
      else if (!this.zzctW.equals(((aej)paramObject).zzctW)) {
        return false;
      }
      if (!Arrays.equals(this.zzctX, ((aej)paramObject).zzctX)) {
        return false;
      }
      if (this.zzctY == null)
      {
        if (((aej)paramObject).zzctY != null) {
          return false;
        }
      }
      else if (!this.zzctY.equals(((aej)paramObject).zzctY)) {
        return false;
      }
      if (this.zzctZ == null)
      {
        if (((aej)paramObject).zzctZ != null) {
          return false;
        }
      }
      else if (!this.zzctZ.equals(((aej)paramObject).zzctZ)) {
        return false;
      }
      if (this.zzcua == null)
      {
        if (((aej)paramObject).zzcua != null) {
          return false;
        }
      }
      else if (!this.zzcua.equals(((aej)paramObject).zzcua)) {
        return false;
      }
      if (this.zzcub == null)
      {
        if (((aej)paramObject).zzcub != null) {
          return false;
        }
      }
      else if (!this.zzcub.equals(((aej)paramObject).zzcub)) {
        return false;
      }
      if (this.zzcuc != ((aej)paramObject).zzcuc) {
        return false;
      }
      if (this.zzcud == null)
      {
        if (((aej)paramObject).zzcud != null) {
          return false;
        }
      }
      else if (!this.zzcud.equals(((aej)paramObject).zzcud)) {
        return false;
      }
      if (!Arrays.equals(this.zzcue, ((aej)paramObject).zzcue)) {
        return false;
      }
      if (this.zzcuf == null)
      {
        if (((aej)paramObject).zzcuf != null) {
          return false;
        }
      }
      else if (!this.zzcuf.equals(((aej)paramObject).zzcuf)) {
        return false;
      }
      if (this.zzcug != ((aej)paramObject).zzcug) {
        return false;
      }
      if (!adn.equals(this.zzcuh, ((aej)paramObject).zzcuh)) {
        return false;
      }
      if (this.zzcui != ((aej)paramObject).zzcui) {
        return false;
      }
      if (this.zzcmX == null)
      {
        if (((aej)paramObject).zzcmX != null) {
          return false;
        }
      }
      else if (!this.zzcmX.equals(((aej)paramObject).zzcmX)) {
        return false;
      }
      if ((this.zzcso != null) && (!this.zzcso.isEmpty())) {
        break;
      }
    } while ((((aej)paramObject).zzcso == null) || (((aej)paramObject).zzcso.isEmpty()));
    return false;
    return this.zzcso.equals(((aej)paramObject).zzcso);
  }
  
  public final int hashCode()
  {
    int i7 = 0;
    int i8 = getClass().getName().hashCode();
    int i9 = (int)(this.zzctQ ^ this.zzctQ >>> 32);
    int i10 = (int)(this.zzctR ^ this.zzctR >>> 32);
    int i11 = (int)(this.zzctS ^ this.zzctS >>> 32);
    int i;
    int i12;
    int i13;
    int j;
    label92:
    int i14;
    int i15;
    int k;
    label119:
    int i16;
    int m;
    label138:
    int n;
    label148:
    int i1;
    label158:
    int i2;
    label168:
    int i17;
    int i3;
    label193:
    int i18;
    int i4;
    label212:
    int i19;
    int i20;
    int i21;
    int i5;
    if (this.tag == null)
    {
      i = 0;
      i12 = this.zzctT;
      i13 = this.zzrB;
      if (!this.zzccZ) {
        break label436;
      }
      j = 1231;
      i14 = adn.hashCode(this.zzctU);
      i15 = Arrays.hashCode(this.zzctV);
      if (this.zzctW != null) {
        break label443;
      }
      k = 0;
      i16 = Arrays.hashCode(this.zzctX);
      if (this.zzctY != null) {
        break label454;
      }
      m = 0;
      if (this.zzctZ != null) {
        break label466;
      }
      n = 0;
      if (this.zzcua != null) {
        break label478;
      }
      i1 = 0;
      if (this.zzcub != null) {
        break label490;
      }
      i2 = 0;
      i17 = (int)(this.zzcuc ^ this.zzcuc >>> 32);
      if (this.zzcud != null) {
        break label502;
      }
      i3 = 0;
      i18 = Arrays.hashCode(this.zzcue);
      if (this.zzcuf != null) {
        break label514;
      }
      i4 = 0;
      i19 = this.zzcug;
      i20 = adn.hashCode(this.zzcuh);
      i21 = (int)(this.zzcui ^ this.zzcui >>> 32);
      if (this.zzcmX != null) {
        break label526;
      }
      i5 = 0;
      label252:
      i6 = i7;
      if (this.zzcso != null) {
        if (!this.zzcso.isEmpty()) {
          break label538;
        }
      }
    }
    label436:
    label443:
    label454:
    label466:
    label478:
    label490:
    label502:
    label514:
    label526:
    label538:
    for (int i6 = i7;; i6 = this.zzcso.hashCode())
    {
      return (i5 + ((((i4 + ((i3 + ((i2 + (i1 + (n + (m + ((k + (((j + (((i + ((((i8 + 527) * 31 + i9) * 31 + i10) * 31 + i11) * 31) * 31 + i12) * 31 + i13) * 31) * 31 + i14) * 31 + i15) * 31) * 31 + i16) * 31) * 31) * 31) * 31) * 31 + i17) * 31) * 31 + i18) * 31) * 31 + i19) * 31 + i20) * 31 + i21) * 31) * 31 + i6;
      i = this.tag.hashCode();
      break;
      j = 1237;
      break label92;
      k = this.zzctW.hashCode();
      break label119;
      m = this.zzctY.hashCode();
      break label138;
      n = this.zzctZ.hashCode();
      break label148;
      i1 = this.zzcua.hashCode();
      break label158;
      i2 = this.zzcub.hashCode();
      break label168;
      i3 = this.zzcud.hashCode();
      break label193;
      i4 = this.zzcuf.hashCode();
      break label212;
      i5 = this.zzcmX.hashCode();
      break label252;
    }
  }
  
  public final void zza(adh paramadh)
    throws IOException
  {
    int j = 0;
    if (this.zzctQ != 0L) {
      paramadh.zzb(1, this.zzctQ);
    }
    if ((this.tag != null) && (!this.tag.equals(""))) {
      paramadh.zzl(2, this.tag);
    }
    int i;
    if ((this.zzctU != null) && (this.zzctU.length > 0))
    {
      i = 0;
      while (i < this.zzctU.length)
      {
        aek localaek = this.zzctU[i];
        if (localaek != null) {
          paramadh.zza(3, localaek);
        }
        i += 1;
      }
    }
    if (!Arrays.equals(this.zzctV, ads.zzcsI)) {
      paramadh.zzb(4, this.zzctV);
    }
    if (!Arrays.equals(this.zzctX, ads.zzcsI)) {
      paramadh.zzb(6, this.zzctX);
    }
    if (this.zzcua != null) {
      paramadh.zza(7, this.zzcua);
    }
    if ((this.zzctY != null) && (!this.zzctY.equals(""))) {
      paramadh.zzl(8, this.zzctY);
    }
    if (this.zzctW != null) {
      paramadh.zza(9, this.zzctW);
    }
    if (this.zzccZ) {
      paramadh.zzk(10, this.zzccZ);
    }
    if (this.zzctT != 0) {
      paramadh.zzr(11, this.zzctT);
    }
    if (this.zzrB != 0) {
      paramadh.zzr(12, this.zzrB);
    }
    if ((this.zzctZ != null) && (!this.zzctZ.equals(""))) {
      paramadh.zzl(13, this.zzctZ);
    }
    if ((this.zzcub != null) && (!this.zzcub.equals(""))) {
      paramadh.zzl(14, this.zzcub);
    }
    if (this.zzcuc != 180000L) {
      paramadh.zzd(15, this.zzcuc);
    }
    if (this.zzcud != null) {
      paramadh.zza(16, this.zzcud);
    }
    if (this.zzctR != 0L) {
      paramadh.zzb(17, this.zzctR);
    }
    if (!Arrays.equals(this.zzcue, ads.zzcsI)) {
      paramadh.zzb(18, this.zzcue);
    }
    if (this.zzcug != 0) {
      paramadh.zzr(19, this.zzcug);
    }
    if ((this.zzcuh != null) && (this.zzcuh.length > 0))
    {
      i = j;
      while (i < this.zzcuh.length)
      {
        paramadh.zzr(20, this.zzcuh[i]);
        i += 1;
      }
    }
    if (this.zzctS != 0L) {
      paramadh.zzb(21, this.zzctS);
    }
    if (this.zzcui != 0L) {
      paramadh.zzb(22, this.zzcui);
    }
    if (this.zzcmX != null) {
      paramadh.zza(23, this.zzcmX);
    }
    if ((this.zzcuf != null) && (!this.zzcuf.equals(""))) {
      paramadh.zzl(24, this.zzcuf);
    }
    super.zza(paramadh);
  }
  
  protected final int zzn()
  {
    int m = 0;
    int i = super.zzn();
    int j = i;
    if (this.zzctQ != 0L) {
      j = i + adh.zze(1, this.zzctQ);
    }
    i = j;
    if (this.tag != null)
    {
      i = j;
      if (!this.tag.equals("")) {
        i = j + adh.zzm(2, this.tag);
      }
    }
    j = i;
    int k;
    if (this.zzctU != null)
    {
      j = i;
      if (this.zzctU.length > 0)
      {
        j = 0;
        while (j < this.zzctU.length)
        {
          aek localaek = this.zzctU[j];
          k = i;
          if (localaek != null) {
            k = i + adh.zzb(3, localaek);
          }
          j += 1;
          i = k;
        }
        j = i;
      }
    }
    i = j;
    if (!Arrays.equals(this.zzctV, ads.zzcsI)) {
      i = j + adh.zzc(4, this.zzctV);
    }
    j = i;
    if (!Arrays.equals(this.zzctX, ads.zzcsI)) {
      j = i + adh.zzc(6, this.zzctX);
    }
    i = j;
    if (this.zzcua != null) {
      i = j + adh.zzb(7, this.zzcua);
    }
    j = i;
    if (this.zzctY != null)
    {
      j = i;
      if (!this.zzctY.equals("")) {
        j = i + adh.zzm(8, this.zzctY);
      }
    }
    i = j;
    if (this.zzctW != null) {
      i = j + adh.zzb(9, this.zzctW);
    }
    j = i;
    if (this.zzccZ) {
      j = i + (adh.zzct(10) + 1);
    }
    i = j;
    if (this.zzctT != 0) {
      i = j + adh.zzs(11, this.zzctT);
    }
    j = i;
    if (this.zzrB != 0) {
      j = i + adh.zzs(12, this.zzrB);
    }
    i = j;
    if (this.zzctZ != null)
    {
      i = j;
      if (!this.zzctZ.equals("")) {
        i = j + adh.zzm(13, this.zzctZ);
      }
    }
    j = i;
    if (this.zzcub != null)
    {
      j = i;
      if (!this.zzcub.equals("")) {
        j = i + adh.zzm(14, this.zzcub);
      }
    }
    i = j;
    if (this.zzcuc != 180000L) {
      i = j + adh.zzf(15, this.zzcuc);
    }
    j = i;
    if (this.zzcud != null) {
      j = i + adh.zzb(16, this.zzcud);
    }
    i = j;
    if (this.zzctR != 0L) {
      i = j + adh.zze(17, this.zzctR);
    }
    j = i;
    if (!Arrays.equals(this.zzcue, ads.zzcsI)) {
      j = i + adh.zzc(18, this.zzcue);
    }
    i = j;
    if (this.zzcug != 0) {
      i = j + adh.zzs(19, this.zzcug);
    }
    j = i;
    if (this.zzcuh != null)
    {
      j = i;
      if (this.zzcuh.length > 0)
      {
        k = 0;
        j = m;
        while (j < this.zzcuh.length)
        {
          k += adh.zzcr(this.zzcuh[j]);
          j += 1;
        }
        j = i + k + this.zzcuh.length * 2;
      }
    }
    i = j;
    if (this.zzctS != 0L) {
      i = j + adh.zze(21, this.zzctS);
    }
    j = i;
    if (this.zzcui != 0L) {
      j = i + adh.zze(22, this.zzcui);
    }
    i = j;
    if (this.zzcmX != null) {
      i = j + adh.zzb(23, this.zzcmX);
    }
    j = i;
    if (this.zzcuf != null)
    {
      j = i;
      if (!this.zzcuf.equals("")) {
        j = i + adh.zzm(24, this.zzcuf);
      }
    }
    return j;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/aej.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */