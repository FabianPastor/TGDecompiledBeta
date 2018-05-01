package com.google.android.gms.internal.measurement;

import java.io.IOException;

public final class zzabo
  extends zzabd<zzabo>
{
  private String zzbsl = "";
  public String zzbsm = "";
  public String zzbsn = "";
  public String zzbso = "";
  public String zzbsp = "";
  public String zzcag = "";
  public String zzcah = "";
  public long zzcai = 0L;
  public String zzcaj = "";
  public long zzcak = 0L;
  public int zzcal = 0;
  public zzabn[] zzcam = zzabn.zzwh();
  public long zzrp = 0L;
  
  public zzabo()
  {
    this.zzbzh = null;
    this.zzbzs = -1;
  }
  
  protected final int zza()
  {
    int i = super.zza();
    int j = i;
    if (this.zzcag != null)
    {
      j = i;
      if (!this.zzcag.equals("")) {
        j = i + zzabb.zzd(1, this.zzcag);
      }
    }
    i = j;
    if (this.zzcah != null)
    {
      i = j;
      if (!this.zzcah.equals("")) {
        i = j + zzabb.zzd(2, this.zzcah);
      }
    }
    j = i;
    if (this.zzcai != 0L) {
      j = i + zzabb.zzc(3, this.zzcai);
    }
    i = j;
    if (this.zzcaj != null)
    {
      i = j;
      if (!this.zzcaj.equals("")) {
        i = j + zzabb.zzd(4, this.zzcaj);
      }
    }
    j = i;
    if (this.zzcak != 0L) {
      j = i + zzabb.zzc(5, this.zzcak);
    }
    i = j;
    if (this.zzrp != 0L) {
      i = j + zzabb.zzc(6, this.zzrp);
    }
    j = i;
    if (this.zzbsl != null)
    {
      j = i;
      if (!this.zzbsl.equals("")) {
        j = i + zzabb.zzd(7, this.zzbsl);
      }
    }
    i = j;
    if (this.zzbsm != null)
    {
      i = j;
      if (!this.zzbsm.equals("")) {
        i = j + zzabb.zzd(8, this.zzbsm);
      }
    }
    int k = i;
    if (this.zzbsp != null)
    {
      k = i;
      if (!this.zzbsp.equals("")) {
        k = i + zzabb.zzd(9, this.zzbsp);
      }
    }
    j = k;
    if (this.zzbsn != null)
    {
      j = k;
      if (!this.zzbsn.equals("")) {
        j = k + zzabb.zzd(10, this.zzbsn);
      }
    }
    i = j;
    if (this.zzbso != null)
    {
      i = j;
      if (!this.zzbso.equals("")) {
        i = j + zzabb.zzd(11, this.zzbso);
      }
    }
    j = i;
    if (this.zzcal != 0) {
      j = i + zzabb.zzf(12, this.zzcal);
    }
    i = j;
    if (this.zzcam != null)
    {
      i = j;
      if (this.zzcam.length > 0)
      {
        k = 0;
        while (k < this.zzcam.length)
        {
          zzabn localzzabn = this.zzcam[k];
          i = j;
          if (localzzabn != null) {
            i = j + zzabb.zzb(13, localzzabn);
          }
          k++;
          j = i;
        }
        i = j;
      }
    }
    return i;
  }
  
  public final void zza(zzabb paramzzabb)
    throws IOException
  {
    if ((this.zzcag != null) && (!this.zzcag.equals(""))) {
      paramzzabb.zzc(1, this.zzcag);
    }
    if ((this.zzcah != null) && (!this.zzcah.equals(""))) {
      paramzzabb.zzc(2, this.zzcah);
    }
    if (this.zzcai != 0L) {
      paramzzabb.zzb(3, this.zzcai);
    }
    if ((this.zzcaj != null) && (!this.zzcaj.equals(""))) {
      paramzzabb.zzc(4, this.zzcaj);
    }
    if (this.zzcak != 0L) {
      paramzzabb.zzb(5, this.zzcak);
    }
    if (this.zzrp != 0L) {
      paramzzabb.zzb(6, this.zzrp);
    }
    if ((this.zzbsl != null) && (!this.zzbsl.equals(""))) {
      paramzzabb.zzc(7, this.zzbsl);
    }
    if ((this.zzbsm != null) && (!this.zzbsm.equals(""))) {
      paramzzabb.zzc(8, this.zzbsm);
    }
    if ((this.zzbsp != null) && (!this.zzbsp.equals(""))) {
      paramzzabb.zzc(9, this.zzbsp);
    }
    if ((this.zzbsn != null) && (!this.zzbsn.equals(""))) {
      paramzzabb.zzc(10, this.zzbsn);
    }
    if ((this.zzbso != null) && (!this.zzbso.equals(""))) {
      paramzzabb.zzc(11, this.zzbso);
    }
    if (this.zzcal != 0) {
      paramzzabb.zze(12, this.zzcal);
    }
    if ((this.zzcam != null) && (this.zzcam.length > 0)) {
      for (int i = 0; i < this.zzcam.length; i++)
      {
        zzabn localzzabn = this.zzcam[i];
        if (localzzabn != null) {
          paramzzabb.zza(13, localzzabn);
        }
      }
    }
    super.zza(paramzzabb);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzabo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */