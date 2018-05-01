package com.google.android.gms.internal;

import java.io.IOException;

public final class aen
  extends adp
{
  public long zzaLt = 0L;
  public String zzcun = "";
  public String zzcuo = "";
  public long zzcup = 0L;
  public String zzcuq = "";
  public long zzcur = 0L;
  public String zzcus = "";
  public String zzcut = "";
  public String zzcuu = "";
  public String zzcuv = "";
  public String zzcuw = "";
  public int zzcux = 0;
  public aem[] zzcuy = aem.zzMh();
  
  public aen()
  {
    this.zzcsx = -1;
  }
  
  public static aen zzL(byte[] paramArrayOfByte)
    throws ado
  {
    return (aen)adp.zza(new aen(), paramArrayOfByte);
  }
  
  public final void zza(adh paramadh)
    throws IOException
  {
    if ((this.zzcun != null) && (!this.zzcun.equals(""))) {
      paramadh.zzl(1, this.zzcun);
    }
    if ((this.zzcuo != null) && (!this.zzcuo.equals(""))) {
      paramadh.zzl(2, this.zzcuo);
    }
    if (this.zzcup != 0L) {
      paramadh.zzb(3, this.zzcup);
    }
    if ((this.zzcuq != null) && (!this.zzcuq.equals(""))) {
      paramadh.zzl(4, this.zzcuq);
    }
    if (this.zzcur != 0L) {
      paramadh.zzb(5, this.zzcur);
    }
    if (this.zzaLt != 0L) {
      paramadh.zzb(6, this.zzaLt);
    }
    if ((this.zzcus != null) && (!this.zzcus.equals(""))) {
      paramadh.zzl(7, this.zzcus);
    }
    if ((this.zzcut != null) && (!this.zzcut.equals(""))) {
      paramadh.zzl(8, this.zzcut);
    }
    if ((this.zzcuu != null) && (!this.zzcuu.equals(""))) {
      paramadh.zzl(9, this.zzcuu);
    }
    if ((this.zzcuv != null) && (!this.zzcuv.equals(""))) {
      paramadh.zzl(10, this.zzcuv);
    }
    if ((this.zzcuw != null) && (!this.zzcuw.equals(""))) {
      paramadh.zzl(11, this.zzcuw);
    }
    if (this.zzcux != 0) {
      paramadh.zzr(12, this.zzcux);
    }
    if ((this.zzcuy != null) && (this.zzcuy.length > 0))
    {
      int i = 0;
      while (i < this.zzcuy.length)
      {
        aem localaem = this.zzcuy[i];
        if (localaem != null) {
          paramadh.zza(13, localaem);
        }
        i += 1;
      }
    }
    super.zza(paramadh);
  }
  
  protected final int zzn()
  {
    int j = super.zzn();
    int i = j;
    if (this.zzcun != null)
    {
      i = j;
      if (!this.zzcun.equals("")) {
        i = j + adh.zzm(1, this.zzcun);
      }
    }
    j = i;
    if (this.zzcuo != null)
    {
      j = i;
      if (!this.zzcuo.equals("")) {
        j = i + adh.zzm(2, this.zzcuo);
      }
    }
    i = j;
    if (this.zzcup != 0L) {
      i = j + adh.zze(3, this.zzcup);
    }
    j = i;
    if (this.zzcuq != null)
    {
      j = i;
      if (!this.zzcuq.equals("")) {
        j = i + adh.zzm(4, this.zzcuq);
      }
    }
    i = j;
    if (this.zzcur != 0L) {
      i = j + adh.zze(5, this.zzcur);
    }
    j = i;
    if (this.zzaLt != 0L) {
      j = i + adh.zze(6, this.zzaLt);
    }
    i = j;
    if (this.zzcus != null)
    {
      i = j;
      if (!this.zzcus.equals("")) {
        i = j + adh.zzm(7, this.zzcus);
      }
    }
    j = i;
    if (this.zzcut != null)
    {
      j = i;
      if (!this.zzcut.equals("")) {
        j = i + adh.zzm(8, this.zzcut);
      }
    }
    i = j;
    if (this.zzcuu != null)
    {
      i = j;
      if (!this.zzcuu.equals("")) {
        i = j + adh.zzm(9, this.zzcuu);
      }
    }
    j = i;
    if (this.zzcuv != null)
    {
      j = i;
      if (!this.zzcuv.equals("")) {
        j = i + adh.zzm(10, this.zzcuv);
      }
    }
    int k = j;
    if (this.zzcuw != null)
    {
      k = j;
      if (!this.zzcuw.equals("")) {
        k = j + adh.zzm(11, this.zzcuw);
      }
    }
    i = k;
    if (this.zzcux != 0) {
      i = k + adh.zzs(12, this.zzcux);
    }
    j = i;
    if (this.zzcuy != null)
    {
      j = i;
      if (this.zzcuy.length > 0)
      {
        j = 0;
        while (j < this.zzcuy.length)
        {
          aem localaem = this.zzcuy[j];
          k = i;
          if (localaem != null) {
            k = i + adh.zzb(13, localaem);
          }
          j += 1;
          i = k;
        }
        j = i;
      }
    }
    return j;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/aen.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */