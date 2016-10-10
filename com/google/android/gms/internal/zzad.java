package com.google.android.gms.internal;

import java.io.IOException;

public abstract interface zzad
{
  public static final class zza
    extends zzare<zza>
  {
    public String stackTrace = null;
    public String zzck = null;
    public Long zzcl = null;
    public String zzcm = null;
    public String zzcn = null;
    public Long zzco = null;
    public Long zzcp = null;
    public String zzcq = null;
    public Long zzcr = null;
    public String zzcs = null;
    
    public zza()
    {
      this.bqE = -1;
    }
    
    public zza zza(zzarc paramzzarc)
      throws IOException
    {
      for (;;)
      {
        int i = paramzzarc.cw();
        switch (i)
        {
        default: 
          if (super.zza(paramzzarc, i)) {}
          break;
        case 0: 
          return this;
        case 10: 
          this.zzck = paramzzarc.readString();
          break;
        case 16: 
          this.zzcl = Long.valueOf(paramzzarc.cz());
          break;
        case 26: 
          this.stackTrace = paramzzarc.readString();
          break;
        case 34: 
          this.zzcm = paramzzarc.readString();
          break;
        case 42: 
          this.zzcn = paramzzarc.readString();
          break;
        case 48: 
          this.zzco = Long.valueOf(paramzzarc.cz());
          break;
        case 56: 
          this.zzcp = Long.valueOf(paramzzarc.cz());
          break;
        case 66: 
          this.zzcq = paramzzarc.readString();
          break;
        case 72: 
          this.zzcr = Long.valueOf(paramzzarc.cz());
          break;
        case 82: 
          this.zzcs = paramzzarc.readString();
        }
      }
    }
    
    public void zza(zzard paramzzard)
      throws IOException
    {
      if (this.zzck != null) {
        paramzzard.zzr(1, this.zzck);
      }
      if (this.zzcl != null) {
        paramzzard.zzb(2, this.zzcl.longValue());
      }
      if (this.stackTrace != null) {
        paramzzard.zzr(3, this.stackTrace);
      }
      if (this.zzcm != null) {
        paramzzard.zzr(4, this.zzcm);
      }
      if (this.zzcn != null) {
        paramzzard.zzr(5, this.zzcn);
      }
      if (this.zzco != null) {
        paramzzard.zzb(6, this.zzco.longValue());
      }
      if (this.zzcp != null) {
        paramzzard.zzb(7, this.zzcp.longValue());
      }
      if (this.zzcq != null) {
        paramzzard.zzr(8, this.zzcq);
      }
      if (this.zzcr != null) {
        paramzzard.zzb(9, this.zzcr.longValue());
      }
      if (this.zzcs != null) {
        paramzzard.zzr(10, this.zzcs);
      }
      super.zza(paramzzard);
    }
    
    protected int zzx()
    {
      int j = super.zzx();
      int i = j;
      if (this.zzck != null) {
        i = j + zzard.zzs(1, this.zzck);
      }
      j = i;
      if (this.zzcl != null) {
        j = i + zzard.zzf(2, this.zzcl.longValue());
      }
      i = j;
      if (this.stackTrace != null) {
        i = j + zzard.zzs(3, this.stackTrace);
      }
      j = i;
      if (this.zzcm != null) {
        j = i + zzard.zzs(4, this.zzcm);
      }
      i = j;
      if (this.zzcn != null) {
        i = j + zzard.zzs(5, this.zzcn);
      }
      j = i;
      if (this.zzco != null) {
        j = i + zzard.zzf(6, this.zzco.longValue());
      }
      i = j;
      if (this.zzcp != null) {
        i = j + zzard.zzf(7, this.zzcp.longValue());
      }
      j = i;
      if (this.zzcq != null) {
        j = i + zzard.zzs(8, this.zzcq);
      }
      i = j;
      if (this.zzcr != null) {
        i = j + zzard.zzf(9, this.zzcr.longValue());
      }
      j = i;
      if (this.zzcs != null) {
        j = i + zzard.zzs(10, this.zzcs);
      }
      return j;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzad.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */