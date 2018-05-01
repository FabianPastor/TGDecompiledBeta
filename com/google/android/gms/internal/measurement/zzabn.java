package com.google.android.gms.internal.measurement;

import java.io.IOException;

public final class zzabn
  extends zzabd<zzabn>
{
  private static volatile zzabn[] zzcaf;
  public String zzcag = "";
  
  public zzabn()
  {
    this.zzbzh = null;
    this.zzbzs = -1;
  }
  
  public static zzabn[] zzwh()
  {
    if (zzcaf == null) {}
    synchronized (zzabh.zzbzr)
    {
      if (zzcaf == null) {
        zzcaf = new zzabn[0];
      }
      return zzcaf;
    }
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
    return j;
  }
  
  public final void zza(zzabb paramzzabb)
    throws IOException
  {
    if ((this.zzcag != null) && (!this.zzcag.equals(""))) {
      paramzzabb.zzc(1, this.zzcag);
    }
    super.zza(paramzzabb);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzabn.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */