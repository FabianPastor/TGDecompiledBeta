package com.google.android.gms.internal;

import java.io.IOException;

public final class aee
  extends adg
{
  public long zzaLt = 0L;
  public String zzctY = "";
  public String zzctZ = "";
  public long zzcua = 0L;
  public String zzcub = "";
  public long zzcuc = 0L;
  public String zzcud = "";
  public String zzcue = "";
  public String zzcuf = "";
  public String zzcug = "";
  public String zzcuh = "";
  public int zzcui = 0;
  public aed[] zzcuj = aed.zzMf();
  
  public aee()
  {
    this.zzcsi = -1;
  }
  
  public static aee zzL(byte[] paramArrayOfByte)
    throws adf
  {
    return (aee)adg.zza(new aee(), paramArrayOfByte);
  }
  
  public final void zza(acy paramacy)
    throws IOException
  {
    if ((this.zzctY != null) && (!this.zzctY.equals(""))) {
      paramacy.zzl(1, this.zzctY);
    }
    if ((this.zzctZ != null) && (!this.zzctZ.equals(""))) {
      paramacy.zzl(2, this.zzctZ);
    }
    if (this.zzcua != 0L) {
      paramacy.zzb(3, this.zzcua);
    }
    if ((this.zzcub != null) && (!this.zzcub.equals(""))) {
      paramacy.zzl(4, this.zzcub);
    }
    if (this.zzcuc != 0L) {
      paramacy.zzb(5, this.zzcuc);
    }
    if (this.zzaLt != 0L) {
      paramacy.zzb(6, this.zzaLt);
    }
    if ((this.zzcud != null) && (!this.zzcud.equals(""))) {
      paramacy.zzl(7, this.zzcud);
    }
    if ((this.zzcue != null) && (!this.zzcue.equals(""))) {
      paramacy.zzl(8, this.zzcue);
    }
    if ((this.zzcuf != null) && (!this.zzcuf.equals(""))) {
      paramacy.zzl(9, this.zzcuf);
    }
    if ((this.zzcug != null) && (!this.zzcug.equals(""))) {
      paramacy.zzl(10, this.zzcug);
    }
    if ((this.zzcuh != null) && (!this.zzcuh.equals(""))) {
      paramacy.zzl(11, this.zzcuh);
    }
    if (this.zzcui != 0) {
      paramacy.zzr(12, this.zzcui);
    }
    if ((this.zzcuj != null) && (this.zzcuj.length > 0))
    {
      int i = 0;
      while (i < this.zzcuj.length)
      {
        aed localaed = this.zzcuj[i];
        if (localaed != null) {
          paramacy.zza(13, localaed);
        }
        i += 1;
      }
    }
    super.zza(paramacy);
  }
  
  protected final int zzn()
  {
    int j = super.zzn();
    int i = j;
    if (this.zzctY != null)
    {
      i = j;
      if (!this.zzctY.equals("")) {
        i = j + acy.zzm(1, this.zzctY);
      }
    }
    j = i;
    if (this.zzctZ != null)
    {
      j = i;
      if (!this.zzctZ.equals("")) {
        j = i + acy.zzm(2, this.zzctZ);
      }
    }
    i = j;
    if (this.zzcua != 0L) {
      i = j + acy.zze(3, this.zzcua);
    }
    j = i;
    if (this.zzcub != null)
    {
      j = i;
      if (!this.zzcub.equals("")) {
        j = i + acy.zzm(4, this.zzcub);
      }
    }
    i = j;
    if (this.zzcuc != 0L) {
      i = j + acy.zze(5, this.zzcuc);
    }
    j = i;
    if (this.zzaLt != 0L) {
      j = i + acy.zze(6, this.zzaLt);
    }
    i = j;
    if (this.zzcud != null)
    {
      i = j;
      if (!this.zzcud.equals("")) {
        i = j + acy.zzm(7, this.zzcud);
      }
    }
    j = i;
    if (this.zzcue != null)
    {
      j = i;
      if (!this.zzcue.equals("")) {
        j = i + acy.zzm(8, this.zzcue);
      }
    }
    i = j;
    if (this.zzcuf != null)
    {
      i = j;
      if (!this.zzcuf.equals("")) {
        i = j + acy.zzm(9, this.zzcuf);
      }
    }
    j = i;
    if (this.zzcug != null)
    {
      j = i;
      if (!this.zzcug.equals("")) {
        j = i + acy.zzm(10, this.zzcug);
      }
    }
    int k = j;
    if (this.zzcuh != null)
    {
      k = j;
      if (!this.zzcuh.equals("")) {
        k = j + acy.zzm(11, this.zzcuh);
      }
    }
    i = k;
    if (this.zzcui != 0) {
      i = k + acy.zzs(12, this.zzcui);
    }
    j = i;
    if (this.zzcuj != null)
    {
      j = i;
      if (this.zzcuj.length > 0)
      {
        j = 0;
        while (j < this.zzcuj.length)
        {
          aed localaed = this.zzcuj[j];
          k = i;
          if (localaed != null) {
            k = i + acy.zzb(13, localaed);
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/aee.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */