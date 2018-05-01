package com.google.android.gms.internal;

import java.io.IOException;
import java.util.Arrays;

public final class aea
  extends ada<aea>
  implements Cloneable
{
  private String tag = "";
  private boolean zzccZ = false;
  private aec zzcmI = null;
  public long zzctB = 0L;
  public long zzctC = 0L;
  private long zzctD = 0L;
  public int zzctE = 0;
  private aeb[] zzctF = aeb.zzMc();
  private byte[] zzctG = adj.zzcst;
  private ady zzctH = null;
  public byte[] zzctI = adj.zzcst;
  private String zzctJ = "";
  private String zzctK = "";
  private adx zzctL = null;
  private String zzctM = "";
  public long zzctN = 180000L;
  private adz zzctO = null;
  public byte[] zzctP = adj.zzcst;
  private String zzctQ = "";
  private int zzctR = 0;
  private int[] zzctS = adj.zzcsn;
  private long zzctT = 0L;
  public int zzrD = 0;
  
  public aea()
  {
    this.zzcrZ = null;
    this.zzcsi = -1;
  }
  
  private final aea zzMb()
  {
    try
    {
      aea localaea = (aea)super.zzLL();
      if ((this.zzctF != null) && (this.zzctF.length > 0))
      {
        localaea.zzctF = new aeb[this.zzctF.length];
        int i = 0;
        while (i < this.zzctF.length)
        {
          if (this.zzctF[i] != null) {
            localaea.zzctF[i] = ((aeb)this.zzctF[i].clone());
          }
          i += 1;
        }
      }
      if (this.zzctH == null) {
        break label111;
      }
    }
    catch (CloneNotSupportedException localCloneNotSupportedException)
    {
      throw new AssertionError(localCloneNotSupportedException);
    }
    localCloneNotSupportedException.zzctH = ((ady)this.zzctH.clone());
    label111:
    if (this.zzctL != null) {
      localCloneNotSupportedException.zzctL = ((adx)this.zzctL.clone());
    }
    if (this.zzctO != null) {
      localCloneNotSupportedException.zzctO = ((adz)this.zzctO.clone());
    }
    if ((this.zzctS != null) && (this.zzctS.length > 0)) {
      localCloneNotSupportedException.zzctS = ((int[])this.zzctS.clone());
    }
    if (this.zzcmI != null) {
      localCloneNotSupportedException.zzcmI = ((aec)this.zzcmI.clone());
    }
    return localCloneNotSupportedException;
  }
  
  public final boolean equals(Object paramObject)
  {
    if (paramObject == this) {}
    do
    {
      return true;
      if (!(paramObject instanceof aea)) {
        return false;
      }
      paramObject = (aea)paramObject;
      if (this.zzctB != ((aea)paramObject).zzctB) {
        return false;
      }
      if (this.zzctC != ((aea)paramObject).zzctC) {
        return false;
      }
      if (this.zzctD != ((aea)paramObject).zzctD) {
        return false;
      }
      if (this.tag == null)
      {
        if (((aea)paramObject).tag != null) {
          return false;
        }
      }
      else if (!this.tag.equals(((aea)paramObject).tag)) {
        return false;
      }
      if (this.zzctE != ((aea)paramObject).zzctE) {
        return false;
      }
      if (this.zzrD != ((aea)paramObject).zzrD) {
        return false;
      }
      if (this.zzccZ != ((aea)paramObject).zzccZ) {
        return false;
      }
      if (!ade.equals(this.zzctF, ((aea)paramObject).zzctF)) {
        return false;
      }
      if (!Arrays.equals(this.zzctG, ((aea)paramObject).zzctG)) {
        return false;
      }
      if (this.zzctH == null)
      {
        if (((aea)paramObject).zzctH != null) {
          return false;
        }
      }
      else if (!this.zzctH.equals(((aea)paramObject).zzctH)) {
        return false;
      }
      if (!Arrays.equals(this.zzctI, ((aea)paramObject).zzctI)) {
        return false;
      }
      if (this.zzctJ == null)
      {
        if (((aea)paramObject).zzctJ != null) {
          return false;
        }
      }
      else if (!this.zzctJ.equals(((aea)paramObject).zzctJ)) {
        return false;
      }
      if (this.zzctK == null)
      {
        if (((aea)paramObject).zzctK != null) {
          return false;
        }
      }
      else if (!this.zzctK.equals(((aea)paramObject).zzctK)) {
        return false;
      }
      if (this.zzctL == null)
      {
        if (((aea)paramObject).zzctL != null) {
          return false;
        }
      }
      else if (!this.zzctL.equals(((aea)paramObject).zzctL)) {
        return false;
      }
      if (this.zzctM == null)
      {
        if (((aea)paramObject).zzctM != null) {
          return false;
        }
      }
      else if (!this.zzctM.equals(((aea)paramObject).zzctM)) {
        return false;
      }
      if (this.zzctN != ((aea)paramObject).zzctN) {
        return false;
      }
      if (this.zzctO == null)
      {
        if (((aea)paramObject).zzctO != null) {
          return false;
        }
      }
      else if (!this.zzctO.equals(((aea)paramObject).zzctO)) {
        return false;
      }
      if (!Arrays.equals(this.zzctP, ((aea)paramObject).zzctP)) {
        return false;
      }
      if (this.zzctQ == null)
      {
        if (((aea)paramObject).zzctQ != null) {
          return false;
        }
      }
      else if (!this.zzctQ.equals(((aea)paramObject).zzctQ)) {
        return false;
      }
      if (this.zzctR != ((aea)paramObject).zzctR) {
        return false;
      }
      if (!ade.equals(this.zzctS, ((aea)paramObject).zzctS)) {
        return false;
      }
      if (this.zzctT != ((aea)paramObject).zzctT) {
        return false;
      }
      if (this.zzcmI == null)
      {
        if (((aea)paramObject).zzcmI != null) {
          return false;
        }
      }
      else if (!this.zzcmI.equals(((aea)paramObject).zzcmI)) {
        return false;
      }
      if ((this.zzcrZ != null) && (!this.zzcrZ.isEmpty())) {
        break;
      }
    } while ((((aea)paramObject).zzcrZ == null) || (((aea)paramObject).zzcrZ.isEmpty()));
    return false;
    return this.zzcrZ.equals(((aea)paramObject).zzcrZ);
  }
  
  public final int hashCode()
  {
    int i7 = 0;
    int i8 = getClass().getName().hashCode();
    int i9 = (int)(this.zzctB ^ this.zzctB >>> 32);
    int i10 = (int)(this.zzctC ^ this.zzctC >>> 32);
    int i11 = (int)(this.zzctD ^ this.zzctD >>> 32);
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
      i12 = this.zzctE;
      i13 = this.zzrD;
      if (!this.zzccZ) {
        break label436;
      }
      j = 1231;
      i14 = ade.hashCode(this.zzctF);
      i15 = Arrays.hashCode(this.zzctG);
      if (this.zzctH != null) {
        break label443;
      }
      k = 0;
      i16 = Arrays.hashCode(this.zzctI);
      if (this.zzctJ != null) {
        break label454;
      }
      m = 0;
      if (this.zzctK != null) {
        break label466;
      }
      n = 0;
      if (this.zzctL != null) {
        break label478;
      }
      i1 = 0;
      if (this.zzctM != null) {
        break label490;
      }
      i2 = 0;
      i17 = (int)(this.zzctN ^ this.zzctN >>> 32);
      if (this.zzctO != null) {
        break label502;
      }
      i3 = 0;
      i18 = Arrays.hashCode(this.zzctP);
      if (this.zzctQ != null) {
        break label514;
      }
      i4 = 0;
      i19 = this.zzctR;
      i20 = ade.hashCode(this.zzctS);
      i21 = (int)(this.zzctT ^ this.zzctT >>> 32);
      if (this.zzcmI != null) {
        break label526;
      }
      i5 = 0;
      label252:
      i6 = i7;
      if (this.zzcrZ != null) {
        if (!this.zzcrZ.isEmpty()) {
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
    for (int i6 = i7;; i6 = this.zzcrZ.hashCode())
    {
      return (i5 + ((((i4 + ((i3 + ((i2 + (i1 + (n + (m + ((k + (((j + (((i + ((((i8 + 527) * 31 + i9) * 31 + i10) * 31 + i11) * 31) * 31 + i12) * 31 + i13) * 31) * 31 + i14) * 31 + i15) * 31) * 31 + i16) * 31) * 31) * 31) * 31) * 31 + i17) * 31) * 31 + i18) * 31) * 31 + i19) * 31 + i20) * 31 + i21) * 31) * 31 + i6;
      i = this.tag.hashCode();
      break;
      j = 1237;
      break label92;
      k = this.zzctH.hashCode();
      break label119;
      m = this.zzctJ.hashCode();
      break label138;
      n = this.zzctK.hashCode();
      break label148;
      i1 = this.zzctL.hashCode();
      break label158;
      i2 = this.zzctM.hashCode();
      break label168;
      i3 = this.zzctO.hashCode();
      break label193;
      i4 = this.zzctQ.hashCode();
      break label212;
      i5 = this.zzcmI.hashCode();
      break label252;
    }
  }
  
  public final void zza(acy paramacy)
    throws IOException
  {
    int j = 0;
    if (this.zzctB != 0L) {
      paramacy.zzb(1, this.zzctB);
    }
    if ((this.tag != null) && (!this.tag.equals(""))) {
      paramacy.zzl(2, this.tag);
    }
    int i;
    if ((this.zzctF != null) && (this.zzctF.length > 0))
    {
      i = 0;
      while (i < this.zzctF.length)
      {
        aeb localaeb = this.zzctF[i];
        if (localaeb != null) {
          paramacy.zza(3, localaeb);
        }
        i += 1;
      }
    }
    if (!Arrays.equals(this.zzctG, adj.zzcst)) {
      paramacy.zzb(4, this.zzctG);
    }
    if (!Arrays.equals(this.zzctI, adj.zzcst)) {
      paramacy.zzb(6, this.zzctI);
    }
    if (this.zzctL != null) {
      paramacy.zza(7, this.zzctL);
    }
    if ((this.zzctJ != null) && (!this.zzctJ.equals(""))) {
      paramacy.zzl(8, this.zzctJ);
    }
    if (this.zzctH != null) {
      paramacy.zza(9, this.zzctH);
    }
    if (this.zzccZ) {
      paramacy.zzk(10, this.zzccZ);
    }
    if (this.zzctE != 0) {
      paramacy.zzr(11, this.zzctE);
    }
    if (this.zzrD != 0) {
      paramacy.zzr(12, this.zzrD);
    }
    if ((this.zzctK != null) && (!this.zzctK.equals(""))) {
      paramacy.zzl(13, this.zzctK);
    }
    if ((this.zzctM != null) && (!this.zzctM.equals(""))) {
      paramacy.zzl(14, this.zzctM);
    }
    if (this.zzctN != 180000L) {
      paramacy.zzd(15, this.zzctN);
    }
    if (this.zzctO != null) {
      paramacy.zza(16, this.zzctO);
    }
    if (this.zzctC != 0L) {
      paramacy.zzb(17, this.zzctC);
    }
    if (!Arrays.equals(this.zzctP, adj.zzcst)) {
      paramacy.zzb(18, this.zzctP);
    }
    if (this.zzctR != 0) {
      paramacy.zzr(19, this.zzctR);
    }
    if ((this.zzctS != null) && (this.zzctS.length > 0))
    {
      i = j;
      while (i < this.zzctS.length)
      {
        paramacy.zzr(20, this.zzctS[i]);
        i += 1;
      }
    }
    if (this.zzctD != 0L) {
      paramacy.zzb(21, this.zzctD);
    }
    if (this.zzctT != 0L) {
      paramacy.zzb(22, this.zzctT);
    }
    if (this.zzcmI != null) {
      paramacy.zza(23, this.zzcmI);
    }
    if ((this.zzctQ != null) && (!this.zzctQ.equals(""))) {
      paramacy.zzl(24, this.zzctQ);
    }
    super.zza(paramacy);
  }
  
  protected final int zzn()
  {
    int m = 0;
    int i = super.zzn();
    int j = i;
    if (this.zzctB != 0L) {
      j = i + acy.zze(1, this.zzctB);
    }
    i = j;
    if (this.tag != null)
    {
      i = j;
      if (!this.tag.equals("")) {
        i = j + acy.zzm(2, this.tag);
      }
    }
    j = i;
    int k;
    if (this.zzctF != null)
    {
      j = i;
      if (this.zzctF.length > 0)
      {
        j = 0;
        while (j < this.zzctF.length)
        {
          aeb localaeb = this.zzctF[j];
          k = i;
          if (localaeb != null) {
            k = i + acy.zzb(3, localaeb);
          }
          j += 1;
          i = k;
        }
        j = i;
      }
    }
    i = j;
    if (!Arrays.equals(this.zzctG, adj.zzcst)) {
      i = j + acy.zzc(4, this.zzctG);
    }
    j = i;
    if (!Arrays.equals(this.zzctI, adj.zzcst)) {
      j = i + acy.zzc(6, this.zzctI);
    }
    i = j;
    if (this.zzctL != null) {
      i = j + acy.zzb(7, this.zzctL);
    }
    j = i;
    if (this.zzctJ != null)
    {
      j = i;
      if (!this.zzctJ.equals("")) {
        j = i + acy.zzm(8, this.zzctJ);
      }
    }
    i = j;
    if (this.zzctH != null) {
      i = j + acy.zzb(9, this.zzctH);
    }
    j = i;
    if (this.zzccZ) {
      j = i + (acy.zzct(10) + 1);
    }
    i = j;
    if (this.zzctE != 0) {
      i = j + acy.zzs(11, this.zzctE);
    }
    j = i;
    if (this.zzrD != 0) {
      j = i + acy.zzs(12, this.zzrD);
    }
    i = j;
    if (this.zzctK != null)
    {
      i = j;
      if (!this.zzctK.equals("")) {
        i = j + acy.zzm(13, this.zzctK);
      }
    }
    j = i;
    if (this.zzctM != null)
    {
      j = i;
      if (!this.zzctM.equals("")) {
        j = i + acy.zzm(14, this.zzctM);
      }
    }
    i = j;
    if (this.zzctN != 180000L) {
      i = j + acy.zzf(15, this.zzctN);
    }
    j = i;
    if (this.zzctO != null) {
      j = i + acy.zzb(16, this.zzctO);
    }
    i = j;
    if (this.zzctC != 0L) {
      i = j + acy.zze(17, this.zzctC);
    }
    j = i;
    if (!Arrays.equals(this.zzctP, adj.zzcst)) {
      j = i + acy.zzc(18, this.zzctP);
    }
    i = j;
    if (this.zzctR != 0) {
      i = j + acy.zzs(19, this.zzctR);
    }
    j = i;
    if (this.zzctS != null)
    {
      j = i;
      if (this.zzctS.length > 0)
      {
        k = 0;
        j = m;
        while (j < this.zzctS.length)
        {
          k += acy.zzcr(this.zzctS[j]);
          j += 1;
        }
        j = i + k + this.zzctS.length * 2;
      }
    }
    i = j;
    if (this.zzctD != 0L) {
      i = j + acy.zze(21, this.zzctD);
    }
    j = i;
    if (this.zzctT != 0L) {
      j = i + acy.zze(22, this.zzctT);
    }
    i = j;
    if (this.zzcmI != null) {
      i = j + acy.zzb(23, this.zzcmI);
    }
    j = i;
    if (this.zzctQ != null)
    {
      j = i;
      if (!this.zzctQ.equals("")) {
        j = i + acy.zzm(24, this.zzctQ);
      }
    }
    return j;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/aea.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */